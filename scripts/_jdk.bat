@echo off
rem Localiza el JDK 21. Puedes forzar uno con la variable GIRAR_JDK (ruta del JDK).
if defined GIRAR_JDK goto fin
if exist "C:\Program Files\Java\jdk-21\bin\javac.exe" set "GIRAR_JDK=C:\Program Files\Java\jdk-21"
if not defined GIRAR_JDK if exist "%USERPROFILE%\.jdks\corretto-21.0.8\bin\javac.exe" set "GIRAR_JDK=%USERPROFILE%\.jdks\corretto-21.0.8"
:fin
if not defined GIRAR_JDK (
    echo No se encontro JDK 21. Define GIRAR_JDK con su ruta.
    exit /b 1
)
