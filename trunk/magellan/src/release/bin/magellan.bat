@ECHO OFF

REM This is a startup file for a windows environment. It shall start magellan on Windows 9x, NT, 2000, XP
REM It will do the following steps:
REM a) fetch all arguments via shift-loop
REM b) check for java environment
REM c) check for magellan.ini position

if "%OS%"=="Windows_NT" setlocal

rem %~dp0 is expanded pathname of the current script under NT
set _DEFAULT_MAGELLAN_HOME=%~dp0..

if "%MAGELLAN_HOME%"=="" set MAGELLAN_HOME=%_DEFAULT_MAGELLAN_HOME%
set _DEFAULT_MAGELLAN_HOME=

set _MAGELLAN_OPTS=%MAGELLAN_OPTS%
if "%MAGELLAN_OPTS%"=="" set _MAGELLAN_OPTS=-Xms64m -Xmx240m

rem Slurp the command line arguments. This loop allows for an unlimited number
rem of arguments (up to the command line limit, anyway).
set MAGELLAN_CMD_LINE_ARGS=%1
if ""%1""=="""" goto doneStart
shift
:setupArgs
if ""%1""=="""" goto doneStart
if ""%1""==""-d"" goto fetchWorkDir
set MAGELLAN_CMD_LINE_ARGS=%MAGELLAN_CMD_LINE_ARGS% %1
shift
goto setupArgs

:fetchWorkDir
shift
if ""%1""=="""" goto doneStart
set MAGELLAN_WORKDIR=%1
goto setupArgs

rem This label provides a place for the argument list loop to break out 
rem and for NT handling to skip to.

:doneStart

:checkJava
set _JAVACMD=%JAVACMD%
set LOCALCLASSPATH=%CLASSPATH%
for /R "%MAGELLAN_HOME%\lib" %%i in (.) do for %%j in ("%%i\*.jar") do call "%MAGELLAN_HOME%\bin\lcp.bat" %%j

if "%JAVA_HOME%" == "" goto noJavaHome
if not exist "%JAVA_HOME%\bin\javaw.exe" goto noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=%JAVA_HOME%\bin\javaw.exe
if exist "%JAVA_HOME%\lib\tools.jar" set LOCALCLASSPATH=%JAVA_HOME%\lib\tools.jar;%LOCALCLASSPATH%
if exist "%JAVA_HOME%\lib\classes.zip" set LOCALCLASSPATH=%JAVA_HOME%\lib\classes.zip;%LOCALCLASSPATH%
goto doneCheckJava

:noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=javaw.exe
echo.
echo Warning: JAVA_HOME environment variable is not set.
echo   If Magellan fails because java is not found you will
echo   need to set the JAVA_HOME environment variable
echo   to the installation directory of java.
echo.

:doneCheckJava

if "%MAGELLAN_WORKDIR%" == "" set MAGELLAN_WORKDIR=%MAGELLAN_HOME%\work

@ECHO ON
start %_JAVACMD% %_MAGELLAN_OPTS% -classpath "%LOCALCLASSPATH%" "-Dmagellan.home=%MAGELLAN_WORKDIR%" com.eressea.demo.Client -d "%MAGELLAN_WORKDIR%" %MAGELLAN_CMD_LINE_ARGS%
goto end

@ECHO OFF

:end

if "%OS%"=="Windows_NT" endlocal
REM pause
