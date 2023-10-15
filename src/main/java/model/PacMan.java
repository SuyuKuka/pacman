package model;

import geometry.RealCoordinates;

/**
 * Implements Pac-Man character using singleton pattern. FIXME: check whether singleton is really a good idea.
 * (Faudra transformer Pac-Man en objet si on veut ajouter du multijoueur local et associer à chaque pacman un pacmanController)
 */
public final class PacMan implements Critter {
    private Direction direction = Direction.NONE;
    private Direction lastDirection;
    private RealCoordinates pos;
    private boolean energized;
    private int pacManMoved;
    /* Buffer */
    private Direction bufferDirection = Direction.NONE; // Direction stockée dans le buffer
    private final double bufferMaxTime = 0.7; // Temps en seconde avant que la direction stockée dans le buffer se retire
    private double bufferDurationTime = 0; // Temps passé sans que l'utilisateur est input une touche

    private PacMan() {
    }

    public static final PacMan INSTANCE = new PacMan();

    @Override
    public RealCoordinates getPos() {
        return pos;
    }

    @Override
    public double getSpeed() {
        return isEnergized() ? 9 : 6;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public void setPos(RealCoordinates pos) {
        this.pos = pos;
    }

    /**
     *
     * @return whether Pac-Man just ate an energizer
     */
    public boolean isEnergized() {
        // TODO handle timeout!
        return energized;
    }

    public void setEnergized(boolean energized) {
        this.energized = energized;
    }

    public Direction getLastDirection() {
        return lastDirection;
    }

    public void setLastDirection(Direction lastDirection) {
        this.lastDirection = lastDirection;
    }

    public int getPacManMoved() {
        return pacManMoved;
    }

    public void setPacManMoved(int pacManMoved) {
        this.pacManMoved = pacManMoved;
    }

    public void setBufferDurationTime(double bufferDurationTime) {
      this.bufferDurationTime = bufferDurationTime;
    }

    public Direction getBufferDirection() {
      return bufferDirection;
    }

    public void setBufferDirection(Direction bufferDirection) {
      this.bufferDirection = bufferDirection;
    }

    public double getBufferDurationTime() {
      return bufferDurationTime;
    }

    public void increaseBufferDurationTime(long deltaTns) {
        bufferDurationTime += deltaTns / Math.pow(10, 9);
        if (bufferDurationTime >= bufferMaxTime) {
            bufferDurationTime = 0;
            bufferDirection = Direction.NONE;
        }
    }

}
