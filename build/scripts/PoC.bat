@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  PoC startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and PO_C_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\PoC.jar;%APP_HOME%\lib\guava-23.0.jar;%APP_HOME%\lib\graphviz-java-0.5.0.jar;%APP_HOME%\lib\slf4j-nop-1.7.25.jar;%APP_HOME%\lib\jfoenix-9.0.3.jar;%APP_HOME%\lib\freehep-psviewer-2.0.jar;%APP_HOME%\lib\jsr305-1.3.9.jar;%APP_HOME%\lib\error_prone_annotations-2.0.18.jar;%APP_HOME%\lib\j2objc-annotations-1.1.jar;%APP_HOME%\lib\animal-sniffer-annotations-1.14.jar;%APP_HOME%\lib\batik-rasterizer-1.9.jar;%APP_HOME%\lib\batik-codec-1.9.jar;%APP_HOME%\lib\batik-svgrasterizer-1.9.jar;%APP_HOME%\lib\batik-transcoder-1.9.jar;%APP_HOME%\lib\batik-bridge-1.9.jar;%APP_HOME%\lib\batik-script-1.9.jar;%APP_HOME%\lib\batik-anim-1.9.jar;%APP_HOME%\lib\batik-svg-dom-1.9.jar;%APP_HOME%\lib\batik-dom-1.9.jar;%APP_HOME%\lib\batik-css-1.9.jar;%APP_HOME%\lib\xmlgraphics-commons-2.2.jar;%APP_HOME%\lib\j2v8_macosx_x86_64-4.6.0.jar;%APP_HOME%\lib\j2v8_linux_x86_64-4.6.0.jar;%APP_HOME%\lib\j2v8_win32_x86_64-4.6.0.jar;%APP_HOME%\lib\j2v8_win32_x86-4.6.0.jar;%APP_HOME%\lib\commons-exec-1.3.jar;%APP_HOME%\lib\jcl-over-slf4j-1.7.25.jar;%APP_HOME%\lib\jul-to-slf4j-1.7.25.jar;%APP_HOME%\lib\slf4j-api-1.7.25.jar;%APP_HOME%\lib\freehep-argv-2.1.jar;%APP_HOME%\lib\freehep-graphics2d-2.1.3.jar;%APP_HOME%\lib\freehep-graphics-base-2.1.3.jar;%APP_HOME%\lib\freehep-io-2.1.jar;%APP_HOME%\lib\batik-parser-1.9.jar;%APP_HOME%\lib\batik-gvt-1.9.jar;%APP_HOME%\lib\batik-svggen-1.9.jar;%APP_HOME%\lib\batik-awt-util-1.9.jar;%APP_HOME%\lib\batik-xml-1.9.jar;%APP_HOME%\lib\batik-util-1.9.jar;%APP_HOME%\lib\xalan-2.7.2.jar;%APP_HOME%\lib\serializer-2.7.2.jar;%APP_HOME%\lib\xml-apis-1.3.04.jar;%APP_HOME%\lib\commons-io-1.3.1.jar;%APP_HOME%\lib\junit-4.7.jar;%APP_HOME%\lib\batik-ext-1.9.jar;%APP_HOME%\lib\xml-apis-ext-1.3.04.jar;%APP_HOME%\lib\batik-constants-1.9.jar;%APP_HOME%\lib\batik-i18n-1.9.jar;%APP_HOME%\lib\commons-logging-1.0.4.jar

@rem Execute PoC
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %PO_C_OPTS%  -classpath "%CLASSPATH%" tk.dcmmc.Reg2AutomataGUI %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable PO_C_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%PO_C_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
