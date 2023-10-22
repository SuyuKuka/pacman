package gui;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.Critter;
import model.Ghost;
import model.PacMan;


public final class CritterGraphicsFactory {
    private final double scale;
    private boolean isPacManMouthOpen = false;

    public CritterGraphicsFactory(double scale) {
        this.scale = scale;
    }

    public GraphicsUpdater makeGraphics(Critter critter) {
        var size = 0.7;

        /*remplacer
        var image = new ImageView(new Image(url, scale * size, scale * size, true, true));
        à  Image(pas encore le url, setFitWidth(),setFitHeight(),setPreserveRatio(),setSmooth()*/
        var image = new ImageView();
        image.setFitWidth(scale * size);
        image.setFitHeight(scale * size);
        image.setPreserveRatio(true);
        image.setSmooth(true);
        
        return new GraphicsUpdater() {
            private String url = ""; 

            @Override
            public void update() {
                String newUrl = getImageUrlForCritter(critter);
                if (!newUrl.equals(url)) {// if url a changé return le nouveau url  
                    image.setImage(new Image(newUrl));
                    url = newUrl;
                } 
                image.setTranslateX((critter.getPos().x() + (1 - size) / 2) * scale);
                image.setTranslateY((critter.getPos().y() + (1 - size) / 2) * scale);
                // Debug.out("sprite updated");
            }

            @Override
            public Node getNode() {
                return image;
            }
        };
    }

    //renvoi le file du image en regardant le direction de pacman
    private String getImageUrlForCritter(Critter critter) {
        if (critter instanceof PacMan) {
            PacMan pacMan = (PacMan) critter;
            switch (pacMan.getDirection()) {
                case NORTH:
                    isPacManMouthOpen = !isPacManMouthOpen;
                    if (isPacManMouthOpen) {
                        return "pacman_north.png";
                    } else {
                        return "pacman_north_ferm.png";
                    }
                case EAST:
                    isPacManMouthOpen = !isPacManMouthOpen;
                    if (isPacManMouthOpen) {
                        return "pacman_east.png";
                    } else {
                        return "pacman_east_ferm.png";
                    }
                case SOUTH:
                    isPacManMouthOpen = !isPacManMouthOpen;
                    if (isPacManMouthOpen) {
                        return "pacman_south.png";
                    } else {
                        return "pacman_south_ferm.png";
                    }
                case WEST:
                    isPacManMouthOpen = !isPacManMouthOpen;
                    if (isPacManMouthOpen) {
                        return "pacman_west.png";
                    } else {
                        return "pacman_west_ferm.png";
                    }
                default:
                    return "pacman_defaut.png";
                    //On peut mettre "return url" pour "default:"" en gardant l'ancien direction fait par pacman(mettre url ds paramètre)
                    //mais il enleve l'image de pacman à point initial
            }
        } else if (critter instanceof Ghost) {//ghost reste de la même
            switch ((Ghost) critter) {
                case BLINKY:
                    return "ghost_blinky.png";
                case CLYDE:
                    return "ghost_clyde.png";
                case INKY:
                    return "ghost_inky.png";
                case PINKY:
                    return "ghost_pinky.png";
                default:
                    return "";
            }
        }
        return "";
    }
}

