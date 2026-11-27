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
| Clic | Menús y tienda |
| Esc | Salir |

## Empaquetar

`scripts\empaquetar.bat` crea `dist\Girarrodillos.jar`. Se juega con `java -jar dist\Girarrodillos.jar` desde la raíz del proyecto
(los recursos de `rec/` se leen del disco con rutas relativas, así que la carpeta debe estar al lado).

## Herramientas de desarrollo

- `scripts\capturar.bat <estado> <salida.png> [ticks] [entrada]` saca una captura PNG de una pantalla sin abrir ventana
  (por ejemplo `scripts\capturar.bat GALERIA out\galeria.png 40 3`).
- Con `-Dgirar.debug=true` el juego muestra FPS y textos de depuración, y la tecla **F2** abre la galería de efectos
  (W/S elegir, Espacio lanzar, Enter volver).

## Progreso

Las monedas, los rodillos comprados y el equipado se guardan en `guardado.properties` (ignorado por git). Borra ese archivo para empezar de cero.

## Música

`rec/musica/OST.zip` (≈ 265 MB) no está en el repositorio por superar el límite de GitHub. Las pistas `.wav` sueltas sí están.

## Estado

Hay un plan de trabajo con tareas pequeñas en [PLAN.md](PLAN.md).
