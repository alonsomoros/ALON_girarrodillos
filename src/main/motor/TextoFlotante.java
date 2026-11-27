package main.motor;

import main.herramientas.Dibujador;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * Texto que sube y se desvanece (daño "-3", curación "+2"...). Se crea con {@link #mostrar}, que lo deja en el gestor de
 * efectos global; si no hay destino (pruebas, herramientas sin ventana) no hace nada.
 */
public class TextoFlotante extends EfectoTemporal {

    /** Dónde van los textos. Lo fija GestorEstados al arrancar. */
    private static volatile GestorEfectos destino;

    private final String texto;
    private final int x;
    private final int y;
    private final Color color;

    public TextoFlotante(final String texto, final int x, final int y, final Color color, final int duracionMs) {
        super(duracionMs);
        this.texto = texto;
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public static void setDestino(final GestorEfectos gestor) {
        destino = gestor;
    }

    /** Muestra un texto flotante de 900 ms centrado en (x, y). */
    public static void mostrar(final String texto, final int x, final int y, final Color color) {
        GestorEfectos d = destino;
        if (d != null) {
            d.agregar(GestorEfectos.Capa.FRENTE, new TextoFlotante(texto, x, y, color, 900));
        }
    }

    @Override
    public void dibujar(final Graphics graficos) {
        double p = progreso();
        int altura = (int) (46 * Interpolador.SALIDA.aplicar(p));
        float opacidad = p < 0.6 ? 1f : (float) (1 - (p - 0.6) / 0.4);
        int tamanio = (int) (26 + 12 * (1 - Interpolador.RETROCESO.aplicar(Interpolador.limitar(p * 4)))); // "pop" al aparecer

        Graphics2D g = (Graphics2D) graficos.create();
        try {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, Math.min(1f, opacidad))));
            Dibujador.TEXTO.dibujar(g, texto, x + 2, y - altura + 2, tamanio, new Color(0, 0, 0, 200)); // Sombra
            Dibujador.TEXTO.dibujar(g, texto, x, y - altura, tamanio, color);
        } finally {
            g.dispose();
        }
    }
}
