package main.maquina_estado.juego;

import main.herramientas.CargadorRecursos;
import main.motor.Animacion;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Estrellitas de victoria y de subida de nivel. Usan la tira <code>rec/sprites/efectos/estrella_3x.png</code> (3 frames del mismo
 * ancho con la estrella en tres tamaños: pequeña, mediana y grande). Si no existe se generan estrellas provisionales de 32x32.
 */
final class Estrella {

    static final String RUTA = "rec/sprites/efectos/estrella_3x.png";
    private static BufferedImage[] frames;

    private Estrella() {
    }

    private static synchronized BufferedImage[] frames() {
        if (frames == null) {
            if (new File(RUTA).exists()) {
                frames = Animacion.cortarTira(CargadorRecursos.cargarImagenCompatibleTranslucida(RUTA), Animacion.framesEnNombre(RUTA));
            }
            if (frames == null || frames.length < 3) {
                frames = provisionales();
            }
        }
        return frames;
    }

    /** Dibuja una estrella del tamaño 0 (pequeña), 1 o 2 (grande) centrada en (cx, cy). */
    static void dibujar(final Graphics2D g, final int tamanio, final double cx, final double cy, final double rotacion, final float alfa) {
        BufferedImage imagen = frames()[Math.max(0, Math.min(frames().length - 1, tamanio))];
        java.awt.Composite original = g.getComposite();
        AffineTransform t = g.getTransform();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, Math.min(1f, alfa))));
        g.translate(cx, cy);
        g.rotate(rotacion);
        g.drawImage(imagen, -imagen.getWidth() / 2, -imagen.getHeight() / 2, null);
        g.setTransform(t);
        g.setComposite(original);
    }

    private static BufferedImage[] provisionales() {
        BufferedImage[] f = new BufferedImage[3];
        int[] radios = {7, 11, 15};
        for (int k = 0; k < 3; k++) {
            f[k] = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = f[k].createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int[] x = new int[10];
            int[] y = new int[10];
            for (int i = 0; i < 10; i++) {
                double ang = -Math.PI / 2 + i * Math.PI / 5;
                double r = i % 2 == 0 ? radios[k] : radios[k] * 0.45;
                x[i] = (int) Math.round(16 + Math.cos(ang) * r);
                y[i] = (int) Math.round(16 + Math.sin(ang) * r);
            }
            g.setColor(new Color(0xF4C94A));
            g.fillPolygon(x, y, 10);
            g.setColor(new Color(0xFFF1B0));
            g.drawPolygon(x, y, 10);
            g.dispose();
        }
        return f;
    }
}
