package gui;

import gui.menu.Menu;
import gui.menu.Solo;
import gui.menu.Menu.KeySelector;
import gui.menu.Menu.MouseSelector;
import javafx.event.EventHandler;
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
import model.Direction;
import model.MazeState;
import model.PacMan;
import utils.Resources;
import utils.Music;

public final class InGameGui {
    /** Le stage. */
    private static Stage primaryStage;
    /** La{@code Scene}actuelle. */
    private Scene gameScene;
    /** Le{@code StackPane}racine de tous les{@code Pane}. */
    private StackPane scorePane = new StackPane();
    /** Le{@code StackPane}de fin de jeu. */
    private StackPane endGamePane = new StackPane();
    /** Le{@code StackPane}lorsque le jeu est en pause. */
    private StackPane pausePane = new StackPane();

    /** Arrière-plan assombri. */
    private static Rectangle dimBackground;
    /** Menu de fin de jeu. */
    private VBox endGameMenu;
    /** Menu pause. */
    private VBox pauseMenu;
    /** Nombre de choix. */
    private static final int NB_CHOICE = 3;
    /** Les ">" du menu de fin de jeu. */
    private static Text[] endGameSelects = new Text[NB_CHOICE];
    /** Les choix du menu de fin de jeu. */
    private static Text[] endgameOptions = new Text[NB_CHOICE];
    /** Les ">" du menu pause. */
    private static Text[] pauseSelects = new Text[NB_CHOICE];
    /** Les choix du menu pause. */
    private static Text[] pauseOptions = new Text[NB_CHOICE];

    /** Indice du niveau choisi. */
    private int currentLevel;
    /** Le{@code Text}contenant le score. */
    private Text scoreText;
    /** Un tableau contenant les images des vies de Pac-Man. */
    private ImageView[] lives;
    /** Boolean indiquant si la partie est finie. */
    private boolean end;
    /** Boolean indiquant si la partie est mise en pause. */
    private boolean paused;

    /** Implémentation du singleton. */
    public static final InGameGui INSTANCE = new InGameGui();

    /**
     * Méthode qui change le {@link #primaryStage} et la scene du jeu
     * {@link #gameScene}, et réinitialise {@link #end} en{@code false}.
     * @param stage Le nouveau{@code Stage}
     * @param scene La nouvelle{@code Scene}
     */
    public static void setInGameGui(final Stage stage, final Scene scene) {
        InGameGui.primaryStage = stage;
        INSTANCE.gameScene = scene;
        INSTANCE.reset();
        Music.playSound("start", () -> Music.playLoopingBgMusic("siren_1"));
    }

    /** Réinitialise les menus in game. */
    public void reset() {
        end = false;
        paused = false;
        scorePane.getChildren().clear();
        endGamePane.getChildren().clear();
        pausePane.getChildren().clear();
        ((Pane) gameScene.getRoot()).getChildren().clear();
        setPauseSelects(new Text[NB_CHOICE]);
        setPauseOptions(new Text[NB_CHOICE]);
        setEndGameSelects(new Text[NB_CHOICE]);
        setEndgameOptions(new Text[NB_CHOICE]);
        pauseMenu = pauseMenu();
        addPauseHandlers();
    }

    public static void setPauseSelects(final Text[] pauseSelects) {
        InGameGui.pauseSelects = pauseSelects;
    }

    public static void setPauseOptions(final Text[] pauseOptions) {
        InGameGui.pauseOptions = pauseOptions;
    }

    public static void setEndGameSelects(final Text[] endGameSelects) {
        InGameGui.endGameSelects = endGameSelects;
    }

    public static void setEndgameOptions(final Text[] endgameOptions) {
        InGameGui.endgameOptions = endgameOptions;
    }

