# =========================
# VARIABLES
# =========================
$awsAcId = 542115677157
$alias               = "fb"
$clusterName         = "$alias-ecs"
$serviceNamegrafana  = "$alias-service-grafana"
$taskFamily          = "grafana"
$adminUser           = "indrajeet"
$adminPassword       = "JeeT@1984"
$launchType          = "FARGATE"
$region              = "us-east-1"
$roleName            = "GrafanaEcsTaskRole"
$ecsTaskExecutionRoleName = "ecsTaskExecutionRole"



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
  --assume-role-policy-document $assumeRolePolicy 2>$null
  
  # IMPORTANT: IAM propagation delay
Write-Host "⏳ Waiting for IAM role propagation..."
Start-Sleep -Seconds 10

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
}
else {
    Write-Host "ℹ️ IAM role already exists"
}

if (-not (aws iam get-role --role-name $ecsTaskExecutionRoleName 2>$null)) {

    aws iam create-role `
        --role-name $ecsTaskExecutionRoleName `
        --assume-role-policy-document $trustPolicy | Out-Null

    

    Write-Host "✅ Created ECS execution role"
}
aws iam attach-role-policy `
        --role-name $ecsTaskExecutionRoleName `
        --policy-arn arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy

# =========================
# STEP 4: TASK DEFINITION
# =========================

#$accountId = aws sts get-caller-identity --query Account --output text

if (-not $awsAcId) {
    Write-Host "❌ Failed to get AWS Account ID" -ForegroundColor Red
    exit 1
}

Write-Host "✅ AWS Account ID: $accountId"

$taskDef = @"
{
  "family": "grafana",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "512",
  "memory": "1024",
  "executionRoleArn": "arn:aws:iam::${awsAcId}:role/ecsTaskExecutionRole",
  "taskRoleArn": "arn:aws:iam::${awsAcId}:role/GrafanaEcsTaskRole",
  "containerDefinitions": [
    {
      "name": "grafana",
      "image": "grafana/grafana:latest",
      "essential": true,
      "portMappings": [
        {
          "containerPort": 3000,
          "protocol": "tcp"
        }
      ],
      "environment": [
        { "name": "GF_SECURITY_ADMIN_USER", "value": "${adminUser}" },
        { "name": "GF_SECURITY_ADMIN_PASSWORD", "value": "${adminPassword}" }
      ]
    }
  ]
}
"@


$taskDef | Out-File grafana-task.json -Encoding utf8

$taskResult = aws ecs register-task-definition `
    --cli-input-json file://grafana-task.json `
    --region $region 2>&1

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Task definition registration failed" -ForegroundColor Red
    Write-Host $taskResult
    exit 1
}

Write-Host "✅ Task definition registered successfully"

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
Write-Host "👉 Add CloudWatch datasource and import ECS dashboards"
Write-Host "⏳ Waiting for Grafana task to start..."
Start-Sleep -Seconds 30

# Get running task ARN
$taskArn = aws ecs list-tasks `
    --cluster $clusterName `
    --service-name $serviceNamegrafana `
    --desired-status RUNNING `
    --region $region `
    --query "taskArns[0]" `
    --output text

if (-not $taskArn -or $taskArn -eq "None") {
    Write-Host "❌ No running Grafana task found" -ForegroundColor Red
    exit 1
}

Write-Host "✅ Grafana Task found"

# Get ENI ID from task
$eniId = aws ecs describe-tasks `
    --cluster $clusterName `
    --tasks $taskArn `
    --region $region `
    --query "tasks[0].attachments[0].details[?name=='networkInterfaceId'].value" `
    --output text

if (-not $eniId) {
    Write-Host "❌ Failed to get ENI ID" -ForegroundColor Red
    exit 1
}

Write-Host "✅ ENI ID: $eniId"

# Get Public IP from ENI
$publicIp = aws ec2 describe-network-interfaces `
    --network-interface-ids $eniId `
    --region $region `
    --query "NetworkInterfaces[0].Association.PublicIp" `
    --output text

if (-not $publicIp -or $publicIp -eq "None") {
    Write-Host "❌ Public IP not assigned yet" -ForegroundColor Red
    exit 1
}

# Compose Grafana URL
$grafanaUrl = "http://" + $publicIp +":3000"

Write-Host ""
Write-Host "🎉 Grafana is LIVE!" -ForegroundColor Green
Write-Host "👤 Username: $adminUser"
Write-Host "🔑 Password: $adminPassword"

Write-Host "🌐 Grafana URL: $grafanaUrl" -ForegroundColor Cyan

Start-Sleep 20

# =========================
# STEP 8: GRAFANA API AUTH
# =========================
$basicAuth = [Convert]::ToBase64String(
  [Text.Encoding]::ASCII.GetBytes("${adminUser}:${adminPassword}")
)

$headers = @{
  Authorization = "Basic $basicAuth"
  "Content-Type" = "application/json"
}

# =========================
# STEP 9: DATASOURCE
# =========================
Invoke-RestMethod `
  -Method POST `
  -Uri "$grafanaUrl/api/datasources" `
  -Headers $headers `
  -Body '{
    "name":"CloudWatch",
    "type":"cloudwatch",
    "access":"proxy",
    "jsonData":{"defaultRegion":"us-east-1"}
  }' `
  -ErrorAction SilentlyContinue

Write-Host "✅ CloudWatch datasource ready"

# =========================
# STEP 10: DASHBOARDS
# =========================
$importApi = "$grafanaUrl/api/dashboards/import"

$dashboards = @(13946, 13015)

foreach ($id in $dashboards) {
    $body = @"
{
  "grafanaComDashboardId": $id,
  "overwrite": true,
  "inputs": [{
    "name":"DS_CLOUDWATCH",
    "type":"datasource",
    "pluginId":"cloudwatch",
    "value":"CloudWatch"
  }]
}
"@

    Invoke-RestMethod `
      -Method POST `
      -Uri $importApi `
      -Headers $headers `
      -Body $body `
      -ErrorAction Stop

    Write-Host "📊 Dashboard $id imported"
}

Write-Host "🎉 Grafana ECS setup COMPLETE!" -ForegroundColor Green
Write-Host "👉 Open: $grafanaUrl"