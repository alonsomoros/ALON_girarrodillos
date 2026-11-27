package main.maquina_estado.juego;

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
 * Las tres lámparas de turno (una por tirada). Usan la tira <code>rec/sprites/efectos/lampara_3f.png</code> (3 frames del mismo
 * ancho: 0 apagada, 1 encendida, 2 brillo/parpadeo). Si el archivo no existe se dibuja una lámpara provisional con código.
 * Al encenderse una lámpara da un pequeño "pop".
 */
class LamparasTurno {

    static final String RUTA = "rec/sprites/efectos/lampara_3f.png";
    private static final int FRAME_APAGADA = 0;
    private static final int FRAME_ENCENDIDA = 1;
    private static final int FRAME_BRILLO = 2;
    private static final int TICKS_POP = 12;

    private static final int X = 1100;
    private static final int Y = 530;

    private BufferedImage[] frames;

    private BufferedImage[] frames() {
        if (frames == null) {
            if (new File(RUTA).exists()) {
                frames = Animacion.cortarTira(CargadorRecursos.cargarImagenCompatibleTranslucida(RUTA), Animacion.framesEnNombre(RUTA));
            }
            if (frames == null || frames.length < 3) { // Sin tira propia (o incompleta): lámpara provisional
                frames = provisional();
            }
        }
        return frames;
    }

    /**
     * @param turno             tiradas ya hechas (lámparas que quedan encendidas)
     * @param parpadeoEncendido true durante la fase "brillo" del aviso de nuevo turno (se ven las tres a la vez)
     * @param edadCambioTurno   actualizaciones desde la última tirada, para el "pop" de la lámpara recién encendida
     */
    void dibujar(final Graphics graficos, final int turno, final boolean parpadeoEncendido, final long edadCambioTurno) {
        BufferedImage[] f = frames();
        Graphics2D g = (Graphics2D) graficos;
        for (int i = 0; i < 3; i++) {
            int frame = parpadeoEncendido ? FRAME_BRILLO : (i < turno ? FRAME_ENCENDIDA : FRAME_APAGADA);
            double escala = 1;
            if (!parpadeoEncendido && i == turno - 1 && edadCambioTurno < TICKS_POP) {
                escala += 0.35 * (1 - Interpolador.SALIDA.aplicar(edadCambioTurno / (double) TICKS_POP));
            }
            BufferedImage imagen = f[frame];
            int ancho = (int) Math.round(imagen.getWidth() * escala);
            int alto = (int) Math.round(imagen.getHeight() * escala);
            g.drawImage(imagen, X - i * 10 - ancho / 2, Y + i * 20 - alto / 2, ancho, alto, null);
        }
    }

    // Lámpara provisional de 24x24: apagada (oscura), encendida (crema) y brillo (crema con halo)
    private static BufferedImage[] provisional() {
        BufferedImage[] f = new BufferedImage[3];
        for (int k = 0; k < 3; k++) {
            f[k] = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = f[k].createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (k == FRAME_BRILLO) {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
                g.setColor(new Color(0xFFF3C0));
                g.fillOval(0, 0, 24, 24);
                g.setComposite(AlphaComposite.SrcOver);
            }
            g.setColor(k == FRAME_APAGADA ? new Color(0x2E1E13) : new Color(0xE4DEB0));
            g.fillOval(2, 2, 20, 20);
            g.dispose();
        }
        return f;
    }
}
