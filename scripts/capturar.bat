@echo off
rem Saca una captura PNG de una pantalla del juego SIN abrir ventana, para revisar como queda.
rem Uso: scripts\capturar.bat <estado> <salida.png> [ticks] [entrada de la galeria]
rem   estado: MENU, JUEGO, TIENDA, MUSICA, CREDITOS, GALERIA
rem   ticks:  actualizaciones que se dejan pasar antes de la captura (60 = 1 segundo; por defecto 0)
rem   entrada: solo GALERIA: numero de efecto a lanzar antes (0 = el primero)
rem Ejemplo: scripts\capturar.bat GALERIA out\galeria.png 40 3
setlocal
cd /d "%~dp0.."
call "%~dp0compilar.bat" || exit /b 1
call "%~dp0_jdk.bat" || exit /b 1
if not exist out\test mkdir out\test
dir /s /b test\*.java > out\pruebas.txt
"%GIRAR_JDK%\bin\javac" -encoding UTF-8 -cp out\production\Girarodillo -d out\test @out\pruebas.txt
if errorlevel 1 (echo ERROR compilando las pruebas & exit /b 1)
"%GIRAR_JDK%\bin\java" -cp out\production\Girarodillo;out\test Capturador %*
