
set _CLASSPATHCOMPONENT=%1
if ""%1""=="""" goto gotAllArgs
shift

:argCheck
if ""%1""=="""" goto gotAllArgs
set _CLASSPATHCOMPONENT=%_CLASSPATHCOMPONENT% %1
shift
goto argCheck

:gotAllArgs
REM remove "\.\" in classpath
set LOCALCLASSPATH=%_CLASSPATHCOMPONENT:\.\=\%;%LOCALCLASSPATH%

