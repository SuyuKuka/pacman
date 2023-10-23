package model;

import config.Cell;
import config.MazeConfig;
import geometry.IntCoordinates;
import geometry.RealCoordinates;
import utils.Music;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static config.Cell.Content.*;

import static model.Ghost.*;

public final class MazeState {
    private static MazeState currentMaze;

    private final MazeConfig config;
    private final int height;
    private final int width;

    private final boolean[][] gridState;

    public enum Mode {
        CHASE, SCATTER, FRIGHTENED
    }

    private Mode mode;
    private long start;

    private final List<Critter> critters;
    private int score;

    private final Map<Critter, RealCoordinates> initialPos;
    private final int totalLives;
    private int lives;
    private boolean win = false;
    private int maxScore = 0;
    private int pacgomme = 0;

    public MazeState(MazeConfig config) {
        this.config = config;
        // TODO: Ajouter difficulté dans config et faire un switch (difficulté) pour le nb de total live
        PacMan.INSTANCE.resetPacMan();
        totalLives = 3;
        lives = totalLives;
        height = config.getHeight();
        width = config.getWidth();
        critters = List.of(PacMan.INSTANCE, CLYDE, BLINKY, INKY, PINKY);
        gridState = new boolean[height][width];
        setGrid(config.getGrid());
        initialPos = Map.of(
            PacMan.INSTANCE, config.getPacManPos().toRealCoordinates(1.0),
            BLINKY, config.getBlinkyPos().toRealCoordinates(1.0),
            INKY, config.getInkyPos().toRealCoordinates(1.0),
            CLYDE, config.getClydePos().toRealCoordinates(1.0),
            PINKY, config.getPinkyPos().toRealCoordinates(1.0)
        );
        resetCritters();
        currentMaze = this;
    }

    public void update(long deltaTns) {
        if (lives > 0 && !win) {
            PacMan.INSTANCE.updatePacMan(deltaTns, this);
            // Active les fantômes après la première input
            if (PacMan.INSTANCE.getPacManMoved() == 1) {
                start = Instant.now().toEpochMilli();
                mode = Mode.SCATTER;
                Ghost.activateGhost();
                updateMode();
                PacMan.INSTANCE.setPacManMoved(-1);
            } else if (PacMan.INSTANCE.getPacManMoved() != 0) {
                updateMode();
                Ghost.updateGhost(deltaTns, this);
            }
            updateScore();
            updateCollision();
        }
    }

    /**
     * La méthode met à jour le mode selon le temps de jeu. A chaque changement de
     * mode sauf pour la fin du mode {@code FRIGHTENED}, les fantômes
     * se retournent.
     */
    private void updateMode() {
        // Pac-Man est ENERGIZED, changement du mode en FRIGHTENED
        if (PacMan.INSTANCE.isEnergized() && mode != Mode.FRIGHTENED) {
            reverseGhostsDirection(config);
            mode = Mode.FRIGHTENED;
            // Changement du mode selon le temps de jeu
        } else if (!PacMan.INSTANCE.isEnergized()) {
            long now = Instant.now().toEpochMilli();
            
            if (now < start + 7 * 1000 ||
                now > start + (7 + 20) * 1000 && now <= start + (7 + 20 + 7) * 1000 ||
                now > start + (7 + 20 + 7 + 20) * 1000 && now <= start + (7 + 20 + 7 + 20 + 5) * 1000 ||
                now > start + (7 + 20 + 7 + 20 + 5 + 20) * 1000
                && now <= start + (7 + 20 + 7 + 20 + 5 + 20 + 5) * 1000) {
                if (mode == Mode.CHASE) {
                    Ghost.reverseGhostsDirection(config);
                }
                scatterMode();
            } else if (now > start + 7 * 1000 ||
                now > start + (7 + 20 + 7) * 1000 ||
                now > start + (7 + 20 + 7 + 20 + 5) * 1000 ||
                now > start + (7 + 20 + 7 + 20 + 5 + 20 + 5) * 1000) {
                if (mode == Mode.SCATTER) {
                    Ghost.reverseGhostsDirection(config);
                }
                chaseMode();
            }
        }
    }

