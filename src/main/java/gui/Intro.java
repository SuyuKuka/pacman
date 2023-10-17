package gui;

import java.io.File;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Intro extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Mac-Pan-Pac-Man");
        primaryStage.getIcons().add(new Image(Paths.get("src/main/resources/pacman.png").toUri().toString()));

        // Chemin vers la vidéo
        String videoPath = Paths.get("src/main/resources/mp4/tenders_org.mp4").toUri().toString();
        Media media = new Media(videoPath);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        MediaView mediaView = new MediaView(mediaPlayer);

        StackPane root = new StackPane();
        root.getChildren().add(mediaView);

        Scene gameScene = new Scene(root, 690, 510);
        gameScene.setFill(Color.BLACK);

        primaryStage.setScene(gameScene);
        primaryStage.show();

        mediaView.setMediaPlayer(mediaPlayer);

        mediaPlayer.setOnEndOfMedia(() -> {
            Menu launchMenu = new Menu();
            launchMenu.start(primaryStage);
        });

        mediaPlayer.play();

    }
}
