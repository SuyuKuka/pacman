package model;

import geometry.IntCoordinates;
import geometry.RealCoordinates;
import gui.GraphicsBuffer;
import model.MazeState.GhostState;

import static model.Direction.NORTH;
import static model.Direction.EAST;
import static model.Direction.SOUTH;
import static model.Direction.WEST;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import config.Cell;
import config.MazeConfig;
import config.Cell.Content;

public final class Ghost extends Critter {
    /** Vitesse du fantôme loesqu'il est dans un Tunnel. */
    private static final int TUNNEL_SPEED = 3;
    /** Vitesse du fantôme lorsqu'il à été mangé par {@link PacMan}. */
    private static final int EATEN_SPEED = 12;

    /** Types de fantômes disponibles. (BLINKY, PINKY, INKY et CLYDE).
     * Chaque fantôme a un comportement différent lors du jeu.
    */
    public enum GhostType {
        /** {@link Ghost} rouge. */
        BLINKY,
        /** {@link Ghost} rose. */
        PINKY,
        /** {@link Ghost} cyan. */
        INKY,
        /** {@link Ghost} jaune. */
        CLYDE
    }

    /** Type du fantôme actuel. */
    private final GhostType ghostType;
    /** Coordonnées cible du {@link Ghost}. */
    private RealCoordinates target;
    /** {@link PacMan} attaché au {@link Ghost}. */
    private PacMan pacman;
    private PacMan pacman2;
    private final RealCoordinates eatenTarget;
    private final RealCoordinates scatterTarget;

    /** Variable nécessaire seulement pour les {@link Ghost} de type {@link GhostType INKY}.
     * Utile notamment pour le calcul de sa cible en {@link GhostState#CHASE}.
    */
    private final Ghost inkyLinkedGhost;

    private boolean eaten = false;
    private boolean activated = false;

    /** Buffer utilisé pour le mode versus en multijoueur. */
    private DirectionBuffer directionBuffer = null;
    private GraphicsBuffer graphicsBuffer = new GraphicsBuffer();

    /**
     * Constructeur spécial pour créer un {@link GhostType#INKY}.
     * Inky à besoin d'un fantôme lié pour calculer sa cible en mode CHASE.
     * @param ghostType
     * @param inkyLinkedGhost
     * @param pacman
     * @param pacman2
     * @param spawnPos
     * @param eatenTarget
     * @param scatterTarget
     */
    public Ghost(final GhostType ghostType, final Ghost inkyLinkedGhost, final PacMan pacman,
    final PacMan pacman2, final RealCoordinates spawnPos, final RealCoordinates eatenTarget,
    final RealCoordinates scatterTarget) {
        this.ghostType = ghostType;
        this.inkyLinkedGhost = inkyLinkedGhost;
        this.pacman = pacman;
        this.pacman2 = pacman2;
        this.setSpawnPos(spawnPos);
        this.setPos(spawnPos);
        this.eatenTarget = eatenTarget;
        this.scatterTarget = scatterTarget;
        this.target = scatterTarget;
    }

    /**
     * Constructeur pour créer un {@link Ghost}.
     * @param ghostType
     * @param pacman
     * @param pacman2
     * @param spawnPos
     * @param eatenTarget
     * @param scatterTarget
     */
    public Ghost(final GhostType ghostType, final PacMan pacman, final PacMan pacman2,
    final RealCoordinates spawnPos, final RealCoordinates eatenTarget,
    final RealCoordinates scatterTarget) {
        this(ghostType, null, pacman, pacman2, spawnPos, eatenTarget, scatterTarget);
    }

    /**
     * Fonction générale qui actualise tous les paramètres d'un {@link Ghost}.
     * @param state {@link MazeState}
     * @param deltaTns Tick de temps.
     */
    public void update(final MazeState state, final long deltaTns) {
        if (isPlayer()) {
            updatePlayerGhost(state, deltaTns);
        } else {
            boolean hasDirectionReversed = updateState(state);
            updateTarget(state);
            if (!hasDirectionReversed) {
                updateDirection(state);
            }
            updatePosition(state, deltaTns);
        }
    }

    /**
     * Méthode qui met à jour le fantôme joueur.
     * @param state {@link MazeState}
     * @param deltaTns Tick de temps.
     */
    public void updatePlayerGhost(final MazeState state, final long deltaTns) {
        if (directionBuffer.isBufferDirectionValid(this, deltaTns, state.getConfig())) {
            setDirection(directionBuffer.getBufferDirection());
        }
        boolean precision = isInSpawn(state.getConfig());
        updateSpeed(state);
        updatePosition(deltaTns, state.getConfig(), precision);
        directionBuffer.updateDirectionBufferTime(deltaTns);
        updateState(state);
    }

