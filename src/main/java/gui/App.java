package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import config.MazeConfig;
import model.MazeState;
import utils.Music;//ajouter le fichier utils/Music

public class App extends Application {
        @Override
        public void start(Stage primaryStage) {
                var root = new Pane();
                var gameScene = new Scene(root);
                var pacmanController = new PacmanController();
                gameScene.setOnKeyPressed(pacmanController::keyPressedHandler);
                gameScene.setOnKeyReleased(pacmanController::keyReleasedHandler);
                var maze = new MazeState(MazeConfig.makeExample1(), gameScene);
                var gameView = new GameView(maze, root, 30.0);
                primaryStage.setTitle("Mac-Pan-Pac-Man");
                primaryStage.getIcons().add(new Image("file:src\\main\\resources\\pacman.png"));
                primaryStage.setScene(gameScene);
                primaryStage.show();
                gameView.animate();

                Music.playLoopingBgMusic("bgm.wav");//file du fond de music en boucle(obligé:.wav)
        }

}
