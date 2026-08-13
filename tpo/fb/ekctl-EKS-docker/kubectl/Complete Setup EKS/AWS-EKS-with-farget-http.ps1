
function Deploy-SampleApp {
	
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

. .\AWS-EKS-common.ps1