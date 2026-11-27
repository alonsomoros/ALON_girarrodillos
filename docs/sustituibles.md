# Registro de elementos "en crudo" y provisionales (candidatos a sprite o sonido propio)

Aquí se apunta **todo lo que el juego dibuja con código** (rectángulos, círculos, texto, efectos) o suena con audio generado,
para decidir uno a uno si se **sustituye por un sprite o sonido hecho a mano**. Regla del proyecto: cada elemento nuevo que
se dibuje "en crudo" se añade a este registro en el mismo commit que lo crea.

- **Estado:** `crudo` = dibujado con código; `provisional` = ya lee tu archivo si existe y, si no, usa uno generado;
  `listo` = ya es arte tuyo.
- **Decisión:** `por decidir` / `sí, sustituir` / `no, se queda en código`.
- Para los que son `provisional`, basta con dejar el archivo con el nombre indicado y se usa solo.

## Arte con sustitución ya prevista

| Elemento | Dónde | Archivo que lo sustituye | Estado | Decisión |
|----------|-------|--------------------------|--------|----------|
| Lámpara de turno (3 frames) | `LamparasTurno` | `rec/sprites/efectos/lampara_3f.png` (24x32 por frame: apagada, encendida, brillo) | provisional | sí, sustituir |
| Gema de vida (2 frames) | `GemasVida` | `rec/sprites/efectos/vida_gema_2f.png` (llena, vacía) | provisional | sí, sustituir |
| Bastión de la IA (5 alturas) | `Bastion` | `rec/sprites/bastion/bastion_ia_1.png` a `bastion_ia_5.png` (355x160, ya orientado hacia abajo) | provisional (usa el del jugador volteado) | sí, sustituir |
| Impacto de golpe | `Impacto` | `rec/sprites/efectos/impacto_5f.png` (64x64 por frame, 5 frames) | provisional (anillo con chispas) | sí, sustituir |
| Brillo de activador listo | `Activador` | Sprite de brillo o activador con frame "listo" | crudo (óvalo dorado que late) | por decidir |

## Sonidos provisionales (generados con código)

| Sonido | Archivo | Cuándo suena |
|--------|---------|--------------|
| Giro del rodillo | `rec/sonidos/giro.wav` | Al empezar a girar (solo el rodillo del jugador) |
| "Tic" de frenada | `rec/sonidos/tic.wav` | Cada vez que un canal para |
| Fijar canal | `rec/sonidos/fijar.wav` | Al fijar un canal |
| Ataque por héroe | `rec/sonidos/ataque_arquero.wav`, `_asesino`, `_caballero`, `_ingeniero`, `_mago`, `_sacerdote` | Al salir el ataque de ese héroe |
| Bastión sube | `rec/sonidos/bastion_sube.wav` | Al subir el bastión |
| Bastión se rompe | `rec/sonidos/bastion_rompe.wav` | Al bajar el bastión |
| Impacto | `rec/sonidos/impacto.wav` | Al golpear (muro, corona, rival) |
| Genéricos del juego original | `ataque.wav`, `boton.wav`, `victoria.wav`, `derrota.wav` | Ya existentes |

## Dibujado en crudo en el tablero (partida)

