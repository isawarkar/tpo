$appname = "student"
$accId = 356723301672
$RepositoryName = $appname
$Region = "us-east-1"
cd D:\latestworks\tpo\$appname
mvn install
D:
cd D:\latestworks\tpo\fb\ekctl-EKS-docker\kubectl\dockerImageCreation\$appname
copy D:\latestworks\tpo\$appname\target\$appname.war $appname.war
docker build -t "$appname-image:latest" .
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin "$accId.dkr.ecr.us-east-1.amazonaws.com"
docker tag student-image:latest 356723301672.dkr.ecr.us-east-1.amazonaws.com/student:latest

Write-Host "Checking if ECR repository '$RepositoryName' exists in region '$Region'..."

$repo = aws ecr describe-repositories `
    --repository-names $RepositoryName `
    --region $Region `
    --query "repositories[0].repositoryName" `
    --output text 2>$null

if ($LASTEXITCODE -ne 0 -or $repo -eq "None") {
    Write-Host "Repository not found. Creating ECR repository '$RepositoryName'..."
    
    aws ecr create-repository `
        --repository-name $RepositoryName `
        --region $Region `
        --image-scanning-configuration scanOnPush=true `
        --encryption-configuration encryptionType=AES256 `
        --output table

    Write-Host "✅ Repository '$RepositoryName' created successfully."
}
else {
    Write-Host "✅ Repository '$RepositoryName' already exists."
}

docker push 356723301672.dkr.ecr.us-east-1.amazonaws.com/student:latest
#docker run -d --name A2 -p 8080:8080 $appname-image:latest