[CmdletBinding()]
param(
    [ValidateRange(0,65535)] [int]$WebPort = 0,
    [ValidateRange(1,65535)] [int]$DatabasePort = 55432,
    [string]$PgBin = 'D:\Program Files\PostgreSQL\18\bin',
    [string]$Java = 'C:\Program Files\Eclipse Adoptium\jdk-21.0.8.9-hotspot\bin\java.exe',
    [switch]$WithoutDemoAccounts
)

$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    $jar = Join-Path $PSScriptRoot 'target/warehouse-postgresql.jar'
    if (!(Test-Path -LiteralPath $jar)) {
        throw 'Build the PostgreSQL JAR first: mvn -Ppostgresql-demo -DskipTests package'
    }

    $localDirectory = Join-Path $PSScriptRoot '.warehouse-local'
    $dataDirectory = Join-Path $localDirectory 'postgresql'
    $passwordFile = Join-Path $localDirectory 'postgres-password.txt'
    $jwtFile = Join-Path $localDirectory 'jwt-key.txt'
    New-Item -ItemType Directory -Path $localDirectory -Force | Out-Null

    foreach ($keyFile in @($passwordFile, $jwtFile)) {
        if (!(Test-Path -LiteralPath $keyFile)) {
            $bytes = New-Object byte[] 32
            $rng = [Security.Cryptography.RandomNumberGenerator]::Create()
            try { $rng.GetBytes($bytes) } finally { $rng.Dispose() }
            [IO.File]::WriteAllText($keyFile, [Convert]::ToBase64String($bytes))
        }
    }

    if (!(Test-Path -LiteralPath (Join-Path $dataDirectory 'PG_VERSION'))) {
        & (Join-Path $PgBin 'initdb.exe') -D $dataDirectory -U warehouse -A scram-sha-256 --pwfile=$passwordFile --encoding=UTF8
        if ($LASTEXITCODE -ne 0) { throw 'Could not initialize the local PostgreSQL cluster.' }
    }

    $pgCtl = Join-Path $PgBin 'pg_ctl.exe'
    & $pgCtl status -D $dataDirectory | Out-Null
    if ($LASTEXITCODE -ne 0) {
        $pgLog = Join-Path $localDirectory 'postgresql.log'
        $pgOptions = "-h 127.0.0.1 -p $DatabasePort"
        $pgLaunchArgs = 'start -D "' + $dataDirectory + '" -l "' + $pgLog + '" -w -t 30 -o "' + $pgOptions + '"'
        $pgLaunch = Start-Process -FilePath $pgCtl -ArgumentList $pgLaunchArgs -WindowStyle Hidden -PassThru
        $pgLaunch.WaitForExit()
        if ($pgLaunch.ExitCode -ne 0) { throw "PostgreSQL did not start. Read $pgLog" }
    }

    $dbPassword = [IO.File]::ReadAllText($passwordFile).Trim()
    $env:PGPASSWORD = $dbPassword
    try {
        $psql = Join-Path $PgBin 'psql.exe'
        $exists = & $psql -X -w -h 127.0.0.1 -p $DatabasePort -U warehouse -d postgres -tAc "SELECT 1 FROM pg_database WHERE datname = 'warehouse'"
        if ($LASTEXITCODE -ne 0) { throw 'Could not connect to the local PostgreSQL cluster.' }
        if ("$exists".Trim() -ne '1') {
            & (Join-Path $PgBin 'createdb.exe') -w -h 127.0.0.1 -p $DatabasePort -U warehouse warehouse
            if ($LASTEXITCODE -ne 0) { throw 'Could not create the warehouse database.' }
        }
        $hasAdmin = $false
        $table = & $psql -X -w -h 127.0.0.1 -p $DatabasePort -U warehouse -d warehouse -tAc "SELECT to_regclass('public.users')"
        if ($LASTEXITCODE -ne 0) { throw 'Could not inspect the warehouse database.' }
        if ("$table".Trim()) {
            $adminRow = & $psql -X -w -h 127.0.0.1 -p $DatabasePort -U warehouse -d warehouse -tAc "SELECT 1 FROM users WHERE username = 'admin'"
            if ($LASTEXITCODE -ne 0) { throw 'Could not inspect the warehouse accounts.' }
            $hasAdmin = "$adminRow".Trim() -eq '1'
        }
    } finally {
        Remove-Item Env:PGPASSWORD -ErrorAction SilentlyContinue
    }

    $env:WAREHOUSE_DEMO = 'false'
    if (!$WithoutDemoAccounts -and !$hasAdmin) {
        if (!$env:WAREHOUSE_ADMIN_PASSWORD) {
            $entered = Read-Host 'Dat mat khau demo (toi thieu 8 ky tu)' -AsSecureString
            $env:WAREHOUSE_ADMIN_PASSWORD = [Net.NetworkCredential]::new('', $entered).Password
        }
        $env:WAREHOUSE_DEMO = 'true'
    } else {
        Remove-Item Env:WAREHOUSE_ADMIN_PASSWORD -ErrorAction SilentlyContinue
    }

    $env:SPRING_PROFILES_ACTIVE = 'postgresql'
    $env:DB_URL = "jdbc:postgresql://127.0.0.1:$DatabasePort/warehouse"
    $env:DB_USER = 'warehouse'
    $env:DB_PASSWORD = $dbPassword
    $env:JWT_SECRET = [IO.File]::ReadAllText($jwtFile).Trim()

    Write-Host "PostgreSQL 127.0.0.1:$DatabasePort/warehouse"
    Write-Host "Data directory: $dataDirectory"
    Write-Host 'Open http://localhost:<port>/ after Tomcat prints its port.'
    & $Java -jar $jar --server.address=127.0.0.1 "--server.port=$WebPort"
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
} finally {
    Pop-Location
}
