# EKS Cluster Provisioning Script (Modular, Idempotent, Secure)
# Prerequisites: AWS CLI, kubectl, eksctl, Helm, PowerShell

$ErrorActionPreference = 'Stop'
# Disable PowerShell paging
$PSDefaultParameterValues['Out-Host:Paging'] = $false
# Disable AWS CLI pager
$env:AWS_PAGER = ""

# Configuration
$awsAcId = 356723301672
$alias = "fb"
$appName = "$alias-fresher-buddy"
$clusterName = "eks-$appName"
$region = "us-east-1"
$namespace = $appName
$deploymentName = "deployment-$appName"
$serviceName = "service-$appName"
$ingressName = "ingress-$appName"
$containerName = "container-$appName"
$roleName = "EKSFargatePodExecutionRole"
$policyName = "AWSLoadBalancerControllerIAMPolicy"
$albControllerSA = "aws-load-balancer-controller-$appName"
$AmazonEKSLoadBalancerControllerRole = "AmazonEKSLoadBalancerControllerRole-$appName"
$image = "$awsAcId.dkr.ecr.$region.amazonaws.com/$alias" + ":latest"
#$image = "public.ecr.aws/l6m2t8p7/docker-2048:latest"
$containerPort = 8080
$noOfReplicas = 1
$prometheusUserName = "indrajeet"
$prometheusPassword = "JeeT@1984"
$logGroup = "/aws/eks/$clusterName/logs"
$cpu = 1
$memory = "1024Mi"
$health = "/FB/actuator/health"
$domainName = "fresherbuddy.in"
$aRecordName = "www.$domainName"
$MonitoringNs = "monitoring"
#ClusterIP: (default) accessible only within the cluster NodePort: accessible from outside the cluster via a node’s IP and port LoadBalancer: creates an external load balancer (for public access) ExternalName: maps to an external DNS name

function createChoicePromptHttpsOrHttps {
	
	Add-Type -AssemblyName System.Windows.Forms

# Create a new form
$form = New-Object System.Windows.Forms.Form
$form.Text = "Confirmation"
$form.Size = New-Object System.Drawing.Size(400,150)
$form.StartPosition = "CenterScreen"

# Label
$label = New-Object System.Windows.Forms.Label
$label.Text = "Do you want to proceed with 'Https(SSL)' or 'Http'?"
$label.AutoSize = $true
$label.Location = New-Object System.Drawing.Point(50,20)
$form.Controls.Add($label)

# Custom 'https' button
$btnProceed = New-Object System.Windows.Forms.Button
$btnProceed.Text = "https"
$btnProceed.Location = New-Object System.Drawing.Point(40,60)
$btnProceed.Add_Click({
    $form.Tag = "https"
    $form.Close()
})

# Set background color (e.g., LightBlue)
$btnProceed.BackColor = [System.Drawing.Color]::Green

# Optional: Set text color
$btnProceed.ForeColor = [System.Drawing.Color]::White

$form.Controls.Add($btnProceed)

# Custom 'http' button
$btnAbort = New-Object System.Windows.Forms.Button
$btnAbort.Text = "http"
$btnAbort.Location = New-Object System.Drawing.Point(150,60)
$btnAbort.Add_Click({
    $form.Tag = "http"
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


try {
    Add-Type -AssemblyName System.Windows.Forms
	#$result = [System.Windows.Forms.MessageBox]::Show("Press 'Yes' for HTTPS(SSL) and 'No' for HTTP(Not Recommmended) ?", "Confirmation", "YesNo", "Question")
$obj = createChoicePromptHttpsOrHttps
$obj
if ($obj -eq "https") {
.\AWS-EKS-with-farget-https.ps1
}else {
	.\AWS-EKS-with-farget-http.ps1
}
}
catch {
    Write-Host "`n❌ Script failed!" -ForegroundColor Red
    Write-Host "Message   : $($_.Exception.Message)" -ForegroundColor Yellow
    Write-Host "Script    : $($_.InvocationInfo.ScriptName)"
    Write-Host "Line      : $($_.InvocationInfo.ScriptLineNumber)"
    Write-Host "Position  : $($_.InvocationInfo.OffsetInLine)"
    Write-Host "Command   : $($_.InvocationInfo.Line.Trim())`n"
}