    /**
     * La méthode met à jour les targets de chaque fantôme dans un coin de la map
     * qui leur est dédié durant le {@code SCATTER} mode.
     */
    private void scatterMode() {
        mode = Mode.SCATTER;
        Ghost.setScatterTarget();
    }

    /**
     * La méthode met à jour les targets de chaque fantôme par rapport à Pac-Man en
     * {@code CHASE} mode.
     */
    private void chaseMode() {
        mode = Mode.CHASE;
        updateBlinky();
        updateInky();
        updatePinky();
        updateClyde();
    }

    /**
     * La méthode met à jour les directions de chaque fantôme "aléatoirement" en
     * {@code FRIGHTENED} mode.
     */
    public void frightenedMode(Ghost ghost) {
        if (ghost.isActivated()) {
            ghost.setRandomDirection(config);
        }
    }

    /**
     * La méthode met à jour l'état du gridState
     */
    private void setGrid(Cell[][] maze) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (maze[i][j].initialContent() == Cell.Content.DOT || maze[i][j].initialContent() == ENERGIZER) {
                    maxScore++;
                }
                if (maze[i][j].initialContent() == Cell.Content.NOTHING) {
                    gridState[i][j] = true;
                }
            }
        }
    }

    // FIXME Pac-Man rules should somehow be in Pacman class (mettre updateScore et updateCollision dans PacMan ?)

    /**
     * La méthode incrémente le score de 1 après que Pac-Man mange un pac-gomme.
     */
    private void updateScore() {
        var pacPos = PacMan.INSTANCE.getPos().round();
        if (!gridState[pacPos.y()][pacPos.x()]) {
            Music.playSound("eat.wav");//Music pour eat
            addScore(1);
            gridState[pacPos.y()][pacPos.x()] = true;
        }
    }

    /**
     * La méthode appelle la méthode {@code playerList} lorsque Pac-Man est au
     * contact d'un fantôme. Si Pac-Man est {@code ENERGIZED}, le score
     * augmente de 10 et le fantôme revient à sa position initiale.
     */
    private void updateCollision() {
        var pacPos = PacMan.INSTANCE.getPos().round();
        for (var critter : critters) {
            if (critter instanceof Ghost && critter.getPos().round().equals(pacPos)) {
                if (PacMan.INSTANCE.isEnergized() && !((Ghost) critter).isEaten()) {
                    addScore(10);
                    ((Ghost) critter).setEaten(true);
                    if ((Ghost) critter == BLINKY) {
                        updateBlinky();
                    } else if ((Ghost) critter == PINKY) {
                        updatePinky();
                    } else if ((Ghost) critter == INKY) {
                        updateInky();
                    } else {
                        updateClyde();
                    }
                } else if (!((Ghost) critter).isEaten()) {
                    playerLost();
                    return;
                }
            }
        }
    }

    private void addScore(int increment) {
        score += increment;
        if (increment == 1) pacgomme++;
    }

    private void playerLost() {
        Music.playSound("fail.wav");//ajouter le son à la mort
        lives--;
        resetCritters();
    }

    public void playerWin() {
        win = true;
        resetCritters();
    }

    private void resetCritters() {
        for (var critter : critters)
            resetCritter(critter);
    }

    private void resetCritter(Critter critter) {
        if (critter instanceof Ghost) {
            ((Ghost) critter).resetGhostPos(this);
        } else {
            PacMan.INSTANCE.resetPacManPos(this);
        }
    }
    
    public MazeConfig getConfig() {
        return config;
    }

    public boolean getGridState(IntCoordinates pos) {
        return gridState[pos.y()][pos.x()];
    }

    public int getLives() {
        return lives;
    }

    public int getScore() {
        return score;
    }

    public int getTotalLive() {
        return totalLives;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public int getPacgomme() {
        return pacgomme;
    }

    public Map<Critter, RealCoordinates> getInitialPos() {
        return initialPos;
    }

    public List<Critter> getCritters() {
        return critters;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Mode getMode() {
        return mode;
    }

    // Tests

    public static MazeState getCurrentMaze() {
        return currentMaze;
    }
}