package model;


import config.Cell;
import config.Cell.Content;

import geometry.IntCoordinates;
import gui.GraphicsBuffer;
import utils.Music;

public final class PacMan extends Critter {

    private static final int ENERGIZED_SPEED = 9;
    public static final double MAX_ENERGIZED_TIME = 6;

    /** Dernière {@link Direction} de {@link PacMan} qui n'est pas {@link Direction#NONE}. */
    private Direction lastDirection = Direction.NONE;
    // Etat énergisé de PacMan :
    private static double energizedTimer = 0;
    private boolean energized = false;
    private DirectionBuffer directionBuffer = new DirectionBuffer();
    private GraphicsBuffer graphicsBuffer = new GraphicsBuffer();

    /**
     * Fonction générale de {@link PacMan} qui actualise tous ses paramètres.
     * @param state
     * @param deltaTns
     */
    public void update(final MazeState state, final long deltaTns) {
        updateLastDirection();
        eat(state);
        if (directionBuffer.isBufferDirectionValid(this, deltaTns, state.getConfig())) {
            setDirection(directionBuffer.getBufferDirection());
        }
        updateSpeed();
        updatePosition(deltaTns, state.getConfig(), false);
        directionBuffer.updateDirectionBufferTime(deltaTns);
        if (this != state.getConfig().getPacman2()) {
            updateEnergizedTime(deltaTns);
        }
    }

    /**
     * Actualise le paramètre {@code lastDirection}.
     */
    public void updateLastDirection() {
        if (getDirection() != Direction.NONE) {
            lastDirection = getDirection();
        }
    }

    /**
     * Met à jour l'état de PacMan s'il mange un energizer. <br><br>
     * Augmente le score du jeu s'il mange un dot.
     * @param state
     */
    public void eat(final MazeState state) {
        if (!eatDot(state)) {
            eatEnergizer(state);
        }
    }

    /**
     * Renvoie true si PacMan mange un dot et augmente le score.
     * @param state
     * @return {@code boolean}
     */
    public boolean eatDot(final MazeState state) {
        IntCoordinates pacPos = getPos().toIntCoordinates();
        if (state.getConfig().getCell(pacPos).initialContent() == Cell.Content.DOT) {
            Music.playSound("eat");
            boolean restricted = state.getConfig().getGrid()[pacPos.y()][pacPos.x()].restricted();
            state.getConfig().getGrid()[pacPos.y()][pacPos.x()] =
                new Cell(false, false, false, false, Content.NOTHING, restricted);
            final int dotScore = 10;
            state.addScore(dotScore);
            return true;
        }
        return false;
    }

    /**
     * Rend PacMan énergisé s'il a mangé un énergiseur.
     * @param state
     */
    public void eatEnergizer(final MazeState state) {
        IntCoordinates pacPos = getPos().toIntCoordinates();
        if (state.getConfig().getCell(pacPos).initialContent() == Cell.Content.ENERGIZER) {
            Music.playSound("eat");
            Music.playLoopingBgMusic("energized");
            boolean restricted = state.getConfig().getGrid()[pacPos.y()][pacPos.x()].restricted();
            state.getConfig().getGrid()[pacPos.y()][pacPos.x()] =
                new Cell(false, false, false, false, Content.NOTHING, restricted);
            setEnergizedTimer(energized ? energizedTimer : 0);
            if (state.getConfig().isCoop()) {
                state.getConfig().getPacman().setEnergized(true);
                state.getConfig().getPacman2().setEnergized(true);
            } else {
                energized = true;
            }
        }
    }

    /**
     * Méthode qui met à jour la vitesse de PacMan en fonction de s'il est énergisé ou non.
     */
    public void updateSpeed() {
        if (energized) {
            setSpeed(ENERGIZED_SPEED);
        } else {
            setSpeed(getNormalSpeed());
        }
    }

    /**
     * Incrémente {@link #energizedTime} à chaque tick.
     * {@link #energizedTimer} est réinitialisé s'il dépasse
     * {@link #MAX_ENERGIZED_TIME}.
     * @param deltaTns Durée d'un tick
     */
    public void updateEnergizedTime(final long deltaTns) {
        if (isEnergized()) {
            final double nanoseconds = Math.pow(10, 9);
            setEnergizedTimer(energizedTimer + deltaTns / nanoseconds);
            if (energizedTimer >= MAX_ENERGIZED_TIME) {
                setEnergizedTimer(0);
                energized = false;
            }
        }
    }

    /**
     * Réinitialise l'état de Pac-Man.
     * {@link #energized}, {@link DirectionBuffer#bufferDirection}. {@link Critter#direction},
     * {@link #lastDirection} et la position de Pac-Man sont remis à zéro.
     */
    public void resetPacMan() {
        energized = false;
        setDirection(Direction.NONE);
        directionBuffer.setBufferDirection(Direction.NONE);
        lastDirection = Direction.NONE;
        setPos(getSpawnPos());
    }

    public static int getEnergizedSpeed() {
        return ENERGIZED_SPEED;
    }

    public Direction getLastDirection() {
        return lastDirection;
    }

    public void setLastDirection(final Direction lastDirection) {
        this.lastDirection = lastDirection;
    }

    public boolean isEnergized() {
        return energized;
    }

    public void setEnergized(final boolean energized) {
        this.energized = energized;
    }

    public static double getMaxEnergizedTime() {
        return MAX_ENERGIZED_TIME;
    }

    public static void setEnergizedTimer(final double energizedTimer) {
        PacMan.energizedTimer = energizedTimer;
    }

    public static double getEnergizedTimer() {
        return energizedTimer;
    }

    public DirectionBuffer getDirectionBuffer() {
        return directionBuffer;
    }

    public GraphicsBuffer getGraphicsBuffer() {
        return graphicsBuffer;
    }
}
