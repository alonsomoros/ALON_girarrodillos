package main.motor;

import java.util.Random;

/**
 * Sacudida de pantalla (screen shake): desplaza todo lo que se dibuja unos píxeles, con una fuerza que decae hasta 0.
 * Es global (una sola pantalla): cualquier parte del juego llama a {@link #iniciar(int, int)} y
 * SuperficieDeDibujo aplica {@link #desplazamientoX()} / {@link #desplazamientoY()} antes de pintar el estado.
 */
public final class Sacudida {

    private static final Random AZAR = new Random();

    private static int intensidadPx = 0;
    private static int duracionTicks = 0;
    private static long inicio = 0;
    private static int dx = 0;
    private static int dy = 0;

    /** Multiplicador de la opción "reducir efectos" (1 = normal, 0 = sin sacudidas). */
    private static volatile double escala = 1.0;

    private Sacudida() {
    }

    /**
     * Empieza una sacudida. Si ya había una en curso se queda la más fuerte de las dos.
     *
     * @param intensidadPx desplazamiento máximo en píxeles al principio
     * @param duracionMs   cuánto tarda en apagarse
     */
    public static void iniciar(final int intensidadPx, final int duracionMs) {
        int fuerza = (int) Math.round(intensidadPx * escala);
        if (fuerza <= 0 || duracionMs <= 0) {
            return;
        }
        if (amplitudActual() < fuerza) {
            Sacudida.intensidadPx = fuerza;
            Sacudida.duracionTicks = Math.max(1, Tiempo.msATicks(duracionMs));
            Sacudida.inicio = Tiempo.ahora();
        }
    }

    /** Fuerza actual en píxeles: cae de forma cuadrática hasta 0 al acabar la duración. */
    public static double amplitudActual() {
        if (intensidadPx == 0) {
            return 0;
        }
        double t = (Tiempo.ahora() - inicio) / (double) duracionTicks;
        if (t >= 1) {
            return 0;
        }
        double resto = 1 - Math.max(0, t);
        return intensidadPx * resto * resto;
    }

    /** Recalcula el desplazamiento de este tick (lo llama el bucle una vez por actualización). */
    public static void actualizar() {
        double amplitud = amplitudActual();
        if (amplitud <= 0) {
            intensidadPx = 0;
            dx = 0;
            dy = 0;
            return;
        }
        dx = (int) Math.round((AZAR.nextDouble() * 2 - 1) * amplitud);
        dy = (int) Math.round((AZAR.nextDouble() * 2 - 1) * amplitud);
    }

    public static int desplazamientoX() {
        return dx;
    }

    public static int desplazamientoY() {
        return dy;
    }

    public static boolean activa() {
        return dx != 0 || dy != 0;
    }

    public static void setEscala(final double nuevaEscala) {
        escala = Math.max(0, Math.min(1, nuevaEscala));
    }

    /** Para las pruebas y para cortar una sacudida. */
    public static void detener() {
        intensidadPx = 0;
        dx = 0;
        dy = 0;
    }
}
