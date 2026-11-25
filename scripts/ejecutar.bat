@echo off
rem Ejecuta el juego desde la raiz del proyecto (las rutas rec\... son relativas).
setlocal
cd /d "%~dp0.."
call "%~dp0_jdk.bat" || exit /b 1
"%GIRAR_JDK%\bin\java" -cp out\production\Girarodillo main.GestorPrincipal
