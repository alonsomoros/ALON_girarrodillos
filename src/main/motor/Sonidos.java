package main.motor;

import main.AudioPlayer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Banco de efectos de sonido. Cada efecto se lee del disco una sola vez y se reparte en varias "voces" (clips ya abiertos)
 * para que el mismo sonido pueda sonar solapado (varios golpes seguidos) sin crear un Clip en cada reproducción.
 * También guarda los volúmenes de efectos y de música (0 a 1) y el silencio general.
 */
public class Sonidos {

    private static final int VOCES_POR_EFECTO = 4;

    private static Sonidos instance;

    /** Un efecto precargado: su pool de clips y el siguiente que se robará si todos están sonando. */
    private static class Efecto {
        final Clip[] voces;
        int siguiente = 0;

        Efecto(final Clip[] voces) {
            this.voces = voces;
        }
    }

    private final Map<String, Efecto> efectos = new HashMap<>();
    private final java.util.Set<String> fallidos = new java.util.HashSet<>();
    private double volumenEfectos = 1.0;
    private double volumenMusica = 1.0;
    private boolean silencio = false;

    public static synchronized Sonidos getInstance() {
        if (instance == null) {
            instance = new Sonidos();
        }
        return instance;
    }

    /** Volumen lineal (0 a 1) a decibelios para MASTER_GAIN: 1 = 0 dB (sin cambio), 0,5 = -6 dB, 0 = -80 dB (casi mudo). */
    public static float volumenADecibelios(final double volumen) {
        if (volumen <= 0.0001) {
            return -80f;
        }
        return (float) (20 * Math.log10(Math.min(1.0, volumen)));
    }

    /** Aplica un volumen lineal (0 a 1) a un clip, respetando los límites que admita su línea. */
    public static void aplicarVolumen(final Clip clip, final double volumen) {
        if (clip == null || !clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            return;
        }
        FloatControl ganancia = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float db = volumenADecibelios(volumen);
        ganancia.setValue(Math.max(ganancia.getMinimum(), Math.min(ganancia.getMaximum(), db)));
    }

    /** Lee el efecto y prepara sus voces. Si falla (archivo ausente, sin tarjeta de sonido...) avisa una vez y lo ignora. */
    public synchronized boolean precargar(final String ruta) {
        if (efectos.containsKey(ruta)) {
            return true;
        }
        if (fallidos.contains(ruta)) {
            return false;
        }
        try {
            AudioInputStream origen = AudioSystem.getAudioInputStream(new File(ruta));
            AudioFormat formato = origen.getFormat();
            byte[] datos = origen.readAllBytes();
            origen.close();

            Clip[] voces = new Clip[VOCES_POR_EFECTO];
            for (int i = 0; i < voces.length; i++) {
                voces[i] = AudioSystem.getClip();
                voces[i].open(new AudioInputStream(new ByteArrayInputStream(datos), formato, datos.length / formato.getFrameSize()));
                aplicarVolumen(voces[i], volumenEfectos);
            }
            efectos.put(ruta, new Efecto(voces));
            return true;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | IllegalArgumentException e) {
            fallidos.add(ruta);
            System.err.println("No se pudo cargar el sonido " + ruta + ": " + e.getMessage());
            return false;
        }
    }

    /** Reproduce un efecto (lo precarga la primera vez). Varias llamadas seguidas suenan solapadas. */
    public synchronized void reproducir(final String ruta) {
        if (!AudioPlayer.efectosActivos || silencio || volumenEfectos <= 0 || !precargar(ruta)) {
            return;
        }
        Efecto efecto = efectos.get(ruta);
        Clip voz = null;
        for (Clip candidata : efecto.voces) {
            if (!candidata.isActive()) {
                voz = candidata;
                break;
            }
        }
        if (voz == null) { // Todas suenan: se reinicia la más antigua
            voz = efecto.voces[efecto.siguiente];
            efecto.siguiente = (efecto.siguiente + 1) % efecto.voces.length;
            voz.stop();
        }
        voz.setFramePosition(0);
        voz.start();
    }

    public synchronized double getVolumenEfectos() {
        return volumenEfectos;
    }

    public synchronized void setVolumenEfectos(final double volumen) {
        volumenEfectos = Math.max(0, Math.min(1, volumen));
        for (Efecto efecto : efectos.values()) {
            for (Clip voz : efecto.voces) {
                aplicarVolumen(voz, volumenEfectos);
            }
        }
    }

    public synchronized double getVolumenMusica() {
        return volumenMusica;
    }

    public synchronized void setVolumenMusica(final double volumen) {
        volumenMusica = Math.max(0, Math.min(1, volumen));
        AudioPlayer.getInstance().refrescarVolumen();
    }

    public synchronized boolean estaSilenciado() {
        return silencio;
    }

    /** Silencia o reactiva todo el sonido (música incluida). */
    public synchronized void setSilencio(final boolean silencio) {
        this.silencio = silencio;
        AudioPlayer.getInstance().refrescarVolumen();
    }

    /** Volumen efectivo de la música teniendo en cuenta el silencio. */
    public synchronized double volumenMusicaEfectivo() {
        return silencio ? 0 : volumenMusica;
    }
}
