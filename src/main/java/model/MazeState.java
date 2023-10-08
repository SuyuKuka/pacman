package model;

import config.MazeConfig;
import config.Cell;
import geometry.IntCoordinates;
import geometry.RealCoordinates;

import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static model.Ghost.*;
import static model.Direction.*;

public final class MazeState {
    private final MazeConfig config;
    private final int height;
    private final int width;

    private final boolean[][] gridState;
    private enum Mode {
        CHASE, SCATTER, FRIGHTENED
    }
    private Mode mode;
    private Timer timer = new Timer();

    private final List<Critter> critters;
    private int score;

    private final Map<Critter, RealCoordinates> initialPos;
    private int lives = 3;

    public MazeState(MazeConfig config) {
        this.config = config;
        height = config.getHeight();
        width = config.getWidth();
        critters = List.of(PacMan.INSTANCE, CLYDE, BLINKY, INKY, PINKY);
        gridState = new boolean[height][width];
        initialPos = Map.of(
                PacMan.INSTANCE, config.getPacManPos().toRealCoordinates(1.0),
                BLINKY, config.getBlinkyPos().toRealCoordinates(1.0),
                INKY, config.getInkyPos().toRealCoordinates(1.0),
                CLYDE, config.getClydePos().toRealCoordinates(1.0),
                PINKY, config.getPinkyPos().toRealCoordinates(1.0)
        );
        resetCritters();
    }

    public List<Critter> getCritters() {
        return critters;
    }

    public double getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void update(long deltaTns) {
        updatePacMan(deltaTns);
        // Active les fantômes après la première input
        if (PacMan.INSTANCE.getPacManMoved() == 1) {
            activateGhost();
            PacMan.INSTANCE.setPacManMoved(-1);
        } else if (PacMan.INSTANCE.getPacManMoved() != 0) {
            updateGhost(deltaTns);
        }

        updateScore();
        updateCollision();
    }

    /**
     * La méthode arrête Pac-Man lorsqu'il rencontre un mur. Elle met aussi à jour sa position lorsqu'il traverse une case pour faciliter les
     * virages.
     * @param deltaTns La variation du temps entre deux instants.
     */
    private void updatePacMan(long deltaTns) {
        var curPos = PacMan.INSTANCE.getPos();
        var nextPos = PacMan.INSTANCE.nextPos(deltaTns);
        var curNeighbours = curPos.intNeighbours();
        var nextNeighbours = nextPos.intNeighbours();

        // Nécessaire pour éviter les problèmes de détermination du target selon la direction de Pac-Man
        if (PacMan.INSTANCE.getDirection() != NONE) PacMan.INSTANCE.setLastDirection(PacMan.INSTANCE.getDirection());

        if (!curNeighbours.containsAll(nextNeighbours)) { // the critter would overlap new cells. Do we allow it?
            switch (PacMan.INSTANCE.getDirection()) {
                case NORTH -> {
                    for (var n: curNeighbours) if (config.getCell(n).northWall()) {
                        nextPos = curPos.floorY();
                        PacMan.INSTANCE.setDirection(Direction.NONE);
                        break;
                    }
                }
                case EAST -> {
                    for (var n: curNeighbours) if (config.getCell(n).eastWall()) {
                        nextPos = curPos.ceilX();
                        PacMan.INSTANCE.setDirection(Direction.NONE);
                        break;
                    }
                }
                case SOUTH -> {
                    for (var n: curNeighbours) if (config.getCell(n).southWall()) {
                        nextPos = curPos.ceilY();
                        PacMan.INSTANCE.setDirection(Direction.NONE);
                        break;
                    }
                }
                case WEST -> {
                    for (var n: curNeighbours) if (config.getCell(n).westWall()) {
                        nextPos = curPos.floorX();
                        PacMan.INSTANCE.setDirection(Direction.NONE);
                        break;
                    }
                }
                default -> {
                    break;
                }
            }
        }
        PacMan.INSTANCE.calibratePos(deltaTns, curPos, nextPos, height, width);
    }

    /**
     * La méthode active le timer pour les changements de mode entre {@code CHASE}, {@code SCATTER} et {@code FRIGHTENED}. Les positions de
     * {@code BLINKY} et de {@code PINKY} sont également initialiséees.
     */
    public void activateGhost() {
        updateMode();
        BLINKY.setDirection(EAST);
        PINKY.setDirection(NORTH);
    }

