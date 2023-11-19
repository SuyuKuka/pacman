package gui;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import model.Critter;
import model.Ghost;
import model.PacMan;
import utils.Resources;

public final class CritterGraphicsFactory {
    /** (Nombre d'update par seconde) / 60. */
    private static final double UPDATE_FREQUENCY = (double) 1 / 6;
    /** Scale pour la taille des critters. */
    private static final double SIZE = 0.7;
    /** Boolean indiquant s'il faut remettre l'image par défaut de Pac-Man. */
    private static boolean resetPacImg = false;
    /** Echelle globale. */
    private final double scale;
    /** Somme de ticks,
     * se remet à 0 lorsqu'elle dépasse {@link #UPDATE_FREQUENCY}.
     */
    private double buffer;
    /** Dernière image de Pac-Man. */
    private String lastPacImg = "pacman";
    /** Boolean indiquant si la bouche de Pac-Man est ouverte. */
    private boolean isPacManMouthOpen = false;

    /**
     * Set le boolean {@link #resetPacImg} à{@code reset}.
     * @param reset {@code boolean}
     */
    public static void setResetPacImg(final boolean reset) {
        resetPacImg = reset;
    }

    /**
     * Constructeur qui initialise le {@link scale}.
     * @param newScale Echelle
     */
    public CritterGraphicsFactory(final double newScale) {
        scale = newScale;
    }

    /**
     * La méthode définie les propriétés de l'image du{@code critter}et
     * retourne un {@link GraphicsUpdater} contenant la méthode
     * {@link GraphicsUpdater#update} et l'image du{@code critter}via
     * la méthode {@link GraphicsUpdater#getNode}.<br><br>
     * {@link GraphicsUpdater#update} met à jour l'image du{@code critter}et
     * la position de l'image.
     * @param critter
     * @return {@link GraphicsUpdater}
     */
    public GraphicsUpdater makeGraphics(final Critter critter) {
        var image = new ImageView();
        image.setFitWidth(scale * SIZE);
        image.setFitHeight(scale * SIZE);
        image.setPreserveRatio(true);
        image.setSmooth(true);
        image.setImage(Resources.getImage(getCritterImageName(critter)));

        return new GraphicsUpdater() {
            
            @Override
            public void update() {
                buffer += UPDATE_FREQUENCY;
                if (buffer >= 1) {
                    buffer = 0;
                    String newImage = getCritterImageName(critter);
                    image.setImage(Resources.getImage(newImage));
                    if (critter instanceof PacMan) {
                        lastPacImg = newImage;
                    }
                    if (critter instanceof PacMan && resetPacImg) {
                        lastPacImg = "pacman";
                        image.setImage(Resources.getImage(lastPacImg));
                        resetPacImg = false;
                    }
                }
                image.setTranslateX(
                    (critter.getPos().x() + (1 - SIZE) / 2) * scale
                );
                image.setTranslateY(
                    (critter.getPos().y() + (1 - SIZE) / 2) * scale
                );
            }

            @Override
            public Node getNode() {
                return image;
            }
        };
    }

    /**
     * Méthode renvoyant le nom de l'image pour chaque {@code Critter}.
     * L'image de Pac-Man dépendra de son orientation actuelle.
     * @param critter Un {@code Critter}
     * @return {@code String} du nom de l'image
     */
    private String getCritterImageName(final Critter critter) {
        isPacManMouthOpen = !isPacManMouthOpen;
        if (critter instanceof PacMan) {
            switch (critter.getDirection()) {
                case NORTH:
                    if (isPacManMouthOpen) {
                        return "pacman_north";
                    }
                    return "pacman_north_ferm";
                case EAST:
                    if (isPacManMouthOpen) {
                        return "pacman_east";
                    }
                    return "pacman_east_ferm";
                case SOUTH:
                    if (isPacManMouthOpen) {
                        return "pacman_south";
                    }
                    return "pacman_south_ferm";
                case WEST:
                    if (isPacManMouthOpen) {
                        return "pacman_west";
                    }
                    return "pacman_west_ferm";
                default:
                    return lastPacImg;
            }
        } else {
            if (((Ghost) critter).isEaten()) {
                return "ghost_white_eaten";
            }

            /** Vérifie si Pac-Man2 existe (Multijouer-Coop)  */
            boolean isPacMan2Energized = false;
            if (((Ghost) critter).getPacman2()!=null){
                isPacMan2Energized = ((Ghost) critter).getPacman2().isEnergized() ;
            }

            if (((Ghost) critter).getPacman().isEnergized() || isPacMan2Energized) {
                return "ghost_white_2";
            }
            switch (((Ghost) critter).getGhostType()) {
                case BLINKY:
                    return "ghost_blinky";
                case CLYDE:
                    return "ghost_clyde";
                case INKY:
                    return "ghost_inky";
                case PINKY:
                    return "ghost_pinky";
                default:
                    return "";
            }
        }
    }
}

