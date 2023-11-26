package model;

import geometry.IntCoordinates;
import geometry.RealCoordinates;
import gui.GraphicsBuffer;
import model.MazeState.GameState;

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

    /** Vitesse du fantôme lorsqu'il est dans un tunnel. */
    private static final int TUNNEL_SPEED = 3;
    /** Vitesse du fantôme lorsqu'il à été mangé par {@link PacMan}. */
    private static final int EATEN_SPEED = 20;

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

    /** Etat que peut prendre un {@link Ghost} au cours d'une partie. (NORMAL, FRIGTHENED, EATEN) */
    public enum GhostState {
        /** Etat de base/normal. */
        NORMAL,
        /** Etat apeuré, il prend des directions aléatoires et change de couleur.*/
        FRIGHTENED,
        /** Etat mangé, il se rend vers le spawn. */
        EATEN
    }

    // region Variables

    /** Type du fantôme actuel. */
    private final GhostType ghostType;
    /** Etat du fantôme actuel. */
    private GhostState ghostState = GhostState.NORMAL;

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

    private boolean activated = false;

    /** Buffer utilisé pour le mode versus en multijoueur. */
    private DirectionBuffer directionBuffer = null;
    private GraphicsBuffer graphicsBuffer = new GraphicsBuffer();

    // endregion Variables

    // region Constructeurs

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

    // endregion Constructeurs

    /**
     * Fonction générale qui actualise tous les paramètres d'un {@link Ghost}.
     * @param state {@link MazeState}
     * @param deltaTns Tick de temps.
     */
    public void updateGhost(final MazeState state, final long deltaTns) {
        boolean hasDirectionReversed = updateState(state);
        updateTarget(state);
        if (!hasDirectionReversed) {
            updateDirection(state);
        }
        updatePosition(deltaTns, state.getConfig());
        updateSpeed(state);
    }

    /**
     * Méthode qui met à jour le fantôme joueur.
     * @param state {@link MazeState}
     * @param deltaTns Tick de temps.
     */
    public void updatePlayerGhost(final MazeState state, final long deltaTns) {
        if (isInSpawn(state.getConfig())) {
            ghostState = GhostState.NORMAL;
        }
        if (directionBuffer.isBufferDirectionValid(this, deltaTns, state.getConfig())) {
            setDirection(directionBuffer.getBufferDirection());
        }
        updatePosition(deltaTns, state.getConfig());
        updateSpeed(state);
        directionBuffer.updateDirectionBufferTime(deltaTns);
    }

    //region State

    /**
     * Actualise les paramètres du {@link Ghost} en fonction de l'état du {@link MazeState}.
     * La fonction renvoie true si elle a déjà changé la direction du {@link Ghost}.
     * @param state
     * @return {@code boolean}
     */
    public boolean updateState(final MazeState state) {
        if (getPos().equals(eatenTarget) && activated) {
            ghostState = GhostState.NORMAL;
        }
        // Vérifie si les conditions d'activation des fantômes INKY et CLYDE sont remplies.
        if (!activated) {
            switch (ghostType) {
                case INKY:
                    final int inkyThreshold = state.getConfig().getTotalPacGomme() / 10;
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
        // Quand l'état actuel du jeu change, tous les fantômes doivent faire un 180°.
        GameState gameState = state.getGameState();
        if (gameState != GameState.FRIGHTENED && gameState != state.getCurrentState()) {
            state.setGameState(state.getCurrentState());
            setDirection(getDirection().getOpposite());
            return true;
        }
        return false;
    }

    //endregion State

    //region Target

    /**
     * Actualise la cible du {@link Ghost}.
     * @param state
     */
    public void updateTarget(final MazeState state) {
        if (ghostState == GhostState.EATEN) {
            RealCoordinates spawnEntrance = state.getConfig().getGhostSpawnEntrance();

            // Si mangé, alors fixe sa cible à l'entrée du spawn des fantômes.
            if (!target.equals(spawnEntrance) && !isInSpawn(state.getConfig())) {
                target = spawnEntrance;

            // Si il se trouve à l'entrée du spawn, alors fixe sa cible en eatenTarget.
            } else if (!target.equals(eatenTarget) && isInSpawn(state.getConfig())) {
                target = eatenTarget;
            }

        // Si l'état actuel du jeu est "SCATTER", alors fixe sa cible en scatterTarget.
        } else if (state.getCurrentState() == GameState.SCATTER && !target.equals(scatterTarget)) {
            target = scatterTarget;

        // Si l'état actuel du jeu est "CHASE", alors fixe sa cible en chaseTarget.
        } else if (state.getCurrentState() == GameState.CHASE) {
            target = calcChaseTarget();
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
        RealCoordinates targetCell = pacman.getLastDirection().directionToUnit().times(distance);
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
        plus(pacman.getLastDirection().directionToUnit().times(distance));
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

    //endregion Target

    //region Direction
    /**
     * Actualise la direction du {@link Ghost}.
     * @param state
     */
    public void updateDirection(final MazeState state) {
        MazeConfig config = state.getConfig();

        // Si le fantôme est dans l'état normal et se trouve dans le spawn,
        // alors le fantôme doit sortir du spawn en prenant un chemin prédéfini.
        if (ghostState != GhostState.EATEN && isInSpawn(config)) {
            setDirection(getDirectionToExitSpawn(config));
            return;
        }

        // Si le fantôme est activé et apeuré et qu'il n'a pas encore été mangé,
        // alors le fantôme doit prendre des directions aléatoires.
        if (activated && ghostState == GhostState.FRIGHTENED) {
            setDirection(getRandomDirection(config));
            return;
        }

        // Si le fantôme est dans l'état mangé et se trouve à l'entrée du spawn ou est dans le spawn
        // alors le fantôme doit aller vers sa cible en prenant un chemin prédéfini.
        if (ghostState == GhostState.EATEN && getPos().equals(config.getGhostSpawnEntrance())
                || isInSpawn(config)) {
            setDirection(getDirectionToEatenTarget());
            return;
        }

        // Sinon, prend la direction du chemin le plus court vers sa cible.
        setDirection(getDirectionClosestToTarget(config));
    }

    /**
     * La méthode retourne une liste contenant les directions possibles qu'un
     * fantôme peut emprunter à une intersection.
     * @param config
     * @return Une {@code List<Direction>} contenant les directions possibles.
     */
    public List<Direction> possibleDirections(final MazeConfig config) {
        if (isInTunnel(config)) {
            return Arrays.asList(getDirection());
        }
        List<Direction> directions = new ArrayList<>(Arrays.asList(NORTH, WEST, SOUTH, EAST));
        directions.remove(getDirection().getOpposite());
        RealCoordinates curPos = getPos();
        // Cas où le fantôme se trouve sur les cellules dites "murs invisibles".
        if (config.getCell(curPos.toIntCoordinates()).isRestricted()) {
            directions.remove(NORTH);
        }
        if (curPos.x() != Math.floor(curPos.x())) {
            directions.remove(NORTH);
            directions.remove(SOUTH);
        } else if (curPos.y() != Math.floor(curPos.y())) {
            directions.remove(EAST);
            directions.remove(WEST);
        } else {
            // Cas où le fantôme est au milieu d'une case, alors retire toutes les cases du spawn
            // et celles qui ont un mur.
            List<Direction> possibleDirections = new ArrayList<>();
            for (Direction dir : directions) {
                IntCoordinates adjCoords = curPos.plus(dir.directionToUnit()).toIntCoordinates();
                Cell cell = config.getCell(adjCoords);
                if (!cell.hasWall() && cell.getContent() != Cell.Content.SPAWN) {
                    possibleDirections.add(dir);
                }
            }
            directions = possibleDirections;
        }
        return directions;
    }

    /**
     * La méthode renvoie la {@code Direction} la plus proche de sa cible en
     * utilisant la méthode {@code possibleDirections()}.
     * @param config
     * @return La {@code Direction} la plus proche du target.
     */
    public Direction getDirectionClosestToTarget(final MazeConfig config) {
        // Directions possibles qu'il peut prendre (la direction derrière lui n'est pas incluse).
        List<Direction> possibleDir = possibleDirections(config);

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
        double min = getDistance(getPos().plus(possibleDir.get(0).directionToUnit()), target);
        int indice = 0;
        for (int i = 1; i < possibleDir.size(); i++) {
            final RealCoordinates possiblePos = possibleDir.get(i).directionToUnit();
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
     * @param config {@link MazeConfig}
     * @return {@link Direction}
     */
    public Direction getRandomDirection(final MazeConfig config) {
        List<Direction> possibles = possibleDirections(config);
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
     * Méthode qui renvoie la {@link Direction} que doit prendre le {@link Ghost}
     * pour retourner sur sa cellule de "reset/eatenTarget".
     * @return {@link Direction}
     */
    public Direction getDirectionToEatenTarget() {
        RealCoordinates pos = getPos();
        if (pos.y() == eatenTarget.y()) {
            if (pos.x() < eatenTarget.x()) {
                return EAST;
            }
            return WEST;
        }
        return SOUTH;
    }

    /**
     * Méthode qui indique la trajectoire à prendre pour un fantôme pour sortir du spawn.
     * @param config
     * @return {@code boolean}
     */
    public Direction getDirectionToExitSpawn(final MazeConfig config) {
        RealCoordinates entrance = config.getGhostSpawnEntrance();
        RealCoordinates pos = getPos();
        if (pos.x() == entrance.x()) {
            return NORTH;
        } else if (pos.x() < entrance.x()) {
            return EAST;
        }
        return WEST;
    }

    //endregion Direction

    //region Position

    /**
     * Méthode qui actualise la position d'un fantôme.
     * @param deltaTns
     * @param config
     */
    public void updatePosition(final long deltaTns, final MazeConfig config) {
        if (activated) {
            RealCoordinates pos = getPos();
            RealCoordinates nextPos = nextPos(deltaTns);
            final int width = config.getWidth();
            final int height = config.getHeight();
            if (pos.equals(nextPos)) {
                return;
            } else if (pos.haveSameNeighbours(nextPos)) {
                nextPos = pos.centeredPos(nextPos, true, config).warp(width, height);
            } else {
                nextPos = furthestFreeCell(nextPos, config).warp(width, height);
            }
            setPos(nextPos.warp(width, height));
        }
    }

    /**
     * Méthode qui est appelée lorsqu'un fantôme a une très grande vitesse.
     * On doit alors vérifier qu'entre sa position actuelle et sa prochaine position qu'il n'y
     * ait pas de murs entre ces 2 positions.
     * @param end
     * @param config
     * @return {@link RealCoordinates}
     */
    public RealCoordinates furthestFreeCell(final RealCoordinates end, final MazeConfig config) {
        RealCoordinates pos = getPos();
        final Direction dir = pos.getDirectionTo(end);
        RealCoordinates tmpPos = pos;
        while (dir == tmpPos.getDirectionTo(end)) {
            // Cas où on trouve un mur
            if (config.getCell(tmpPos.plus(dir.directionToUnit()).toIntCoordinates()).hasWall()) {
                return pos.roundFromDirection(getDirection());
            } else {
                tmpPos = tmpPos.plus(dir.directionToUnit());
            }
        }
        // Cas où aucun mur n'a été trouvé, renvoie nextPos
        return pos.centeredPos(end, true, config);
    }

    //endregion Position

    /**
     * Actualise la vitesse du {@link Ghost}.
     * @param state
     */
    public void updateSpeed(final MazeState state) {
        MazeConfig config = state.getConfig();
        if (getSpeed() != TUNNEL_SPEED && isInTunnel(config)) {
            setSpeed(TUNNEL_SPEED);
        } else if (getSpeed() != EATEN_SPEED && ghostState == GhostState.EATEN) {
            setSpeed(EATEN_SPEED);
        } else if (getSpeed() != getNormalSpeed()) {
            setSpeed(getNormalSpeed());
        }
    }

    /**
     * La méthode renvoie {@code true} si le fantôme est dans un tunnel.
     * @param config {@link MazeConfig}
     * @return {@code true} si dans un tunnel
     */
    public boolean isInTunnel(final MazeConfig config) {
        return config.getCell(getPos().toIntCoordinates()).getContent() == Content.TUNNEL;
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
            if (config.getCell(cell).getContent() == Content.SPAWN) {
                return true;
            }
        }
        return false;
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

    public void setGhostState(final GhostState ghostState) {
        this.ghostState = ghostState;
    }

    public GhostState getGhostState() {
        return ghostState;
    }
}