    /**
     * La méthode active plusieurs timers nécessaires à l'alternance des modes dans lesquels les fantômes se trouvent.
     */
    private void updateMode() {
        mode = Mode.SCATTER;
        TimerTask chase1 = new TimerTask() {
            public void run() {
                setMode(Mode.CHASE);
            }
        };
        timer.schedule(chase1, (long) 7 * 1000);
        TimerTask scatter2 = new TimerTask() {
            public void run() {
                setMode(Mode.SCATTER);
            }
        };
        timer.schedule(scatter2, (long) (7 + 20) * 1000);
        TimerTask chase2 = new TimerTask() {
            public void run() {
                setMode(Mode.CHASE);
            }
        };
        timer.schedule(chase2, (long) (7 + 20 + 7) * 1000);
        TimerTask scatter3 = new TimerTask() {
            public void run() {
                setMode(Mode.SCATTER);
            }
        };
        timer.schedule(scatter3, (long) (7 + 20 + 7 + 20) * 1000);
        TimerTask chase3 = new TimerTask() {
            public void run() {
                setMode(Mode.CHASE);
            }
        };
        timer.schedule(chase3, (long) (7 + 20 + 7 + 20 + 5) * 1000);
        TimerTask scatter4 = new TimerTask() {
            public void run() {
                setMode(Mode.SCATTER);
            }
        };
        timer.schedule(scatter4, (long) (7 + 20 + 7 + 20 + 5 + 20) * 1000);
    }

    private void setMode(Mode mode) {
        this.mode = mode;
    }

    /**
     * La méthode met à jour les targets de chaque fantôme selon le mode courant. Elle met aussi à jour la direction de chaque fantôme 
     * lorsqu'il entre dans une intersection. {@code INKY} et {@code CLYDE} ne peuvent bouger seulement lorsqu'un score a été atteint.
     * @param deltaTns La variation du temps entre deux instants.
     */
    private void updateGhost(long deltaTns) {
        if (mode == Mode.SCATTER) {
            scatterMode();
        } else if (mode == Mode.CHASE) {
            updateBlinky();
            updateInky();
            updatePinky();
            updateClyde();
        }
        
        for (Critter critter : critters) {
            critter.calibratePos(deltaTns, critter.getPos(), critter.nextPos(deltaTns), height, width);
            RealCoordinates pos = critter.getPos();
            Direction dir = critter.getDirection();
            if (critter == INKY && score == 30) {
                critter.setDirection(EAST);
            } else if (critter == CLYDE && score == 146/3) {
                critter.setDirection(WEST);
            } else if (critter instanceof Ghost && pos.x()%1 == 0 && pos.y()%1 == 0) {
                critter.setDirection(directionClosestToTarget(config, (Ghost) critter, pos, dir));
            }
        }
        
    }

    /**
     * La méthode met à jour les targets de chaque fantôme dans un coin de la map qui leur est dédié durant le {@code SCATTER} mode.
     */
    private void scatterMode() {
        BLINKY.setTarget(new IntCoordinates(16, 0).toRealCoordinates(1.0));
        INKY.setTarget(new IntCoordinates(16, 21).toRealCoordinates(1.0));
        PINKY.setTarget(new IntCoordinates(0, 0).toRealCoordinates(1.0));
        CLYDE.setTarget(new IntCoordinates(0, 21).toRealCoordinates(1.0));
    }

    // FIXME Pac-Man rules should somehow be in Pacman class (mettre updateScore et updateCollision dans PacMan ?)

    /**
     * La méthode incrémente le score de 1 après que Pac-Man mange un pac-gomme.
     */
    private void updateScore() {
        var pacPos = PacMan.INSTANCE.getPos().round();
        if (!gridState[pacPos.y()][pacPos.x()]) {
            addScore(1);
            gridState[pacPos.y()][pacPos.x()] = true;
        }
    }

    /**
     * La méthode appelle la méthode {@code playerList} lorsque Pac-Man est au contact d'un fantôme. Si Pac-Man est {@code ENERGIZED}, le score
     * augmente de 10 et le fantôme revient à sa position initiale.
     */
    private void updateCollision() {
        var pacPos = PacMan.INSTANCE.getPos().round();
        for (var critter : critters) {
            if (critter instanceof Ghost && critter.getPos().round().equals(pacPos)) {
                if (PacMan.INSTANCE.isEnergized()) {
                    addScore(10);
                    resetCritter(critter);
                } else {
                    playerLost();
                    return;
                }
            }
        }
    }

    private void addScore(int increment) {
        score += increment;
        displayScore();
    }

    private void displayScore() {
        // FIXME: this should be displayed in the JavaFX view, not in the console
        System.out.println("Score: " + score);
    }

    private void playerLost() {
        // FIXME: this should be displayed in the JavaFX view, not in the console. A game over screen would be nice too.
        lives--;
        if (lives == 0) {
            System.out.println("Game over!");
            System.exit(0);
        }
        System.out.println("Lives: " + lives);
        resetCritters();
    }

    private void resetCritter(Critter critter) {
        if (critter == BLINKY || critter == INKY && score >= 30) critter.setDirection(EAST);
        else if (critter == PINKY) critter.setDirection(NORTH);
        else if (critter == CLYDE && score >= 146/3) critter.setDirection(WEST);
        else critter.setDirection(NONE);
        critter.setPos(initialPos.get(critter));
    }

    private void resetCritters() {
        for (var critter: critters) resetCritter(critter);
    }

    public MazeConfig getConfig() {
        return config;
    }

    public boolean getGridState(IntCoordinates pos) {
        return gridState[pos.y()][pos.x()];
    }
}
