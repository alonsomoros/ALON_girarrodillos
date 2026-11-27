# Mapa de animaciones de ataque (borrador para revisar)

Este documento fija **cómo se anima cada figura** antes de programar nada (tareas 6.7 a 6.10 de PLAN_ESTETICA.md).
La lógica del juego (daños, bastión, IA) **no cambia**; solo cambia el orden en que se ve.

## 1. Las 10 trampillas

Posiciones aproximadas (centro, en píxeles del juego 1280x640), medidas sobre una captura real:

```
 (1) 415,215        (2) 553,185   (3) 685,185        (4) 830,215
                         [corona IA, arriba]
              (5) 473,300                (6) 770,300
                         [corona jugador, abajo]
 (7) 415,390        (8) 553,430   (9) 685,430        (10) 833,393
```

| Tipo | Trampillas | Quién la usa |
|------|-----------|--------------|
| Esquina (peana de cada figura) | 1, 4, 7, 10 | Salida del ataque de la figura 1, 2, 3, 4. También "figura rival" para el Asesino y "compañero" para el Sacerdote |
| Corona | 2 y 3 (arriba), 8 y 9 (abajo) | Impacto en la corona. Sacerdote: aparece en la de su propia corona |
| Centro (diagonales) | 5 y 6 | Punto de rebote/escondite cuando un muro bloquea el ataque |

Los tres puntos que ya usa el código (`ATAQUE_1..4`) **coinciden con estas trampillas**:

| Figura | Hoy (esquina, centro, corona) | Trampillas |
|--------|-------------------------------|-----------|
| 1 (arriba izq.) | (385,170) (442,268) (528,395) | 1 → 5 → 8 |
| 2 (arriba der.) | (798,170) (738,260) (653,385) | 4 → 6 → 9 |
| 3 (abajo izq.) | (385,350) (445,262) (527,148) | 7 → 5 → 2 |
| 4 (abajo der.) | (802,345) (740,255) (655,140) | 10 → 6 → 3 |

(Las coordenadas del código son la esquina del sprite de 55x55; el centro es +27.)
La depresión grande de la derecha (1058,393) **no es una trampilla**: es el hueco del activador de la figura derecha aún sin elegir. No se usa.

## 2. Piezas de animación (vocabulario)

Cada ataque se compone de pasos reutilizables, encadenados con `Secuencia`:

| Paso | Qué se ve | Duración base |
|------|-----------|---------------|
| `SALIR(trampilla)` | El objeto sube desde el agujero (escala 0→1 con rebote, la trampilla se oscurece) | 250 ms |
| `VIAJAR(a, b)` | Se desliza de una trampilla a otra con ease, rotado hacia el destino | 350 ms |
| `ESCONDER(trampilla)` | Baja al agujero (escala 1→0) | 200 ms |
| `GOLPE(trampilla)` | Sacudida pequeña + destello; sin partículas (decisión tuya) | 150 ms |
| `ESPERAR(ms)` | Pausa | variable |

El **efecto real** (bajar bastión, quitar vida, retrasar activador...) se aplica al terminar el paso
`GOLPE`/`SALIR` correspondiente, no al final de todo. El tiempo total de un ataque sigue siendo ≈ 2 s.

## 3. Flujo por figura

Leyenda: **E** = trampilla de esquina de la propia figura; **C** = trampilla central de su diagonal (5 o 6);
**K** = trampilla de la corona enemiga de su diagonal; **Kp** = corona propia; **Ee** = esquina de la figura rival;
**Ec** = esquina del compañero.

### Caballero (golpe normal: stat1 corona / stat2 bastión)
- **Sin muro rival:** SALIR(E) → VIAJAR(E→C) → VIAJAR(C→K) → GOLPE(K) [daño corona] → ESCONDER(K).
- **Con muro rival:** SALIR(E) → VIAJAR(E→C) → GOLPE(C) [el muro frena, baja stat2] → ESCONDER(C).
  (Tu escenario: rebota en el muro y se esconde por la trampilla central.)