    /**
     * Actualise les paramètres du {@link Ghost} en fonction de l'état du {@link MazeState}.
     * La fonction renvoie true si elle a déjà changé la direction des {@link Ghost}.
     * @param state
     * @return {@code boolean}
     */
    public boolean updateState(final MazeState state) {
        // Retire l'état mangé et sa vitesse si il est arrivé a destination.
        if (eaten && getPos().equals(eatenTarget)) {
            setSpeed(getNormalSpeed());
            eaten = false;
        }

        if (isPlayer()) {
            return false;
        }

        if (pacman.isEnergized() && state.getGhostState() != GhostState.FRIGHTENED) {
            setDirection(getDirection().getOpposite());
            return true;
        }
        if (!activated) {
            switch (ghostType) {
                case INKY:
                    final int inkyThreshold = state.getConfig().getTotalPacGomme() / 15;
                    if (state.getEatenPacgomme() >= inkyThreshold) {
                        activated = true;
                    }
                    return false;
                case CLYDE:
                    final int clydeThreshold = state.getConfig().getTotalPacGomme() / 3;
                    if (state.getEatenPacgomme() >= clydeThreshold) {
                        activated = true;
                    }
                    return false;
                default:
                    break;
            }
        }
        GhostState ghostState = state.getGhostState();
        if (ghostState != GhostState.FRIGHTENED && ghostState != state.getCurrentState()) {
            state.setGhostState(state.getCurrentState());
            setDirection(getDirection().getOpposite());
            return true;
        }
        return false;
    }

    /**
     * Actualise la cible du {@link Ghost}.
     * @param state
     */
    public void updateTarget(final MazeState state) {
        if (eaten) {
            if (!target.equals(eatenTarget)) {
                target = eatenTarget;
            }
        } else if (state.getCurrentState() == GhostState.SCATTER && !target.equals(scatterTarget)) {
            target = scatterTarget;
        } else if (state.getCurrentState() == GhostState.CHASE) {
            target = calcChaseTarget();
        }
    }

    /**
     * Actualise la direction du {@link Ghost}.
     * @param state
     */
    public void updateDirection(final MazeState state) {
        if (state.getCurrentState() == GhostState.FRIGHTENED && activated && !eaten) {
            setDirection(getRandomDirection(state));
        } else if (isInSpawn(state.getConfig()) && !eaten) {
            setDirection(exitSpawnDirection(state.getConfig()));
        } else {
            setDirection(directionClosestToTarget(state));
        }
    }

    /**
     * Actualise la position du {@link Ghost}.
     * @param state
     * @param deltaTns
     */
    public void updatePosition(final MazeState state, final long deltaTns) {
        updateSpeed(state);
        if (activated) {
            boolean precision = isInSpawn(state.getConfig());
            setCalibratedPos(getPos(), nextPos(deltaTns), state.getConfig(), precision);
        }
    }

    /**
     * Actualise la vitesse du {@link Ghost}.
     * @param state
     */
    public void updateSpeed(final MazeState state) {
        if (isInTunnel(state.getConfig())) {
            setSpeed(TUNNEL_SPEED);
        } else if (!isInTunnel(state.getConfig())) {
            setSpeed(getNormalSpeed());
        }
        if (eaten) {
            setSpeed(EATEN_SPEED);
        }
    }

    /**
     * Méthode qui renvoie la cible du {@link Ghost} lorsqu'il est en {@link GhostType#CHASE}.
     * <br><br> Chaque fantôme calcule sa cible différement.<br><br>
     * @return cible {@link RealCoordinates}
     */
    public RealCoordinates calcChaseTarget() {
        switch (ghostType) {
            case BLINKY:
                return calcBlinkyChaseTarget();
            case PINKY:
                return calcPinkyChaseTarget();
            case INKY:
                return calcInkyChaseTarget();
            case CLYDE:
                return calcClydeChaseTarget();
            default:
                break;
        }
        // sécurité
        return pacman.getPos();
    }

    /**
     * La cible de Blinky est toujours la position actuelle de PacMan.
     * @return cible {@link RealCoordinates}
     */
    public RealCoordinates calcBlinkyChaseTarget() {
        return pacman.getPos();
    }

    /**
     * La cible de Pinky est toujours 4 cases devant PacMan. <br><br>
     * Exception pour la direction Nord où la cible est aussi décalé de 4 cases à l'Est.
     * @return cible {@link RealCoordinates}
     */
    public RealCoordinates calcPinkyChaseTarget() {
        final int distance = 4;
        RealCoordinates targetCell = RealCoordinates.
            directionToUnit(pacman.getLastDirection()).times(distance);
        if (pacman.getLastDirection() == NORTH) {
            targetCell = targetCell.plus(new RealCoordinates(-distance, 0));
        }
        return pacman.getPos().plus(targetCell);
    }

