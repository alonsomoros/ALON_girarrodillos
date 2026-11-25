# Plan de depuración y cierre de Girarrodillos

## Context

Proyecto Java (Swing/AWT + `javax.sound`) empezado en una etapa temprana de aprendizaje. La auditoría detectó:
bugs de lógica (rodillo `static` compartido entre jugador e IA, `instanceof` equivocado, ataques huérfanos),
un gran problema de rendimiento (imágenes releídas del disco en cada frame), código repetido, recursos que
no existen y un juego sin final ni IA real. **Objetivo:** corregirlo todo sin cambiar la arquitectura
(estados, figuras, rodillo) y sin librerías nuevas (solo JDK 21), en tareas pequeñas, de un commit cada una,
que se van tachando en un `PLAN.md` dentro del repo.

## Reglas de trabajo (aplican a TODAS las tareas)

1. **1 tarea = 1 commit.** Mensaje: `fase.tarea: título corto` (ej. `1.2: conectar CargadorRecursos a la caché`).
2. El commit que termina una tarea **también tacha su casilla** en `PLAN.md` (`- [x]`).
3. Antes de cada commit: el proyecto **compila** y arranca (ver 0.2). Si la tarea toca gameplay, se prueba a mano lo indicado en "Verif.".
4. Trailer de los commits: `Co-Authored-By: Claude Sonnet 5.5 <noreply@anthropic.com>`.
5. Una rama por fase (`fase-1-rendimiento`, …) y merge a `main` al terminar la fase. **Push solo al cerrar fase, con tu OK.**
6. Una tarea no mezcla refactor y cambio de comportamiento. Si aparece algo nuevo, se apunta en la sección "Hallazgos nuevos" al final de `PLAN.md`.
7. Lo que dependa de un archivo que solo tú puedes aportar (fuente `.ttf`, `.wav`, sonidos) se marca 👤 y no bloquea el resto.

## Decisiones tomadas por defecto (dime si alguna no te vale)

| # | Tema | Valor por defecto |
|---|------|-------------------|
| D1 | Regla de energía | Se **mantiene el código** (suma de valores de los símbolos ≥3 → `suma-2`) y se corrige `Instrucciones.txt` para que lo describa. No se cambia el gameplay. |
| D2 | Daño al bastión (Caballero/Ingeniero/Mago) | `bastion.disminuirAltura(stat2)` (hoy `altura - stat2`, que sube el bastión si stat2 > altura). |
| D3 | Mago | 2 ataques seguidos: 1.º a nivel de suelo (lo frena el bastión), 2.º ignora el bastión (según `Instrucciones.txt`). |
| D4 | Ataque especial ("bomba") | 2 de daño directo a la corona, ignora bastión, termina y limpia el estado. |
| D5 | Rodillo de la IA | Mismo `R2_Prob` en los 5 canales; fijado con heurística simple (fija símbolos de valor ≥2 del tipo de su héroe). |
| D6 | Música | Solo `.wav` (el `.mp3` no lo lee `javax.sound`). 👤 Convertir `TheMountainTrail`, `ChillyNight`, `Courage` a `.wav` (Audacity/ffmpeg). |
| D7 | Tienda | Fase 4 propone un diseño mínimo en 1 página y tú lo apruebas antes de implementar. |

---

## FASE 0 — Preparación (sin tocar el juego)

