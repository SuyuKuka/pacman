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
    private Text scoreText;
    private Group[] livegroup;
    private boolean lose = false;
    private StackPane Interface = new StackPane();
    private int winVerif=0;

    public MenuGraphics(Scene gameScene, Stage primaryStage) {
        this.gameScene = gameScene;
        this.primaryStage=primaryStage;
    }

    public void displayScore(MazeState state) {
        if (scoreText == null) {
            // Création d'un texte
            scoreText = new Text();
            // Position du score 
            scoreText.setTranslateX((state.getWidth() + state.getHeight()) * 30 / 2);
            scoreText.setTranslateY((state.getHeight() + state.getWidth()) * 30 / 6 - 50);
            // Configuration du texte
            scoreText.setFont(Font.loadFont("file:src/main/resources/fonts/slkscr.ttf", 24));
            scoreText.setFill(Color.WHITE);
            // Ajoute le texte au groupe root
            root.getChildren().add(scoreText);
            // Met le group dans la scene
            ((Pane) gameScene.getRoot()).getChildren().add(root);
        }
        // Affiche le score
        scoreText.setText("Score: " + state.getScore()); // Total : 154 score en classique
        if(state.getScore()==171 && winVerif<1){
            winVerif++;
            win(state);
        }
    }

    private ImageView setLives(MazeState state) {
        ImageView imageView = new ImageView();
        Image image = new Image("file:src/main/resources/pacman.png");
        imageView.setImage(image);
        imageView.setY((state.getHeight() + state.getWidth()) * 30 / 2 - 50);
        imageView.setFitWidth(state.getWidth() * 1.4);
        imageView.setPreserveRatio(true);
        return imageView;
    }

    public void displayLives(MazeState state) {
        livegroup = new Group[state.getLives()];
        for (int i = 0; i < livegroup.length; i++) {
            ImageView live = setLives(state);
            live.setX(((state.getWidth() + state.getHeight() + i * 1.9) * 30 / 2));
            livegroup[i] = new Group();
            livegroup[i].getChildren().add(live);
            if (!((Pane) gameScene.getRoot()).getChildren().contains(livegroup[i])) {
                ((Pane) gameScene.getRoot()).getChildren().add(livegroup[i]);
            }
        }
    }

    public void removeLife(MazeState state) {
        if (state.getLives() < state.getTotalLive()) {
            livegroup[state.getLives()].setVisible(false);
        }

        if (state.getLives() == 0 && !lose) {
            lose = true;
            gameOver(state);
        }
    }

    public void gameOver(MazeState state) {
        if(lose){
            Rectangle transparent = new Rectangle(0, 0, primaryStage.getWidth(), primaryStage.getHeight() - 40);
            
            transparent.setFill(Color.GRAY);
            transparent.setOpacity(0.6);

            Text gameover = Menu.createText("GameOver", 104);
            Text score = Menu.createText("Score : " + state.getScore(), 64);
        
            Text continuer = Menu.createText("Continue", 38);
            Text select1 = Menu.createText(">", 38);
            HBox option1 = Menu.createHBox(20, select1, continuer);
            continuer.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
                    
            });

            Text retry = Menu.createText("Retry", 38);
            Text select2 = Menu.createText(">", 38);
            HBox option2 = Menu.createHBox(20, select2, retry);
            select2.setVisible(false);
            retry.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
                Menu.selected = 1;
                lose = false;
                Solo.classique(primaryStage);
            });

            Text quitter = Menu.createText("Quit", 38);
            Text select3 = Menu.createText(">", 38);
            HBox option3 = Menu.createHBox(20, select3, quitter);
            select3.setVisible(false);
            quitter.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
                Menu quit = new Menu();
                quit.start(primaryStage);
            });

            Text cleanPosition = Menu.createText(" ", 38);
            HBox posHBox = Menu.createHBox(40, cleanPosition);

            VBox position = new VBox(100);
            VBox titlePos = new VBox(0);
            VBox titlePosPos = new VBox(50);
            VBox textPos = new VBox(15);
                    
            textPos.getChildren().addAll(option1, option2, option3);
            textPos.setAlignment(Pos.CENTER);

            titlePos.getChildren().addAll(gameover, score);
            titlePos.setAlignment(Pos.CENTER);

            titlePosPos.getChildren().addAll(titlePos, textPos);
            titlePosPos.setAlignment(Pos.CENTER);

            position.getChildren().addAll(posHBox, titlePosPos);
            position.setAlignment(Pos.TOP_CENTER);


            ((Pane) gameScene.getRoot()).getChildren().remove(Interface);

            Interface.getChildren().addAll(transparent, position);

            Text[] allText = {select1, select2, select3};
            Menu.selector(gameScene, allText);

            gameScene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    switch (Menu.selected) {
                        case 1:
                            // Continue sur le prochain niveau
                            break;

                        case 2:
                            Menu.selected = 1;
                            lose = false;
                            Solo.classique(primaryStage);
                            break;

                        case 3:
                            Menu menu = new Menu();
                            menu.start(primaryStage);
                            break;

                        default:
                            break;
                    }
                }
            });

            ((Pane) gameScene.getRoot()).getChildren().add(Interface);
        }
    }

    public void win(MazeState state){
        state.playerWin();

        Rectangle transparent = new Rectangle(0, 0, primaryStage.getWidth(), primaryStage.getHeight() - 40);
            
        transparent.setFill(Color.GRAY);
        transparent.setOpacity(0.6);

        Text win = Menu.createText("You Win !", 104);
        Text score = Menu.createText("Score : " + state.getScore(), 64);
    
        Text continuer = Menu.createText("Continue", 38);
        Text select1 = Menu.createText(">", 38);
        HBox option1 = Menu.createHBox(20, select1, continuer);
        continuer.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
                
        });

        Text retry = Menu.createText("Retry", 38);
        Text select2 = Menu.createText(">", 38);
        HBox option2 = Menu.createHBox(20, select2, retry);
        select2.setVisible(false);
        retry.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            Menu.selected = 1;
            lose = false;
            Solo.classique(primaryStage);
        });

        Text quitter = Menu.createText("Quit", 38);
        Text select3 = Menu.createText(">", 38);
        HBox option3 = Menu.createHBox(20, select3, quitter);
        select3.setVisible(false);
        quitter.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            Menu quit = new Menu();
            quit.start(primaryStage);
        });

        Text cleanPosition = Menu.createText(" ", 38);
        HBox posHBox = Menu.createHBox(40, cleanPosition);

        VBox position = new VBox(100);
        VBox titlePos = new VBox(0);
        VBox titlePosPos = new VBox(50);
        VBox textPos = new VBox(15);
                
        textPos.getChildren().addAll(option1, option2, option3);
        textPos.setAlignment(Pos.CENTER);

        titlePos.getChildren().addAll(win, score);
        titlePos.setAlignment(Pos.CENTER);

        titlePosPos.getChildren().addAll(titlePos, textPos);
        titlePosPos.setAlignment(Pos.CENTER);

        position.getChildren().addAll(posHBox, titlePosPos);
        position.setAlignment(Pos.TOP_CENTER);


        ((Pane) gameScene.getRoot()).getChildren().remove(Interface);

        Interface.getChildren().addAll(transparent, position);

        Text[] allText = {select1, select2, select3};
        Menu.selector(gameScene, allText);

        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                switch (Menu.selected) {
                    case 1:
                        // Continue sur le prochain niveau
                        break;

                    case 2:
                        Menu.selected = 1;
                        lose = false;
                        Solo.classique(primaryStage);
                        break;

                    case 3:
                        Menu menu = new Menu();
                        menu.start(primaryStage);
                        break;

                    default:
                        break;
                }
            }
        });

        ((Pane) gameScene.getRoot()).getChildren().add(Interface);
    }

}