package gui;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import model.Ghost;
import model.PacMan;
import utils.Resources;

public final class CritterGraphicsFactory {
    /** (Nombre d'update par seconde) / 60. */
    private static final double UPDATE_FREQUENCY = (double) 1 / 4;
    /** (Nombre d'update par seconde) / 60. */
    private static final double ALTERNATE_FREQUENCY = (double) 1 / 15;
    /** Scale pour la taille des critters. */
    private static final double SIZE = 0.7;
    /** Echelle globale. */
    private final double scale;

    /**
     * Constructeur qui initialise le {@link scale}.
     * @param newScale Echelle
     */
    public CritterGraphicsFactory(final double newScale) {
        scale = newScale;
    }

    /**
     * La méthode définie les propriétés de l'image du {@link PacMan} et
     * retourne un {@link GraphicsUpdater} contenant la méthode
     * {@link GraphicsUpdater#update} et l'image du {@link PacMan} via
     * la méthode {@link GraphicsUpdater#getNode}.<br><br>
     * {@link GraphicsUpdater#update} met à jour l'image du {@link PacMan} et
     * la position de l'image.
     * @param pacman
     * @return {@link GraphicsUpdater}
     */
    public GraphicsUpdater makePacGraphics(final PacMan pacman) {
        var image = new ImageView();
        image.setFitWidth(scale * SIZE);
        image.setFitHeight(scale * SIZE);
        image.setPreserveRatio(true);
        image.setSmooth(true);
        image.setImage(Resources.getImage(getPacManImageName(pacman)));

        return new GraphicsUpdater() {
            @Override
            public void update() {
                image.setTranslateX((pacman.getPos().x() + (1 - SIZE) / 2) * scale);
                image.setTranslateY((pacman.getPos().y() + (1 - SIZE) / 2) * scale);

                double graphicsBuffer
                = pacman.getGraphicsBuffer().getUpdateBuffer() + UPDATE_FREQUENCY;
                pacman.getGraphicsBuffer().setUpdateBuffer(graphicsBuffer);
                if (graphicsBuffer >= 1) {
                    pacman.getGraphicsBuffer().setUpdateBuffer(0);

                    String newImage = getPacManImageName(pacman);

                    if (!pacman.getGraphicsBuffer().isResetPacImg()
                    && pacman.getImage().equals(newImage)) {
                        return;
                    }

                    if (pacman.getGraphicsBuffer().isResetPacImg()) {
                        newImage = "pacman";
                        pacman.getGraphicsBuffer().setResetPacImg(false);
                    }

                    pacman.setImage(newImage);
                    image.setImage(Resources.getImage(newImage));
                }
            }

            @Override
            public Node getNode() {
                return image;
            }
        };
    }

    /**
     * La méthode définie les propriétés de l'image du {@link Ghost} et
     * retourne un {@link GraphicsUpdater} contenant la méthode
     * {@link GraphicsUpdater#update} et l'image du {@link Ghost} via
     * la méthode {@link GraphicsUpdater#getNode}.<br><br>
     * {@link GraphicsUpdater#update} met à jour l'image du {@link Ghost} et
     * la position de l'image.
     * @param ghost
     * @return {@link GraphicsUpdater}
     */
    public GraphicsUpdater makeGhostGraphics(final Ghost ghost) {
        var image = new ImageView();
        image.setFitWidth(scale * SIZE);
        image.setFitHeight(scale * SIZE);
        image.setPreserveRatio(true);
        image.setSmooth(true);
        image.setImage(Resources.getImage(getGhostImageName(ghost)));

        return new GraphicsUpdater() {
            @Override
            public void update() {
                image.setTranslateX((ghost.getPos().x() + (1 - SIZE) / 2) * scale);
                image.setTranslateY((ghost.getPos().y() + (1 - SIZE) / 2) * scale);

                double graphicsBuffer
                = ghost.getGraphicsBuffer().getUpdateBuffer() + UPDATE_FREQUENCY;
                double alternateBuffer
                = ghost.getGraphicsBuffer().getAlternateBuffer() + ALTERNATE_FREQUENCY;

                ghost.getGraphicsBuffer().setUpdateBuffer(graphicsBuffer);
                ghost.getGraphicsBuffer().setAlternateBuffer(alternateBuffer);

                if (!ghost.getPacman().isEnergized() && ghost.getGraphicsBuffer().isAlternate()) {
                    ghost.getGraphicsBuffer().setAlternate(false);
                }

                if (ghost.getGraphicsBuffer().getUpdateBuffer() >= 1) {
                    ghost.getGraphicsBuffer().setUpdateBuffer(0);

                    if (PacMan.MAX_ENERGIZED_TIME - PacMan.getEnergizedTimer() < 2
                    && alternateBuffer >= 1) {
                        ghost.getGraphicsBuffer().setAlternateBuffer(0);
                        ghost.getGraphicsBuffer().setAlternate(
                            !ghost.getGraphicsBuffer().isAlternate()
                        );
                    }

                    String newImage = getGhostImageName(ghost);

                    if (ghost.getImage().equals(newImage)) {
                        return;
                    }

                    ghost.setImage(newImage);
                    image.setImage(Resources.getImage(newImage));
                }
            }

            @Override
            public Node getNode() {
                return image;
            }
        };
    }

    /**
     * Méthode renvoyant le nom de l'image pour le {@link PacMan}.
     * L'image de Pac-Man dépendra de son orientation actuelle.
     * @param pacman Un {@link PacMan}
     * @return {@code String} du nom de l'image
     */
    private String getPacManImageName(final PacMan pacman) {
        pacman.getGraphicsBuffer().setPacManMouthOpen(
            !pacman.getGraphicsBuffer().isPacManMouthOpen()
        );
        switch (pacman.getDirection()) {
            case NORTH:
                return pacman.getGraphicsBuffer().isPacManMouthOpen()
                ? "pacman_north" : "pacman_north_ferm";
            case EAST:
                return pacman.getGraphicsBuffer().isPacManMouthOpen()
                ? "pacman_east" : "pacman_east_ferm";
            case SOUTH:
                return pacman.getGraphicsBuffer().isPacManMouthOpen()
                ? "pacman_south" : "pacman_south_ferm";
            case WEST:
                return pacman.getGraphicsBuffer().isPacManMouthOpen()
                ? "pacman_west" : "pacman_west_ferm";
            default:
                return pacman.getImage();
        }
    }

    /**
     * Méthode renvoyant le nom de l'image pour le {@link Ghost}.
     * @param ghost Un {@link Ghost}
     * pour alterner les images des fantômes.
     * @return {@code String} du nom de l'image
     */
    private String getGhostImageName(final Ghost ghost) {
        if (ghost.isEaten()) {
            return "ghost_eaten";
        }

        if (ghost.getPacman().isEnergized() && !ghost.getGraphicsBuffer().isAlternate()) {
            return "ghost_frightened";
        }

        switch (ghost.getGhostType()) {
            case BLINKY:
                return "ghost_blinky";
            case CLYDE:
                return "ghost_clyde";
            case INKY:
                return "ghost_inky";
            case PINKY:
                return "ghost_pinky";
            default:
                return ghost.getImage();
        }
    }
}

