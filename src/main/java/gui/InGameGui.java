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
    /** Le stage. */
    private Stage primaryStage;
    /** La{@code Scene}actuelle. */
    private Scene gameScene;
    /** Le{@code StackPane}de fin de jeu. */
    private StackPane endGamePane = new StackPane();
    /** Le{@code StackPane}racine de tous les{@code Pane}. */
    private StackPane rootPane = new StackPane();
    /** Le{@code Text}contenant le score. */
    private Text scoreText;
    /** Un tableau contenant les images des vies de Pac-Man. */
    private ImageView[] lives;
    /** Boolean indiquant si la partie est finie. */
    private boolean end;

    /** Implémentation du singleton. */
    public static final InGameGui INSTANCE = new InGameGui();

    /**
     * Méthode qui change le {@link #primaryStage} et la scene du jeu
     * {@link #gameScene}, et réinitialise {@link #end} en{@code false}.
     * @param stage Le nouveau{@code Stage}
     * @param scene La nouvelle{@code Scene}
     */
    public static void setInGameGui(final Stage stage, final Scene scene) {
        INSTANCE.primaryStage = stage;
        INSTANCE.gameScene = scene;
        INSTANCE.end = false;
    }

    /**
     * Méthode qui affiche la zone du score lors du démarrage de la partie.
     * @param state Le {@link MazeState} actuel
     */
    public void displayScore(final MazeState state) {
        // Création d'un texte
        scoreText = new Text();
        // Position du score
        final int translateX = 30 / 2;
        final int translateY1 = 30 / 6;
        final int translateY2 = 50;
        scoreText.setTranslateX(
            (double) (state.getWidth() + state.getHeight()) * translateX
        );
        scoreText.setTranslateY(
            (double) (state.getHeight() + state.getWidth())
            * translateY1 - translateY2
        );
        // Configuration du texte
        final int fontSize = 24;
        scoreText.setFont(Font.loadFont(Resources.getFile("slkscr"), fontSize));
        scoreText.setFill(Color.WHITE);
        // Ajoute le texte au groupe rootPane
        rootPane.getChildren().add(scoreText);
        // Met le groupe dans la scene
        if (!((Pane) gameScene.getRoot()).getChildren().contains(scoreText)) {
            ((Pane) gameScene.getRoot()).getChildren().add(scoreText);
        }
    }

    /**
     * Méthode mettant à jour le score à chaque tick.
     * @param state Le {@link MazeState} actuel
     */
    public void updateScore(final MazeState state) {
        // Affiche le score
        scoreText.setText("Score: " + state.getScore());
        // Total : 150 pac-gommes en classique
        if (state.getPacgomme() == state.getMaxScore() && !end) {
            end = true;
            state.playerWin();
            endGame(true, state);
        }
    }

    /**
     * Méthode qui initialise une image du tableau {@link #lives}.
     * @param state Le {@link MazeState} actuel
     * @return {@code ImageView}
     */
    private ImageView setLife(final MazeState state) {
        ImageView imageView = new ImageView();
        Image image = Resources.getImage("pacman");

        final int yScale1 = 30 / 2;
        final int yScale2 = 50;
        final double widthScale = 1.4;
        imageView.setImage(image);
        imageView.setY(
            (double) (state.getHeight() + state.getWidth()) * yScale1 - yScale2
        );
        imageView.setFitWidth(state.getWidth() * widthScale);
        imageView.setPreserveRatio(true);
        return imageView;
    }

    /**
     * Méthode affichant les vies de Pac-Man au démarrage d'une partie.
     * @param state Le {@link MazeState} actuel
     */
    public void displayLives(final MazeState state) {
        final double spacing = 1.9;
        final int translateX = 30 / 2;
        lives = new ImageView[state.getLives()];
        for (int i = 0; i < lives.length; i++) {
            ImageView life = setLife(state);
            life.setX(
                ((state.getWidth() + state.getHeight() + i * spacing)
                * translateX)
            );
            lives[i] = life;
            if (!((Pane) gameScene.getRoot()).getChildren()
                .contains(lives[i])) {
                ((Pane) gameScene.getRoot()).getChildren().add(lives[i]);
            }
        }
    }

    /**
     * Méthode qui met à jour la visibilité des vies de Pac-Man.
     * Si Pac-Man n'a plus de vie, {@link #endGame} est appelée.
     * @param state Le {@link MazeState} actuel
     */
    public void updateLives(final MazeState state) {
        if (state.getLives() < state.getTotalLive()) {
            lives[state.getLives()].setVisible(false);
        }

        if (state.getLives() == 0 && !end) {
            end = true;
            endGame(false, state);
        }
    }

    /**
     * Méthode appelée en fin de partie. Elle ajoute {@link #endGamePane} au
     * {@link #gameScene} ce qui affiche la fenêtre de fin de partie.
     * @param won {@code boolean}indiquant le titre à afficher, win ou gameover
     * @param state Le {@link MazeState} actuel
     */
    public void endGame(final boolean won, final MazeState state) {
        final int heightDiff = 40;
        final double opacity = 0.6;
        final int titleSize = 104;
        final int scoreSize = 64;
        final int textSize = 38;
        final int textSpacing = 20;
        final int optionSpacing = 15;
        final int mainBoxSpacing = 50;

        Rectangle transparent = new Rectangle(
            0, 0, primaryStage.getWidth(), primaryStage.getHeight() - heightDiff
        );

        transparent.setFill(Color.GRAY);
        transparent.setOpacity(opacity);

        String title = won ? "You Win !" : "GameOver";
        Text gameover = Menu.createText(title, titleSize);
        Text score = Menu.createText("Score : " + state.getScore(), scoreSize);

        Text select0 = Menu.createText(">", textSize);
        Text continuer = Menu.createText("Continue", textSize);
        HBox option0 = Menu.createHBox(
            textSpacing, Pos.CENTER, select0, continuer
        );
        continuer.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {

        });

        Text select1 = Menu.createText(">", textSize);
        Text retry = Menu.createText("Retry", textSize);
        HBox option1 = Menu.createHBox(textSpacing, Pos.CENTER, select1, retry);
        select1.setVisible(false);
        retry.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            Solo.startLevel(primaryStage);
        });

        Text quitter = Menu.createText("Quit", textSize);
        Text select2 = Menu.createText(">", textSize);
        HBox option2 = Menu.createHBox(
            textSpacing, Pos.CENTER, select2, quitter
        );
        select2.setVisible(false);
        quitter.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            Menu quit = new Menu();
            quit.start(primaryStage);
        });

        VBox titleBox = Menu.createVBox(0, Pos.CENTER, gameover, score);
        VBox optionBox = Menu.createVBox(
            optionSpacing, Pos.CENTER, option0, option1, option2
        );
        VBox mainBox = Menu.createVBox(
            mainBoxSpacing, Pos.CENTER, titleBox, optionBox
        );

        endGamePane.getChildren().addAll(transparent, mainBox);
        ((Pane) gameScene.getRoot()).getChildren().add(endGamePane);

        Text[] choices = {select0, select1, select2};
        Text[] options = {continuer, retry, quitter};
        int select = 0;
        Menu.selector(gameScene, choices, primaryStage);
        for (Text text : options) {
            Menu.mouseSelector(text, choices, select++);
        }

        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                INSTANCE.endGamePane.getChildren().clear();
                switch (Menu.getSelected()) {
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
