package model;

import geometry.IntCoordinates;
import geometry.RealCoordinates;
import model.MazeState.Mode;

import static model.Direction.*;
import static model.MazeState.*;

import java.util.ArrayList;
import java.util.List;

import config.Cell;
import config.MazeConfig;

public enum Ghost implements Critter {

    BLINKY, INKY, PINKY, CLYDE;

    private RealCoordinates pos;
    private Direction direction = Direction.NONE;
    private RealCoordinates target;
    private boolean activated;
    private boolean eaten;

    @Override
    public RealCoordinates getPos() {
        return pos;
    }

    @Override
    public void setPos(RealCoordinates newPos) {
        pos = newPos;
    }

    @Override
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public double getSpeed() {
        if (isInTunnel()) {
            return 2;
        }
        return 3;
    }

    public RealCoordinates getTarget() {
        return target;
    }

    public boolean isActivated() {
        return activated;
    }

    public boolean isEaten() {
        return eaten;
    }

    public void setEaten(boolean eaten) {
        this.eaten = eaten;
    }

    /**
     * La méthode renvoie {@code true} si le fantôme est dans un tunnel de téléportation horizontal de 3 de longueur à la position y = 10.
     * @return {@code true} si dans un tunnel
     */
    public boolean isInTunnel() {
        return ((int) pos.x() >= 0 && (int) pos.x() < 3 || (int) pos.x() <= 16 && (int) pos.x() > 13) && (int) pos.y() == 10;
    }

    /**
     * La méthode met à jour le target de {@code BLINKY} en {@code CHASE} mode.
     */
    public static void updateBlinky() {
        // Lorsque le fantôme a été mangé
        if (BLINKY.eaten) {
            BLINKY.target = new IntCoordinates(8, 10).toRealCoordinates(1.0);
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
            PINKY.target = new IntCoordinates(8, 10).toRealCoordinates(1.0);
            return;
        }

        double deltaX;
        double deltaY;
        // Pour éviter les situations lorsque Pac-Man est stationnaire
        Direction pacDir = PacMan.INSTANCE.getDirection() == NONE ? PacMan.INSTANCE.getLastDirection() : PacMan.INSTANCE.getDirection();

        switch (pacDir) {
            // Si Pac-Man regarde le Nord, le target se trouve deux cases en haut et deux cases à gauche de lui
            case NORTH:
                deltaX = new IntCoordinates(2, 0).toRealCoordinates(1.0).x();
                deltaY = new IntCoordinates(0, 2).toRealCoordinates(1.0).y();
                PINKY.target = new RealCoordinates(PacMan.INSTANCE.getPos().x() - deltaX, PacMan.INSTANCE.getPos().y() - deltaY);
                break;
            // Pour les autres directions, le target se trouvera 2 cases devant Pac-Man
            case EAST:
                deltaX = new IntCoordinates(2, 0).toRealCoordinates(1.0).x();
                PINKY.target = new RealCoordinates(PacMan.INSTANCE.getPos().x() + deltaX, PacMan.INSTANCE.getPos().y());
                break;
            case SOUTH:
                deltaY = new IntCoordinates(0, 2).toRealCoordinates(1.0).y();
                PINKY.target = new RealCoordinates(PacMan.INSTANCE.getPos().x(), PacMan.INSTANCE.getPos().y() + deltaY);
                break;
            case WEST:
                deltaX = new IntCoordinates(2, 0).toRealCoordinates(1.0).x();
                PINKY.target = new RealCoordinates(PacMan.INSTANCE.getPos().x() - deltaX, PacMan.INSTANCE.getPos().y());
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
            INKY.target = new IntCoordinates(8, 10).toRealCoordinates(1.0);
            return;
        }

        // Pour éviter les situations lorsque Pac-Man est stationnaire
        Direction pacDir = PacMan.INSTANCE.getDirection() == NONE ? PacMan.INSTANCE.getLastDirection() : PacMan.INSTANCE.getDirection();

        // Le target d'Inky se trouve à 2 fois la distances entre la position de Blinky et une case devant Pac-Man
        switch (pacDir) {
            case NORTH:
                INKY.target = new RealCoordinates(2 * PINKY.target.x() + 2 - BLINKY.getPos().x(), 2 * PINKY.target.y() + 2 - BLINKY.getPos().y());
                break;
            case EAST:
                INKY.target = new RealCoordinates(2 * PINKY.target.x() - 2 - BLINKY.getPos().x(), 2 * PINKY.target.y() - BLINKY.getPos().y());
                break;
            case SOUTH:
                INKY.target = new RealCoordinates(2 * PINKY.target.x() - BLINKY.getPos().x(), 2 * PINKY.target.y() - 2 - BLINKY.getPos().y());
                break;
            case WEST:
                INKY.target = new RealCoordinates(2 * PINKY.target.x() + 2 - BLINKY.getPos().x(), 2 * PINKY.target.y() - BLINKY.getPos().y());
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
            CLYDE.target = new IntCoordinates(8, 10).toRealCoordinates(1.0);
            return;
        }

        // Dans le vrai jeu, si Clyde se trouve dans un rayon de 8 cases de Pac-Man, ce dernier aura comme target le target en mode SCATTER
        // Sinon le target est identique à Blinky (la position de Pac-Man)

        // ici, ce rayon de 8 cases sera remplacé par 4 car le fonctionnement des cellules est différent (ici, les cellules sont plus grandes que celles du jeu original)
        int radiusRange = 4;
        if (Math.sqrt(Math.pow(CLYDE.getPos().x() - PacMan.INSTANCE.getPos().x(), 2) + Math.pow(CLYDE.getPos().y() - PacMan.INSTANCE.getPos().y(), 2)) < radiusRange) {
            CLYDE.target = new IntCoordinates(0, 21).toRealCoordinates(1.0);
        } else {
            CLYDE.target = PacMan.INSTANCE.getPos();
        }
    }

