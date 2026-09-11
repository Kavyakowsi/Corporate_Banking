
pipeline {

    agent any

    options {
        timeout(time: 30, unit: 'MINUTES')
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

        // IMPORTANT:
        // This is relative to the Git checkout.
        // It will be resolved after checkout.
        PLAYWRIGHT_DIR = '.'
    }

    stages {

        // ============================================================
        // 1. CHECKOUT SOURCE
        // ============================================================

        stage('Checkout Source') {

            steps {

                echo '=========================================='
                echo 'CHECKING OUT CORPORATE BANKING SOURCE'
                echo '=========================================='

                checkout scm

                echo 'Source checkout completed.'

                bat '''
                    @echo off

                    echo.
                    echo ==========================================
                    echo WORKSPACE
                    echo ==========================================

                    echo %WORKSPACE%

                    echo.
                    echo ==========================================
                    echo PROJECT STRUCTURE
                    echo ==========================================

                    dir "%WORKSPACE%"

                    echo.
                    echo ==========================================
                    echo MAVEN PROJECTS
                    echo ==========================================

                    dir /s /b "%WORKSPACE%\\pom.xml"
                '''
            }
        }

        // ============================================================
        // 2. BUILD BACKEND JAR
        // ============================================================

        stage('Build Backend Jar') {

            steps {

                echo '=========================================='
                echo 'BUILDING CORPORATE BANKING BACKEND'
                echo '=========================================='

                bat '''
                    @echo off

                    set "JAVA_HOME=%JAVA_HOME%"
                    set "PATH=%JAVA_HOME%\\bin;%MAVEN_HOME%\\bin;%PATH%"

                    echo.
                    echo ==========================================
                    echo JAVA VERSION
                    echo ==========================================

                    java -version

                    echo.
                    echo ==========================================
                    echo MAVEN VERSION
                    echo ==========================================

                    mvn -version

                    echo.
                    echo ==========================================
                    echo FINDING POM.XML
                    echo ==========================================

                    set "POM_FILE="

                    for /f "delims=" %%F in ('dir /s /b "%WORKSPACE%\\pom.xml" 2^>nul') do (
                        if not defined POM_FILE set "POM_FILE=%%F"
                    )

                    if "%POM_FILE%"=="" (
                        echo ERROR: pom.xml was not found in Jenkins workspace.
                        echo Workspace:
                        echo %WORKSPACE%
                        exit /b 1
                    )

                    echo POM FILE:
                    echo %POM_FILE%

                    for %%F in ("%POM_FILE%") do (
                        set "MAVEN_PROJECT_DIR=%%~dpF"
                    )

                    echo.
                    echo ==========================================
                    echo MAVEN PROJECT DIRECTORY
                    echo ==========================================

                    echo %MAVEN_PROJECT_DIR%

                    cd /d "%MAVEN_PROJECT_DIR%"

                    echo.
                    echo ==========================================
                    echo CURRENT DIRECTORY
                    echo ==========================================

                    cd

                    echo.
                    echo ==========================================
                    echo MAVEN BUILD
                    echo ==========================================

                    mvn clean package -DskipTests

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
                    echo GENERATED JAR FILES
                    echo ==========================================

                    dir /s /b "*.jar"
                '''
            }
        }

        // ============================================================
        // 3. FIND GENERATED JAR
        // ============================================================

        stage('Find Backend Jar') {

            steps {

                echo '=========================================='
                echo 'FINDING GENERATED BACKEND JAR'
                echo '=========================================='

                script {

                    def jarPath = bat(
                        script: '''
                            @echo off
                            for /f "delims=" %%F in ('dir /s /b "%WORKSPACE%\\target\\*.jar" 2^>nul') do (
                                echo %%F
                                exit /b 0
                            )

                            exit /b 1
                        ''',
                        returnStatus: true
                    )

                    if (jarPath != 0) {

                        error(
                            'No JAR file was generated inside the Jenkins workspace.'
                        )
                    }

                    def output = bat(
                        script: '''
                            @echo off
                            for /f "delims=" %%F in ('dir /s /b "%WORKSPACE%\\target\\*.jar" 2^>nul') do (
                                echo %%F
                                exit /b 0
                            )
                        ''',
                        returnStdout: true
                    ).trim()

                    def lines = output.readLines()

                    def generatedJar = lines.find {
                        it.toLowerCase().endsWith('.jar')
                    }

                    if (!generatedJar) {
                        error(
                            'Generated JAR could not be identified.'
                        )
                    }

                    env.APP_JAR = generatedJar

                    echo '=========================================='
                    echo 'BACKEND JAR FOUND'
                    echo '=========================================='
                    echo "APP_JAR = ${env.APP_JAR}"
                }
            }
        }

        // ============================================================
        // 4. STOP OLD BACKEND
        // ============================================================

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

                    echo Waiting...

                    ping 127.0.0.1 -n 4 >nul

                    echo Backend port checked.
                '''
            }
        }

        // ============================================================
        // 5. START BACKEND
        // ============================================================

        stage('Deploy Backend') {

            steps {

                echo '=========================================='
                echo 'STARTING CORPORATE BANKING BACKEND'
                echo '=========================================='

                bat '''
                    @echo off

                    set "JAVA_HOME=%JAVA_HOME%"
                    set "PATH=%JAVA_HOME%\\bin;%PATH%"

                    set "JENKINS_NODE_COOKIE=dontKillMe"

                    echo JAVA_HOME:
                    echo %JAVA_HOME%

                    echo.
                    echo Backend JAR:
                    echo %APP_JAR%

                    if not exist "%APP_JAR%" (
                        echo ERROR: Generated JAR does not exist.
                        exit /b 1
                    )

                    echo.
                    echo Starting backend...

                    start "CorporateBanking-Backend" /B cmd /c "set JENKINS_NODE_COOKIE=dontKillMe && set JAVA_HOME=%JAVA_HOME% && java -jar "%APP_JAR%" > "%WORKSPACE%\\backend.log" 2>&1"

                    echo Backend start command executed.

                    echo.
                    echo Waiting for backend...

                    ping 127.0.0.1 -n 10 >nul

                    echo.
                    echo ==========================================
                    echo BACKEND LOG
                    echo ==========================================

                    if exist "%WORKSPACE%\\backend.log" (
                        powershell -Command "Get-Content '%WORKSPACE%\\backend.log' -Tail 40"
                    ) else (
                        echo backend.log not found.
                    )
                '''
            }
        }

        // ============================================================
        // 6. BACKEND HEALTH CHECK
        // ============================================================

        stage('Backend Health Check') {

            steps {

                echo '=========================================='
                echo 'BACKEND HEALTH CHECK'
                echo '=========================================='

                bat '''
                    @echo off

                    set RETRIES=20

                    :CHECK_BACKEND

                    echo.
                    echo Checking:
                    echo %BACKEND_URL%

                    curl -s -o nul -w "%%{http_code}" "%BACKEND_URL%" | findstr "200 201"

                    if not errorlevel 1 (

                        echo.
                        echo ==========================================
                        echo BACKEND IS RUNNING
                        echo ==========================================

                        exit /b 0
                    )

                    echo Backend not ready.

                    set /a RETRIES-=1

                    if %RETRIES% LEQ 0 (

                        echo.
                        echo ==========================================
                        echo BACKEND FAILED
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
        // 7. FIND APPZILLON FILES
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

                    Write-Host "APPZ_HOME       : $env:APPZ_HOME"
                    Write-Host "QUIZZ_PROJECT   : $env:QUIZZ_PROJECT"
                    Write-Host "QUIZZ_BIN       : $env:QUIZZ_BIN"
                    Write-Host "APPZ_ARTIFACTS  : $env:APPZ_ARTIFACTS"

                    # ------------------------------------------------
                    # CHECK TOMCAT
                    # ------------------------------------------------

                    if (-not (Test-Path $env:APPZ_HOME)) {

                        Write-Host "ERROR: Tomcat directory not found."
                        Write-Host $env:APPZ_HOME

                        exit 1
                    }

                    if (-not (Test-Path "$env:APPZ_HOME/bin/catalina.bat")) {

                        Write-Host "ERROR: catalina.bat not found."

                        exit 1
                    }

                    Write-Host "Tomcat found successfully."

                    $webWar = $null
                    $serverWar = $null
                    $webProps = $null
                    $serverProps = $null
                    $dbPath = $null

                    # ------------------------------------------------
                    # WEB WAR
                    # ------------------------------------------------

                    if (Test-Path "$env:QUIZZ_BIN/Web") {

                        $file = Get-ChildItem `
                            -Path "$env:QUIZZ_BIN/Web" `
                            -Filter "*.war" `
                            -Recurse `
                            -ErrorAction SilentlyContinue |
                            Select-Object -First 1

                        if ($file) {
                            $webWar = $file.FullName
                        }
                    }

                    # ------------------------------------------------
                    # SERVER WAR
                    # ------------------------------------------------

                    if (Test-Path "$env:QUIZZ_BIN/Server") {

                        $file = Get-ChildItem `
                            -Path "$env:QUIZZ_BIN/Server" `
                            -Filter "*.war" `
                            -Recurse `
                            -ErrorAction SilentlyContinue |
                            Select-Object -First 1

                        if ($file) {
                            $serverWar = $file.FullName
                        }
                    }

                    # ------------------------------------------------
                    # WEB PROPERTIES
                    # ------------------------------------------------

                    if (Test-Path "$env:QUIZZ_BIN/Web/Properties") {

                        $directory = Get-ChildItem `
                            -Path "$env:QUIZZ_BIN/Web/Properties" `
                            -Directory `
                            -ErrorAction SilentlyContinue |
                            Select-Object -First 1

                        if ($directory) {
                            $webProps = $directory.FullName
                        }
                    }

                    # ------------------------------------------------
                    # SERVER PROPERTIES
                    # ------------------------------------------------

                    if (Test-Path "$env:QUIZZ_BIN/Server/Properties") {

                        $directory = Get-ChildItem `
                            -Path "$env:QUIZZ_BIN/Server/Properties" `
                            -Directory `

