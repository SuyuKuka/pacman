package gui.controller;

import model.Direction;
import model.PacMan;
import utils.Music;
import gui.InGameGui;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public final class PacmanController extends Controller implements KeyHandlers {
        private PacMan pacman;

        /**
         * Constructeur pour {@link PacmanController}.
         * Associe un objet {@link PacMan} à une "manette".
         * @param pacman {@link PacMan}
         */
        public PacmanController(final PacMan pacman) {
            this.pacman = pacman;
        }

        @Override
        public void keyPressedHandler(final KeyEvent event) {
            if (event.getCode() == KeyCode.ESCAPE && !InGameGui.isPaused()) {
                getInGameGui().pause(true);
            } else if (event.getCode() == KeyCode.ESCAPE) {
                getInGameGui().pause(false);
            }

            if (InGameGui.isPaused() || Music.isStartBgmPlaying()) {
                return;
            }

            pacman.getDirectionBuffer().setBufferDirection(
                switch (event.getCode()) {
                    case UP -> Direction.NORTH;
                    case RIGHT -> Direction.EAST;
                    case DOWN -> Direction.SOUTH;
                    case LEFT -> Direction.WEST;
                    default -> pacman.getDirectionBuffer().getBufferDirection();
                }
            );
            pacman.getDirectionBuffer().setDirectionBufferTimer(0);
        }

        @Override
        public void keyReleasedHandler(final KeyEvent event) {
            pacman.getDirectionBuffer().setDirectionBufferTimer(0);
        }
    }
