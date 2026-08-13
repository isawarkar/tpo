$clusterName = "fb-ecs"
$serviceName = "fb-service"
$newImage = "542115677157.dkr.ecr.us-east-1.amazonaws.com/fb"

# Disable PowerShell paging
$PSDefaultParameterValues['Out-Host:Paging'] = $false
# Disable AWS CLI pager
$env:AWS_PAGER = ""

# 1. Get current task definition ARN used by the service
$taskDefArn = aws ecs describe-services `
    --cluster $clusterName `
    --services $serviceName `
    --query "services[0].taskDefinition" `
    --output text

# 2. Get full task definition JSON
$taskDefJson = aws ecs describe-task-definition `
    --task-definition $taskDefArn `
    --output json

# 3. Convert JSON to PowerShell object
$taskDefObj = $taskDefJson | ConvertFrom-Json

# 4. Update container image
foreach ($container in $taskDefObj.taskDefinition.containerDefinitions) {
    # Replace "containerName" with your actual container name
    if ($container.name -eq "containerName") {
        $container.image = $newImage
    }
}

# 5. Remove fields that can't be passed when registering a new task definition
$fieldsToRemove = @("taskDefinitionArn", "revision", "status", "requiresAttributes", "compatibilities", "registeredAt", "registeredBy")
foreach ($field in $fieldsToRemove) {
    $taskDefObj.taskDefinition.PSObject.Properties.Remove($field)
}

# 6. Convert back to JSON (deep enough for nested objects)
$newTaskDefJson = $taskDefObj.taskDefinition | ConvertTo-Json -Depth 10

# 7. Register the new task definition revision
$newTaskDef = aws ecs register-task-definition --cli-input-json $newTaskDefJson | ConvertFrom-Json

$newTaskDefArn = $newTaskDef.taskDefinition.taskDefinitionArn

# 8. Update ECS service to use the new task definition ARN
aws ecs update-service --cluster $clusterName --service $serviceName --task-definition $newTaskDefArn
