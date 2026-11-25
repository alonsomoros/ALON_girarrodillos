@echo off
rem Compila src\**\*.java a out\production\Girarodillo (ejecutar desde cualquier sitio).
setlocal
cd /d "%~dp0.."
call "%~dp0_jdk.bat" || exit /b 1
if not exist out\production\Girarodillo mkdir out\production\Girarodillo
dir /s /b src\*.java > out\fuentes.txt
"%GIRAR_JDK%\bin\javac" -encoding UTF-8 -d out\production\Girarodillo @out\fuentes.txt
if errorlevel 1 (echo ERROR de compilacion & exit /b 1)
echo Compilado OK
