package gui;

import geometry.IntCoordinates;
import geometry.RealCoordinates;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import model.Ghost;
import model.MazeState;
import utils.FpsAndThreads;

import java.util.ArrayList;
import java.util.List;

public final class GameView {
    /** La carte qui sera affichée. */
    private MazeState instMaze;
    /** Main node. */
    private Pane gameRoot;
    /** L'AnimationTimer qui appelle les fonctions d'update. */
    private AnimationTimer animationTimer;
    /** Liste d'éléments à update. */
    private List<GraphicsUpdater> graphicsUpdaters = new ArrayList<>();;
    /** Largeur additionnelle pour l'affichage des infos à droite. */
    private static final double RIGHT_WIDTH = 300;
    /** Nombre de pixels représentant une unité de la map. */
    private double SCALE = 16;
    private InGameGui inGameGui;
    private Scene gameScene;
    private double initialWidth;
    private double initialHeight;


    private GameView() {
    }

    /** Implémentation en singleton. */
    public static final GameView INSTANCE = new GameView();

    private void addGraphics(final GraphicsUpdater updater) {
        gameRoot.getChildren().add(updater.getNode());
        graphicsUpdaters.add(updater);
    }

    private void removeGraphics(final GraphicsUpdater updater){
        gameRoot.getChildren().remove(updater.getNode());
        graphicsUpdaters.remove(updater);
        
    }

    /**
     * @param maze  le "modèle" de cette vue
     * (le labyrinthe et tout ce qui s'y trouve)
     * @param newInGameGui {@code InGameGui}courant
     * @param root  le nœud racine dans la scène JavaFX dans lequel
     * le jeu sera affiché
     */
    public void setGameView(
        final MazeState maze, final InGameGui newInGameGui, final Pane root,final Scene gameScene) {
        instMaze = maze;
        inGameGui = newInGameGui;
        gameRoot = root;
        this.gameScene = gameScene;

        initialWidth = gameScene.getWidth();
        initialHeight = gameScene.getHeight();
        root.setStyle("-fx-background-color: #000000");
        gameRoot.setTranslateX(gameScene.getWidth()/4 - SCALE);
        gameRoot.setTranslateY(-15);
            //20 normal
            //28 full screen pour moi
        CritterGraphicsFactory critterFactory = new CritterGraphicsFactory(SCALE);
        CellGraphicsFactory cellFactory = new CellGraphicsFactory(SCALE);
        
        for (model.Critter critter : maze.getConfig().getCritters()) {
            addGraphics(critterFactory.makeGraphics(critter));
        }

        for (int x = 0; x < maze.getWidth(); x++) {
            for (int y = 0; y < maze.getHeight(); y++) {
                addGraphics(
                    cellFactory.makeGraphics(maze, new IntCoordinates(x, y))
                );
            }
        }

        System.out.println(graphicsUpdaters.size());
        inGameGui.displayLives(maze, SCALE);
        inGameGui.displayScore(maze, SCALE);

        gameScene.widthProperty().addListener((obs, oldVal, newVal) -> {
            resizeWidth(critterFactory, cellFactory, newVal.doubleValue(), oldVal.doubleValue());
        });
        
        gameScene.heightProperty().addListener((obs, oldVal, newVal) -> {
            resizeHeight(critterFactory, cellFactory, newVal.doubleValue(), oldVal.doubleValue());
        });
    }

    public void resizeWidth(
        final CritterGraphicsFactory critterFactory,
        final CellGraphicsFactory cellFactory,
        double newVal,
        double oldVal)
        {
        //SUPPRESSION
        inGameGui.getScorePane().getChildren().remove(inGameGui.getScoreText());
        ((Pane) gameScene.getRoot()).getChildren().remove(inGameGui.getScorePane());
        for (ImageView img : inGameGui.getLives()) {
            ((Pane) gameScene.getRoot()).getChildren().remove(img);
        }
        for (model.Critter critter : instMaze.getConfig().getCritters()) {
            removeGraphics(critterFactory.makeGraphics(critter));
        }
        for (int x = 0; x < instMaze.getWidth(); x++) {
            for (int y = 0; y < instMaze.getHeight(); y++) {
                removeGraphics(cellFactory.makeGraphics(instMaze, new IntCoordinates(x, y)));
            }
        }

        System.out.println(graphicsUpdaters.size());
        //SCALE + TRANSLATE
        SCALE++;

        //RAJOUT
        // CritterGraphicsFactory newCritterFactory = new CritterGraphicsFactory(SCALE);
        // CellGraphicsFactory newCellFactory = new CellGraphicsFactory(SCALE);

        // for (model.Critter critter : instMaze.getConfig().getCritters()) {
        //     addGraphics(newCritterFactory.makeGraphics(critter));
        // }

        // for (int x = 0; x < instMaze.getWidth(); x++) {
        //     for (int y = 0; y < instMaze.getHeight(); y++) {
        //         addGraphics(newCellFactory.makeGraphics(instMaze, new IntCoordinates(x, y)));
        //     }
        // }

        // inGameGui.displayLives(instMaze, SCALE);
        // inGameGui.displayScore(instMaze, SCALE);
    }

