package gui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import model.MazeState;
import utils.Resources;

public final class InGameGui {
    private Stage primaryStage;
    private Scene gameScene;
    private StackPane endGamePane = new StackPane();
    private StackPane rootPane = new StackPane();
    private Text scoreText;
    private ImageView[] lives;
    private boolean end;

    public static final InGameGui INSTANCE = new InGameGui();

    public static void setInGameGui(Stage stage, Scene scene) {
        INSTANCE.primaryStage = stage;
        INSTANCE.gameScene = scene;
        INSTANCE.end = false;
    }

    public void displayScore(MazeState state) {
        // Création d'un texte
        scoreText = new Text();
        // Position du score 
        scoreText.setTranslateX((double) (state.getWidth() + state.getHeight()) * 30 / 2);
        scoreText.setTranslateY((double) (state.getHeight() + state.getWidth()) * 30 / 6 - 50);
        // Configuration du texte
        scoreText.setFont(Font.loadFont(Resources.getFile("slkscr"), 24));
        scoreText.setFill(Color.WHITE);
        // Ajoute le texte au groupe rootPane
        rootPane.getChildren().add(scoreText);
        // Met le groupe dans la scene
        if (!((Pane) gameScene.getRoot()).getChildren().contains(scoreText)) {
            ((Pane) gameScene.getRoot()).getChildren().add(scoreText);
        }
    }

    public void updateScore(MazeState state) {
        // Affiche le score
        scoreText.setText("Score: " + state.getScore());
        // Total : 150 pac-gommes en classique
        if (state.getPacgomme() == state.getMaxScore() && !end) {
            end = true;
            state.playerWin();
            endGame(true, state);
        }
    }

    private ImageView setLife(MazeState state) {
        ImageView imageView = new ImageView();
        Image image = Resources.getImage("pacman");
        
        imageView.setImage(image);
        imageView.setY((double) (state.getHeight() + state.getWidth()) * 30 / 2 - 50);
        imageView.setFitWidth(state.getWidth() * 1.4);
        imageView.setPreserveRatio(true);
        return imageView;
    }

    public void displayLives(MazeState state) {
        lives = new ImageView[state.getLives()];
        for (int i = 0; i < lives.length; i++) {
            ImageView life = setLife(state);
            life.setX(((state.getWidth() + state.getHeight() + i * 1.9) * 30 / 2));
            lives[i] = life;
            if (!((Pane) gameScene.getRoot()).getChildren().contains(lives[i])) {
                ((Pane) gameScene.getRoot()).getChildren().add(lives[i]);
            }
        }
    }

    public void updateLives(MazeState state) {
        if (state.getLives() < state.getTotalLive()) {
            lives[state.getLives()].setVisible(false);
        }

        if (state.getLives() == 0 && !end) {
            end = true;
            endGame(false, state);
        }
    }

    public void endGame(boolean won, MazeState state) {
        Rectangle transparent = new Rectangle(0, 0, primaryStage.getWidth(), primaryStage.getHeight() - 40);
        
        transparent.setFill(Color.GRAY);
        transparent.setOpacity(0.6);

        String title = won ? "You Win !" : "GameOver";
        Text gameover = Menu.createText(title, 104);
        Text score = Menu.createText("Score : " + state.getScore(), 64);
    
        Text select0 = Menu.createText(">", 38);
        Text continuer = Menu.createText("Continue", 38);
        HBox option0 = Menu.createHBox(20, Pos.CENTER, select0, continuer);
        continuer.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
                
        });

        Text select1 = Menu.createText(">", 38);
        Text retry = Menu.createText("Retry", 38);
        HBox option1 = Menu.createHBox(20, Pos.CENTER, select1, retry);
        select1.setVisible(false);
        retry.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            Solo.startLevel(primaryStage);
        });

        Text quitter = Menu.createText("Quit", 38);
        Text select2 = Menu.createText(">", 38);
        HBox option2 = Menu.createHBox(20, Pos.CENTER, select2, quitter);
        select2.setVisible(false);
        quitter.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            Menu quit = new Menu();
            quit.start(primaryStage);
        });

        VBox titleBox = Menu.createVBox(0, Pos.CENTER, gameover, score);
        VBox optionBox = Menu.createVBox(15, Pos.CENTER, option0, option1, option2);
        VBox mainBox = Menu.createVBox(50, Pos.CENTER, titleBox, optionBox);

        endGamePane.getChildren().addAll(transparent, mainBox);
        ((Pane) gameScene.getRoot()).getChildren().add(endGamePane);

        Text[] choices = {select0, select1, select2};
        Menu.selector(gameScene, choices);

        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                INSTANCE.endGamePane.getChildren().clear();
                switch (Menu.selected) {
                    case 0:
                        // Continue sur le prochain niveau
                        break;

                    case 1:
                        Solo.startLevel(primaryStage);
                        break;

                    case 2:
                        Menu quit = new Menu();
                        quit.start(primaryStage);
                        break;

                    default:
                        break;
                }
            }
        });
    }
}
