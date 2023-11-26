package gui;

import geometry.IntCoordinates;
import geometry.RealCoordinates;
import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Ghost;
import model.MazeState;
import model.PacMan;
import utils.FpsAndThreads;

import java.util.ArrayList;
import java.util.List;

public final class GameView {
    /** La carte qui sera affichée. */
    private MazeState instMaze;
    /** {@code HBox}contenant la map et le score / vie. */
    private HBox mainBox;
    /** Map node. */
    private GridPane mapPane;
    /** L'AnimationTimer qui appelle les fonctions d'update. */
    private AnimationTimer animationTimer;
    /** Liste d'éléments à update. */
    private List<GraphicsUpdater> graphicsUpdaters;
    /** Nombre de pixels représentant une unité de la map. */
    private static final double SCALE = 18;
    private InGameGui inGameGui;

    private GameView() {
    }

    /** Implémentation en singleton. */
    public static final GameView INSTANCE = new GameView();

    private void addGraphics(final GraphicsUpdater updater, final int x, final int y) {
        mapPane.add(updater.getNode(), x, y);
        graphicsUpdaters.add(updater);
    }

    private void addCritterGraphics(final GraphicsUpdater updater) {
        HBox box = new HBox(updater.getNode());
        box.setAlignment(Pos.CENTER);
        mapPane.add(box, 0, 0);
        graphicsUpdaters.add(updater);
    }

    /**
     * @param maze  le "modèle" de cette vue (le labyrinthe et tout ce qui s'y trouve)
     * @param newInGameGui {@code InGameGui}courant
     * @param box  le nœud racine dans la scène JavaFX dans lequel
     * le jeu sera affiché
     */
    public void setGameView(
        final MazeState maze, final InGameGui newInGameGui, final HBox box) {
        instMaze = maze;
        inGameGui = newInGameGui;
        mainBox = box;
        mapPane = new GridPane();

        VBox mapBox = new VBox();
        mapBox.setAlignment(Pos.CENTER);
        mapBox.getChildren().add(mapPane);

        mainBox.setAlignment(Pos.CENTER);
        mainBox.getChildren().addAll(mapBox);
        mainBox.setStyle("-fx-background-color: #000000");

        var critterFactory = new CritterGraphicsFactory(SCALE);
        var cellFactory = new CellGraphicsFactory(SCALE);
        graphicsUpdaters = new ArrayList<>();
        for (var critter : maze.getConfig().getCritters()) {
            if (critter instanceof PacMan) {
                addCritterGraphics(critterFactory.makePacGraphics((PacMan) critter, maze));
            } else {
                addCritterGraphics(critterFactory.makeGhostGraphics((Ghost) critter));
            }
        }

        for (int x = 0; x < maze.getWidth(); x++) {
            for (int y = 0; y < maze.getHeight(); y++) {
                addGraphics(
                    cellFactory.makeGraphics(maze, new IntCoordinates(x, y)),
                    x, y
                );
            }
        }

        inGameGui.displayLives(maze);
        inGameGui.displayScore(maze);
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
                tests(deltaT, false, false, null);

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

    /** Remets à{@code null} les variables. */
    public void resetInstance() {
        instMaze = null;
        mainBox = null;
        mapPane = null;
        animationTimer = null;
        graphicsUpdaters = null;
    }

    /**
     * Méthode permettant d'obtenir les FPS, les threads et d'afficher
     * les targets d'un fantôme spécifique.
     * @param deltaT Durée depuis le dernier tick
     * @param showFps {@code true}affiche les FPS
     * @param showThreads {@code true}affiche les threads
     * @param ghostTarget Nom du{@code Ghost}
     */
    public void tests(
        final long deltaT, final boolean showFps,
        final boolean showThreads, final Ghost ghostTarget) {
        if (showFps) {
            FpsAndThreads.updateFps(deltaT);
        }
        if (showThreads) {
            FpsAndThreads.updateThread(deltaT);
        }
        if (ghostTarget != null) {
            var cellFactory = new CellGraphicsFactory(SCALE);
            if (ghostTarget.getTarget() != null) {
                RealCoordinates target = ghostTarget.getTarget();
                addGraphics(cellFactory.showTarget(target), 0, 0);
            }
        }
    }
}
