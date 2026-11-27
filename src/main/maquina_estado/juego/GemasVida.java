package main.maquina_estado.juego;

import main.Constantes;
import main.herramientas.CargadorRecursos;
import main.motor.Animacion;
import main.motor.Interpolador;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Barra de vida hecha de gemas, una por punto de vida máxima. Usa <code>rec/sprites/efectos/vida_gema_2f.png</code>
 * (2 frames del mismo ancho: 0 llena, 1 vacía); si no existe dibuja unas gemas provisionales con código.
 * La gema que se pierde se rompe (se encoge y se vuelve blanca) y la que se gana da un pequeño "pop".
 */
class GemasVida {

    static final String RUTA = "rec/sprites/efectos/vida_gema_2f.png";
    private static final int SEPARACION = 15;
    private static final int TICKS_EFECTO = 24;

    private BufferedImage[] frames;

    private BufferedImage[] frames() {
        if (frames == null) {
            if (new File(RUTA).exists()) {
                frames = Animacion.cortarTira(CargadorRecursos.cargarImagenCompatibleTranslucida(RUTA), Animacion.framesEnNombre(RUTA));
            }
            if (frames == null || frames.length < 2) {
                frames = provisional();
            }
        }
        return frames;
    }

    /**
     * @param centroX      centro horizontal de la fila
     * @param y            centro vertical de la fila
     * @param vida         vida actual
     * @param vidaAntes    vida antes del último cambio
     * @param edadCambio   actualizaciones desde ese cambio
     */
    void dibujar(final Graphics graficos, final int centroX, final int y, final int vida, final int vidaAntes, final long edadCambio) {
        BufferedImage[] f = frames();
        Graphics2D g = (Graphics2D) graficos;
        int n = Constantes.VIDA_MAXIMA;
        int inicio = centroX - (n - 1) * SEPARACION / 2;
        double t = Interpolador.limitar(edadCambio / (double) TICKS_EFECTO);
        boolean cambiando = edadCambio < TICKS_EFECTO;

        for (int i = 0; i < n; i++) {
            BufferedImage llena = f[0];
            BufferedImage vacia = f[1];
            int x = inicio + i * SEPARACION;
            if (i < vida) {
                double escala = 1;
                if (cambiando && i >= vidaAntes) { // Gema ganada: pop
                    escala = 1 + 0.5 * (1 - Interpolador.SALIDA.aplicar(t));
                }
                dibujarCentrada(g, llena, x, y, escala, 1f);
            } else {
                dibujarCentrada(g, vacia, x, y, 1, 1f);
                if (cambiando && i < vidaAntes) { // Gema perdida: se rompe
                    java.awt.Composite original = g.getComposite();
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (1 - t)));
                    dibujarCentrada(g, llena, x, y, 1 + 0.6 * t, (float) (1 - t));
                    g.setColor(Color.WHITE);
                    int r = (int) (6 + 6 * t);
                    g.fillOval(x - r, y - r, r * 2, r * 2);
                    g.setComposite(original);
                }
            }
        }
    }

    private static void dibujarCentrada(final Graphics2D g, final BufferedImage imagen, final int cx, final int cy, final double escala, final float alfa) {
        int ancho = (int) Math.round(imagen.getWidth() * escala);
        int alto = (int) Math.round(imagen.getHeight() * escala);
        g.drawImage(imagen, cx - ancho / 2, cy - alto / 2, ancho, alto, null);
    }

    // Gemas provisionales de 14x14: rombo rojo con brillo (llena) y rombo oscuro (vacía)
    private static BufferedImage[] provisional() {
        BufferedImage[] f = new BufferedImage[2];
        for (int k = 0; k < 2; k++) {
            f[k] = new BufferedImage(14, 14, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = f[k].createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int[] x = {7, 13, 7, 1};
            int[] y = {0, 7, 14, 7};
            g.setColor(k == 0 ? new Color(0xD9453B) : new Color(0x2E1E13));
            g.fillPolygon(x, y, 4);
            if (k == 0) {
                g.setColor(new Color(0xFF9A8F));
                g.fillPolygon(new int[]{7, 10, 7, 5}, new int[]{2, 6, 5, 6}, 4);
            }
            g.dispose();
        }
        return f;
    }
}
