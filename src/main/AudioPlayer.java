package main;

import main.motor.Sonidos;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * Música de fondo (un único clip en bucle) y acceso rápido a los efectos de sonido, que gestiona {@link Sonidos}.
 */
public class AudioPlayer {

    // Las pruebas desactivan todo el audio para no llenar el equipo de música y pitidos
    public static volatile boolean efectosActivos = true;

    private Clip currentClip;
    private static AudioPlayer instance;

    public static synchronized AudioPlayer getInstance() {
        if (instance == null) {
            instance = new AudioPlayer();
        }
        return instance;
    }

    public synchronized void playSound(String filePath) {
        stopSound();
        if (!efectosActivos) {
            return;
        }

        try {
            // Obtiene un flujo de entrada del archivo de audio
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

            // Obtiene un clip de audio y carga el audio en él
            currentClip = AudioSystem.getClip();
            currentClip.open(audioStream);
            refrescarVolumen();

            // Reproduce el clip en bucle
            currentClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public synchronized void stopSound() {
        if (currentClip != null) {
            currentClip.stop();
            currentClip.close();
            currentClip = null;
        }
    }

    /** Efecto de sonido de una sola vez (puede solaparse con otros iguales). */
    public void playSoundOnce(String filePath) {
        Sonidos.getInstance().reproducir(filePath);
    }

    /** Vuelve a aplicar a la música el volumen y el silencio actuales. */
    public synchronized void refrescarVolumen() {
        Sonidos.aplicarVolumen(currentClip, Sonidos.getInstance().volumenMusicaEfectivo());
    }
}
