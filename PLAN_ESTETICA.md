# Plan de mejora estética y "sensación de juego" de Girarrodillos (iteración 2)

## Context

El juego v1.0 funciona y está limpio, pero se ve y se siente estático: los héroes son una imagen fija, los ataques son un
sprite de 55x55 que salta entre 3 puntos, el rodillo cambia de símbolos de golpe, el daño solo se ve como dígitos en la corona,
la UI son recuadros pintados con coordenadas y el texto depende de fuentes del sistema. Quieres mejorarlo **sin escribir un
motor gráfico**, **sin librerías pesadas** y **dibujando a mano todo lo que se pueda** (estrellas de victoria, impactos, UI, fuente).

La respuesta técnica: no hace falta un motor. Hace falta un **mini-motor de ~8 clases pequeñas, solo JDK (`main.motor`)** sobre el
bucle de 60 APS que ya tenemos: tiempo, interpolación (easing), animaciones por tiras de frames, efectos/partículas, secuencias
(línea de tiempo) y sacudida/fundido. Con eso se resuelven los "flujos y secuencias" que un motor te daría hechos.

### Decisiones tomadas (tus respuestas)
| # | Tema | Decisión |
|---|------|----------|
| D8 | Fuente | **Bitmap dibujada por ti** (atlas PNG en cuadrícula + clase `FuenteBitmap`). Esto cierra la tarea 1.24 sin `.ttf`. |
| D9 | Orden | **Sensación de juego primero** (Fases 5-6), luego UI (7-8) y por último pantalla/ajustes (9). |
| D10 | Pantalla/audio | Escalado entero + F11 pantalla completa **y** volumen/opciones (Fase 9). |

### Sobre las herramientas que mencionas
- **GDX Texture Packer / libGDX / LWJGL / FXGL:** no los necesitas. Texture Packer junta muchos PNG en un atlas para libGDX; nosotros ya
  tenemos caché de imágenes y pocos sprites. Pasar a libGDX sería reescribir el juego. Si algún día quieres shaders, mando o 3D, se
  reevalúa; hoy el mini-motor cubre todo lo que pides.
- **Para dibujar (gratis):** LibreSprite o Aseprite (animación por frames), Piskel (web), Pixelorama; tú ya usas GIMP (`.xcf`) y sirve
  para tiras de frames.
- **Para sonidos propios:** jsfxr / Bfxr / ChipTone (generan efectos retro en el navegador) y Audacity para recortar/normalizar.
- **Convención de sprites animados (sin herramientas extra):** una **tira horizontal** `nombre_Nf.png` con N frames del mismo ancho
  (ej. `impacto_5f.png` = 5 frames). Exporta así desde LibreSprite/Aseprite/Piskel.

## Reglas de trabajo (como antes)
1. **1 tarea = 1 commit** (`fase.tarea: título`), que tacha su casilla. Una rama por fase (`fase-5-motor`, …); merge y **push solo con tu OK**.
2. Cada tarea compila, pasa `scripts\probar.bat` y se **comprueba visualmente** (captura de pantalla con el `Capturador` de la 5.13 y/o juego real).
3. **Registro de lo "en crudo":** todo lo que se dibuja con código o suena con audio generado se apunta en `docs/sustituibles.md` en el mismo commit, para decidir después qué se sustituye por arte tuyo.
4. **Nada se bloquea por arte que aún no hayas dibujado:** cada efecto trae un **placeholder procedural** (hecho con código) y se sustituye
   cuando dejas tu PNG con el mismo nombre en `rec/`. 👤 = lo dibujas tú (opcional, sin prisa).
4. Presupuesto de rendimiento: **≥ 55 FPS y < 25 % de CPU** con efectos activos; las partículas tienen límite y no se crean objetos por frame.
5. La lógica del juego (reglas, rodillo, IA) **no cambia**: los efectos se enganchan por eventos (daño recibido, ataque lanzado, nivel subido…).

---

## FASE 5 — Cimientos: pipeline de recursos y mini-motor  (rama `fase-5-motor`)