    /**
     * Set le fond assombri par rapport aux dimensions
     * du {@link #primaryStage}.
     */
    public static void setDimBackground() {
        final int heightDiff = 40;
        final double opacity = 0.6;
        dimBackground = new Rectangle(
            0, 0, InGameGui.primaryStage.getWidth(),
            InGameGui.primaryStage.getHeight() - heightDiff
        );
        dimBackground.setFill(Color.GRAY);
        dimBackground.setOpacity(opacity);
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
        // Met le groupe dans la scene
        scorePane.getChildren().add(scoreText);
        ((Pane) gameScene.getRoot()).getChildren().add(scorePane);
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
            endGameMenu = endGameMenu(true, state);
            showEndGame();
            addEndHandlers();
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
     * Si Pac-Man n'a plus de vie, {@link #endGameMenu} est appelée.
     * @param state Le {@link MazeState} actuel
     */
    public void updateLives(final MazeState state) {
        if (state.getLives() < state.getTotalLive()) {
            lives[state.getLives()].setVisible(false);
        }

        if (state.getLives() == 0 && !end) {
            end = true;
            endGameMenu = endGameMenu(false, state);
            showEndGame();
            addEndHandlers();
        }
    }

    /**
     * Méthode qui retourne la{@code VBox}de la fenêtre de fin de jeu.
     * @param won {@code boolean}indiquant le titre à afficher, win ou gameover
     * @param state Le {@link MazeState} actuel
     * @return {@code VBox}de la fenêtre de fin de jeu
     */
    public static VBox endGameMenu(final boolean won, final MazeState state) {
        final int titleSize = 104;
        final int scoreSize = 64;
        final int textSize = 38;
        final int textSpacing = 20;
        final int optionSpacing = 15;
        final int mainBoxSpacing = 50;

        String title = won ? "You Win !" : "GameOver";
        if (won) {
            Music.stopAllSounds();
            Music.playSound("gameWin");
        }
        Text gameover = Menu.createText(title, titleSize);
        Text score = Menu.createText("Score : " + state.getScore(), scoreSize);

        Text select0 = Menu.createText(">", textSize);
        Text continuer = Menu.createText("Continue", textSize);
        HBox option0 = Menu.createHBox(
            textSpacing, Pos.CENTER, select0, continuer
        );

        Text select1 = Menu.createText(">", textSize);
        Text retry = Menu.createText("Retry", textSize);
        HBox option1 = Menu.createHBox(textSpacing, Pos.CENTER, select1, retry);

        Text select2 = Menu.createText(">", textSize);
        Text quitter = Menu.createText("Quit", textSize);
        HBox option2 = Menu.createHBox(
            textSpacing, Pos.CENTER, select2, quitter
        );

        endGameSelects[0] = select0;
        endGameSelects[1] = select1;
        endGameSelects[2] = select2;

        endgameOptions[0] = continuer;
        endgameOptions[1] = retry;
        endgameOptions[2] = quitter;

        VBox titleBox = Menu.createVBox(0, Pos.CENTER, gameover, score);
        VBox optionBox = Menu.createVBox(
            optionSpacing, Pos.CENTER, option0, option1, option2
        );
        return Menu.createVBox(
            mainBoxSpacing, Pos.CENTER, titleBox, optionBox
        );
    }

    /**
     * Ajoute {@link #endGamePane} au {@link #gameScene} ce qui
     * affiche la fenêtre de fin de partie.
     */
    private void showEndGame() {
        if (dimBackground == null) {
            setDimBackground();
        }
        endGamePane.getChildren().addAll(dimBackground, endGameMenu);
        ((Pane) gameScene.getRoot()).getChildren().add(endGamePane);
    }

    private void addEndHandlers() {
        EventHandler<MouseEvent> next = new EventHandler<>() {
            @Override
            public void handle(final MouseEvent mouseEvent) {
                //TODO
            }
        };
        EventHandler<MouseEvent> retry = new EventHandler<>() {
            @Override
            public void handle(final MouseEvent mouseEvent) {
                endgameOptions[1].removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
                Solo.startLevel(primaryStage, currentLevel);
            }
        };
        endgameOptions[0].addEventHandler(MouseEvent.MOUSE_CLICKED, next);
        endgameOptions[1].addEventHandler(MouseEvent.MOUSE_CLICKED, retry);
        endgameOptions[2].addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            Menu quit = new Menu();
            Menu.setMusicStarted(false);
            quit.start(primaryStage);
        });

