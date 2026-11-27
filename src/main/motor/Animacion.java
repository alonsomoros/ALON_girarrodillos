package main.motor;

import main.herramientas.CargadorRecursos;

import java.awt.image.BufferedImage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Animación a partir de una tira horizontal de frames del mismo ancho. Convención de nombre: <code>nombre_Nf.png</code>
 * (N = número de frames, p. ej. <code>impacto_5f.png</code>). Es "sin estado": quien la usa pasa los ticks
 * transcurridos desde que empezó, así una misma Animacion se puede compartir entre muchos efectos.
 */
public class Animacion {

    public enum Modo {
        UNA_VEZ,   // Se queda en el último frame
        BUCLE,     // Vuelve al primero
        PING_PONG  // Ida y vuelta
    }

    private static final Pattern PATRON_FRAMES = Pattern.compile("_([0-9]+)f[.][A-Za-z]+$");

    private final BufferedImage[] frames;
    private final int ticksPorFrame;
    private final Modo modo;

    public Animacion(final BufferedImage[] frames, final int msPorFrame, final Modo modo) {
        if (frames == null || frames.length == 0) {
            throw new IllegalArgumentException("Una animación necesita al menos un frame");
        }
        this.frames = frames;
        this.ticksPorFrame = Math.max(1, Tiempo.msATicks(msPorFrame));
        this.modo = modo;
    }

    /** Carga una tira desde disco; el número de frames sale del nombre (<code>_Nf</code>), o 1 si no lo trae. */
    public static Animacion cargar(final String ruta, final int msPorFrame, final Modo modo) {
        BufferedImage tira = CargadorRecursos.cargarImagenCompatibleTranslucida(ruta);
        return new Animacion(cortarTira(tira, framesEnNombre(ruta)), msPorFrame, modo);
    }

    /** Número de frames que indica el nombre del archivo (<code>impacto_5f.png</code> = 5); 1 si no indica nada. */
    public static int framesEnNombre(final String ruta) {
        Matcher m = PATRON_FRAMES.matcher(ruta);
        return m.find() ? Math.max(1, Integer.parseInt(m.group(1))) : 1;
    }

    /** Parte una tira horizontal en n frames iguales. Si la imagen es demasiado estrecha devuelve un solo frame. */
    public static BufferedImage[] cortarTira(final BufferedImage tira, final int n) {
        if (n <= 1 || tira.getWidth() < n) {
            return new BufferedImage[]{tira};
        }
        int ancho = tira.getWidth() / n;
        BufferedImage[] frames = new BufferedImage[n];
        for (int i = 0; i < n; i++) {
            frames[i] = tira.getSubimage(i * ancho, 0, ancho, tira.getHeight());
        }
        return frames;
    }

    public int numeroFrames() {
        return frames.length;
    }

    public int duracionTicks() {
        return frames.length * ticksPorFrame;
    }

    /** Índice del frame que toca a los ticks transcurridos dados. */
    public int indiceFrame(final long ticksTranscurridos) {
        long paso = Math.max(0, ticksTranscurridos) / ticksPorFrame;
        int n = frames.length;
        switch (modo) {
            case BUCLE:
                return (int) (paso % n);
            case PING_PONG:
                if (n == 1) {
                    return 0;
                }
                int ciclo = 2 * (n - 1);
                int p = (int) (paso % ciclo);
                return p < n ? p : ciclo - p;
            default: // UNA_VEZ
                return (int) Math.min(paso, n - 1);
        }
    }

    public BufferedImage frame(final long ticksTranscurridos) {
        return frames[indiceFrame(ticksTranscurridos)];
    }

    /** Solo tiene sentido en UNA_VEZ: ya se mostraron todos los frames. */
    public boolean terminada(final long ticksTranscurridos) {
        return modo == Modo.UNA_VEZ && ticksTranscurridos >= duracionTicks();
    }

    public int ancho() {
        return frames[0].getWidth();
    }

    public int alto() {
        return frames[0].getHeight();
    }
}
