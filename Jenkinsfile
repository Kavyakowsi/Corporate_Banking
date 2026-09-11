@echo off

setlocal EnableExtensions EnableDelayedExpansion
 
title Appzillon RBC Deployment
 
echo ==================================================

echo       APPZILLON RBC DEPLOYMENT STARTED

echo ==================================================
 
REM ==================================================

REM CONFIGURATION

REM CHANGE ONLY VALUES IN THIS SECTION

REM ==================================================
 
REM --- Appzillon Project Build Folder ---

set "PROJECT_BIN=D:\Corporate_Banking\Corporate_Banking\bin"
 
REM --- Tomcat Installation ---

set "TOMCAT_HOME=D:\Tomcat9\apache-tomcat-9.0.53\apache-tomcat-9.0.53"
 
REM --- MySQL Configuration ---

set "MYSQL_HOME=C:\Program Files\MySQL\MySQL Server 8.0\bin"

set "MYSQL_USER=root"

set "MYSQL_PASSWORD=root"

set "MYSQL_DATABASE=corporate_banking"
 
REM --- Application URL ---

set "APP_URL=http://localhost:8085/Corporate_Banking"
 
REM --- Deployment Window Timeout ---

REM 3 HOURS = 3 * 60 * 60 = 10800 SECONDS

set "DEPLOYMENT_TIMEOUT=10800"
 
REM ==================================================

REM DERIVED PATHS - DON'T CHANGE

REM ==================================================
 
set "WEB=%PROJECT_BIN%\Web"

set "SERVER=%PROJECT_BIN%\Server"
 
set "WEB_PROPERTIES=%WEB%\Properties"

set "SERVER_PROPERTIES=%SERVER%\Properties"

set "DATABASE=%SERVER%\Database\MySql"
 
set "TOMCAT_WEBAPPS=%TOMCAT_HOME%\webapps"

set "TOMCAT_LIB=%TOMCAT_HOME%\lib"

set "TOMCAT_BIN=%TOMCAT_HOME%\bin"
 
set "MYSQL=%MYSQL_HOME%\mysql.exe"
 
REM ==================================================

REM VALIDATE CONFIGURATION

REM ==================================================
 
echo.

echo ==================================================

echo Checking configuration...

echo ==================================================
 
if not exist "%PROJECT_BIN%" (

    echo.

    echo ERROR: Project build folder not found:

    echo %PROJECT_BIN%

    echo.

    pause

    exit /b 1

)
 
if not exist "%TOMCAT_HOME%" (

    echo.

    echo ERROR: Tomcat folder not found:

    echo %TOMCAT_HOME%

    echo.

    pause

    exit /b 1

)
 
if not exist "%MYSQL%" (

    echo.

    echo ERROR: MySQL executable not found:

    echo %MYSQL%

    echo.

    pause

    exit /b 1

)
 
if not exist "%TOMCAT_WEBAPPS%" (

    echo.

    echo ERROR: Tomcat webapps folder not found:

    echo %TOMCAT_WEBAPPS%

    echo.

    pause

    exit /b 1

)
 
echo.

echo Configuration OK.
 
REM ==================================================

REM 1. SHUTDOWN TOMCAT

REM ==================================================
 
echo.

echo ==================================================

echo [1/7] Stopping Tomcat...

echo ==================================================
 
call "%TOMCAT_BIN%\shutdown.bat"
 
echo.

echo Waiting for Tomcat to stop...

timeout /t 10 /nobreak >nul
 
echo Tomcat shutdown command completed.
 
REM ==================================================

REM 2. COPY WEB WAR

REM ==================================================
 
echo.

echo ==================================================

echo [2/7] Copying Web WAR...

echo ==================================================
 
set "WEB_WAR_FOUND=0"
 
for %%F in ("%WEB%\*.war") do (

    if exist "%%~fF" (

        set "WEB_WAR_FOUND=1"
 
        echo.

        echo Copying Web WAR:

        echo %%~nxF
 
        copy /Y "%%~fF" "%TOMCAT_WEBAPPS%\" >nul
 
        if errorlevel 1 (

            echo.

            echo ERROR: Failed to copy Web WAR:

            echo %%~nxF

            echo.

            pause

            exit /b 1

        )
 
        echo Web WAR copied successfully.

    )

)
 
if "%WEB_WAR_FOUND%"=="0" (

    echo.

    echo WARNING: No Web WAR found in:

    echo %WEB%

)
 
echo.

echo Web WAR copy completed.
 
REM ==================================================

REM 3. COPY WEB PROPERTIES

REM ==================================================
 
echo.

echo ==================================================

echo [3/7] Copying Web Properties...

echo ==================================================
 
