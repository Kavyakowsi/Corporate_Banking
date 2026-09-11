pipeline {


agent any

options {
    timeout(time: 60, unit: 'MINUTES')
    disableConcurrentBuilds()
    durabilityHint('PERFORMANCE_OPTIMIZED')
}

environment {

    // ============================================================
    // JAVA
    // ============================================================

    JAVA_HOME = 'C:/Program Files/Java/jdk-17.0.2'

    // ============================================================
    // MAVEN
    // ============================================================

    MAVEN_HOME = 'D:/apache-maven-3.8.5'

    // ============================================================
    // BACKEND
    // ============================================================

    BACKEND_PORT = '8080'
    BACKEND_URL = 'http://localhost:8080/api/transfers'

    // Jenkins will populate these during the build.
    POM_FILE = ''
    MAVEN_PROJECT_DIR = ''
    APP_JAR = ''

    // ============================================================
    // TOMCAT
    // ============================================================

    APPZ_HOME = 'D:/Tomcat9/apache-tomcat-9.0.53/apache-tomcat-9.0.53'
    TOMCAT_PORT = '8085'
    APPZILLON_URL = 'http://localhost:8085/Corporate_Banking'

    // ============================================================
    // APPZILLON
    // ============================================================

    APPZ_ARTIFACTS = 'D:/forDeploy'
    QUIZZ_PROJECT = 'D:/Corporate_Banking/Corporate_Banking'
    QUIZZ_BIN = 'D:/Corporate_Banking/Corporate_Banking/bin'

    // ============================================================
    // DATABASE
    // ============================================================

    DB_NAME = 'corporate_banking'
    DB_USER = 'root'
    DB_PASS = 'root'
    MYSQL_BIN = 'C:/Program Files/MySQL/MySQL Server 8.0/bin'

    // ============================================================
    // PLAYWRIGHT
    // ============================================================

    PLAYWRIGHT_DIR = ''
}

stages {

    // ============================================================
    // 1. CHECKOUT SOURCE
    // ============================================================

    stage('Checkout Source') {

        steps {

            echo '=========================================='
            echo 'CHECKING OUT SOURCE CODE'
            echo '=========================================='

            checkout scm

            bat '''
                @echo off

                echo.
                echo ==========================================
                echo WORKSPACE
                echo ==========================================
                echo %WORKSPACE%

                echo.
                echo ==========================================
                echo PROJECT FILES
                echo ==========================================
                dir /b

                echo.
                echo Source checkout completed successfully.
            '''
        }
    }

    // ============================================================
    // 2. FIND MAVEN PROJECT
    // ============================================================

    stage('Find Maven Project') {

        steps {

            echo '=========================================='
            echo 'SEARCHING FOR POM.XML'
            echo '=========================================='

            script {

                def pomPath = bat(
                    returnStdout: true,
                    script: '''
                        @echo off
                        setlocal enabledelayedexpansion

                        set "FOUND_POM="

                        for /f "delims=" %%F in ('dir /s /b "%WORKSPACE%\\pom.xml" 2^>nul') do (
                            if not defined FOUND_POM (
                                set "FOUND_POM=%%F"
                            )
                        )

                        if not defined FOUND_POM (
                            echo POM_NOT_FOUND
                            exit /b 0
                        )

                        echo !FOUND_POM!
                    '''
                ).trim()

                if (!pomPath || pomPath == 'POM_NOT_FOUND') {
                    error("pom.xml was not found anywhere inside Jenkins workspace: ${env.WORKSPACE}")
                }

                def pomLines = pomPath.readLines()
                def actualPom = pomLines.find { line ->
                    line.trim().toLowerCase().endsWith('pom.xml')
                }

                if (!actualPom) {
                    error("Unable to determine pom.xml path.")
                }

                env.POM_FILE = actualPom.trim()

                def pomFile = new File(env.POM_FILE)
                env.MAVEN_PROJECT_DIR = pomFile.getParent()

                echo "pom.xml found:"
                echo env.POM_FILE

                echo "Maven project directory:"
                echo env.MAVEN_PROJECT_DIR
            }
        }
    }

    // ============================================================
    // 3. BUILD BACKEND JAR
    // ============================================================

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

                echo.
                echo ==========================================
                echo MAVEN PROJECT
                echo ==========================================

                echo POM:
                echo %POM_FILE%

                echo.
                echo PROJECT DIRECTORY:
                echo %MAVEN_PROJECT_DIR%

                if not exist "%POM_FILE%" (
                    echo ERROR: pom.xml does not exist.
                    exit /b 1
                )

                cd /d "%MAVEN_PROJECT_DIR%"

                echo.
                echo Current directory:
                cd

                echo.
                echo ==========================================
                echo MAVEN CLEAN PACKAGE
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
                echo ==========================================
                echo TARGET DIRECTORY
                echo ==========================================

                if exist target (
                    dir target
                ) else (
                    echo ERROR: target directory was not created.
                    exit /b 1
                )
            '''
        }
    }

    // ============================================================
    // 4. FIND GENERATED JAR
    // ============================================================

    stage('Find Generated Backend Jar') {

        steps {

            echo '=========================================='
            echo 'SEARCHING FOR GENERATED JAR'
            echo '=========================================='

            script {

                def jarPath = bat(
                    returnStdout: true,
                    script: '''
                        @echo off
                        setlocal enabledelayedexpansion

                        cd /d "%MAVEN_PROJECT_DIR%"

                        set "FOUND_JAR="

                        for /f "delims=" %%F in ('dir /b /a-d "target\\*.jar" 2^>nul') do (

                            echo %%F

                            echo %%F | findstr /I /V /C:"original-" >nul

                            if not errorlevel 1 (
                                if not defined FOUND_JAR (
                                    set "FOUND_JAR=%MAVEN_PROJECT_DIR%\\target\\%%F"
                                )
                            )
                        )

                        if not defined FOUND_JAR (
                            echo JAR_NOT_FOUND
                            exit /b 0
                        )

                        echo GENERATED_JAR=!FOUND_JAR!
                    '''
                ).trim()

                def generatedJar = jarPath
                    .readLines()
                    .find { line ->
                        line.startsWith('GENERATED_JAR=')
                    }

                if (!generatedJar) {
                    error("Generated backend JAR was not found in ${env.MAVEN_PROJECT_DIR}/target")
                }

                env.APP_JAR = generatedJar
                    .substring('GENERATED_JAR='.length())
                    .trim()

                if (!fileExists(env.APP_JAR)) {
                    error("Generated JAR does not exist: ${env.APP_JAR}")
                }

                echo "Generated backend JAR:"
                echo env.APP_JAR
            }
        }
    }

    // ============================================================
    // 5. VERIFY BACKEND JAR
    // ============================================================

    stage('Verify Backend Jar') {

        steps {

            echo '=========================================='
            echo 'VERIFYING BACKEND JAR'
            echo '=========================================='

            bat '''
                @echo off

                if "%APP_JAR%"=="" (
                    echo ERROR: APP_JAR variable is empty.
                    exit /b 1
                )

                if not exist "%APP_JAR%" (
                    echo ERROR: Backend JAR does not exist.
                    echo Expected:
                    echo %APP_JAR%
                    exit /b 1
                )

                echo.
                echo ==========================================
                echo BACKEND JAR FOUND
                echo ==========================================

                echo %APP_JAR%

                echo.
                echo File details:

                dir "%APP_JAR%"
            '''
        }
    }

    // ============================================================
    // 6. STOP OLD BACKEND
    // ============================================================

    stage('Stop Old Backend') {

        steps {

            echo '=========================================='
            echo 'STOPPING OLD BACKEND'
            echo '=========================================='

            bat '''
                @echo off

                echo Checking backend port:
                echo %BACKEND_PORT%

                for /f "tokens=5" %%a in (
                    'netstat -ano ^| findstr :%BACKEND_PORT% ^| findstr LISTENING'
                ) do (

                    echo Stopping backend PID %%a

                    taskkill /F /PID %%a >nul 2>&1
                )

                echo.
                echo Waiting for backend process to stop...

                ping 127.0.0.1 -n 4 >nul

                echo Backend process check completed.
            '''
        }
    }

    // ============================================================
    // 7. START BACKEND
    // ============================================================

    stage('Deploy Backend') {

        steps {

            echo '=========================================='
            echo 'STARTING CORPORATE BANKING BACKEND'
            echo '=========================================='

            bat '''
                @echo off

                setlocal

                set "JAVA_HOME=%JAVA_HOME%"
                set "PATH=%JAVA_HOME%\\bin;%PATH%"

                set "JENKINS_NODE_COOKIE=dontKillMe"

                echo JAVA_HOME:
                echo %JAVA_HOME%

                echo.
                echo Backend JAR:
                echo %APP_JAR%

                if not exist "%APP_JAR%" (
                    echo ERROR: Backend JAR not found.
                    exit /b 1
                )

                echo.
                echo Starting backend...

                start "CorporateBanking-Backend" /B cmd /c "set JENKINS_NODE_COOKIE=dontKillMe && set JAVA_HOME=%JAVA_HOME% && java -jar "%APP_JAR%" > "%WORKSPACE%\\backend.log" 2>&1"

                echo Backend start command executed.

                echo.
                echo Waiting for backend...

                ping 127.0.0.1 -n 8 >nul

                echo.
                echo ==========================================
                echo BACKEND LOG
                echo ==========================================

                if exist "%WORKSPACE%\\backend.log" (
                    powershell -NoProfile -Command "Get-Content '%WORKSPACE%\\backend.log' -Tail 50"
                ) else (
                    echo backend.log has not been created yet.
                )
            '''
        }
    }

    // ============================================================
    // 8. BACKEND HEALTH CHECK
    // ============================================================

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

                echo Backend is not ready yet.

                set /a RETRIES-=1

                if !RETRIES! LEQ 0 (

                    echo.
                    echo ==========================================
                    echo BACKEND HEALTH CHECK FAILED
                    echo ==========================================

                    echo.
                    echo PORT STATUS:

                    netstat -ano | findstr :%BACKEND_PORT%

                    echo.
                    echo BACKEND LOG:

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

    // ============================================================
    // 9. FIND APPZILLON FILES
    // ============================================================

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

                # ------------------------------------------------
                # VALIDATE TOMCAT
                # ------------------------------------------------

                if (-not (Test-Path $env:APPZ_HOME)) {
                    throw "Tomcat directory not found: $env:APPZ_HOME"
                }

                $catalina = Join-Path $env:APPZ_HOME "bin/catalina.bat"

                if (-not (Test-Path $catalina)) {
                    throw "catalina.bat not found: $catalina"
                }

                Write-Host "Tomcat found successfully."

                # ------------------------------------------------
                # INITIALIZE VARIABLES
                # ------------------------------------------------

                $webWar = $null
                $serverWar = $null
                $webProps = $null
                $serverProps = $null
                $dbPath = $null

                # ------------------------------------------------
                # SEARCH WEB WAR
                # ------------------------------------------------

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

                # ------------------------------------------------
                # SEARCH SERVER WAR
                # ------------------------------------------------

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

                # ------------------------------------------------
                # SEARCH WEB PROPERTIES
                # ------------------------------------------------

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

                # ------------------------------------------------
                # SEARCH SERVER PROPERTIES
                # ------------------------------------------------

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

                # ------------------------------------------------
                # SEARCH DATABASE
                # ------------------------------------------------

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

                # ------------------------------------------------
                # WEB WAR FALLBACK
                # ------------------------------------------------

                if (-not $webWar) {

                    $fallbackWebWar = Join-Path $env:APPZ_ARTIFACTS "quizzz.war"

                    if (Test-Path $fallbackWebWar) {
                        $webWar = $fallbackWebWar
                    }
                }

                # ------------------------------------------------
                # SERVER WAR FALLBACK
                # ------------------------------------------------

                if (-not $serverWar) {

                    $fallbackServerWar = Join-Path $env:APPZ_ARTIFACTS "AppzillonServer.war"

                    if (Test-Path $fallbackServerWar) {
                        $serverWar = $fallbackServerWar
                    }
                }

                # ------------------------------------------------
                # WEB PROPERTIES FALLBACK
                # ------------------------------------------------

                if (-not $webProps) {

                    $fallbackWebProps = Join-Path $env:APPZ_ARTIFACTS "quizzz"

                    if (Test-Path $fallbackWebProps) {
                        $webProps = $fallbackWebProps
                    }
                }

                # ------------------------------------------------
                # SERVER PROPERTIES FALLBACK
                # ------------------------------------------------

                if (-not $serverProps) {

                    $fallbackServerProps = Join-Path $env:APPZ_ARTIFACTS "lib/AppzillonServer"

                    if (Test-Path $fallbackServerProps) {
                        $serverProps = $fallbackServerProps
                    }
                }

                # ------------------------------------------------
                # DISPLAY RESULTS
                # ------------------------------------------------

                Write-Host ""
                Write-Host "=========================================="
                Write-Host "DISCOVERED APPZILLON FILES"
                Write-Host "=========================================="

                Write-Host "Web WAR      : $webWar"
                Write-Host "Server WAR   : $serverWar"
                Write-Host "Web Props    : $webProps"
                Write-Host "Server Props : $serverProps"
                Write-Host "DB Path      : $dbPath"

                # ------------------------------------------------
                # WEB WAR IS REQUIRED
                # ------------------------------------------------

                if (-not $webWar) {
                    throw "Web WAR was not found."
                }

                if (-not (Test-Path $webWar)) {
                    throw "Web WAR does not exist: $webWar"
                }

                # ------------------------------------------------
                # SAVE DISCOVERED VALUES
                # ------------------------------------------------

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
                Write-Host "Appzillon variables saved:"
                Write-Host $variablesFile
            '''
        }
    }

    // ============================================================
    // 10. COPY APPZILLON PROPERTIES
    // ============================================================

    stage('Copy Appzillon Properties') {

        steps {

            echo '==========================================
```
