$RepositoryName = "eureka"
$Region = "us-east-1"
cd D:\latestworks\tpo\$appname
mvn install
D:
copy D:\latestworks\tpo\eurekaserver\target\eurekaServer-0.0.1-SNAPSHOT.jar D:\latestworks\tpo\fb\ekctl-EKS-docker\kubectl\dockerImageCreation\eurekaserver\eurekaServer-0.0.1-SNAPSHOT.jar
D:
cd D:\latestworks\tpo\fb\ekctl-EKS-docker\kubectl\dockerImageCreation\eurekaserver
docker build -t fb-eureka .
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 356723301672.dkr.ecr.us-east-1.amazonaws.com
docker tag fb-eureka:latest 356723301672.dkr.ecr.us-east-1.amazonaws.com/eureka:latest
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

docker push 356723301672.dkr.ecr.us-east-1.amazonaws.com/eureka:latest
#docker run -d --name A1 -p 8761:8761 fb-eureka