if exist "%WEB_PROPERTIES%" (
 
    for /D %%D in ("%WEB_PROPERTIES%\*") do (
 
        if exist "%%~fD" (
 
            echo.

            echo Copying Web Properties folder:

            echo %%~nxD
 
            xcopy "%%~fD" "%TOMCAT_LIB%\%%~nxD\" /E /I /Y >nul
 
            if errorlevel 1 (

                echo.

                echo ERROR: Failed to copy Web Properties:

                echo %%~nxD

                echo.

                pause

                exit /b 1

            )
 
            echo Web Properties copied successfully.

        )

    )
 
) else (
 
    echo.

    echo WARNING: Web Properties folder not found:

    echo %WEB_PROPERTIES%
 
)
 
echo.

echo Web Properties copy completed.
 
REM ==================================================

REM 4. COPY SERVER WAR

REM ==================================================
 
echo.

echo ==================================================

echo [4/7] Copying Server WAR...

echo ==================================================
 
set "SERVER_WAR_FOUND=0"
 
for %%F in ("%SERVER%\*.war") do (
 
    if exist "%%~fF" (
 
        set "SERVER_WAR_FOUND=1"
 
        echo.

        echo Copying Server WAR:

        echo %%~nxF
 
        copy /Y "%%~fF" "%TOMCAT_WEBAPPS%\" >nul
 
        if errorlevel 1 (

            echo.

            echo ERROR: Failed to copy Server WAR:

            echo %%~nxF

            echo.

            pause

            exit /b 1

        )
 
        echo Server WAR copied successfully.

    )

)
 
if "%SERVER_WAR_FOUND%"=="0" (

    echo.

    echo WARNING: No Server WAR found in:

    echo %SERVER%

)
 
echo.

echo Server WAR copy completed.
 
REM ==================================================

REM 5. COPY APPZILLON SERVER PROPERTIES

REM ==================================================
 
echo.

echo ==================================================

echo [5/7] Copying AppzillonServer properties...

echo ==================================================
 
set "APPZILLON_PROPERTIES=%SERVER_PROPERTIES%\AppzillonServer"
 
if exist "%APPZILLON_PROPERTIES%" (
 
    for %%F in ("%APPZILLON_PROPERTIES%\*") do (
 
        if exist "%%~fF" (
 
            echo.

            echo Copying:

            echo %%~nxF
 
            copy /Y "%%~fF" "%TOMCAT_LIB%\" >nul
 
            if errorlevel 1 (

                echo.

                echo ERROR: Failed to copy:

                echo %%~nxF

                echo.

                pause

                exit /b 1

            )
 
            echo File copied successfully.

        )

    )
 
) else (
 
    echo.

    echo WARNING: AppzillonServer properties folder not found:

    echo %APPZILLON_PROPERTIES%
 
)
 
echo.

echo AppzillonServer properties copy completed.
 
REM ==================================================

REM 6. RUN MYSQL DATABASE SCRIPTS

REM ==================================================
 
echo.

echo ==================================================

echo [6/7] Running MySQL database scripts...

echo ==================================================
 
if not exist "%MYSQL%" (

    echo.

    echo ERROR: MySQL executable not found:

    echo %MYSQL%

    echo.

    pause

    exit /b 1

)
 
echo.

echo MySQL       : %MYSQL%

echo User        : %MYSQL_USER%

echo Database    : %MYSQL_DATABASE%

echo Script Path : %DATABASE%
 
if not exist "%DATABASE%" (

    echo.

    echo ERROR: Database script folder not found:

    echo %DATABASE%

    echo.

    pause

    exit /b 1

)
 
set "SQL_FOUND=0"
 
for %%F in ("%DATABASE%\*.sql") do (
 
    if exist "%%~fF" (
 
        set "SQL_FOUND=1"
 
        echo.

        echo --------------------------------------------------

        echo Executing:

        echo %%~nxF

        echo --------------------------------------------------
 
        "%MYSQL%" -u%MYSQL_USER% -p%MYSQL_PASSWORD% "%MYSQL_DATABASE%" < "%%~fF"
 
        if errorlevel 1 (

            echo.

            echo ==================================================

            echo ERROR: Database script failed

            echo File: %%~nxF

            echo ==================================================

            echo.

            pause

            exit /b 1

        )
 
        echo.

        echo Successfully executed:

        echo %%~nxF

    )

)
 
if "%SQL_FOUND%"=="0" (

    echo.

    echo WARNING: No SQL files found in:

    echo %DATABASE%

)
 
echo.

echo All MySQL scripts executed successfully.
 
REM ==================================================

REM 7. START TOMCAT

REM ==================================================
 
echo.

echo ==================================================

echo [7/7] Starting Tomcat...

echo ==================================================
 
