D:
cd D:\Mywork\fb\ekctl-EKS-docker\kubectl\dockerImageCreation
copy D:\Mywork\fb\target\FB.war FB.war
docker build -t fb-image:latest .
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 542115677157.dkr.ecr.us-east-1.amazonaws.com
docker tag fb-image:latest 542115677157.dkr.ecr.us-east-1.amazonaws.com/fb:latest
docker push 542115677157.dkr.ecr.us-east-1.amazonaws.com/fb:latest
#docker run -d --name A2 -p 8080:8080 fb-image:latest