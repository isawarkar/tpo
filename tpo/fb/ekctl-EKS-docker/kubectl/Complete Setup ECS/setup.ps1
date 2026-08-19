$ErrorActionPreference = "Stop"
$ProgressPreference = "SilentlyContinue"
$env:AWS_PAGER = ""

Add-Type -AssemblyName System.Windows.Forms


# ============================================================
# CONFIGURATION
# ============================================================

$AwsRegion = "us-east-1"


# ============================================================
# DOMAIN / ACM CONFIGURATION
# ============================================================

$RootDomain = "fresherbuddy.in"

$CertificateDomains = @(
    "fresherbuddy.in",
    "exam.fresherbuddy.in",
    "www.fresherbuddy.in",
    "eureka.fresherbuddy.in",
    "*.fresherbuddy.in",
    "student.fresherbuddy.in"
)


# ============================================================
# ROUTE 53
# ============================================================

# Leave empty to automatically find the hosted zone.
#
# Example:
#
# $HostedZoneId = "Z123456789ABCDE"
#
$HostedZoneId = ""


# ============================================================
# ALB / TARGET GROUP
# ============================================================

$HttpsLoadBalancerName = "fresherbuddy-alb"
$HttpsTargetGroupName = "fresherbuddy-tg"

$HttpsListenerPort = 443


# ============================================================
# ACM WAIT CONFIGURATION
# ============================================================

$AcmTimeoutMinutes = 30
$AcmCheckIntervalSeconds = 15


# ============================================================
# ACM CERTIFICATE CONFIGURATION
# ============================================================

# RSA_2048 is a safe choice for ALB.
$AcmKeyAlgorithm = "RSA_2048"

# ACM certificate validation.
$RequireIssuedCertificate = $true


# ============================================================
# GLOBAL ERROR HANDLER
# ============================================================

function Show-ErrorDetails {

    param(
        [Parameter(Mandatory = $true)]
        [System.Management.Automation.ErrorRecord]$ErrorRecord
    )

    Write-Host ""
    Write-Host "============================================================" -ForegroundColor Red
    Write-Host "❌ ERROR OCCURRED" -ForegroundColor Red
    Write-Host "============================================================" -ForegroundColor Red

    $invocation = $ErrorRecord.InvocationInfo

    $scriptName = if ($invocation.ScriptName) {
        $invocation.ScriptName
    }
    else {
        $MyInvocation.MyCommand.Path
    }

    $lineNumber = $invocation.ScriptLineNumber

    Write-Host "Script   : $scriptName" -ForegroundColor Yellow
    Write-Host "Line No. : $lineNumber" -ForegroundColor Yellow
    Write-Host "Message  : $($ErrorRecord.Exception.Message)" -ForegroundColor Yellow

    if ($invocation.Line) {

        Write-Host ""
        Write-Host "Code:" -ForegroundColor Cyan
        Write-Host "  $($invocation.Line.Trim())" -ForegroundColor White
    }

    if ($invocation.PositionMessage) {

        Write-Host ""
        Write-Host "Position:" -ForegroundColor Cyan
        Write-Host $invocation.PositionMessage -ForegroundColor White
    }

    Write-Host ""
    Write-Host "Full Error:" -ForegroundColor Cyan
    Write-Host $ErrorRecord.ToString() -ForegroundColor DarkYellow

    Write-Host "============================================================" -ForegroundColor Red
    Write-Host ""
}


# ============================================================
# RUN POWERSHELL SCRIPT WITH ERROR HANDLING
# ============================================================

function Invoke-SetupScript {

    param(
        [Parameter(Mandatory = $true)]
        [string]$ScriptPath,

        [Parameter(Mandatory = $true)]
        [string]$Description
    )

    if (-not (Test-Path -LiteralPath $ScriptPath)) {

        throw "Required script not found: $ScriptPath"
    }

    Write-Host ""
    Write-Host "------------------------------------------------------------" -ForegroundColor DarkGray
    Write-Host "▶ $Description" -ForegroundColor Cyan
    Write-Host "Script: $ScriptPath" -ForegroundColor DarkCyan
    Write-Host "------------------------------------------------------------" -ForegroundColor DarkGray

    try {

        & $ScriptPath

        if ($LASTEXITCODE -ne 0 -and $null -ne $LASTEXITCODE) {

            throw "$Description failed with exit code $LASTEXITCODE."
        }

        Write-Host ""
        Write-Host "✅ $Description completed successfully." -ForegroundColor Green
    }
    catch {

        throw
    }
}


# ============================================================
# DOCKER CHECK
# ============================================================

function Test-DockerDesktopRunning {

    try {

        docker info *> $null

        if ($LASTEXITCODE -eq 0) {

            return $true
        }
    }
    catch {

        # Continue.
    }

    $dockerProcess = Get-Process `
        -Name "Docker Desktop" `
        -ErrorAction SilentlyContinue

    if ($dockerProcess) {

        return $true
    }

    return $false
}


# ============================================================
# WAIT FOR DOCKER
# ============================================================

function Wait-ForDockerDesktop {

    Write-Host ""
    Write-Host "⏳ Waiting for Docker Desktop to become ready..." `
        -ForegroundColor Yellow

    $timeoutSeconds = 600
    $elapsedSeconds = 0

    while (-not (Test-DockerDesktopRunning)) {

        if ($elapsedSeconds -ge $timeoutSeconds) {

            throw `
                "Docker Desktop did not become ready within $timeoutSeconds seconds."
        }

        Start-Sleep -Seconds 10

        $elapsedSeconds += 10

        Write-Host `
            "   Still waiting... ($elapsedSeconds / $timeoutSeconds seconds)" `
            -ForegroundColor DarkYellow
    }

    Write-Host ""
    Write-Host "✅ Docker Desktop is ready." -ForegroundColor Green
}


# ============================================================
# AWS ACCOUNT
# ============================================================

function Get-AwsAccountId {

    Write-Host ""
    Write-Host "===== AWS ACCOUNT CHECK =====" -ForegroundColor Cyan

    $accountId = aws sts get-caller-identity `
        --query "Account" `
        --output text `
        --region $AwsRegion

    if ($LASTEXITCODE -ne 0) {

        throw `
            "AWS authentication failed. Please check AWS CLI credentials."
    }

    if ([string]::IsNullOrWhiteSpace($accountId)) {

        throw "Unable to determine AWS Account ID."
    }

    $accountId = $accountId.Trim()

    Write-Host "AWS Account ID : $accountId" -ForegroundColor Green
    Write-Host "AWS Region     : $AwsRegion" -ForegroundColor Green

    return $accountId
}


# ============================================================
# ROUTE 53 HOSTED ZONE
# ============================================================

