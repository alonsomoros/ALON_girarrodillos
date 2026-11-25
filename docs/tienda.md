# Diseño mínimo de la tienda (propuesta, tarea 4.9)

**Estado:** pendiente de tu aprobación. Nada de esto está implementado.

## Idea en una frase
Ganas **monedas** al terminar partidas y las gastas en **rodillos mejores** para el jugador. Es la progresión natural
del juego: en `Constantes.RODILLOS` ya hay `R3_Prob`, `R4_Prob` y `R5_Prob` declarados (vacíos) pensados para esto.

## Moneda
| Resultado de la partida | Monedas |
|---|---|
| Victoria | +3 |
| Empate | +1 |
| Derrota | +1 |

El total se muestra arriba a la izquierda en la tienda y en la pantalla de fin de partida ("+3 monedas").

## Qué se vende (rodillos del jugador; los 5 canales usan la misma tabla)
| Rodillo | Precio | Carácter | Tabla |
|---|---|---|---|
| Rodillo 1 | gratis (inicial) | el actual: tiene símbolos rotos | `R1_Prob` (ya existe) |
| Rodillo 2 | 5 | igual de simple pero sin símbolos rotos | `R2_Prob` (ya existe) |
| Rodillo 3 | 10 | más símbolos de valor 2 y 3 y más fondos azules (EXP) | nueva `R3_Prob` |
| Rodillo 4 | 20 | muy ofensivo: muchos 2 y 3, bastante EXP | nueva `R4_Prob` |

Tablas nuevas propuestas (cada una suma exactamente 1; una prueba lo vigila):

- **R3:** cuadrado1 .10, rombo1 .10, martillo1 .10, cuadrado2 .12, rombo2 .12, martillo2 .08, cuadradoNivel1 .10,
  romboNivel1 .10, cuadradoNivel2 .05, romboNivel2 .05, cuadrado3 .04, rombo3 .04
- **R4:** cuadrado2 .14, rombo2 .14, martillo2 .10, cuadrado3 .10, rombo3 .10, martillo3 .06, cuadrado1 .06, rombo1 .06,
  cuadradoNivel1 .06, romboNivel1 .06, cuadradoNivel2 .06, romboNivel2 .06

`R5_Prob` queda sin usar (se podría vender más adelante).

## Dificultad
La IA sigue con `R2_Prob` siempre. Con el Rodillo 4 el jugador será claramente superior: es la recompensa.
*(Alternativa si prefieres: la IA usa el mismo rodillo que el jugador menos uno. Dímelo.)*

## Pantalla
Se reutiliza el fondo `rec/wallpapers/TiendaTM.png` y las `Etiqueta` del menú de música (misma mecánica de ratón):

- Una etiqueta por rodillo: `Rodillo 3 - 10 monedas`, `Rodillo 2 - EQUIPADO`, `Rodillo 1 - COMPRADO`.
- Clic: si no está comprado y alcanzan las monedas, se compra y se equipa; si ya está comprado, se equipa.
- Al pasar el ratón se muestra una línea con su carácter. Enter vuelve al menú (ya funciona).

## Guardado
Un archivo `guardado.properties` en la raíz (con `java.util.Properties`, sin librerías), ignorado por git:
`monedas`, `rodillosComprados` (lista) y `rodilloEquipado`. Si no existe, se parte de 0 monedas y Rodillo 1.

## Cómo se implementaría (tareas 4.10a a 4.10f)
1. `Progreso` (monedas, comprados, equipado) con carga y guardado.
2. Tablas R3 y R4 en `Constantes` + prueba de que suman 1.
3. `Player` usa el rodillo equipado.
4. `GestorTienda`: lista de etiquetas, compra y equipar.
5. Dar monedas al terminar la partida y mostrarlas en la pantalla de fin.
6. Pruebas: comprar sin monedas falla, comprar descuenta, equipar cambia el rodillo, guardar/cargar.

## Fuera de alcance
Vender héroes, varias monedas, estadísticas, desbloqueos por logros.
