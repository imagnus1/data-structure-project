@echo off
cd /d "%~dp0"
if not exist build mkdir build
javac -encoding UTF-8 -d build *.java tests\*.java
if errorlevel 1 exit /b 1
java -cp build ProjectTest
if errorlevel 1 exit /b 1
echo All project tests passed.
