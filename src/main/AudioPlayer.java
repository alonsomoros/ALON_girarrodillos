package main;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class AudioPlayer {

    private Clip currentClip;
    private Map<String, Clip> clips;
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
        if (currentClip != null && currentClip.isRunning()) {
            currentClip.close();
            currentClip.stop();
        }
    }

    public synchronized void playSoundOnce(String filePath) {
        try {
            // Obtiene un flujo de entrada del archivo de audio
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

            // Obtiene un clip de audio
            Clip clip = AudioSystem.getClip();

            // Abre el clip y carga el audio en él
            clip.open(audioStream);

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
