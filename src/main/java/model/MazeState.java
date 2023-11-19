package model;

import config.MazeConfig;
import geometry.RealCoordinates;
import gui.CritterGraphicsFactory;
import utils.Music;

public final class MazeState {
    /** Liste des modes. */
    public enum GhostState {
        /** Chasse Pac-Man. */
        CHASE,
        /** Chacun dans un coin. */
        SCATTER,
        /** Effrayé. */
        FRIGHTENED
    }

    //CHECKSTYLE:OFF
    private static final double NANOSECONDS = Math.pow(10, 9);
    //CHECKSTYLE:ON

    /** MazeState actuel pour les tests unitaires. */
    private static MazeState currentMaze;

    /** {@link MazeConfig#config} actuel. */
    private final MazeConfig config;

    private static final int TOTAL_LIVES = 3;
    private int lives;

    /** {@link GhostState} actuel. */
    private GhostState ghostState = GhostState.SCATTER;
    /** Chronomètre depuis le début de la partie. */
    private double totalTime = 0.0;
    private Double currentLiveTime;
    private int score = 0;
    private int eatenPacgomme = 0;

    private boolean win = false;
    /**
     * Constructeur qui initialise les infos de la partie.
     * @param newConfig {@link MazeConfig#config} Configuration utilisée
     */
    public MazeState(final MazeConfig newConfig) {
        config = newConfig;
        config.getPacman().resetPacMan();
        lives = TOTAL_LIVES;
    }

    /**
     * Méthode appelée à chaque tick pour mettre à jour l'état de la carte.
     * @param deltaTns Durée d'un tick.
     */
    public void update(final long deltaTns) {
        if (lives > 0 && !win) {

            config.getPacman().update(this, deltaTns);
            Direction pacman1Dir = config.getPacman().getDirection();
            Direction pacman2Dir
            = config.isCoop() ? config.getPacman2().getDirection() : Direction.NONE;

            if (config.isCoop()) {
                config.getPacman2().update(this, deltaTns);
            }

            if (currentLiveTime == null
            && (pacman1Dir != Direction.NONE || pacman2Dir != Direction.NONE)) {
                currentLiveTime = 0.0;
                config.activateGhost();
            } else if (currentLiveTime != null) {
                updateGhost(deltaTns);
                currentLiveTime += deltaTns / NANOSECONDS;
                updateCollision();
            }
        }
    }

    /**
     * Met à jour les paramètres des {@link Ghost}.
     * @param deltaTns Tick par temps.
     */
    public void updateGhost(final long deltaTns) {
        for (Ghost ghost : config.getGhostList()) {
            ghost.update(this, deltaTns);
        }
        energizedCheck();
    }

    /**
     * Méthode qui actualise l'état actuel du jeu en fonction de si pacman est énergisé.
     */
    public void energizedCheck() {
        final boolean setStateFrightened;
        final boolean removeBGM;
        if (config.isCoop()) {
            setStateFrightened
            = config.getPacman().isEnergized() || config.getPacman2().isEnergized()
            && ghostState != GhostState.FRIGHTENED;
            removeBGM
            = !config.getPacman().isEnergized() && !config.getPacman2().isEnergized()
            && ghostState == GhostState.FRIGHTENED;
        } else {
            setStateFrightened
            = config.getPacman().isEnergized() && ghostState != GhostState.FRIGHTENED;
            removeBGM
            = !config.getPacman().isEnergized() && ghostState == GhostState.FRIGHTENED;
        }

        if (setStateFrightened) {
            Music.playSound("energizer");
            Music.playLoopingBgMusic("energized");
            ghostState = GhostState.FRIGHTENED;
        } else if (removeBGM) {
            Music.stopBackgroundMusic();
        }
    }