- [x] **0.1** Copiar este plan a `PLAN.md` en la raíz del repo. *Commit:* `0.1: añadir PLAN.md`
- [x] **0.2** Crear `scripts/compilar.bat` y `scripts/ejecutar.bat` que usen el JDK 21 (el `java` del PATH es 1.8; localizar la ruta de Corretto 21 usada por IntelliJ) y compilen `src/**/*.java` a `out/production/Girarodillo`, ejecutando desde la raíz (las rutas `rec/...` son relativas). *Verif.:* `scripts\compilar.bat` termina sin errores y `scripts\ejecutar.bat` abre la ventana.
- [x] **0.3** `.gitignore`: añadir `out/`, `*.class`, `desktop.ini`, `rec/musica/OST.zip`; `git rm --cached` de los `desktop.ini` ya subidos.
- [x] **0.4** Quitar `import com.sun.source.tree.BreakTree;` de `control/Etiqueta.java` (import del compilador, sin uso). *Verif.:* compila.
- [x] **0.5** Línea base: ejecutar el juego y anotar en `PLAN.md > Hallazgos` qué se ve/rompe hoy (FPS reales, CPU del proceso, errores de consola, qué estados abren). Solo documentación.
- [x] **0.6** Crear `README.md` mínimo: requisitos (JDK 21), cómo compilar/ejecutar, controles (W/A/S/D, E, Espacio, Enter, Esc).

## FASE 1 — Rendimiento, recursos y bugs de UI/audio  (rama `fase-1-rendimiento`)

### 1A. Caché de imágenes (el mayor cuello de botella)
- [x] **1.1** Crear `herramientas/CacheImagenes.java`: `Map<String,BufferedImage>`, métodos `obtener(ruta, Transparency)`, `precargar(String...)`, `limpiar()`, y un `boolean activa` configurable. Sin usarla aún.
- [x] **1.2** `CargadorRecursos.cargarImagenCompatibleOpaca/Translucida` consultan la caché antes de leer del disco. *Verif.:* el juego se ve igual; en consola ya no hay lecturas repetidas.
- [x] **1.3** Borrar el bucle de píxeles inútil de `cargarImagenCompatibleTranslucida` y el método `tieneVecinoOpaco`.
- [x] **1.4** Archivo de imagen inexistente: dejar de lanzar `NullPointerException`; devolver una imagen "placeholder" (32×32 magenta) y avisar **una sola vez** por ruta.
- [x] **1.5** `Dibujador.IMAGEN.dibujarRotado`: cachear la imagen volteada (mapa por `Image`) en vez de recalcularla cada frame.
- [x] **1.6** Precargar en el arranque lo que se usa en partida (rodillo, bases, activadores, bastiones, tablero) desde `GestorPrincipal` usando `CacheImagenes.precargar`.

### 1B. Depuración y consola
- [x] **1.7** Crear `herramientas/Depurar.java` (`static boolean ACTIVO`, `log(String)`).
- [x] **1.8** Pasar a `Depurar.log` (o borrar) los `System.out.println` por frame/evento: `Ataque.actualizar/dibujar`, `Figura.atacarNormal`, `GestorEstados`, `GestorGirarrodillos` ("Turno"), FPS en `GestorPrincipal`.
- [x] **1.9** Los textos de depuración en pantalla (`Raton.dibujar`: "Raton x…", "Click Izquierdo"; contador de objetos en `GestorMenuPrincipal`) solo si `Depurar.ACTIVO`.

### 1C. Bucle principal e hilos
- [x] **1.10** `GestorPrincipal.run()`: limitar el dibujado (dormir con `Thread.sleep` el sobrante del frame); `enFuncionamiento` pasa a `volatile`. *Verif.:* CPU del juego baja claramente vs. línea base (0.5).
- [x] **1.11** `Tecla`: `pulsada/procesada/ultimaPulsacion` → `volatile` (se escriben en el hilo de Swing y se leen en el del juego).
- [x] **1.12** Sustituir `java.util.Timer` del parpadeo de la luz (`GestorGirarrodillos`) por un contador de frames en `actualizar()`; eliminar `crearParpadeoLuz` y el hilo.

