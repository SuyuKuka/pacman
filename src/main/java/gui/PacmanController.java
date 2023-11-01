package gui;

import model.Direction;
import model.PacMan;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class PacmanController {
    /**
     * Méthode appelée lorsqu'une touche est appuyée.
     * Elle contrôle le buffer de direction et l'activation des fantômes.
     * @param event {@code KeyEvent}
     */
    public final void keyPressedHandler(final KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            if (!InGameGui.INSTANCE.isPaused()) {
                InGameGui.pause(true);
                return;
            } else {
                InGameGui.pause(false);
            }
        }

        if (InGameGui.INSTANCE.isPaused()) {
            return;
        }

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
        if (PacMan.INSTANCE.getPacManMoved() == 0
            && (event.getCode().equals(KeyCode.UP)
            || event.getCode().equals(KeyCode.RIGHT)
            || event.getCode().equals(KeyCode.DOWN)
            || event.getCode().equals(KeyCode.LEFT))) {
            PacMan.INSTANCE.setPacManMoved(1);
        }
    }

    /**
     * Méthode appelée lorsqu'une touche est relâchée.
     * Elle réinitialise la durée du buffer.
     * @param event {@code KeyEvent}
     */
    public final void keyReleasedHandler(final KeyEvent event) {
        PacMan.INSTANCE.setBufferDurationTime(0);
    }
}
