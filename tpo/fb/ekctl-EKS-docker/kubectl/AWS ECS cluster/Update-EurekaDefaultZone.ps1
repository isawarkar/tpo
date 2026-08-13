param(
    [Parameter(Mandatory = $true)]
    [string]$RootFolder,

    [Parameter(Mandatory = $true)]
    [string]$NewEurekaHost,

    [int]$EurekaPort = 8761,

    [switch]$DryRun
)

$resourcesPath = Join-Path $RootFolder "src\main\resources"
if (-not (Test-Path -LiteralPath $resourcesPath)) {
    throw "Resources folder not found: $resourcesPath"
}

$targetUrl = "http://$NewEurekaHost`:$EurekaPort/eureka"

$files = Get-ChildItem -LiteralPath $resourcesPath -Recurse -File |
    Where-Object { $_.Extension -in @(".yml", ".yaml") }

$updated = @()
$skipped = @()

foreach ($f in $files) {
    $lines = Get-Content -LiteralPath $f.FullName
    $changed = $false

    for ($i = 0; $i -lt $lines.Count; $i++) {
        $line = $lines[$i]

        # Match defaultZone pointing to Eureka BUT exclude host.docker.internal
        if (
            $line -match '^\s*defaultZone\s*:\s*.*?/eureka\s*$' -and
            $line -notmatch '(?i)host\.docker\.internal'
        ) {
            # Keep indentation
            $indent = ($line -replace '^(\s*).*$', '$1')

            # Preserve quote style
            if ($line -match 'defaultZone\s*:\s*"') {
                $newLine = "${indent}defaultZone: `"$targetUrl`""
            }
            elseif ($line -match "defaultZone\s*:\s*'") {
                $newLine = "${indent}defaultZone: '$targetUrl'"
            }
            else {
                $newLine = "${indent}defaultZone: $targetUrl"
            }

            if ($newLine -ne $line) {
                $lines[$i] = $newLine
                $changed = $true
            }
        }
    }

    if ($changed) {
        if ($DryRun) {
            Write-Host "[DRYRUN] Would update: $($f.FullName)" -ForegroundColor Cyan
        } else {
            Copy-Item -LiteralPath $f.FullName -Destination ($f.FullName + ".bak") -Force
            Set-Content -LiteralPath $f.FullName -Value $lines -Encoding UTF8
            Write-Host "Updated: $($f.FullName)" -ForegroundColor Green
        }
        $updated += $f.FullName
    } else {
        $skipped += $f.FullName
    }
}

Write-Host ""
Write-Host "========== SUMMARY ==========" -ForegroundColor Magenta
Write-Host ("Updated files : {0}" -f $updated.Count) -ForegroundColor Green
Write-Host ("Skipped files : {0}" -f $skipped.Count) -ForegroundColor Yellow
Write-Host ("Target URL    : {0}" -f $targetUrl) -ForegroundColor Cyan
Write-Host ("Scanned path  : {0}" -f $resourcesPath) -ForegroundColor Cyan
