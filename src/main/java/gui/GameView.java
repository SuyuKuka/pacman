package gui;

import geometry.IntCoordinates;
import javafx.animation.AnimationTimer;
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
    private List<GraphicsUpdater> graphicsUpdaters;
    /** Largeur additionnelle pour l'affichage des infos à droite. */
    private static final double RIGHT_WIDTH = 300;

    private GameView() {
    }

    /** Implémentation en singleton. */
    public static final GameView INSTANCE = new GameView();

    private void addGraphics(final GraphicsUpdater updater) {
        gameRoot.getChildren().add(updater.getNode());
        graphicsUpdaters.add(updater);
    }

    /**
     * @param maze  le "modèle" de cette vue
     * (le labyrinthe et tout ce qui s'y trouve)
     * @param root  le nœud racine dans la scène JavaFX dans lequel
     * le jeu sera affiché
     * @param scale le nombre de pixels représentant une unité du labyrinthe
     */
    public void setGameView(
        final MazeState maze, final Pane root, final double scale
        ) {
        INSTANCE.instMaze = maze;
        INSTANCE.gameRoot = root;
        // pixels per cell
        root.setMinWidth(maze.getWidth() * scale + RIGHT_WIDTH);
        root.setMinHeight(maze.getHeight() * scale);
        root.setStyle("-fx-background-color: #000000");
        var critterFactory = new CritterGraphicsFactory(scale);
        var cellFactory = new CellGraphicsFactory(scale);
        graphicsUpdaters = new ArrayList<>();
        for (var critter : maze.getCritters()) {
            addGraphics(critterFactory.makeGraphics(critter));
        }
        for (int x = 0; x < maze.getWidth(); x++) {
            for (int y = 0; y < maze.getHeight(); y++) {
                addGraphics(
                    cellFactory.makeGraphics(maze, new IntCoordinates(x, y))
                );
            }
        }
        InGameGui.INSTANCE.displayLives(maze);
        InGameGui.INSTANCE.displayScore(maze);
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
                if (last == 0) {
                    last = now;
                    return;
                }
                var deltaT = now - last;
                instMaze.update(deltaT);
                InGameGui.INSTANCE.updateScore(instMaze);
                InGameGui.INSTANCE.updateLives(instMaze);

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
            final double scale = 30;
            var cellFactory = new CellGraphicsFactory(scale);
            if (ghost.getTarget() != null) {
                int xTarg = (int) (ghost.getTarget().x());
                int yTarg = (int) (ghost.getTarget().y());
                IntCoordinates targetPos = new IntCoordinates(xTarg, yTarg);
                addGraphics(cellFactory.showTarget(targetPos));
            }
        }
    }
}