| Elemento | Dónde | Cómo se dibuja ahora | Posible sustituto | Estado | Decisión |
|----------|-------|----------------------|-------------------|--------|----------|
| Puntero del rodillo | `Rodillo.dibujarPuntero` | Triángulo con pulso | Sprite `puntero_2f.png` | crudo | por decidir |
| Destello al frenar un canal | `Rodillo.dibujarDestello` | Rectángulo blanco semitransparente | Brillo del marco | crudo | por decidir |
| Difuminado del giro | `Rodillo.dibujarGirando` | Copias semitransparentes de los símbolos | Variantes `<elemento>_blur.png` | crudo | por decidir |
| Números flotantes (daño, cura, bastión) | `TextoFlotante` | Texto con sombra | Fuente bitmap (7.4) | crudo | sí, con la fuente bitmap |
| Vida de la corona (dígitos) | `Player/IA.dibujarVida` | Texto | Dígitos de la fuente bitmap o sprite | crudo | por decidir |
| Brillo rojo de la corona al recibir daño | `EsqueletoPlayer` | Óvalo rojo semitransparente | Sprite de destello | crudo | por decidir |
| Grietas de la corona con poca vida | `EsqueletoPlayer` | Líneas oscuras | Sprite de corona agrietada | crudo | por decidir |
| Escombros y polvo del bastión | `Bastion` | Cuadrados y círculos grises | Sprites `escombro_Nx.png` / `polvo_4f.png` | crudo | por decidir |
| Estelas de proyectiles | `Ataque.dibujarEstela` | Copias semitransparentes del sprite | Sprites de estela por héroe | crudo | por decidir |
| Brasas del fuego del Mago | `Ataque.dibujarEstela` | Círculos naranjas | Partículas propias | crudo | por decidir |
| Tajo del Caballero | `Ataque.dibujarTajo` | Arco blanco | `tajo_4f.png` | crudo | por decidir |
| Halo del Sacerdote | `Ataque.dibujarPaso` | Óvalo dorado | Sprite de halo | crudo | por decidir |
| Destello blanco del golpe | `Ataque.dibujarPaso` | Círculo blanco suave bajo el impacto | Se queda o se quita al tener `impacto_5f.png` | crudo | por decidir |
| Triángulos de elección de héroe | `GestorGirarrodillos` | Triángulos | Flechas dibujadas (8.4) | crudo | sí, con 8.4 |
| Texto de stats de las figuras | `Figura.dibujarStats` | Texto sobre una imagen | Fuente bitmap | crudo | sí, con la fuente bitmap |
| Fondo de pantalla de victoria/derrota | `GestorGirarrodillos.dibujarFinDePartida` | Rectángulo negro y texto con sombra | Banner con estrellas (6.20) | crudo | sí, con 6.20 |

## Dibujado en crudo en menús y pantallas

| Elemento | Dónde | Cómo se dibuja ahora | Posible sustituto | Estado | Decisión |
|----------|-------|----------------------|-------------------|--------|----------|
| Título "Girarrodillos" | `GestorMenuPrincipal` | Rectángulo + 2 semicírculos + texto con sombra | `rec/ui/logo.png` (~515x91) | crudo | sí (7.11) |
| Etiquetas del menú (Jugar, Tienda...) | `GestorMenuPrincipal` / etiquetas | Texto sobre el fondo | Paneles 9-slice (7.5, 7.6) | crudo | por decidir |
| Copyright del menú | `GestorMenuPrincipal` | Texto | Fuente bitmap | crudo | por decidir |
| Título "TIENDA" y monedas | `GestorTienda` | Texto con sombra | Rótulo + icono de moneda (7.9) | crudo | por decidir |
| Filas de la tienda (rodillos) | `GestorTienda` | Rectángulo semitransparente con borde | Panel 9-slice (7.6) | crudo | sí (8.8) |
| Texto de ayuda ("Enter: volver...") | varias pantallas | Texto | Iconos de teclas (8.5) | crudo | sí (8.5) |
| Texto de música y créditos | `GestorMenuMusica`, `GestorCreditos` | Texto | Fuente bitmap | crudo | por decidir |
| Cursor del ratón | `Raton` | Imagen con rotación | Cursor de 2 estados (7.8) | crudo | sí (7.8) |
| Fundido a negro | `Fundido` | Rectángulo negro | — | crudo | no, se queda en código |

## Solo herramientas de desarrollo (no se sustituyen)
Galería de efectos (`GestorGaleria`, `DemoEasing`, `DemoSecuencia`) y el contador de objetos dibujados del menú en modo depuración.

## Cómo se usa
1. Antes de empezar cada fase, mira esta tabla y dime qué quieres dibujar tú.
2. Para lo `provisional`, deja tu archivo con el nombre indicado.
3. Para lo `crudo` que quieras sustituir, dímelo y creamos la tarea (se enlaza con la 7.x o la 8.x que corresponda).
