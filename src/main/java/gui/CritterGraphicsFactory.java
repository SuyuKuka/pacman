package gui;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import model.Critter;
import model.Ghost;
import model.PacMan;
import utils.Resources;


public final class CritterGraphicsFactory {
    private static final double UPDATE_FREQUENCY = (double) 1 / 3; // (Nombre d'update par seconde) / 60
    private static final double SIZE = 0.7;
    private static boolean resetPacImg = false;
    private final double scale;
    private double buffer;
    private String lastPacImg = "pacman";
    private boolean isPacManMouthOpen = false;

    public static void setResetPacImg(boolean reset) {
        resetPacImg = reset;
    }

    public CritterGraphicsFactory(double scale) {
        this.scale = scale;
    }

    public GraphicsUpdater makeGraphics(Critter critter) {
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
                    if (critter instanceof PacMan) lastPacImg = newImage;
                    if (critter instanceof PacMan && resetPacImg) {
                        lastPacImg = "pacman";
                        image.setImage(Resources.getImage(lastPacImg));
                        resetPacImg = false;
                    }
                }
                image.setTranslateX((critter.getPos().x() + (1 - SIZE) / 2) * scale);
                image.setTranslateY((critter.getPos().y() + (1 - SIZE) / 2) * scale);
            }

            @Override
            public Node getNode() {
                return image;
            }
        };
    }

    /**
     * Méthode renvoyant le nom de l'image pour chaque {@code Critter}. L'image de Pac-Man dépendra de son orientation actuelle.
     * @param critter Un {@code Critter}
     * @return {@code String} du nom de l'image
     */
    private String getCritterImageName(Critter critter) {
        isPacManMouthOpen = !isPacManMouthOpen;
        if (critter instanceof PacMan) {
            switch (critter.getDirection()) {
                case NORTH:
                    if (isPacManMouthOpen) return "pacman_north";
                    return "pacman_north_ferm";
                case EAST:
                    if (isPacManMouthOpen) return "pacman_east";
                    return "pacman_east_ferm";
                case SOUTH:
                    if (isPacManMouthOpen) return "pacman_south";
                    return "pacman_south_ferm";
                case WEST:
                    if (isPacManMouthOpen) return "pacman_west";
                    return "pacman_west_ferm";
                default:
                    return lastPacImg;
            }
        } else { // TODO : Changer l'image des fantômes
            switch ((Ghost) critter) {
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

