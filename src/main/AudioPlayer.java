package main;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class AudioPlayer {

    private Clip currentClip;
    private final Map<String, Clip> clips = new HashMap<>();
    private static AudioPlayer instance;

    public static synchronized AudioPlayer getInstance() {
        if (instance == null) {
            instance = new AudioPlayer();
        }
        return instance;
    }

    public synchronized void playSound(String filePath) {
        stopSound();
        try {
            // Obtiene un flujo de entrada del archivo de audio
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

            // Obtiene un clip de audio
            currentClip = AudioSystem.getClip();

            // Abre el clip y carga el audio en él
            currentClip.open(audioStream);

            // Reproduce el clip en bucle
            currentClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public synchronized void stopSound() {
        if (currentClip != null) {
            currentClip.stop();
            if (clips.containsValue(currentClip)) {
                currentClip.setFramePosition(0); // Precargado: se conserva para poder reutilizarlo
            } else {
                currentClip.close();
            }
            currentClip = null;
        }

    }

    // Las pruebas desactivan los efectos para no llenar el audio de pitidos
    public static volatile boolean efectosActivos = true;

    public synchronized void playSoundOnce(String filePath) {
        if (!efectosActivos) {
            return;
        }
        try {

            // Obtiene un flujo de entrada del archivo de audio
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

            // Obtiene un clip de audio
            Clip clip = AudioSystem.getClip();

            // Abre el clip y carga el audio en él
            clip.open(audioStream);

            // Cuando termina, libera la línea de audio (si no, se acumulan hasta agotarlas)
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });

            // Reproduce el clip una vez
            clip.start();

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public synchronized void preloadSound(String filePath) {
        try {
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clips.put(filePath, clip);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public synchronized void playPreloadedSound(String filePath, boolean loop) {
        stopSound();
        Clip clip = clips.get(filePath);
        if (clip != null) {
            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                clip.start();
            }
            currentClip = clip;
        }
    }

}
