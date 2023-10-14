package gui;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import model.MazeState;

public class MenuGraphics {
    private Stage primaryStage;
    private Scene gameScene;
    private Group root = new Group();
    private Text text_score;
    private Group[] livegroup;
    private int select = 1;
    private boolean lose = false;
    private Group Interface = new Group();
    private Rectangle Transparent;
    private Rectangle TextPos;
    private Text GameOver = new Text("GameOver");
    private Text Continue = new Text("Continue");
    private Text Retry = new Text("Retry");
    private Text Menu = new Text("Quit");
    private Text Option1 = new Text(">");
    private Text Option2 = new Text(">");
    private Text Option3 = new Text(">");

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
        Transparent = new Rectangle(0, 0, state.getHeight() * 100, state.getWidth() * 100);
        TextPos = new Rectangle(0, 0, state.getHeight() * 30, state.getWidth() * 30);

        Transparent.setFill(Color.BLACK);
        Transparent.setOpacity(0.4);

        // TextPos.setFill(Color.WHITE);
        TextPos.setOpacity(0);

        GameOver.setX(TextPos.getWidth() / 4 + 42);
        GameOver.setY(200);
        GameOver.setFont(Font.loadFont("file:src\\main\\resources\\fonts\\small_pixel-7.ttf", 120));
        GameOver.setFill(Color.WHITE);

        Text Score = new Text("Score: " + state.getScore());
        Score.setX(TextPos.getWidth() / 3 + GameOver.getX() / 4 + 10);
        Score.setY(280);
        Score.setFont(Font.loadFont("file:src\\main\\resources\\fonts\\small_pixel-7.ttf", 75));
        Score.setFill(Color.WHITE);

        Continue.setX(TextPos.getWidth() / 3 + GameOver.getX() / 2 + 24);
        Continue.setY(400);
        Continue.setFont(Font.loadFont("file:src\\main\\resources\\fonts\\small_pixel-7.ttf", 42));
        Continue.setFill(Color.WHITE);

        Retry.setX(TextPos.getWidth() / 3 + GameOver.getX() / 2 + 42);
        Retry.setY(440);
        Retry.setFont(Font.loadFont("file:src\\main\\resources\\fonts\\small_pixel-7.ttf", 42));
        Retry.setFill(Color.WHITE);

        Menu.setX(TextPos.getWidth() / 3 + GameOver.getX() / 2 + 56);
        Menu.setY(480);
        Menu.setFont(Font.loadFont("file:src\\main\\resources\\fonts\\small_pixel-7.ttf", 42));
        Menu.setFill(Color.WHITE);

        Option1.setX(TextPos.getWidth() / 3 + GameOver.getX() / 2 + 5);
        Option1.setY(400);
        Option1.setFont(Font.loadFont("file:src\\main\\resources\\fonts\\small_pixel-7.ttf", 42));
        Option1.setFill(Color.WHITE);

        Option2.setX(TextPos.getWidth() / 3 + GameOver.getX() / 2 + 5);
        Option2.setY(440);
        Option2.setFont(Font.loadFont("file:src\\main\\resources\\fonts\\small_pixel-7.ttf", 42));
        Option2.setFill(Color.WHITE);
        Option2.setVisible(false);

        Option3.setX(TextPos.getWidth() / 3 + GameOver.getX() / 2 + 5);
        Option3.setY(480);
        Option3.setFont(Font.loadFont("file:src\\main\\resources\\fonts\\small_pixel-7.ttf", 42));
        Option3.setFill(Color.WHITE);
        Option3.setVisible(false);

        Interface.getChildren().addAll(Transparent, TextPos, GameOver, Score, Continue, Retry, Menu, Option1, Option2,
                Option3);

        Continue.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            System.out.println("Continue MOUse");
        });

        Retry.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            System.out.println("Retry MOUse");
        });
        Menu.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            System.out.println("Menu MOUse");
        });

        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.UP) {
                // Flèche vers le haut
                select--;
                if (select == 0 || select == -1) {
                    select = 3;
                }
            } else if (keyEvent.getCode() == KeyCode.DOWN) {
                // Flèche vers le bas
                select++;
                if (select == 4 || select == 5) {
                    select = 1;
                }
            } else if (keyEvent.getCode() == KeyCode.ENTER) {
                // (fonction)
                if (select == 1) {
                    // Continue sur le prochain niveau
                }
                if (select == 2) {
                    //test
                    Solo.classique(primaryStage);
                }
                if (select == 3) {
                    Menu menu = new Menu();
                    menu.start(primaryStage);
                }
            }
            if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.UP) {
                if (select == 1) {
                    Option1.setVisible(true);
                    Option2.setVisible(false);
                    Option3.setVisible(false);
                }
                if (select == 2) {
                    Option1.setVisible(false);
                    Option2.setVisible(true);
                    Option3.setVisible(false);
                }
                if (select == 3) {
                    Option1.setVisible(false);
                    Option2.setVisible(false);
                    Option3.setVisible(true);
                }
            }
        });
        ((Pane) gameScene.getRoot()).getChildren().add(Interface);
    }

}