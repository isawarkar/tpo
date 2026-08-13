function createACMCertificate {
	$certARN = $(aws acm list-certificates --region us-east-1 --query "CertificateSummaryList[?DomainName=='$domainName'].CertificateArn" --output text)
if ([string]::IsNullOrEmpty($certARN)) {
	Write-Host "Certificate for $domainName is does not exist. Creating "  -ForegroundColor green
$hostedZoneId = aws route53 list-hosted-zones --query "HostedZones[?Name=='$domainName.'].Id" --output text

# SANs - do NOT include the base domain again
$sanList = @(
    "*.fresherbuddy.in",
    "www.fresherbuddy.in",
    "student.fresherbuddy.in",
    "exam.fresherbuddy.in",
    "eureka.fresherbuddy.in"
)

# Validation domains for each SAN (all validated through base domain)
$domainValidationOptions = @(
    "DomainName=$domainName,ValidationDomain=$domainName"
)

foreach ($san in $sanList) {
    $domainValidationOptions += "DomainName=$san,ValidationDomain=$domainName"
}

# IMPORTANT: expand SANs inline properly
$certArn = aws acm request-certificate `
  --domain-name $domainName `
  --validation-method DNS `
  --subject-alternative-names $sanList `
  --domain-validation-options $domainValidationOptions `
  --output text

Start-Sleep -Seconds 60

# Get validation options to create Route53 records
$validationOptions = aws acm describe-certificate --certificate-arn $certArn | ConvertFrom-Json

$records = $validationOptions.Certificate.DomainValidationOptions

$records

foreach ($record in $records) {
    $domain = $record.DomainName
    $r53Name = $record.ResourceRecord.Name
    $r53Type = $record.ResourceRecord.Type
    $r53Value = $record.ResourceRecord.Value

    Write-Host "🔧 Creating Route 53 validation record for: $domain"

    $changeBatch = @{
        Changes = @(
            @{
                Action = "UPSERT"
                ResourceRecordSet = @{
                    Name = $r53Name
                    Type = $r53Type
                    TTL = 300
                    ResourceRecords = @(
                        @{ Value = $r53Value }
                    )
                }
            }
        )
    } | ConvertTo-Json -Depth 10

    aws route53 change-resource-record-sets `
        --hosted-zone-id $hostedZoneId `
        --change-batch $changeBatch | Out-Null
}

Write-Host "`n✅ DNS validation records created. Certificate will be issued once DNS propagates (usually a few minutes)."


  Write-Host "Certificate for $domainName created . ARN : $certARN"  -ForegroundColor green
 } else{
	 
	  Write-Host "Certificate for $domainName is already exist. ARN : $certARN "  -ForegroundColor green
 }

Write-Host "Certificate ARN :$certARN" 

return $certARN
}


function Deploy-SampleApp {
	$certARN = createACMCertificate
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

showPodsSVCIngressDeploy
}

.\AWS-EKS-common.ps1