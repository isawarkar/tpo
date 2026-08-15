
# ============================================================
# Build and Run All Applications
#
# Applications:
#   1. Eureka Server
#   2. FB
#   3. Student
#   4. Exam
#
# Port Mapping:
#   Eureka   8761 -> 8761
#   FB       8080 -> 8080
#   Student  8081 -> 8080
#   Exam     8082 -> 8080
#
# Flow:
#   Maven Build
#       ↓
#   Copy WAR/JAR
#       ↓
#   Docker Build
#       ↓
#   Remove Existing Container
#       ↓
#   Start Container
# ============================================================

$ErrorActionPreference = "Stop"


# ============================================================
# Global Configuration
# ============================================================

$accId = 356723301672
$Region = "us-east-1"

$BaseProjectPath = "D:\latestworks\tpo"

$DockerBasePath = "D:\latestworks\tpo\fb\ekctl-EKS-docker\kubectl\dockerImageCreation"


# ============================================================
# Application Configuration
# IMPORTANT: Eureka is FIRST
# ============================================================

$Applications = @(

    # --------------------------------------------------------
    # 1. Eureka Server
    # --------------------------------------------------------
    @{
        Name              = "356723301672"
        MavenProject      = "$BaseProjectPath\356723301672"
        DockerPath        = "$DockerBasePath\356723301672"
        ArtifactType      = "JAR"
        ArtifactName      = "356723301672-0.0.1-SNAPSHOT.jar"
        ImageName         = "356723301672"
        ContainerName     = "356723301672-container"
        HostPort          = 8761
        ContainerPort     = 8761
        ApplicationUrl    = "http://localhost:8761/"
    },

    # --------------------------------------------------------
    # 2. FB
    # --------------------------------------------------------
    @{
        Name              = "fb"
        MavenProject      = "$BaseProjectPath\fb"
        DockerPath        = "$DockerBasePath\fb"
        ArtifactType      = "WAR"
        ArtifactName      = "FB.war"
        ImageName         = "fb-tomcat"
        ContainerName     = "fb-container"
        HostPort          = 8080
        ContainerPort     = 8080
        ApplicationUrl    = "http://localhost:8080/FB/"
    },

    # --------------------------------------------------------
    # 3. Student
    # --------------------------------------------------------
    @{
        Name              = "student"
        MavenProject      = "$BaseProjectPath\student"
        DockerPath        = "$DockerBasePath\student"
        ArtifactType      = "WAR"
        ArtifactName      = "student.war"
        ImageName         = "student-tomcat"
        ContainerName     = "student-container"
        HostPort          = 8081
        ContainerPort     = 8080
        ApplicationUrl    = "http://localhost:8081/student/"
    },

    # --------------------------------------------------------
    # 4. Exam
    # --------------------------------------------------------
    @{
        Name              = "exam"
        MavenProject      = "$BaseProjectPath\exam"
        DockerPath        = "$DockerBasePath\exam"
        ArtifactType      = "WAR"
        ArtifactName      = "exam.war"
        ImageName         = "exam-tomcat"
        ContainerName     = "exam-container"
        HostPort          = 8082
        ContainerPort      = 8080
        ApplicationUrl    = "http://localhost:8082/exam/"
    }
)


# ============================================================
# Header
# ============================================================

Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "          BUILD AND RUN ALL APPLICATIONS" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan

Write-Host ""
Write-Host "Build Order:" -ForegroundColor Yellow
Write-Host "  1. Eureka Server"
Write-Host "  2. FB"
Write-Host "  3. Student"
Write-Host "  4. Exam"

Write-Host ""
Write-Host "AWS Account : $accId" -ForegroundColor White
Write-Host "AWS Region  : $Region" -ForegroundColor White
Write-Host "Base Path   : $BaseProjectPath" -ForegroundColor White


# ============================================================
# Step 1 - Check Maven
# ============================================================

Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host " STEP 1 - CHECK MAVEN" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan

mvn -version

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Maven is not available." -ForegroundColor Red
    exit 1
}

Write-Host "Maven is available." -ForegroundColor Green


# ============================================================
# Step 2 - Check Docker
# ============================================================

Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host " STEP 2 - CHECK DOCKER" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan

docker --version

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Docker is not available." -ForegroundColor Red
    exit 1
}