function Get-HostedZoneId {

    Write-Host ""
    Write-Host "===== ROUTE 53 HOSTED ZONE =====" -ForegroundColor Cyan

    if (-not [string]::IsNullOrWhiteSpace($HostedZoneId)) {

        $zoneId = $HostedZoneId.Trim()

        if ($zoneId.StartsWith("/hostedzone/")) {

            $zoneId = $zoneId.Replace("/hostedzone/", "")
        }

        Write-Host `
            "Using configured Hosted Zone: $zoneId" `
            -ForegroundColor Green

        return $zoneId
    }

    Write-Host `
        "Searching for hosted zone: $RootDomain" `
        -ForegroundColor Cyan

    $zoneJson = aws route53 list-hosted-zones-by-name `
        --dns-name "$RootDomain." `
        --output json

    if ($LASTEXITCODE -ne 0) {

        throw "Unable to query Route 53 hosted zones."
    }

    if ([string]::IsNullOrWhiteSpace($zoneJson)) {

        throw "Route 53 returned an empty response."
    }

    $zoneResponse = $zoneJson | ConvertFrom-Json

    if (
        $null -eq $zoneResponse.HostedZones -or
        $zoneResponse.HostedZones.Count -eq 0
    ) {

        throw `
            "Route 53 hosted zone not found for $RootDomain."
    }

    $zone = $zoneResponse.HostedZones |
        Where-Object {
            $_.Name -eq "$RootDomain."
        } |
        Select-Object -First 1

    if ($null -eq $zone) {

        throw `
            "Unable to find exact Route 53 hosted zone for $RootDomain."
    }

    $zoneId = $zone.Id

    if ($zoneId.StartsWith("/hostedzone/")) {

        $zoneId = $zoneId.Replace("/hostedzone/", "")
    }

    Write-Host "Hosted Zone ID: $zoneId" -ForegroundColor Green

    return $zoneId
}


# ============================================================
# GET ALL ACM CERTIFICATES
# ============================================================

function Get-AllAcmCertificates {

    Write-Host ""
    Write-Host "===== ACM CERTIFICATE LIST =====" -ForegroundColor Cyan

    $certificateJson = aws acm list-certificates `
        --certificate-statuses ISSUED `
        --certificate-statuses PENDING_VALIDATION `
        --region $AwsRegion `
        --output json

    if ($LASTEXITCODE -ne 0) {

        throw "Unable to list ACM certificates."
    }

    if ([string]::IsNullOrWhiteSpace($certificateJson)) {

        return @()
    }

    $response = $certificateJson | ConvertFrom-Json

    if ($null -eq $response.CertificateSummaryList) {

        return @()
    }

    return @($response.CertificateSummaryList)
}


# ============================================================
# CHECK CERTIFICATE DOMAINS
# ============================================================

function Test-CertificateDomains {

    param(
        [Parameter(Mandatory = $true)]
        [string]$CertificateArn
    )

    $certificate = Get-AcmCertificateDetails `
        -CertificateArn $CertificateArn

    if ($null -eq $certificate) {

        return $false
    }

    $certificateDomains = @()

    if ($certificate.DomainName) {

        $certificateDomains += $certificate.DomainName
    }

    if ($certificate.SubjectAlternativeNames) {

        $certificateDomains += @(
            $certificate.SubjectAlternativeNames
        )
    }

    $certificateDomains = @(
        $certificateDomains |
        ForEach-Object {
            $_.ToString().Trim().ToLower()
        } |
        Sort-Object -Unique
    )

    foreach ($requiredDomain in $CertificateDomains) {

        $required = $requiredDomain.Trim().ToLower()

        if ($certificateDomains -notcontains $required) {

            return $false
        }
    }

    return $true
}


# ============================================================
# FIND BEST EXISTING ACM CERTIFICATE
# ============================================================

function Get-ExistingAcmCertificate {

    Write-Host ""
    Write-Host "===== ACM CERTIFICATE CHECK =====" -ForegroundColor Cyan

    $certificates = Get-AllAcmCertificates

    if ($certificates.Count -eq 0) {

        Write-Host `
            "No ACM certificates found." `
            -ForegroundColor Yellow

        return $null
    }

    $matchingCertificates = @()

    foreach ($certificateSummary in $certificates) {

        if (
            [string]::IsNullOrWhiteSpace(
                $certificateSummary.CertificateArn
            )
        ) {

            continue
        }

        try {

            $matches = Test-CertificateDomains `
                -CertificateArn $certificateSummary.CertificateArn

            if ($matches) {

                $matchingCertificates += $certificateSummary
            }
        }
        catch {

            Write-Host `
                "⚠️ Unable to inspect certificate $($certificateSummary.CertificateArn)" `
                -ForegroundColor Yellow
        }
    }

    if ($matchingCertificates.Count -eq 0) {

        Write-Host ""
        Write-Host `
            "No certificate containing all required domains was found." `
            -ForegroundColor Yellow

        return $null
    }

    Write-Host ""
    Write-Host "Matching certificates found:" -ForegroundColor Cyan

    foreach ($certificate in $matchingCertificates) {

        Write-Host ""
        Write-Host "Domain : $($certificate.DomainName)"
        Write-Host "Status : $($certificate.Status)"
        Write-Host "ARN    : $($certificate.CertificateArn)"
    }

    # --------------------------------------------------------
    # IMPORTANT
    #
    # ALWAYS prefer ISSUED over PENDING_VALIDATION.
    # --------------------------------------------------------

    $issued = @(
        $matchingCertificates |
        Where-Object {
            $_.Status -eq "ISSUED"
        }
    )

    if ($issued.Count -gt 0) {

        $selected = $issued |
            Select-Object -First 1

        Write-Host ""
        Write-Host `
            "✅ Selecting ISSUED ACM certificate." `
            -ForegroundColor Green

        Write-Host `
            "Selected ARN: $($selected.CertificateArn)" `
            -ForegroundColor Green

        return $selected
    }

    $pending = @(
        $matchingCertificates |
        Where-Object {
            $_.Status -eq "PENDING_VALIDATION"
        }
    )

    if ($pending.Count -gt 0) {

        $selected = $pending |
            Select-Object -First 1

        Write-Host ""
        Write-Host `
            "⏳ Selecting existing PENDING_VALIDATION certificate." `
            -ForegroundColor Yellow

        Write-Host `
            "Selected ARN: $($selected.CertificateArn)" `
            -ForegroundColor Yellow

        return $selected
    }

    return $null
}


# ============================================================
# CREATE ACM CERTIFICATE
# ============================================================