    /**
     * La cible de Inky est toujours la symétrie centrale entre la position de Blinky et
     * la 2eme case devant PacMan. <br> <br>
     * Exception pour le Nord où la case devant PacMan est aussi décalé de 2 cases à l'Est.
     * @return cible {@link RealCoordinates}
     */
    public RealCoordinates calcInkyChaseTarget() {
        final int distance = 2;
        RealCoordinates blinkyPos = inkyLinkedGhost.getPos();
        RealCoordinates pacTPos = pacman.getPos().
        plus(RealCoordinates.directionToUnit(pacman.getLastDirection()).times(distance));
        if (pacman.getLastDirection() == NORTH) {
            pacTPos = pacTPos.plus(new RealCoordinates(-distance, 0));
        }
        RealCoordinates vecteur =
            new RealCoordinates(pacTPos.x() - blinkyPos.x(), pacTPos.y() - blinkyPos.y()).times(2);
        return blinkyPos.plus(vecteur);
    }

    /**
     * La cible de Clyde est la position de PacMan mais lorsqu'il est à 8 cases de PacMan,
     * sa cible devient celle de son mode {@GhostType#SCATTER}.
     * @return cible {@link RealCoordinates}
     */
    public RealCoordinates calcClydeChaseTarget() {
        final int radiusRange = 8;
        if (getDistance(getPos(), pacman.getPos()) < radiusRange) {
            return scatterTarget;
        }
        return pacman.getPos();
    }

    /**
     * La méthode renvoie {@code true} si le fantôme est dans un tunnel.
     * @param config {@link MazeConfig}
     * @return {@code true} si dans un tunnel
     */
    public boolean isInTunnel(final MazeConfig config) {
        return config.getCell(getPos().toIntCoordinates()).initialContent() == Content.TUNNEL;
    }

    /**
     * La méthode retourne une liste contenant les directions possibles qu'un
     * fantôme peut emprunter à une intersection.
     * @param state {@link MazeState}
     * @return Une {@code List<Direction>} contenant les directions possibles.
     */
    public List<Direction> possibleDirections(final MazeState state) {
        if (isInTunnel(state.getConfig())) {
            return Arrays.asList(getDirection());
        }
        List<Direction> directions = new ArrayList<>(Arrays.asList(NORTH, WEST, SOUTH, EAST));
        directions.remove(getDirection().getOpposite());
        if (getPos().x() != Math.floor(getPos().x())) {
            directions.remove(NORTH);
            directions.remove(SOUTH);
        }
        if (getPos().y() != Math.floor(getPos().y())) {
            directions.remove(EAST);
            directions.remove(WEST);
        }
        // Cas où le fantôme se trouve sur les cellules dites "murs invisibles".
        if (state.getConfig().getCell(getPos().toIntCoordinates()).restricted()) {
            directions.remove(NORTH);
        }

        if (directions.size() == 1) {
            return directions;
        }

        List<Direction> possibleDirections = new ArrayList<>();
        for (Direction direction : directions) {
            RealCoordinates pos = RealCoordinates.directionToUnit(direction).plus(this.getPos());
            if (pos.x() >= 0 && pos.x() < state.getConfig().getGrid()[0].length) {
                final int posY = (int) Math.round(pos.y());
                final int posX = (int) Math.round(pos.x());
                Cell targetCell = state.getConfig().getGrid()[posY][posX];
                if (targetCell.initialContent() == Content.SPAWN && activated && !eaten) {
                    continue;
                }
                if (!targetCell.hasWall()) {
                    possibleDirections.add(direction);
                }
            }
        }
        return possibleDirections;
    }

    /**
     * La méthode renvoie la {@code Direction} la plus proche de sa cible en
     * utilisant la méthode {@code possibleDirections()}.
     * @param state {@link MazeState}
     * @return La {@code Direction} la plus proche du target.
     */
    public Direction directionClosestToTarget(final MazeState state) {
        // Directions possibles qu'il peut prendre (la direction derrière lui n'est pas incluse).
        List<Direction> possibleDir = possibleDirections(state);

        // Si le fantôme se trouve dans un cul-de-sac, il se retourne (toujours possible)
        if (possibleDir.isEmpty()) {
            return getDirection().getOpposite();
        }
        // Si une seule direction n'est possible, renvoie cette direction.
        if (possibleDir.size() == 1) {
            return possibleDir.get(0);
        }

        // Prend la direction où la distance est la plus petite.
        double[] deltaDist = new double[possibleDir.size()];
        double min = getDistance(getPos().plus(
            RealCoordinates.directionToUnit(possibleDir.get(0))), target);
        int indice = 0;
        for (int i = 1; i < possibleDir.size(); i++) {
            final RealCoordinates possiblePos = RealCoordinates.directionToUnit(possibleDir.get(i));
            deltaDist[i] = getDistance(getPos().plus(possiblePos), target);
            if (deltaDist[i] < min) {
                min = deltaDist[i];
                indice = i;
            }
        }
        return possibleDir.get(indice);
    }

