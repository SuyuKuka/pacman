package gui;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.Critter;
import model.Ghost;
import model.PacMan;


public final class CritterGraphicsFactory {
    private final double scale;

    public CritterGraphicsFactory(double scale) {
        this.scale = scale;
    }

    public GraphicsUpdater makeGraphics(Critter critter) {
        var size = 0.7;
        var image = new ImageView();
        //var image = new ImageView(new Image(url, scale * size, scale * size, true, true));
        image.setFitWidth(scale * size);
        image.setFitHeight(scale * size);
        image.setPreserveRatio(true);
        image.setSmooth(true);
        
        return new GraphicsUpdater() {
            private String url = ""; 

            @Override
            public void update() {

                String newUrl = getImageUrlForCritter(critter);
                if (!newUrl.equals(url)) {  
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

    private String getImageUrlForCritter(Critter critter) {
        if (critter instanceof PacMan) {
            PacMan pacMan = (PacMan) critter;
            switch (pacMan.getDirection()) {
                case NORTH:
                    return "pacman_north.png";
                case EAST:
                    return "pacman_east.png";
                case SOUTH:
                    return "pacman_south.png";
                case WEST:
                    return "pacman_west.png";
                default:
                    return "pacman_east.png";
            }
        } else if (critter instanceof Ghost) {
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
                    return "";  // or some default ghost image
            }
        }
        return "";
    }
}