### 1D. Ratón y menús
- [x] **1.13** `Raton` duplicado: `SuperficieDeDibujo` hace `new Raton(this)` y `Raton.getInstance()` crea otro → un único `Raton` (que `getInstance` devuelva el de la superficie).
- [x] **1.14** `GestorEtiquetas.getInstance()` devuelve `new` cada vez (nunca guarda `instance`): corregir singleton.
- [x] **1.15** Un clic sobre una etiqueta del menú principal cambia de estado **una vez** (llamar a `Raton.reiniciarClickIzquierdo()` tras actuar); igual en menú de música. Hoy dispara cada frame mientras se mantiene.
- [x] **1.16** `Etiqueta` mide texto con `SuperficieDeDibujo.graficos2D` (Graphics guardado de por vida). Medir con un `BufferedImage` 1×1 propio en `MedidorString`; así se puede borrar `inicializarGraficos()` en la Fase 3.

### 1E. Audio
- [x] **1.17** `AudioPlayer`: inicializar `clips = new HashMap<>()` (hoy `null` → NPE en `preloadSound`).
- [x] **1.18** `AudioPlayer.stopSound`: corregir el orden (`stop()` luego `close()`) y no exigir `isRunning()` para cerrar.
- [x] **1.19** `AudioPlayer.playSoundOnce`: cerrar el `Clip` al terminar con un `LineListener` (hoy fuga de líneas de audio).
- [x] **1.20** (hecho con ffmpeg) Convertir los 3 `.mp3` a `.wav` y subirlos (D6).
- [x] **1.21** `Constantes.RUTAS.MUSICA`: apuntar solo a archivos que existen; `CREDITOS` (`Moorlands.wav`, inexistente) → elegir una pista real; `rec/sonidos/boton.wav` → quitar la constante hasta que haya sonido (se retoma en 4.x).
- [x] **1.22** `GestorEtiquetas.initEtiquetasMenuMusica`: tolerar que `rec/musica` no exista o esté vacío (hoy `listFiles()` puede ser `null`) y que el menú muestre solo pistas `.wav` válidas.

### 1F. Fuentes
- [x] **1.23** `CargadorRecursos.cargarFuente(ruta)` real con `Font.createFont` + `registerFont`, con *fallback* a `Font.SANS_SERIF` si falta el `.ttf`.
- [ ] **1.24** 👤 Aportar `.ttf` (p. ej. Pixelify Sans, licencia OFL) en `rec/fuentes/`; `Constantes.FUENTES` pasa a cargarla en lugar de "Algerian"/"AniMe Matrix" del sistema. *Verif.:* el texto se ve igual en otro PC.

**Cierre de fase 1:** merge a `main` + push (con tu OK). Re-medir FPS/CPU y anotarlo en `PLAN.md`.

---

## FASE 2 — Bugs de lógica de juego  (rama `fase-2-logica`)

### 2A. Red de seguridad mínima (sin librerías)
- [x] **2.1** Crear `test/` con una clase `PruebasLogica.java` con `main` y `assert` (se ejecuta con `java -ea`). Añadir `scripts/probar.bat`. Sin JUnit.

### 2B. Rodillo
- [x] **2.2** `EsqueletoPlayer.rodillo` deja de ser `static` (y se quita `this.rodillo = null` del constructor). `getRodillo()` pasa a método de instancia; actualizar `Player.dibujar`, `IA`, `GestorGirarrodillos`.
- [x] **2.3** `GestorGirarrodillos`: sustituir `Player.getRodillo()/IA.getRodillo()` por `player.getRodillo()/ia.getRodillo()`. *Verif.:* jugador e IA muestran rodillos **distintos**.
- [x] **2.4** `IA` usa su propio rodillo definitivo (D5) y se retira el rodillo de prueba `RP_Cuadrado3…`.
- [x] **2.5** `Rodillo.girarRodillo`: usar `EnumMap`/orden fijo, normalizar probabilidades en el constructor y garantizar un valor por defecto si el acumulado no llega (nunca `null`). *Prueba (2.1):* 100 000 giros sin `null`.
- [x] **2.6** (verificado: ya existen todos los sprites; añadida la prueba que lo vigila) `Rodillo.girarRodillo`: no dejar que `lateral1/lateral2` contengan símbolos que no existen como sprite (revisar `roto.png`, `*Lateral.png` por cada `Elementos`). 

