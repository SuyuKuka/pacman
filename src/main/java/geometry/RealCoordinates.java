package geometry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import model.Direction;

public record RealCoordinates(double x, double y) {
    /** Position du zero. */
    public static final RealCoordinates ZERO = new RealCoordinates(0, 0);
    /** Une unité au Nord. */
    public static final RealCoordinates NORTH_UNIT = new RealCoordinates(0, -1);
    /** Une unité à l'Est. */
    public static final RealCoordinates EAST_UNIT = new RealCoordinates(1, 0);
    /** Une unité au Sud. */
    public static final RealCoordinates SOUTH_UNIT = new RealCoordinates(0, 1);
    /** Une unité à l'Ouest. */
    public static final RealCoordinates WEST_UNIT = new RealCoordinates(-1, 0);

    /**
     * Additionne les deux coordonnées.
     * @param other {@link RealCoordinates}
     * @return {@link RealCoordinates}
     */
    public RealCoordinates plus(final RealCoordinates other) {
        return new RealCoordinates(x + other.x, y + other.y);
    }

    /**
     * Multiplie les coordonnées par{@code multiplier}.
     * @param multiplier Le multiplicateur
     * @return {@link RealCoordinates}
     */
    public RealCoordinates times(final double multiplier) {
        return new RealCoordinates(x * multiplier, y * multiplier);
    }

    /**
     * Méthode qui renvoie un {@code Set<IntCoordinates>} de toutes les
     * {@code Cell}que{@code this}superpose
     * sur un rayon de 1.
     * @return Une {@code List<IntCoordinates>} contenant les cellules.
     */
    public Set<IntCoordinates> intNeighbours() {
        return new HashSet<>(
            List.of(
                new IntCoordinates((int) Math.floor(x), (int) Math.floor(y)),
                new IntCoordinates((int) Math.ceil(x), (int) Math.floor(y)),
                new IntCoordinates((int) Math.floor(x), (int) Math.ceil(y)),
                new IntCoordinates((int) Math.ceil(x), (int) Math.ceil(y))
            )
        );
    }

    /**
     * Méthode qui renvoie une liste contenant
     * les coordonnées de 3 cellules adjacentes :
     * à gauche, en face et à droite de la direction choisie.
     * @param direction La direction actuelle du fantôme.
     * @return Une {@code List<IntCoordinates>} contenant les cellules
     * adjacentes triées dans le sens horaire
     * (ordre : haut, droite, bas, gauche).
     */
    public List<IntCoordinates> getAdjacentCellsCoordinates(
        final Direction direction
        ) {
        List<IntCoordinates> coordinates = new ArrayList<>();
        switch (direction) {
            case NORTH:
                coordinates.add(0, new IntCoordinates((int) x, (int) y - 1));
                coordinates.add(1, new IntCoordinates((int) x + 1, (int) y));
                coordinates.add(2, new IntCoordinates((int) x - 1, (int) y));
                break;
            case EAST:
                coordinates.add(0, new IntCoordinates((int) x, (int) y - 1));
                coordinates.add(1, new IntCoordinates((int) x + 1, (int) y));
                coordinates.add(2, new IntCoordinates((int) x, (int) y + 1));
                break;
            case SOUTH:
                coordinates.add(0, new IntCoordinates((int) x + 1, (int) y));
                coordinates.add(1, new IntCoordinates((int) x, (int) y + 1));
                coordinates.add(2, new IntCoordinates((int) x - 1, (int) y));
                break;
            case WEST:
                coordinates.add(0, new IntCoordinates((int) x, (int) y - 1));
                coordinates.add(1, new IntCoordinates((int) x, (int) y + 1));
                coordinates.add(2, new IntCoordinates((int) x - 1, (int) y));
                break;
            default:
                return new ArrayList<>();
        }
        return coordinates;
    }

