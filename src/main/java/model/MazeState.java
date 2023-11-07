package model;

import config.Cell;
import config.MazeConfig;
import geometry.IntCoordinates;
import geometry.RealCoordinates;
import gui.CritterGraphicsFactory;
import utils.Music;

import java.util.List;
import java.util.Map;

import static model.Ghost.BLINKY;
import static model.Ghost.PINKY;
import static model.Ghost.INKY;
import static model.Ghost.CLYDE;

public final class MazeState {
    /** MazeState actuel pour les tests unitaires. */
    private static MazeState currentMaze;

    /** {@link MazeConfig#config} actuel. */
    private final MazeConfig config;
    /** Hauteur de la map. */
    private final int height;
    /** Largeur de la map. */
    private final int width;

    /**
     * Tableau contenant des booleans indiquant
     * l'état des pac-gommes sur la carte.
     */
    private final boolean[][] gridState;

    /** Liste des modes. */
    public enum Mode {
        /** Chasse Pac-Man. */
        CHASE,
        /** Chacun dans un coin. */
        SCATTER,
        /** Effrayé. */
        FRIGHTENED
    }

    /** {@link Mode} actuel. */
    private Mode mode;
    /** Chronomètre depuis le début de la partie. */
    private long modeTimer;

    /** Liste des{@code Critter}. */
    private final List<Critter> critters;
    /** Score actuel. */
    private int score;

    /** Position initial de chaque{@code Critter}. */
    private final Map<Critter, RealCoordinates> initialPos;
    /** Vies totales. */
    private final int totalLives = 3;
    /** Vies actuelles. */
    private int lives;
    /** True si gagné. */
    private boolean win = false;
    /** Score maximum. */
    private int maxScore = 0;
    /** Nombre de pac-gommes mangés. */
    private int pacgomme = 0;