echo.

echo Starting Tomcat in a separate window...

echo.
 
REM --------------------------------------------------

REM IMPORTANT:

REM /K keeps the Tomcat CMD window alive.

REM CALL executes catalina.bat correctly.

REM --------------------------------------------------
 
start "Tomcat Server" "%ComSpec%" /k call "%TOMCAT_BIN%\catalina.bat" run
 
echo.

echo Tomcat startup command executed.

echo.

echo Tomcat Server window has been opened.

echo Tomcat Server window will remain open.
 
REM ==================================================

REM WAIT FOR APPLICATION

REM ==================================================
 
echo.

echo ==================================================

echo Waiting for Appzillon application to start...

echo ==================================================
 
set "MAX_RETRIES=60"

set "RETRY_COUNT=0"
 
:CHECK_APP
 
set /a RETRY_COUNT+=1
 
echo.

echo Checking application...

echo Attempt %RETRY_COUNT%/%MAX_RETRIES%

echo URL: %APP_URL%
 
powershell -NoProfile -ExecutionPolicy Bypass -Command "try { $response = Invoke-WebRequest -Uri '%APP_URL%' -UseBasicParsing -TimeoutSec 3; if ($response.StatusCode -ge 200 -and $response.StatusCode -lt 500) { exit 0 } else { exit 1 } } catch { exit 1 }"
 
if %ERRORLEVEL% EQU 0 (

    echo.

    echo ==================================================

    echo APPZILLON APPLICATION IS READY

    echo ==================================================

    goto OPEN_APP

)
 
if %RETRY_COUNT% GEQ %MAX_RETRIES% (
 
    echo.

    echo ==================================================

    echo ERROR: APPZILLON APPLICATION DID NOT START

    echo ==================================================

    echo.

    echo Please check the Tomcat Server window.

    echo.

    echo Tomcat should still be running.

    echo.

    pause

    exit /b 1

)
 
timeout /t 5 /nobreak >nul
 
goto CHECK_APP
 
REM ==================================================

REM OPEN APPLICATION

REM ==================================================
 
:OPEN_APP
 
echo.

echo ==================================================

echo Opening Appzillon application...

echo ==================================================
 
start "" "%APP_URL%"
 
REM ==================================================

REM DEPLOYMENT COMPLETED

REM ==================================================
 
echo.

echo ==================================================

echo     APPZILLON RBC DEPLOYMENT COMPLETED

echo ==================================================
 
echo.

echo Application URL:

echo %APP_URL%
 
echo.

echo Tomcat Home:

echo %TOMCAT_HOME%
 
echo.

echo ==================================================

echo Tomcat is still running.

echo Tomcat Server window will remain open.

echo ==================================================
 
echo.

echo ==================================================

echo DEPLOYMENT WINDOW TIMEOUT

echo ==================================================
 
echo.

echo This deployment window will remain open

echo for 3 HOURS.

echo.

echo After 3 hours, this deployment window will

echo automatically close.

echo.

echo IMPORTANT:

echo This will NOT stop Tomcat.

echo Tomcat Server will continue running.

echo.
 
REM ==================================================

REM 3 HOUR TIMER

REM ==================================================
 
set /a REMAINING=%DEPLOYMENT_TIMEOUT%
 
:THREE_HOUR_TIMER
 
if %REMAINING% LEQ 0 goto TIMER_FINISHED
 
set /a HOURS=REMAINING/3600

set /a MINUTES=(REMAINING%%3600)/60

set /a SECONDS=REMAINING%%60
 
cls
 
echo ==================================================

echo     APPZILLON RBC DEPLOYMENT COMPLETED

echo ==================================================

echo.

echo Application:

echo %APP_URL%

echo.

echo Tomcat:

echo RUNNING

echo.

echo ==================================================

echo     DEPLOYMENT WINDOW REMAINING TIME

echo ==================================================

echo.

echo        %HOURS% hours %MINUTES% minutes %SECONDS% seconds

echo.

echo ==================================================

echo.

echo This window will close automatically after

echo the 3-hour timeout.

echo.

echo Tomcat will NOT be stopped.

echo.
 
timeout /t 1 /nobreak >nul
 
set /a REMAINING-=1
 
goto THREE_HOUR_TIMER
 
REM ==================================================

REM 3 HOURS COMPLETED

REM ==================================================
 
:TIMER_FINISHED
 
cls
 
echo.

echo ==================================================

echo       3 HOURS COMPLETED

echo ==================================================
 
echo.

echo Deployment window is closing...

echo.

echo Tomcat is NOT being stopped.

echo Tomcat Server window will remain running.

echo.
 
timeout /t 5 /nobreak >nul
 
endlocal

exit /b 0
 
