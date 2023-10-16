import javax.print.attribute.standard.Media;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.MeshView;
import javafx.stage.Stage;

public class Intro<MediaPlayer> extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        StackPane root = new StackPane();

        MediaPlayer player = new MediaPlayer( new Media(getClass().getResource("video.mp4").toExternalForm()));
        MeshView mediaView = new MediaView(player);

        root.getChildren().add( mediaView);

        Scene scene = new Scene(root, 1024, 768);

        primaryStage.setScene(scene);
        primaryStage.show();


        player.play();

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'start'");
    }

}