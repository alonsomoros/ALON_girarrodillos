package main.motor;

import main.Constantes;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Cubre la pantalla con un color que sube (fundido a negro) o baja (fundido desde negro) de opacidad.
 * Se añade a la capa FRENTE de un {@link GestorEfectos}.
 */
public class Fundido extends EfectoTemporal {

    private final boolean haciaNegro;
    private final Color color;

    public Fundido(final int duracionMs, final boolean haciaNegro) {
        this(duracionMs, haciaNegro, Color.BLACK);
    }

    public Fundido(final int duracionMs, final boolean haciaNegro, final Color color) {
        super(duracionMs);
        this.haciaNegro = haciaNegro;
        this.color = color;
    }

    /** Opacidad 0 a 255 en este momento. */
    int opacidad() {
        double p = Interpolador.ENTRADA_SALIDA.aplicar(progreso());
        return (int) Math.round(255 * (haciaNegro ? p : 1 - p));
    }

    @Override
    public void dibujar(final Graphics graficos) {
        int alfa = opacidad();
        if (alfa <= 0) {
            return;
        }
        graficos.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alfa));
        graficos.fillRect(0, 0, Constantes.ANCHO_JUEGO, Constantes.ALTO_JUEGO);
    }
}
