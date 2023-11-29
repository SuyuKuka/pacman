package gui;

import java.util.ArrayList;
import java.util.List;

import gui.menu.Menu;
import gui.menu.Multiplayer;
import gui.menu.Solo;
import gui.menu.Selector;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import model.Ghost;
import model.MazeState;
import model.Ghost.GhostState;
import utils.Music;
import utils.Resources;

public final class InGameGui {
    /** La{@code Scene}actuelle. */
    private Scene gameScene;
    /** Le{@code StackPane}de fin de jeu. */
    private StackPane endGamePane = new StackPane();
    /** Le{@code StackPane}lorsque le jeu est en pause. */
    private StackPane pausePane = new StackPane();

    /** Arrière-plan assombri. */
    private Rectangle dimBackground = makeDimBackground();
    /** Menu de fin de jeu. */
    private VBox endGameMenu;
    /** Les ">" du menu de fin de jeu. */
    private ArrayList<Text> endGameSelects = new ArrayList<>();
    /** Les choix du menu de fin de jeu. */
    private ArrayList<Text> endgameOptions = new ArrayList<>();
    /** Les ">" du menu pause. */
    private ArrayList<Text> pauseSelects = new ArrayList<>();
    /** Les choix du menu pause. */
    private ArrayList<Text> pauseOptions = new ArrayList<>();
    /** Menu pause. */
    private VBox pauseMenu = pauseMenu();

    /** Indice du niveau choisi. */
    private static int currentLevel;
    /** {@code VBox}contenant le score et les vies. */
    private VBox infos;
    /** Le{@code Text}contenant le score. */
    private Text scoreText;
    /** Liste de{@code GraphicsUpdater}pour le score lorsqu'on mange un fantôme. */
    private static ArrayList<GraphicsUpdater> eatScoreUpdater = new ArrayList<>();
    /** Un tableau contenant les images des vies de Pac-Man. */
    private ImageView[] lives;
    /** Boolean indiquant si la partie est finie. */
    private boolean end = false;
    /** Boolean indiquant si la partie est mise en pause. */
    private static boolean paused = false;

    /** Versus ou Coop. */
    private String mode;

    private Selector selector;

    private static final String FONT = "small_pixel-7";

    /**
     * Constructeur{@code InGameGui}.
     * @param scene
     * @param mode
     */
    public InGameGui(final Scene scene, final String mode) {
        gameScene = scene;
        this.mode = mode;
        addPauseHandlers();
        setPaused(false);
        Music.setBgMusicClip(null);
        Music.playSound("start");
    }

    /**
     * Retourne un fond assombri par rapport aux dimensions du {@link Menu#mainStage}.
     * @return {@code Rectangle}fond assombri
     */
    public Rectangle makeDimBackground() {
        final int heightDiff = 40;
        final double opacity = 0.6;
        Rectangle dimBg = new Rectangle(
            0, 0, Menu.getMainStage().getWidth(),
            Menu.getMainStage().getHeight() - heightDiff
        );
        dimBg.widthProperty().bind(Menu.getMainStage().widthProperty());
        dimBg.heightProperty().bind(Menu.getMainStage().heightProperty().add(-heightDiff));
        dimBg.setFill(Color.GRAY);
        dimBg.setOpacity(opacity);

        return dimBg;
    }

    /**
     * Méthode qui ajoute les infos dans la scène de jeu.
     * @param state
     */
    public void displayInfos(final MazeState state) {
        StackPane rootPane = (StackPane) gameScene.getRoot();
        HBox gameBox = (HBox) (rootPane.getChildren().get(0));

        infos = new VBox();
        infos.setAlignment(Pos.CENTER);
        infos.setSpacing(rootPane.getHeight() / 2);
        makeScoreDisplay();
        makeLivesDisplay(state);

        gameBox.getChildren().add(infos);
    }

    /**
     * Méthode qui ajoute le texte avec le score dans {@link #infos}.
     */
    public void makeScoreDisplay() {
        // Création d'un texte
        Text title = new Text();
        scoreText = new Text();

        // Configuration du texte
        final int titleSize = 50;
        final int scoreSize = 40;
        final int spacing = 10;

        title.setText("Score");
        title.setFont(Font.loadFont(Resources.getPathOrContent(FONT), titleSize));
        title.setFill(Color.WHITE);
        scoreText.setFont(Font.loadFont(Resources.getPathOrContent(FONT), scoreSize));
        scoreText.setFill(Color.WHITE);

        VBox score = new VBox(title, scoreText);
        score.setAlignment(Pos.CENTER);
        score.setSpacing(spacing);

        infos.getChildren().add(score);
    }

