package main.motor;

/**
 * Funciones de suavizado (easing): convierten el avance lineal t (0 a 1) en un avance con "carácter".
 * Todas valen 0 en t=0 y 1 en t=1; las que rebotan o se pasan (RETROCESO, ELASTICO) superan 1 durante el recorrido.
 */
public final class Interpolador {

    /** Una curva de suavizado: recibe t en [0, 1] y devuelve el avance suavizado. */
    public interface Easing {
        double aplicar(double t);
    }

    public static final Easing LINEAL = t -> t;
    public static final Easing ENTRADA = t -> t * t;                       // Arranca lento
    public static final Easing SALIDA = t -> 1 - (1 - t) * (1 - t);        // Frena al final
    public static final Easing ENTRADA_SALIDA = t -> t < 0.5 ? 2 * t * t : 1 - Math.pow(-2 * t + 2, 2) / 2;

    /** Se pasa un poco del destino y vuelve (ideal para que algo "caiga" en su sitio). */
    public static final Easing RETROCESO = t -> {
        final double c1 = 1.70158;
        final double c3 = c1 + 1;
        return 1 + c3 * Math.pow(t - 1, 3) + c1 * Math.pow(t - 1, 2);
    };

    /** Rebota contra el destino como una pelota. */
    public static final Easing REBOTE = t -> {
        final double n1 = 7.5625;
        final double d1 = 2.75;
        if (t < 1 / d1) {
            return n1 * t * t;
        } else if (t < 2 / d1) {
            t -= 1.5 / d1;
            return n1 * t * t + 0.75;
        } else if (t < 2.5 / d1) {
            t -= 2.25 / d1;
            return n1 * t * t + 0.9375;
        }
        t -= 2.625 / d1;
        return n1 * t * t + 0.984375;
    };

    /** Oscila como un muelle antes de asentarse. */
    public static final Easing ELASTICO = t -> {
        if (t == 0 || t == 1) {
            return t;
        }
        final double c4 = (2 * Math.PI) / 3;
        return Math.pow(2, -10 * t) * Math.sin((t * 10 - 0.75) * c4) + 1;
    };

    private Interpolador() {
    }

    /** Limita t a [0, 1]. */
    public static double limitar(final double t) {
        return t < 0 ? 0 : (t > 1 ? 1 : t);
    }

    /** Valor entre desde y hasta según t (0 a 1) con la curva dada. */
    public static double interpolar(final double desde, final double hasta, final double t, final Easing easing) {
        return desde + (hasta - desde) * easing.aplicar(limitar(t));
    }

    public static int interpolar(final int desde, final int hasta, final double t, final Easing easing) {
        return (int) Math.round(interpolar((double) desde, (double) hasta, t, easing));
    }
}