    /**
     * La méthode retourne une liste contenant les directions possibles qu'un fantôme peut emprunter à une intersection.
     * @param ghost Le fantôme
     * @param dir La direction actuelle du fantôme
     * @return Une {@code List<Direction>} contenant les directions possibles. Triées dans le sens horaire.
     */
    public List<Direction> possibleDirections(MazeConfig config) {
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
                if (isInTunnel()) {
                    possibleDirections.add(EAST);
                }
                if (!adjacentCells.get(2).northWall()) {
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
                if (!adjacentCells.get(1).northWall()) {
                    possibleDirections.add(SOUTH);
                }
                if (!adjacentCells.get(2).eastWall()) {
                    possibleDirections.add(WEST);
                }
                if (isInTunnel()) {
                    possibleDirections.add(WEST);
                }
                break;
            }
            default -> {
                break;
            }
        }

        // Si le fantôme se trouve au spot des murs invisibles
        if (pos.equals(new IntCoordinates(7, 8).toRealCoordinates(1.0)) ||
            pos.equals(new IntCoordinates(9, 8).toRealCoordinates(1.0)) ||
            pos.equals(new IntCoordinates(7, 16).toRealCoordinates(1.0)) ||
            pos.equals(new IntCoordinates(9, 16).toRealCoordinates(1.0))) {
            possibleDirections.remove(NORTH);
        }