### 5A. Recursos (alivia RAM y prepara el arte nuevo)
- [x] **5.1** `CargadorRecursos.cargarEscalada(ruta, ancho, alto)` con caché (la clave incluye el tamaño). Hoy los fondos de 3840x2400 se cargan enteros y se escalan en cada frame.
- [x] **5.2** Fondos de menú, tienda, créditos y música usan la versión escalada a 1280x640. *Verif.:* la RAM baja de ~450 MB (medido con `medir.ps1`).
- [ ] **5.3** Inventario de `rec/wallpapers`: hay 12 "Captura de pantalla (…).png" y otras imágenes que el código no usa. Moverlas a `rec/_archivo/` (tú decides si borrar). *Solo tras tu OK.*
- [x] **5.4** `main.motor.Tiempo`: contador de actualizaciones (ticks) que avanza en `GestorPrincipal.actualizar()` y conversión ms↔ticks. Pruebas.
- [x] **5.5** `main.motor.Interpolador`: easing puros (lineal, easeIn/Out, easeInOut, rebote, elástico, easeOutBack). Pruebas de valores en 0, 0.5 y 1.
- [x] **5.6** `main.motor.Animacion`: carga una tira `nombre_Nf.png`, ms por frame, modos una vez/bucle/ping-pong, `frameActual(ticks)`. Pruebas de selección de frame.

### 5B. Efectos, secuencias y transiciones
- [x] **5.7** `Efecto` (interfaz: `actualizar()`, `dibujar(g)`, `terminado()`) y `GestorEfectos` con 3 capas (fondo/medio/frente) y ciclo de vida. Un gestor por estado. Pruebas de alta/baja.
- [x] **5.8** `main.motor.Secuencia` (línea de tiempo): `esperar(ms)`, `hacer(Runnable)`, `durante(ms, t -> …)`, encadenable y cancelable. Pruebas de orden y tiempos.
- [x] **5.9** `Sacudida` (screen shake): desplazamiento decreciente aplicado en `SuperficieDeDibujo` antes de pintar el estado.
- [x] **5.10** `Fundido` (fade a negro / desde negro) y transición suave en `GestorEstados.cambiarEstadoActual` (≈ 15 ticks).
- [x] **5.11** `Sonidos`: banco de efectos precargados (no se crea un `Clip` por reproducción), polifonía y volumen por categoría. `AudioPlayer` delega en él. Pruebas mínimas.

### 5C. Herramientas para iterar con tu arte
- [x] **5.12** **Galería de efectos** (solo con `-Dgirar.debug=true`, tecla F2): pantalla donde pulsas teclas para lanzar cada efecto/animación aislados. Es donde verás tus sprites nuevos sin jugar una partida.
- [x] **5.13** `Capturador` (en `test/`): renderiza un estado/efecto a PNG sin abrir ventana, para revisar el aspecto de cada tarea. `scripts\capturar.bat`.

**Cierre de fase 5:** pruebas verdes, RAM y FPS medidos y anotados, merge + push con tu OK.

**Resultados de la Fase 5 (medidos con JDK 21):**
- Menú: CPU 28 % → **16 %** y RAM 470 → **322 MB** (fondos escalados una sola vez al cargar). Partida: ~9 % de CPU, 340 MB, 57 FPS.
- 53 pruebas en verde. Nuevo paquete `main.motor`: `Tiempo`, `Interpolador`, `Animacion`, `Efecto`/`EfectoTemporal`/`GestorEfectos`, `Secuencia`, `Sacudida`, `Fundido`, `Sonidos`.
- Transiciones con fundido entre pantallas (`GestorEstados.irA`), banco de sonidos con volumen y silencio, galería F2 y `scripts\capturar.bat`.
- **5.3 queda pendiente de tu OK** (mover archivos sin uso a `rec/_archivo/`): 47 archivos, 26,9 MB. Ver el mensaje de cierre de la fase.

---

## FASE 6 — Sensación de juego en el tablero  (rama `fase-6-tablero`)

