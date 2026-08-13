Write-Host "🔍 Checking for kubectl..."

$kubectlPath = "$HOME\kubectl.exe"
$kubectlVersionRequired = "1.32.1"

# Function to get kubectl version if present
function Get-KubectlVersion {
    if (Get-Command kubectl -ErrorAction SilentlyContinue) {
        $versionOutput = kubectl version --client --short
        if ($versionOutput -match "v([\d.]+)") {
            return $Matches[1]
        }
    }
    return $null
}

$currentVersion = Get-KubectlVersion
if ($null -eq $currentVersion -or $currentVersion -ne $kubectlVersionRequired) {
    Write-Host "⏬ Installing kubectl v$kubectlVersionRequired..."
    $kubectlUrl = "https://dl.k8s.io/release/v$kubectlVersionRequired/bin/windows/amd64/kubectl.exe"
    Invoke-WebRequest -Uri $kubectlUrl -OutFile $kubectlPath -UseBasicParsing
    $env:PATH += ";$HOME"
    Write-Host "✅ kubectl v$kubectlVersionRequired installed at $kubectlPath"
} else {
    Write-Host "✅ kubectl v$currentVersion is already installed"
}
