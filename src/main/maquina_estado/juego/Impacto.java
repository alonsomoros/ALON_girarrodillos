package main.maquina_estado.juego;

import main.herramientas.CargadorRecursos;
import main.motor.Animacion;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Animación del golpe: tira <code>rec/sprites/efectos/impacto_5f.png</code> (5 frames de 64x64, uno al lado del otro).
 * Si el archivo no existe se generan 5 frames provisionales con código: un anillo que se expande con chispas.
 */
class Impacto {

    static final String RUTA = "rec/sprites/efectos/impacto_5f.png";
    private static final int FRAMES_PROVISIONALES = 5;
    private static final int LADO = 64;

    private static BufferedImage[] frames;

    private static synchronized BufferedImage[] frames() {
        if (frames == null) {
            if (new File(RUTA).exists()) {
                frames = Animacion.cortarTira(CargadorRecursos.cargarImagenCompatibleTranslucida(RUTA), Animacion.framesEnNombre(RUTA));
            }
            if (frames == null || frames.length < 2) {
                frames = provisionales();
            }
        }
        return frames;
    }

    /** Dibuja el frame que toca al avance u (0 a 1) centrado en (cx, cy), con un tamaño relativo (1 = el del frame). */
    static void dibujar(final Graphics2D g, final double cx, final double cy, final double u, final double escala) {
        BufferedImage[] f = frames();
        int i = (int) Math.min(f.length - 1, Math.max(0, u * f.length));
        int ancho = (int) Math.round(f[i].getWidth() * escala);
        int alto = (int) Math.round(f[i].getHeight() * escala);
        g.drawImage(f[i], (int) Math.round(cx - ancho / 2.0), (int) Math.round(cy - alto / 2.0), ancho, alto, null);
    }

    private static BufferedImage[] provisionales() {
        BufferedImage[] f = new BufferedImage[FRAMES_PROVISIONALES];
        for (int k = 0; k < f.length; k++) {
            double t = k / (double) (f.length - 1);
            f[k] = new BufferedImage(LADO, LADO, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = f[k].createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (1 - 0.65 * t)));
            int c = LADO / 2;
            int radio = (int) (6 + 22 * t);
            g.setColor(t < 0.4 ? Color.WHITE : new Color(0xFFC25C));
            g.setStroke(new BasicStroke((float) (7 - 5 * t)));
            g.drawOval(c - radio, c - radio, radio * 2, radio * 2);
            g.setStroke(new BasicStroke(3f));
            g.setColor(new Color(0xFFF1C2));
            for (int a = 0; a < 8; a++) { // Chispas que salen del centro
                double ang = a * Math.PI / 4 + 0.2;
                int r1 = radio + 3;
                int r2 = radio + 6 + (int) (8 * t);
                g.drawLine((int) (c + Math.cos(ang) * r1), (int) (c + Math.sin(ang) * r1), (int) (c + Math.cos(ang) * r2), (int) (c + Math.sin(ang) * r2));
            }
            g.dispose();
        }
        return f;
    }
}