### 6A. Rodillo (lo que más se toca en cada turno)
- [x] **6.1** **Giro animado:** cada canal desliza sus símbolos en vertical (hay 3 por canal: lateral/principal/lateral) y frena escalonado de izquierda a derecha con rebote; el resultado ya está decidido al empezar (la lógica no cambia). `Rodillo` guarda estado `GIRANDO/PARADO`.
- [x] **6.2** Sonidos de giro y de "tic" al frenar cada canal (`giro.wav`, `tic.wav`). Provisionales generados; luego los tuyos con jsfxr.
- [x] **6.3** **Fijar canal animado:** el `Fijador` baja con easing y suena `fijar.wav`; al soltar sube.
- [x] **6.4** Puntero del rodillo con pulso suave (escala/brillo) en vez de triángulo fijo.
- [ ] **6.5** **Resolución visible de la tirada:** tras la última tirada, los símbolos que cuentan brillan y "vuelan" con estela a su destino (activador, bastión o barra de EXP), con un "+N" flotante. Se arma con `Secuencia`.
- [x] **6.6** Luces de turno: pasar los 3 círculos pintados a una lámpara sprite de 3 frames (apagada/encendida/parpadeo). 👤 `lampara_3f.png`

### 6B. Ataques y daño
- [x] **6.7a** **Cola de acciones por prioridad (lógica):** las activaciones de la ronda se resuelven de una en una en el orden Asesino, Sacerdote, Ingeniero, Mago, Guerrero, Arquero; el Sacerdote ayuda al compañero tras actuar este. Ver `docs/animaciones.md`. Con pruebas.
- [x] **6.7** `Ataque` con trayecto **interpolado** (curva suave con easing, rotación hacia el objetivo) en vez de saltar entre 3 puntos fijos.
- [x] **6.8** Ataque en 3 pasos con `Secuencia`: preparación (el héroe se ilumina y retrocede un píxel), proyectil, impacto. Duración total ≈ la actual (2 s) para no cambiar el ritmo.
- [ ] **6.9** **Impacto:** animación de 5 frames en el punto de golpe + `Destello` + sacudida pequeña + `impacto.wav`. 👤 `impacto_5f.png` (64x64 por frame). Placeholder: estallido de partículas.
- [x] **6.10** Variantes por héroe: estela de la flecha, bola de fuego con partículas, tajo del Caballero, destello del Sacerdote… y un sonido por héroe (6 `.wav`).
- [x] **6.11** **Números flotantes:** "-3" rojo sobre la corona, "+2" verde al curar, "-2" gris sobre el bastión; suben y se desvanecen (usa la fuente nueva cuando exista; antes, la actual).
- [x] **6.12** **Corona:** pulso/sacudida y destello rojo al recibir daño, y barra de vida visual (gemas o corazones) además de los dígitos; se agrieta cuando le queda poco. 👤 `vida_gema_2f.png`
- [x] **6.13** **Bastión:** la altura sube y baja con animación (bloques que caen/se levantan), polvo y escombros al romperse, sonido.
- [ ] **6.14** **Activadores:** la barra baja con tween suave (no salta), brilla cuando llega a 0 ("listo") y parpadea hasta que el héroe actúa.

### 6C. Héroes y progresión
- [ ] **6.15** **Idle:** cada héroe "respira" (sube/baja 1-2 px y cambia de escala mínimamente, procedural). Si dibujas 2-4 frames, se usan en su lugar. 👤 `arquero_lvl1_2f.png`, etc. (prioridad baja)
- [ ] **6.16** **Animación de ataque del héroe:** pequeño impulso hacia delante y vuelta con easing durante el lanzamiento.
- [ ] **6.17** **Subida de nivel:** destello dorado, estrellitas, cambio de sprite con flash y `nivel.wav`.
- [ ] **6.18** **Bomba (ataque especial):** carga con halo, la pantalla se oscurece, sacudida fuerte y explosión; `bomba.wav`.
- [ ] **6.19** Barra de EXP de la base: se rellena con animación y brilla al llenarse.

