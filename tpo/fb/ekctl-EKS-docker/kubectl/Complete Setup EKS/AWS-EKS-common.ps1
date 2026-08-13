
function createPolicies {
	 $policyARN = aws iam list-policies --query "Policies[?PolicyName=='$policyName'].Arn" --output text

    if (-not $policyARN) {
        curl -O https://raw.githubusercontent.com/kubernetes-sigs/aws-load-balancer-controller/v2.11.0/docs/install/iam_policy.json
        if (-not (Test-Path "./iam_policy.json")) {
            Write-Error "❌ Failed to download IAM policy."
            exit 1
        }
        aws iam create-policy --policy-name $policyName --policy-document file://iam_policy.json
        $policyARN = aws iam list-policies --query "Policies[?PolicyName=='$policyName'].Arn" --output text
		Write-Host "📄 Policy : $policyName created" -ForegroundColor Green $policyARN
    }
	
	Write-Host "Start Creating Role and Attaching AmazonEC2ContainerRegistryReadOnly,CloudWatchLogsFullAccess Policy to Fargate Pod Execution Role..." -ForegroundColor Cyan 

$podExecutionRoleArn = aws iam get-role --role-name $roleName --query "Role.Arn" --output text
if (![string]::IsNullOrEmpty($podExecutionRoleArn)) {
    Write-Host "`n[4/5] Fargate Pod Execution Role $podExecutionRoleArn exist" -ForegroundColor Green
} else{
	# Create JSON file
@'
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "eks-fargate-pods.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
'@ | Out-File -FilePath "trust-policy.json" -Encoding ascii

# Use it with AWS CLI
$response = aws iam create-role `
  --role-name $roleName `
  --assume-role-policy-document file://trust-policy.json `
  --output json | ConvertFrom-Json

$podExecutionRoleArn = $response.Role.Arn
}

 aws iam attach-role-policy `
  --role-name $roleName `
  --policy-arn arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryReadOnly
  	  
 aws iam attach-role-policy `
        --role-name $roleName `
        --policy-arn arn:aws:iam::aws:policy/CloudWatchLogsFullAccess
 Write-Host "END Creating Role and Attaching AmazonEC2ContainerRegistryReadOnly,CloudWatchLogsFullAccess Policy to Fargate Pod Execution Role..." -ForegroundColor Cyan 
 
}

function EKSFluentBitRole {
	Write-Host "Start Creating EKSFluentBitRole and associate-iam-oidc-provider" -ForegroundColor Cyan 

	eksctl utils associate-iam-oidc-provider --cluster $clusterName --approve

	$policy = @'
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "logs:PutLogEvents",
        "logs:DescribeLogStreams",
        "logs:DescribeLogGroups",
        "logs:CreateLogStream",
        "logs:CreateLogGroup"
      ],
      "Resource": "*"
    }
  ]
}
'@

# Save to file
$policy | Out-File -FilePath "fluent-bit-cloudwatch-policy.json" -Encoding utf8

aws iam create-policy `
  --policy-name FluentBitCloudWatchLogsPolicy `
  --policy-document file://fluent-bit-cloudwatch-policy.json
  
  $policyARN = aws iam list-policies --query "Policies[?PolicyName=='FluentBitCloudWatchLogsPolicy'].Arn" --output text

$namespace = "amazon-cloudwatch"
$serviceAccountName = "fluent-bit"
$roleName = "FluentBitServiceAccountRole"

$oidcHost = aws eks describe-cluster --name $clusterName --region $region --query "cluster.identity.oidc.issuer" --output text
$oidcUrl = $oidcHost -replace '^https://', ''
Write-Host "OIDC Provider URL: $oidcUrl"
# Final federated ARN
$federatedArn = "arn:aws:iam::$awsAcId" + ":oidc-provider/$oidcUrl"
$federatedArn
$oidcUrlSub = "$oidcUrl" + ":sub"
  
  $policy1 = @"
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Federated": "$federatedArn"
      },
      "Action": "sts:AssumeRoleWithWebIdentity",
      "Condition": {
        "StringEquals": {
          "$oidcUrlSub": "system:serviceaccount:${namespace}:${serviceAccountName}"
        }
      }
    }
  ]
}
"@