### Arquero (a 3 de altura)
- **Muro de 3 o más:** igual que el Caballero con muro (se esconde en C, baja 3 al muro).
- **Muro menor de 3 o sin muro:** pasa por encima: SALIR(E) → VIAJAR(E→C) → VIAJAR(C→K) → GOLPE(K) → ESCONDER(K).
- Proyectil: flecha rotada, con estela de difuminado.

### Ingeniero (golpe normal + eleva SU bastión en 2)
1. Primero el martillo: SALIR(Kp, trampilla de SU lado de la corona propia) → el bastión propio sube +2 con animación → ESCONDER(Kp).
2. Después el ataque, igual que el Caballero (con muro: se esconde en C; sin muro: llega a la corona enemiga K).

### Asesino (ignora muros; retrasa a la figura rival con menos energía pendiente)
1. SALIR(Ee de la figura rival más cerca de actuar) → GOLPE → su activador **sube** (se retrasa) → ESCONDER(Ee).
2. SALIR(K) (aparece **directamente** en la corona enemiga, sin viajar) → GOLPE(K) [daño stat2] → ESCONDER(K).
- Cambio de orden respecto al código actual: hoy se daña la corona y después se retrasa; lo animaré en el orden que describes
  (la lógica es la misma; solo cambia cuándo se ve cada efecto).

### Sacerdote (cura a su corona y da energía al compañero; no ataca)
1. SALIR(Kp) (en **su propia** corona) → curación visible (+N verde, **se muestra siempre**, aunque la vida esté al máximo) → ESCONDER(Kp).
2. **Actúa el último:** espera a que terminen todas las demás acciones de la ronda. Si el compañero también se activa ese turno,
   el compañero ataca primero, sube +2 de experiencia y se reinicia su activador; solo entonces el Sacerdote le ayuda.
3. SALIR(Ec) (esquina del compañero) → su activador **baja** → ESCONDER(Ec).
- **Cambio de lógica (hay que tocar código, no solo animación):** hoy el Sacerdote y su compañero atacan a la vez, y la ayuda
  puede aplicarse antes de que se reinicie el activador del compañero (se pierde). Con la regla nueva, el Sacerdote se lanza
  cuando nadie más de su equipo está atacando. Irá con pruebas propias (tarea 6.8b).

### Mago (2 ataques)
- Bola 1: como el Caballero. Pega en el muro **o** en la corona (con muro: se esconde en C; sin muro: llega a K) y se esconde.
- Bola 2: ignora cualquier muro: SALIR(E) → VIAJAR hasta K (pasando por C sin chocar) → GOLPE(K) → ESCONDER(K).
- Entre las dos bolas: pausa corta (200 ms).

### Bomba (héroe de oro con barra llena)
- Carga corta en su peana (halo) → SALIR(E) → viaja a K → GOLPE fuerte (sacudida grande) → ESCONDER. Sin pantalla oscura: se prueba solo con el golpe y se añade si hace falta.
- Ignora muros, 2 de daño directo.

## 4. Rodillo: brillo, difuminado y aire (decisión)

Sí: el efecto correcto es **difuminado vertical + brillo**, no sprites nuevos. Se hace con código, sin shaders:
- **Difuminado de movimiento:** mientras gira, cada símbolo se dibuja 3 veces (con transparencia 100 %, 60 %, 30 %) separado
  unos píxeles hacia arriba (el "rastro"); al frenar el rastro se reduce a 0.
- **Brillo:** al parar cada canal, un destello blanco semitransparente sobre su marco durante ~150 ms.
- **Aire:** líneas finas verticales claras que cruzan el canal mientras gira (efecto "viento"), opcionales.
- Todo se pinta con `Graphics2D` (composición alfa), sin librerías.

## 5. Pendiente
1. Cuando las dos figuras de un equipo se activan a la vez (sin Sacerdote), ¿animan **a la vez** (como hoy) o una detrás de otra?
   Propuesta: a la vez, para no alargar el turno.
2. ¿Las animaciones de ambos jugadores son iguales y espejadas? El código ya espeja por posición.
