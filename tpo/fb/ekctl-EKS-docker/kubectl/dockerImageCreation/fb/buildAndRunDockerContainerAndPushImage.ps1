$appname = "fb"
$accId = 542115677157
$RepositoryName = $appname
$Region = "us-east-1"
cd D:\Mywork\$appname
mvn install
D:
cd D:\Mywork\fb\ekctl-EKS-docker\kubectl\dockerImageCreation\$appname
copy D:\Mywork\$appname\target\FB.war FB.war
docker build -t "$appname-image:latest" .
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin "$accId.dkr.ecr.us-east-1.amazonaws.com"
docker tag fb-image:latest 542115677157.dkr.ecr.us-east-1.amazonaws.com/fb:latest

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

docker push 542115677157.dkr.ecr.us-east-1.amazonaws.com/fb:latest
#docker run -d --name A2 -p 8080:8080 $appname-image:latest