$policy1 | Out-File -FilePath "fluent-bit-trust-policy.json" -Encoding utf8
# Save to file
aws iam create-role --role-name $roleName  --assume-role-policy-document file://fluent-bit-trust-policy.json
aws iam attach-role-policy --role-name $roleName  --policy-arn $policyARN
Write-Host "End Creating $roleName  and associate-iam-oidc-provider" -ForegroundColor Cyan
}

function Create-EKSCluster {
    	# Build YAML content
createPolicies
$yamlContent = @"
apiVersion: eksctl.io/v1alpha5
kind: ClusterConfig

metadata:
  name: $clusterName
  region: $region

"@
# Write to file
$yamlPath = "cluster-config.yaml"
$yamlContent | Out-File -Encoding UTF8 -FilePath $yamlPath

Write-Host "🚀 Creating new EKS cluster : $clusterName" -ForegroundColor Green

eksctl create cluster -f cluster-config.yaml

kubectl get namespace $namespace -o name > $null 2>&1
    if ($LASTEXITCODE -ne 0) {
		aws eks update-kubeconfig --name $clusterName --region $region
        kubectl create namespace $namespace --save-config
}

EKSFluentBitRole
$choiceFargateOrEc2 = createChoicePrompt

if ($choiceFargateOrEc2 -eq "Yes") {
$podExecutionRoleArn = aws iam get-role --role-name $roleName --query "Role.Arn" --output text
$yamlContent = @"
apiVersion: eksctl.io/v1alpha5
kind: ClusterConfig
metadata:
  name: $clusterName
  region: $region
fargateProfiles:
  - name: $appName
    selectors:
      - namespace: monitoring
      - namespace: amazon-cloudwatch
      - namespace: $namespace
      - namespace: kube-system
    podExecutionRoleARN: $podExecutionRoleArn
"@

# Write to file
$yamlPath = "fargate-profile.yaml"
$yamlContent | Out-File -Encoding UTF8 -FilePath $yamlPath

Write-Host "🚀 Creating Fargate profile : $appName" -ForegroundColor Green
eksctl create fargateprofile -f fargate-profile.yaml

Write-Host "📦 Deploying sample app..." -ForegroundColor Green
Deploy-SampleApp

$status = waitTOCheckClusterStatus
if($status -eq "ACTIVE"){
Write-Host "🔧 Setting up ALB Controller..." -ForegroundColor Green
$status = waitTOCheckProfileStatus
if ($status -eq "ACTIVE") {
    Write-Host "✅ Fargate '$appName' is ACTIVE." -ForegroundColor Green
	Setup-ALBController
} else {
    Write-Host "❌ Fargate profile creation FAILED." -ForegroundColor Red
}
}

} else {

	ssh-keygen -t rsa -b 2048 -f id_rsa
	$sshKeyPath = "id_rsa.pub"
$nodeGroupYaml = @"
---
apiVersion: eksctl.io/v1alpha5
kind: ClusterConfig
metadata:
  name: $clusterName
  region: $region
nodeGroups:
  - name: $nodeGroupName
    instanceType: $instanceType
    amiFamily: AmazonLinux2023
    desiredCapacity: $desiredCapacity
    minSize: 1
    maxSize: 3
    volumeSize: 20
    ssh:
      allow: true
      publicKeyPath: $sshKeyPath
    labels:
      role: worker
    tags:
      nodegroup-role: worker
    iam:
      withAddonPolicies:
        autoScaler: true
        cloudWatch: true
        awsLoadBalancerController: true
addons:
  - name: vpc-cni
    version: latest
    attachPolicyARNs:
      - arn:aws:iam::aws:policy/AmazonEKS_CNI_Policy
    podIdentityAssociations:
      - namespace: kube-system
        serviceAccountName: aws-node
        roleName: eks-vpc-cni-irsa-role
"@

# Write node group YAML
$nodeGroupYamlPath = ".\eks-nodegroup.yaml"
$nodeGroupYaml | Set-Content $nodeGroupYamlPath

# Create node group
eksctl create nodegroup -f $nodeGroupYamlPath

Write-Host "📦 Deploying sample app..." -ForegroundColor Green
Deploy-SampleApp

Setup-ALBController
}
}

