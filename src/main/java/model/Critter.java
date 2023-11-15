package model;

import config.MazeConfig;
import geometry.RealCoordinates;

public sealed class Critter permits Ghost, PacMan {

    private static final int NORMAL_SPEED = 5;

    private RealCoordinates pos;
    private RealCoordinates spawnPos;
    private double speed = NORMAL_SPEED;
    private Direction direction = Direction.NONE;

   /**
     * Méthode qui retourne la prochaine position en fonction de la vitesse
     * et de la direction actuelle du {@link Critter}.
     * @param deltaTNS Tick
     * @return {@code RealCoordinates}
     */
    public final RealCoordinates nextPos(final long deltaTNS) {
        return nextPos(deltaTNS, getDirection());
    }

    /**
     * Méthode qui retourne la prochaine position en fonction de la direction
     * donnée en paramètre et de la vitesse du {@link Critter}.
     * @param deltaTNS Tick
     * @param d {@code Direction}
     * @return {@code RealCoordinates}
     */
    public RealCoordinates nextPos(final long deltaTNS, final Direction d) {
        final double nanoseconds = 1E-9;
        return getPos().plus((
            switch (d) {
            case NONE -> RealCoordinates.ZERO;
            case NORTH -> RealCoordinates.NORTH_UNIT;
            case EAST -> RealCoordinates.EAST_UNIT;
            case SOUTH -> RealCoordinates.SOUTH_UNIT;
            case WEST -> RealCoordinates.WEST_UNIT;
            default -> RealCoordinates.ZERO;
        }).times(getSpeed() * deltaTNS * nanoseconds));
    }

    /**
     * La méthode téléporte le{@code Critter}lorsqu'il sort par l'un des côtés de la map
     * et le replace au côté opposé.<br><br>
     * Elle recalibre aussi la position du{@code Critter}lorsqu'il traverse
     * le centre d'une case afin de faciliter les opérations lors
     * des virages.
     * @param curPos La position actuelle
     * @param nextPos La position suivante
     * @param config {@link MazeConfig}
     * @param precision {@code boolean}
     */
    public void setCalibratedPos(final RealCoordinates curPos,
        final RealCoordinates nextPos, final MazeConfig config, final boolean precision) {
        setPos(curPos.centerCell(nextPos.warp(config.getWidth(), config.getHeight()), precision));
    }

    public static int getNormalSpeed() {
        return NORMAL_SPEED;
    }

    public final RealCoordinates getPos() {
        return pos;
    }

    public final void setPos(final RealCoordinates pos) {
        this.pos = pos;
    }

    public final RealCoordinates getSpawnPos() {
        return spawnPos;
    }

    public final void setSpawnPos(final RealCoordinates spawnPos) {
        this.spawnPos = spawnPos;
    }

    public final Direction getDirection() {
        return direction;
    }

    public final void setDirection(final Direction direction) {
        this.direction = direction;
    }

    public final double getSpeed() {
        return speed;
    }

    public final void setSpeed(final double speed) {
        this.speed = speed;
    }
}
