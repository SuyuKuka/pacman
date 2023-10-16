package gui;

import model.Direction;
import model.PacMan;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class PacmanController {
    public void keyPressedHandler(KeyEvent event) {
        PacMan.INSTANCE.setBufferDirection(
            switch (event.getCode()) {
            case UP -> Direction.NORTH;
            case RIGHT -> Direction.EAST;
            case DOWN -> Direction.SOUTH;
            case LEFT -> Direction.WEST;
            default -> PacMan.INSTANCE.getBufferDirection();
        });
        PacMan.INSTANCE.setBufferDurationTime(0);

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
        PacMan.INSTANCE.setBufferDurationTime(0);
    }
}
