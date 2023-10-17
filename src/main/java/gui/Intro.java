package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;

public class Intro extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Mac-Pan-Pac-Man");
        primaryStage.getIcons().add(new Image("file:src/main/resources/pacman.png"));  
        // Chemin vers la vidéo
        String videoPath = "src/main/ressources/mp4/tenders_org.mp4";

        // Créer un objet Media en utilisant le chemin de la vidéo
        Media media = new Media(new File(videoPath).toURI().toString());

        // Créer un lecteur multimédia
        MediaPlayer mediaPlayer = new MediaPlayer(media);

        // Créer une vue multimédia associée au lecteur multimédia
        MediaView mediaView = new MediaView(mediaPlayer);

        // Créer un conteneur StackPane et y ajouter la vue multimédia
        StackPane root = new StackPane();
        root.getChildren().add(mediaView);

        // Créer une scène avec le conteneur StackPane
        Scene gameScene = new Scene(root, 690, 510);

        // Configurer la scène et le stage
        gameScene.setFill(Color.BLACK);
        primaryStage.setScene(gameScene);
        primaryStage.show();

        // Associer le lecteur multimédia à la vue multimédia
        mediaView.setMediaPlayer(mediaPlayer);

        // Définir l'écouteur pour détecter la fin de la vidéo
        mediaPlayer.setOnEndOfMedia(() -> {
            // Action à effectuer lorsque la vidéo est terminée
            Menu launchMenu = new Menu();
            launchMenu.start(primaryStage);
        });

        // Commencer la lecture de la vidéo
        mediaPlayer.play();
    }
}
