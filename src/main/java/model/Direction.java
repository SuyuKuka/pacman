package model;

import config.Cell;

public enum Direction {
    NONE, NORTH, EAST, SOUTH, WEST;

    public boolean wall(Cell c) {
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
