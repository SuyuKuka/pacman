package geometry;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.Direction;

public record RealCoordinates(double x, double y) {

    public static final RealCoordinates ZERO = new RealCoordinates(0, 0);
    public static final RealCoordinates NORTH_UNIT = new RealCoordinates(0, -1);
    public static final RealCoordinates EAST_UNIT = new RealCoordinates(1, 0);
    public static final RealCoordinates SOUTH_UNIT = new RealCoordinates(0, 1);
    public static final RealCoordinates WEST_UNIT = new RealCoordinates(-1, 0);


    public RealCoordinates plus(RealCoordinates other) {
        return new RealCoordinates(x + other.x, y + other.y);
    }

    public RealCoordinates times(double multiplier) {
        return new RealCoordinates(x * multiplier, y * multiplier);
    }

    /**
     *
     * @return the coordinates of all integer squares that a unit square with current coordinates would intersect
      */
    public Set<IntCoordinates> intNeighbours() {
        return new HashSet<>(List.of(
                new IntCoordinates((int) Math.floor(x), (int) Math.floor(y)),
                new IntCoordinates((int) Math.floor(x), (int) Math.ceil(y)),
                new IntCoordinates((int) Math.ceil(x), (int) Math.floor(y)),
                new IntCoordinates((int) Math.ceil(x), (int) Math.ceil(y))
        )
        );
    }

    public IntCoordinates round() {
        return new IntCoordinates((int) Math.round(x), (int) Math.round(y));
    }

    public RealCoordinates floorX() {
        return new RealCoordinates((int) Math.floor(x), y);
    }

    public RealCoordinates floorY() {
        return new RealCoordinates(x, (int) Math.floor(y));
    }

    public RealCoordinates ceilX() {
        return new RealCoordinates((int) Math.ceil(x), y);
    }

    public RealCoordinates ceilY() {
        return new RealCoordinates(x, (int) Math.ceil(y));
    }

    public RealCoordinates warp(int width, int height) {
        var rx = x;
        var ry = y;
        while (Math.round(rx) < 0)
            rx += width;
        while (Math.round(ry) < 0)
            ry += height;
        while (Math.round(rx) >= width)
            rx -= width;
        while (Math.round(ry) >= height)
            ry -= height;
        return new RealCoordinates(rx, ry);
    }
    /**
     * Retourne une Direction calculée en fonction de 2 RealCoordinates
     * @param end Le RealCoordinates destination
     * @return Donne la Direction de this à end
     */
    public Direction getDirectionTo(RealCoordinates end) {
        double deltaY = end.y() - this.y();
        if (deltaY != 0) {
            if (deltaY < 0) {
            return Direction.NORTH;
            }
            return Direction.SOUTH;
        }
        double deltaX = end.x() - this.x();
        if (deltaX < 0) {
            return Direction.WEST;
        }
        return Direction.EAST;
    }

    /**
     * Fonction qui permet d'obtenir exactement le centre d'une case si on la traverse.
     * Sinon, renvoie la coordonnée de destination initiale.
     * Par exemple : 
     * this.y = 0.9 et end.y = 1.1 retourne 1 
     * this.y = 0.2 et end.y = 0.3 retourne 0.3 (car il n'y a pas eu traversée)
     * @param end Le RealCoordinates destination
     * @return Retourne un RealCoordinates qui prend soit :
     * - les coordonnées exacte du centre d'une case traversée
     * - la valeur end
     */
    public RealCoordinates calibrate(RealCoordinates end) {
        if (this.intNeighbours().containsAll(end.intNeighbours())) {
            return end;
        }
        Direction d = this.getDirectionTo(end);
        switch (d) {
        case NORTH:
            if (Math.floor(this.y()) != this.y() && Math.floor(this.y()) - Math.ceil(end.y()) == 0) {
                return this.floorY();
            }       
            break;
        case EAST:
            if (Math.floor(this.x()) != this.x() && Math.ceil(this.x()) - Math.floor(end.x()) == 0) 
                return this.ceilX();
            break;
        case SOUTH:
            if (Math.floor(this.y()) != this.y() && Math.ceil(this.y()) - Math.floor(end.y()) == 0) {
                return this.ceilY();
            }
            break;
        case WEST:
            if (Math.floor(this.x()) != this.x() && Math.floor(this.x()) - Math.ceil(end.x()) == 0) 
                return this.floorX();
            break;
        default:
            break;
        }
        return end;
    }
}