Write-Host "Docker is available." -ForegroundColor Green


# ============================================================
# Step 3 - Maven Build and Copy Artifacts
# ============================================================

foreach ($App in $Applications) {

    $AppName = $App.Name
    $MavenProject = $App.MavenProject
    $DockerPath = $App.DockerPath
    $ArtifactName = $App.ArtifactName

    Write-Host ""
    Write-Host "============================================================" -ForegroundColor Cyan
    Write-Host " BUILDING: $AppName" -ForegroundColor Cyan
    Write-Host "============================================================" -ForegroundColor Cyan

    # Check Maven project
    if (-not (Test-Path $MavenProject)) {
        Write-Host "ERROR: Maven project not found:" -ForegroundColor Red
        Write-Host $MavenProject -ForegroundColor Red
        exit 1
    }

    # Maven build
    Set-Location $MavenProject

    Write-Host ""
    Write-Host "Project:" -ForegroundColor Yellow
    Write-Host $MavenProject

    Write-Host ""
    Write-Host "Running: mvn install" -ForegroundColor Yellow

    mvn install

    if ($LASTEXITCODE -ne 0) {
        Write-Host ""
        Write-Host "ERROR: Maven build failed for $AppName." -ForegroundColor Red
        exit 1
    }

    Write-Host ""
    Write-Host "Maven build completed successfully." -ForegroundColor Green

    # Find artifact
    $ArtifactSource = Join-Path $MavenProject "target\$ArtifactName"

    if (-not (Test-Path $ArtifactSource)) {
        Write-Host ""
        Write-Host "ERROR: Artifact not found:" -ForegroundColor Red
        Write-Host $ArtifactSource -ForegroundColor Red
        exit 1
    }

    Write-Host ""
    Write-Host "Artifact found:" -ForegroundColor Green
    Write-Host $ArtifactSource

    # Check Docker directory
    if (-not (Test-Path $DockerPath)) {
        Write-Host ""
        Write-Host "ERROR: Docker directory not found:" -ForegroundColor Red
        Write-Host $DockerPath -ForegroundColor Red
        exit 1
    }

    # Copy artifact
    $ArtifactDestination = Join-Path $DockerPath $ArtifactName

    Write-Host ""
    Write-Host "Copying artifact..." -ForegroundColor Yellow

    Copy-Item `
        -Path $ArtifactSource `
        -Destination $ArtifactDestination `
        -Force

    if (-not (Test-Path $ArtifactDestination)) {
        Write-Host ""
        Write-Host "ERROR: Failed to copy artifact." -ForegroundColor Red
        exit 1
    }

    Write-Host "Artifact copied successfully." -ForegroundColor Green
}


# ============================================================
# Step 4 - Build Docker Images
# ============================================================

foreach ($App in $Applications) {

    $AppName = $App.Name
    $DockerPath = $App.DockerPath
    $ImageName = $App.ImageName

    Write-Host ""
    Write-Host "============================================================" -ForegroundColor Cyan
    Write-Host " DOCKER BUILD: $AppName" -ForegroundColor Cyan
    Write-Host "============================================================" -ForegroundColor Cyan

    $Dockerfile = Join-Path $DockerPath "Dockerfile"

    if (-not (Test-Path $Dockerfile)) {
        Write-Host ""
        Write-Host "ERROR: Dockerfile not found:" -ForegroundColor Red
        Write-Host $Dockerfile -ForegroundColor Red
        exit 1
    }

    Set-Location $DockerPath

    Write-Host ""
    Write-Host "Docker directory:" -ForegroundColor Yellow
    Write-Host $DockerPath

    Write-Host ""
    Write-Host "Building image:" -ForegroundColor Yellow
    Write-Host $ImageName

    docker build -t $ImageName .

    if ($LASTEXITCODE -ne 0) {
        Write-Host ""
        Write-Host "ERROR: Docker image build failed for $AppName." -ForegroundColor Red
        exit 1
    }

    Write-Host ""
    Write-Host "Docker image built successfully." -ForegroundColor Green
}


# ============================================================
# Step 5 - Remove Existing Containers
# ============================================================

Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host " REMOVE EXISTING CONTAINERS" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan

foreach ($App in $Applications) {

    $ContainerName = $App.ContainerName

    Write-Host ""
    Write-Host "Checking container: $ContainerName" -ForegroundColor Yellow

    $ExistingContainer = docker ps -a `
        --filter "name=^${ContainerName}$" `
        --format "{{.Names}}"

    if ($ExistingContainer -eq $ContainerName) {

        Write-Host "Removing existing container..." -ForegroundColor Yellow

        docker rm -f $ContainerName

        if ($LASTEXITCODE -ne 0) {
            Write-Host ""
            Write-Host "ERROR: Failed to remove $ContainerName." -ForegroundColor Red
            exit 1
        }

        Write-Host "Container removed." -ForegroundColor Green
    }
    else {
        Write-Host "Container does not exist." -ForegroundColor Green
    }
}


# ============================================================
# Step 6 - Start All Containers
# ============================================================

Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host " START ALL CONTAINERS" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan

foreach ($App in $Applications) {

    $AppName = $App.Name
    $ImageName = $App.ImageName
    $ContainerName = $App.ContainerName
    $HostPort = $App.HostPort
    $ContainerPort = $App.ContainerPort

    Write-Host ""
    Write-Host "------------------------------------------------------------" -ForegroundColor DarkCyan
    Write-Host "Starting: $AppName" -ForegroundColor Cyan
    Write-Host "Image: $ImageName" -ForegroundColor White
    Write-Host "Container: $ContainerName" -ForegroundColor White
    Write-Host "Port: ${HostPort}:${ContainerPort}" -ForegroundColor White
    Write-Host "------------------------------------------------------------" -ForegroundColor DarkCyan

    docker run -d `
        --name $ContainerName `
        -p "${HostPort}:${ContainerPort}" `
        $ImageName

    if ($LASTEXITCODE -ne 0) {
        Write-Host ""
        Write-Host "ERROR: Failed to start $AppName." -ForegroundColor Red
        exit 1
    }

    Write-Host "Container started successfully." -ForegroundColor Green
}


