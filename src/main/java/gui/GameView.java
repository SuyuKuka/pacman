package gui;

import geometry.IntCoordinates;
import javafx.animation.AnimationTimer;
import javafx.scene.layout.Pane;

import model.Ghost;
import static model.Ghost.*;
import model.MazeState;
import utils.FpsAndThreads;

import java.util.ArrayList;
import java.util.List;

public final class GameView {
    // class parameters
    private MazeState maze;
    private Pane gameRoot; // main node of the game
    private AnimationTimer animationTimer;

    private List<GraphicsUpdater> graphicsUpdaters;

    private GameView() {}

    public static final GameView INSTANCE = new GameView();

    private void addGraphics(GraphicsUpdater updater) {
        gameRoot.getChildren().add(updater.getNode());
        graphicsUpdaters.add(updater);
    }

    /**
     * @param maze  le "modèle" de cette vue (le labyrinthe et tout ce qui s'y trouve)
     * @param root  le nœud racine dans la scène JavaFX dans lequel le jeu sera affiché
     * @param scale le nombre de pixels représentant une unité du labyrinthe
     */
    public void setGameView(MazeState maze, Pane root, double scale) {
        INSTANCE.maze = maze;
        INSTANCE.gameRoot = root;
        // pixels per cell
        root.setMinWidth(maze.getWidth() * scale + 300);
        root.setMinHeight(maze.getHeight() * scale);
        root.setStyle("-fx-background-color: #000000");
        var critterFactory = new CritterGraphicsFactory(scale);
        var cellFactory = new CellGraphicsFactory(scale);
        graphicsUpdaters = new ArrayList<>();
        for (var critter : maze.getCritters())
            addGraphics(critterFactory.makeGraphics(critter));
        for (int x = 0; x < maze.getWidth(); x++)
            for (int y = 0; y < maze.getHeight(); y++)
                addGraphics(cellFactory.makeGraphics(maze, new IntCoordinates(x, y)));
        InGameGui.INSTANCE.displayLives(maze);
        InGameGui.INSTANCE.displayScore(maze);
    }

    public void animate() {
        animationTimer = new AnimationTimer() {
            long last = 0;

            @Override
            public void handle(long now) {
                if (last == 0) { // ignore the first tick, just compute the first deltaT
                    last = now;
                    return;
                }
                var deltaT = now - last;
                maze.update(deltaT);
                InGameGui.INSTANCE.updateScore(maze);
                InGameGui.INSTANCE.updateLives(maze);
                
                // Changer les boolean pour afficher les infos respectifs
                tests(deltaT, false, false, false);
                
                for (var updater : graphicsUpdaters) {
                    updater.update();
                }
                last = now;
            }
        };
        animationTimer.start();
    }

    public void stopAnimation() {
        if (animationTimer != null) animationTimer.stop();
    }

    public void tests(long deltaT, boolean showFps, boolean showThreads, boolean showTarget) {
        if (showFps) {
            FpsAndThreads.updateFps(deltaT);
        }
        if (showThreads) {
            FpsAndThreads.updateThread(deltaT);
        }
        if (showTarget) {
            var cellFactory = new CellGraphicsFactory(30);
            Ghost ghost = BLINKY;
            if (ghost.getTarget() != null) {
                addGraphics(cellFactory.showTarget(new IntCoordinates((int)(ghost.getTarget().x()), (int)(ghost.getTarget().y()))));
            }
        }
    }
}