### 6D. Fin de partida
- [ ] **6.20** **Victoria:** lluvia de estrellas/confeti (partículas con tus sprites) y banner "VICTORIA" que cae con rebote. **Derrota:** viñeta oscura y humo. **Empate:** neutro. 👤 `estrella_3x.png` (tres tamaños), `confeti_4x.png`
- [ ] **6.21** El contador de monedas "+3" sube animado con sonido de monedas (`monedas.wav`).
- [ ] **6.22** (Opcional) *Hit-stop*: 3-4 frames de pausa en impactos fuertes para dar peso.

**Cierre de fase 6:** partida completa grabada con el `Capturador`/robot, FPS y CPU medidos, merge + push.

---

## FASE 7 — UI hecha a mano: fuente, paneles y botones  (rama `fase-7-ui`)

- [ ] **7.1** `docs/ui.md`: guía de estilo (paleta de `Constantes` + 2-3 colores, tamaños, márgenes, estados normal/hover/pulsado).
- [ ] **7.2** `FuenteBitmap`: lee un atlas PNG (ASCII 32-126 + `ñ á é í ó ú ü ¡ ¿`), ancho variable por glifo (detectado por columnas vacías), `medir`, `dibujar`, centrado, sombra y tinte por color (con caché por color). Pruebas de medida.
- [ ] **7.3** Plantilla `rec/fuentes/plantilla.png` generada con cuadrícula y guías para que dibujes tus letras. 👤 `rec/fuentes/fuente.png`. Mientras no exista, `FuenteBitmap` pinta con una fuente genérica (sin depender de "Algerian").
- [ ] **7.4** `Dibujador.TEXTO` y `MedidorString` usan `FuenteBitmap` (escalas enteras 1x, 2x, 3x). **Cierra la 1.24.**
- [ ] **7.5** `Panel9` (9-slice): estira esquinas/lados de una imagen pequeña a cualquier tamaño. Pruebas del cálculo de rectángulos.
- [ ] **7.6** Arte de panel: `panel.png`, `panel_hover.png`, `panel_activo.png` (24x24, esquinas de 8 px). 👤 Placeholder: `Panel9.generar()` con código.
- [ ] **7.7** `Boton` (texto, normal/hover/pulsado/deshabilitado, sonido de clic, callback) y `ListaVertical` para menús. Pruebas de la lógica de hover/clic.
- [ ] **7.8** Cursor propio con 2 estados (flecha / mano) dibujado por el juego; usa `flecha.png` actual como base.
- [ ] **7.9** Iconos de HUD de 16x16 unificados (cuadrado, rombo, martillo, corona, moneda, EXP). 👤 `iconos_hud.png`
- [ ] **7.10** `Tooltip`: caja de información reutilizable (descripción de héroe, de rodillo…).
- [ ] **7.11** **Logo** dibujado de "GIRARRODILLOS" (sustituye el texto con sombra y los semicírculos pintados de `GestorMenuPrincipal`). 👤 `logo.png` (~515x91)

---

## FASE 7b — Controles cómodos: flechas y navegación por teclado  (rama `fase-7b-controles`)

Hoy el teclado es fiel al juego original (WASD mueve, `E` gira, Espacio elige/fija) y los menús solo se manejan con el ratón.
Objetivo: añadir los controles habituales **sin quitar los actuales**.

- [ ] **7b.1** `Teclado`: acciones lógicas (ARRIBA, ABAJO, IZQUIERDA, DERECHA, ACEPTAR, ATRAS) que se activan con **flechas o WASD** y con **Enter/Espacio** y **Esc**. Todo el juego pregunta por la acción, no por la tecla. Pruebas del mapeo.
- [ ] **7b.2** **Foco en los menús:** una etiqueta/botón siempre tiene el foco (marcado con flecha o resalte); flechas arriba/abajo (o izquierda/derecha) lo mueven y Enter/Espacio lo acepta. Si el ratón pasa por encima, el foco le sigue (y el clic sigue funcionando). Se hace sobre `Boton` y `ListaVertical` (7.7).
- [ ] **7b.3** Aplicar el foco a: menú principal, tienda (elegir rodillo y comprar), música (siguiente/anterior), créditos (Enter salta) y la pausa (8.11).
- [ ] **7b.4** **En partida:** flechas izquierda/derecha mueven el puntero del rodillo (como A/D), **Enter** gira (como `E`) y **Espacio** fija (como hoy). En la selección de héroes: flechas + Enter. Los controles antiguos siguen funcionando.
- [ ] **7b.5** Rótulos de ayuda en pantalla con las teclas válidas en cada pantalla (se enlaza con los iconos de teclas de la 8.5).
- [ ] **7b.6** Pruebas de navegación por foco (orden, salto de elementos deshabilitados, rebote en los extremos).

