package gui;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import model.Ghost;
import model.MazeState;
import model.PacMan;
import model.Ghost.GhostState;
import utils.Resources;

public final class CritterGraphicsFactory {
    /** (Nombre d'update par seconde) / 60. */
    private static final double UPDATE_FREQUENCY = (double) 25 / 60;
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
     * @param maze
     * @return {@link GraphicsUpdater}
     */
    public GraphicsUpdater makePacGraphics(final PacMan pacman, final MazeState maze) {
        var image = new ImageView();
        image.setFitWidth(scale * SIZE);
        image.setFitHeight(scale * SIZE);
        image.setPreserveRatio(true);
        image.setSmooth(true);
        image.setImage(Resources.getImage(getPacManImageName(pacman, image, maze)));

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

                    String newImage = getPacManImageName(pacman, image, maze);

                    if (!pacman.getGraphicsBuffer().isResetPacImg()
                    && pacman.getImage().equals(newImage)) {
                        return;
                    }

                    if (pacman.getGraphicsBuffer().isResetPacImg()) {
                        if (maze.getConfig().isCoop() && pacman == maze.getConfig().getPacman2()) {
                            newImage = "pacman_rainbow_3";
                        } else {
                            newImage = "pacman_3";
                        }
                        image.setRotate(0);
                        image.setScaleX(1);
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
        image.setImage(Resources.getImage(getGhostImageName(ghost, image)));

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

                    String newImage = getGhostImageName(ghost, image);

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
     * @param image L'{@code ImageView}
     * @param maze {@link MazeState}
     * @return {@code String} du nom de l'image
     */
    private String getPacManImageName(
    final PacMan pacman, final ImageView image, final MazeState maze) {
        pacman.getGraphicsBuffer().setPacManMouthOpen(
            !pacman.getGraphicsBuffer().isPacManMouthOpen()
        );
        final double angle = 90.0;
        switch (pacman.getDirection()) {
            case NORTH:
                if (image.getRotate() != -angle) {
                    image.setScaleX(1);
                    image.setRotate(-angle);
                }
                break;
            case EAST:
                if (image.getRotate() != 0 || image.getScaleX() != 1) {
                    image.setRotate(0);
                    image.setScaleX(1);
                }
                break;
            case SOUTH:
                if (image.getRotate() != angle) {
                    image.setScaleX(1);
                    image.setRotate(angle);
                }
                break;
            case WEST:
                if (image.getScaleX() != -1) {
                    image.setRotate(0);
                    image.setScaleX(-1);
                }
                break;

            default:
                return pacman.getImage();
        }

        GraphicsBuffer buffer = pacman.getGraphicsBuffer();

        final String name;
        if (maze.getConfig().isCoop() && pacman == maze.getConfig().getPacman2()) {
            name = "pacman_rainbow_" + buffer.getPhase();
        } else {
            name = "pacman_" + buffer.getPhase();
        }

        final int firstImg = 1;
        final int lastImg = 4;
        if (buffer.getPhase() == firstImg) {
            buffer.setAdd(true);
        } else if (buffer.getPhase() == lastImg) {
            buffer.setAdd(false);
        }

        if (buffer.isAdd()) {
            buffer.setPhase(buffer.getPhase() + 1);
        } else {
            buffer.setPhase(buffer.getPhase() - 1);
        }
        return name;
    }

    /**
     * Méthode renvoyant le nom de l'image pour le {@link Ghost}.
     * @param ghost Un {@link Ghost} pour alterner les images des fantômes.
     * @param image L'{@code ImageView}
     * @return {@code String} du nom de l'image
     */
    private String getGhostImageName(final Ghost ghost, final ImageView image) {
        if (ghost.getGhostState() == GhostState.EATEN) {
            return "eaten";
        }

        if (ghost.getPacman().isEnergized() && !ghost.getGraphicsBuffer().isAlternate()) {
            return "frightened";
        }

        switch (ghost.getDirection()) {
            case EAST:
                if (image.getScaleX() != -1) {
                    image.setScaleX(-1);
                }
                break;
            case WEST:
                if (image.getScaleX() != 1) {
                    image.setScaleX(1);
                }
                break;

            default:
                break;
        }

        switch (ghost.getGhostType()) {
            case BLINKY:
                return "blinky";
            case CLYDE:
                return "clyde";
            case INKY:
                return "inky";
            case PINKY:
                return "pinky";
            default:
                return ghost.getImage();
        }
    }
}

