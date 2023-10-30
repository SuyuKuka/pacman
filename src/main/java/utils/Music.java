package utils;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

public final class Music {
    /** {@code Clip}de musique/son. */
    private static Clip bgMusicClip = null;

    private Music() {
    }

    /**
     * Joue un effet sonore une fois.
     * @param soundFileName Nom du fichier dans le répertoire "resources"
     */
    public static void playSound(final String soundFileName) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                new URL(Resources.getFile(soundFileName))
            );
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Joue la musique de fond en boucle.
     * @param soundFileName Nom du fichier dans le répertoire "resources"
     */
    public static void playLoopingBgMusic(final String soundFileName) {
        stopBgMusic();
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                new URL(Resources.getFile(soundFileName))
            );
            bgMusicClip = AudioSystem.getClip();
            bgMusicClip.open(audioInputStream);
            bgMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Arrête la musique/son actuel. */
    public static void stopBgMusic() {
        if (bgMusicClip != null && bgMusicClip.isRunning()) {
            bgMusicClip.stop();
        }
    }
}
