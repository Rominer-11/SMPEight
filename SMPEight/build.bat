@echo off
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.6.7-hotspot
set MAVEN=C:\Users\tmmck\Desktop\BuildTools\apache-maven-3.9.6\bin\mvn.cmd
cd /d "%~dp0"
echo Building SMPEight...
"%MAVEN%" clean package -q
if %ERRORLEVEL% EQU 0 (
    echo Build successful! SMPEight.jar copied to SMPEightTesting\plugins\
) else (
    echo Build failed!
)
pause