    /**
     * La méthode appelle la méthode {@link #playerLost} lorsque Pac-Man est au
     * contact d'un fantôme. Si Pac-Man est {@link PacMan#energized}, le score
     * augmente de 10 et le fantôme revient à sa position initiale.
     */
    private void updateCollision() {
        final double collisionRange = 0.7;
        final int eatScore = 200;
        final RealCoordinates pacmanPos = config.getPacman().getPos();
        RealCoordinates pacman2Pos = new RealCoordinates(0, 0);
        if (config.isCoop()) {
            pacman2Pos = config.getPacman2().getPos();
        }

        for (Ghost ghost : config.getGhostList()) {
            final RealCoordinates ghostPos = ghost.getPos();
            if (Math.abs(ghostPos.x() - pacmanPos.x()) <= collisionRange
            && Math.abs(ghostPos.y() - pacmanPos.y()) <= collisionRange
            || Math.abs(ghostPos.x() - pacman2Pos.x()) <= collisionRange
            && Math.abs(ghostPos.y() - pacman2Pos.y()) <= collisionRange) {
                if (!ghost.isEaten() && config.getPacman().isEnergized()) {
                    addScore(eatScore);
                    ghost.setEaten(true);
                    ghost.setTarget(ghost.getEatenTarget());
                } else if (!ghost.isEaten()) {
                    playerLost();
                    return;
                }
            }
        }
    }

    /**
     * Renvoie {@link GhostState#SCATTER} ou {@link GhostState#CHASE} en fonction du temps de jeu.
     * @return {@link GhostState#SCATTER} ou {@link GhostState#CHASE}
     */
    public GhostState getCurrentState() {
        double time = totalTime + currentLiveTime;
        final int[] stateSwap = config.getGhostStateSwap();
        for (int i = 0; i < stateSwap.length; i++) {
            time -= stateSwap[i];
            if (time < 0) {
                if (i % 2 == 0) {
                    return GhostState.SCATTER;
                } else {
                    break;
                }
            }
        }
        return GhostState.CHASE;
    }

    /**
     * Incrémente le score du jeu par la valeur donnée en paramètre : <br><br>
     * 10 : Dot <br> <br>
     * 200 * nbre de fantôme mangé durant une même frénésie : Fantôme (à implémenter) <br><br>
     * + fruits à faire.
     * @param increment
     */
    public void addScore(final int increment) {
        final int dotEatenScore = 10;
        score += increment;
        if (increment == dotEatenScore) {
            eatenPacgomme++;
        }
    }

    private void playerLost() {
        lives--;
        totalTime += currentLiveTime;
        currentLiveTime = null;
        if (lives == 0) {
            Music.stopBackgroundMusic();
            Music.playSound("gameOver");
        } else {
            Music.playSound("fail");
        }
        resetCritters();
    }

    /**
     * Méthode appelée lorsque le joueur a gagné.
     * Réinitialise la position des critters.
     */
    public void playerWin() {
        win = true;
        totalTime += currentLiveTime;
        resetCritters();
    }

    private void resetCritters() {
        for (Critter critter : config.getCritters()) {
            resetCritter(critter);
        }
    }

    private void resetCritter(final Critter critter) {
        if (critter instanceof Ghost) {
            ((Ghost) critter).setPos(((Ghost) critter).getSpawnPos());
            ((Ghost) critter).setEaten(false);
            if (((Ghost) critter).getDirectionBuffer() != null) {
                ((Ghost) critter).getDirectionBuffer().setBufferDirection(Direction.NONE);
                critter.setDirection(Direction.NONE);
            }
        } else {
            config.getPacman().resetPacMan();
            if (config.isCoop()) {
                config.getPacman2().resetPacMan();
            }
            CritterGraphicsFactory.setResetPacImg(true);
        }
    }

    public static MazeState getCurrentMaze() {
        return currentMaze;
    }

    public MazeConfig getConfig() {
        return config;
    }


    public int getTotalLives() {
        return TOTAL_LIVES;
    }

    public int getLives() {
        return lives;
    }

    public GhostState getGhostState() {
        return ghostState;
    }

    public int getScore() {
        return score;
    }

    public int getEatenPacgomme() {
        return eatenPacgomme;
    }

    public void setGhostState(final GhostState ghostState) {
        this.ghostState = ghostState;
    }

    /**
     * Retourne la largeur de la grille.
     * @return {@code int}largeur
     */
    public int getWidth() {
        return config.getGrid()[0].length;
    }

    /**
     * Retourne la hauteur de la grille.
     * @return {@code int}hauteur
     */
    public int getHeight() {
        return config.getGrid().length;
    }
}
