package model;

import config.Cell.Content;
import config.MazeConfig;

import geometry.IntCoordinates;
import geometry.RealCoordinates;


/**
 * Implements Pac-Man character using singleton pattern.
 * FIXME : check whether singleton is really a good idea.
 * (Faudra transformer Pac-Man en objet si on veut ajouter
 * du multijoueur local et associer à chaque pacman un pacmanController)
 */
public final class PacMan implements Critter {
    /** Direction de PacMan. */
    private Direction direction = Direction.NONE;
    /** Dernière direction de PacMan. */
    private Direction lastDirection;
    /** Position de PacMan. */
    private RealCoordinates pos;
    /**
     * 1 si Pac-Man bouge pour la première fois,
     * 0 sinon et -1 après qu'il ait bougé.
     */
    private int pacManMoved = 0;
    /** Etat energisé. */
    private boolean energized;

    // Buffer du Controller de PacMan
    /** Direction stockée à l'appuie d'une touche. */
    private Direction bufferDirection = Direction.NONE;
    /**
     * Temps en seconde avant que la direction stockée
     * dans le buffer se retire.
     */
    private static final double BUFFER_MAX_TIME = 0.7;
    /** Temps passé sans que l'utilisateur n'ait appuyé une touche. */
    private double bufferDurationTime = 0;

    // Timer :
    /** Durée depuis que Pac-Man est energisé. */
    private double energizedTimer = 0;
    /** Durée maximale de l'état energisé. */
    private static final double MAX_ENERGIZED_TIME = 3;

    private PacMan() {
    }

    /** Implémentation du singleton. */
    public static final PacMan INSTANCE = new PacMan();

    @Override
    public RealCoordinates getPos() {
        return pos;
    }

