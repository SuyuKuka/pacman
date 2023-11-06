package utils;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.net.URL;

public final class Music {
    /** {@code Clip}de musique/son. */
    private static Clip bgMusicClip = null;
    private static float currentVolume = 1.0f;

    private Music() {
    }

    public static float getVolume() {
        return currentVolume;
    }

    public static void setVolume(final float volume) {
        if (volume < 0f || volume > 1f) {
            throw new IllegalArgumentException("Volume value has to be between 0 and 1");
        }
        currentVolume = volume;

        if (bgMusicClip != null && bgMusicClip.isRunning()) {
            FloatControl volumeControl =
                (FloatControl) bgMusicClip.getControl(FloatControl.Type.MASTER_GAIN);
            final float dB = (float) (Math.log(currentVolume) / Math.log(10.0) * 20.0);
            volumeControl.setValue(dB);
        }
    }

    private static void setClipVolume(final Clip clip) {
        if (clip != null) {
            FloatControl volumeControl =
                (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            final float dB = (float) (Math.log(currentVolume) / Math.log(10.0) * 20.0);
            volumeControl.setValue(dB);
        }
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

            setClipVolume(clip);

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

            setClipVolume(bgMusicClip);

            bgMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Arrête la musique/son actuel. */
    public static void stopBgMusic() {
        if (bgMusicClip != null && bgMusicClip.isRunning()) {
            bgMusicClip.stop();
            bgMusicClip.close();
        }
    }
}
