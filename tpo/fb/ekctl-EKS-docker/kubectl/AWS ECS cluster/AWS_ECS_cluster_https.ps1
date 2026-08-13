function createACMCertificate {
	$certARN = $(aws acm list-certificates --region us-east-1 --query "CertificateSummaryList[?DomainName=='$domainName'].CertificateArn" --output text)
if ([string]::IsNullOrEmpty($certARN)) {
	Write-Host "Certificate for $domainName is does not exist. Creating "  -ForegroundColor green
$hostedZoneId = aws route53 list-hosted-zones --query "HostedZones[?Name=='$domainName.'].Id" --output text

# SANs - do NOT include the base domain again
$sanList = @(
    "*.fresherbuddy.in",
    "www.fresherbuddy.in",
    "student.fresherbuddy.in",
    "exam.fresherbuddy.in",
    "eureka.fresherbuddy.in"
)

# Validation domains for each SAN (all validated through base domain)
$domainValidationOptions = @(
    "DomainName=$domainName,ValidationDomain=$domainName"
)

foreach ($san in $sanList) {
    $domainValidationOptions += "DomainName=$san,ValidationDomain=$domainName"
}

# IMPORTANT: expand SANs inline properly
$certArn = aws acm request-certificate `
  --domain-name $domainName `
  --validation-method DNS `
  --subject-alternative-names $sanList `
  --domain-validation-options $domainValidationOptions `
  --output text

Start-Sleep -Seconds 60

# Get validation options to create Route53 records
$validationOptions = aws acm describe-certificate --certificate-arn $certArn | ConvertFrom-Json

$records = $validationOptions.Certificate.DomainValidationOptions

$records

foreach ($record in $records) {
    $domain = $record.DomainName
    $r53Name = $record.ResourceRecord.Name
    $r53Type = $record.ResourceRecord.Type
    $r53Value = $record.ResourceRecord.Value

    Write-Host "🔧 Creating Route 53 validation record for: $domain"

    $changeBatch = @{
        Changes = @(
            @{
                Action = "UPSERT"
                ResourceRecordSet = @{
                    Name = $r53Name
                    Type = $r53Type
                    TTL = 300
                    ResourceRecords = @(
                        @{ Value = $r53Value }
                    )
                }
            }
        )
    } | ConvertTo-Json -Depth 10

    aws route53 change-resource-record-sets `
        --hosted-zone-id $hostedZoneId `
        --change-batch $changeBatch | Out-Null
}

Write-Host "`n✅ DNS validation records created. Certificate will be issued once DNS propagates (usually a few minutes)."


  Write-Host "Certificate for $domainName created . ARN : $certARN"  -ForegroundColor green
 } else{
	 
	  Write-Host "Certificate for $domainName is already exist. ARN : $certARN "  -ForegroundColor green
 }

Write-Host "Certificate ARN :$certARN" 

return $certARN
}

function createChoicePromptServiceOrTaskDefination {
	
	Add-Type -AssemblyName System.Windows.Forms

# Create a new form
$form = New-Object System.Windows.Forms.Form
$form.Text = "Confirmation"
$form.Size = New-Object System.Drawing.Size(400,150)
$form.StartPosition = "CenterScreen"

# Label
$label = New-Object System.Windows.Forms.Label
$label.Text = "Do you want to proceed with 'Service' or 'Task Def'?"
$label.AutoSize = $true
$label.Location = New-Object System.Drawing.Point(50,20)
$form.Controls.Add($label)

# Custom 'Service' button
$btnProceed = New-Object System.Windows.Forms.Button
$btnProceed.Text = "Service"
$btnProceed.Location = New-Object System.Drawing.Point(40,60)
$btnProceed.Add_Click({
    $form.Tag = "Service"
    $form.Close()
})

# Set background color (e.g., LightBlue)
$btnProceed.BackColor = [System.Drawing.Color]::Green

# Optional: Set text color
$btnProceed.ForeColor = [System.Drawing.Color]::White

$form.Controls.Add($btnProceed)

# Custom 'task' button
$btnAbort = New-Object System.Windows.Forms.Button
$btnAbort.Text = "Task Defination"
$btnAbort.Location = New-Object System.Drawing.Point(150,60)
$btnAbort.Add_Click({
    $form.Tag = "task"
    $form.Close()
})

# Set background color (e.g., LightBlue)
$btnAbort.BackColor = [System.Drawing.Color]::Red

# Optional: Set text color
$btnAbort.ForeColor = [System.Drawing.Color]::White

$form.Controls.Add($btnAbort)

# Show form and wait
$form.ShowDialog() | Out-Null

return $form.Tag
}