function Request-AcmCertificate {

    Write-Host ""
    Write-Host "===== CREATE ACM CERTIFICATE =====" `
        -ForegroundColor Cyan

    Write-Host ""
    Write-Host "Domains:" -ForegroundColor Cyan

    foreach ($domain in $CertificateDomains) {

        Write-Host "  - $domain"
    }

    $sanDomains = @(
        $CertificateDomains |
        Where-Object {
            $_ -ne $RootDomain
        }
    )

    Write-Host ""
    Write-Host `
        "Requesting ACM certificate with key algorithm: $AcmKeyAlgorithm" `
        -ForegroundColor Cyan

    # --------------------------------------------------------
    # IMPORTANT:
    #
    # --key-algorithm RSA_2048
    #
    # This avoids unsupported key configuration.
    # --------------------------------------------------------

    $certificateArn = aws acm request-certificate `
        --domain-name $RootDomain `
        --subject-alternative-names $sanDomains `
        --validation-method DNS `
        --key-algorithm $AcmKeyAlgorithm `
        --region $AwsRegion `
        --query "CertificateArn" `
        --output text

    if ($LASTEXITCODE -ne 0) {

        throw "Failed to request ACM certificate."
    }

    if ([string]::IsNullOrWhiteSpace($certificateArn)) {

        throw "ACM certificate ARN was empty."
    }

    $certificateArn = $certificateArn.Trim()

    # --------------------------------------------------------
    # Validate ARN format
    # --------------------------------------------------------

    if (
        $certificateArn -notmatch `
        "^arn:aws:acm:$([regex]::Escape($AwsRegion)):\d+:certificate/[a-zA-Z0-9-]+$"
    ) {

        throw `
            "ACM returned an invalid certificate ARN: $certificateArn"
    }

    Write-Host ""
    Write-Host "✅ ACM certificate request created." `
        -ForegroundColor Green

    Write-Host ""
    Write-Host "Certificate ARN:" -ForegroundColor Cyan
    Write-Host $certificateArn

    return $certificateArn
}


# ============================================================
# GET ACM CERTIFICATE DETAILS
# ============================================================

function Get-AcmCertificateDetails {

    param(
        [Parameter(Mandatory = $true)]
        [string]$CertificateArn
    )

    if ([string]::IsNullOrWhiteSpace($CertificateArn)) {

        throw "Certificate ARN is empty."
    }

    $CertificateArn = $CertificateArn.Trim()

    $certificateJson = aws acm describe-certificate `
        --certificate-arn $CertificateArn `
        --region $AwsRegion `
        --output json

    if ($LASTEXITCODE -ne 0) {

        throw `
            "Unable to describe ACM certificate: $CertificateArn"
    }

    if ([string]::IsNullOrWhiteSpace($certificateJson)) {

        throw `
            "ACM returned an empty certificate response."
    }

    $response = $certificateJson | ConvertFrom-Json

    if ($null -eq $response.Certificate) {

        throw `
            "ACM returned no Certificate object."
    }

    return $response.Certificate
}


# ============================================================
# VALIDATE CERTIFICATE BEFORE ALB
# ============================================================

function Validate-AcmCertificateForAlb {

    param(
        [Parameter(Mandatory = $true)]
        [string]$CertificateArn
    )

    Write-Host ""
    Write-Host "===== FINAL ACM / ALB CERTIFICATE CHECK =====" `
        -ForegroundColor Cyan

    if ([string]::IsNullOrWhiteSpace($CertificateArn)) {

        throw "Certificate ARN is empty."
    }

    $CertificateArn = $CertificateArn.Trim()

    Write-Host ""
    Write-Host "Certificate ARN: $CertificateArn"

    # --------------------------------------------------------
    # ARN REGION
    # --------------------------------------------------------

    if (
        $CertificateArn `
        -notmatch `
        "^arn:aws:acm:$([regex]::Escape($AwsRegion)):"
    ) {

        throw `
            "Certificate is not in AWS region '$AwsRegion'. ARN: $CertificateArn"
    }

    $certificate = Get-AcmCertificateDetails `
        -CertificateArn $CertificateArn

    # --------------------------------------------------------
    # STATUS
    # --------------------------------------------------------

    Write-Host "Status         : $($certificate.Status)"

    if ($certificate.Status -ne "ISSUED") {

        throw `
            "Certificate cannot be attached to ALB because ACM status is '$($certificate.Status)'."
    }

    # --------------------------------------------------------
    # DOMAIN
    # --------------------------------------------------------

    Write-Host "Primary Domain : $($certificate.DomainName)"

    if (
        [string]::IsNullOrWhiteSpace(
            $certificate.DomainName
        )
    ) {

        throw `
            "Certificate does not contain a valid fully-qualified domain name."
    }

    # --------------------------------------------------------
    # KEY ALGORITHM
    # --------------------------------------------------------

    Write-Host "Key Algorithm  : $($certificate.KeyAlgorithm)"

    if (
        [string]::IsNullOrWhiteSpace(
            $certificate.KeyAlgorithm
        )
    ) {

        throw `
            "Certificate key algorithm could not be determined."
    }

    $supportedAlgorithms = @(
        "RSA_1024",
        "RSA_2048",
        "RSA_3072",
        "RSA_4096",
        "EC_prime256v1",
        "EC_secp384r1",
        "EC_secp521r1"
    )

    if (
        $supportedAlgorithms `
        -notcontains `
        $certificate.KeyAlgorithm
    ) {

        throw `
            "Certificate uses unsupported key algorithm: $($certificate.KeyAlgorithm)"
    }

    # --------------------------------------------------------
    # SIGNATURE
    # --------------------------------------------------------

    if ($certificate.SignatureAlgorithm) {

        Write-Host `
            "Signature      : $($certificate.SignatureAlgorithm)"
    }

    # --------------------------------------------------------
    # SAN CHECK
    # --------------------------------------------------------

    Write-Host ""
    Write-Host "Certificate domains:" -ForegroundColor Cyan

    $certificateDomains = @()

    if ($certificate.DomainName) {

        $certificateDomains += $certificate.DomainName
    }

    if ($certificate.SubjectAlternativeNames) {

        $certificateDomains += @(
            $certificate.SubjectAlternativeNames
        )
    }

    $certificateDomains = @(
        $certificateDomains |
        ForEach-Object {
            $_.ToString().Trim()
        } |
        Sort-Object -Unique
    )

    foreach ($domain in $certificateDomains) {

        Write-Host "  ✓ $domain" -ForegroundColor Green
    }

    foreach ($requiredDomain in $CertificateDomains) {

        $required = $requiredDomain.Trim().ToLower()

        $found = $certificateDomains |
            Where-Object {
                $_.ToLower() -eq $required
            }

        if (-not $found) {

            throw `
                "Required domain '$requiredDomain' is missing from certificate."
        }
    }

    Write-Host ""
    Write-Host `
        "✅ Certificate passed ALB validation." `
        -ForegroundColor Green

    return $certificate
}


# ============================================================
# CREATE ROUTE 53 ACM VALIDATION RECORDS
# ============================================================

function Create-AcmValidationRecords {

    param(
        [Parameter(Mandatory = $true)]
        [string]$CertificateArn,

        [Parameter(Mandatory = $true)]
        [string]$ZoneId
    )

    Write-Host ""
    Write-Host "===== ACM DNS VALIDATION =====" `
        -ForegroundColor Cyan

    $certificate = Get-AcmCertificateDetails `
        -CertificateArn $CertificateArn

    if ($null -eq $certificate.DomainValidationOptions) {

        throw `
            "ACM did not return DomainValidationOptions."
    }

    $processedRecords = @{}

    foreach ($validation in $certificate.DomainValidationOptions) {

        $domainName = $validation.DomainName

        Write-Host ""
        Write-Host `
            "🔧 Processing validation record for: $domainName" `
            -ForegroundColor Cyan

        if ($null -eq $validation.ResourceRecord) {

            Write-Host `
                "⏳ ACM has not generated ResourceRecord yet." `
                -ForegroundColor Yellow

            continue
        }

        $recordName = $validation.ResourceRecord.Name
        $recordType = $validation.ResourceRecord.Type
        $recordValue = $validation.ResourceRecord.Value

        if (
            [string]::IsNullOrWhiteSpace($recordName) -or
            [string]::IsNullOrWhiteSpace($recordValue)
        ) {

            Write-Host `
                "⚠️ Validation record information incomplete for $domainName." `
                -ForegroundColor Yellow

            continue
        }

        $recordKey = "$recordName|$recordValue"

        if ($processedRecords.ContainsKey($recordKey)) {

            Write-Host `
                "ℹ️ Duplicate validation record. Skipping." `
                -ForegroundColor DarkGray

            continue
        }

        $changeBatch = @{
            Changes = @(
                @{
                    Action = "UPSERT"
                    ResourceRecordSet = @{
                        Name = $recordName
                        Type = $recordType
                        TTL = 300
                        ResourceRecords = @(
                            @{
                                Value = $recordValue
                            }
                        )
                    }
                }
            )
        }

        $changeBatchFile = Join-Path `
            $env:TEMP `
            "acm-validation-$([guid]::NewGuid()).json"

        try {

            $changeBatch |
                ConvertTo-Json -Depth 10 |
                Set-Content `
                    -LiteralPath $changeBatchFile `
                    -Encoding UTF8

            aws route53 change-resource-record-sets `
                --hosted-zone-id $ZoneId `
                --change-batch "file://$changeBatchFile" `
                --output json

            if ($LASTEXITCODE -ne 0) {

                throw `
                    "Failed to create Route 53 validation record for $domainName."
            }

            $processedRecords[$recordKey] = $true

            Write-Host `
                "✅ Validation record created/updated for $domainName." `
                -ForegroundColor Green
        }
        finally {

            if (Test-Path -LiteralPath $changeBatchFile) {

                Remove-Item `
                    -LiteralPath $changeBatchFile `
                    -Force `
                    -ErrorAction SilentlyContinue
            }
        }
    }

    Write-Host ""
    Write-Host `
        "✅ DNS validation records created/updated." `
        -ForegroundColor Green
}


