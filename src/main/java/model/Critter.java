package model;

import geometry.RealCoordinates;

public sealed interface Critter permits Ghost, PacMan {
    /**
     * Retourne la position du{@code Critter}.
     * @return {@code RealCoordinates}
     */
    RealCoordinates getPos();

    /**
     * Retourne la direction du{@code Critter}.
     * @return {@code Direction}
     */
    Direction getDirection();

    /**
     * Retourne la vitesse du{@code Critter}.
     * @return {@code double}
     */
    double getSpeed();

    /**
     * @param deltaTNanoSeconds time since the last update in nanoseconds
     * @return the next position if there is no wall
     */
    default RealCoordinates nextPos(long deltaTNanoSeconds) {
        return nextPos(deltaTNanoSeconds, getDirection());
    }

    /**
     * Méthode qui retourne la prochaine position en fonction de la direction
     * et de la vitesse des{@code Critter}.
     * @param deltaTNanoSeconds Tick
     * @param d {@code Direction}
     * @return {@code RealCoordinates}
     */
    default RealCoordinates nextPos(long deltaTNanoSeconds, Direction d) {
        final double nanoseconds = 1E-9;
        return getPos().plus((
            switch (d) {
            case NONE -> RealCoordinates.ZERO;
            case NORTH -> RealCoordinates.NORTH_UNIT;
            case EAST -> RealCoordinates.EAST_UNIT;
            case SOUTH -> RealCoordinates.SOUTH_UNIT;
            case WEST -> RealCoordinates.WEST_UNIT;
            default -> RealCoordinates.ZERO;
        }).times(getSpeed() * deltaTNanoSeconds * nanoseconds));
    }

    /**
     * La méthode téléporte Pac-Man lorsqu'il sort par l'un des côtés de la map
     * et le replace au côté opposé.<br><br>
     * Elle recalibre aussi la position d'un {@code Critter} lorsqu'il traverse
     * le centre d'une case afin de faciliter les opérations lors
     * des virages.
     * @param deltaTns La variation du temps entre deux instants
     * @param curPos La position actuelle
     * @param nextPos La position suivante
     * @param height La hauteur de la map
     * @param width La largeur de la map
     */
    default void calibratePos(
        long deltaTns, RealCoordinates curPos, RealCoordinates nextPos,
        int height, int width
        ) {
        RealCoordinates calibratedPos = curPos.centerCell(nextPos);
        if (nextPos.equals(calibratedPos)) {
            setPos(nextPos.warp(width, height));
        } else {
            setPos(calibratedPos);
        }
    }

    /**
     * La méthode renvoie {@code true} si le{@code Critter}
     * se trouve à l'entrée du spawn des fantômes.
     * @return {@code true} si au spawn entry
     */
    default boolean atSpawnEntry() {
        final int spawnX = 8;
        final int spawnY = 8;
        return (int) getPos().x() == spawnX && (int) getPos().y() == spawnY;
    };

    /**
     * Set la position du{@code Critter}.
     * @param realCoordinates La nouvelle position
     */
    void setPos(RealCoordinates realCoordinates);

    /**
     * Set la direction du{@code Critter}.
     * @param direction Nouvelle direction
     */
    void setDirection(Direction direction);
}