function Setup-ALBController {
	 Write-Host "✅ ALBController deployment started" -ForegroundColor yellow
	 
	 $policyARN = aws iam list-policies --query "Policies[?PolicyName=='$policyName'].Arn" --output text
	 
	 eksctl create iamserviceaccount `
        --cluster=$clusterName `
        --namespace=kube-system `
        --name=$albControllerSA `
        --role-name $AmazonEKSLoadBalancerControllerRole `
        --attach-policy-arn=$policyARN `
        --approve `
        --override-existing-serviceaccounts

    helm repo add eks https://aws.github.io/eks-charts
    helm repo update

    $vpcId = aws eks describe-cluster --name $clusterName --region $region --query "cluster.resourcesVpcConfig.vpcId" --output text

    helm install aws-load-balancer-controller eks/aws-load-balancer-controller -n kube-system `
        --set clusterName=$clusterName `
        --set serviceAccount.create=false `
        --set serviceAccount.name=$albControllerSA `
        --set region=$region `
        --set vpcId=$vpcId
			
	Write-Host "📦 ALBController $albControllerSA  deployment done.Please wait at least 5 minutes..." -ForegroundColor Green
	Start-Sleep -Seconds 300
	createHostedZoneAndArecord	
}

function createHostedZoneAndArecord {
	  Write-Host "Creating createHostedZoneAndArecord start.........."
   $albDns = aws elbv2 describe-load-balancers --query "LoadBalancers[?starts_with(LoadBalancerName, 'k8s-$alias')].DNSName" --output text --region $region
   $albDns
   $canonicalHostedZoneId = aws elbv2 describe-load-balancers --query "LoadBalancers[?starts_with(LoadBalancerName, 'k8s-$alias')].CanonicalHostedZoneId" --output text --region $region
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
        $trustFile = "$env:TEMP\ecs-trust-policy.json"
        $aRecord | Out-File -Encoding ASCII -FilePath $trustFile
aws route53 change-resource-record-sets --hosted-zone-id $hostedZoneId --change-batch file://$trustFile
Write-Host "Wait for 2 minutes" -ForegroundColor Green
	
Start-Sleep -Seconds 120
 Write-Host "Your application is accessible at: https://$aRecordName" -ForegroundColor Yellow
    
}

function showPodsSVCIngressDeploy {

Write-Host "🌐 Pods Status " -ForegroundColor Cyan
kubectl get pods -n $namespace
Write-Host "🌐 Service Status " -ForegroundColor green
kubectl get svc -n $namespace
Write-Host "🌐 Ingress Status " -ForegroundColor Yellow
kubectl get ingress -n $namespace
Write-Host "🌐 Deploy Status on kube-system " -ForegroundColor Yellow
kubectl get deploy -n kube-system
    $dns = aws elbv2 describe-load-balancers `
  --query "LoadBalancers[?starts_with(LoadBalancerName, 'k8s-$alias')].DNSName" `
  --output text
    Write-Host "🌐 Access the app at: http://$dns" -ForegroundColor Cyan
	
}

