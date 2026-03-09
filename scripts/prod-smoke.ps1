param(
  [string]$EnvFile = ".env",
  [int]$FrontendPort = 80,
  [int]$BackendPort = 8080
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

function Invoke-WithRetry {
  param(
    [scriptblock]$Action,
    [int]$Attempts = 20,
    [int]$DelaySeconds = 2,
    [string]$Name = "operation"
  )

  for ($i = 1; $i -le $Attempts; $i++) {
    try {
      return & $Action
    } catch {
      if ($i -eq $Attempts) {
        throw "$Name failed after $Attempts attempts. Last error: $($_.Exception.Message)"
      }
      Start-Sleep -Seconds $DelaySeconds
    }
  }
}

if (-not (Test-Path $EnvFile)) {
  throw "Env file not found: $EnvFile"
}

$envValues = Read-EnvFile -Path $EnvFile
$adminEmail = $envValues["APP_BOOTSTRAP_ADMIN_EMAIL"]
$adminPassword = $envValues["APP_BOOTSTRAP_ADMIN_PASSWORD"]

if ([string]::IsNullOrWhiteSpace($adminEmail) -or [string]::IsNullOrWhiteSpace($adminPassword)) {
  throw "APP_BOOTSTRAP_ADMIN_EMAIL and APP_BOOTSTRAP_ADMIN_PASSWORD are required for smoke test."
}

$backendHealthUrl = "http://localhost:$BackendPort/actuator/health"
$frontendHealthUrl = "http://localhost:$FrontendPort/healthz"
$loginUrl = "http://localhost:$BackendPort/api/auth/login"
$usersUrl = "http://localhost:$BackendPort/api/users"

Write-Host "Checking backend health: $backendHealthUrl"
$health = Invoke-WithRetry -Name "backend health" -Action {
  Invoke-RestMethod -Method GET -Uri $backendHealthUrl -TimeoutSec 10
}
if ($health.status -ne "UP") {
  throw "Backend health is not UP. Received: $($health.status)"
}

Write-Host "Checking frontend health: $frontendHealthUrl"
$frontendHealth = Invoke-WithRetry -Name "frontend health" -Action {
  Invoke-WebRequest -Method GET -Uri $frontendHealthUrl -TimeoutSec 10
}
if ($frontendHealth.StatusCode -ne 200) {
  throw "Frontend health failed. Status code: $($frontendHealth.StatusCode)"
}

Write-Host "Testing login via API..."
$session = Invoke-WithRetry -Name "auth login" -Action {
  Invoke-RestMethod -Method POST -Uri $loginUrl -ContentType "application/json" -Body (
    @{ email = $adminEmail; password = $adminPassword } | ConvertTo-Json
  )
}

if ([string]::IsNullOrWhiteSpace($session.accessToken)) {
  throw "Login succeeded but accessToken is missing."
}

Write-Host "Testing protected endpoint /api/users..."
$users = Invoke-WithRetry -Name "users endpoint" -Action {
  Invoke-RestMethod -Method GET -Uri $usersUrl -Headers @{ Authorization = "Bearer $($session.accessToken)" }
}

Write-Host "Smoke test OK. Returned users count: $($users.Count)"