    public void resizeHeight(
        final CritterGraphicsFactory critterFactory,
        final CellGraphicsFactory cellFactory,
        double newVal,
        double oldVal)
        {
        //SUPPRESSION
        inGameGui.getScorePane().getChildren().remove(inGameGui.getScoreText());
        ((Pane) gameScene.getRoot()).getChildren().remove(inGameGui.getScorePane());
        for (ImageView img : inGameGui.getLives()) {
            ((Pane) gameScene.getRoot()).getChildren().remove(img);
        }
        for (model.Critter critter : instMaze.getConfig().getCritters()) {
            removeGraphics(critterFactory.makeGraphics(critter));
        }
        for (int x = 0; x < instMaze.getWidth(); x++) {
            for (int y = 0; y < instMaze.getHeight(); y++) {
                removeGraphics(cellFactory.makeGraphics(instMaze, new IntCoordinates(x, y)));
            }
        }

        //SCAKE + TRANSLATE
        SCALE++;

        //RAJOUT
        // CritterGraphicsFactory newCritterFactory = new CritterGraphicsFactory(SCALE);
        // CellGraphicsFactory newCellFactory = new CellGraphicsFactory(SCALE);

        // for (model.Critter critter : instMaze.getConfig().getCritters()) {
        //     addGraphics(newCritterFactory.makeGraphics(critter));
        // }

        // for (int x = 0; x < instMaze.getWidth(); x++) {
        //     for (int y = 0; y < instMaze.getHeight(); y++) {
        //         addGraphics(newCellFactory.makeGraphics(instMaze, new IntCoordinates(x, y)));
        //     }
        // }

        // inGameGui.displayLives(instMaze, SCALE);
        // inGameGui.displayScore(instMaze, SCALE);

    }


    /**
     * Méthode permettant d'animer les entités de la carte
     * JavaFX utilise le taux de rafraîchissement de l'écran de l'utilisateur
     * pour effectuer ses updates. (Ex: 60 updates pour un écran 60hz)
     */
    public void animate() {
        animationTimer = new AnimationTimer() {
            private long last = 0;

            @Override
            public void handle(final long now) {
                // Ignore le premier tick
                if (last == 0 || InGameGui.isPaused()) {
                    last = now;
                    return;
                }
                var deltaT = now - last;
                instMaze.update(deltaT);
                inGameGui.updateScore(instMaze);
                inGameGui.updateLives(instMaze);
                

                // Changer les boolean pour afficher les infos respectifs
                tests(deltaT, false, false, false, null);

                for (var updater : graphicsUpdaters) {
                    updater.update();
                }
                last = now;
            }
        };
        animationTimer.start();
    }

    /** Arrête l'animation. */
    public void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }

    /**
     * Méthode permettant d'obtenir les FPS, les threads et d'afficher
     * les targets d'un fantôme spécifique.
     * @param deltaT Durée depuis le dernier tick
     * @param showFps {@code true}affiche les FPS
     * @param showThreads {@code true}affiche les threads
     * @param showTarget {@code true}affiche le target
     * @param ghost Nom du{@code Ghost}
     */
    public void tests(
        final long deltaT,
        final boolean showFps, final boolean showThreads,
        final boolean showTarget, final Ghost ghost
        ) {
        if (showFps) {
            FpsAndThreads.updateFps(deltaT);
        }
        if (showThreads) {
            FpsAndThreads.updateThread(deltaT);
        }
        if (showTarget) {
            var cellFactory = new CellGraphicsFactory(SCALE);
            if (ghost.getTarget() != null) {
                RealCoordinates target = ghost.getTarget();
                addGraphics(cellFactory.showTarget(target));
            }
        }
    }
}
