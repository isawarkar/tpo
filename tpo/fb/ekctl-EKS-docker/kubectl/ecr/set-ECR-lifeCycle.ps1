$repoName = "fb"
$policyJson = @"
{
  "rules": [
    {
      "rulePriority": 1,
      "description": "Expire untagged images older than 7 days",
      "selection": {
        "tagStatus": "untagged",
        "countType": "sinceImagePushed",
        "countUnit": "days",
        "countNumber": 7
      },
      "action": {
        "type": "expire"
      }
    },
    {
      "rulePriority": 2,
      "description": "Keep only last 10 tagged images with any tag",
      "selection": {
        "tagStatus": "tagged",
        "tagPrefixList": [""],
        "countType": "imageCountMoreThan",
        "countNumber": 10
      },
      "action": {
        "type": "expire"
      }
    }
  ]
}

"@

$policyFile = "ecr-policy.json"
$policyJson | Out-File -Encoding ascii $policyFile

aws ecr put-lifecycle-policy --repository-name $repoName --lifecycle-policy-text file://ecr-policy.json

aws ecr get-lifecycle-policy --repository-name $repoName

