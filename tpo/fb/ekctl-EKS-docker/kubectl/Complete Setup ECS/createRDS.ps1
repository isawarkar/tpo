Clear-Host
Write-Host "===== RDS Provisioning (Idempotent) =====" -ForegroundColor Cyan

# -------------------------------
# Configuration
# -------------------------------
$Region               = "us-east-1"
$DBInstanceIdentifier = "fresher-buddy-new"
$DBInstanceClass      = "db.t3.micro"
$Engine               = "mysql"
$MasterUsername       = "root"
$MasterUserPassword   = "teejardni"
$AllocatedStorage     = 20
$AvailabilityZone     = "us-east-1a"
$DBName               = "fb"
$BackupRetentionPeriod = 7

# -------------------------------
# Get Default Security Group
# -------------------------------
Write-Host "Fetching default VPC security group..." -ForegroundColor Cyan

$VpcSecurityGroupId = aws --% ec2 describe-security-groups --filters Name=group-name,Values=default --query SecurityGroups[0].GroupId --output text --region us-east-1

if (-not $VpcSecurityGroupId) {
    Write-Host "❌ Failed to fetch default security group" -ForegroundColor Red
    exit 1
}

# -------------------------------
# Check if RDS exists
# -------------------------------
Write-Host "Checking if RDS instance exists..." -ForegroundColor Cyan

$rdsExists = $true
try {
    aws --% rds describe-db-instances --db-instance-identifier fresher-buddy-new --region us-east-1 | Out-Null
}
catch {
    $rdsExists = $false
}

# -------------------------------
# Create RDS (if needed)
# -------------------------------
if (-not $rdsExists) {

    Write-Host "RDS instance not found. Creating..." -ForegroundColor Green

    aws --% rds create-db-instance --db-instance-identifier fresher-buddy-new --db-instance-class db.t3.micro --engine mysql --master-username root --master-user-password teejardni --allocated-storage 20 --vpc-security-group-ids $VpcSecurityGroupId --availability-zone us-east-1a --db-name fb --backup-retention-period 7 --publicly-accessible --region us-east-1

    Write-Host "Waiting for RDS instance to become available..." -ForegroundColor Yellow

    aws --% rds wait db-instance-available --db-instance-identifier fresher-buddy-new --region us-east-1
}
else {
    Write-Host "RDS instance already exists. Skipping creation." -ForegroundColor Yellow
}

# -------------------------------
# Fetch RDS Endpoint
# -------------------------------
Write-Host "Fetching RDS endpoint..." -ForegroundColor Cyan

$rdsEndpoint = aws --% rds describe-db-instances --db-instance-identifier fresher-buddy-new --query DBInstances[0].Endpoint.Address --output text --region us-east-1

if (-not $rdsEndpoint) {
    Write-Host "❌ Unable to retrieve RDS endpoint" -ForegroundColor Red
    exit 1
}

Write-Host "✅ RDS Endpoint: $rdsEndpoint" -ForegroundColor Green

# -------------------------------
# Optional: MySQL Login
# -------------------------------
Write-Host ""
$connect = Read-Host "Do you want to connect to MySQL now? (YES / NO)"

if ($connect.ToUpper() -eq "YES") {
    mysql -h $rdsEndpoint -u $MasterUsername -p
}

Write-Host ""
Write-Host "===== RDS Setup Completed =====" -ForegroundColor Cyan
