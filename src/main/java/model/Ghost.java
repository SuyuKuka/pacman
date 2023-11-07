package model;

import geometry.IntCoordinates;
import geometry.RealCoordinates;
import model.MazeState.Mode;

import static model.Direction.NONE;
import static model.Direction.NORTH;
import static model.Direction.EAST;
import static model.Direction.SOUTH;
import static model.Direction.WEST;

import java.util.ArrayList;
import java.util.List;

import config.Cell;
import config.MazeConfig;
import config.Cell.Content;

public enum Ghost implements Critter {
    /** Différents fantômes. */
    BLINKY, INKY, PINKY, CLYDE;

    /** Position du fantôme. */
    private RealCoordinates pos;
    /** Direction du fantôme. */
    private Direction direction = Direction.NONE;
    /** Target du fantôme. */
    private RealCoordinates target;
    /** Etat activé. */
    private boolean activated;
    /** Etat mangé. */
    private boolean eaten;

    @Override
    public RealCoordinates getPos() {
        return pos;
    }

    @Override
    public void setPos(final RealCoordinates newPos) {
        pos = newPos;
    }

    @Override
    public void setDirection(final Direction dir) {
        direction = dir;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public double getSpeed() {
        final int tunnelSpeed = 3;
        final int normalSpeed = 4;
        return normalSpeed;
    }

    /**
     * Retourne le target du fantôme.
     * @return {@code RealCoordinates}
     */
    public RealCoordinates getTarget() {
        return target;
    }

    /**
     * Retourne true si le fantôme est activé.
     * @return {@code boolean}
     */
    public boolean isActivated() {
        return activated;
    }

    /**
     * Retourne true si le fantôme a été mangé.
     * @return {@code boolean}
     */
    public boolean isEaten() {
        return eaten;
    }

    /**
     * Set l'état {@link #eaten}.
     * @param isEaten {@code boolean}
     */
    public void setEaten(final boolean isEaten) {
        eaten = isEaten;
    }

    /**
     * Set le target du fantôme lorsqu'il a été mangé.
     */
    private void setEatenTarget() {
        final int targetX = 13;
        final int targetY = 17;
        target = new IntCoordinates(
            targetX, targetY).toRealCoordinates(1.0
        );
    }

    /**
     * La méthode renvoie {@code true} si le fantôme est dans un tunnel de
     * téléportation horizontal de 3 de longueur à la position y = 10.
     * @return {@code true} si dans un tunnel
     */
    public boolean isInTunnel(final MazeConfig config) {
        return config.getCell(pos.round()).initialContent() == Content.TUNNEL;
    }

    /**
     * La méthode met à jour le target de {@code BLINKY} en {@code CHASE} mode.
     */
    public static void updateBlinky() {
        // Lorsque le fantôme a été mangé
        if (BLINKY.eaten) {
            BLINKY.setEatenTarget();
        // Cas général
        } else {
            BLINKY.target = PacMan.INSTANCE.getPos();
        }
    }

    /**
     * La méthode met à jour le target de {@code PINKY} en {@code CHASE} mode.
     */
    public static void updatePinky() {
        // Lorsque le fantôme a été mangé
        if (PINKY.eaten) {
            PINKY.setEatenTarget();
            return;
        }

        double deltaX;
        double deltaY;
        // Pour éviter les situations lorsque Pac-Man est stationnaire
        Direction pacDir = PacMan.INSTANCE.getDirection() == NONE
        ? PacMan.INSTANCE.getLastDirection() : PacMan.INSTANCE.getDirection();

        switch (pacDir) {
            // Si Pac-Man regarde le Nord, le target
            // se trouve deux cases en haut et deux cases à gauche de lui
            case NORTH:
                deltaX = new IntCoordinates(2, 0).toRealCoordinates(1.0).x();
                deltaY = new IntCoordinates(0, 2).toRealCoordinates(1.0).y();
                PINKY.target = new RealCoordinates(
                    PacMan.INSTANCE.getPos().x() - deltaX,
                    PacMan.INSTANCE.getPos().y() - deltaY
                );
                break;
            // Pour les autres directions,
            // le target se trouvera 2 cases devant Pac-Man
            case EAST:
                deltaX = new IntCoordinates(2, 0).toRealCoordinates(1.0).x();
                PINKY.target = new RealCoordinates(
                    PacMan.INSTANCE.getPos().x() + deltaX,
                    PacMan.INSTANCE.getPos().y()
                );
                break;
            case SOUTH:
                deltaY = new IntCoordinates(0, 2).toRealCoordinates(1.0).y();
                PINKY.target = new RealCoordinates(
                    PacMan.INSTANCE.getPos().x(),
                    PacMan.INSTANCE.getPos().y() + deltaY
                );
                break;
            case WEST:
                deltaX = new IntCoordinates(2, 0).toRealCoordinates(1.0).x();
                PINKY.target = new RealCoordinates(
                    PacMan.INSTANCE.getPos().x() - deltaX,
                    PacMan.INSTANCE.getPos().y()
                );
                break;
            default:
                PINKY.target = PacMan.INSTANCE.getPos();
                break;
        }
    }

    /**
     * La méthode met à jour le target de {@code INKY} en {@code CHASE} mode.
     */
    public static void updateInky() {
        // Lorsque le fantôme a été mangé
        if (INKY.eaten) {
            INKY.setEatenTarget();
            return;
        }

        // Pour éviter les situations lorsque Pac-Man est stationnaire
        Direction pacDir = PacMan.INSTANCE.getDirection() == NONE
        ? PacMan.INSTANCE.getLastDirection() : PacMan.INSTANCE.getDirection();

        // Le target d'Inky se trouve à 2 fois la distances
        // entre la position de Blinky et une case devant Pac-Man
        switch (pacDir) {
            case NORTH:
                INKY.target = new RealCoordinates(
                    2 * PINKY.target.x() + 2 - BLINKY.getPos().x(),
                    2 * PINKY.target.y() + 2 - BLINKY.getPos().y()
                );
                break;
            case EAST:
                INKY.target = new RealCoordinates(
                    2 * PINKY.target.x() - 2 - BLINKY.getPos().x(),
                    2 * PINKY.target.y() - BLINKY.getPos().y()
                );
                break;
            case SOUTH:
                INKY.target = new RealCoordinates(
                    2 * PINKY.target.x() - BLINKY.getPos().x(),
                    2 * PINKY.target.y() - 2 - BLINKY.getPos().y()
                );
                break;
            case WEST:
                INKY.target = new RealCoordinates(
                    2 * PINKY.target.x() + 2 - BLINKY.getPos().x(),
                    2 * PINKY.target.y() - BLINKY.getPos().y()
                );
                break;
            default:
                INKY.target = PacMan.INSTANCE.getPos();
                break;
        }
    }

    /**
     * La méthode met à jour le target de {@code CLYDE} en {@code CHASE} mode.
     */
    public static void updateClyde() {
        // Lorsque le fantôme a été mangé
        if (CLYDE.eaten) {
            CLYDE.setEatenTarget();
            return;
        }

        // Dans le vrai jeu, si Clyde se trouve dans un rayon de 8 cases
        // de Pac-Man, ce dernier aura comme target le target en mode SCATTER
        // Sinon le target est identique à Blinky (la position de Pac-Man)

        // Ici, ce rayon de 8 cases sera remplacé par 4 car le fonctionnement
        // des cellules est différent (ici, les cellules sont plus grandes que
        // celles du jeu original)
        final int radiusRange = 8;
        if (Math.sqrt(
                Math.pow(CLYDE.getPos().x() - PacMan.INSTANCE.getPos().x(), 2)
                + Math.pow(CLYDE.getPos().y() - PacMan.INSTANCE.getPos().y(), 2)
            ) < radiusRange) {
            final int targetX = 0;
            final int targetY = 34;
            CLYDE.target = new IntCoordinates(
                targetX, targetY).toRealCoordinates(1.0
            );
        } else {
            CLYDE.target = PacMan.INSTANCE.getPos();
        }
    }

    /**
     * La méthode retourne une liste contenant les directions possibles qu'un
     * fantôme peut emprunter à une intersection.
     * @param config {@link MazeConfig#config} Configuration actuelle de la map
     * @return Une {@code List<Direction>} contenant les directions possibles.
     *         Triées dans le sens horaire.
     */
    public List<Direction> possibleDirections(final MazeConfig config) {
        List<Cell> adjacentCells = config.getAdjacentCells(getPos(), direction);
        List<Direction> possibleDirections = new ArrayList<>();
        switch (direction) {
            case NORTH -> {
                if (!adjacentCells.get(0).southWall()) {
                    possibleDirections.add(NORTH);
                }
                if (!adjacentCells.get(1).westWall()) {
                    possibleDirections.add(EAST);
                }
                if (!adjacentCells.get(2).eastWall()) {
                    possibleDirections.add(WEST);
                }
                break;
            }
            case EAST -> {
                if (!adjacentCells.get(0).southWall()) {
                    possibleDirections.add(NORTH);
                }
                if (!adjacentCells.get(1).westWall()) {
                    possibleDirections.add(EAST);
                }
                if (isInTunnel(config)) {
                    possibleDirections.add(EAST);
                }
                if (!adjacentCells.get(2).northWall()
                    && (!atSpawnEntry() || (eaten && atSpawnEntry()))
                    ) {
                    possibleDirections.add(SOUTH);
                }
                break;
            }
            case SOUTH -> {
                if (!adjacentCells.get(0).westWall()) {
                    possibleDirections.add(EAST);
                }
                if (!adjacentCells.get(1).northWall()) {
                    possibleDirections.add(SOUTH);
                }
                if (!adjacentCells.get(2).eastWall()) {
                    possibleDirections.add(WEST);
                }
                break;
            }
            case WEST -> {
                if (!adjacentCells.get(0).southWall()) {
                    possibleDirections.add(NORTH);
                }
                if (!adjacentCells.get(1).northWall()
                    && (!atSpawnEntry() || (eaten && atSpawnEntry()))
                    ) {
                    possibleDirections.add(SOUTH);
                }
                if (!adjacentCells.get(2).eastWall()) {
                    possibleDirections.add(WEST);
                }
                if (isInTunnel(config)) {
                    possibleDirections.add(WEST);
                }
                break;
            }
            default -> {
                break;
            }
        }

        // Si le fantôme se trouve au spot des murs invisibles
        final int[] invisX = {7, 9};
        final int[] invisY = {8, 16};
        if (pos.equals(
                new IntCoordinates(invisX[0], invisY[0]).toRealCoordinates(1.0))
            || pos.equals(
                new IntCoordinates(invisX[1], invisY[0]).toRealCoordinates(1.0))
            || pos.equals(
                new IntCoordinates(invisX[0], invisY[1]).toRealCoordinates(1.0))
            || pos.equals(
                new IntCoordinates(invisX[1], invisY[1]).toRealCoordinates(1.0))
            ) {
            possibleDirections.remove(NORTH);
        }

        return possibleDirections;
    }

    /**
     * La méthode retourne la {@code Direction} la plus proche du target en
     * utilisant la méthode {@code possibleDirections()}.<br>
     * </br>
     * Si la liste des directions possibles est nulle, la méthode retourne la
     * direction contraire à la direction actuelle.<br>
     * </br>
     * Si le fantôme est au centre du spawn, la méthode retourne {@code NORTH}.
     * @param config {@link MazeConfig#config} Configuration actuelle de la map
     * @return La {@code Direction} la plus proche du target.
     */
    public Direction directionClosestToTarget(final MazeConfig config) {
        List<Direction> possibleDirections = possibleDirections(config);

        // Si le fantôme se trouve dans un cul-de-sac, il se retourne
        if (possibleDirections.isEmpty()) {
            reverseDirection(config);
            return direction;
        }

        // Oblige le fantôme à sortir du home
        final int spawnX = 8;
        final int spawnY = 10;
        if (pos.equals(
                new IntCoordinates(spawnX, spawnY).toRealCoordinates(1.0))
            ) {
            return NORTH;
        }

        double[] deltaDist = {-1, -1, -1, -1};

        final int north = 0;
        final int west = 1;
        final int south = 2;
        final int east = 3;

        // Calcul des distances entre le fantôme et le target après déplacement
        for (Direction dir : possibleDirections) {
            switch (dir) {
                case NORTH:
                    deltaDist[north] = Math.sqrt(Math.pow(
                        target.x() - pos.x(), 2)
                        + Math.pow((target.y() - (pos.y() - 1)),
                        2));
                    break;
                case WEST:
                    deltaDist[west] = Math.sqrt(Math.pow(
                        target.x() - (pos.x() - 1), 2)
                        + Math.pow((target.y() - pos.y()),
                        2));
                    break;
                case SOUTH:
                    deltaDist[south] = Math.sqrt(Math.pow(
                        target.x() - pos.x(), 2)
                        + Math.pow((target.y() - (pos.y() + 1)),
                        2));
                    break;
                case EAST:
                    deltaDist[east] = Math.sqrt(Math.pow(
                        target.x() - (pos.x() + 1), 2)
                        + Math.pow((target.y() - pos.y()),
                        2));
                    break;
                default:
                    break;
            }
        }

        // Détermination de la plus petite distance
        final double big = 10000;
        double minVal = big;
        int min = -1;
        for (int i = 0; i < deltaDist.length; i++) {
            if (deltaDist[i] != -1 && deltaDist[i] < minVal) {
                min = i;
                minVal = deltaDist[i];
            }
        }
        switch (min) {
            case north:
                return NORTH;
            case west:
                return WEST;
            case south:
                return SOUTH;
            case east:
                return EAST;
            default:
                return NONE;
        }
    }

    /**
     * La méthode renvoie une direction "aléatoire" basée sur la direction du
     * fantôme et des probabilités de chaque direction.
     * Les probabilités sont basées sur le jeu original : 16.3% Nord, 25.2% Est,
     * 28.5% Sud, 29.9% Ouest.
     * @param config {@link MazeConfig#config} Configuration actuelle de la map
     */
    public void setRandomDirection(final MazeConfig config) {
        List<Direction> possibles = possibleDirections(config);
        final double rand = Math.random() * 100;
        final double probaNorth = 16.3;
        final double probaEast = probaNorth + 25.2;
        final double probaSouth = probaEast + 28.5;
        if (rand < probaNorth) {
            if (!possibles.isEmpty()) {
                direction = possibles.get(0);
            } else {
                reverseDirection(config);
            }
        }
        if (rand >= probaNorth && rand < probaEast) {
            if (possibles.contains(EAST)) {
                direction = EAST;
            } else if (possibles.contains(SOUTH)) {
                direction = SOUTH;
            } else if (possibles.contains(WEST)) {
                direction = WEST;
            } else if (possibles.contains(NORTH)) {
                direction = NORTH;
            } else {
                reverseDirection(config);
            }
        }
        if (rand >= probaEast && rand < probaSouth) {
            if (possibles.contains(SOUTH)) {
                direction = SOUTH;
            } else if (possibles.contains(WEST)) {
                direction = WEST;
            } else if (!possibles.isEmpty()) {
                direction = possibles.get(0);
            } else {
                reverseDirection(config);
            }
        }
        if (rand >= probaSouth) {
            if (possibles.contains(WEST)) {
                direction = WEST;
            } else if (!possibles.isEmpty()) {
                direction = possibles.get(0);
            } else {
                reverseDirection(config);
            }
        }
    }

    /**
     * Méthode qui appelle la méthode {@link #reverseDirection} pour chaque
     * critter.
     * @param config {@link MazeConfig#config} Configuration actuelle de la map
     * @param critters {@code List<Critter>} Liste des critters
     */
    public static void reverseGhostsDirection(
        final MazeConfig config, final List<Critter> critters
        ) {
        for (Critter critter : critters) {
            if (critter instanceof Ghost) {
                ((Ghost) critter).reverseDirection(config);
            }
        }
    }

    /**
     * Change la direction actuelle du fantôme en sa direction opposée.
     * @param config {@link MazeConfig#config} Configuration actuelle de la map
     */
    public void reverseDirection(final MazeConfig config) {
        if (eaten) {
            return;
        }
        switch (direction) {
            case NORTH:
                if (!config.getCell(
                    new IntCoordinates(
                        pos.round().x(), pos.round().y() + 1)
                    ).northWall()
                    ) {
                    direction = SOUTH;
                }
                break;
            case EAST:
                if (!config.getCell(
                    new IntCoordinates(
                        pos.round().x() - 1, pos.round().y())
                    ).eastWall()) {
                    direction = WEST;
                }
                break;
            case SOUTH:
                if (!config.getCell(
                    new IntCoordinates(
                        pos.round().x(), pos.round().y() - 1)
                    ).southWall()) {
                    direction = NORTH;
                }
                break;
            case WEST:
                if (!config.getCell(
                    new IntCoordinates(
                        pos.round().x() + 1, pos.round().y())
                    ).westWall()) {
                    direction = EAST;
                }
                break;
            default:
                break;
        }
    }

    /**
     * Réinitialise la position des fantômes après un reset.
     * @param state {@code MazeState} Etat actuel de la map
     */
    public void resetGhostPos(final MazeState state) {
        final int inkyThreshold = 30;
        final int clydeThreshold = state.getMaxScore() / 3;
        if (this == BLINKY
            || this == INKY && state.getPacgomme() >= inkyThreshold
            ) {
            direction = EAST;
        } else if (this == PINKY) {
            direction = NORTH;
        } else if (this == CLYDE && state.getPacgomme() >= clydeThreshold) {
            direction = WEST;
        } else {
            direction = NONE;
        }

        pos = (state.getInitialPos().get(this));
    }

    /**
     * La méthode initialise la variable {@code start} qui garde l'heure UNIX au
     * lancement de la partie (lorsque Pac-Man bouge pour la première fois),
     * initialise le premier mode en {@code SCATTER} et
     * initialise les directions de {@code BLINKY} et de {@code PINKY}.
     */
    public static void activateGhost() {
        BLINKY.activated = true;
        PINKY.activated = true;
        INKY.activated = false;
        CLYDE.activated = false;
        BLINKY.direction = EAST;
        PINKY.direction = NORTH;
    }

    /**
     * La méthode met à jour les targets de chaque fantôme
     * selon le mode courant.
     * Elle met aussi à jour la direction de chaque fantôme
     * lorsqu'il entre dans une intersection. {@code INKY} et {@code CLYDE} ne
     * peuvent bouger seulement lorsqu'un score a été atteint.
     * @param deltaTns La variation du temps entre deux instants.
     * @param state {@code MazeState} Etat actuel de la map
     */
    public static void updateGhost(final long deltaTns, final MazeState state) {
        List<Ghost> critters = List.of(CLYDE, BLINKY, INKY, PINKY);
        for (Ghost critter : critters) {
            critter.calibratePos(
                deltaTns, critter.getPos(),
                critter.nextPos(deltaTns),
                state.getHeight(),
                state.getWidth()
            );
            RealCoordinates pos = critter.getPos();

            // Lorsque le fantôme mangé retourne au spawn
            final int spawnX = 13;
            final int spawnY = 17;
            if (critter.eaten
                && pos.equals(
                    new IntCoordinates(spawnX, spawnY).toRealCoordinates(1.0)
                )) {
                critter.eaten = false;
            }

            final int inkyThreshold = 30;
            final int clydeThreshold = state.getMaxScore() / 3;
            // Activation d'Inky à partir de 30 points
            if (critter == INKY
                && !INKY.activated && state.getPacgomme() >= inkyThreshold
                ) {
                INKY.activated = true;
                critter.direction = EAST;
            // Activation de Clyde lorsqu'un tier des points
            // totaux a été obtenu
            } else if (critter == CLYDE
                && !CLYDE.activated && state.getPacgomme() >= clydeThreshold
                ) {
                CLYDE.activated = true;
                critter.direction = WEST;
            // Set direction aléatoire en FRIGHTENED mode
            } else if (!critter.eaten
                    && state.getMode() == Mode.FRIGHTENED
                    && pos.x() % 1 == 0 && pos.y() % 1 == 0
                ) {
                state.frightenedMode(critter);
            // Set direction la plus proche du target
            } else if (critter.activated
                    && pos.x() % 1 == 0 && pos.y() % 1 == 0) {
                critter.direction =
                    critter.directionClosestToTarget(state.getConfig());
            }
        }
    }

    /** Set le target de chaque fantôme en{@code Scatter}mode. */
    public static void setScatterTarget() {
        final int[] scatterX = {0, 27};
        final int[] scatterY = {2, 34};
        BLINKY.target = new IntCoordinates(
            scatterX[1], scatterY[0]).toRealCoordinates(1.0
        );
        INKY.target = new IntCoordinates(
            scatterX[1], scatterY[1]).toRealCoordinates(1.0
        );
        PINKY.target = new IntCoordinates(
            scatterX[0], scatterY[0]).toRealCoordinates(1.0
        );
        CLYDE.target = new IntCoordinates(
            scatterX[0], scatterY[1]).toRealCoordinates(1.0
        );
    }
}