## FASE 8 — Pantallas y flujo  (rama `fase-8-pantallas`)

- [ ] **8.1** **Menú principal:** botones con `Panel9` y hover animado (flecha), título con brillo lento, fondo con parallax según el ratón (4-8 px) y partículas ambiente (hojas/luciérnagas). 👤 `particula_hoja_2x.png`
- [ ] **8.2** Entradas nuevas en el menú: **Cómo jugar** y **Opciones** (esta última se completa en la Fase 9).
- [ ] **8.3** **Cómo jugar:** `Instrucciones.txt` partido en páginas con ilustraciones y flechas (hoy el texto no se puede leer dentro del juego).
- [ ] **8.4** **Selección de héroes** como pantalla propia (sale del tablero): 6 héroes con idle, panel con nombre, descripción corta, stats por nivel y energía; elegir izquierdo/derecho y confirmar. Requiere extraer la selección de `Player` a un estado `SELECCION`.
- [ ] **8.5** Rótulos de ronda en el tablero ("Tirada 1/3", "Turno de la IA", "¡Ataque!") con iconos de teclas dibujados (E, A/D, Espacio). 👤 `teclas_4x.png`
- [ ] **8.6** **Ratón en el tablero:** clic en un canal para fijarlo y botón "Girar" en pantalla (el teclado sigue funcionando).
- [ ] **8.7** **Música:** nubes en parallax (el método `dibujarNubes` está vacío desde el principio), icono de "sonando" animado, siguiente/parar y volumen.
- [ ] **8.8** **Tienda:** rehecha con `Panel9` e iconos, vista previa del rodillo (barras con los símbolos y su probabilidad) y animación de compra (monedas que vuelan + destello).
- [ ] **8.9** **Créditos:** fundido cruzado entre fondos, fuente nueva y Enter para saltar.
- [ ] **8.10** **Fin de partida** con panel propio: resumen (rondas, daño hecho, monedas) y botones **Reintentar / Menú**.
- [ ] **8.11** **Pausa (Esc):** Continuar / Opciones / Salir al menú / Cerrar (hoy Esc cierra el juego al instante y pierdes la partida).

---

## FASE 9 — Pantalla, escala y ajustes  (rama `fase-9-ajustes`)

- [ ] **9.1** Dibujar siempre en un buffer lógico de 1280x640 y escalarlo a la ventana con factor **entero** (vecino más cercano, pixel perfect); el `Raton` convierte coordenadas. Hoy `FACTOR_ESCALA` vale siempre 1.
- [ ] **9.2** **F11:** pantalla completa/ventana con letterbox; F10 alterna 1x/2x. En tu pantalla de 2560x1440 la ventana pasa a verse al doble.
- [ ] **9.3** Volumen de música y de efectos (`MASTER_GAIN`) y silenciar con M, sobre el banco `Sonidos` de la 5.11.
- [ ] **9.4** Pantalla **Opciones** (deslizadores de `Panel9`): volúmenes, pantalla completa, mostrar FPS. Persistencia en `ajustes.properties` (ignorado por git).
- [ ] **9.5** Opción "reducir efectos" (menos sacudida y parpadeo) para comodidad.

