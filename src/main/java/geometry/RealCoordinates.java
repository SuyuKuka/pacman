package geometry;

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
     * Renvoie un boolean vérifiant si 2 coordonnées sont identiques.
     * @param other
     * @return {@code boolean}
     */
    public boolean equals(final RealCoordinates other) {
        return x == other.x && y == other.y;
    }

    /**
     * Additionne les deux coordonnées.
     * @param other {@link RealCoordinates}
     * @return {@link RealCoordinates}
     */
    public RealCoordinates plus(final RealCoordinates other) {
        return new RealCoordinates(x + other.x, y + other.y);
    }

    /**
     * Soustrait les deux coordonnées.
     * @param other {@link RealCoordinates}
     * @return {@link RealCoordinates}
     */
    public RealCoordinates minus(final RealCoordinates other) {
        return new RealCoordinates(x - other.x, y - other.y);
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
     * Méthode qui renvoie vraie si les 2 {@link RealCoordinates} se trouvent sur les mêmes cellules
     * {@code this}.
     * @param nextPos
     * @return {@code boolean}
     */
    public boolean haveSameNeighbours(final RealCoordinates nextPos) {
        Set<IntCoordinates> curNeighbours = intNeighbours();
        Set<IntCoordinates> nextNeighbours = nextPos.intNeighbours();
        return curNeighbours.containsAll(nextNeighbours)
            && curNeighbours.size() == nextNeighbours.size();
    }

    /**
     * Téléportation de gauche à droite et de haut en bas.
     * @param width Largeur de la map
     * @param height Hauteur de la map
     * @return Les {@code RealCoordinates} de la position
     * après la téléportation.
     */
    public RealCoordinates warp(final int width, final int height) {
        double rx = x;
        double ry = y;
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
     * @param precision {@code boolean}
     * @return Retourne un {@link RealCoordinates} qui prend soit
     * les coordonnées exacte du centre d'une case traversée,
     * soit la valeur{@code end}.
     */
    public RealCoordinates centeredPos(final RealCoordinates end, final boolean precision) {
        //CHECKSTYLE:OFF
        final Direction d = this.getDirectionTo(end);
        final double value = precision ? 0.5 : 1;
        switch (d) {
        case NORTH:
            if (y % value != 0 && end.y() % value > this.y() % value) {
                return new RealCoordinates(x, y - doubleMod(y, value));
            }
            break;
        case EAST:
            if (end.x() % value < this.x() % value) {
                return new RealCoordinates((x - x % value) + value, y);
            }
            break;
        case SOUTH:
            if (y % value != 0 && end.y() % value < this.y() % value) {
                return new RealCoordinates(x, (y - y % value) + value);
            }
            break;
        case WEST:
            if (x % value != 0 && end.x() % value > this.x() % value) {
                return new RealCoordinates(x - doubleMod(x, value), y);
            }
            break;
        default:
            break;
        }
        return end;
        //CHECKSTYLE:ON
    }

    /**
     * Fonction qui renvoie le modulo de 2 nombres double proprement pour
     * les nombres négatifs.
     * @param x
     * @param y
     */
    public static double doubleMod(final double x, final double y) {
        return (x - Math.floor(x / y) * y);
    }

    /**
     * Arrondie le {@link RealCoordinates} et renvoie un {@link IntCoordinates}.
     * @return {@link IntCoordinates}
     */
    public IntCoordinates toIntCoordinates() {
        return new IntCoordinates((int) Math.round(x), (int) Math.round(y));
    }

    /**
     * Renvoie un {@link RealCoordinates} avec une des coordonnée arrondie en fonction de
     * la direction prise.
     * @return {@link RealCoordinates}
     */
    public RealCoordinates roundFromDirection(final Direction dir) {
        switch (dir) {
            case NORTH:
            case SOUTH:
                return new RealCoordinates(x, Math.round(y));
            case EAST:
            case WEST:
                return new RealCoordinates(Math.round(x), y);
            default:
                break;
        }
        return new RealCoordinates(Math.round(x), Math.round(y));
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
