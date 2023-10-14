package gui;

import javafx.geometry.Pos;
import javafx.scene.Group;
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

public class MenuGraphics {
    private Stage primaryStage;
    private Scene gameScene;
    private StackPane root = new StackPane();
    private Text text_score;
    private Group[] livegroup;
    private int select = 1;
    private boolean lose = false;
    private StackPane Interface = new StackPane();
    private Rectangle Transparent;
    private Rectangle TextPos;
    

    public MenuGraphics(Scene gameScene, Stage primaryStage) {
        this.gameScene = gameScene;
        this.primaryStage=primaryStage;
    }

    public void displayScore(MazeState state) {
        if (text_score == null) {
            // Création d'un texte
            text_score = new Text();
            // Position du score // 30 = scale, 2 et 6 valeur arbitraire
            text_score.setX((state.getWidth() + state.getHeight()) * 30 / 2);
            text_score.setY((state.getHeight() + state.getWidth()) * 30 / 6);
            // Configuration du texte
            text_score.setFont(Font.loadFont("file:src\\main\\resources\\fonts\\slkscr.ttf", 24));
            text_score.setFill(Color.WHITE);
            // Ajoute le texte au groupe root
            root.getChildren().add(text_score);
            // Met le group dans la scene
            ((Pane) gameScene.getRoot()).getChildren().add(root);
        }
        // Affiche le score
        text_score.setText("Score: " + state.getScore()); // Total : 154 score en classique
    }

    private ImageView setLive(MazeState state) {
        ImageView imageView = new ImageView();
        Image image = new Image("file:src\\main\\resources\\pacman.png");
        imageView.setImage(image);
        imageView.setY((state.getHeight() + state.getWidth()) * 30 / 2 - 50);
        imageView.setFitWidth(state.getWidth() * 1.4);
        imageView.setPreserveRatio(true);
        return imageView;
    }

    public void displayLive(MazeState state) {
        livegroup = new Group[state.getLive()];
        for (int i = 0; i < livegroup.length; i = i + 1) {
            ImageView live = setLive(state);
            live.setX(((state.getWidth() + state.getHeight() + i * 1.9) * 30 / 2));
            livegroup[i] = new Group();
            livegroup[i].getChildren().add(live);
            if (!((Pane) gameScene.getRoot()).getChildren().contains(livegroup[i])) {
                ((Pane) gameScene.getRoot()).getChildren().add(livegroup[i]);
            }
        }
    }

    public void removeLife(MazeState state) {
        if (state.getLive() < state.getTotalLive()) {
            livegroup[state.getLive()].setVisible(false);
        }

        if (state.getLive() == 0 && !lose) {
            lose = true;
            GameOver(state);
        }
    }

    public void GameOver(MazeState state) {
        /* 
        Transparent = new Rectangle(0, 0, state.getHeight() * 100, state.getWidth() * 100);
        TextPos = new Rectangle(0, 0, state.getHeight() * 30, state.getWidth() * 30);

        Transparent.setFill(Color.BLACK);
        Transparent.setOpacity(0.4);

        TextPos.setOpacity(0);
        */
        Text GameOver = Menu.createText("GameOver", 38);
       
        Text Continue = Menu.createText("Continue", 38);
        Text select1 = Menu.createText(">", 38);
        HBox option1 = Menu.createHBox(20, select1, Continue);
        Continue.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
                
        });

        Text Retry = Menu.createText("Retry", 38);
        Text select2 = Menu.createText(">", 38);
        HBox option2 = Menu.createHBox(20, select2, Retry);
        Retry.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
                
        });

        Text Quit = Menu.createText("Quit", 38);
        Text select3 = Menu.createText(">", 38);
        HBox option3 = Menu.createHBox(20, select3, Quit);
        Quit.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
                
        });

        Text Score = Menu.createText("Score : " + state.getScore(), 46);

        VBox titlePos = new VBox(100);
        VBox textPos = new VBox(20);
                
        textPos.getChildren().addAll(Score, option1, option2, option3);
        textPos.setAlignment(Pos.CENTER);

        titlePos.getChildren().addAll(GameOver, textPos);
        titlePos.setAlignment(Pos.CENTER);

        Interface.getChildren().addAll(titlePos);

        //Interface.getChildren().addAll(Transparent, TextPos);

        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                // (fonction)
                if (select == 1) {
                    // Continue sur le prochain niveau
                }else if (select == 2) {
                    //test
                    Solo.classique(primaryStage);
                }else if (select == 3) {
                    Menu menu = new Menu();
                    menu.start(primaryStage);
                }
            }
        });

        ((Pane) gameScene.getRoot()).getChildren().add(Interface);
        
    }

}