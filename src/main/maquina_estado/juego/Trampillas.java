package main.maquina_estado.juego;

import java.awt.Point;

/**
 * Las 10 trampillas ovaladas del tablero, por donde salen y se esconden los ataques (ver docs/animaciones.md).
 * Los puntos son el centro de cada agujero en píxeles del juego. Posiciones de figura: 1 y 2 arriba (IA), 3 y 4 abajo (jugador).
 * <pre>
 *  (1) esquina arriba-izq   (2) corona arriba-izq   (3) corona arriba-der   (4) esquina arriba-der
 *                (5) centro izq                          (6) centro der
 *  (7) esquina abajo-izq    (8) corona abajo-izq    (9) corona abajo-der     (10) esquina abajo-der
 * </pre>
 */
public final class Trampillas {

    /** Índice 0 a 9 = trampillas 1 a 10. */
    private static final Point[] CENTROS = {
            new Point(415, 217), new Point(555, 187), new Point(686, 187), new Point(829, 215),
            new Point(470, 302), new Point(771, 302),
            new Point(413, 392), new Point(553, 432), new Point(686, 432), new Point(833, 395)
    };

    private Trampillas() {
    }

    private static Point de(final int numero) {
        return CENTROS[numero - 1];
    }

    private static int posicionValida(final int posicion) {
        return posicion < 1 || posicion > 4 ? 1 : posicion;
    }

    /** Trampilla junto a la peana de la figura. */
    public static Point esquina(final int posicion) {
        return de(new int[]{1, 4, 7, 10}[posicionValida(posicion) - 1]);
    }

    /** Trampilla central de la diagonal de la figura (donde rebota si el muro la frena). */
    public static Point centro(final int posicion) {
        return de(new int[]{5, 6, 5, 6}[posicionValida(posicion) - 1]);
    }

    /** Trampilla de la corona enemiga, en la diagonal de la figura. */
    public static Point coronaEnemiga(final int posicion) {
        return de(new int[]{8, 9, 2, 3}[posicionValida(posicion) - 1]);
    }

    /** Trampilla de la corona de su propio equipo, en el mismo lado que la figura. */
    public static Point coronaPropia(final int posicion) {
        return de(new int[]{2, 3, 8, 9}[posicionValida(posicion) - 1]);
    }
}
