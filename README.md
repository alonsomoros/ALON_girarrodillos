# Girarrodillos

Juego de combate por turnos con rodillos tipo tragaperras, hecho en Java (Swing/AWT y `javax.sound`), sin librerías externas.

## Requisitos

- JDK 21 (en Windows los scripts lo buscan en `C:\Program Files\Java\jdk-21` o en `%USERPROFILE%\.jdks\corretto-21.0.8`; puedes forzar otro con la variable `GIRAR_JDK`).

## Compilar y ejecutar

Desde la raíz del proyecto (las rutas `rec/...` son relativas):

```
scripts\compilar.bat
scripts\ejecutar.bat
scripts\probar.bat   # pruebas de lógica (sin librerías)
```

En un IDE (VS Code, IntelliJ…): JDK 21 y ejecuta `main.GestorPrincipal` con la raíz del proyecto como directorio de trabajo.

## Controles

| Tecla | Acción |
|-------|--------|
| A / D | Mover el puntero del rodillo / elegir héroe |
| Espacio | Fijar o soltar un canal / confirmar héroe |
| E | Girar el rodillo (avanza el turno) |
| Enter | Volver al menú principal |
| Esc | Salir |

## Música

`rec/musica/OST.zip` (≈ 265 MB) no está en el repositorio por superar el límite de GitHub. Las pistas `.wav` sueltas sí están.

## Estado

Hay un plan de trabajo con tareas pequeñas en [PLAN.md](PLAN.md).
