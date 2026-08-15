param(
    [switch]$Force,
    [switch]$CleanupOnly,
    [switch]$DeployOnly
)

# Configuration Variables
$clusterName = "EUREKA"
$taskName = "EUREKA_TASK"
$serviceName = "EUREKA_SERVICE"
$region = "us-east-1"
$awsAccountId = "356723301672"
$imageName = "$awsAccountId.dkr.ecr.$region.amazonaws.com/eureka"
$port = 8761
$ecsTaskExecutionRoleName = "ecsTaskExecutionRole"
$ApplicationLoadBalancerName = "EUREKA-LB"
$targetGroupName = "EUREKA-Target-Group"
$containerName = "EUREKA-Container"
$logGroupName = "/ecs/EUREKA-logGroup"

# Run mode: true = run-task, false = run service with ALB
$flag = $true



if (-not $Force) {
    $confirmation = Read-Host "This will delete and recreate resources. Type 'yes' to continue"
    if ($confirmation -ne 'yes') {
        Write-Host "Cancelled."
        exit
    }
}

# Helper: Safe execution wrapper
function Try-AwsCommand {
    param (
        [scriptblock]$Command,
        [string]$ErrorMessage
    )
    try {
        & $Command
    } catch {
        Write-Host "${ErrorMessage}: $_"
    }
}

if (-not $DeployOnly) {
    # Clean Up: ECS Services
    $serviceArns = aws ecs list-services --cluster $clusterName --query "serviceArns[]" --output text
    if (-not [string]::IsNullOrWhiteSpace($serviceArns)) {
        $services = $serviceArns -split '\s+'
        foreach ($service in $services) {
            Write-Host "Scaling down service: $service"
            aws ecs update-service --cluster $clusterName --service $service --desired-count 0
            Write-Host "Deleting service: $service"
            aws ecs delete-service --cluster $clusterName --service $service --force
        }
    } else {
        Write-Host "No services found in cluster '$clusterName'."
    }

    # Clean Up: ECS Tasks
    $taskArns = aws ecs list-tasks --cluster $clusterName --query "taskArns[]" --output text
    if (-not [string]::IsNullOrWhiteSpace($taskArns)) {
        $taskList = $taskArns -split '\s+'
        foreach ($task in $taskList) {
            Write-Host "Stopping task: $task"
            aws ecs stop-task --cluster $clusterName --task $task
        }
    } else {
        Write-Host "No running tasks found in cluster '$clusterName'."
    }

    # Clean Up: Target Group
    $tgArn = aws elbv2 describe-target-groups --names $targetGroupName --query "TargetGroups[0].TargetGroupArn" --output text
    if (-not [string]::IsNullOrWhiteSpace($tgArn)) {
        Write-Host "Deleting Target Group: $targetGroupName"
        aws elbv2 delete-target-group --target-group-arn $tgArn
    } else {
        Write-Host "Target Group $targetGroupName does not exist."
    }

    # Clean Up: Load Balancer
    $lbArn = aws elbv2 describe-load-balancers --names $ApplicationLoadBalancerName --query "LoadBalancers[0].LoadBalancerArn" --output text
    if (-not [string]::IsNullOrWhiteSpace($lbArn)) {
        Write-Host "Deleting Load Balancer: $ApplicationLoadBalancerName"
        aws elbv2 delete-load-balancer --load-balancer-arn $lbArn
    } else {
        Write-Host "Load Balancer $ApplicationLoadBalancerName does not exist."
    }

    # Optional: Delete Log Group
    $existingLog = aws logs describe-log-groups --log-group-name-prefix $logGroupName --query 'logGroups[0].logGroupName' --output text
    if ($existingLog -ne "None" -and -not [string]::IsNullOrWhiteSpace($existingLog)) {
        aws logs delete-log-group --log-group-name $logGroupName
        Write-Host "Deleted existing log group: $logGroupName"
    }

    # Clean Up: ECS Cluster (delete last)
    Write-Host "Deleting ECS Cluster: $clusterName"
    aws ecs delete-cluster --cluster $clusterName
}