### 2C. Ataques
- [x] **2.7** `Ataque`: arranca su propio cronómetro en el constructor (`iniciar()`); desaparece `GestorAtaques.ejecutarAtaques()` y deja de reiniciar los ataques en curso.
- [x] **2.8** Eliminar la lista estática `GestorAtaques.ataques` (nunca se vaciaba).
- [x] **2.9** `EsqueletoPlayer.actualizarEstados`: el bloque de `figuraDerecha` comprobaba `figuraIzquierda instanceof Mago`. Extraer `lanzarAtaque(Figura f)` y usarlo para ambas. *Prueba:* Mago a la derecha ataca 2 veces; no-Mago a la derecha, 1.
- [x] **2.10** Mago (D3): la figura mantiene una **cola** de ataques (`Deque<Ataque>`); se animan en secuencia y cada uno aplica su efecto al terminar (el 1.º respeta bastión, el 2.º lo ignora).
- [x] **2.11** Ataque especial (D4): `ataqueEspecial.actualizar()` se invoca, termina, aplica 2 de daño directo y limpia `estaAtacando*`. *Verif.:* no queda sprite colgado en pantalla.
- [x] **2.19** *(hallazgo nuevo)* Los ataques golpeaban al **dueño** en vez de al enemigo (`Ataque.actualizar` pasaba `getDuenio()` como objetivo). Pasar `getEnemigo()`. *Prueba:* un ataque del jugador resta vida a la IA y no al jugador.
- [x] **2.12** `Figura.actualizar`: gestionar correctamente subida de nivel y reset de XP (XP 6 → nivel+1 → XP 0; en nivel 3 con XP 6 → bomba y XP 0). *Prueba (2.1).*

### 2D. Efectos de figura
- [x] **2.13** Daño al bastión (D2): Caballero, Ingeniero y Mago pasan a `disminuirAltura(stat2)`. *Prueba:* bastión 2 + stat2 3 → bastión 0 (nunca sube).
- [x] **2.14** `Figura.getCompaniero()` usa `playerDuenio` (nulo en la IA → NPE con Sacerdote IA): usar `getDuenio()`.
- [x] **2.15** Asesino: alinear efecto con `Instrucciones.txt` (retrasa al héroe rival al que menos le falta) o corregir el texto; decidir y anotar.
- [x] **2.16** `EsqueletoPlayer.terminadoDeProcesarAtaques`: se pone a `true` y no se resetea; reiniciarlo al empezar cada ronda.
- [x] **2.17** `Activador.aumentarAltura` usa `alturaDefault` como techo mientras `Figura.alturaDefault` es otro campo duplicado: dejar una única fuente.

### 2E. Reglas escritas
- [x] **2.18** `rec/texto/Instrucciones.txt`: reescribir la regla de energía (D1) y cualquier cosa que el código haga distinto tras 2.13/2.15.

**Cierre de fase 2:** `scripts/probar.bat` en verde + partida completa a mano. Merge + push (con tu OK).

---

## FASE 3 — Limpieza y refactor (mismo comportamiento)  (rama `fase-3-limpieza`)

> Cada tarea debe dejar el juego idéntico. Se comprueba con una partida corta y diff visual.

### 3A. Constructores y duplicación en Figura
- [x] **3.1** `Figura`: añadir `Figura(EsqueletoPlayer duenio)` (único); los 2 constructores viejos delegan en él.
- [x] **3.2** Migrar cada figura a un solo constructor (un commit por figura): Arquero, Mago, Caballero, Asesino, Ingeniero, Sacerdote → `3.2a…3.2f`.
- [x] **3.3** Eliminar los constructores `Player`/`IA` viejos y `playerDuenio/iaDuenio`.

