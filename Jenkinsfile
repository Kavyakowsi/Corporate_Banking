pipeline {

```
agent any

options {
    timeout(time: 60, unit: 'MINUTES')
    disableConcurrentBuilds()
}

environment {

    JAVA_HOME = 'C:/Program Files/Java/jdk-17.0.2'
    MAVEN_HOME = 'D:/apache-maven-3.8.5'

    BACKEND_PORT = '8080'
    BACKEND_URL = 'http://localhost:8080/api/transfers'

    APPZ_HOME = 'D:/Tomcat9/apache-tomcat-9.0.53/apache-tomcat-9.0.53'
    TOMCAT_PORT = '8085'
    APPZILLON_URL = 'http://localhost:8085/Corporate_Banking'

    APPZ_ARTIFACTS = 'D:/forDeploy'
    QUIZZ_PROJECT = 'D:/Corporate_Banking/Corporate_Banking'
    QUIZZ_BIN = 'D:/Corporate_Banking/Corporate_Banking/bin'

    DB_NAME = 'corporate_banking'
    DB_USER = 'root'
    DB_PASS = 'root'
    MYSQL_BIN = 'C:/Program Files/MySQL/MySQL Server 8.0/bin'
}

stages {

    stage('Checkout Source') {

        steps {

            echo '=========================================='
            echo 'CHECKING OUT SOURCE CODE'
            echo '=========================================='

            checkout scm

            bat '''
                @echo off

                echo.
                echo Workspace:
                echo %WORKSPACE%

                echo.
                echo Repository files:
                dir /b

                echo.
                echo Source checkout completed.
            '''
        }
    }

    stage('Find Maven Project') {

        steps {

            echo '=========================================='
            echo 'SEARCHING FOR POM.XML'
            echo '=========================================='

            bat '''
                @echo off

                setlocal enabledelayedexpansion

                set "POM_FILE="

                for /f "delims=" %%F in ('dir /s /b "%WORKSPACE%\\pom.xml" 2^>nul') do (

                    if not defined POM_FILE (
                        set "POM_FILE=%%F"
                    )
                )

                if not defined POM_FILE (

                    echo.
                    echo ERROR: pom.xml was not found.
                    echo.
                    echo Workspace:
                    echo %WORKSPACE%

                    echo.
                    echo Searching workspace:

                    dir /s /b "%WORKSPACE%\\*pom.xml" 2>nul

                    exit /b 1
                )

                echo.
                echo ==========================================
                echo POM.XML FOUND
                echo ==========================================

                echo !POM_FILE!

                for %%F in ("!POM_FILE!") do (
                    echo !POM_FILE! > "%WORKSPACE%\\pom_path.txt"
                    echo %%~dpF > "%WORKSPACE%\\maven_project_dir.txt"
                )

                echo.
                echo Maven project directory:

                type "%WORKSPACE%\\maven_project_dir.txt"
            '''
        }
    }

    stage('Build Backend Jar') {

        steps {

            echo '=========================================='
            echo 'BUILDING CORPORATE BANKING BACKEND'
            echo '=========================================='

            bat '''
                @echo off

                setlocal

                set "JAVA_HOME=%JAVA_HOME%"
                set "PATH=%JAVA_HOME%\\bin;%MAVEN_HOME%\\bin;%PATH%"

                echo.
                echo ==========================================
                echo JAVA VERSION
                echo ==========================================

                java -version

                if errorlevel 1 (
                    echo ERROR: Java is not available.
                    exit /b 1
                )

                echo.
                echo ==========================================
                echo MAVEN VERSION
                echo ==========================================

                mvn -version

                if errorlevel 1 (
                    echo ERROR: Maven is not available.
                    exit /b 1
                )

                if not exist "%WORKSPACE%\\maven_project_dir.txt" (
                    echo ERROR: Maven project directory file not found.
                    exit /b 1
                )

                set /p MAVEN_PROJECT_DIR=<"%WORKSPACE%\\maven_project_dir.txt"

                echo.
                echo Maven project directory:
                echo !MAVEN_PROJECT_DIR!

                cd /d "!MAVEN_PROJECT_DIR!"

                echo.
                echo Current directory:
                cd

                echo.
                echo ==========================================
                echo RUNNING MAVEN BUILD
                echo ==========================================

                call mvn clean package -DskipTests

                if errorlevel 1 (

                    echo.
                    echo ==========================================
                    echo MAVEN BUILD FAILED
                    echo ==========================================

                    exit /b 1
                )

                echo.
                echo ==========================================
                echo MAVEN BUILD SUCCESSFUL
                echo ==========================================

                echo.
                echo Target directory:

                dir target
            '''
        }
    }

    stage('Find Generated Backend Jar') {

        steps {

            echo '=========================================='
            echo 'SEARCHING FOR GENERATED BACKEND JAR'
            echo '=========================================='

            bat '''
                @echo off

                setlocal enabledelayedexpansion

                if not exist "%WORKSPACE%\\maven_project_dir.txt" (
                    echo ERROR: Maven project directory file not found.
                    exit /b 1
                )

                set /p MAVEN_PROJECT_DIR=<"%WORKSPACE%\\maven_project_dir.txt"

                cd /d "!MAVEN_PROJECT_DIR!"

                set "APP_JAR="

                for /f "delims=" %%F in ('dir /b /a-d "target\\*.jar" 2^>nul') do (

                    echo Checking JAR: %%F

                    echo %%F | findstr /I /C:"original-" >nul

                    if errorlevel 1 (

                        if not defined APP_JAR (
                            set "APP_JAR=!MAVEN_PROJECT_DIR!\\target\\%%F"
                        )
                    )
                )

                if not defined APP_JAR (

                    echo.
                    echo ==========================================
                    echo ERROR: GENERATED JAR NOT FOUND
                    echo ==========================================

                    echo.
                    echo Target contents:

                    dir target

                    exit /b 1
                )

                echo.
                echo ==========================================
                echo GENERATED JAR FOUND
                echo ==========================================

                echo !APP_JAR!

                echo !APP_JAR! > "%WORKSPACE%\\backend_jar_path.txt"

                echo.
                echo JAR path saved successfully.
            '''
        }
    }

    stage('Verify Backend Jar') {

        steps {

            echo '=========================================='
            echo 'VERIFYING BACKEND JAR'
            echo '=========================================='

            bat '''
                @echo off

                if not exist "%WORKSPACE%\\backend_jar_path.txt" (
                    echo ERROR: backend_jar_path.txt was not created.
                    exit /b 1
                )

                set /p APP_JAR=<"%WORKSPACE%\\backend_jar_path.txt"

                echo Backend JAR:
                echo %APP_JAR%

                if not exist "%APP_JAR%" (
                    echo ERROR: Backend JAR does not exist.
                    exit /b 1
                )

                echo.
                echo ==========================================
                echo BACKEND JAR VERIFIED
                echo ==========================================

                dir "%APP_JAR%"
            '''
        }
    }

    stage('Stop Old Backend') {

        steps {

            echo '=========================================='
            echo 'STOPPING OLD BACKEND'
            echo '=========================================='

            bat '''
                @echo off

                echo Checking port %BACKEND_PORT%...

                for /f "tokens=5" %%a in (
                    'netstat -ano ^| findstr :%BACKEND_PORT% ^| findstr LISTENING'
                ) do (

                    echo Stopping PID %%a

                    taskkill /F /PID %%a >nul 2>&1
                )

                echo.
                echo Waiting for old backend...

                ping 127.0.0.1 -n 4 >nul

                echo Backend process check completed.
            '''
        }
    }

    stage('Deploy Backend') {

        steps {

            echo '=========================================='
            echo 'STARTING BACKEND'
            echo '=========================================='

            bat '''
                @echo off

                setlocal

                set "JAVA_HOME=%JAVA_HOME%"
                set "PATH=%JAVA_HOME%\\bin;%PATH%"
                set "JENKINS_NODE_COOKIE=dontKillMe"

                if not exist "%WORKSPACE%\\backend_jar_path.txt" (
                    echo ERROR: Backend JAR path file not found.
                    exit /b 1
                )

                set /p APP_JAR=<"%WORKSPACE%\\backend_jar_path.txt"

                if not exist "!APP_JAR!" (
                    echo ERROR: Backend JAR does not exist:
                    echo !APP_JAR!
                    exit /b 1
                )

                echo Backend JAR:
                echo !APP_JAR!

                echo.
                echo Starting backend...

                start "CorporateBanking-Backend" /B cmd /c "set JENKINS_NODE_COOKIE=dontKillMe&&set JAVA_HOME=%JAVA_HOME%&&java -jar "!APP_JAR!" > "%WORKSPACE%\\backend.log" 2>&1"

                echo Backend start command executed.

                echo.
                echo Waiting for backend...

                ping 127.0.0.1 -n 10 >nul

                echo.
                echo ==========================================
                echo BACKEND LOG
                echo ==========================================

                if exist "%WORKSPACE%\\backend.log" (

                    powershell -NoProfile -Command "Get-Content '%WORKSPACE%\\backend.log' -Tail 50"

                ) else (

                    echo backend.log was not created.
                )
            '''
        }
    }

    stage('Backend Health Check') {

        steps {

            echo '=========================================='
            echo 'BACKEND HEALTH CHECK'
            echo '=========================================='

            bat '''
                @echo off

                setlocal enabledelayedexpansion

                set "RETRIES=20"

                :CHECK_BACKEND

                echo.
                echo Checking:
                echo %BACKEND_URL%

                curl -s -o nul -w "%%{http_code}" "%BACKEND_URL%" | findstr /R /C:"200" /C:"201" >nul

                if not errorlevel 1 (

                    echo.
                    echo ==========================================
                    echo BACKEND IS RUNNING
                    echo ==========================================

                    exit /b 0
                )

                echo Backend is not ready.

                set /a RETRIES-=1

                if !RETRIES! LEQ 0 (

                    echo.
                    echo ==========================================
                    echo BACKEND HEALTH CHECK FAILED
                    echo ==========================================

                    echo.
                    echo Port status:

                    netstat -ano | findstr :%BACKEND_PORT%

                    echo.
                    echo Backend log:

                    if exist "%WORKSPACE%\\backend.log" (
                        type "%WORKSPACE%\\backend.log"
                    ) else (
                        echo backend.log not found.
                    )

                    exit /b 1
                )

                echo Waiting 3 seconds...

                ping 127.0.0.1 -n 4 >nul

                goto CHECK_BACKEND
            '''
        }
    }

    stage('Find Appzillon Files') {

        steps {

            echo '=========================================='
            echo 'FINDING APPZILLON FILES'
            echo '=========================================='

            powershell '''
                $ErrorActionPreference = "Stop"

                Write-Host "=========================================="
                Write-Host "APPZILLON CONFIGURATION"
                Write-Host "=========================================="

                Write-Host "APPZ_HOME      : $env:APPZ_HOME"
                Write-Host "APPZ_ARTIFACTS : $env:APPZ_ARTIFACTS"
                Write-Host "QUIZZ_PROJECT  : $env:QUIZZ_PROJECT"
                Write-Host "QUIZZ_BIN      : $env:QUIZZ_BIN"

                if (-not (Test-Path $env:APPZ_HOME)) {
                    throw "Tomcat directory not found: $env:APPZ_HOME"
                }

                $catalina = Join-Path $env:APPZ_HOME "bin/catalina.bat"

                if (-not (Test-Path $catalina)) {
                    throw "catalina.bat not found: $catalina"
                }

                Write-Host "Tomcat found successfully."

                $webWar = $null
                $serverWar = $null
                $webProps = $null
                $serverProps = $null
                $dbPath = $null

                $webDirectory = Join-Path $env:QUIZZ_BIN "Web"

                if (Test-Path $webDirectory) {

                    $file = Get-ChildItem `
                        -Path $webDirectory `
                        -Filter "*.war" `
                        -Recurse `
                        -File `
                        -ErrorAction SilentlyContinue |
                        Select-Object -First 1

                    if ($file) {
                        $webWar = $file.FullName
                    }
                }

                $serverDirectory = Join-Path $env:QUIZZ_BIN "Server"

                if (Test-Path $serverDirectory) {

                    $file = Get-ChildItem `
                        -Path $serverDirectory `
                        -Filter "*.war" `
                        -Recurse `
                        -File `
                        -ErrorAction SilentlyContinue |
                        Select-Object -First 1

                    if ($file) {
                        $serverWar = $file.FullName
                    }
                }

                $webPropertiesDirectory = Join-Path $env:QUIZZ_BIN "Web/Properties"

                if (Test-Path $webPropertiesDirectory) {

                    $directory = Get-ChildItem `
                        -Path $webPropertiesDirectory `
                        -Directory `
                        -ErrorAction SilentlyContinue |
                        Select-Object -First 1

                    if ($directory) {
                        $webProps = $directory.FullName
                    }
                }

                $serverPropertiesDirectory = Join-Path $env:QUIZZ_BIN "Server/Properties"

                if (Test-Path $serverPropertiesDirectory) {

                    $directory = Get-ChildItem `
                        -Path $serverPropertiesDirectory `
                        -Directory `
                        -ErrorAction SilentlyContinue |
                        Select-Object -First 1

                    if ($directory) {
                        $serverProps = $directory.FullName
                    }
                }

                $possibleDbPaths = @(
                    (Join-Path $env:QUIZZ_BIN "Server/Database/MySql"),
                    (Join-Path $env:QUIZZ_BIN "Server/Properties/AppzillonServer/quizzz/Database/MySql"),
                    (Join-Path $env:APPZ_ARTIFACTS "lib/AppzillonServer/quizzz/Database/MySql")
                )

                foreach ($path in $possibleDbPaths) {

                    if (Test-Path $path) {
                        $dbPath = $path
                        break
                    }
                }

                if (-not $webWar) {

                    $fallbackWebWar = Join-Path $env:APPZ_ARTIFACTS "quizzz.war"

                    if (Test-Path $fallbackWebWar) {
                        $webWar = $fallbackWebWar
                    }
                }

                if (-not $serverWar) {

                    $fallbackServerWar = Join-Path $env:APPZ_ARTIFACTS "AppzillonServer.war"

                    if (Test-Path $fallbackServerWar) {
                        $serverWar = $fallbackServerWar
                    }
                }

                if (-not $webProps) {

                    $fallbackWebProps = Join-Path $env:APPZ_ARTIFACTS "quizzz"

                    if (Test-Path $fallbackWebProps) {
                        $webProps = $fallbackWebProps
                    }
                }

                if (-not $serverProps) {

                    $fallbackServerProps = Join-Path $env:APPZ_ARTIFACTS "lib/AppzillonServer"

                    if (Test-Path $fallbackServerProps) {
                        $serverProps = $fallbackServerProps
                    }
                }

                Write-Host ""
                Write-Host "=========================================="
                Write-Host "DISCOVERED FILES"
                Write-Host "=========================================="

                Write-Host "Web WAR      : $webWar"
                Write-Host "Server WAR   : $serverWar"
                Write-Host "Web Props    : $webProps"
                Write-Host "Server Props : $serverProps"
                Write-Host "DB Path      : $dbPath"

                if (-not $webWar) {
                    throw "Web WAR was not found."
                }

                $content = @(
                    "WEB_WAR=$webWar"
                    "SERVER_WAR=$serverWar"
                    "WEB_PROPS=$webProps"
                    "SERVER_PROPS=$serverProps"
                    "DB_PATH=$dbPath"
                )

                $variablesFile = Join-Path $env:WORKSPACE "appzillon_vars.txt"

                Set-Content `
                    -Path $variablesFile `
                    -Value $content `
                    -Encoding UTF8

                Write-Host ""
                Write-Host "Appzillon variables saved."
            '''
        }
    }

    stage('Copy Appzillon Properties') {

        steps {

            echo '=========================================='
            echo 'COPYING APPZILLON PROPERTIES'
            echo '=========================================='

            powershell '''
                $ErrorActionPreference = "Stop"

                $variablesFile = Join-Path $env:WORKSPACE "appzillon_vars.txt"

                if (-not (Test-Path $variablesFile)) {
                    throw "Appzillon variables file not found."
                }

                $map = @{}

                foreach ($line in Get-Content $variablesFile) {

                    if ($line -match "^(.*?)=(.*)$") {
                        $map[$matches[1]] = $matches[2]
                    }
                }

                $webProps = $map["WEB_PROPS"]
                $serverProps = $map["SERVER_PROPS"]

                $libPath = Join-Path $env:APPZ_HOME "lib"

                if (-not (Test-Path $libPath)) {

                    New-Item `
                        -ItemType Directory `
                        -Path $libPath `
                        -Force |
                        Out-Null
                }

                if ($webProps -and (Test-Path $webProps)) {

                    Write-Host "Copying Web Properties..."

                    Copy-Item `
                        -Path $webProps `
                        -Destination $libPath `
                        -Recurse `
                        -Force

                    Write-Host "Web properties copied successfully."
                }
                else {

                    Write-Host "WARNING: Web properties were not found."
                }

                if ($serverProps -and (Test-Path $serverProps)) {

                    Write-Host "Copying Server Properties..."

                    Copy-Item `
                        -Path $serverProps `
                        -Destination $libPath `
                        -Recurse `
                        -Force

                    Write-Host "Server properties copied successfully."
                }
                else {

                    Write-Host "WARNING: Server properties were not found."
                }
            '''
        }
    }

    stage('Database Setup') {

        steps {

            echo '=========================================='
            echo 'DATABASE SETUP'
            echo '=========================================='

            bat '''
                @echo off

                setlocal enabledelayedexpansion

                set "MYSQL_EXE=%MYSQL_BIN%\\mysql.exe"

                if not exist "!MYSQL_EXE!" (

                    where mysql >nul 2>&1

                    if errorlevel 1 (

                        echo WARNING: MySQL executable not found.
                        echo Skipping database setup.

                        goto DB_SKIP
                    )

                    for /f "delims=" %%i in ('where mysql') do (

                        set "MYSQL_EXE=%%i"
                        goto MYSQL_FOUND
                    )
                )

                :MYSQL_FOUND

                if not exist "!MYSQL_EXE!" (
                    echo WARNING: MySQL executable unavailable.
                    goto DB_SKIP
                )

                echo MySQL:
                echo !MYSQL_EXE!

                echo.
                echo Creating database:

                "!MYSQL_EXE!" -u%DB_USER% -p%DB_PASS% -e "CREATE DATABASE IF NOT EXISTS %DB_NAME%;"

                if errorlevel 1 (
                    echo WARNING: Database creation failed.
                ) else (
                    echo Database ready.
                )

                set "DB_PATH="

                if exist "%WORKSPACE%\\appzillon_vars.txt" (

                    for /f "tokens=1,* delims==" %%a in (
                        'type "%WORKSPACE%\\appzillon_vars.txt" ^| findstr /B "DB_PATH="'
                    ) do (
                        set "DB_PATH=%%b"
                    )
                )

                echo.
                echo DB_PATH:
                echo !DB_PATH!

                if "!DB_PATH!"=="" (
                    echo No SQL directory found.
                    goto DB_SKIP
                )

                if not exist "!DB_PATH!" (
                    echo SQL directory does not exist.
                    goto DB_SKIP
                )

                dir "!DB_PATH!\\*.sql" >nul 2>&1

                if errorlevel 1 (
                    echo No SQL files found.
                    goto DB_SKIP
                )

                for %%f in ("!DB_PATH!\\*.sql") do (

                    echo.
                    echo ==========================================
                    echo EXECUTING %%~nxf
                    echo ==========================================

                    "!MYSQL_EXE!" -u%DB_USER% -p%DB_PASS% %DB_NAME% < "%%f"

                    if errorlevel 1 (
                        echo WARNING: Failed to execute %%~nxf
                    ) else (
                        echo Successfully executed %%~nxf
                    )
                )

                echo.
                echo Database setup completed.

                :DB_SKIP

                echo Database stage completed.
            '''
        }
    }

    stage('Deploy Appzillon to Tomcat') {

        steps {

            echo '=========================================='
            echo 'DEPLOYING APPZILLON TO TOMCAT'
            echo '=========================================='

            bat '''
                @echo off

                setlocal enabledelayedexpansion

                if not exist "%WORKSPACE%\\appzillon_vars.txt" (
                    echo ERROR: Appzillon variables file not found.
                    exit /b 1
                )

                set "WEB_WAR="
                set "SERVER_WAR="

                for /f "tokens=1,* delims==" %%a in (
                    'type "%WORKSPACE%\\appzillon_vars.txt" ^| findstr /B "WEB_WAR="'
                ) do (
                    set "WEB_WAR=%%b"
                )

                for /f "tokens=1,* delims==" %%a in (
                    'type "%WORKSPACE%\\appzillon_vars.txt" ^| findstr /B "SERVER_WAR="'
                ) do (
                    set "SERVER_WAR=%%b"
                )

                echo WEB WAR:
                echo !WEB_WAR!

                echo.
                echo SERVER WAR:
                echo !SERVER_WAR!

                if "!WEB_WAR!"=="" (
                    echo ERROR: Web WAR not found.
                    exit /b 1
                )

                if not exist "!WEB_WAR!" (
                    echo ERROR: Web WAR does not exist.
                    exit /b 1
                )
```
