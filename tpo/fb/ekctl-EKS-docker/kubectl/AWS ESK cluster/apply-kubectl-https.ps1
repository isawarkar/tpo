
# Configuration
$awsAcId = 356723301672
$alias = "student"
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
$health = "/$alias/actuator/health"
$domainName = "fresherbuddy.in"
$aRecordName = "$alias.$domainName"

#Node group Setting
$nodeGroupName = "$appName-ng"
$instanceType = "t3.medium"
$desiredCapacity = 1
$grafanaServiceType = "ClusterIP"
#ClusterIP: (default) accessible only within the cluster NodePort: accessible from outside the cluster via a node’s IP and port LoadBalancer: creates an external load balancer (for public access) ExternalName: maps to an external DNS name


$certARN = $(aws acm list-certificates --region us-east-1 --query "CertificateSummaryList[?DomainName=='$domainName'].CertificateArn" --output text)

function showPods {
Write-Host "🌐 Pods Status " -ForegroundColor Cyan
kubectl get pods -n $namespace
Write-Host "🌐 Service Status " -ForegroundColor green
kubectl get svc -n $namespace
Write-Host "🌐 Ingress Status " -ForegroundColor Yellow
kubectl get ingress -n $namespace

}
	
@"
---
apiVersion: v1
kind: Namespace
metadata:
  name: $namespace
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: $deploymentName
  namespace: $namespace
spec:
  replicas: $noOfReplicas
  selector:
    matchLabels:
      app.kubernetes.io/name: $appName
  template:
    metadata:
      labels:
        app.kubernetes.io/name: $appName
    spec:
      containers:
        - name: $appName
          image: $image
          imagePullPolicy: Always
          ports:
            - containerPort: $containerPort
          resources:
            requests:
              cpu: "$cpu"
              memory: "$memory"
            limits:
              cpu: "$cpu"
              memory: "$memory"
---
apiVersion: v1
kind: Service
metadata:
  name: $serviceName
  namespace: $namespace
spec:
  type: ClusterIP
  selector:
    app.kubernetes.io/name: $appName
  ports:
    - protocol: TCP
      port: 80
      targetPort: $containerPort
---
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: $ingressName
  namespace: $namespace
  annotations:
    alb.ingress.kubernetes.io/scheme: internet-facing
    alb.ingress.kubernetes.io/target-type: ip
    alb.ingress.kubernetes.io/healthcheck-path: $health
    alb.ingress.kubernetes.io/success-codes: "200"
    alb.ingress.kubernetes.io/listen-ports: '[{"HTTPS": 443}]'
    alb.ingress.kubernetes.io/certificate-arn: $certARN
    alb.ingress.kubernetes.io/backend-protocol: HTTP
spec:
  ingressClassName: alb
  rules:
    - host: $aRecordName
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: $serviceName
                port:
                  number: 80
"@ | kubectl apply -f -

showPods

# Get all pod names
$pods = kubectl get pods -n $namespace -o custom-columns=:metadata.name --no-headers

# Iterate and delete each pod
foreach ($pod in $pods) {
    Write-Host "Deleting pod: $pod"
    kubectl delete pod $pod -n $namespace
	kubectl logs $pod -n $namespace

}

showPods

#kubectl rollout restart deployment-fresher-buddy -n fresher-buddy
#kubectl get pod -n fresher-buddy


kubectl get deploy -n kube-system
    $dns = aws elbv2 describe-load-balancers `
  --query "LoadBalancers[?starts_with(LoadBalancerName, 'k8s-')].DNSName" `
  --output text
    Write-Host "🌐 Access the app at: http://$dns" -ForegroundColor Cyan