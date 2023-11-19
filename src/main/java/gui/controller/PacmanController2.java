package gui.controller;

import model.Direction;
import model.PacMan;
import gui.InGameGui;
import javafx.scene.input.KeyEvent;

public final class PacmanController2 extends Controller implements KeyHandlers {
        private PacMan pacman;

        /**
         * Constructeur pour {@link PacmanController}.
         * Associe un objet {@link PacMan} à une "manette".
         * @param pacman {@link PacMan}
         */
        public PacmanController2(final PacMan pacman) {
            this.pacman = pacman;
        }

        @Override
        public void keyPressedHandler(final KeyEvent event) {
            if (InGameGui.isPaused()) {
                return;
            }

            pacman.getDirectionBuffer().setBufferDirection(
                switch (event.getCode()) {
                    case Z -> Direction.NORTH;
                    case W -> Direction.NORTH;
                    case Q -> Direction.WEST;
                    case A -> Direction.WEST;
                    case S -> Direction.SOUTH;
                    case D -> Direction.EAST;
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
