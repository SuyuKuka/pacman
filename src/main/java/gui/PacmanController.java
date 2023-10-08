package gui;

import model.Direction;
import model.PacMan;
import config.MazeConfig;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class PacmanController {
    public void keyPressedHandler(KeyEvent event) {
        MazeConfig config = MazeConfig.makeExample1();
        var curNeighbours = PacMan.INSTANCE.getPos().intNeighbours();
        boolean hasWall = false;
        
        // Empêche Pac-Man de s'arrêter en allant contre un mur adjacent
        for (var n: curNeighbours) {
            switch ((event.getCode())) {
                case UP:
                    hasWall = config.getCell(n).northWall();
                    break;
                case RIGHT:
                    hasWall = config.getCell(n).eastWall();
                    break;
                case DOWN:
                    hasWall = config.getCell(n).southWall();
                    break;
                case LEFT:
                    hasWall = config.getCell(n).westWall();
                    break;
                default:
                    break;
            }
            if (hasWall) return;
        }

        PacMan.INSTANCE.setDirection(
            switch (event.getCode()) {
                case LEFT -> Direction.WEST;
                case RIGHT -> Direction.EAST;
                case UP -> Direction.NORTH;
                case DOWN -> Direction.SOUTH;
                default -> PacMan.INSTANCE.getDirection(); // do nothing
            }
        );

        // Activation des fantômes à la première input
        if (PacMan.INSTANCE.getPacManMoved() == 0 &&
            (event.getCode().equals(KeyCode.UP) ||
            event.getCode().equals(KeyCode.RIGHT) ||
            event.getCode().equals(KeyCode.DOWN) ||
            event.getCode().equals(KeyCode.LEFT))) {
            PacMan.INSTANCE.setPacManMoved(1);
        }
    }
    public void keyReleasedHandler(KeyEvent event) {
        // Nothing to do?
    }
}