### 3B. Tablas en vez de `switch` repetidos
- [x] **3.4** `Constantes.COORDENADAS`: añadir arrays indexados `BASE[]`, `FIGURA[]`, `ACTIVADOR[]`, `STATS[]`, `ATAQUE[][]` (índice = posición-1), sin quitar aún las constantes sueltas.
- [x] **3.5** `Figura.dibujar` y `atacarNormal/atacarEspecial` usan los arrays (adiós 4 `case` copiados).
- [x] **3.6** `Constantes.RUTAS`: arrays `BASES.CUADRADO[0..6]`, `BASES.ROMBO[0..6]`, `ACTIVADORES.CUADRADO/ROMBO[0..5]`, `BASTION.PLAYER/IA[1..5]`.
- [x] **3.7** `Figura.dibujarBase` → una línea con el array (≈50 líneas menos).
- [x] **3.8** `Activador.dibujarActivador` → array (≈45 líneas menos).
- [x] **3.9** `Bastion.dibujarBastion` → array; unificar la rama Player/IA con un parámetro.
- [x] **3.10** Borrar `IA.dibujarBastion` (duplicado) y las constantes sueltas ya sin uso.
- [x] **3.11** Resolver las rutas `bastion1..5.png` de la IA que no existen: reutilizar las del jugador (volteadas con 1.5) o añadir los PNG 👤.

### 3C. Enums y polimorfismo
- [x] **3.12** `Constantes.RODILLOS.Elementos` con campos (`tipo`, `valor`, `experiencia`) y `calcularSalidaRodillo` sin `switch` de Strings; `Rodillo` guarda `Elementos[]` en vez de `String[]`.
- [x] **3.13** Cada figura implementa `aplicarEfecto(Ataque)`; `GestorAtaques.procesarFinalAtaque` (switch por `getSimpleName()`) se reparte entre las clases y `GestorAtaques` se elimina.
- [x] **3.14** `GestorEstados`: `enum Estado {JUEGO, TIENDA, MUSICA, CREDITOS, MENU}` en lugar de índices (`cambiarEstadoActual(4)`) y de depender del `id` de `Etiqueta` en `GestorMenuPrincipal`.
- [x] **3.15** `GestorCreditos`: la cadena `if` de 6 tramos → `index = min(5, t / (duracion/6))` + array de fondos.

### 3D. Código muerto (un commit por elemento, comprobando con búsqueda que no se usa)
- [x] **3.16** `AtaquesFiguras` y los `atacar(Graphics)/atacarEspecial(Graphics)` que solo imprimen.
- [x] **3.17** `Bastion.actualizar()` vacío, `IA.dibujarRodillo/getFigurasDisponibles` si siguen sin uso.
- [x] **3.18** `SuperficieDeDibujo.inicializarGraficos()` y el campo `graficos2D` (tras 1.16).
- [x] **3.19** `Teclado.ultimaPulsacion*`, `INTERVALO_PULSACION` (si no se usan) y `Ventana.dibujar()`.
- [x] **3.20** `HojaSprites`, `Sprite`, `AnimationPanelSprites`, `CargadorRecursos.unzip/extractFile`, `GestorPrincipal` línea comentada del `unzip`: borrar si nadie los usa.
- [x] **3.21** Rodillos `RP_*` de prueba de `Constantes`.
- [x] **3.22** Renombrar `tieneBation` → `tieneBastion`, `Etiqueta` "WTF" y comentarios obsoletos ("Hay que hacer una clase IA").
- [x] **3.23** `Keyboard`: Esc cierra con `System.exit(0)` desde el hilo de eventos → `ventana.dispose()` + parar el bucle limpiamente.
- [x] **3.24** Imports con comodín (`java.awt.*`, `java.util.*`) → imports explícitos donde choquen nombres (`Timer`, `List`).

**Cierre de fase 3:** comparar líneas (esperado: −400/−500) y partida completa. Merge + push.

---

## FASE 4 — Terminar el juego  (rama `fase-4-cierre`)

