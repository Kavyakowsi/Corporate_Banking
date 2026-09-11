pipeline {

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
                setlocal enabledelayedexpansion

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
                setlocal enabledelayedexpansion

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
