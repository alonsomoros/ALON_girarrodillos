@echo off
rem Crea dist\Girarrodillos.jar. Para jugar: java -jar dist\Girarrodillos.jar ejecutado desde la raiz
rem del proyecto (junto a la carpeta rec\, porque los recursos se leen de disco con rutas relativas).
setlocal
cd /d "%~dp0.."
call "%~dp0compilar.bat" || exit /b 1
call "%~dp0_jdk.bat" || exit /b 1
if not exist dist mkdir dist
"%GIRAR_JDK%\bin\jar" --create --file dist\Girarrodillos.jar --main-class main.GestorPrincipal -C out\production\Girarodillo .
if errorlevel 1 (echo ERROR creando el jar & exit /b 1)
echo Creado dist\Girarrodillos.jar