### 4A. Fin de partida y reinicio
- [x] **4.1** `GestorGirarrodillos`: detectar `vida <= 0` en jugador o IA tras resolver los ataques.
- [x] **4.2** Estado/pantalla `FinPartida` (victoria/derrota): texto + fondo existente + "Enter para volver al menú".
- [x] **4.3** Quitar `static` de `player` e `ia` en `GestorGirarrodillos`; método `nuevaPartida()` que los recrea.
- [x] **4.4** El botón "Jugar" del menú llama a `nuevaPartida()`. *Verif.:* jugar 2 partidas seguidas sin reiniciar el programa.

### 4B. IA
- [x] **4.5** IA elige 2 héroes al azar (usar `seleccionarFigurasAleatoria`, ya existe) y fija sus posiciones 1 y 2.
- [x] **4.6** IA: fijado heurístico de canales entre tiradas (D5).
- [x] **4.7** Dibujar el bastión de la IA (hoy comentado en `IA.dibujar`).
- [ ] **4.8** Ajustar probabilidades de rodillo de IA para que sea batible pero no trivial (3 partidas de prueba, anotar resultado).

### 4C. Tienda y sonido
- [ ] **4.9** Documento de diseño mínimo de la tienda (1 página en `docs/tienda.md`): qué se compra, con qué moneda, dónde se guarda. *Se te enseña antes de seguir.*
- [ ] **4.10** Implementar la tienda según 4.9 (se subdividirá en tareas 4.10a… cuando esté aprobado el diseño).
- [ ] **4.11** 👤 Efectos de sonido (`boton.wav`, ataque, victoria/derrota) en `rec/sonidos/`.
- [ ] **4.12** Reproducirlos con `AudioPlayer.playSoundOnce` (click de botón, ataque, fin de partida).

### 4D. Pulido final
- [ ] **4.13** Actualizar `Instrucciones.txt` y `creditos.txt` con el estado real final.
- [ ] **4.14** Revisión final: `grep` de `println`, avisos del compilador, FPS/CPU final vs. línea base, README definitivo.
- [ ] **4.15** (Opcional) `scripts/empaquetar.bat` con `jar` de JDK para distribuir `.jar` + carpeta `rec/`.

**Cierre:** merge + push + etiqueta `v1.0`.

---

## Hallazgos nuevos
*(se rellena durante el trabajo)*

### Línea base (tarea 0.5, JDK 21, menú principal, 10 s)
- Arranca bien con `scripts\ejecutar.bat`; no hay errores en stderr.
- **FPS ≈ 700-720** (sin límite; el objetivo es 60) y **APS = 60**.
- **CPU ≈ 113 % de un núcleo** y **RAM ≈ 510 MB** solo en el menú (el bucle dibuja sin pausa y relee imágenes del disco).
- Medido solo en el menú; la partida no se pudo conducir sin teclado, así que se medirá a mano al cerrar la Fase 1.
- Compila con un aviso de operaciones `unchecked` en `EsqueletoPlayer` (arrays genéricos de `HashMap`).
- El `.gitignore` ya tenía `rec/musica/OST.zip` añadido a mano (sin commit) antes de 0.3.

### Resultados al cerrar la Fase 1 (JDK 21, menú y partida)
- Menú: **CPU 113 % → ~25 %**, FPS 700 → **57**, RAM ~510 MB → ~420-480 MB.
- Partida (jugador vs IA, conducida con `java.awt.Robot`): **CPU ~11 %, 57 FPS, sin errores**. Antes, una ejecución en partida daba **8 FPS** (visto en tu terminal de VS Code), así que la caché de imágenes era el cuello de botella.
- Confirmado a la vista el bug de la Fase 2: el rodillo de la IA y el del jugador muestran **exactamente los mismos símbolos** (rodillo `static` compartido).
- 1.20 se resolvió con `ffmpeg` (ya instalado en la máquina): `TheMountainTrail`, `ChillyNight` y `Courage` ahora existen como `.wav` (los `.mp3` se mantienen).
- 1.21: la música de créditos usa `PathOfAscension.wav` (la original `Moorlands.wav` no existe); cámbiala si prefieres otra.
- 1.19 quedó dentro del commit de 1.18 (se aplicaron juntas); el commit 1.19 solo tacha la casilla.
- Pendiente de ti: **1.24** (aportar el `.ttf`; hasta entonces las fuentes siguen siendo "Algerian" y "AniMe Matrix" del sistema).