function createHostedZoneAndArecord {
	  Write-Host "Creating createHostedZoneAndArecord start.........."
   $albDns = aws elbv2 describe-load-balancers --query "LoadBalancers[?starts_with(LoadBalancerName, '$ApplicationLoadBalancerName')].DNSName" --output text --region $region
   $albDns
   $canonicalHostedZoneId = aws elbv2 describe-load-balancers --query "LoadBalancers[?starts_with(LoadBalancerName, '$ApplicationLoadBalancerName')].CanonicalHostedZoneId" --output text --region $region
   $canonicalHostedZoneId
		
$callerRef = [guid]::NewGuid().ToString()

# Ensure domain has trailing dot (as AWS returns it that way)
$searchName = "$domainName."
$hostedZones = aws route53 list-hosted-zones | ConvertFrom-Json
# Check if hosted zone exists
$existingZone = $hostedZones.HostedZones | Where-Object { $_.Name -eq $searchName }

$hostedZoneId = $existingZone.Id -replace '^/hostedzone/', ''

if (-not $existingZone) {
    Write-Host "Hosted zone does not exist. Creating...Make sure you update the DNS name servers with DNS provider"

    aws route53 create-hosted-zone `
        --name $domainName `
        --caller-reference $callerRef `
        --output json | ConvertFrom-Json | Format-List
		Start-Sleep -Seconds 60
} else {
    Write-Host "Hosted zone with domain name $domainName already exists: $hostedZoneId"
}

$recordName = "$aRecordName."       # Must end with a dot
$record = aws route53 list-resource-record-sets `
    --hosted-zone-id $hostedZoneId `
    --query "ResourceRecordSets[?Name == '$recordName' && Type == 'A']" `
    --output json | ConvertFrom-Json
if ($record.Count -gt 0) {
    Write-Output "A record found for $recordName"
} else {
    Write-Output "No A record found for $recordName"
}

$aRecord = @"
{
  "Comment": "Create A record alias for ALB",
  "Changes": [
    {
      "Action": "UPSERT",
      "ResourceRecordSet": {
        "Name": "$aRecordName",
        "Type": "A",
        "AliasTarget": {
          "HostedZoneId": "$canonicalHostedZoneId",
          "DNSName": "$albDns",
          "EvaluateTargetHealth": true
        }
      }
    }
  ]
}
"@
        $trustFile = "ecs-trust-policy.json"
        $aRecord | Out-File -Encoding ASCII -FilePath $trustFile
		$aRecord
aws route53 change-resource-record-sets --hosted-zone-id $hostedZoneId --change-batch file://$trustFile
Write-Host "Wait for 2 minutes" -ForegroundColor Green
Start-Sleep -Seconds 120
 Write-Host "Your application is accessible at: https://$aRecordName" -ForegroundColor Yellow
    
}

function createRule {
	 param (
        [string]$listenerArn
	)
	
$path = "/*"

if($alias -eq 'fb'){
	# Build conditions for FB
$conditions = @{ 
        Field = "host-header"; 
        HostHeaderConfig = @{ Values = @("$domainName", "$aRecordName") } 
    },
    @{ 
        Field = "path-pattern"; 
        PathPatternConfig = @{ Values = @($path) } 
    }
} else {
# Build conditions for Exam,Student,Eureca
$conditions = @(
    @{ Field = "host-header"; Values = @($aRecordName) },
    @{ Field = "path-pattern"; Values = @($path) }
)
}	

$actions = @(
    @{ Type = "forward"; TargetGroupArn = $targetGroupArn }
)

$conditions
$actions

# Convert to JSON
$conditionsJson = $conditions | ConvertTo-Json -Depth 4 -Compress
$actionsJson = $actions | ConvertTo-Json -Depth 3 -Compress

$conditionsJson
$actionsJson

# Try to find existing rule
$rules = aws elbv2 describe-rules --listener-arn $listenerArn | ConvertFrom-Json

foreach ($rule in $rules.Rules) {
    $matchHost = $false
  
    foreach ($cond in $rule.Conditions) {
        if ($cond.Field -eq "host-header" -and $cond.Values -contains $aRecordName) {
            $matchHost = $true
        }
    }

    if ($matchHost) {
        Write-Host "🗑 Deleting rule with Host=$aRecordName"
        aws elbv2 delete-rule --rule-arn $rule.RuleArn
    }
}
  $response = aws elbv2 create-rule `
        --listener-arn $listenerArn `
        --priority $priority `
        --conditions "$conditionsJson" `
        --actions "$actionsJson" | ConvertFrom-Json
		
	$ruleArn = $response.Rules[0].RuleArn
Write-Host "✅ Created rule: $ruleArn"

# Step 2: Add a Name tag
aws elbv2 add-tags `
    --resource-arns $ruleArn `
    --tags "Key=Name,Value=$alias"

Write-Host "🏷️ Tagged rule with Name=$alias"	

}

function deleteECSCluster {
	 param ([string]$listenerArn)
	
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

    # Optional: Delete Log Group
    $existingLog = aws logs describe-log-groups --log-group-name-prefix $logGroupName --query 'logGroups[0].logGroupName' --output text
    if ($existingLog -ne "None" -and -not [string]::IsNullOrWhiteSpace($existingLog)) {
        aws logs delete-log-group --log-group-name $logGroupName
        Write-Host "Deleted existing log group: $logGroupName"
    }

    # Clean Up: ECS Cluster (delete last)
    Write-Host "Deleting ECS Cluster: $clusterName"
    aws ecs delete-cluster --cluster $clusterName
	
	if ([string]::IsNullOrEmpty($listenerArn)) {
	 $lbArn = aws elbv2 describe-load-balancers --names $ApplicationLoadBalancerName --region $region --query "LoadBalancers[0].LoadBalancerArn" --output text
    if (-not [string]::IsNullOrWhiteSpace($lbArn)) {
        Write-Host "Load Balancer exist : $ApplicationLoadBalancerName .Deleting.."
       aws elbv2 delete-load-balancer --load-balancer-arn $lbArn
	}
	 Start-Sleep -Seconds 15
	 $tgArn = aws elbv2 describe-target-groups --names $targetGroupName --region $region --query "TargetGroups[0].TargetGroupArn" --output text
    if (-not [string]::IsNullOrWhiteSpace($tgArn)) {
        Write-Host "Target Group Exist : $targetGroupName.Deleting..."
        aws elbv2 delete-target-group --target-group-arn $tgArn
	}
	}
}

function setupgrafana {
	Add-Type -AssemblyName System.Windows.Forms

Write-Host "🚀 Grafana ECS Setup Starting..." -ForegroundColor Green

# =========================
# STEP 0: CHECK ECS CLUSTER
# =========================
Write-Host "🔍 Checking ECS cluster '$clusterName'..."

$clusterStatus = aws ecs describe-clusters `
    --clusters $clusterName `
    --region $region `
    --query "clusters[0].status" `
    --output text 2>$null

if ($clusterStatus -ne "ACTIVE") {
    [System.Windows.Forms.MessageBox]::Show(
        "ECS Cluster '$clusterName' does not exist or is not ACTIVE.`nPlease create it first.",
        "ECS Cluster Missing",
        [System.Windows.Forms.MessageBoxButtons]::OK,
        [System.Windows.Forms.MessageBoxIcon]::Error
    )
    Write-Host "❌ ECS cluster not found. Exiting." -ForegroundColor Red
    exit 1
}

Write-Host "✅ ECS cluster found"

# =========================
# STEP 1: UI PROMPT
# =========================
$result = [System.Windows.Forms.MessageBox]::Show(
    "Do you want to install Grafana on ECS cluster '$clusterName'?",
    "Grafana Installation",
    [System.Windows.Forms.MessageBoxButtons]::YesNo,
    [System.Windows.Forms.MessageBoxIcon]::Question
)

if ($result -ne [System.Windows.Forms.DialogResult]::Yes) {
    Write-Host "⏭️ User skipped Grafana installation." -ForegroundColor Yellow
    exit 0
}

Write-Host "✅ User confirmed Grafana installation"

# =========================
# STEP 2: ENABLE CONTAINER INSIGHTS
# =========================
aws ecs put-account-setting `
    --name containerInsights `
    --value enabled `
    --region $region | Out-Null

Write-Host "✅ Container Insights enabled"

# =========================
# STEP 3: IAM ROLE (IDEMPOTENT)
# =========================
$roleExists = aws iam get-role --role-name $roleName 2>$null

if (-not $roleExists) {

    $assumeRolePolicy = @"
{
  "Version": "2012-10-17",
  "Statement": [{
    "Effect": "Allow",
    "Principal": { "Service": "ecs-tasks.amazonaws.com" },
    "Action": "sts:AssumeRole"
  }]
}
"@

    aws iam create-role `
        --role-name $roleName `
        --assume-role-policy-document $assumeRolePolicy `
        --region $region | Out-Null

    $policyDoc = @"
{
  "Version": "2012-10-17",
  "Statement": [{
    "Effect": "Allow",
    "Action": [
      "cloudwatch:*",
      "logs:*",
      "ecs:Describe*",
      "ec2:Describe*"
    ],
    "Resource": "*"
  }]
}
"@

    aws iam put-role-policy `
        --role-name $roleName `
        --policy-name GrafanaCloudWatchPolicy `
        --policy-document $policyDoc

    Write-Host "✅ IAM role created"
}
else {
    Write-Host "ℹ️ IAM role already exists"
}

# =========================
# STEP 4: TASK DEFINITION
# =========================
$accountId = aws sts get-caller-identity --query Account --output text

$taskDef = @"
{
  "family": "$taskFamily",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["$launchType"],
  "cpu": "512",
  "memory": "1024",
  "executionRoleArn": "arn:aws:iam::$accountId:role/ecsTaskExecutionRole",
  "taskRoleArn": "arn:aws:iam::$accountId:role/$roleName",
  "containerDefinitions": [{
    "name": "grafana",
    "image": "grafana/grafana:latest",
    "essential": true,
    "portMappings": [{
      "containerPort": 3000,
      "protocol": "tcp"
    }],
    "environment": [
      { "name": "GF_SECURITY_ADMIN_USER", "value": "$adminUser" },
      { "name": "GF_SECURITY_ADMIN_PASSWORD", "value": "$adminPassword" }
    ]
  }]
}
"@

$taskDef | Out-File grafana-task.json -Encoding utf8

aws ecs register-task-definition `
    --cli-input-json file://grafana-task.json `
    --region $region | Out-Null

Write-Host "✅ Task definition registered"

# =========================
# STEP 5: NETWORK DISCOVERY
# =========================
$vpcId = aws ec2 describe-vpcs `
    --filters "Name=isDefault,Values=true" `
    --query "Vpcs[0].VpcId" `
    --output text

$sg = aws ec2 describe-security-groups `
    --filters "Name=group-name,Values=default" "Name=vpc-id,Values=$vpcId" `
    --query "SecurityGroups[0].GroupId" `
    --output text

$subnets = aws ec2 describe-subnets `
    --filters "Name=default-for-az,Values=true" "Name=vpc-id,Values=$vpcId" `
    --query "Subnets[*].SubnetId" `
    --output text

$subnetArray = $subnets -split "\s+"

if ($subnetArray.Count -lt 2) {
    Write-Host "❌ Not enough subnets found" -ForegroundColor Red
    exit 1
}

# =========================
# STEP 6: CREATE ECS SERVICE
# =========================
aws ecs create-service `
    --cluster $clusterName `
    --service-name $serviceNamegrafana `
    --task-definition $taskFamily `
    --desired-count 1 `
    --launch-type $launchType `
    --network-configuration "awsvpcConfiguration={subnets=[$($subnetArray[0]),$($subnetArray[1])],securityGroups=[$sg],assignPublicIp=ENABLED}" `
    --region $region 2>$null

Write-Host "🎉 Grafana ECS Service created successfully!" -ForegroundColor Green
Write-Host "👉 Access Grafana via ECS public IP on port 3000"
Write-Host "👉 Add CloudWatch datasource and import ECS dashboards"
}


function createECSCluster {
	 param (
        [string]$listenerArn
	)
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
	$vpcId = aws ec2 describe-vpcs --filters "Name=isDefault,Values=true" --query "Vpcs[0].VpcId" --output text
	$sg = (aws ec2 describe-security-groups `
    --filters "Name=group-name,Values=default" "Name=vpc-id,Values=$vpcId" `
    --query "SecurityGroups[0].GroupId" --output text).Trim()
	 $subnetOutput = (aws ec2 describe-subnets `
    --filters "Name=default-for-az,Values=true" "Name=vpc-id,Values=$vpcId" `
    --query "Subnets[*].SubnetId" --output text).Trim()

# Always convert to array (even if only one item)
if ($subnetOutput -ne "") {
    $subnets = $subnetOutput -split "\s+"
} else {
    $subnets = @()
}
$subnet1 = $subnets[0]
$subnet2 = $subnets[1] 
Write-Host "VPC ID : $vpcId"
Write-Host "Security Group : $sg associated with VPC ID : $vpcId"
Write-Host "Subnets : $subnet1 , $subnet2 under with VPC ID : $vpcId , Total subnets under same Vpc $subnets"

    
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
        --cpu $cpu `
        --memory $memory `
        --execution-role-arn $ecsTaskExecutionRoleArn `
        --container-definitions $containerDef `
        --query 'taskDefinition.taskDefinitionArn' --output text
    Write-Host "Task definition registered: $taskDefArn"

 Add-Type -AssemblyName Microsoft.VisualBasic
$result = createChoicePromptServiceOrTaskDefination

if ($result -eq "task") {
        # Run standalone task
        $taskRun = aws ecs run-task --cluster $clusterName --task-definition $taskName --launch-type FARGATE `
            --network-configuration "awsvpcConfiguration={subnets=[$subnet1,$subnet2],securityGroups=[$sg],assignPublicIp='ENABLED'}" `
            --query 'tasks[0].taskArn' --output text
        Write-Host "Task started: $taskRun"
    } else {
	 
	 if ([string]::IsNullOrEmpty($listenerArn)) {
	# Clean Up: Load Balancer
    $lbArn = aws elbv2 describe-load-balancers --names $ApplicationLoadBalancerName --region $region --query "LoadBalancers[0].LoadBalancerArn" --output text
    if (-not [string]::IsNullOrWhiteSpace($lbArn)) {
        Write-Host "Load Balancer exist : $ApplicationLoadBalancerName .Deleting and Creating New"
       aws elbv2 delete-load-balancer --load-balancer-arn $lbArn
	   # Create ALB and Target Group
       $albArn = aws elbv2 create-load-balancer --name $ApplicationLoadBalancerName --subnets $subnet1 $subnet2 `
            --security-groups $sg --scheme internet-facing --type application --ip-address-type ipv4 `
            --query 'LoadBalancers[0].LoadBalancerArn' --output text
			Write-Host "ALB ARN : $albArn"
	} else {
        Write-Host "Load Balancer $ApplicationLoadBalancerName does not exist.Creating New"
		# Create ALB and Target Group
        $albArn = aws elbv2 create-load-balancer --name $ApplicationLoadBalancerName --subnets $subnet1 $subnet2 `
            --security-groups $sg --scheme internet-facing --type application --ip-address-type ipv4 `
            --query 'LoadBalancers[0].LoadBalancerArn' --output text
			Write-Host "ALB ARN : $albArn"
    }
	# Clean Up: Target Group
    $tgArn = aws elbv2 describe-target-groups --names $targetGroupName --region $region --query "TargetGroups[0].TargetGroupArn" --output text
    if (-not [string]::IsNullOrWhiteSpace($tgArn)) {
        Write-Host "Target Group Exist : $targetGroupName.Deleting and Creating New"
        aws elbv2 delete-target-group --target-group-arn $tgArn
		$targetGroupArn = aws elbv2 create-target-group `
            --name $targetGroupName --protocol HTTP --port 80 `
            --vpc-id $vpcId --target-type ip `
			--health-check-path $healthCheckPath `
            --query "TargetGroups[0].TargetGroupArn" --output text
		Write-Host "Target Group ARN : $targetGroupArn"
		 } else {
        Write-Host "Target Group $targetGroupName does not exist.Creating New"
		$targetGroupArn = aws elbv2 create-target-group `
            --name $targetGroupName --protocol HTTP --port 80 `
            --vpc-id $vpcId --target-type ip `
			--health-check-path $healthCheckPath `
            --query "TargetGroups[0].TargetGroupArn" --output text
			Write-Host "Target Group ARN : $targetGroupArn"
    }
	} else {
		$lbArn = aws elbv2 describe-load-balancers --names $ApplicationLoadBalancerName --region $region --query "LoadBalancers[0].LoadBalancerArn" --output text
    if (-not [string]::IsNullOrWhiteSpace($lbArn)) {
       # Create ALB and Target Group
       $albArn = aws elbv2 create-load-balancer --name $ApplicationLoadBalancerName --subnets $subnet1 $subnet2 `
            --security-groups $sg --scheme internet-facing --type application --ip-address-type ipv4 `
            --query 'LoadBalancers[0].LoadBalancerArn' --output text
			Write-Host "ALB ARN : $albArn"
	}else{
		$albArn =$lbArn
		Write-Host "ALB ARN : $albArn"
	}
	$tgArn = aws elbv2 describe-target-groups --names $targetGroupName --region $region --query "TargetGroups[0].TargetGroupArn" --output text
    if (-not [string]::IsNullOrWhiteSpace($tgArn)) {
        Write-Host "Target Group Exist : $targetGroupName.Deleting and Creating New"
      		$targetGroupArn = $tgArn
	}else {
		 	$targetGroupArn = aws elbv2 create-target-group `
            --name $targetGroupName --protocol HTTP --port 80 `
            --vpc-id $vpcId --target-type ip `
			--health-check-path $healthCheckPath `
            --query "TargetGroups[0].TargetGroupArn" --output text
	}
		Write-Host "Target Group ARN : $targetGroupArn"
	}
	
	if ([string]::IsNullOrEmpty($listenerArn)) {
		 $certificateARN = createACMCertificate
	   $response = aws elbv2 create-listener --load-balancer-arn $albArn --protocol HTTPS --port 443 --certificates CertificateArn=$certificateARN `
            --default-actions "Type=forward,TargetGroupArn=$targetGroupArn" | ConvertFrom-Json
	    $listenerArn = $response.Listeners[0].ListenerArn
		createRule -listenerArn $listenerArn
	} else {
$newWeight = 1
$newTG = @{ TargetGroupArn = $targetGroupArn; Weight = $newWeight }

$listeners = aws elbv2 describe-listeners --load-balancer-arn $albArn | ConvertFrom-Json

$allTargetGroups = @()

foreach ($listener in $listeners.Listeners) {
    $listenerArn = $listener.ListenerArn
    Write-Host "`n🔍 Checking listener: $listenerArn"

    $rules = aws elbv2 describe-rules --listener-arn $listenerArn | ConvertFrom-Json

    foreach ($rule in $rules.Rules) {
        foreach ($action in $rule.Actions) {
            if ($action.Type -eq "forward" -and $action.ForwardConfig -and $action.ForwardConfig.TargetGroups) {
                foreach ($tg in $action.ForwardConfig.TargetGroups) {
                    $allTargetGroups += $tg
                }
            }
            elseif ($action.Type -eq "forward" -and $action.TargetGroupArn) {
                # Fallback if no ForwardConfig exists (older or simple rules)
                $allTargetGroups += @{ TargetGroupArn = $action.TargetGroupArn; Weight = 1 }
            }
        }
    }
}

# Remove duplicates (by ARN)
$uniqueTargetGroups = $allTargetGroups | Sort-Object TargetGroupArn -Unique

Write-Host "`n✅ Total unique target groups associated with ALB: $($uniqueTargetGroups.Count)"
$uniqueTargetGroups | ForEach-Object {
    Write-Host "• $($_.TargetGroupArn) (Weight=$($_.Weight))"
}

# Safely cast to array
$uniqueTargetGroups = @($uniqueTargetGroups)
# Append new TG safely
$allTGs = $uniqueTargetGroups  + $newTG

 # Use hashtable to avoid duplicate ARNs
$targetGroupMap = @{}
foreach ($tg in $allTGs) {
    $arn = $tg.TargetGroupArn
    $weight = if ($tg.Weight) { $tg.Weight } else { 1 }

    # Last one wins if duplicate ARN
    $targetGroupMap[$arn] = @{ TargetGroupArn = $arn; Weight = $weight }
}

# Convert final hashtable values to array
$targetGroups = @()
foreach ($tgEntry in $targetGroupMap.Values) {
    $targetGroups += $tgEntry
}

# Build the full actions object
$forwardConfig = @{
    TargetGroups = $targetGroups
}

$defaultAction = @{
    Type = "forward"
    ForwardConfig = $forwardConfig
}

# Convert to JSON properly (compressed, with full depth)
$defaultActionsJson = @($defaultAction) | ConvertTo-Json -Depth 5 -Compress


    # Apply changes to the listener
    aws elbv2 modify-listener `
        --listener-arn $listenerArn `
        --default-actions $defaultActionsJson

    Write-Host "✅ Updated listener $listenerArn"
}
createRule -listenerArn $listenerArn

}

       # Run as ECS service
       Write-Host "Creating Service $serviceName under cluster $clusterName with task $taskName" 
	 aws ecs create-service `
    --cluster $clusterName `
    --service-name $serviceName `
    --task-definition $taskName `
    --desired-count $desiredTgCount `
    --launch-type FARGATE `
    --network-configuration "awsvpcConfiguration={subnets=[$subnet1,$subnet2],securityGroups=[$sg],assignPublicIp=ENABLED}" `
    --load-balancers "[{""targetGroupArn"":""$targetGroupArn"",""containerName"":""$containerName"",""containerPort"":$port}]" `
    --deployment-configuration "maximumPercent=200,minimumHealthyPercent=100"

     	createHostedZoneAndArecord
		setupgrafana
		
    }