function Setup-Monitoring {
	Write-Host "🚀 Setting up Prometheus + Grafana on EKS" -ForegroundColor Green

# =========================
# STEP 1: Namespace
# =========================
@"
apiVersion: v1
kind: Namespace
metadata:
  name: $MonitoringNs
"@ | kubectl apply -f -

kubectl get ns $MonitoringNs

# =========================
# STEP 2: Helm Repos
# =========================
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts 2>$null
helm repo add eks https://aws.github.io/eks-charts 2>$null
helm repo update

# =========================
# STEP 3: OIDC Provider (SAFE CHECK)
# =========================
$oidcUrl = aws eks describe-cluster `
  --name $clusterName `
  --region $region `
  --query "cluster.identity.oidc.issuer" `
  --output text

if (-not $oidcUrl) {
    Write-Host "❌ Cluster not found or access denied" -ForegroundColor Red
    exit 1
}

$oidcProviderExists = aws iam list-open-id-connect-providers `
  --query "OpenIDConnectProviderList[?contains(Arn, '$($oidcUrl.Split('/')[-1])')].Arn" `
  --output text

if ($oidcProviderExists) {
    Write-Host "✅ OIDC provider already exists"
} else {
    Write-Host "🔐 Creating OIDC provider..."
    eksctl utils associate-iam-oidc-provider `
      --cluster $clusterName `
      --region $region `
      --approve
}

# =========================
# STEP 4: IAM Policy for ALB Controller
# =========================
Invoke-WebRequest `
  -Uri https://raw.githubusercontent.com/kubernetes-sigs/aws-load-balancer-controller/main/docs/install/iam_policy.json `
  -OutFile iam_policy.json

$policyArn = aws iam list-policies `
  --scope Local `
  --query "Policies[?PolicyName=='$policyName'].Arn" `
  --output text

if (-not $policyArn) {
    Write-Host "🔐 Creating IAM policy for ALB controller..."
    $policyArn = aws iam create-policy `
        --policy-name $policyName `
        --policy-document file://iam_policy.json `
        --query Policy.Arn `
        --output text
} else {
    Write-Host "✅ IAM policy already exists"
}

# =========================
# STEP 5: IAM Service Account
# =========================
eksctl create iamserviceaccount `
  --cluster $clusterName `
  --region $region `
  --namespace kube-system `
  --name aws-load-balancer-controller `
  --attach-policy-arn $policyArn `
  --approve `
  --override-existing-serviceaccounts

kubectl get sa aws-load-balancer-controller -n kube-system
kubectl apply -k "github.com/aws/eks-charts/stable/aws-load-balancer-controller//crds?ref=master"
kubectl apply -f https://github.com/kubernetes-sigs/aws-load-balancer-controller/releases/latest/download/aws-load-balancer-controller.yaml


# =========================
# STEP 6: Install ALB Controller
# =========================
helm upgrade --install aws-load-balancer-controller eks/aws-load-balancer-controller `
  -n kube-system `
  --set clusterName=$clusterName `
  --set serviceAccount.create=false `
  --set serviceAccount.name=aws-load-balancer-controller

kubectl get pods -n kube-system | Select-String load-balancer

# =========================
# STEP 7: Grafana Admin Secret (SECURE)
# =========================
kubectl create secret generic grafana-admin-secret `
  -n $MonitoringNs `
  --from-literal=admin-user=$prometheusUserName `
  --from-literal=admin-password=$prometheusPassword `
  --dry-run=client -o yaml | kubectl apply -f -

# =========================
# STEP 8: values.yaml
# =========================
@"
prometheusOperator:
  admissionWebhooks:
    enabled: false

grafana:
  enabled: true

  admin:
    existingSecret: grafana-admin-secret
    userKey: admin-user
    passwordKey: admin-password

  service:
    type: ClusterIP
    port: 80
    targetPort: 3000

  sidecar:
    dashboards:
      enabled: true
      label: grafana_dashboard
    datasources:
      enabled: true

  resources:
    requests:
      cpu: 500m
      memory: 512Mi
    limits:
      cpu: "1"
      memory: 1Gi

prometheus:
  enabled: true

  prometheusSpec:
    maximumStartupDurationSeconds: 300

    resources:
      requests:
        cpu: 750m
        memory: 1Gi
      limits:
        cpu: "1.5"
        memory: 2Gi
"@ | Out-File values.yaml -Encoding utf8

# =========================
# STEP 9: Install Prometheus Stack
# =========================
helm upgrade --install prometheus prometheus-community/kube-prometheus-stack `
  -n $MonitoringNs `
  --create-namespace `
  -f values.yaml

Write-Host "⏳ Waiting for monitoring pods..."
Start-Sleep -Seconds 60
kubectl get pods -n $MonitoringNs

# =========================
# STEP 10: Grafana ALB Ingress
# =========================
@"
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: grafana-alb
  namespace: monitoring
  annotations:
    alb.ingress.kubernetes.io/scheme: internet-facing
    alb.ingress.kubernetes.io/target-type: ip
    alb.ingress.kubernetes.io/listen-ports: '[{"HTTP":80}]'
    alb.ingress.kubernetes.io/healthcheck-path: /login
spec:
  ingressClassName: alb
  rules:
    - http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: prometheus-grafana
                port:
                  number: 80
"@ | kubectl apply -f -


Write-Host "⏳ Waiting for ALB to be provisioned..."
Start-Sleep -Seconds 60

kubectl get ingress -n $MonitoringNs

# =========================
# STEP 11: Grafana Dashboard (Auto-import)
# =========================
@"
apiVersion: v1
kind: ConfigMap
metadata:
  name: grafana-node-dashboard
  namespace: $MonitoringNs
  labels:
    grafana_dashboard: "1"
data:
  node-exporter.json: |
    {
      "id": null,
      "title": "Node Exporter",
      "schemaVersion": 36,
      "version": 1
    }
"@ | kubectl apply -f -

Write-Host "✅ Prometheus & Grafana setup completed!" -ForegroundColor Green
Write-Host "🔑 Login using Grafana admin secret credentials" -ForegroundColor Yellow
kubectl get ingress -n $MonitoringNs
    Write-Host "🔑 Login with username: admin and password: $prometheusPassword"
}

function Setup-CloudWatchLogging {
    Write-Host "📄 Setting up CloudWatch logging via Fluent Bit..." -ForegroundColor Green

# Variables
$cloudwatchNamespace = "amazon-cloudwatch"
$streamPrefix = "from-fluent-bit-"         # Log stream prefix
$releaseName = "aws-for-fluent-bit"
createPolicies
# Create namespace if not exists
kubectl get namespace $cloudwatchNamespace -o name 2>$null | Out-Null
if ($LASTEXITCODE -ne 0) {
    kubectl create namespace $cloudwatchNamespace | Out-Null
    Write-Host "✅ Namespace '$cloudwatchNamespace' created."
} else {
    Write-Host "ℹ️ Namespace '$cloudwatchNamespace' already exists."
}

# Create aws-logging ConfigMap
$configMapYaml = @"
apiVersion: v1
kind: ConfigMap
metadata:
  name: aws-logging
  namespace: $cloudwatchNamespace
  labels:
    k8s-app: fluent-bit
data:
  fluent-bit.conf: |
    [SERVICE]
        Flush        1
        Daemon       Off
        Log_Level    info
        Parsers_File parsers.conf

    [INPUT]
        Name              forward
        Listen            0.0.0.0
        Port              24224

    [OUTPUT]
        Name              cloudwatch
        Match             *
        region            $region
        log_group_name    $logGroup
        log_stream_prefix $streamPrefix
        auto_create_group true

  parsers.conf: |
    [PARSER]
        Name        json
        Format      json
        Time_Key    time
        Time_Format %Y-%m-%dT%H:%M:%S
"@

$configMapYaml | kubectl apply -f -
Write-Host "✅ ConfigMap 'aws-logging' applied."


# Install Fluent Bit via Helm
Write-Host "📦 Installing Fluent Bit Helm chart..."
helm repo add fluent https://fluent.github.io/helm-charts --force-update | Out-Null
helm repo update | Out-Null

helm upgrade --install $releaseName fluent/fluent-bit `
  --namespace $cloudwatchNamespace `
  --set serviceAccount.create=true `
  --set serviceAccount.name=fluent-bit `
  --set config.existingConfigMap=aws-logging | Out-Null

Write-Host "`n✅ [5/5] EKS Logging Enabled Successfully!"
Write-Host "🔍 Check logs in CloudWatch Log Group: $logGroup" -ForegroundColor Green

}

function waitTOCheckClusterStatus {
    Write-Host "Waiting for 15 second to check cluster status.'ACTIVE'" -ForegroundColor Green
do {
    $status = aws eks describe-cluster `
        --name $clusterName `
        --region $region `
        --query "cluster.status" `
        --output text

    Write-Host "Cluster status: $status"
    Start-Sleep -Seconds 15
} while ($status -ne "ACTIVE")
Write-Host "EKS cluster is now ACTIVE."
return $status;
}

function waitTOCheckProfileStatus {
do {
    Start-Sleep -Seconds 10
    $status = aws eks describe-fargate-profile `
        --cluster-name $clusterName `
        --fargate-profile-name $appName `
        --region $region `
        --query 'fargateProfile.status' `
        --output text

    Write-Host "⏳ Fargate profile status: $status"
}
while ($status -ne "ACTIVE" -and $status -ne "FAILED")
Write-Host "profile  $appName is now ACTIVE."
return $status;
}

function createChoicePrompt {
	
	Add-Type -AssemblyName System.Windows.Forms

# Create a new form
$form = New-Object System.Windows.Forms.Form
$form.Text = "Confirmation"
$form.Size = New-Object System.Drawing.Size(400,150)
$form.StartPosition = "CenterScreen"

# Label
$label = New-Object System.Windows.Forms.Label
$label.Text = "Do you want to proceed with 'Fargate' or 'EC2 Node Group'?"
$label.AutoSize = $true
$label.Location = New-Object System.Drawing.Point(50,20)
$form.Controls.Add($label)

# Custom 'Fargate' button
$btnProceed = New-Object System.Windows.Forms.Button
$btnProceed.Text = "Fargate"
$btnProceed.Location = New-Object System.Drawing.Point(40,60)
$btnProceed.Add_Click({
    $form.Tag = "Fargate"
    $form.Close()
})
$form.Controls.Add($btnProceed)

# Custom 'EC2 Node Group' button
$btnAbort = New-Object System.Windows.Forms.Button
$btnAbort.Text = "EC2 Node Group"
$btnAbort.Location = New-Object System.Drawing.Point(150,60)
$btnAbort.Add_Click({
    $form.Tag = "EC2"
    $form.Close()
})
$form.Controls.Add($btnAbort)

# Show form and wait
$form.ShowDialog() | Out-Null

# Get result
switch ($form.Tag) {
    "Fargate" { $choiceFargateOrEc2 = 'Yes' }
    "EC2"   { $choiceFargateOrEc2 = 'No' }
    default   { Write-Host "No selection made." }
}
return $choiceFargateOrEc2
}

function deleteTargetGroup {
	# Get all target groups that start with 'k8s-$alias'
$targetGroups = aws elbv2 describe-target-groups `
    --query "TargetGroups[?starts_with(TargetGroupName, 'k8s-$alias')].TargetGroupArn" `
    --output text

foreach ($tgArn in $targetGroups -split '\s+') {
    # Check if the target group has any associated load balancer
    $lbArns = aws elbv2 describe-target-groups `
        --target-group-arns $tgArn `
        --query "TargetGroups[0].LoadBalancerArns" `
        --output text

    if ([string]::IsNullOrEmpty($lbArns)) {
        Write-Host "🗑️ Deleting target group: $tgArn"
        aws elbv2 delete-target-group --target-group-arn $tgArn
    } else {
        Write-Host "⚠️ Skipping $tgArn — still associated with Load Balancer: $lbArns"
    }
}

}

function Delete-NodeGroup {
	Write-Host "🧹 Deleting All nodes and NodeGroup " -ForegroundColor red
# Get all node names
$nodes = kubectl get nodes -o jsonpath='{.items[*].metadata.name}' -n fresher-buddy 
$nodeNames = $nodes -split '\s+'

# Iterate and take action
foreach ($node in $nodeNames) {
    Write-Host "🔍 Draining node: $node" -ForegroundColor red
  	kubectl drain $node --ignore-daemonsets --delete-emptydir-data --disable-eviction --force
}
	eksctl delete cluster --name $clusterName --region $region
	Write-Host "🧹 Deleted All nodes and NodeGroup " -ForegroundColor red
}

function Delete-VPCResources {
    $vpcId = aws ec2 describe-vpcs --filters "Name=tag:Name,Values=eksctl-$clusterName-cluster/VPC" --query "Vpcs[0].VpcId" --output text
    if ($vpcId -eq 'None') {
        Write-Host "❌ VPC does not exist." -ForegroundColor Red
        return
    }

    if (-not $vpcId) {
        Write-Host "❌ No VPC ID provided." -ForegroundColor Red
        return
    }

    Write-Host "🧹 Starting VPC resource cleanup for VPC: $vpcId..." -ForegroundColor Yellow

    # 1. Detach and delete internet gateways
    $igws = aws ec2 describe-internet-gateways --filters "Name=attachment.vpc-id,Values=$vpcId" --query 'InternetGateways[*].InternetGatewayId' --output text
  	
	foreach ($igw in $igws -split '\s+') {
        aws ec2 detach-internet-gateway --internet-gateway-id $igw --vpc-id $vpcId
        aws ec2 delete-internet-gateway --internet-gateway-id $igw
        Write-Host "✅ Deleted IGW: $igw"
    }

    # 2. Delete NAT gateways
    $natGateways = aws ec2 describe-nat-gateways `
    --filter "Name=vpc-id,Values=$vpcId" `
    --query 'NatGateways[*].NatGatewayId' `
    --output text
	
    foreach ($nat in $natGateways -split '\s+') {
        aws ec2 delete-nat-gateway --nat-gateway-id $nat
        Write-Host "⌛ Waiting for NAT GW $nat to be deleted..."
        aws ec2 wait nat-gateway-deleted --nat-gateway-ids $nat
        Write-Host "✅ NAT GW $nat deleted"
    }

    # 3. Delete VPC endpoints
   
   $endpoints = aws ec2 describe-vpc-endpoints `
    --filters "Name=vpc-id,Values=$($vpcId)" `
    --query 'VpcEndpoints[*].VpcEndpointId' `
    --output text


    if ($endpoints) {
        aws ec2 delete-vpc-endpoints --vpc-endpoint-ids $endpoints
        Write-Host "✅ VPC endpoints deleted: $endpoints"
    }

    # 4. Delete load balancers
    $lbs = aws elbv2 describe-load-balancers --query "LoadBalancers[?VpcId=='$vpcId'].LoadBalancerArn" --output text
    foreach ($lb in $lbs -split '\s+') {
        aws elbv2 delete-load-balancer --load-balancer-arn $lb
        Write-Host "✅ Deleted Load Balancer: $lb"
    }

    # 5. Delete network interfaces
    $enis = aws ec2 describe-network-interfaces --filters Name=vpc-id,Values=$vpcId --query 'NetworkInterfaces[*].NetworkInterfaceId' --output text
    foreach ($eni in $enis -split '\s+') {
        try {
            aws ec2 delete-network-interface --network-interface-id $eni
            Write-Host "✅ Deleted ENI: $eni"
        } catch {
            Write-Host "⚠️ Skipping ENI ${eni}: $_"
        }
    }

    # 6. Delete custom route tables
    $rtbs = aws ec2 describe-route-tables --filters Name=vpc-id,Values=$vpcId --query 'RouteTables[*].RouteTableId' --output text
    foreach ($rtb in $rtbs -split '\s+') {
        # Skip main route table
        $assoc = aws ec2 describe-route-tables --route-table-ids $rtb --query 'RouteTables[0].Associations[*].Main' --output text
        if ($assoc -ne "true") {
            aws ec2 delete-route-table --route-table-id $rtb
            Write-Host "✅ Deleted route table: $rtb"
        }
    }

  
    $sgs = aws ec2 describe-security-groups --filters "Name=vpc-id,Values=$vpcId" --query 'SecurityGroups[*].[GroupId,GroupName]' --output text
    foreach ($sg in $sgs -split "`n") {
        $groupId = ($sg -split '\s+')[0]
        $groupName = ($sg -split '\s+')[1]
        if ($groupName -ne "default") {
            aws ec2 delete-security-group --group-id $groupId
            Write-Host "✅ Deleted security group: $groupId ($groupName)"
        }
    }

    # 8. Delete subnets
    $subnets = aws ec2 describe-subnets --filters "Name=vpc-id,Values=$vpcId" --query 'Subnets[*].SubnetId' --output text
    foreach ($subnet in $subnets -split '\s+') {
        aws ec2 delete-subnet --subnet-id $subnet
        Write-Host "✅ Deleted subnet: $subnet"
    }

    # 9. Finally, delete the VPC
    aws ec2 delete-vpc --vpc-id $vpcId
	$vpcId = aws ec2 describe-vpcs --filters "Name=tag:Name,Values=eksctl-$clusterName-cluster/VPC" --query "Vpcs[0].VpcId" --output text
	if ($vpcId -eq 'None') {
         Write-Host "🎉 Successfully deleted VPC: $vpcId" -ForegroundColor Green
    }else {
		 Write-Host "🎉 VPC is not deleted : $vpcId .Don't worry it will be deleted in next attempt." -ForegroundColor red
	}
  
}

function Delete-EKSCluster {
		Write-Host "✅ Cluster $clusterName exists. Deleting..." -ForegroundColor red
	
	Write-Host "✅ Deleting cloudformation... eksctl-$clusterName-cluster" -ForegroundColor red
	aws cloudformation delete-stack --stack-name eksctl-$clusterName-cluster
	aws cloudformation delete-stack --stack-name eksctl-$clusterName-nodegroup-$nodeGroupName
	aws cloudformation delete-stack --stack-name eksctl-$clusterName-addon-iamserviceaccount-kube-system-$albControllerSA
	
	$choiceFargateOrEc2 = createChoicePrompt
if ($choiceFargateOrEc2 -eq "Yes") {
	eksctl delete fargateprofile --name $appName --cluster $clusterName
	
} else {
	Delete-NodeGroup
	eksctl delete nodegroup --cluster $clusterName --name $nodeGroupName
    
}
	Delete-VPCResources
	deleteTargetGroup

        Write-Host "🚀Deleting cluster..." -ForegroundColor red
    	aws cloudformation delete-stack --stack-name eksctl-$clusterName-cluster
		aws cloudformation delete-stack --stack-name eksctl-$clusterName-nodegroup-$nodeGroupName
		aws cloudformation delete-stack --stack-name eksctl-$clusterName-fargate
				
		Write-Host "⏳ Waiting for CloudFormation stack deletion... Stack Name : eksctl-$clusterName-cluster" -ForegroundColor red
        aws cloudformation wait stack-delete-complete --stack-name eksctl-$clusterName-cluster --region $region
        Write-Host "✅ Stack deleted successfully. Stack Name : eksctl-$clusterName-cluster" -ForegroundColor red
		Delete-VPCResources
		aws cloudformation delete-stack --stack-name eksctl-$clusterName-cluster
		Write-Host "⏳ Waiting for CloudFormation stack deletion... Stack Name : eksctl-$clusterName-cluster"
        aws cloudformation wait stack-delete-complete --stack-name eksctl-$clusterName-cluster --region $region
        Write-Host "✅ Stack deleted successfully. Stack Name : eksctl-$clusterName-cluster"
}

Add-Type -AssemblyName System.Windows.Forms
$result = [System.Windows.Forms.MessageBox]::Show("Do you want first Delete $clusterName and then start the setup ?", "Confirmation", "YesNo", "Question")
if ($result -eq "Yes") {
	
$result = [System.Windows.Forms.MessageBox]::Show("Select 'Yes' only for delete and 'No' for Delete and Install cluster?", "Confirmation", "YesNo", "Question")
if ($result -eq "Yes") {
	# Main Workflow
Delete-EKSCluster
} else {
	Delete-EKSCluster
   Write-Host "📦 Creating cluster $clusterName..." -ForegroundColor Green
Create-EKSCluster

Add-Type -AssemblyName System.Windows.Forms
$result = [System.Windows.Forms.MessageBox]::Show("Do you want to configure CloudWatch Logging?", "Confirmation", "YesNo", "Question")
if ($result -eq "Yes") {
Write-Host "📄 Configuring CloudWatch Logging..." -ForegroundColor Green
Setup-CloudWatchLogging
}

Add-Type -AssemblyName System.Windows.Forms
$result = [System.Windows.Forms.MessageBox]::Show("Do you want to configure Monitoring?", "Confirmation", "YesNo", "Question")
if ($result -eq "Yes") {
Write-Host "📊 Installing monitoring tools..." -ForegroundColor Green
Setup-Monitoring
}
}
Add-Type -AssemblyName System.Windows.Forms
[System.Windows.Forms.MessageBox]::Show("✅ Script completed successfully!", "Done", 'OK', 'Information')

}else {
    Write-Host "Exiting..."
    exit
}

