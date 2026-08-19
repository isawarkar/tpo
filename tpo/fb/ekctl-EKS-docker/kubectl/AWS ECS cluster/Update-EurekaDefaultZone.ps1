param(
    [Parameter(Mandatory = $true)]
    [string]$RootFolder,

    [Parameter(Mandatory = $true)]
    [string]$NewEurekaHost,

    [int]$EurekaPort = 8761,

    [switch]$DryRun
)

if (-not (Test-Path -LiteralPath $RootFolder)) {
    throw "Root folder not found: $RootFolder"
}

$targetUrl = "http://$NewEurekaHost`:$EurekaPort/eureka/"

# Find both files recursively
$files = Get-ChildItem `
    -LiteralPath $RootFolder `
    -Recurse `
    -File |
    Where-Object {
        $_.Name -eq "application.properties" -or
        $_.Name -eq "application-prod.properties"
    }

$updated = @()
$skipped = @()

foreach ($f in $files) {

    $lines = Get-Content -LiteralPath $f.FullName
    $changed = $false

    for ($i = 0; $i -lt $lines.Count; $i++) {

        $line = $lines[$i]

        # ============================================================
        # application-prod.properties
        # Update Eureka defaultZone
        # ============================================================
        if (
            $f.Name -eq "application-prod.properties" -and
            $line -match '^\s*eureka\.client\.service-url\.defaultZone\s*=' -and
            $line -match '/eureka/?\s*$' -and
            $line -notmatch '(?i)host\.docker\.internal'
        ) {

            $indent = ($line -replace '^(\s*).*$', '$1')

            $newLine = "${indent}eureka.client.service-url.defaultZone=$targetUrl"

            if ($newLine -ne $line) {
                $lines[$i] = $newLine
                $changed = $true

                Write-Host "  Eureka defaultZone -> $targetUrl" -ForegroundColor Cyan
            }
        }

        # ============================================================
        # application.properties
        # Update active Spring profile
        # ============================================================
        if (
            $f.Name -eq "application.properties" -and
            $line -match '^\s*spring\.profiles\.active\s*='
        ) {

            $indent = ($line -replace '^(\s*).*$', '$1')

            $newLine = "${indent}spring.profiles.active=prod"

            if ($newLine -ne $line) {
                $lines[$i] = $newLine
                $changed = $true

                Write-Host "  Spring profile -> prod" -ForegroundColor Cyan
            }
        }
    }

    if ($changed) {

        if ($DryRun) {

            Write-Host "[DRYRUN] Would update: $($f.FullName)" -ForegroundColor Yellow

        }
        else {

            # Backup original file
            Copy-Item `
                -LiteralPath $f.FullName `
                -Destination ($f.FullName + ".bak") `
                -Force

            # Write updated content
            Set-Content `
                -LiteralPath $f.FullName `
                -Value $lines `
                -Encoding UTF8

            Write-Host "Updated: $($f.FullName)" -ForegroundColor Green
        }

        $updated += $f.FullName
    }
    else {

        Write-Host "[SKIP] No changes required: $($f.FullName)" -ForegroundColor Gray

        $skipped += $f.FullName
    }
}

Write-Host ""
Write-Host "========== SUMMARY ==========" -ForegroundColor Magenta
Write-Host ("Found files   : {0}" -f $files.Count) -ForegroundColor White
Write-Host ("Updated files : {0}" -f $updated.Count) -ForegroundColor Green
Write-Host ("Skipped files : {0}" -f $skipped.Count) -ForegroundColor Yellow
Write-Host ("Eureka URL    : {0}" -f $targetUrl) -ForegroundColor Cyan
Write-Host ("Spring Profile: prod") -ForegroundColor Cyan
Write-Host ("Root path     : {0}" -f $RootFolder) -ForegroundColor Cyan