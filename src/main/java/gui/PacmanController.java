package gui;

import model.Direction;
import model.PacMan;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class PacmanController {

    private static InGameGui inGameGui;
    private PacMan pacman;

    /**
     * Constructeur pour {@link PacmanController}.
     * Associe un objet {@link PacMan} à une "manette".
     * @param pacman {@link PacMan}
     */
    public PacmanController(final PacMan pacman) {
        this.pacman = pacman;
    }

    public static void setInGameGui(final InGameGui inGameGui) {
        PacmanController.inGameGui = inGameGui;
    }

    /**
     * Méthode appelée lorsqu'une touche est appuyée.
     * Elle contrôle le buffer de direction et l'activation des fantômes.
     * @param event {@code KeyEvent}
     */
    public final void keyPressedHandler(final KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            if (!InGameGui.isPaused()) {
                pacman.setDirection(Direction.NONE);
                inGameGui.pause(true);
                return;
            } else {
                inGameGui.pause(false);
            }
        }

        if (InGameGui.isPaused()) {
            return;
        }

        pacman.setBufferDirection(
                switch (event.getCode()) {
                    case UP -> Direction.NORTH;
                    case RIGHT -> Direction.EAST;
                    case DOWN -> Direction.SOUTH;
                    case LEFT -> Direction.WEST;
                    default -> pacman.getBufferDirection();
                });
        pacman.setDirectionBufferTimer(0);

    }

    /**
     * Méthode appelée lorsqu'une touche est relâchée.
     * Elle réinitialise la durée du buffer.
     * @param event {@code KeyEvent}
     */
    public final void keyReleasedHandler(final KeyEvent event) {
        pacman.setDirectionBufferTimer(0);
    }
}