if (-not $CleanupOnly) {
    # Ensure ECS Role
    $roleArn = aws iam get-role --role-name $ecsTaskExecutionRoleName --query "Role.Arn" --output text
    if (-not [string]::IsNullOrWhiteSpace($roleArn)) {
        Write-Host "Role $ecsTaskExecutionRoleName exists."
    } else {
        Write-Host "Creating ECS Task Execution Role..."
        $ecsTrustPolicy = @"
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": { "Service": "ecs-tasks.amazonaws.com" },
      "Action": "sts:AssumeRole"
    }
  ]
}
"@
        $trustFile = "$env:TEMP\ecs-trust-policy.json"
        $ecsTrustPolicy | Out-File -Encoding ASCII -FilePath $trustFile
        aws iam create-role --role-name $ecsTaskExecutionRoleName --assume-role-policy-document file://$trustFile
        aws iam attach-role-policy --role-name $ecsTaskExecutionRoleName --policy-arn arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy

        $ecsLogPolicy = @"
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [ "logs:CreateLogStream", "logs:PutLogEvents" ],
      "Resource": "*"
    }
  ]
}
"@
        $logPolicyFile = "$env:TEMP\ecs-log-policy.json"
        $ecsLogPolicy | Out-File -Encoding ASCII -FilePath $logPolicyFile
        aws iam put-role-policy --role-name $ecsTaskExecutionRoleName --policy-name MyEcsTaskLoggingPolicy --policy-document file://$logPolicyFile
        Write-Host "Role $ecsTaskExecutionRoleName created and configured."
    }

    # Recreate ECS Cluster and Log Group
    aws ecs create-cluster --cluster-name $clusterName
    aws logs create-log-group --log-group-name $logGroupName
    Write-Host "Log Group created: $logGroupName"

    # Prepare Task Definition
    $containerDef = @"