# ============================================================
# Step 7 - Show Running Containers
# ============================================================

Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host " RUNNING CONTAINERS" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan

docker ps


# ============================================================
# Step 8 - Application URLs
# ============================================================

Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host " APPLICATION URLs" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan

Write-Host ""
Write-Host "Eureka   : http://localhost:8761/" -ForegroundColor Cyan
Write-Host "FB       : http://localhost:8080/FB/" -ForegroundColor Cyan
Write-Host "Student  : http://localhost:8081/student/" -ForegroundColor Cyan
Write-Host "Exam     : http://localhost:8082/exam/" -ForegroundColor Cyan


# ============================================================
# Step 9 - Useful Commands
# ============================================================

Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host " USEFUL COMMANDS" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan

Write-Host ""
Write-Host "Eureka:" -ForegroundColor Yellow
Write-Host "  docker logs -f 356723301672-container"
Write-Host "  docker stop 356723301672-container"
Write-Host "  docker start 356723301672-container"

Write-Host ""
Write-Host "FB:" -ForegroundColor Yellow
Write-Host "  docker logs -f fb-container"
Write-Host "  docker stop fb-container"
Write-Host "  docker start fb-container"

Write-Host ""
Write-Host "Student:" -ForegroundColor Yellow
Write-Host "  docker logs -f student-container"
Write-Host "  docker stop student-container"
Write-Host "  docker start student-container"

Write-Host ""
Write-Host "Exam:" -ForegroundColor Yellow
Write-Host "  docker logs -f exam-container"
Write-Host "  docker stop exam-container"
Write-Host "  docker start exam-container"


# ============================================================
# Finished
# ============================================================

Write-Host ""
Write-Host "============================================================" -ForegroundColor Green
Write-Host " ALL APPLICATIONS STARTED SUCCESSFULLY" -ForegroundColor Green
Write-Host "============================================================" -ForegroundColor Green

Write-Host ""
Write-Host "1. Eureka   : http://localhost:8761/" -ForegroundColor Cyan
Write-Host "2. FB       : http://localhost:8080/FB/" -ForegroundColor Cyan
Write-Host "3. Student  : http://localhost:8081/student/" -ForegroundColor Cyan
Write-Host "4. Exam     : http://localhost:8082/exam/" -ForegroundColor Cyan

Write-Host ""

