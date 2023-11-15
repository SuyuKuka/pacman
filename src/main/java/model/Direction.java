package model;

import config.Cell;

public enum Direction {
    /** Directions possibles. */
    NONE, NORTH, EAST, SOUTH, WEST;

    /**
     * Méthode qui retourne true si la cellule{@code Cell c}
     * contient un mur selon la direction du{@code Critter}.
     * @param c {@code Cell}
     * @return {@code boolean}
     */
    public boolean wall(final Cell c) {
        switch (this) {
            case NORTH:
                return c.northWall();
            case EAST:
                return c.eastWall();
            case SOUTH:
                return c.southWall();
            case WEST:
                return c.westWall();
            default:
                break;
        }
        return true;
    }

    /**
     * Renvoie la {@link Direction} opposée à celle appelée.
     * @return {@link Direction}
     */
    public Direction getOpposite() {
        switch (this) {
            case NORTH:
                return SOUTH;
            case EAST:
                return WEST;
            case SOUTH:
                return NORTH;
            case WEST:
                return EAST;
            default:
                break;
        }
        return NONE;
    }
}
