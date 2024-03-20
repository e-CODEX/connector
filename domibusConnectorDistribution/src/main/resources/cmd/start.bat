@echo off
rem This is the automatically built startup script for the DomibusStandaloneConnector.
rem To be able to run the JAVA_HOME system environment variable must be set properly.

if exist "%JAVA_HOME%" goto okJava
call setenv.bat
if exist "%JAVA_HOME%" goto okJava
echo The JAVA_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
goto end
:okJava

rem set "PATH=%PATH%;%JAVA_HOME%\bin"

set "CURRENT_DIR=%cd%"

set "CLASSPATH=%CURRENT_DIR%\bin\*"
echo %CLASSPATH%

REM set "LIB_FOLDER=%CURRENT_DIR%\lib\"

set "CONFIG_FOLDER=%CURRENT_DIR%\config\"

rem set "LOG_FOLDER=%CURRENT_DIR\logs\"

rem set "FS_STORAGE_FOLDER=%CURRENT_DIR\fsstorage\"

rem set "CONNECTOR_PROPERTIES=%connector-client.properties%"
rem if exist "%CONNECTOR_PROPERTIES%" goto okConnProps
rem set "CONNECTOR_PROPERTIES=conf\connector-client.properties
rem :okConnProps
rem set "connector-client.properties=%CONNECTOR_PROPERTIES%"
rem echo connector-client.properties set to "%CONNECTOR_PROPERTIES%"

rem set "LOGGING_PROPERTIES=%logging.properties%"
rem if exist "%LOGGING_PROPERTIES%" goto okLogProps
rem set "LOGGING_PROPERTIES=conf\log4j.properties
rem :okLogProps
rem set "logging.properties=%LOGGING_PROPERTIES%"
rem echo LOGGING_PROPERTIES set to "%LOGGING_PROPERTIES%"

title "DomibusConnector"

rem -D"spring.config.location=%CONFIG_FOLDER%" -D"spring.config.name=connector" -D"spring.cloud.bootstrap.location=%CONFIG_FOLDER%bootstrap.properties" -D"loader.path=%LIB_FOLDER%"


@echo on
"%JAVA_HOME%\bin\java" -D"loader.path=./lib" -Dspring.config.name=connector -cp "%CLASSPATH%" "org.springframework.boot.loader.PropertiesLauncher"

:end

