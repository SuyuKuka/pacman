package utils;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;


public class Music {

    private static Clip bgMusicClip = null;
    
    // jouera l'effet sonore une fois, sans boucle
    public static void playSound(String soundFileName) {//“soundFileName” Nom du fichier audio dans le répertoire “resources”
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(Music.class.getResource("/" + soundFileName));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        }catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
            System.out.println("le format du fichier audio n'est pas supporté");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("les erreurs d'E/S lors de la lecture du fichier");
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            System.out.println("le périphérique audio n'est pas disponible ou si d'autres lignes échouent à s'ouvrir");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("autre erreur");
        }
    } 
    
    
    // jouera l'effet sonore en boucle
    public static void playLoopingBgMusic(String soundFileName) {
        stopBgMusic();
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(Music.class.getResource("/"+ soundFileName));
            bgMusicClip = AudioSystem.getClip();
            bgMusicClip.open(audioInputStream);
            bgMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
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
