package gui.controller;

import gui.InGameGui;
import javafx.scene.input.KeyEvent;
import model.Direction;
import model.Ghost;
import utils.Music;

public final class GhostController extends Controller implements KeyHandlers {
    private Ghost playerGhost;

    /**
     * Constructeur pour {@link GhostController}.
     * Associe un objet {@link Ghost} à une "manette".
     * @param playerGhost {@link Ghost}
     */
    public GhostController(final Ghost playerGhost) {
        this.playerGhost = playerGhost;
    }

    @Override
    public void keyPressedHandler(final KeyEvent event) {
        if (InGameGui.isPaused() || Music.isStartBgmPlaying()) {
            return;
        }

        playerGhost.getDirectionBuffer().setBufferDirection(
            switch (event.getCode()) {
                case Z -> Direction.NORTH;
                case W -> Direction.NORTH;
                case D -> Direction.EAST;
                case S -> Direction.SOUTH;
                case Q -> Direction.WEST;
                case A -> Direction.WEST;
                default -> playerGhost.getDirectionBuffer().getBufferDirection();
            }
        );
        playerGhost.getDirectionBuffer().setDirectionBufferTimer(0);
    }
}
