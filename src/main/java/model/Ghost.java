package model;

import geometry.IntCoordinates;
import geometry.RealCoordinates;
import static model.Direction.*;

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
            return 3;
        }
        return 4;
    }

    public RealCoordinates getTarget() {
        return target;
    }

    public void setTarget(RealCoordinates target) {
        this.target = target;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
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
        BLINKY.setTarget(PacMan.INSTANCE.getPos());
    }

    /**
     * La méthode met à jour le target de {@code PINKY} en {@code CHASE} mode.
     */
    public static void updatePinky() {
        RealCoordinates targetPos;
        double deltaX;
        double deltaY;
        // Pour éviter les situations lorsque Pac-Man est stationnaire
        Direction pacDir = PacMan.INSTANCE.getDirection() == NONE ? PacMan.INSTANCE.getLastDirection() : PacMan.INSTANCE.getDirection();

        switch (pacDir) {
            // Si Pac-Man regarde le Nord, le target se trouve deux cases en haut et deux cases à gauche de lui
            case NORTH:
                deltaX = new IntCoordinates(2, 0).toRealCoordinates(1.0).x();
                deltaY = new IntCoordinates(0, 2).toRealCoordinates(1.0).y();
                targetPos = new RealCoordinates(PacMan.INSTANCE.getPos().x() - deltaX, PacMan.INSTANCE.getPos().y() - deltaY);
                PINKY.setTarget(targetPos);
                break;
            // Pour les autres directions, le target se trouvera 2 cases devant Pac-Man
            case EAST:
                deltaX = new IntCoordinates(2, 0).toRealCoordinates(1.0).x();
                targetPos = new RealCoordinates(PacMan.INSTANCE.getPos().x() + deltaX, PacMan.INSTANCE.getPos().y());
                PINKY.setTarget(targetPos);
                break;
            case SOUTH:
                deltaY = new IntCoordinates(0, 2).toRealCoordinates(1.0).y();
                targetPos = new RealCoordinates(PacMan.INSTANCE.getPos().x(), PacMan.INSTANCE.getPos().y() + deltaY);
                PINKY.setTarget(targetPos);
                break;
            case WEST:
                deltaX = new IntCoordinates(2, 0).toRealCoordinates(1.0).x();
                targetPos = new RealCoordinates(PacMan.INSTANCE.getPos().x() - deltaX, PacMan.INSTANCE.getPos().y());
                PINKY.setTarget(targetPos);
                break;
            default:
                PINKY.setTarget(PacMan.INSTANCE.getPos());
                break;
        }
    }

    /**
     * La méthode met à jour le target de {@code INKY} en {@code CHASE} mode.
     */
    public static void updateInky() {
        RealCoordinates targetPos;
        // Pour éviter les situations lorsque Pac-Man est stationnaire
        Direction pacDir = PacMan.INSTANCE.getDirection() == NONE ? PacMan.INSTANCE.getLastDirection() : PacMan.INSTANCE.getDirection();

        // Le target d'Inky se trouve à 2 fois la distances entre la position de Blinky et une case devant Pac-Man
        switch (pacDir) {
            case NORTH:
                targetPos = new RealCoordinates(2 * PINKY.getTarget().x() + 2 - BLINKY.getPos().x(), 2 * PINKY.getTarget().y() + 2 - BLINKY.getPos().y());
                INKY.setTarget(targetPos);
                break;
            case EAST:
                targetPos = new RealCoordinates(2 * PINKY.getTarget().x() - 2 - BLINKY.getPos().x(), 2 * PINKY.getTarget().y() - BLINKY.getPos().y());
                INKY.setTarget(targetPos);
                break;
            case SOUTH:
                targetPos = new RealCoordinates(2 * PINKY.getTarget().x() - BLINKY.getPos().x(), 2 * PINKY.getTarget().y() - 2 - BLINKY.getPos().y());
                INKY.setTarget(targetPos);
                break;
            case WEST:
                targetPos = new RealCoordinates(2 * PINKY.getTarget().x() + 2 - BLINKY.getPos().x(), 2 * PINKY.getTarget().y() - BLINKY.getPos().y());
                INKY.setTarget(targetPos);
                break;
            default:
                INKY.setTarget(PacMan.INSTANCE.getPos());
                break;
        }
    }

    /**
     * La méthode met à jour le target de {@code CLYDE} en {@code CHASE} mode.
     */
    public static void updateClyde() {
        // Si Clyde se trouve dans un rayon de 8 cases de Pac-Man, ce dernier aura comme target le target en mode SCATTER
        // Sinon le target est identique à Blinky (la position de Pac-Man)
        if (Math.sqrt(Math.pow(CLYDE.getPos().x() - PacMan.INSTANCE.getPos().x(), 2) + Math.pow(CLYDE.getPos().y() - PacMan.INSTANCE.getPos().y(), 2)) < 8) {
            CLYDE.setTarget(new IntCoordinates(0, 21).toRealCoordinates(1.0));
        } else {
            CLYDE.setTarget(PacMan.INSTANCE.getPos());
        }
    }

    /**
     * La méthode retourne une liste contenant les directions possibles qu'un fantôme peut emprunter à une intersection.
     * @param ghost Le fantôme
     * @param dir La direction actuelle du fantôme
     * @return Une {@code List<Direction>} contenant les directions possibles. Triées dans le sens horaire.
     */
    public List<Direction> possibleDirections(MazeConfig config, Direction dir) {
        List<Cell> adjacentCells = config.getAdjacentCells(getPos(), dir);
        List<Direction> possibleDirections = new ArrayList<>();
        switch (dir) {
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
    public static Direction directionClosestToTarget(MazeConfig config, Ghost ghost, RealCoordinates pos, Direction dir) {
        List<Direction> possibleDirections = ghost.possibleDirections(config, dir);
        
        // Si le fantôme se trouve dans un cul-de-sac, il se retourne
        if (possibleDirections.isEmpty()) {
            ghost.reverseDirection();
        }

        // Oblige le fantôme à sortir du home
        if (pos.equals(new IntCoordinates(8, 10).toRealCoordinates(1.0))) {
            return NORTH;
        }

        double[] deltaDist = new double[4];
        for (int i = 0; i < deltaDist.length; i++) {
            deltaDist[i] = -1;
        }

        // Calcul des distances entre le fantôme et le target après déplacement
        for (Direction direction : possibleDirections) {
            switch (direction) {
                case NORTH:
                    deltaDist[0] = Math.sqrt(Math.pow(ghost.getTarget().x() - pos.x(), 2) + Math.pow((ghost.getTarget().y() - (pos.y() - 1)), 2));
                    break;
                case EAST:
                    deltaDist[1] = Math.sqrt(Math.pow(ghost.getTarget().x() - (pos.x() + 1), 2) + Math.pow((ghost.getTarget().y() - pos.y()), 2));
                    break;
                case SOUTH:
                    deltaDist[2] = Math.sqrt(Math.pow(ghost.getTarget().x() - pos.x(), 2) + Math.pow((ghost.getTarget().y() - (pos.y() + 1)), 2));
                    break;
                case WEST:
                    deltaDist[3] = Math.sqrt(Math.pow(ghost.getTarget().x() - (pos.x() - 1), 2) + Math.pow((ghost.getTarget().y() - pos.y()), 2));
                    break;
                default:
                    break;
            }
        }

        // Détermination de la plus petite distance
        double minVal = 10000;
        int min = 0;
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
                return EAST;
            case 2:
                return SOUTH;
            case 3:
                return WEST;
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
    public void setRandomDirection(MazeConfig config, Direction dir) {
        List<Direction> possibles = possibleDirections(config, dir);
        double rand = Math.random() * 100;
        if (rand < 16.3) {
            if (!possibles.isEmpty()) {
                direction = possibles.get(0);
            } else {
                reverseDirection();
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
                reverseDirection();
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
            reverseDirection();
            }
        }
        if (rand >= 70) {
            if (possibles.contains(WEST)) {
                direction = WEST;
            } else if (!possibles.isEmpty()) {
                direction = possibles.get(0);
            } else {
                reverseDirection();
            }
        }
    }

    public static void reverseGhostsDirection() {
        BLINKY.reverseDirection();
        INKY.reverseDirection();
        PINKY.reverseDirection();
        CLYDE.reverseDirection();
    }

    public void reverseDirection() {
        switch (direction) {
            case NORTH:
                direction = SOUTH;
                break;
            case EAST:
                direction = WEST;
                break;
            case SOUTH:
                direction = NORTH;
                break;
            case WEST:
                direction = EAST;
                break;
            default:
                break;
        }
    }
}