        KeySelector keySel = new KeySelector(
            endGameSelects, primaryStage, false
        );
        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, keySel::handle);

        int select = 0;
        for (Text text : endgameOptions) {
            MouseSelector m = new MouseSelector(
                endGameSelects, select++
            );
            text.setOnMouseEntered(m::handle);
        }

        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                INSTANCE.endGamePane.getChildren().clear();
                switch (Menu.getSelected()) {
                    case 0:
                        // Continue sur le prochain niveau
                        break;

                    case 1:
                        Solo.startLevel(primaryStage, currentLevel);
                        break;

                    case 2:
                        Menu quit = new Menu();
                        Menu.setMusicStarted(false);
                        quit.start(primaryStage);
                        break;

                    default:
                        break;
                }
            }
        });
    }

    /**
     * Met en pause le jeu et affiche le menu de pause si{@code pause}
     * est{@code true}.
     * @param pause {@code boolean}
     */
    public static void pause(final boolean pause) {
        if (pause) {
            INSTANCE.paused = true;
            INSTANCE.showPauseMenu();
        } else {
            INSTANCE.paused = false;
            INSTANCE.pausePane.getChildren().clear();
            ((Pane) INSTANCE.gameScene.getRoot())
                .getChildren().remove(INSTANCE.pausePane);
        }
    }

    private static VBox pauseMenu() {
        PacMan.INSTANCE.setPacManMoved(0);
        PacMan.INSTANCE.setDirection(Direction.NONE);

        final int titleSize = 104;
        final int textSize = 38;
        final int textSpacing = 20;
        final int optionSpacing = 15;
        final int mainBoxSpacing = 50;

        String title = "Pause";
        Text pauseText = Menu.createText(title, titleSize);

        Text select0 = Menu.createText(">", textSize);
        Text continuer = Menu.createText("Continue", textSize);
        HBox option0 = Menu.createHBox(
            textSpacing, Pos.CENTER, select0, continuer
        );

        Text select1 = Menu.createText(">", textSize);
        Text retry = Menu.createText("Retry", textSize);
        HBox option1 = Menu.createHBox(textSpacing, Pos.CENTER, select1, retry);

        Text select2 = Menu.createText(">", textSize);
        Text quitter = Menu.createText("Quit", textSize);
        HBox option2 = Menu.createHBox(
            textSpacing, Pos.CENTER, select2, quitter
        );

        VBox titleBox = Menu.createVBox(0, Pos.CENTER, pauseText);
        VBox optionBox = Menu.createVBox(
            optionSpacing, Pos.CENTER, option0, option1, option2
        );

        pauseSelects[0] = select0;
        pauseSelects[1] = select1;
        pauseSelects[2] = select2;

        pauseOptions[0] = continuer;
        pauseOptions[1] = retry;
        pauseOptions[2] = quitter;

        return Menu.createVBox(
            mainBoxSpacing, Pos.CENTER, titleBox, optionBox
        );
    }

    private void showPauseMenu() {
        if (dimBackground == null) {
            setDimBackground();
        }
        pausePane.getChildren().addAll(dimBackground, pauseMenu);
        ((Pane) gameScene.getRoot()).getChildren().add(pausePane);
    }

    private void addPauseHandlers() {
        EventHandler<MouseEvent> retry = new EventHandler<>() {
            @Override
            public void handle(final MouseEvent mouseEvent) {
                pauseOptions[1].removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
                Solo.startLevel(primaryStage, currentLevel);
            }
        };

        pauseOptions[0].addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            pause(false);
        });
        pauseOptions[1].addEventHandler(MouseEvent.MOUSE_CLICKED, retry);
        pauseOptions[2].addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            Menu quit = new Menu();
            Menu.setMusicStarted(false);
            quit.start(primaryStage);
        });

        KeySelector keySel = new KeySelector(
            pauseSelects, primaryStage, true
        );
        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, keySel::handle);

        int select = 0;
        for (Text text : pauseOptions) {
            MouseSelector m = new MouseSelector(
                pauseSelects, select++
            );
            text.setOnMouseEntered(m::handle);
        }

        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                switch (Menu.getSelected()) {
                    case 0:
                        pause(false);
                        break;

                    case 1:
                        Solo.startLevel(primaryStage, currentLevel);
                        break;

                    case 2:
                        Menu quitGame = new Menu();
                        Menu.setMusicStarted(false);
                        quitGame.start(primaryStage);
                        break;

                    default:
                        break;
                }
            }
        });
    }

    /**
     * Retourne true si la partie est en pause.
     * @return {@code boolean}
     */
    public boolean isPaused() {
        return paused;
    }

    public void setCurrentLevel(final int currentLevel) {
        this.currentLevel = currentLevel;
    }
}
