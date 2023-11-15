package model;


import java.util.Set;
import config.Cell;
import config.Cell.Content;
import config.MazeConfig;

import geometry.IntCoordinates;
import geometry.RealCoordinates;
import utils.Music;

public final class PacMan extends Critter {

    private static final int ENERGIZED_SPEED = 13;

    /** Dernière {@link Direction} de {@link PacMan} qui n'est pas {@link Direction#NONE}. */
    private Direction lastDirection = Direction.NONE;
    /* Buffer du Controller de PacMan
    Pour faciliter le mouvement lors des intersections, le jeu garde en mémoire la dernière
    direction que l'utilisateur a entré même si celle-ci n'est pas accessible à ce moment
    pendant une très courte durée et l'utilise si possible.
    */
    private Direction bufferDirection = Direction.NONE;
    private static final double MAX_BUFFER_DIRECTION_TIME = 0.7;
    private double directionBufferTimer = 0;
    // Etat énergisé de PacMan :
    private boolean energized = false;
    private static final double MAX_ENERGIZED_TIME = 7;
    private double energizedTimer = 0;

    /**
     * Fonction générale de {@link PacMan} qui actualise tous ses paramètres.
     * @param state
     * @param deltaTns
     */
    public void update(final MazeState state, final long deltaTns) {
        updateLastDirection();
        eat(state);
        if (isBufferDirectionValid(deltaTns, state.getConfig())) {
            setDirection(bufferDirection);
        }
        updateSpeed();
        updatePosition(deltaTns, state.getConfig());
        updateDirectionBufferTime(deltaTns);
        updateEnergizedTime(deltaTns);
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
            energizedTimer = 0;
            energized = true;
        }
    }

    /**
     * Méthode qui vérifie si la {@link Direction} stockée dans le Buffer est valide.
     * S'il l'est, remplace la {@link Direction} de {@link PacMan} par celui-ci et
     * réinitialise le contenu du Buffer. Renvoie faux le cas échant.
     * @param deltaTns
     * @param config
     * @return {@code boolean}
     */
    public boolean isBufferDirectionValid(final long deltaTns, final MazeConfig config) {
        if (bufferDirection == Direction.NONE) {
            return false;
        }
        final Set<IntCoordinates> neighbours = nextPos(deltaTns, bufferDirection).intNeighbours();
        final int maxNeighbours = 4;
        if (neighbours.size() >= maxNeighbours) {
            return false;
        }
        for (IntCoordinates c: neighbours) {
            Cell cell = config.getCell(c);
            if (cell.hasWall() || cell.initialContent() == Content.SPAWN) {
                return false;
            }
        }
        return true;
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
     * Méthode qui actualise la position de PacMan.
     * @param deltaTns
     * @param config
     */
    public void updatePosition(final long deltaTns, final MazeConfig config) {
        if (wallCheck(deltaTns, config)) {
            return;
        }
        setCalibratedPos(getPos(), nextPos(deltaTns), config, false);
    }

    /**
     * Méthode qui vérifie si PacMan va rencontrer un mur.
     * @param deltaTns
     * @param config
     * @return {@code boolean}
     */
    public boolean wallCheck(final long deltaTns, final MazeConfig config) {
        RealCoordinates nextPos = nextPos(deltaTns);
        Set<IntCoordinates> nextNeighbours = nextPos.intNeighbours();
        if (!getPos().intNeighbours().containsAll(nextNeighbours)) {
            for (IntCoordinates c : nextNeighbours) {
                Cell cell = config.getCell(c);
                if (cell.hasWall() || cell.initialContent() == Content.SPAWN) {
                    setPos(getPos().centerCell(nextPos, false));
                    setDirection(Direction.NONE);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Incrémente {@link #bufferDurationTime} à chaque tick.
     * {@link #bufferDurationTime} est réinitialisé s'il dépasse
     * {@link #BUFFER_MAX_TIME}.
     * @param deltaTns Durée d'un tick
     */
    public void updateDirectionBufferTime(final long deltaTns) {
        final double nanoseconds = Math.pow(10, 9);
        directionBufferTimer += deltaTns / nanoseconds;
        if (directionBufferTimer >= MAX_BUFFER_DIRECTION_TIME) {
            directionBufferTimer = 0;
            bufferDirection = Direction.NONE;
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
            energizedTimer += deltaTns / nanoseconds;
            if (energizedTimer >= MAX_ENERGIZED_TIME) {
                energizedTimer = 0;
                energized = false;
            }
        }
    }

    /**
     * Réinitialise l'état de Pac-Man.
     * {@link #pacManMoved}, {@link #energized}, {@link #bufferDirection} sont
     * remis à zéro.
     */
    public void resetPacMan() {
        energized = false;
        setDirection(Direction.NONE);
        bufferDirection = Direction.NONE;
    }

    /**
     * Réinitialise la position de Pac-Man.
     * {@link #pacManMoved}, {@link #energized}, {@link #bufferDirection} sont remis à zéro.
     * @param state {@link MazeState} Etat actuel de la map
     */
    public void resetPacMan(final MazeState state) {
        setDirection(Direction.NONE);
        bufferDirection = Direction.NONE;
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

    public Direction getBufferDirection() {
        return bufferDirection;
    }

    public void setBufferDirection(final Direction bufferDirection) {
        this.bufferDirection = bufferDirection;
    }

    public static double getMaxBufferDirectionTime() {
        return MAX_BUFFER_DIRECTION_TIME;
    }

    public double getDirectionBufferTimer() {
        return directionBufferTimer;
    }

    public void setDirectionBufferTimer(final double directionBufferTimer) {
        this.directionBufferTimer = directionBufferTimer;
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

    public void setEnergizedTimer(final double energizedTimer) {
        this.energizedTimer = energizedTimer;
    }

    public double getEnergizedTimer() {
        return energizedTimer;
    }

}
