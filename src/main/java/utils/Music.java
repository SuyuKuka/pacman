package utils;

import javax.sound.sampled.*;
import java.net.URL;

public class Music {

    private static Clip bgMusicClip = null;
    
    // Jouera l'effet sonore une fois, sans boucle
    public static void playSound(String soundFileName) {// "soundFileName" Nom du fichier audio dans le répertoire "resources"
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new URL(Resources.getFile(soundFileName)));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 
    
    
    // jouera l'effet sonore en boucle
    public static void playLoopingBgMusic(String soundFileName) {
        stopBgMusic();
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new URL(Resources.getFile(soundFileName)));
            bgMusicClip = AudioSystem.getClip();
            bgMusicClip.open(audioInputStream);
            bgMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Si y'en a d'autre music en train de jouer en boucle, on l'arrète et commencera la nouvelle musique
    public static void stopBgMusic() {
        if (bgMusicClip != null && bgMusicClip.isRunning()) {
            bgMusicClip.stop();
        }
    }
}
