# Disable PowerShell paging
$PSDefaultParameterValues['Out-Host:Paging'] = $false
# Disable AWS CLI pager
$env:AWS_PAGER = ""

# Configuration Variables
$awsAcId = 356723301672
$alias = "fb"
$clusterName = "$alias-ecs"
$taskName = "$alias-task"
$serviceName = "$alias-service"
$region = "us-east-1"
$imageName = "$awsAcId.dkr.ecr.$region.amazonaws.com/$alias" + ":latest"
$port = 8080
$ecsTaskExecutionRoleName = "ecsTaskExecutionRole"
$ApplicationLoadBalancerName = "$clusterName-load-balancer"
$targetGroupName = "$clusterName-target-group"
$healthCheckPath = "/FB/actuator/health"
$containerName = "$alias-Container"
$logGroupName = "/ecs/$alias-logGroup"
$cpu = 1024
$memory = 2048
$desiredTgCount  = 1
$domainName = "fresherbuddy.in"
$aRecordName = "www.$domainName"
$priority = 11

$serviceNamegrafana  = "$alias-service-grafana"
$taskFamily          = "grafana"
$adminUser           = "indrajeet"
$adminPassword       = "JeeT@1984"
$launchType          = "FARGATE"  # change to ec2 id ec2 type
$roleName            = "GrafanaEcsTaskRole"

# Run mode: true = run-task, false = run service with ALB

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
if ($obj -eq "https") {
	. "$($MyInvocation.MyCommand.Path | Split-Path)\AWS_ECS_cluster_https.ps1"

Add-Type -AssemblyName Microsoft.VisualBasic
$result = [Microsoft.VisualBasic.Interaction]::MsgBox("Press 'Yes' Delete,Create Cluster and 'No' for Delete Cluster?", "YesNo,Question", "Confirmation")

if ($result -eq "Yes") {
    deleteECSCluster
	createECSCluster
} else {
    deleteECSCluster
	}
}else {
	exit
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