    /**
     * Méthode mettant à jour le score à chaque tick.
     * @param state Le {@link MazeState} actuel
     */
    public void updateScore(final MazeState state) {
        // Affiche le score
        scoreText.setText(String.valueOf(state.getScore()));
        // Total : 244 pac-gommes en classique
        if (!end && state.getEatenPacgomme() >= state.getConfig().getTotalPacGomme()) {
            end = true;
            state.playerWin();
            endGameMenu = endGameMenu(true, state);
            showEndGame();
            addEndHandlers();
        }
    }

    /**
     * Affiche la quantité de points gagnée en mangeant un fantôme.
     * @param eatScore Quantité de points
     * @param ghost Le fantôme qui a été mangé
     */
    public static void showEatGhostPoints(final int eatScore, final Ghost ghost) {
        final int fontSize = 11;
        Text eatGhostPoints = new Text(String.valueOf(eatScore));
        eatGhostPoints.setFont(Font.loadFont(Resources.getPathOrContent(FONT), fontSize));
        eatGhostPoints.setFill(Color.WHITE);

        GraphicsUpdater updater = new GraphicsUpdater() {
            @Override
            public void update() {
                if (ghost.getGhostState().equals(GhostState.NORMAL)) {
                    GameView.INSTANCE.getMapPane().getChildren().remove(eatGhostPoints);
                    eatScoreUpdater.remove(this);
                }
            }

            @Override
            public Node getNode() {
                return eatGhostPoints;
            }
        };
        eatScoreUpdater.add(updater);
        GameView.INSTANCE.addGraphics(updater, (int) ghost.getPos().x(), (int) ghost.getPos().y());
    }

    /**
     * Méthode qui initialise une image du tableau {@link #lives}.
     * @return {@code ImageView}
     */
    private ImageView setLife() {
        ImageView imageView = new ImageView();
        Image image = Resources.getImage("pacman");

        final int ratio = 40;
        imageView.setImage(image);
        imageView.fitWidthProperty().bind(Menu.getMainStage().heightProperty().divide(ratio));
        imageView.setPreserveRatio(true);
        return imageView;
    }

    /**
     * Méthode qui ajoute les vies de Pac-Man dans {@link #infos}.
     * @param state Le {@link MazeState} actuel
     */
    public void makeLivesDisplay(final MazeState state) {
        HBox livesBox = new HBox();
        livesBox.setAlignment(Pos.CENTER);
        lives = new ImageView[state.getLives()];
        for (int i = 0; i < lives.length; i++) {
            ImageView life = setLife();
            lives[i] = life;
            livesBox.getChildren().add(lives[i]);
        }
        infos.getChildren().add(livesBox);
    }

