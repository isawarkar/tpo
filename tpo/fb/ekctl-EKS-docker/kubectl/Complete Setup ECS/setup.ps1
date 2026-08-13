
$env:AWS_PAGER = ""
$ProgressPreference = "SilentlyContinue"

Add-Type -AssemblyName System.Windows.Forms

function Test-DockerDesktopRunning {
    # Check Docker Desktop process
    $dockerProcess = Get-Process -Name "Docker Desktop" -ErrorAction SilentlyContinue

    # Fallback: check docker engine via CLI
    if (-not $dockerProcess) {
        docker info *> $null
        if ($LASTEXITCODE -eq 0) {
            return $true
        }
        return $false
    }

    return $true
}

if (-not (Test-DockerDesktopRunning)) {
    $result = [System.Windows.Forms.MessageBox]::Show(
        "🐳 Docker Desktop is NOT running.`n`nPlease start Docker Desktop and click OK to continue.",
        "Docker Desktop Required",
        [System.Windows.Forms.MessageBoxButtons]::OKCancel,
        [System.Windows.Forms.MessageBoxIcon]::Warning
    )

    if ($result -ne [System.Windows.Forms.DialogResult]::OK) {
        Write-Host "❌ Docker Desktop not running. Script aborted."
        exit 1
    }

    Write-Host "⏳ Waiting for Docker Desktop to start..."

    # Wait until Docker is ready
    while (-not (Test-DockerDesktopRunning)) {
        Start-Sleep -Seconds 30
    }

    Write-Host "✅ Docker Desktop is now running."
}
else {
    Clear-Host
Write-Host "===== Project Setup Wizard =====" -ForegroundColor Cyan
Write-Host ""

# --------------------------------------------------
# Base directory
# --------------------------------------------------
$BaseDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$RootDir = Split-Path -Parent $BaseDir

# --------------------------------------------------
# RDS CONFIRMATION
# --------------------------------------------------
$rdsChoice = Read-Host "Do you want to CREATE RDS Database? (YES / NO)"

if ($rdsChoice.ToUpper() -eq "YES") {

    Write-Host "Creating RDS..." -ForegroundColor Green
    $rdsScript = Join-Path $BaseDir "createRDS.ps1"

    if (Test-Path $rdsScript) {
        & $rdsScript
    }
    else {
        Write-Host "❌ createRDS.ps1 not found!" -ForegroundColor Red
        exit 1
    }
}
else {
    Write-Host "Skipping RDS creation." -ForegroundColor Yellow
}

# --------------------------------------------------
# EUREKA DOCKER IMAGE (ALWAYS ASK, RUN EARLY)
# --------------------------------------------------
Write-Host ""
$eurekaDockerChoice = Read-Host "Do you want to build & push Docker image for 'eureka'? (YES / NO)"

if ($eurekaDockerChoice.ToUpper() -eq "YES") {

    Write-Host "Building Eureka Docker image..." -ForegroundColor Green

    $eurekaDockerDir = Join-Path $RootDir "dockerImageCreation\eureka"
    $eurekaDockerScript = Join-Path $eurekaDockerDir "buildAndRunDockerContainerAndPushImage.ps1"

    if (-not (Test-Path $eurekaDockerScript)) {
        Write-Host "❌ Eureka Docker build script not found!" -ForegroundColor Red
        exit 1
    }

    Push-Location $eurekaDockerDir
    & $eurekaDockerScript
    Pop-Location
}
else {
    Write-Host "Skipping Eureka Docker image build." -ForegroundColor Yellow
}

# --------------------------------------------------
# EUREKA ECS CONFIRMATION
# --------------------------------------------------
Write-Host ""
$eurekaChoice = Read-Host "Do you want to run Eureka ECS setup? (YES / NO)"

if ($eurekaChoice.ToUpper() -eq "YES") {

    Write-Host "===== Eureka ECS Setup =====" -ForegroundColor Cyan

    $EcsDir = Join-Path $RootDir "AWS ECS cluster"
    $eurekaScript = Join-Path $EcsDir "0 AWS-ECS-Setup-Eureca.ps1"

    if (-not (Test-Path $eurekaScript)) {
        Write-Host "❌ Eureka ECS script not found!" -ForegroundColor Red
        exit 1
    }

    Push-Location $EcsDir
    & ".\0 AWS-ECS-Setup-Eureca.ps1"
    Pop-Location
}
else {
    Write-Host "Skipping Eureka ECS setup." -ForegroundColor Yellow
}

# --------------------------------------------------
# DOCKER IMAGE CREATION (WITHOUT EUREKA)
# --------------------------------------------------
Write-Host ""
Write-Host "===== Docker Image Creation =====" -ForegroundColor Cyan

$DockerBaseDir = Join-Path $RootDir "dockerImageCreation"
$apps = @("fb", "exam", "student")

foreach ($app in $apps) {

    Write-Host ""
    $choice = Read-Host "Do you want to build & push Docker image for '$app'? (YES / NO)"

    if ($choice.ToUpper() -eq "YES") {

        $appPath = Join-Path $DockerBaseDir $app
        $scriptPath = Join-Path $appPath "buildAndRunDockerContainerAndPushImage.ps1"

        if (Test-Path $scriptPath) {
            Write-Host "Building Docker image for $app..." -ForegroundColor Green
            Push-Location $appPath
            & $scriptPath
            Pop-Location
        }
        else {
            Write-Host "❌ Script not found in $appPath" -ForegroundColor Red
        }
    }
    else {
        Write-Host "Skipping Docker build for $app." -ForegroundColor Yellow
    }
}

# --------------------------------------------------
# ECS CLUSTER SETUP (WITHOUT EUREKA)
# --------------------------------------------------
Write-Host ""
Write-Host "===== AWS ECS Cluster Setup =====" -ForegroundColor Cyan

$EcsDir = Join-Path $RootDir "AWS ECS cluster"
Push-Location $EcsDir

$ecsScripts = @(
    "1 AWS-ECS-Setup-FresherBuddy - Seprate ALB.ps1",
    "2 AWS-ECS-Setup-Exam.ps1",
    "3 AWS-ECS-Setup-Student.ps1"
)

foreach ($ecsScript in $ecsScripts) {

    Write-Host ""
    $choice = Read-Host "Do you want to run '$ecsScript'? (YES / NO)"

    if ($choice.ToUpper() -eq "YES") {

        if (Test-Path $ecsScript) {
            Write-Host "Running $ecsScript..." -ForegroundColor Green
            & ".\$ecsScript"
        }
        else {
            Write-Host "❌ Script not found: $ecsScript" -ForegroundColor Red
        }
    }
    else {
        Write-Host "Skipping $ecsScript." -ForegroundColor Yellow
    }
}

Pop-Location

# --------------------------------------------------
# DONE
# --------------------------------------------------
Write-Host ""

Write-Host ""
    $choice = Read-Host "Do you want to create S3 buckets? (YES / NO)"

    if ($choice.ToUpper() -eq "YES") {
		Clear-Host

# --------------------------------
# HARD-CODED CONFIG
# --------------------------------
$awsAcId = 542115677157
$region = "us-east-1"

$Buckets = @(
    "$awsAcId-dbbackup-fb",
    "$awsAcId-fresherbuddy-files-server"
)

Write-Host "🚀 Starting S3 bucket creation" -ForegroundColor Cyan
Write-Host "Region : $region"
Write-Host ""

foreach ($BucketName in $Buckets) {

    Write-Host "----------------------------------------"
    Write-Host "🪣 Processing bucket: $BucketName" -ForegroundColor Yellow

    # Check if bucket exists
    aws s3api head-bucket `
        --bucket $BucketName `
        --region $region 2>$null

    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Bucket already exists. Skipping." -ForegroundColor Green
        continue
    }

    # Create bucket
    Write-Host "🚀 Creating bucket..." -ForegroundColor Cyan

    if ($region -eq "us-east-1") {
        aws s3api create-bucket `
            --bucket $BucketName `
            --region $region
    } else {
        aws s3api create-bucket `
            --bucket $BucketName `
            --region $region `
            --create-bucket-configuration LocationConstraint=$region
    }

    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Failed to create bucket $BucketName" -ForegroundColor Red
        exit 1
    }

    Write-Host "🎉 Bucket '$BucketName' created successfully." -ForegroundColor Green
}

Write-Host ""
Write-Host "✅ All buckets processed." -ForegroundColor Cyan

    }
    else {
        Write-Host "Skipping $ecsScript." -ForegroundColor Yellow
    }
Write-Host "===== Setup Completed Successfully =====" -ForegroundColor Cyan

}
