# =========================
# VARIABLES
# =========================
$alias = "fb"
$appName = "$alias-fresher-buddy"
$clusterName = "eks-$appName"
$region = "us-east-1"
$MonitoringNs = "monitoring"
$policyName = "AWSLoadBalancerControllerIAMPolicy"
$prometheusUserName = "indrajeet"
$prometheusPassword = "JeeT@1984"

Write-Host "🚀 Setting up Prometheus + Grafana on EKS" -ForegroundColor Green

# =========================
# STEP 1: Namespace
# =========================
@"
apiVersion: v1
kind: Namespace
metadata:
  name: $MonitoringNs
"@ | kubectl apply -f -

kubectl get ns $MonitoringNs

# =========================
# STEP 2: Helm Repos
# =========================
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts 2>$null
helm repo add eks https://aws.github.io/eks-charts 2>$null
helm repo update

# =========================
# STEP 3: OIDC Provider (SAFE CHECK)
# =========================
$oidcUrl = aws eks describe-cluster `
  --name $clusterName `
  --region $region `
  --query "cluster.identity.oidc.issuer" `
  --output text

if (-not $oidcUrl) {
    Write-Host "❌ Cluster not found or access denied" -ForegroundColor Red
    exit 1
}

$oidcProviderExists = aws iam list-open-id-connect-providers `
  --query "OpenIDConnectProviderList[?contains(Arn, '$($oidcUrl.Split('/')[-1])')].Arn" `
  --output text

if ($oidcProviderExists) {
    Write-Host "✅ OIDC provider already exists"
} else {
    Write-Host "🔐 Creating OIDC provider..."
    eksctl utils associate-iam-oidc-provider `
      --cluster $clusterName `
      --region $region `
      --approve
}

# =========================
# STEP 4: IAM Policy for ALB Controller
# =========================
Invoke-WebRequest `
  -Uri https://raw.githubusercontent.com/kubernetes-sigs/aws-load-balancer-controller/main/docs/install/iam_policy.json `
  -OutFile iam_policy.json

$policyArn = aws iam list-policies `
  --scope Local `
  --query "Policies[?PolicyName=='$policyName'].Arn" `
  --output text

if (-not $policyArn) {
    Write-Host "🔐 Creating IAM policy for ALB controller..."
    $policyArn = aws iam create-policy `
        --policy-name $policyName `
        --policy-document file://iam_policy.json `
        --query Policy.Arn `
        --output text
} else {
    Write-Host "✅ IAM policy already exists"
}

# =========================
# STEP 5: IAM Service Account
# =========================
eksctl create iamserviceaccount `
  --cluster $clusterName `
  --region $region `
  --namespace kube-system `
  --name aws-load-balancer-controller `
  --attach-policy-arn $policyArn `
  --approve `
  --override-existing-serviceaccounts

kubectl get sa aws-load-balancer-controller -n kube-system
kubectl apply -k "github.com/aws/eks-charts/stable/aws-load-balancer-controller//crds?ref=master"
kubectl apply -f https://github.com/kubernetes-sigs/aws-load-balancer-controller/releases/latest/download/aws-load-balancer-controller.yaml


# =========================
# STEP 6: Install ALB Controller
# =========================
helm upgrade --install aws-load-balancer-controller eks/aws-load-balancer-controller `
  -n kube-system `
  --set clusterName=$clusterName `
  --set serviceAccount.create=false `
  --set serviceAccount.name=aws-load-balancer-controller

kubectl get pods -n kube-system | Select-String load-balancer

# =========================
# STEP 7: Grafana Admin Secret (SECURE)
# =========================
kubectl create secret generic grafana-admin-secret `
  -n $MonitoringNs `
  --from-literal=admin-user=$prometheusUserName `
  --from-literal=admin-password=$prometheusPassword `
  --dry-run=client -o yaml | kubectl apply -f -

# =========================
# STEP 8: values.yaml
# =========================
@"
prometheusOperator:
  admissionWebhooks:
    enabled: false

grafana:
  enabled: true

  admin:
    existingSecret: grafana-admin-secret
    userKey: admin-user
    passwordKey: admin-password

  service:
    type: ClusterIP
    port: 80
    targetPort: 3000

  sidecar:
    dashboards:
      enabled: true
      label: grafana_dashboard
    datasources:
      enabled: true

  resources:
    requests:
      cpu: 500m
      memory: 512Mi
    limits:
      cpu: "1"
      memory: 1Gi

prometheus:
  enabled: true

  prometheusSpec:
    maximumStartupDurationSeconds: 300

    resources:
      requests:
        cpu: 750m
        memory: 1Gi
      limits:
        cpu: "1.5"
        memory: 2Gi
"@ | Out-File values.yaml -Encoding utf8

# =========================
# STEP 9: Install Prometheus Stack
# =========================
helm upgrade --install prometheus prometheus-community/kube-prometheus-stack `
  -n $MonitoringNs `
  --create-namespace `
  -f values.yaml

Write-Host "⏳ Waiting for monitoring pods..."
Start-Sleep -Seconds 60
kubectl get pods -n $MonitoringNs

# =========================
# STEP 10: Grafana ALB Ingress
# =========================
@"
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: grafana-alb
  namespace: monitoring
  annotations:
    alb.ingress.kubernetes.io/scheme: internet-facing
    alb.ingress.kubernetes.io/target-type: ip
    alb.ingress.kubernetes.io/listen-ports: '[{"HTTP":80}]'
    alb.ingress.kubernetes.io/healthcheck-path: /login
spec:
  ingressClassName: alb
  rules:
    - http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: prometheus-grafana
                port:
                  number: 80
"@ | kubectl apply -f -


Write-Host "⏳ Waiting for ALB to be provisioned..."
Start-Sleep -Seconds 60

kubectl get ingress -n $MonitoringNs

# =========================
# STEP 11: Grafana Dashboard (Auto-import)
# =========================
@"
apiVersion: v1
kind: ConfigMap
metadata:
  name: grafana-node-dashboard
  namespace: $MonitoringNs
  labels:
    grafana_dashboard: "1"
data:
  node-exporter.json: |
    {
      "id": null,
      "title": "Node Exporter",
      "schemaVersion": 36,
      "version": 1
    }
"@ | kubectl apply -f -

Write-Host "✅ Prometheus & Grafana setup completed!" -ForegroundColor Green
Write-Host "🔑 Login using Grafana admin secret credentials" -ForegroundColor Yellow
kubectl get ingress -n $MonitoringNs