### Resultados al cerrar la Fase 2
- **18 pruebas** en `scripts\probar.bat`, todas en verde (rodillos, ataques, bastión, Mago, bomba, nivel/XP, Sacerdote de la IA, Asesino, regla de energía).
- Partida completa con el juego real (jugador vs IA): 57 FPS, ~8 % de CPU, sin errores en consola.
- **Hallazgo grave (2.19):** los ataques golpeaban al propio dueño. `Ataque.actualizar` pasaba `getDuenio()` como objetivo, así que tus ataques te quitaban vida a ti y los de la IA se la quitaban a ella. Detectado al escribir la prueba de la bomba.
- 2.2 y 2.3 se hicieron en un solo commit (separarlos dejaba el proyecto sin compilar).
- 2.6: ya existían todos los sprites del rodillo; la tarea quedó como una prueba que lo vigila.
- 2.15: el Asesino ahora retrasa al héroe rival al que le queda menos energía (como dice `Instrucciones.txt`), y no al que tiene enfrente.
- 2.12: la subida de nivel se comprueba al llenarse la XP (también la que da el rodillo), no solo tras atacar.
- 2.18: la regla "3 del mismo tipo + 1" ya era coherente con el código si se entiende que un símbolo con número N cuenta como N. Solo se aclaró el texto.

### Resultados al cerrar la Fase 3
- Código en `src/`: **4268 → 3683 líneas** (la fase restó 871 y sumó 286). Las 18 pruebas siguen en verde y una partida real va a 57 FPS, ~8 % de CPU, sin errores.
- 3.2 y 3.3 se mezclaron en un commit (la migración de las figuras y el borrado de los constructores viejos).
- 3.14: las etiquetas del menú ya no dependen de su `id`; `GestorMenuPrincipal` tiene una tabla `DESTINOS` explícita (Jugar, Tienda, Musica, Creditos).
- 3.18: se mantuvo el buffer de 4 (comportamiento original); `inicializarBuffer()` solo lo crea.
- 3.20: se borró también `CargadorRecursos.unzip` (con su comentario en `GestorPrincipal`). Si lo necesitas para extraer `OST.zip`, está en el historial de git.
- 3.23: Esc ya no hace `System.exit` desde el hilo de eventos; pide el cierre al bucle, que libera audio y ventana.
- `Constantes.RODILLOS.R3_Prob`, `R4_Prob` y `R5_Prob` siguen sin usarse; se dejan por si la tienda los necesita (4.x).

## Verificación general

- **Por tarea:** `scripts\compilar.bat` sin errores → arrancar juego → comprobar lo indicado en "Verif.".
- **Lógica (Fase 2+):** `scripts\probar.bat` (`java -ea`) en verde.
- **Rendimiento:** comparar FPS/APS y uso de CPU del proceso `java` contra la línea base de 0.5.
- **Final:** una partida completa jugador vs IA con victoria, otra con derrota y reinicio; menú Música y Créditos sin errores en consola.

## Archivos críticos

`GestorPrincipal.java`, `AudioPlayer.java`, `Constantes.java`, `herramientas/CargadorRecursos.java`, `herramientas/Dibujador.java`,
`maquina_estado/GestorEstados.java`, `maquina_estado/juego/{GestorGirarrodillos,EsqueletoPlayer,Player,IA,Figura,Rodillo,Ataque,GestorAtaques,Activador,Bastion}.java`,
`maquina_estado/juego/figuras/*.java`, `control/{Raton,Tecla,Teclado,Etiqueta,GestorEtiquetas}.java`, `rec/texto/Instrucciones.txt`.