    /**
     * Méthode qui met à jour la visibilité des vies de Pac-Man.
     * Si Pac-Man n'a plus de vie, {@link #endGameMenu} est appelée.
     * @param state Le {@link MazeState} actuel
     */
    public void updateLives(final MazeState state) {
        if (state.getLives() < state.getTotalLives()) {
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
    public VBox endGameMenu(final boolean won, final MazeState state) {
        final int titleSize = 104;
        final int scoreSize = 64;
        final int textSize = 38;
        final int textSpacing = 20;
        final int optionSpacing = 15;
        final int mainBoxSpacing = 50;

        String title = won ? "You Win !" : "GameOver";
        if (won) {
            Music.stopBackgroundMusic();
            Music.playSound("start");
        }
        Text gameover = Menu.createText(title, titleSize);
        Text score = Menu.createText("Score : " + state.getScore(), scoreSize);

        Text select0 = Menu.createText(">", textSize);
        Text restart = Menu.createText("Restart", textSize);
        HBox option0 = Menu.createHBox(textSpacing, Pos.CENTER, select0, restart);

        Text select1 = Menu.createText(">", textSize);
        Text menu = Menu.createText("Menu", textSize);
        HBox option1 = Menu.createHBox(
            textSpacing, Pos.CENTER, select1, menu
        );

        endGameSelects.add(0, select0);
        endGameSelects.add(1, select1);

        endgameOptions.add(0, restart);
        endgameOptions.add(1, menu);

        VBox titleBox = Menu.createVBox(0, Pos.CENTER, gameover, score);
        VBox optionBox = Menu.createVBox(optionSpacing, Pos.CENTER, option0, option1);

        return Menu.createVBox(mainBoxSpacing, Pos.CENTER, titleBox, optionBox);
    }

    /**
     * Ajoute {@link #endGamePane} au {@link #gameScene} ce qui
     * affiche la fenêtre de fin de partie.
     */
    private void showEndGame() {
        endGamePane.getChildren().addAll(dimBackground, endGameMenu);
        ((StackPane) gameScene.getRoot()).getChildren().add(endGamePane);
    }

    private void addEndHandlers() {
        selector = new Selector(endGameSelects, endgameOptions, gameScene, false);

        endgameOptions.get(0).setOnMouseClicked(event -> {
            if (mode.equals("Solo")) {
                Solo.startLevel(currentLevel);
            } else {
                Multiplayer.startLevel(currentLevel);
            }
            selector.getKeySelector().resetSelect();
        });
        endgameOptions.get(1).setOnMouseClicked(mouseEvent -> {
            Menu.INSTANCE.toMenu();
            selector.getKeySelector().resetSelect();
        });

        gameScene.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                switch (Menu.getSelected()) {
                    case 0:
                        if (mode.equals("Solo")) {
                            Solo.startLevel(currentLevel);
                        } else {
                            Multiplayer.startLevel(currentLevel);
                        }
                        break;

                    case 1:
                        Menu.INSTANCE.toMenu();
                        break;

                    default:
                        break;
                }
                selector.getKeySelector().resetSelect();
            }
        });
    }

    /**
     * Met en pause le jeu et affiche le menu de pause si{@code pause}
     * est{@code true}.
     * @param pause {@code boolean}
     */
    public void pause(final boolean pause) {
        if (pause) {
            setPaused(true);
            showPauseMenu();
            Music.pause();
        } else {
            setPaused(false);
            pausePane.getChildren().clear();
            ((StackPane) gameScene.getRoot()).getChildren().remove(pausePane);
            Music.resume();
        }
    }

    private VBox pauseMenu() {
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
        Text restart = Menu.createText("Restart", textSize);
        HBox option1 = Menu.createHBox(textSpacing, Pos.CENTER, select1, restart);

        Text select2 = Menu.createText(">", textSize);
        Text menu = Menu.createText("Menu", textSize);
        HBox option2 = Menu.createHBox(
            textSpacing, Pos.CENTER, select2, menu
        );

        VBox titleBox = Menu.createVBox(0, Pos.CENTER, pauseText);
        VBox optionBox = Menu.createVBox(
            optionSpacing, Pos.CENTER, option0, option1, option2
        );

        pauseSelects.add(0, select0);
        pauseSelects.add(1, select1);
        pauseSelects.add(2, select2);

        pauseOptions.add(0, continuer);
        pauseOptions.add(1, restart);
        pauseOptions.add(2, menu);

        return Menu.createVBox(
            mainBoxSpacing, Pos.CENTER, titleBox, optionBox
        );
    }

    private void showPauseMenu() {
        pausePane.getChildren().addAll(dimBackground, pauseMenu);
        ((StackPane) gameScene.getRoot()).getChildren().add(pausePane);

        Music.pause();
    }

    private void addPauseHandlers() {
        selector = new Selector(pauseSelects, pauseOptions, gameScene, true);

        pauseOptions.get(0).setOnMouseClicked(mouseEvent -> {
            pause(false);
            Music.resume();
            selector.getKeySelector().resetSelect();
        });
        pauseOptions.get(1).setOnMouseClicked(mouseEvent -> {
            pause(false);
            if (mode.equals("Solo")) {
                Solo.startLevel(currentLevel);
            } else {
                Multiplayer.startLevel(currentLevel);
            }
            selector.getKeySelector().resetSelect();
        });
        pauseOptions.get(2).setOnMouseClicked(mouseEvent -> {
            Menu.INSTANCE.toMenu();
            selector.getKeySelector().resetSelect();
        });

        gameScene.setOnKeyPressed(keyEvent -> {
            if (paused && keyEvent.getCode() == KeyCode.ENTER) {
                pause(false);
                switch (Menu.getSelected()) {
                    case 0:
                        Music.resume();
                        break;

                    case 1:
                        if (mode.equals("Solo")) {
                            Solo.startLevel(currentLevel);
                        } else {
                            Multiplayer.startLevel(currentLevel);
                        }
                        break;

                    case 2:
                        Menu.INSTANCE.toMenu();
                        break;

                    default:
                        break;
                }
                selector.getKeySelector().resetSelect();
            }
        });
    }

    /**
     * Retourne true si la partie est en pause.
     * @return {@code boolean}
     */
    public static boolean isPaused() {
        return paused;
    }

    public static void setPaused(final boolean paused) {
        InGameGui.paused = paused;
    }

    public static void setCurrentLevel(final int currentLevel) {
        InGameGui.currentLevel = currentLevel;
    }

    public static List<GraphicsUpdater> getEatScoreUpdater() {
        return eatScoreUpdater;
    }
}
