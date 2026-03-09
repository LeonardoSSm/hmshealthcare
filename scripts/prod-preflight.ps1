param(
  [string]$EnvFile = ".env"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function Read-EnvFile {
  param([string]$Path)
  $map = @{}
  Get-Content $Path | ForEach-Object {
    $line = $_.Trim()
    if ([string]::IsNullOrWhiteSpace($line) -or $line.StartsWith("#")) {
      return
    }
    $parts = $line -split "=", 2
    if ($parts.Length -eq 2) {
      $map[$parts[0].Trim()] = $parts[1].Trim()
    }
  }
  return $map
}

function Require-Value {
  param(
    [hashtable]$Values,
    [string]$Key
  )
  if (-not $Values.ContainsKey($Key)) {
    throw "Missing variable: $Key"
  }
  $value = $Values[$Key]
  if ([string]::IsNullOrWhiteSpace($value)) {
    throw "Empty variable: $Key"
  }
  if ($value -match "change_this|replace_with|your-domain") {
    throw "Variable still has placeholder value: $Key"
  }
}

if (-not (Test-Path $EnvFile)) {
  throw "Env file not found: $EnvFile"
}

Write-Host "Reading env file: $EnvFile"
$envValues = Read-EnvFile -Path $EnvFile

$required = @(
  "MYSQL_DATABASE",
  "MYSQL_USER",
  "MYSQL_PASSWORD",
  "MYSQL_ROOT_PASSWORD",
  "APP_JWT_SECRET",
  "APP_CORS_ALLOWED_ORIGINS",
  "APP_DISABLE_SEEDED_USERS",
  "APP_BOOTSTRAP_ADMIN_ENABLED",
  "APP_BOOTSTRAP_ADMIN_EMAIL",
  "APP_BOOTSTRAP_ADMIN_PASSWORD"
)

$required | ForEach-Object { Require-Value -Values $envValues -Key $_ }

if ($envValues["APP_DISABLE_SEEDED_USERS"].ToLowerInvariant() -ne "true") {
  throw "APP_DISABLE_SEEDED_USERS must be true in production."
}

if ($envValues["APP_BOOTSTRAP_ADMIN_ENABLED"].ToLowerInvariant() -ne "true") {
  throw "APP_BOOTSTRAP_ADMIN_ENABLED must be true in production bootstrap phase."
}

if ($envValues["APP_BOOTSTRAP_ADMIN_PASSWORD"].Length -lt 12) {
  throw "APP_BOOTSTRAP_ADMIN_PASSWORD must have at least 12 characters."
}

Write-Host "Checking Docker availability..."
docker info | Out-Null

Write-Host "Preflight OK."
Write-Host "Next step:"
Write-Host "  docker compose -f docker-compose.prod.yml --env-file $EnvFile up -d --build"
