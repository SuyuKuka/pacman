package model;

import geometry.RealCoordinates;

public sealed interface Critter permits Ghost, PacMan {
    RealCoordinates getPos();

    Direction getDirection();

    double getSpeed();

    /**
     * @param deltaTNanoSeconds time since the last update in nanoseconds
     * @return the next position if there is no wall
     */
    default RealCoordinates nextPos(long deltaTNanoSeconds) {
        return nextPos(deltaTNanoSeconds, getDirection());
    }

    default RealCoordinates nextPos(long deltaTNanoSeconds, Direction d) {
        return getPos().plus((switch (d) {
            case NONE -> RealCoordinates.ZERO;
            case NORTH -> RealCoordinates.NORTH_UNIT;
            case EAST -> RealCoordinates.EAST_UNIT;
            case SOUTH -> RealCoordinates.SOUTH_UNIT;
            case WEST -> RealCoordinates.WEST_UNIT;
        }).times(getSpeed()*deltaTNanoSeconds * 1E-9));
    }

    /**
     * La méthode téléporte Pac-Man lorsqu'il sort par l'un des côtés de la map et le replace au côté opposé.<br><br>
     * Elle recalibre aussi la position d'un {@code Critter} lorsqu'il traverse le centre d'une case afin de faciliter les opérations lors
     * des virages.
     * @param deltaTns La variation du temps entre deux instants
     * @param curPos La position actuelle
     * @param nextPos La position suivante
     * @param height La hauteur de la map
     * @param width La largeur de la map
     */
    default void calibratePos(long deltaTns, RealCoordinates curPos, RealCoordinates nextPos, int height, int width) {
        RealCoordinates calibratedPos = curPos.calibrate(nextPos);
        if (nextPos.equals(calibratedPos)) {
            setPos(nextPos.warp(width, height));
        } else {
            setPos(calibratedPos);
        }
    }

    void setPos(RealCoordinates realCoordinates);
    void setDirection(Direction direction);
}
