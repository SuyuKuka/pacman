package model;

import config.MazeConfig;
import geometry.IntCoordinates;
import geometry.RealCoordinates;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private Instant start;

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
            updateMode();
            PacMan.INSTANCE.setPacManMoved(-1);
        } else if (PacMan.INSTANCE.getPacManMoved() != 0) {
            updateMode();
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

        // Nécessaire pour éviter les problèmes de détermination du target selon la direction de Pac-Man
        if (PacMan.INSTANCE.getDirection() != NONE) PacMan.INSTANCE.setLastDirection(PacMan.INSTANCE.getDirection());
        
        if (updatePacManDirection(deltaTns)) {
            nextPos = PacMan.INSTANCE.nextPos(deltaTns, PacMan.INSTANCE.getBufferDirection());
        }
        nextPos = updatePacManPos(curPos, nextPos);
        PacMan.INSTANCE.calibratePos(deltaTns, curPos, nextPos, height, width);
        PacMan.INSTANCE.increaseBufferDurationTime(deltaTns);
    }

    /**
     * Méthode qui actualise la {@code Direction} de {@code Pac-Man} en fonction du terrain et de son {@code bufferDirection}.
     * @param deltaTns Temps écoulé en nanosecondes.
     * @return Retourne vraie si la {@code Direction} de {@code Pac-Man} devrait changer ou faux sinon.
     */
    public boolean updatePacManDirection(long deltaTns) {
        var curNeighbours = PacMan.INSTANCE.getPos().intNeighbours();
        var nextNeighbours = PacMan.INSTANCE.nextPos(deltaTns).intNeighbours();
        boolean isBufferUsed = true;
        if (PacMan.INSTANCE.getBufferDirection() != Direction.NONE) {
            switch (PacMan.INSTANCE.getBufferDirection()) {
                case NORTH -> {
                    for (var n: curNeighbours) 
                        if (nextNeighbours.size() >= 4 || config.getCell(n).northWall()) isBufferUsed = false;
                    break;
                }
                case EAST -> {
                    for (var n: curNeighbours)
                        if (nextNeighbours.size() >= 4 || config.getCell(n).eastWall()) isBufferUsed = false;
                    break;
                }
                case SOUTH -> {
                    for (var n: curNeighbours)
                        if (nextNeighbours.size() >= 4 || config.getCell(n).southWall()) isBufferUsed = false;
                    break;
                }
                case WEST -> {
                    for (var n: curNeighbours)
                        if (nextNeighbours.size() >= 4 || config.getCell(n).westWall()) isBufferUsed = false;
                    break;
                }
                default -> {
                    break;
                }
            }
            if (isBufferUsed) {
                PacMan.INSTANCE.setDirection(PacMan.INSTANCE.getBufferDirection());
                PacMan.INSTANCE.setBufferDirection(Direction.NONE);
                return true;
            }
        }
        return false;
    }

    /**
     * Méthode qui actualise la position ({@code RealCoordinates}) de {@code Pac-Man} en fonction du terrain.
     * @param curPos {@code RealCoordinates} actuelle de {@code Pac-Man}.
     * @param nextPos {@code RealCoordinates} supposée (si il ne traverse pas de mur) de {@code Pac-Man}.
     * @return Retourne un {@code RealCoordinates} (modifié ou non).
     */
    public RealCoordinates updatePacManPos(RealCoordinates curPos, RealCoordinates nextPos) {
        var curNeighbours = curPos.intNeighbours();
        var nextNeighbours = nextPos.intNeighbours();
        if (!curNeighbours.containsAll(nextNeighbours)) { // the critter would overlap new cells. Do we allow it?
            switch (PacMan.INSTANCE.getDirection()) {
                case NORTH -> {
                    for (var n: curNeighbours) if (config.getCell(n).northWall()) {
                        PacMan.INSTANCE.setDirection(Direction.NONE);
                        return nextPos = curPos.floorY();
                    }
                }
                case EAST -> {
                    for (var n: curNeighbours) if (config.getCell(n).eastWall()) {
                        PacMan.INSTANCE.setDirection(Direction.NONE);
                        return nextPos = curPos.ceilX();
                    }
                }
                case SOUTH -> {
                    for (var n: curNeighbours) if (config.getCell(n).southWall()) {
                        PacMan.INSTANCE.setDirection(Direction.NONE);
                        return nextPos = curPos.ceilY();
                    }
                }
                case WEST -> {
                    for (var n: curNeighbours) if (config.getCell(n).westWall()) {
                        PacMan.INSTANCE.setDirection(Direction.NONE);
                        return nextPos = curPos.floorX();
                    }
                }
                default -> {
                    break;
                }
            }
        }
        return nextPos;
    }

    /**
     * La méthode initialise la variable {@code start} qui garde l'heure UNIX au lancement de la partie (lorsque Pac-Man bouge pour la première fois),
     * initialise le premier mode en {@code SCATTER} et
     * initialise les directions de {@code BLINKY} et de {@code PINKY}.
     */
    public void activateGhost() {
        start = Instant.now();
        mode = Mode.SCATTER;
        BLINKY.setActivated(true);
        PINKY.setActivated(true);
        BLINKY.setDirection(EAST);
        PINKY.setDirection(NORTH);
    }

    /**
     * La méthode met à jour les targets de chaque fantôme selon le mode courant. Elle met aussi à jour la direction de chaque fantôme 
     * lorsqu'il entre dans une intersection. {@code INKY} et {@code CLYDE} ne peuvent bouger seulement lorsqu'un score a été atteint.
     * @param deltaTns La variation du temps entre deux instants.
     */
    private void updateGhost(long deltaTns) {
        for (Critter critter : critters) {
            if (critter instanceof Ghost) critter.calibratePos(deltaTns, critter.getPos(), critter.nextPos(deltaTns), height, width);
            RealCoordinates pos = critter.getPos();
            Direction dir = critter.getDirection();

            // Activation d'Inky à partir de 30 points
            if (critter == INKY && !INKY.isActivated() && score == 30 ) {
                INKY.setActivated(true);
                critter.setDirection(EAST);
            // Activation de Clyde lorsqu'un tier des points totaux a été obtenu
            } else if (critter == CLYDE && !CLYDE.isActivated() && score == 146/3) {
                CLYDE.setActivated(true);
                critter.setDirection(WEST);
            // Set direction aléatoire en FRIGHTENED mode
            } else if (critter instanceof Ghost && mode == Mode.FRIGHTENED && pos.x()%1 == 0 && pos.y()%1 == 0) {
                frightenedMode((Ghost) critter, dir);
            // Set direction la plus proche du target
            } else if (critter instanceof Ghost && ((Ghost) critter).isActivated() && pos.x()%1 == 0 && pos.y()%1 == 0) {
                critter.setDirection(directionClosestToTarget(config, (Ghost) critter, pos, dir));
            }
        }
    }

    /**
     * La méthode met à jour le mode selon le temps de jeu. A chaque changement de mode sauf pour la fin du mode {@code FRIGHTENED}, les fantômes
     * se retournent.
     */
    private void updateMode() {
        // Pac-Man est ENERGIZED, changement du mode en FRIGHTENED
        if (PacMan.INSTANCE.isEnergized() && mode != Mode.FRIGHTENED) {
            reverseGhostsDirection();
            mode = Mode.FRIGHTENED;
        // Changement du mode selon le temps de jeu
        } else if (!PacMan.INSTANCE.isEnergized()) {
            long startMilli = start.toEpochMilli();
            long now = Instant.now().toEpochMilli();

            if (now > startMilli + 7 * 1000 ||
                now > startMilli + (7 + 20 + 7) * 1000 ||
                now > startMilli + (7 + 20 + 7 + 20 + 5) * 1000 ||
                now > startMilli + (7 + 20 + 7 + 20 + 5 + 20 + 5) * 1000) {
                    if (mode == Mode.SCATTER) {
                        Ghost.reverseGhostsDirection();
                    }
                    chaseMode();
            }
            if (now < startMilli + 7 * 1000 ||
                now > startMilli + (7 + 20) * 1000 && now <= startMilli + (7 + 20 + 7) * 1000 ||
                now > startMilli + (7 + 20 + 7 + 20) * 1000 && now <= startMilli + (7 + 20 + 7 + 20 + 5) * 1000 ||
                now > startMilli + (7 + 20 + 7 + 20 + 5 + 20) * 1000 && now <= startMilli + (7 + 20 + 7 + 20 + 5 + 20 + 5) * 1000) {
                    if (mode == Mode.CHASE) {
                        Ghost.reverseGhostsDirection();
                    }
                    mode = Mode.SCATTER;
                    scatterMode();
            }
        }
    }

    /**
     * La méthode met à jour les targets de chaque fantôme dans un coin de la map qui leur est dédié durant le {@code SCATTER} mode.
     */
    private void scatterMode() {
        mode = Mode.SCATTER;
        BLINKY.setTarget(new IntCoordinates(16, 0).toRealCoordinates(1.0));
        INKY.setTarget(new IntCoordinates(16, 21).toRealCoordinates(1.0));
        PINKY.setTarget(new IntCoordinates(0, 0).toRealCoordinates(1.0));
        CLYDE.setTarget(new IntCoordinates(0, 21).toRealCoordinates(1.0));
    }

    /**
     * La méthode met à jour les targets de chaque fantôme par rapport à Pac-Man en {@code CHASE} mode.
     */
    private void chaseMode() {
        mode = Mode.CHASE;
        updateBlinky();
        updateInky();
        updatePinky();
        updateClyde();
    }

    /**
     * La méthode met à jour les directions de chaque fantôme "aléatoirement" en {@code FRIGHTENED} mode.
     */
    private void frightenedMode(Ghost ghost, Direction dir) {
        if (ghost.isActivated()) {
            ghost.setRandomDirection(config, dir);
        }
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