    /**
     * Téléportation de gauche à droite et de haut en bas.
     * @param width Largeur de la map
     * @param height Hauteur de la map
     * @return Les {@code RealCoordinates} de la position
     * après la téléportation.
     */
    public RealCoordinates warp(final int width, final int height) {
        var rx = x;
        var ry = y;
        while (Math.round(rx) < 0) {
            rx += width;
        }
        while (Math.round(ry) < 0) {
            ry += height;
        }
        while (Math.round(rx) >= width) {
            rx -= width;
        }
        while (Math.round(ry) >= height) {
            ry -= height;
        }
        return new RealCoordinates(rx, ry);
    }
    /**
     * Retourne une Direction calculée en fonction de 2 RealCoordinates.
     * @param end Le {@code RealCoordinates} destination
     * @return Donne la {@code Direction} de this à {@code end}
     */
    public Direction getDirectionTo(final RealCoordinates end) {
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
     * Méthode qui permet d'obtenir exactement le centre d'une case
     * si on la traverse (entre{@code this}et{@code end}). <br><br>
     * Sinon, renvoie la coordonnée de destination initiale.
     * @param end Le RealCoordinates destination
     * @return Retourne un {@link RealCoordinates} qui prend soit
     * les coordonnées exacte du centre d'une case traversée,
     * soit la valeur{@code end}.
     */
    public RealCoordinates centerCell(final RealCoordinates end) {
        if (this.intNeighbours().containsAll(end.intNeighbours())) {
            return end;
        }
        Direction d = this.getDirectionTo(end);
        switch (d) {
        case NORTH:
        case SOUTH:
            if ((int) this.y() != this.y()) {
                return this.adjustCoordinates(d);
            }
            break;
        case EAST:
        case WEST:
            if ((int) this.x() != this.x()) {
                return this.adjustCoordinates(d);
            }
            break;
        default:
            break;
        }
        return end;
    }
    /**
     * Méthode qui renvoie un {@link RealCoordinates} "ajustée"
     * en fonction des cellules par rapport à la {@link Direction}.
     * @param direction Direction choisie.
     * @return Un {@code RealCoordinates} modifié ou non.
     */

    public RealCoordinates adjustCoordinates(final Direction direction) {
        switch (direction) {
            case NORTH:
                return this.floorY();
            case EAST:
                return this.ceilX();
            case SOUTH:
                return this.ceilY();
            case WEST:
                return this.floorX();
            default:
                break;
        }
        return this;
    }

    /**
     * Renvoie une unité de {@link RealCoordinates}
     * en fonction de la {@link Direction}.
     * @param d {@link Direction}
     * @return {@code RealCoordinates}
     */
    public static RealCoordinates directionToUnit(final Direction d) {
        switch (d) {
            case NORTH:
                return NORTH_UNIT;
            case EAST:
                return EAST_UNIT;
            case SOUTH:
                return SOUTH_UNIT;
            case WEST:
                return WEST_UNIT;
            default:
                break;
        }
        return ZERO;
    }

    /**
     * Arrondie le {@link RealCoordinates} et renvoie un {@link IntCoordinates}.
     * @return {@link IntCoordinates}
     */
    public IntCoordinates round() {
        return new IntCoordinates((int) Math.round(x), (int) Math.round(y));
    }

    /**
     * Renvoie un {@link RealCoordinates} dont on a
     * "floor" la coordonnée {@link #x}.
     * @return {@link RealCoordinates}
     */
    public RealCoordinates floorX() {
        return new RealCoordinates((int) Math.floor(x), y);
    }

    /**
     * Renvoie un {@link RealCoordinates} dont on a
     * "floor" la coordonnée {@link #y}.
     * @return {@link RealCoordinates}
     */
    public RealCoordinates floorY() {
        return new RealCoordinates(x, (int) Math.floor(y));
    }

    /**
     * Renvoie un {@link RealCoordinates} dont on a
     * "ceil" (plafonné) la coordonnée {@link #x}.
     * @return {@link RealCoordinates}
     */
    public RealCoordinates ceilX() {
        return new RealCoordinates((int) Math.ceil(x), y);
    }

    /**
     * Renvoie un {@link RealCoordinates} dont on a
     * "ceil" (plafonné) la coordonnée {@link #y}.
     * @return {@link RealCoordinates}
     */
    public RealCoordinates ceilY() {
        return new RealCoordinates(x, (int) Math.ceil(y));
    }
}