        return possibleDirections;
    }

    /**
     * La méthode retourne la {@code Direction} la plus proche du target en utilisant la méthode {@code possibleDirections()}.<br></br>
     * Si la liste des directions possibles est nulle, la méthode retourne la direction contraire à la direction actuelle.<br></br>
     * Si le fantôme est au centre du spawn, la méthode retourne {@code NORTH}.
     * 
     * @param ghost Le fantôme
     * @param pos La position actuelle du fantôme
     * @param dir La direction actuelle du fantôme
     * @return La {@code Direction} la plus proche du target.
     */
    public Direction directionClosestToTarget(MazeConfig config) {
        List<Direction> possibleDirections = possibleDirections(config);
        
        // Si le fantôme se trouve dans un cul-de-sac, il se retourne
        if (possibleDirections.isEmpty()) {
            reverseDirection(config);
            return direction;
        }

        // Oblige le fantôme à sortir du home
        if (pos.equals(new IntCoordinates(8, 10).toRealCoordinates(1.0))) {
            return NORTH;
        }

        double[] deltaDist = {-1, -1, -1, -1};

        // Calcul des distances entre le fantôme et le target après déplacement
        for (Direction dir : possibleDirections) {
            switch (dir) {
                case NORTH:
                    deltaDist[0] = Math.sqrt(Math.pow(target.x() - pos.x(), 2) + Math.pow((target.y() - (pos.y() - 1)), 2));
                    break;
                case WEST:
                    deltaDist[1] = Math.sqrt(Math.pow(target.x() - (pos.x() - 1), 2) + Math.pow((target.y() - pos.y()), 2));
                    break;
                case SOUTH:
                    deltaDist[2] = Math.sqrt(Math.pow(target.x() - pos.x(), 2) + Math.pow((target.y() - (pos.y() + 1)), 2));
                    break;
                case EAST:
                    deltaDist[3] = Math.sqrt(Math.pow(target.x() - (pos.x() + 1), 2) + Math.pow((target.y() - pos.y()), 2));
                    break;
                default:
                    break;
            }
        }

        // Détermination de la plus petite distance
        double minVal = 10000;
        int min = -1;
        for (int i = 0; i < deltaDist.length; i++) {
            if (deltaDist[i] != -1 && deltaDist[i] < minVal) {
                min = i;
                minVal = deltaDist[i];
            }
        }
        switch (min) {
            case 0:
                return NORTH;
            case 1:
                return WEST;
            case 2:
                return SOUTH;
            case 3:
                return EAST;
            default:
                return NONE;
        }
    }

    /**
     * La méthode renvoie une direction "aléatoire" basée sur la direction du fantôme et des probabilités de chaque direction.
     * Les probabilités sont basées sur le jeu original : 16.3% Nord, 25.2% Est, 28.5% Sud, 29.9% Ouest.
     * @param config La configuration actuelle
     * @param dir La direction actuelle du fantôme
     */
    public void setRandomDirection(MazeConfig config) {
        List<Direction> possibles = possibleDirections(config);
        double rand = Math.random() * 100;
        if (rand < 16.3) {
            if (!possibles.isEmpty()) {
                direction = possibles.get(0);
            } else {
                reverseDirection(config);
            }
        }
        if (rand >= 16.3 && rand < 41.5) {
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
        if (rand >= 41.5 && rand < 70) {
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
        if (rand >= 70) {
            if (possibles.contains(WEST)) {
                direction = WEST;
            } else if (!possibles.isEmpty()) {
                direction = possibles.get(0);
            } else {
                reverseDirection(config);
            }
        }
    }

    public static void reverseGhostsDirection(MazeConfig config) {
        BLINKY.reverseDirection(config);
        INKY.reverseDirection(config);
        PINKY.reverseDirection(config);
        CLYDE.reverseDirection(config);
    }

    public void reverseDirection(MazeConfig config) {
        switch (direction) {
            case NORTH:
                if (!config.getCell(new IntCoordinates(pos.round().x(), pos.round().y() + 1)).northWall()) {
                    direction = SOUTH;
                }
                break;
            case EAST:
                if (!config.getCell(new IntCoordinates(pos.round().x() - 1, pos.round().y())).eastWall()) {
                    direction = WEST;
                }
                break;
            case SOUTH:
                if (!config.getCell(new IntCoordinates(pos.round().x(), pos.round().y() - 1)).southWall()) {
                    direction = NORTH;
                }
                break;
            case WEST:
                if (!config.getCell(new IntCoordinates(pos.round().x() + 1, pos.round().y())).westWall()) {
                    direction = EAST;
                }
                break;
            default:
                break;
        }
    }

    public void resetGhostPos(MazeState state) {
        if (this == BLINKY || this == INKY && state.getPacgomme() >= 30) direction = EAST;
        else if (this == PINKY) direction = NORTH;
        else if (this == CLYDE && state.getPacgomme() >= 146 / 3) direction = WEST;
        else direction = NONE;
        
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
     * La méthode met à jour les targets de chaque fantôme selon le mode courant.
     * Elle met aussi à jour la direction de chaque fantôme
     * lorsqu'il entre dans une intersection. {@code INKY} et {@code CLYDE} ne
     * peuvent bouger seulement lorsqu'un score a été atteint.
     * 
     * @param deltaTns La variation du temps entre deux instants.
     */
    public static void updateGhost(long deltaTns, MazeState state) {
        List<Ghost> critters = List.of(CLYDE, BLINKY, INKY, PINKY);
        for (Ghost critter : critters) {
            critter.calibratePos(deltaTns, critter.getPos(), critter.nextPos(deltaTns), state.getHeight(), state.getWidth());
            RealCoordinates pos = critter.getPos();

            // Lorsque le fantôme mangé retourne au spawn
            if (critter.eaten && pos.equals(new IntCoordinates(8, 10).toRealCoordinates(1.0))) {
                critter.eaten = false;
            }

            // Activation d'Inky à partir de 30 points
            if (critter == INKY && !INKY.activated && state.getPacgomme() >= 30) {
                INKY.activated = true;
                critter.direction = EAST;
                // Activation de Clyde lorsqu'un tier des points totaux a été obtenu
            } else if (critter == CLYDE && !CLYDE.activated && state.getPacgomme() >= state.getMaxScore() / 3) {
                CLYDE.activated = true;
                critter.direction = WEST;
                // Set direction aléatoire en FRIGHTENED mode
            } else if (!critter.eaten && state.getMode() == Mode.FRIGHTENED && pos.x() % 1 == 0 && pos.y() % 1 == 0) {
                state.frightenedMode(critter);
                // Set direction la plus proche du target
            } else if (critter.activated && pos.x() % 1 == 0 && pos.y() % 1 == 0) {
                critter.direction = critter.directionClosestToTarget(state.getConfig());
            }
        }
    }

    public static void setScatterTarget() {
        BLINKY.target = new IntCoordinates(16, 0).toRealCoordinates(1.0);
        INKY.target = new IntCoordinates(16, 21).toRealCoordinates(1.0);
        PINKY.target = new IntCoordinates(0, 0).toRealCoordinates(1.0);
        CLYDE.target = new IntCoordinates(0, 21).toRealCoordinates(1.0);
    }
}