    @Override
    public double getSpeed() {
        final int energizedSpeed = 7;
        final int normalSpeed = 5;
        return isEnergized() ? energizedSpeed : normalSpeed;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public void setDirection(final Direction newDirection) {
        direction = newDirection;
    }

    @Override
    public void setPos(final RealCoordinates newPos) {
        pos = newPos;
    }

    /**
     * Retourne true si Pac-Man est energisé.
     * @return {@code boolean}
     */
    public boolean isEnergized() {
        return energized;
    }

    /**
     * Set l'état energisé à{@code true}si Pac-Man se trouve sur une cellule
     * avec un{@code ENERGIZER}. Le timer est reset s'il est déjà energisé.
     * @param mazeConfig {@link MazeConfig#config}
     * Configuration actuelle de la map
     */
    public void containsEnergizer(final MazeConfig mazeConfig) {
        if (mazeConfig.getCell(
                new IntCoordinates(
                    pos.round().x(), pos.round().y()
                )
            ).initialContent() == Content.ENERGIZER) {
            if (energized) {
                energizedTimer = 0;
            }
            energized = true;
        }
    }

    /**
     * Incrémente {@link #energizedTimer} à chaque tick.
     * {@link #energizedTimer} est réinitialisé s'il dépasse
     * {@link #MAX_ENERGIZED_TIME}.
     * @param deltaTns Durée d'un tick
     */
    public void increaseEnergizedTimer(final long deltaTns) {
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
     * Retourne la dernière direction de Pac-Man.
     * @return {@link #lastDirection}
     */
    public Direction getLastDirection() {
        return lastDirection;
    }

    /**
     * Retourne l'entier indiquant si Pac-Man a bougé.
     * @return {@link #pacManMoved}
     */
    public int getPacManMoved() {
        return pacManMoved;
    }

    /**
     * Set {@link #pacManMoved} à{@code state}.
     * @param state Choix entre 0, 1 et -1.
     */
    public void setPacManMoved(final int state) {
        pacManMoved = state;
    }

    /**
     * Set la durée du buffer {@link #bufferDurationTime}.
     * @param duration {@code double}
     */
    public void setBufferDurationTime(final double duration) {
        bufferDurationTime = duration;
    }

    /**
     * Retourne la direction du buffer {@link #bufferDirection}.
     * @return {@code Direction}
     */
    public Direction getBufferDirection() {
        return bufferDirection;
    }

    /**
     * Set la direction du {@link #bufferDirection}.
     * @param dir {@code Direction}
     */
    public void setBufferDirection(final Direction dir) {
        bufferDirection = dir;
    }

    /**
     * Incrémente {@link #bufferDurationTime} à chaque tick.
     * {@link #bufferDurationTime} est réinitialisé s'il dépasse
     * {@link #BUFFER_MAX_TIME}.
     * @param deltaTns Durée d'un tick
     */
    public void updateBufferDurationTime(final long deltaTns) {
        final double nanoseconds = Math.pow(10, 9);
        bufferDurationTime += deltaTns / nanoseconds;
        if (bufferDurationTime >= BUFFER_MAX_TIME) {
            bufferDurationTime = 0;
            bufferDirection = Direction.NONE;
        }
    }

    /**
     * Réinitialise l'état de Pac-Man.
     * {@link #pacManMoved}, {@link #energized}, {@link #bufferDirection} sont
     * remis à zéro.
     */
    public void resetPacMan() {
        PacMan.INSTANCE.pacManMoved = 0;
        PacMan.INSTANCE.energized = false;
        PacMan.INSTANCE.bufferDirection = Direction.NONE;
    }

    /**
     * Réinitialise la position de Pac-Man.
     * @param state {@code MazeState} Etat actuel de la map
     */
    public void resetPacManPos(final MazeState state) {
        PacMan.INSTANCE.direction = Direction.NONE;
        PacMan.INSTANCE.pos = state.getInitialPos().get(PacMan.INSTANCE);
    }

    /**
     * La méthode arrête Pac-Man lorsqu'il rencontre un mur.
     * Elle met aussi à jour sa position lorsqu'il traverse
     * une case pour faciliter les virages.
     * @param deltaTns La variation du temps entre deux instants.
     * @param state {@code MazeState} Etat actuel de la map
     */
    public void updatePacMan(final long deltaTns, final MazeState state) {
        var curPos = pos;
        var nextPos = nextPos(deltaTns);

        // Nécessaire pour éviter les problèmes de détermination
        // du target selon la direction de Pac-Man
        if (direction != Direction.NONE) {
            lastDirection = direction;
        }

        // Modifie ou non la direction de PacMan en fonction
        // du terrain du buffer
        if (canPacManTurn(bufferDirection, deltaTns, state.getConfig())) {
            nextPos = nextPos(deltaTns, bufferDirection);
            direction = bufferDirection;
            bufferDirection = Direction.NONE;
        }
        nextPos = calibratePacManPos(curPos, nextPos, state.getConfig());
        calibratePos(
            deltaTns, curPos, nextPos, state.getHeight(), state.getWidth()
        );
        updateBufferDurationTime(deltaTns);

        // Si Pac-Man mange un ENERGIZER
        var pacPos = PacMan.INSTANCE.getPos().round();
        if (!state.getGridState(new IntCoordinates(pacPos.x(), pacPos.y()))) {
            containsEnergizer(state.getConfig());
        }
        increaseEnergizedTimer(deltaTns);
    }

    /**
     * Méthode qui calcul la position à la frame suivante
     * de PacMan en fonction de {@code Direction}. <br><br>
     * Si cette position est valable, change la {@code Direction} de PacMan
     * sinon, garde l'ancienne {@code Direction}.
     * @param dir Calcul par rapport à cette direction.
     * @param deltaTns  Temps écoulée.
     * @param config {@link MazeConfig#config}
     * @return Retourne vraie si un changement de direction est possible,
     * faux sinon.
     */
    public boolean canPacManTurn(
        final Direction dir, final long deltaTns, final MazeConfig config) {
        if (this.direction == dir || atSpawnEntry()) {
            return false;
        }
        var nextNeighbours = nextPos(deltaTns).intNeighbours();
        final int maxNeighbours = 4;
        if (nextNeighbours.size() >= maxNeighbours) {
            return false;
        }
        if (nextNeighbours.size() == 2) {
            if ((dir == Direction.NORTH || dir == Direction.SOUTH)
            && (int) pos.x() != pos.x()) {
                return false;
            }
            if ((dir == Direction.EAST || dir == Direction.WEST)
            && (int) pos.y() != pos.y()) {
                return false;
            }
            var curNeighbours = pos.intNeighbours();
            for (var n : curNeighbours) {
                if (dir.wall(config.getCell(n))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Méthode qui arrête la course de PacMan et retire sa direction
     * si il rencontre un mur de face.
     * @param curPos  {@code RealCoordinates} actuelle de {@code Pac-Man}.
     * @param nextPos {@code RealCoordinates} supposée
     * @param config {@link MazeConfig#config}
     * (= s'il ne traverse pas de mur) de {@code Pac-Man}.
     * @return Retourne {@code nextPos} modifié ou non.
     */
    public RealCoordinates calibratePacManPos(
        final RealCoordinates curPos,
        final RealCoordinates nextPos,
        final MazeConfig config) {
        if (!curPos.intNeighbours().containsAll(nextPos.intNeighbours())) {
            for (var n : curPos.intNeighbours()) {
                if (direction.wall(config.getCell(n))) {
                    RealCoordinates newPos = pos.adjustCoordinates(direction);
                    direction = Direction.NONE;
                    return newPos;
                }
            }
        }
        return nextPos;
    }
}
