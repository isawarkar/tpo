function Delete-VPCResources {
    param (
        [string]$vpcId
    )

    if (-not $vpcId) {
        Write-Host "❌ No VPC ID provided." -ForegroundColor Red
        return
    }

    Write-Host "🧹 Starting VPC resource cleanup for VPC: $vpcId..." -ForegroundColor Yellow

    # 1. Detach and delete internet gateways
    $igws = aws ec2 describe-internet-gateways --filters "Name=attachment.vpc-id,Values=$vpcId" --query 'InternetGateways[*].InternetGatewayId' --output text
  	
	foreach ($igw in $igws -split '\s+') {
        aws ec2 detach-internet-gateway --internet-gateway-id $igw --vpc-id $vpcId
        aws ec2 delete-internet-gateway --internet-gateway-id $igw
        Write-Host "✅ Deleted IGW: $igw"
    }

    # 2. Delete NAT gateways
    $natGateways = aws ec2 describe-nat-gateways `
    --filter "Name=vpc-id,Values=$vpcId" `
    --query 'NatGateways[*].NatGatewayId' `
    --output text
	
    foreach ($nat in $natGateways -split '\s+') {
        aws ec2 delete-nat-gateway --nat-gateway-id $nat
        Write-Host "⌛ Waiting for NAT GW $nat to be deleted..."
        aws ec2 wait nat-gateway-deleted --nat-gateway-ids $nat
        Write-Host "✅ NAT GW $nat deleted"
    }

    # 3. Delete VPC endpoints
   
   $endpoints = aws ec2 describe-vpc-endpoints `
    --filters "Name=vpc-id,Values=$($vpcId)" `
    --query 'VpcEndpoints[*].VpcEndpointId' `
    --output text


    if ($endpoints) {
        aws ec2 delete-vpc-endpoints --vpc-endpoint-ids $endpoints
        Write-Host "✅ VPC endpoints deleted: $endpoints"
    }

    # 4. Delete load balancers
    $lbs = aws elbv2 describe-load-balancers --query "LoadBalancers[?VpcId=='$vpcId'].LoadBalancerArn" --output text
    foreach ($lb in $lbs -split '\s+') {
        aws elbv2 delete-load-balancer --load-balancer-arn $lb
        Write-Host "✅ Deleted Load Balancer: $lb"
    }

    # 5. Delete network interfaces
    $enis = aws ec2 describe-network-interfaces --filters Name=vpc-id,Values=$vpcId --query 'NetworkInterfaces[*].NetworkInterfaceId' --output text
    foreach ($eni in $enis -split '\s+') {
        try {
            aws ec2 delete-network-interface --network-interface-id $eni
            Write-Host "✅ Deleted ENI: $eni"
        } catch {
            Write-Host "⚠️ Skipping ENI ${eni}: $_"
        }
    }

    # 6. Delete custom route tables
    $rtbs = aws ec2 describe-route-tables --filters Name=vpc-id,Values=$vpcId --query 'RouteTables[*].RouteTableId' --output text
    foreach ($rtb in $rtbs -split '\s+') {
        # Skip main route table
        $assoc = aws ec2 describe-route-tables --route-table-ids $rtb --query 'RouteTables[0].Associations[*].Main' --output text
        if ($assoc -ne "true") {
            aws ec2 delete-route-table --route-table-id $rtb
            Write-Host "✅ Deleted route table: $rtb"
        }
    }

  
    $sgs = aws ec2 describe-security-groups --filters "Name=vpc-id,Values=$vpcId" --query 'SecurityGroups[*].[GroupId,GroupName]' --output text
    foreach ($sg in $sgs -split "`n") {
        $groupId = ($sg -split '\s+')[0]
        $groupName = ($sg -split '\s+')[1]
        if ($groupName -ne "default") {
            aws ec2 delete-security-group --group-id $groupId
            Write-Host "✅ Deleted security group: $groupId ($groupName)"
        }
    }

    # 8. Delete subnets
    $subnets = aws ec2 describe-subnets --filters "Name=vpc-id,Values=$vpcId" --query 'Subnets[*].SubnetId' --output text
    foreach ($subnet in $subnets -split '\s+') {
        aws ec2 delete-subnet --subnet-id $subnet
        Write-Host "✅ Deleted subnet: $subnet"
    }

    # 9. Finally, delete the VPC
    aws ec2 delete-vpc --vpc-id $vpcId
    Write-Host "🎉 Successfully deleted VPC: $vpcId" -ForegroundColor Green
}
# Replace this with your VPC ID
$vpcId = "vpc-08dba727382a9d5d6"
Delete-VPCResources -vpcId $vpcId