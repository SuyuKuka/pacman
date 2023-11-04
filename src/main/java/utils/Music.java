package utils;

import javax.sound.sampled.*;
import java.io.IOException;

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
    * Joue un effet sonore une fois, puis exécute l'action spécifiée après sa fin.
    * @param soundFileName Nom du fichier dans le répertoire "resources"
    * @param onFinished Action à exécuter après la fin de l'effet sonore
    */
    public static void playSound(String soundFileName, Runnable onFinished) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                new URL(Resources.getFile(soundFileName))
            );
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    onFinished.run(); 
                    clip.close();
                }
            });
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Joue la musique de fond en boucle.
     * @param soundFileName Nom du fichier dans le répertoire "resources"
     */
    public static void playLoopingBgMusic(String soundFileName) {
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


    /**
    * Joue l'effet sonore une fois, puis répète indéfiniment la musique de fond.
    * @param introMusicFileName Nom du fichier dans le répertoire "resources" pour la musique d'intro
    * @param loopBgMusicFileName Nom du fichier dans le répertoire "resources" pour la musique de fond en boucle
    */
    public static void playIntroMusicThenLoopBgMusic(String introMusicFileName, String loopBgMusicFileName) {
        playSound(introMusicFileName, () -> playLoopingBgMusic(loopBgMusicFileName)); 
    }
}