## FASE 10 — Cierre  (rama `fase-10-cierre`)
- [ ] **10.1** `docs/arte.md` con todo el arte que falta/está sustituido y su nombre de archivo (ver la tabla de abajo ya completada).
- [ ] **10.2** Revisión de rendimiento y de memoria con todo activo; ajustar límites de partículas.
- [ ] **10.3** README, `PLAN.md` y etiqueta **v1.1**.

---

## Guía de arte: lo que puedes dibujar tú (orden recomendado, de más a menos impacto)

| # | Qué | Tamaño | Frames | Archivo | Se usa en |
|---|-----|--------|--------|---------|-----------|
| 1 | Impacto de golpe | 64x64 | 5 | `rec/sprites/efectos/impacto_5f.png` | 6.9 |
| 2 | Estrellas de victoria | 16/24/32 | 3 sprites | `rec/sprites/efectos/estrella_3x.png` | 6.17, 6.20 |
| 3 | Panel de UI (9-slice) | 24x24 | 3 estados | `rec/ui/panel*.png` | 7.6 en adelante |
| 4 | Fuente bitmap | rejilla 8x12 aprox. | 1 atlas | `rec/fuentes/fuente.png` | 7.3 |
| 5 | Lámpara de turno | 24x32 | 3 | `rec/sprites/efectos/lampara_3f.png` | 6.6 |
| 6 | Confeti / partículas ambiente | 8x8-12x12 | 4 + 2 | `confeti_4x.png`, `particula_hoja_2x.png` | 6.20, 8.1 |
| 7 | Iconos de HUD | 16x16 | 1 hoja | `rec/ui/iconos_hud.png` | 7.9 |
| 8 | Gema/corazón de vida | 20x20 | 2 | `rec/sprites/efectos/vida_gema_2f.png` | 6.12 |
| 9 | Logo | ~515x91 | 1 | `rec/ui/logo.png` | 7.11 |
| 10 | Teclas (E, A, D, Espacio) | 32x32 | 4 | `rec/ui/teclas_4x.png` | 8.5 |
| 11 | Idle de héroes | 125x145 | 2-4 por héroe y nivel | `rec/sprites/figuras/<h>/<h>_lvlN_Nf.png` | 6.15 |
| 12 | Sonidos propios | wav 44,1 kHz | — | `rec/sonidos/*.wav` (jsfxr) | 6.2, 6.10… |
| 13 | Bastión de la IA (5 alturas, ya orientado hacia abajo) | 355x160 | 5 sprites | `rec/sprites/bastion/bastion_ia_1.png` a `bastion_ia_5.png` | 6.13 (si no existen, se usa el del jugador volteado) |

Ya tienes dos recursos que encajan sin dibujar nada: los sprites dorados `*_lvl3x.png` (arte de nivel oro, hoy sin usar, útiles para el
destello de subida de nivel) y las nubes de `Musica.png` (para el parallax de la 8.7).

## Verificación general
- **Por tarea:** compilar, `scripts\probar.bat` en verde y captura con el `Capturador` (revisamos juntos el resultado).
- **Lógica nueva** (`Interpolador`, `Animacion`, `Secuencia`, `GestorEfectos`, `Panel9`, `FuenteBitmap`, `Boton`): pruebas con el ejecutor propio, sin librerías.
- **Rendimiento:** `medir.ps1` y `jugar.ps1` (robot) tras cada fase: FPS ≥ 55, CPU < 25 %, RAM por debajo de la actual.
- **Final:** partida completa jugador vs IA con todas las animaciones, tienda, pausa, F11 y opciones, sin errores en consola.

## Archivos críticos
`GestorPrincipal.java` (reloj), `graficos/SuperficieDeDibujo.java` (sacudida, escalado), `herramientas/{CargadorRecursos,Dibujador,MedidorString}.java`,
`maquina_estado/GestorEstados.java` (transiciones), `maquina_estado/juego/{Rodillo,Ataque,Figura,Activador,Bastion,EsqueletoPlayer,Player,GestorGirarrodillos}.java`,
`maquina_estado/{menu_inicial,musica,tienda,creditos}/*`, `AudioPlayer.java`, `Constantes.java`; nuevos: paquete `main.motor` y `main.ui`.
