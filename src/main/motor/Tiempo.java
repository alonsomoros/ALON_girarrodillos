package main.motor;

/**
 * Reloj del juego medido en "ticks": uno por cada actualización del bucle principal (60 por segundo).
 * Las animaciones y efectos cuentan ticks en vez de milisegundos reales para ir siempre al ritmo de la lógica.
 */
public class Tiempo {

    public static final int TICKS_POR_SEGUNDO = 60;

    private static volatile long ticks = 0;

    private Tiempo() {
    }

    /** Lo llama el bucle principal una vez por actualización. */
    public static void avanzar() {
        ticks++;
    }

    public static long ahora() {
        return ticks;
    }

    /** Milisegundos a ticks (redondeado; cualquier duración positiva dura al menos 1 tick). */
    public static int msATicks(final long ms) {
        if (ms <= 0) {
            return 0;
        }
        return (int) Math.max(1, Math.round(ms * TICKS_POR_SEGUNDO / 1000.0));
    }

    public static long ticksAMs(final long ticks) {
        return Math.round(ticks * 1000.0 / TICKS_POR_SEGUNDO);
    }
}
