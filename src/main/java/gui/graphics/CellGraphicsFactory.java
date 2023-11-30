package gui.graphics;

import config.Cell;
import geometry.IntCoordinates;
import geometry.RealCoordinates;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import model.MazeState;

public class CellGraphicsFactory {
    /** L'échelle des images. */
    private final double scale;

    /**
     * Constructeur qui initialise l'échelle.
     * @param newScale L'échelle
     */
    public CellGraphicsFactory(final int newScale) {
        scale = newScale;
    }

    /**
     * Retourne un {@link GraphicsUpdater} contenant la méthode
     * {@link GraphicsUpdater#update} qui initialise la visibilité
     * du {@link config.Cell#DOT} et {@link GraphicsUpdater#getNode}
     * renvoyant un{@code Group}contenant les graphismes pour la cellule.
     * @param state Etat de la carte
     * @param pos Position de la cellule
     * @return {@link GraphicsUpdater}
     */
    public GraphicsUpdater makeGraphics(final MazeState state, final IntCoordinates pos) {
        final int dotScale = 15;
        final int energizerScale = 5;
        final int wallScale = 10;

        var group = new BorderPane();
        group.setMaxSize(scale, scale);
        group.setMinSize(scale, scale);

        Cell cell = state.getConfig().getCell(pos);

        var dot = new Circle();
        group.setCenter(dot);
        dot.setRadius(
            switch (cell.getContent()) {
            case DOT -> scale / dotScale;
            case ENERGIZER -> scale / energizerScale;
            default -> 0;
        });
        dot.setFill(Color.YELLOW);

        if (cell.getNorthWall()) {
            var wall = new Rectangle();
            wall.setHeight(scale / wallScale);
            wall.setWidth(scale);
            wall.setFill(Color.BLUE);
            group.setTop(wall);
        }
        if (cell.getEastWall()) {
            var wall = new Rectangle();
            wall.setHeight(scale);
            wall.setWidth(scale / wallScale);
            wall.setFill(Color.BLUE);
            group.setRight(wall);
        }
        if (cell.getSouthWall()) {
            var wall = new Rectangle();
            wall.setHeight(scale / wallScale);
            wall.setWidth(scale);
            wall.setFill(Color.BLUE);
            group.setBottom(wall);
        }
        if (cell.getWestWall()) {
            var wall = new Rectangle();
            wall.setHeight(scale);
            wall.setWidth(scale / wallScale);
            wall.setFill(Color.BLUE);
            group.setLeft(wall);
        }
        return new GraphicsUpdater() {
            @Override
            public void update() {
                Cell cell = state.getConfig().getCell(pos);
                dot.setVisible(cell.isDrawn());
            }

            @Override
            public Node getNode() {
                return group;
            }
        };
    }

    /**
     * Méthode pour vérifier la position des targets
     * en les affichant sur la map.
     * @param pos Position du target
     * @return {@code GraphicsUpdater}
     */
    public GraphicsUpdater showTarget(final RealCoordinates pos) {
        final int radius = 5;
        var group = new Group();
        group.setTranslateX(pos.x() * scale);
        group.setTranslateY(pos.y() * scale);
        var dot = new Circle();
        group.getChildren().add(dot);
        dot.setRadius(radius);
        dot.setCenterX(scale / 2);
        dot.setCenterY(scale / 2);
        dot.setFill(Color.color(Math.random(), Math.random(), Math.random()));
        return new GraphicsUpdater() {
            @Override
            public void update() {
                dot.setVisible(true);
            }

            @Override
            public Node getNode() {
                return group;
            }
        };
    }
}