    /**
     * La méthode renvoie une direction "aléatoire" basée sur la direction du
     * fantôme et des probabilités de chaque direction.
     * Les probabilités sont basées sur le jeu original : 16.3% Nord, 25.2% Est,
     * 28.5% Sud, 30.0% Ouest.
     * @param state {@link MazeState}
     * @return {@link Direction}
     */
    public Direction getRandomDirection(final MazeState state) {
        List<Direction> possibles = possibleDirections(state);
        if (possibles.size() == 1) {
            return possibles.get(0);
        }
        //CHECKSTYLE:OFF: checkstyle:magicnumber
        List<Double> dirProba = new ArrayList<>(Arrays.asList(16.3, 25.2, 28.5, 30.0));
        List<Direction> dirList = new ArrayList<>(Arrays.asList(NORTH, EAST, SOUTH, WEST));

        for (int i = 0; i < dirList.size(); i++) {
            if (!possibles.contains(dirList.get(i))) {
                dirProba.set(
                    (i+1)%dirList.size(),
                    dirProba.get(i) + dirProba.get((i+1) % dirList.size())
                );
                dirList.remove(i);
                dirProba.remove(i);
                i--;
            }
        }
        double random = Math.random() * 100; // 0 à 100
        int j = 0;
        for (int i = 0; i < dirProba.size(); i++) {
            random -= dirProba.get(i);
            if (random <= 0) {
                break;
            }
            j++;
        }
        //CHECKSTYLE:ON: checkstyle:magicnumber
        return dirList.get(j);
    }

    /**
     * Méthode qui renvoie vraie si le {@link RealCoordinates}
     * donné se trouve dans le spawn des {@link Ghost}.
     * @param config {@link MazeConfig}
     * @return true ou false
     */
    public boolean isInSpawn(final MazeConfig config) {
        Set<IntCoordinates> neighbours = getPos().intNeighbours();
        for (var cell : neighbours) {
            if (config.getCell(cell).initialContent() == Content.SPAWN) {
                return true;
            }
        }
        return false;
    }

    /**
     * Méthode qui indique la trajectoire à prendre pour un fantôme pour sortir du spawn.
     * @param config
     * @return {@code boolean}
     */
    public Direction exitSpawnDirection(final MazeConfig config) {
        RealCoordinates pos1 = config.getGhostSpawnPos()[0];
        RealCoordinates pos2 = config.getGhostSpawnPos()[1];
        double spawnXCenter = (pos1.x() + pos2.x()) / 2;
        if (getPos().x() < spawnXCenter) {
            return EAST;
        } else if (getPos().x() > spawnXCenter) {
            return WEST;
        }
        return NORTH;
    }

    /**
     * Renvoie la distance entre 2 {@link RealCoordinates}.
     * @param pos1 {@link RealCoordinates}
     * @param pos2 {@link RealCoordinates}
     * @return {@code int}
     */
    public static int getDistance(final RealCoordinates pos1, final RealCoordinates pos2) {
        return Math.abs((int)
        (Math.sqrt(Math.pow(pos2.x() - pos1.x(), 2) + Math.pow(pos2.y() - pos1.y(), 2))));
    }

    public GhostType getGhostType() {
        return ghostType;
    }

    public RealCoordinates getTarget() {
        return target;
    }

    public void setTarget(final RealCoordinates target) {
        this.target = target;
    }

    public PacMan getPacman() {
        return pacman;
    }

    public PacMan getPacman2() {
        return pacman2;
    }

    public void setPacman(final PacMan pacman) {
        this.pacman = pacman;
    }

    public RealCoordinates getEatenTarget() {
        return eatenTarget;
    }

    public RealCoordinates getScatterTarget() {
        return scatterTarget;
    }

    public boolean isEaten() {
        return eaten;
    }

    public void setEaten(final boolean eaten) {
        this.eaten = eaten;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(final boolean activated) {
        this.activated = activated;
    }

    public DirectionBuffer getDirectionBuffer() {
        return directionBuffer;
    }

    public void setDirectionBuffer(final DirectionBuffer directionBuffer) {
        this.directionBuffer = directionBuffer;
    }

    public boolean isPlayer() {
        return directionBuffer != null;
    }

    public GraphicsBuffer getGraphicsBuffer() {
        return graphicsBuffer;
    }
}