# ============================================================
# SHOW ACM VALIDATION STATUS
# ============================================================

function Show-AcmValidationStatus {

    param(
        [Parameter(Mandatory = $true)]
        [string]$CertificateArn
    )

    $certificate = Get-AcmCertificateDetails `
        -CertificateArn $CertificateArn

    Write-Host ""
    Write-Host `
        "ACM Certificate Status: $($certificate.Status)" `
        -ForegroundColor Yellow

    Write-Host ""
    Write-Host "Domain validation status:" -ForegroundColor Cyan

    foreach ($validation in $certificate.DomainValidationOptions) {

        $status = $validation.ValidationStatus

        if ($status -eq "SUCCESS") {

            $color = "Green"
        }
        elseif ($status -eq "FAILED") {

            $color = "Red"
        }
        else {

            $color = "Yellow"
        }

        Write-Host `
            ("  {0,-40} {1}" -f $validation.DomainName, $status) `
            -ForegroundColor $color

        if ($validation.ResourceRecord) {

            Write-Host `
                "      Record : $($validation.ResourceRecord.Name)" `
                -ForegroundColor DarkGray

            Write-Host `
                "      Type   : $($validation.ResourceRecord.Type)" `
                -ForegroundColor DarkGray

            Write-Host `
                "      Value  : $($validation.ResourceRecord.Value)" `
                -ForegroundColor DarkGray
        }
    }

    return $certificate
}


# ============================================================
# WAIT FOR ACM ISSUED
# ============================================================

function Wait-ForAcmCertificateIssued {

    param(
        [Parameter(Mandatory = $true)]
        [string]$CertificateArn,

        [Parameter(Mandatory = $true)]
        [int]$TimeoutMinutes
    )

    Write-Host ""
    Write-Host "============================================================" `
        -ForegroundColor Cyan

    Write-Host `
        "       WAITING FOR ACM CERTIFICATE VALIDATION" `
        -ForegroundColor Cyan

    Write-Host "============================================================" `
        -ForegroundColor Cyan

    Write-Host ""
    Write-Host "Certificate ARN : $CertificateArn"
    Write-Host "Timeout         : $TimeoutMinutes minutes"
    Write-Host ""

    $timeoutSeconds = $TimeoutMinutes * 60
    $elapsedSeconds = 0

    while ($true) {

        $certificate = Get-AcmCertificateDetails `
            -CertificateArn $CertificateArn

        $status = $certificate.Status

        Write-Host `
            "[$(Get-Date -Format 'HH:mm:ss')] ACM Status: $status" `
            -ForegroundColor Yellow

        # ----------------------------------------------------
        # SUCCESS
        # ----------------------------------------------------

        if ($status -eq "ISSUED") {

            Write-Host ""
            Write-Host `
                "✅ ACM certificate is ISSUED." `
                -ForegroundColor Green

            Write-Host `
                "Certificate is ready for ALB." `
                -ForegroundColor Green

            return $true
        }

        # ----------------------------------------------------
        # FAILED
        # ----------------------------------------------------

        if ($status -eq "FAILED") {

            Write-Host ""

            Show-AcmValidationStatus `
                -CertificateArn $CertificateArn

            throw `
                "ACM certificate validation FAILED."
        }

        # ----------------------------------------------------
        # EXPIRED
        # ----------------------------------------------------

        if ($status -eq "EXPIRED") {

            throw `
                "ACM certificate is EXPIRED."
        }

        # ----------------------------------------------------
        # REVOKED
        # ----------------------------------------------------

        if ($status -eq "REVOKED") {

            throw `
                "ACM certificate is REVOKED."
        }

        # ----------------------------------------------------
        # OUR TIMEOUT
        # ----------------------------------------------------

        if ($elapsedSeconds -ge $timeoutSeconds) {

            Write-Host ""

            Write-Host `
                "❌ ACM certificate did not become ISSUED within $TimeoutMinutes minutes." `
                -ForegroundColor Red

            Show-AcmValidationStatus `
                -CertificateArn $CertificateArn

            throw `
                "ACM validation timeout. Current status: $status"
        }

        Start-Sleep `
            -Seconds $AcmCheckIntervalSeconds

        $elapsedSeconds += $AcmCheckIntervalSeconds
    }
}


# ============================================================
# FIND ALB
# ============================================================

function Get-HttpsLoadBalancer {

    Write-Host ""
    Write-Host "===== FINDING APPLICATION LOAD BALANCER =====" `
        -ForegroundColor Cyan

    if ([string]::IsNullOrWhiteSpace($HttpsLoadBalancerName)) {

        throw "HttpsLoadBalancerName is empty."
    }

    $lbJson = aws elbv2 describe-load-balancers `
        --names $HttpsLoadBalancerName `
        --region $AwsRegion `
        --output json

    if ($LASTEXITCODE -ne 0) {

        throw `
            "Unable to find ALB '$HttpsLoadBalancerName'."
    }

    $response = $lbJson | ConvertFrom-Json

    if (
        $null -eq $response.LoadBalancers -or
        $response.LoadBalancers.Count -eq 0
    ) {

        throw `
            "No ALB found with name '$HttpsLoadBalancerName'."
    }

    $loadBalancer = $response.LoadBalancers[0]

    Write-Host ""
    Write-Host "✅ ALB found." -ForegroundColor Green
    Write-Host "Name : $($loadBalancer.LoadBalancerName)"
    Write-Host "ARN  : $($loadBalancer.LoadBalancerArn)"
    Write-Host "DNS  : $($loadBalancer.DNSName)"

    return $loadBalancer
}


# ============================================================
# FIND TARGET GROUP
# ============================================================

function Get-HttpsTargetGroup {

    Write-Host ""
    Write-Host "===== FINDING TARGET GROUP =====" `
        -ForegroundColor Cyan

    if ([string]::IsNullOrWhiteSpace($HttpsTargetGroupName)) {

        throw "HttpsTargetGroupName is empty."
    }

    $tgJson = aws elbv2 describe-target-groups `
        --names $HttpsTargetGroupName `
        --region $AwsRegion `
        --output json

    if ($LASTEXITCODE -ne 0) {

        throw `
            "Unable to find target group '$HttpsTargetGroupName'."
    }

    $response = $tgJson | ConvertFrom-Json

    if (
        $null -eq $response.TargetGroups -or
        $response.TargetGroups.Count -eq 0
    ) {

        throw `
            "No target group found with name '$HttpsTargetGroupName'."
    }

    $targetGroup = $response.TargetGroups[0]

    Write-Host ""
    Write-Host "✅ Target group found." -ForegroundColor Green
    Write-Host "Name : $($targetGroup.TargetGroupName)"
    Write-Host "ARN  : $($targetGroup.TargetGroupArn)"
    Write-Host "Port : $($targetGroup.Port)"

    return $targetGroup
}


# ============================================================
# FIND EXISTING HTTPS LISTENER
# ============================================================

function Get-ExistingHttpsListener {

    param(
        [Parameter(Mandatory = $true)]
        [string]$LoadBalancerArn
    )

    Write-Host ""
    Write-Host "===== CHECKING HTTPS LISTENER =====" `
        -ForegroundColor Cyan

    $listenerJson = aws elbv2 describe-listeners `
        --load-balancer-arn $LoadBalancerArn `
        --region $AwsRegion `
        --output json

    if ($LASTEXITCODE -ne 0) {

        throw `
            "Unable to describe ALB listeners."
    }

    if ([string]::IsNullOrWhiteSpace($listenerJson)) {

        return $null
    }

    $response = $listenerJson | ConvertFrom-Json

    if (
        $null -eq $response.Listeners -or
        $response.Listeners.Count -eq 0
    ) {

        return $null
    }

    foreach ($listener in $response.Listeners) {

        if (
            $listener.Port -eq $HttpsListenerPort -and
            $listener.Protocol -eq "HTTPS"
        ) {

            return $listener
        }
    }

    return $null
}


