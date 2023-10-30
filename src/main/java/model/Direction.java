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
}
