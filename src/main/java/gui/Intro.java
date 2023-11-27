package gui;

import gui.menu.Menu;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import utils.Resources;

public class Intro extends Application {

    /**
     * Méthode appelée au lancement du jeu. Elle joue l'introduction.
     * La méthode {@link Menu#start} est appelée à la fin.
     */
    @Override
    public void start(final Stage primaryStage) {
        final int width = 1000;
        final int height = 720;
        primaryStage.setMinWidth(width);
        primaryStage.setMinHeight(height);

        primaryStage.setTitle("Mac-Pan-Pac-Man");
        primaryStage.getIcons().add(Resources.getImage("pacman"));

        // Chemin vers la vidéo
        String videoPath = Resources.getFile("tenders_org");
        Media media = new Media(videoPath);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        MediaView mediaView = new MediaView(mediaPlayer);

        StackPane root = new StackPane();
        root.getChildren().add(mediaView);

        Scene gameScene = new Scene(root, width, height);
        gameScene.setFill(Color.BLACK);

        primaryStage.setScene(gameScene);
        primaryStage.show();

        mediaView.setMediaPlayer(mediaPlayer);

        mediaPlayer.setOnEndOfMedia(() -> Menu.INSTANCE.start(primaryStage));

        mediaPlayer.play();

    }
}