# ============================================================
# CREATE HTTPS LISTENER
# ============================================================

function Create-HttpsListener {

    param(
        [Parameter(Mandatory = $true)]
        [string]$LoadBalancerArn,

        [Parameter(Mandatory = $true)]
        [string]$TargetGroupArn,

        [Parameter(Mandatory = $true)]
        [string]$CertificateArn
    )

    Write-Host ""
    Write-Host "===== CREATING HTTPS LISTENER =====" `
        -ForegroundColor Cyan

    # --------------------------------------------------------
    # FINAL SAFETY CHECK
    # --------------------------------------------------------

    $CertificateArn = $CertificateArn.Trim()

    if (
        $CertificateArn `
        -notmatch `
        "^arn:aws:acm:$([regex]::Escape($AwsRegion)):\d+:certificate/[a-zA-Z0-9-]+$"
    ) {

        throw `
            "Invalid certificate ARN supplied to Create-HttpsListener: '$CertificateArn'"
    }

    $certificate = Validate-AcmCertificateForAlb `
        -CertificateArn $CertificateArn

    if ($certificate.Status -ne "ISSUED") {

        throw `
            "Refusing to create HTTPS listener because certificate is not ISSUED."
    }

    Write-Host ""
    Write-Host "Load Balancer : $LoadBalancerArn"
    Write-Host "Target Group  : $TargetGroupArn"
    Write-Host "Certificate   : $CertificateArn"
    Write-Host "Port          : $HttpsListenerPort"
    Write-Host ""

    # --------------------------------------------------------
    # IMPORTANT:
    #
    # Exactly ONE certificate must be supplied as default.
    #
    # Never pass multiple ARNs in this variable.
    # --------------------------------------------------------

    $certificateParameter = "CertificateArn=$CertificateArn"

    $defaultAction = `
        "Type=forward,TargetGroupArn=$TargetGroupArn"

    Write-Host ""
    Write-Host `
        "Creating ALB HTTPS listener..." `
        -ForegroundColor Cyan

    $listenerArn = aws elbv2 create-listener `
        --load-balancer-arn $LoadBalancerArn `
        --protocol HTTPS `
        --port $HttpsListenerPort `
        --certificates $certificateParameter `
        --default-actions $defaultAction `
        --region $AwsRegion `
        --query "Listeners[0].ListenerArn" `
        --output text

    if ($LASTEXITCODE -ne 0) {

        throw `
            "AWS failed to create HTTPS listener."
    }

    if (
        [string]::IsNullOrWhiteSpace($listenerArn) -or
        $listenerArn.Trim() -eq "None"
    ) {

        throw `
            "CreateListener returned an empty Listener ARN."
    }

    $listenerArn = $listenerArn.Trim()

    Write-Host ""
    Write-Host `
        "✅ HTTPS listener created." `
        -ForegroundColor Green

    Write-Host "Listener ARN: $listenerArn"

    return $listenerArn
}


# ============================================================
# UPDATE EXISTING HTTPS LISTENER
# ============================================================

function Update-HttpsListener {

    param(
        [Parameter(Mandatory = $true)]
        [string]$ListenerArn,

        [Parameter(Mandatory = $true)]
        [string]$TargetGroupArn,

        [Parameter(Mandatory = $true)]
        [string]$CertificateArn
    )

    Write-Host ""
    Write-Host "===== UPDATING HTTPS LISTENER =====" `
        -ForegroundColor Cyan

    $CertificateArn = $CertificateArn.Trim()

    # --------------------------------------------------------
    # FINAL SAFETY CHECK
    # --------------------------------------------------------

    Validate-AcmCertificateForAlb `
        -CertificateArn $CertificateArn | Out-Null

    Write-Host ""
    Write-Host "Listener ARN  : $ListenerArn"
    Write-Host "Certificate   : $CertificateArn"
    Write-Host "Target Group  : $TargetGroupArn"
    Write-Host ""

    $certificateParameter = `
        "CertificateArn=$CertificateArn"

    $defaultAction = `
        "Type=forward,TargetGroupArn=$TargetGroupArn"

    aws elbv2 modify-listener `
        --listener-arn $ListenerArn `
        --certificates $certificateParameter `
        --default-actions $defaultAction `
        --region $AwsRegion `
        --output json

    if ($LASTEXITCODE -ne 0) {

        throw `
            "Failed to update HTTPS listener."
    }

    Write-Host ""
    Write-Host `
        "✅ HTTPS listener updated." `
        -ForegroundColor Green

    return $ListenerArn
}


# ============================================================
# VERIFY HTTPS LISTENER
# ============================================================

function Verify-HttpsListener {

    param(
        [Parameter(Mandatory = $true)]
        [string]$ListenerArn,

        [Parameter(Mandatory = $true)]
        [string]$CertificateArn
    )

    Write-Host ""
    Write-Host "===== VERIFYING HTTPS LISTENER =====" `
        -ForegroundColor Cyan

    $CertificateArn = $CertificateArn.Trim()

    $listenerJson = aws elbv2 describe-listeners `
        --listener-arns $ListenerArn `
        --region $AwsRegion `
        --output json

    if ($LASTEXITCODE -ne 0) {

        throw `
            "Unable to verify HTTPS listener."
    }

    if ([string]::IsNullOrWhiteSpace($listenerJson)) {

        throw `
            "Listener verification returned an empty response."
    }

    $response = $listenerJson | ConvertFrom-Json

    # --------------------------------------------------------
    # IMPORTANT FIX
    # --------------------------------------------------------

    if (
        $null -eq $response.Listeners -or
        $response.Listeners.Count -eq 0
    ) {

        throw `
            "Listener verification failed: AWS returned no listeners."
    }

    $listener = $response.Listeners[0]

    if ($listener.Protocol -ne "HTTPS") {

        throw `
            "Listener protocol is '$($listener.Protocol)' instead of HTTPS."
    }

    if ($listener.Port -ne $HttpsListenerPort) {

        throw `
            "Listener port is '$($listener.Port)' instead of $HttpsListenerPort."
    }

    $certificateFound = $false

    if ($null -ne $listener.Certificates) {

        foreach ($certificate in $listener.Certificates) {

            if ($certificate.CertificateArn -eq $CertificateArn) {

                $certificateFound = $true

                break
            }
        }
    }

    if (-not $certificateFound) {

        throw `
            "HTTPS listener exists, but expected ACM certificate is not attached."
    }

    Write-Host ""
    Write-Host `
        "✅ HTTPS listener verification successful." `
        -ForegroundColor Green

    Write-Host ""
    Write-Host "Listener ARN : $($listener.ListenerArn)"
    Write-Host "Protocol     : $($listener.Protocol)"
    Write-Host "Port         : $($listener.Port)"
    Write-Host "SSL Policy   : $($listener.SslPolicy)"

    Write-Host ""
    Write-Host "Attached certificates:" -ForegroundColor Cyan

    foreach ($certificate in $listener.Certificates) {

        Write-Host `
            "  $($certificate.CertificateArn)" `
            -ForegroundColor Green
    }
}


# ============================================================
# COMPLETE HTTPS SETUP
# ============================================================

function Invoke-HttpsSetup {

    Write-Host ""
    Write-Host ""
    Write-Host "============================================================" `
        -ForegroundColor Cyan

    Write-Host `
        "          HTTPS / ACM / ROUTE 53 SETUP" `
        -ForegroundColor Cyan

    Write-Host "============================================================" `
        -ForegroundColor Cyan

    Write-Host ""

    Write-Host "Region       : $AwsRegion"
    Write-Host "Root Domain  : $RootDomain"
    Write-Host "ALB          : $HttpsLoadBalancerName"
    Write-Host "Target Group : $HttpsTargetGroupName"
    Write-Host ""

    # --------------------------------------------------------
    # AWS ACCOUNT
    # --------------------------------------------------------

    $accountId = Get-AwsAccountId

    # --------------------------------------------------------
    # ROUTE 53
    # --------------------------------------------------------

    $zoneId = Get-HostedZoneId

    # --------------------------------------------------------
    # FIND EXISTING CERTIFICATE
    # --------------------------------------------------------

    $existingCertificate = Get-ExistingAcmCertificate

    if ($null -ne $existingCertificate) {

        $certificateArn = `
            $existingCertificate.CertificateArn.Trim()

        $certificateStatus = `
            $existingCertificate.Status

        Write-Host ""
        Write-Host `
            "Using existing matching ACM certificate." `
            -ForegroundColor Green

        Write-Host "Certificate ARN : $certificateArn"
        Write-Host "Certificate Status : $certificateStatus"
    }
    else {

        Write-Host ""
        Write-Host `
            "No suitable ACM certificate found. Creating new certificate..." `
            -ForegroundColor Yellow

        $certificateArn = Request-AcmCertificate

        $certificateStatus = "PENDING_VALIDATION"
    }

    # --------------------------------------------------------
    # MAKE SURE ARN IS ONLY ONE VALUE
    # --------------------------------------------------------

    $certificateArn = $certificateArn.Trim()

    if (
        $certificateArn -match "\s"
    ) {

        throw `
            "Certificate ARN contains whitespace or multiple values: '$certificateArn'"
    }

    Write-Host ""
    Write-Host "Selected Certificate ARN:" -ForegroundColor Cyan
    Write-Host $certificateArn -ForegroundColor Green

    # --------------------------------------------------------
    # ACM VALIDATION
    # --------------------------------------------------------

    Write-Host ""
    Write-Host `
        "Current ACM Status: $certificateStatus" `
        -ForegroundColor Yellow

    if ($certificateStatus -eq "PENDING_VALIDATION") {

        Write-Host ""
        Write-Host `
            "Certificate requires DNS validation." `
            -ForegroundColor Yellow

        # Give ACM a moment to populate validation records.
        Start-Sleep -Seconds 5

        Create-AcmValidationRecords `
            -CertificateArn $certificateArn `
            -ZoneId $zoneId

        Write-Host ""
        Write-Host `
            "⏳ Waiting for ACM certificate to become ISSUED..." `
            -ForegroundColor Yellow

        Wait-ForAcmCertificateIssued `
            -CertificateArn $certificateArn `
            -TimeoutMinutes $AcmTimeoutMinutes
    }
    elseif ($certificateStatus -eq "ISSUED") {

        Write-Host ""
        Write-Host `
            "✅ Existing certificate is already ISSUED." `
            -ForegroundColor Green
    }
    else {

        Show-AcmValidationStatus `
            -CertificateArn $certificateArn

        throw `
            "ACM certificate cannot be used. Current status: $certificateStatus"
    }

    # --------------------------------------------------------
    # FINAL ACM CHECK
    # --------------------------------------------------------

    Write-Host ""
    Write-Host `
        "Performing final ACM validation before touching ALB..." `
        -ForegroundColor Cyan

    $finalCertificate = Validate-AcmCertificateForAlb `
        -CertificateArn $certificateArn

    if ($finalCertificate.Status -ne "ISSUED") {

        throw `
            "Certificate is not ISSUED. Current status: $($finalCertificate.Status)"
    }

    Write-Host ""
    Write-Host "============================================================" `
        -ForegroundColor Green

    Write-Host `
        "       ✅ ACM CERTIFICATE IS READY" `
        -ForegroundColor Green

    Write-Host "============================================================" `
        -ForegroundColor Green

    Write-Host ""
    Write-Host "Certificate ARN: $certificateArn"

    # --------------------------------------------------------
    # ALB
    # --------------------------------------------------------

    $loadBalancer = Get-HttpsLoadBalancer

    $loadBalancerArn = $loadBalancer.LoadBalancerArn

    # --------------------------------------------------------
    # TARGET GROUP
    # --------------------------------------------------------

    $targetGroup = Get-HttpsTargetGroup

    $targetGroupArn = $targetGroup.TargetGroupArn

    # --------------------------------------------------------
    # EXISTING HTTPS LISTENER
    # --------------------------------------------------------

    $existingListener = Get-ExistingHttpsListener `
        -LoadBalancerArn $loadBalancerArn

    if ($null -eq $existingListener) {

        Write-Host ""
        Write-Host `
            "No HTTPS listener found on port $HttpsListenerPort." `
            -ForegroundColor Yellow

        $listenerArn = Create-HttpsListener `
            -LoadBalancerArn $loadBalancerArn `
            -TargetGroupArn $targetGroupArn `
            -CertificateArn $certificateArn
    }
    else {

        Write-Host ""
        Write-Host `
            "Existing HTTPS listener found." `
            -ForegroundColor Green

        Write-Host `
            "Listener ARN: $($existingListener.ListenerArn)"

        $listenerArn = Update-HttpsListener `
            -ListenerArn $existingListener.ListenerArn `
            -TargetGroupArn $targetGroupArn `
            -CertificateArn $certificateArn
    }

    # --------------------------------------------------------
    # VERIFY
    # --------------------------------------------------------

    Verify-HttpsListener `
        -ListenerArn $listenerArn `
        -CertificateArn $certificateArn

    # --------------------------------------------------------
    # HTTPS COMPLETE
    # --------------------------------------------------------

    Write-Host ""
    Write-Host "============================================================" `
        -ForegroundColor Green

    Write-Host `
        "           ✅ HTTPS SETUP COMPLETED" `
        -ForegroundColor Green

    Write-Host "============================================================" `
        -ForegroundColor Green

    Write-Host ""

    Write-Host "AWS Account       : $accountId"
    Write-Host "Region            : $AwsRegion"
    Write-Host "Hosted Zone       : $zoneId"
    Write-Host "Certificate ARN   : $certificateArn"
    Write-Host "Load Balancer ARN : $loadBalancerArn"
    Write-Host "Target Group ARN  : $targetGroupArn"
    Write-Host "HTTPS Listener    : $listenerArn"

    Write-Host ""
    Write-Host "HTTPS URLs:" -ForegroundColor Cyan

    Write-Host "  https://fresherbuddy.in"
    Write-Host "  https://www.fresherbuddy.in"
    Write-Host "  https://exam.fresherbuddy.in"
    Write-Host "  https://student.fresherbuddy.in"
    Write-Host "  https://eureka.fresherbuddy.in"

    Write-Host ""

    Write-Host `
        "✅ HTTPS configuration completed successfully." `
        -ForegroundColor Green
}