[
  {
    "name": "$containerName",
    "image": "$imageName",
    "essential": true,
    "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "$logGroupName",
          "awslogs-region": "$region",
          "awslogs-stream-prefix": "ecs"
        }
    },
    "portMappings": [
      {
        "containerPort": $port,
        "protocol": "tcp"
      }
    ]
  }
]
"@

    # Network Configuration
    $subnet1 = aws ec2 describe-subnets --filters "Name=default-for-az,Values=true" --query "Subnets[0].SubnetId" --output text
    $subnet2 = aws ec2 describe-subnets --filters "Name=default-for-az,Values=true" --query "Subnets[1].SubnetId" --output text
    $sg = aws ec2 describe-security-groups --filters "Name=group-name,Values=default" --query "SecurityGroups[0].GroupId" --output text

    if (-not $subnet1 -or -not $subnet2 -or -not $sg) {
        Write-Host "Missing required network resources (subnets or security group)."
        exit 1
    }

    # Register ECS Task Definition
    $ecsTaskExecutionRoleArn = aws iam get-role --role-name $ecsTaskExecutionRoleName --query 'Role.Arn' --output text
    $taskDefArn = aws ecs register-task-definition `
        --family $taskName `
        --requires-compatibilities FARGATE `
        --network-mode awsvpc `
        --cpu "256" `
        --memory "512" `
        --execution-role-arn $ecsTaskExecutionRoleArn `
        --container-definitions $containerDef `
        --query 'taskDefinition.taskDefinitionArn' --output text
    Write-Host "Task definition registered: $taskDefArn"

    if ($flag) {
        # Run standalone task
$taskArn = aws ecs run-task `
    --cluster $clusterName `
    --task-definition $taskName `
    --launch-type FARGATE `
    --network-configuration "awsvpcConfiguration={subnets=[$subnet1,$subnet2],securityGroups=[$sg],assignPublicIp='ENABLED'}" `
    --query 'tasks[0].taskArn' `
    --output text

if (-not [string]::IsNullOrWhiteSpace($taskArn) -and $taskArn -ne "None") {
    Write-Host "Task started successfully: $taskArn"
    Write-Host "Waiting 30 seconds for task networking to initialize..."
    Start-Sleep -Seconds 30

    # Get ENI ID attached to the task
    $eniId = aws ecs describe-tasks `
        --cluster $clusterName `
        --tasks $taskArn `
        --query "tasks[0].attachments[0].details[?name=='networkInterfaceId'].value" `
        --output text

    if (-not [string]::IsNullOrWhiteSpace($eniId) -and $eniId -ne "None") {
        # Get Public IP from ENI
        $publicIp = aws ec2 describe-network-interfaces `
            --network-interface-ids $eniId `
            --query "NetworkInterfaces[0].Association.PublicIp" `
            --output text

        if (-not [string]::IsNullOrWhiteSpace($publicIp) -and $publicIp -ne "None") {
            
			# Make sure Windows Forms assembly is loaded
Add-Type -AssemblyName System.Windows.Forms


# Show message as a popup
[System.Windows.Forms.MessageBox]::Show(
    "🚀 Task Public IP: $publicIp`nPlease update IP in all the app application.yaml files.it will be updated automatically.please check",
    "Task IP Info",
    [System.Windows.Forms.MessageBoxButtons]::OK,
    [System.Windows.Forms.MessageBoxIcon]::Information
)


           Write-Host "Access your Eureca app at: http://${publicIp}:${port}"
		   
		   & .\Update-EurekaDefaultZone.ps1 -RootFolder "D:\latestworks\tpo\fb" -NewEurekaHost "$publicIp"
		   & .\Update-EurekaDefaultZone.ps1 -RootFolder "D:\latestworks\tpo\exam" -NewEurekaHost "$publicIp"
		   & .\Update-EurekaDefaultZone.ps1 -RootFolder "D:\latestworks\tpo\student" -NewEurekaHost "$publicIp"

        } else {
            Write-Host "⚠️ Public IP not assigned yet."
        }
    } else {
        Write-Host "❌ Failed to retrieve ENI ID for the task."
    }
} else {
    Write-Host "❌ Task failed to start."
}

    } else {
        # Create ALB and Target Group
        $albArn = aws elbv2 create-load-balancer --name $ApplicationLoadBalancerName --subnets $subnet1 $subnet2 `
            --security-groups $sg --scheme internet-facing --type application --ip-address-type ipv4 `
            --query 'LoadBalancers[0].LoadBalancerArn' --output text

        $vpcId = aws ec2 describe-vpcs --filters "Name=isDefault,Values=true" --query "Vpcs[0].VpcId" --output text

        $targetGroupArn = aws elbv2 create-target-group `
            --name $targetGroupName --protocol HTTP --port 80 `
            --vpc-id $vpcId --target-type ip `
            --query "TargetGroups[0].TargetGroupArn" --output text

        aws elbv2 create-listener --load-balancer-arn $albArn --protocol HTTP --port 80 `
            --default-actions "Type=forward,TargetGroupArn=$targetGroupArn"

        # Run as ECS service
        aws ecs create-service --cluster $clusterName --service-name $serviceName --task-definition $taskName `
            --desired-count 1 --launch-type FARGATE `
            --network-configuration "awsvpcConfiguration={subnets=[$subnet1,$subnet2],securityGroups=[$sg],assignPublicIp='ENABLED'}" `
            --load-balancers "targetGroupArn=$targetGroupArn,containerName=$containerName,containerPort=$port"

        Start-Sleep -Seconds 180
        $albDns = aws elbv2 describe-load-balancers --names $ApplicationLoadBalancerName --query "LoadBalancers[0].DNSName" --output text
        Write-Host "Your application is accessible at: http://$albDns"
    }
}

