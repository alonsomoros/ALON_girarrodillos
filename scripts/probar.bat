@echo off
rem Compila el juego y las pruebas de test\ y las ejecuta. Devuelve error si alguna falla.
setlocal
cd /d "%~dp0.."
call "%~dp0compilar.bat" || exit /b 1
call "%~dp0_jdk.bat" || exit /b 1
if not exist out\test mkdir out\test
"%GIRAR_JDK%\bin\javac" -encoding UTF-8 -cp out\production\Girarodillo -d out\test test\*.java
if errorlevel 1 (echo ERROR compilando las pruebas & exit /b 1)
"%GIRAR_JDK%\bin\java" -ea -cp out\production\Girarodillo;out\test PruebasLogica
