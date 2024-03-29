@echo off

REM echo Magellan Build System
REM echo ----------------

if "%JAVA_HOME%" == "" goto error

set LIBDIR=buildlib
set LOCALCLASSPATH=%JAVA_HOME%\lib\tools.jar;%JAVA_HOME%\lib\classes.zip
set LOCALCLASSPATH=%LOCALCLASSPATH%;%LIBDIR%\ant-1.5.3.jar
set LOCALCLASSPATH=%LOCALCLASSPATH%;%LIBDIR%\optional-1.5.3.jar
set LOCALCLASSPATH=%LOCALCLASSPATH%;%LIBDIR%\xml-apis.jar
set LOCALCLASSPATH=%LOCALCLASSPATH%;%LIBDIR%\xercesImpl-2.2.1.jar
set LOCALCLASSPATH=%LOCALCLASSPATH%;%LIBDIR%\xalan-2.4.1.jar

set ANT_HOME=%LIBDIR%

rem echo Building with classpath %LOCALCLASSPATH%

rem echo Starting Ant...

%JAVA_HOME%\bin\java.exe -Dant.home=%ANT_HOME% -classpath "%LOCALCLASSPATH%" org.apache.tools.ant.Main %1 %2 %3 %4 %5

goto end

:error

echo ERROR: JAVA_HOME not found in your environment.
echo Please, set the JAVA_HOME variable in your environment to match the
echo location of the Java Virtual Machine you want to use.

:end

rem set LOCALCLASSPATH=