# ============================================================
# MAIN SCRIPT
# ============================================================

try {

    Clear-Host

    Write-Host "============================================================" `
        -ForegroundColor Cyan

    Write-Host `
        "              PROJECT SETUP WIZARD" `
        -ForegroundColor Cyan

    Write-Host "============================================================" `
        -ForegroundColor Cyan

    Write-Host ""

    # ========================================================
    # BASE DIRECTORIES
    # ========================================================

    $BaseDir = Split-Path `
        -Parent `
        $MyInvocation.MyCommand.Path

    $RootDir = Split-Path `
        -Parent `
        $BaseDir

    Write-Host "Base Directory : $BaseDir"
    Write-Host "Root Directory : $RootDir"
    Write-Host ""


    # ========================================================
    # DOCKER CHECK
    # ========================================================

    Write-Host ""
    Write-Host "===== DOCKER CHECK =====" -ForegroundColor Cyan

    if (-not (Test-DockerDesktopRunning)) {

        $result = [System.Windows.Forms.MessageBox]::Show(
            "🐳 Docker Desktop is NOT running.`n`nPlease start Docker Desktop and click OK to continue.",
            "Docker Desktop Required",
            [System.Windows.Forms.MessageBoxButtons]::OKCancel,
            [System.Windows.Forms.MessageBoxIcon]::Warning
        )

        if ($result -ne [System.Windows.Forms.DialogResult]::OK) {

            throw `
                "Docker Desktop is not running. User cancelled the setup."
        }

        Wait-ForDockerDesktop
    }
    else {

        Write-Host `
            "✅ Docker Desktop is already running." `
            -ForegroundColor Green
    }


    # ========================================================
    # RDS
    # ========================================================

    Write-Host ""
    Write-Host "===== RDS DATABASE SETUP =====" `
        -ForegroundColor Cyan

    $rdsChoice = Read-Host `
        "Do you want to CREATE RDS Database? (YES / NO)"

    if ($rdsChoice.Trim().ToUpper() -eq "YES") {

        Write-Host `
            "Creating RDS..." `
            -ForegroundColor Green

        $rdsScript = Join-Path `
            $BaseDir `
            "createRDS.ps1"

        Invoke-SetupScript `
            -ScriptPath $rdsScript `
            -Description "RDS Database Creation"
    }
    else {

        Write-Host `
            "Skipping RDS creation." `
            -ForegroundColor Yellow
    }


    # ========================================================
    # EUREKA DOCKER IMAGE
    # ========================================================

    Write-Host ""
    Write-Host "===== EUREKA DOCKER IMAGE =====" `
        -ForegroundColor Cyan

    $eurekaDockerChoice = Read-Host `
        "Do you want to build & push Docker image for 'eureka'? (YES / NO)"

    if ($eurekaDockerChoice.Trim().ToUpper() -eq "YES") {

        $eurekaDockerDir = Join-Path `
            $RootDir `
            "dockerImageCreation\eurekaserver"

        $eurekaDockerScript = Join-Path `
            $eurekaDockerDir `
            "buildAndRunDockerContainerAndPushImage.ps1"

        if (-not (Test-Path -LiteralPath $eurekaDockerScript)) {

            throw `
                "Eureka Docker build script not found: $eurekaDockerScript"
        }

        Push-Location $eurekaDockerDir

        try {

            Invoke-SetupScript `
                -ScriptPath $eurekaDockerScript `
                -Description "Eureka Docker Image Build & Push"
        }
        finally {

            Pop-Location
        }
    }
    else {

        Write-Host `
            "Skipping Eureka Docker image build." `
            -ForegroundColor Yellow
    }


    # ========================================================
    # EUREKA ECS
    # ========================================================

    Write-Host ""
    Write-Host "===== EUREKA ECS SETUP =====" `
        -ForegroundColor Cyan

    $eurekaChoice = Read-Host `
        "Do you want to run Eureka ECS setup? (YES / NO)"

    if ($eurekaChoice.Trim().ToUpper() -eq "YES") {

        $EcsDir = Join-Path `
            $RootDir `
            "AWS ECS cluster"

        $eurekaScript = Join-Path `
            $EcsDir `
            "0 AWS-ECS-Setup-Eureca.ps1"

        Push-Location $EcsDir

        try {

            Invoke-SetupScript `
                -ScriptPath $eurekaScript `
                -Description "Eureka ECS Setup"
        }
        finally {

            Pop-Location
        }
    }
    else {

        Write-Host `
            "Skipping Eureka ECS setup." `
            -ForegroundColor Yellow
    }


    # ========================================================
    # DOCKER IMAGE CREATION
    # ========================================================

    Write-Host ""
    Write-Host "===== DOCKER IMAGE CREATION =====" `
        -ForegroundColor Cyan

    $DockerBaseDir = Join-Path `
        $RootDir `
        "dockerImageCreation"

    $apps = @(
        "fb",
        "exam",
        "student"
    )

    foreach ($app in $apps) {

        Write-Host ""

        $choice = Read-Host `
            "Do you want to build & push Docker image for '$app'? (YES / NO)"

        if ($choice.Trim().ToUpper() -eq "YES") {

            $appPath = Join-Path `
                $DockerBaseDir `
                $app

            $scriptPath = Join-Path `
                $appPath `
                "buildAndRunDockerContainerAndPushImage.ps1"

            if (-not (Test-Path -LiteralPath $scriptPath)) {

                throw `
                    "Docker build script not found for '$app': $scriptPath"
            }

            Push-Location $appPath

            try {

                Invoke-SetupScript `
                    -ScriptPath $scriptPath `
                    -Description "$app Docker Image Build & Push"
            }
            finally {

                Pop-Location
            }
        }
        else {

            Write-Host `
                "Skipping Docker build for $app." `
                -ForegroundColor Yellow
        }
    }


    # ========================================================
    # ECS CLUSTER SETUP
    # ========================================================

    Write-Host ""
    Write-Host "===== AWS ECS CLUSTER SETUP =====" `
        -ForegroundColor Cyan

    $EcsDir = Join-Path `
        $RootDir `
        "AWS ECS cluster"

    if (-not (Test-Path -LiteralPath $EcsDir)) {

        throw `
            "AWS ECS cluster directory not found: $EcsDir"
    }

    Push-Location $EcsDir

    try {

        $ecsScripts = @(
            "1 AWS-ECS-Setup-FresherBuddy - Seprate ALB.ps1",
            "2 AWS-ECS-Setup-Exam.ps1",
            "3 AWS-ECS-Setup-Student.ps1"
        )

        foreach ($ecsScript in $ecsScripts) {

            Write-Host ""

            $choice = Read-Host `
                "Do you want to run '$ecsScript'? (YES / NO)"

            if ($choice.Trim().ToUpper() -eq "YES") {

                $fullScriptPath = Join-Path `
                    $EcsDir `
                    $ecsScript

                Invoke-SetupScript `
                    -ScriptPath $fullScriptPath `
                    -Description "ECS Setup - $ecsScript"
            }
            else {

                Write-Host `
                    "Skipping $ecsScript." `
                    -ForegroundColor Yellow
            }
        }

    }
    finally {

        Pop-Location
    }


    # ========================================================
    # HTTPS / ACM / ROUTE 53 SETUP
    # ========================================================

    Write-Host ""
    Write-Host "===== HTTPS / ACM / ROUTE 53 SETUP =====" `
        -ForegroundColor Cyan

    $httpsChoice = Read-Host `
        "Do you want to configure HTTPS / ACM / Route 53? (YES / NO)"

    if ($httpsChoice.Trim().ToUpper() -eq "YES") {

        Invoke-HttpsSetup
    }
    else {

        Write-Host `
            "Skipping HTTPS / ACM / Route 53 setup." `
            -ForegroundColor Yellow
    }


    # ========================================================
    # S3 BUCKETS
    # ========================================================

    Write-Host ""
    Write-Host "===== S3 BUCKET SETUP =====" `
        -ForegroundColor Cyan

    $s3Choice = Read-Host `
        "Do you want to create S3 buckets? (YES / NO)"

    if ($s3Choice.Trim().ToUpper() -eq "YES") {

        Clear-Host

        $region = $AwsRegion

        Write-Host `
            "Checking AWS credentials..." `
            -ForegroundColor Cyan

        aws sts get-caller-identity `
            --region $region *> $null

        if ($LASTEXITCODE -ne 0) {

            throw `
                "AWS credentials are not configured correctly or AWS CLI authentication failed."
        }

        # ----------------------------------------------------
        # GET ACCOUNT ID
        # ----------------------------------------------------

        $awsAcId = aws sts get-caller-identity `
            --query "Account" `
            --output text `
            --region $region

        if (
            $LASTEXITCODE -ne 0 -or
            [string]::IsNullOrWhiteSpace($awsAcId)
        ) {

            throw `
                "Unable to determine AWS Account ID."
        }

        $awsAcId = $awsAcId.Trim()

        $Buckets = @(
            "$awsAcId-dbbackup-fb",
            "$awsAcId-fresherbuddy-files-server"
        )

        Write-Host ""
        Write-Host `
            "🚀 Starting S3 bucket creation" `
            -ForegroundColor Cyan

        Write-Host "AWS Account : $awsAcId"
        Write-Host "Region      : $region"
        Write-Host ""

        foreach ($BucketName in $Buckets) {

            Write-Host "----------------------------------------"

            Write-Host `
                "🪣 Processing bucket: $BucketName" `
                -ForegroundColor Yellow

            # ------------------------------------------------
            # CHECK BUCKET
            # ------------------------------------------------

            aws s3api head-bucket `
                --bucket $BucketName `
                --region $region *> $null

            if ($LASTEXITCODE -eq 0) {

                Write-Host `
                    "✅ Bucket already exists and is accessible. Skipping." `
                    -ForegroundColor Green

                continue
            }

            # ------------------------------------------------
            # CREATE BUCKET
            # ------------------------------------------------

            Write-Host `
                "🚀 Creating bucket..." `
                -ForegroundColor Cyan

            if ($region -eq "us-east-1") {

                aws s3api create-bucket `
                    --bucket $BucketName `
                    --region $region
            }
            else {

                aws s3api create-bucket `
                    --bucket $BucketName `
                    --region $region `
                    --create-bucket-configuration `
                    "LocationConstraint=$region"
            }

            if ($LASTEXITCODE -ne 0) {

                throw `
                    "Failed to create S3 bucket '$BucketName'. AWS CLI exit code: $LASTEXITCODE"
            }

            Write-Host `
                "🎉 Bucket '$BucketName' created successfully." `
                -ForegroundColor Green
        }

        Write-Host ""
        Write-Host `
            "✅ All S3 buckets processed." `
            -ForegroundColor Cyan
    }
    else {

        Write-Host `
            "Skipping S3 bucket creation." `
            -ForegroundColor Yellow
    }


    # ========================================================
    # FINAL SUCCESS
    # ========================================================

    Write-Host ""
    Write-Host ""

    Write-Host "============================================================" `
        -ForegroundColor Green

    Write-Host `
        "       ✅ SETUP COMPLETED SUCCESSFULLY" `
        -ForegroundColor Green

    Write-Host "============================================================" `
        -ForegroundColor Green

    Write-Host ""
}
catch {

    # ========================================================
    # CENTRAL ERROR HANDLER
    # ========================================================

    Show-ErrorDetails `
        -ErrorRecord $_

    Write-Host ""
    Write-Host `
        "❌ Project setup FAILED." `
        -ForegroundColor Red

    Write-Host ""

    Read-Host "Press ENTER to exit"

    exit 1
}