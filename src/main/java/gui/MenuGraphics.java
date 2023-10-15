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

    public MenuGraphics(Scene gameScene, Stage primaryStage) {
        this.gameScene = gameScene;
        this.primaryStage=primaryStage;
    }

    public void displayScore(MazeState state) {
        if (text_score == null) {
            // Création d'un texte
            text_score = new Text();
            // Position du score // 30 = scale, 2 et 6 valeur arbitraire
            text_score.setTranslateX(primaryStage.getWidth()/1.18);
            text_score.setTranslateY(primaryStage.getHeight()/4);
            System.out.println(primaryStage.getWidth());
            System.out.println(primaryStage.getHeight());
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
        if(lose){
            Transparent = new Rectangle(0, 0, primaryStage.getWidth(), primaryStage.getHeight()-40);
            

            Transparent.setFill(Color.GRAY);
            Transparent.setOpacity(0.6);

            Text GameOver = Menu.createText("GameOver", 124);
            Text Score = Menu.createText("Score : " + state.getScore(), 64);
        
            Text Continue = Menu.createText("Continue", 38);
            Text select1 = Menu.createText(">", 38);
            HBox option1 = Menu.createHBox(20, select1, Continue);
            Continue.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
                    
            });

            Text Retry = Menu.createText("Retry", 38);
            Text select2 = Menu.createText(">", 38);
            HBox option2 = Menu.createHBox(20, select2, Retry);
            select2.setVisible(false);
            Retry.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
                    lose =false;
                    state.reset();
                    Solo.classique(primaryStage);
            });

            Text Quit = Menu.createText("Quit", 38);
            Text select3 = Menu.createText(">", 38);
            HBox option3 = Menu.createHBox(20, select3, Quit);
            select3.setVisible(false);
            Quit.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
                    Menu quit = new Menu();
                    quit.start(primaryStage);
            });

            VBox titlePos = new VBox(40);
            VBox textPos = new VBox(20);
                    
            textPos.getChildren().addAll(Score, option1, option2, option3);
            textPos.setAlignment(Pos.CENTER);

            titlePos.getChildren().addAll(GameOver, textPos);
            titlePos.setAlignment(Pos.CENTER);

            ((Pane) gameScene.getRoot()).getChildren().remove(Interface);

            Interface.getChildren().addAll(Transparent, titlePos);

            Text[] allText = {select1, select2, select3};
            Menu.selector(gameScene, allText);

            gameScene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    System.out.println("enter "+ select);
                    // (fonction)
                    if (Menu.select == 1) {
                        // Continue sur le prochain niveau
                    }else if (Menu.select == 2) {
                        ((Pane) gameScene.getRoot()).getChildren().clear();
                        lose =false;
                        state.reset();
                        Solo.classique(primaryStage);
                    }else if (Menu.select == 3) {
                        System.out.println("quit");
                        Menu menu = new Menu();
                        menu.start(primaryStage);
                    }
                }
            });

            ((Pane) gameScene.getRoot()).getChildren().add(Interface);
        }
    }

}