    private boolean isEnergizedPlaying = false;
    /**
     * Constructeur qui initialise les infos de la partie.
     * @param newConfig {@link MazeConfig#config} Configuration utilisée
     */
    public MazeState(final MazeConfig newConfig) {
        config = newConfig;
        PacMan.INSTANCE.resetPacMan();
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
                PINKY, config.getPinkyPos().toRealCoordinates(1.0));
        resetCritters();
        // currentMaze = this;
    }

    /**
     * Méthode appelée à chaque tick pour mettre à jour l'état de la carte.
     * @param deltaTns Durée d'un tick.
     */
    public void update(final long deltaTns) {
        if (lives > 0 && !win) {
            PacMan.INSTANCE.updatePacMan(deltaTns, this.getConfig());
            // Active les fantômes après la première input
            if (PacMan.INSTANCE.getPacManMoved() == 1) {
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
            if (PacMan.INSTANCE.getPacManMoved() == -1) {
                final double milli = Math.pow(10, 6);
                modeTimer += deltaTns / milli;
            }
        }
    }

    /**
     * La méthode met à jour le mode selon le temps de jeu.
     * A chaque changement de mode sauf pour la fin
     * du {@link Mode#FRIGHTENED}, les fantômes se retournent.
     */
    private void updateMode() {
        // Pac-Man est ENERGIZED, changement du mode en FRIGHTENED
        if (PacMan.INSTANCE.isEnergized() && mode != Mode.FRIGHTENED) {
            Music.stopAllSounds();
            Music.playSound("energizer", () -> Music.playLoopingBgMusic("energized"));
            isEnergizedPlaying = true;

            Ghost.reverseGhostsDirection(config, critters);
            mode = Mode.FRIGHTENED;
            // Changement du mode selon le temps de jeu
        } else if (!PacMan.INSTANCE.isEnergized()) {
            if (isEnergizedPlaying) {
                Music.playLoopingBgMusic("siren_1");
                isEnergizedPlaying = false;
            }

            final int chase1 = 7;
            final int scatter1 = 27;
            final int chase2 = 34;
            final int scatter2 = 54;
            final int chase3 = 59;
            final int scatter3 = 79;
            final int chase4 = 84;
            final int milli = 1000;

            if (modeTimer < chase1 * milli
                || modeTimer > scatter1 * milli
                    && modeTimer <= chase2 * milli
                || modeTimer > scatter2 * milli
                    && modeTimer <= chase3 * milli
                || modeTimer > scatter3 * milli
                    && modeTimer <= chase4 * milli
                ) {
                if (mode == Mode.CHASE) {
                    Ghost.reverseGhostsDirection(config, critters);
                }
                scatterMode();
            } else if (modeTimer > chase1 * milli
                || modeTimer > chase2 * milli
                || modeTimer > chase3 * milli
                || modeTimer > chase4 * milli
                ) {
                if (mode == Mode.SCATTER) {
                    Ghost.reverseGhostsDirection(config, critters);
                }
                chaseMode();
            }
        }
    }

    /**
     * La méthode met à jour les targets de chaque fantôme
     * dans un coin de la map qui leur est dédié durant le {@link Mode#SCATTER}.
     */
    private void scatterMode() {
        mode = Mode.SCATTER;
        Ghost.setScatterTarget();
    }

    /**
     * La méthode met à jour les targets de chaque fantôme
     * par rapport à Pac-Man en {@link Mode#CHASE}.
     */
    private void chaseMode() {
        mode = Mode.CHASE;
        Ghost.updateBlinky();
        Ghost.updateInky();
        Ghost.updatePinky();
        Ghost.updateClyde();
    }

    /**
     * La méthode met à jour les directions de chaque fantôme "aléatoirement" en
     * {@link Mode#FRIGHTENED} mode.
     * @param ghost Fantôme
     */
    public void frightenedMode(final Ghost ghost) {
        if (ghost.isActivated() && !ghost.isEaten()) {
            ghost.setRandomDirection(this);
        }
    }

    /**
     * La méthode met à jour l'état du gridState.
     * @param maze {@link MazeConfig#grid}
     */
    private void setGrid(final Cell[][] maze) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (maze[i][j].initialContent() == Cell.Content.DOT
                    || maze[i][j].initialContent() == Cell.Content.ENERGIZER
                    ) {
                    maxScore++;
                }
                if (maze[i][j].initialContent() == Cell.Content.NOTHING
                    || maze[i][j].initialContent() == Cell.Content.RESTRICTED
                    || maze[i][j].initialContent() == Cell.Content.TUNNEL
                    || maze[i][j].initialContent() == Cell.Content.SPAWN) {
                    gridState[i][j] = true;
                }
            }
        }
    }

    // FIXME Pac-Man rules should somehow be in Pacman class
    // (mettre updateScore et updateCollision dans PacMan ?)

    /**
     * La méthode incrémente le score de 1 après que Pac-Man mange un pac-gomme.
     */
    private void updateScore() {
        var pacPos = PacMan.INSTANCE.getPos().round();
        if (!gridState[pacPos.y()][pacPos.x()]) {
            Music.playSound("eat");
            addScore(1);
            gridState[pacPos.y()][pacPos.x()] = true;
        }
    }

    /**
     * La méthode appelle la méthode {@link #playerLost} lorsque Pac-Man est au
     * contact d'un fantôme. Si Pac-Man est {@link PacMan#energized}, le score
     * augmente de 10 et le fantôme revient à sa position initiale.
     */
    private void updateCollision() {
        double pacPosX = PacMan.INSTANCE.getPos().x();
        double pacPosY = PacMan.INSTANCE.getPos().y();
        final double range = 0.4;
        final int eatScore = 10;
        for (var critter : critters) {
            if (critter instanceof Ghost
                    && Math.abs(pacPosX - critter.getPos().x()) <= range
                    && Math.abs(pacPosY - critter.getPos().y()) <= range) {
                if (PacMan.INSTANCE.isEnergized()
                    && !((Ghost) critter).isEaten()
                    ) {
                    addScore(eatScore);
                    ((Ghost) critter).setEaten(true);
                    if ((Ghost) critter == BLINKY) {
                        Ghost.updateBlinky();
                    } else if ((Ghost) critter == PINKY) {
                        Ghost.updatePinky();
                    } else if ((Ghost) critter == INKY) {
                        Ghost.updateInky();
                    } else {
                        Ghost.updateClyde();
                    }
                } else if (!((Ghost) critter).isEaten()) {
                    playerLost();
                    return;
                }
            }
        }
    }

    private void addScore(final int increment) {
        score += increment;
        if (increment == 1) {
            pacgomme++;
        }
    }

    private void playerLost() {
        lives--;
        if (lives == 0) {
            Music.stopAllSounds();
            Music.playSound("gameOver");
        } else {
            Music.playSound("fail");
            isEnergizedPlaying = false;
        }
        resetCritters();
    }

    /**
     * Méthode appelée lorsque le joueur a gagné.
     * Réinitialise la position des critters.
     */
    public void playerWin() {
        win = true;
        resetCritters();
    }

    private void resetCritters() {
        for (var critter : critters) {
            resetCritter(critter);
        }
    }

    private void resetCritter(final Critter critter) {
        if (critter instanceof Ghost) {
            ((Ghost) critter).resetGhostPos(this);
        } else {
            PacMan.INSTANCE.resetPacManPos(this);
            CritterGraphicsFactory.setResetPacImg(true);
        }
    }

    /**
     * Retourne la{@code MazeConfig} actuelle.
     * @return {@code MazeConfig}
     */
    public MazeConfig getConfig() {
        return config;
    }

    /**
     * Retourne true si la cellule n'a plus de pac-gomme.
     * @param pos Position de la cellule
     * @return {@code boolean}
     */
    public boolean getGridState(final IntCoordinates pos) {
        return gridState[pos.y()][pos.x()];
    }

    /**
     * Retourne le nombre actuel de vies.
     * @return {@code int}
     */
    public int getLives() {
        return lives;
    }

    /**
     * Retourne le score actuel.
     * @return {@code int}
     */
    public int getScore() {
        return score;
    }

    /**
     * Retourne le nombre de vies initial.
     * @return {@code int}
     */
    public int getTotalLive() {
        return totalLives;
    }

    /**
     * Retourne le nombre maximal de pac-gommes.
     * @return {@code int}
     */
    public int getMaxScore() {
        return maxScore;
    }

    /**
     * Retourne le nombre de pac-gommes mangés.
     * @return {@code int}
     */
    public int getPacgomme() {
        return pacgomme;
    }

    /**
     * Retourne la{@code Map<Critter, RealCoordinates>}contenant
     * les positions initiales des {@Code Critter}.
     * @return {@code Map<Critter, RealCoordinates>}
     */
    public Map<Critter, RealCoordinates> getInitialPos() {
        return initialPos;
    }

    /**
     * Retourne la liste des{@code Critter}.
     * @return {@code List<Critter>}
     */
    public List<Critter> getCritters() {
        return critters;
    }

    /**
     * Retourne la largeur de la map.
     * @return {@code int}
     */
    public int getWidth() {
        return width;
    }

    /**
     * Retourne la hauteur de la map.
     * @return {@code int}
     */
    public int getHeight() {
        return height;
    }

    /**
     * Retourne le {@link Mode} actuel.
     * @return {@link Mode}
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * Retourne le{@code MazeState}actuel pour les tests unitaires.
     * @return {@code MazeState}
     */
    public static MazeState getCurrentMaze() {
        return currentMaze;
    }
}
