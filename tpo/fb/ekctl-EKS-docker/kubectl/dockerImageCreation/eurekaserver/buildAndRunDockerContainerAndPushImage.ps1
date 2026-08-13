$RepositoryName = "eureka"
$Region = "us-east-1"
copy D:\Mywork\eurekaserver\target\eurekaServer-0.0.1-SNAPSHOT.jar D:\Mywork\fb\ekctl-EKS-docker\kubectl\dockerImageCreation\eureka\eurekaServer-0.0.1-SNAPSHOT.jar
D:
cd D:\Mywork\fb\ekctl-EKS-docker\kubectl\dockerImageCreation\eureka
docker build -t fb-eureka .
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 542115677157.dkr.ecr.us-east-1.amazonaws.com
docker tag fb-eureka:latest 542115677157.dkr.ecr.us-east-1.amazonaws.com/eureka:latest
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

docker push 542115677157.dkr.ecr.us-east-1.amazonaws.com/eureka:latest
#docker run -d --name A1 -p 8761:8761 fb-eureka
