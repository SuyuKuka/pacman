package model;

import config.Cell;
import javafx.scene.Scene;
import config.MazeConfig;
import geometry.IntCoordinates;
import geometry.RealCoordinates;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static config.Cell.Content.ENERGIZER;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

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
    private final int totalLives;
    private int lives;

    private Scene gameScene;

    public MazeState(MazeConfig config) {
        this.config = config;
        // Ajouter difficulté dans config et faire un switch (difficulté) pour le nb de total live
        totalLives = 1;
        lives = totalLives;
        PacMan.INSTANCE.setPacManMoved(0);
        height = config.getHeight();
        width = config.getWidth();
        critters = List.of(PacMan.INSTANCE, CLYDE, BLINKY, INKY, PINKY);
        gridState = new boolean[height][width];
        setGrid();
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
        if (lives > 0) {
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
    }

    /**
     * La méthode arrête Pac-Man lorsqu'il rencontre un mur. Elle met aussi à jour
     * sa position lorsqu'il traverse une case pour faciliter les
     * virages.
     * 
     * @param deltaTns La variation du temps entre deux instants.
     */
    private void updatePacMan(long deltaTns) {
        // Si Pac-Man mange un ENERGIZER
        if (config.getCell(new IntCoordinates((int) PacMan.INSTANCE.getPos().x(), (int) PacMan.INSTANCE.getPos().y())).initialContent().equals(ENERGIZER)) {
            PacMan.INSTANCE.setEnergized(true);
        }
        var curPos = PacMan.INSTANCE.getPos();
        var nextPos = PacMan.INSTANCE.nextPos(deltaTns);

        // Nécessaire pour éviter les problèmes de détermination du target selon la direction de Pac-Man
        if (PacMan.INSTANCE.getDirection() != NONE)
            PacMan.INSTANCE.setLastDirection(PacMan.INSTANCE.getDirection());

        // Modifie ou non la direction de PacMan en fonction du terrain du buffer
        if (canPacManTurn(PacMan.INSTANCE.getBufferDirection(), deltaTns)) {
            nextPos = PacMan.INSTANCE.nextPos(deltaTns, PacMan.INSTANCE.getBufferDirection());
            PacMan.INSTANCE.setDirection(PacMan.INSTANCE.getBufferDirection());
            PacMan.INSTANCE.setBufferDirection(Direction.NONE);
        }
        nextPos = calibratePacManPos(curPos, nextPos);
        PacMan.INSTANCE.calibratePos(deltaTns, curPos, nextPos, height, width);
        PacMan.INSTANCE.updateBufferDurationTime(deltaTns);
    }

     /**
     * Méthode qui calcul la position à la frame suivante de PacMan en fonction de {@code Direction}. <br><br>
     * Si cette position est valable, change la {@code Direction} de PacMan sinon, garde l'ancienne {@code Direction}.
     * 
     * @param Maze MazeConfig utilisée.
     * @param direction Calcul par rapport à cette direction.
     * @param deltaTns Temps écoulée.
     * @return Retourne vraie si un changement de direction est possible, faux sinon.
     */
    public boolean canPacManTurn(Direction direction, long deltaTns) {
        if (PacMan.INSTANCE.getDirection() == direction) {
            return false;
        }
        var nextNeighbours = PacMan.INSTANCE.nextPos(deltaTns).intNeighbours();
        if (nextNeighbours.size() >= 4) {
            return false;
        }
        if (nextNeighbours.size() == 2) {
            if ((direction == Direction.NORTH || direction == Direction.SOUTH) && (int) PacMan.INSTANCE.getPos().x() != PacMan.INSTANCE.getPos().x()) {
                return false;
            }
            if ((direction == Direction.EAST || direction == Direction.WEST) && (int) PacMan.INSTANCE.getPos().y() != PacMan.INSTANCE.getPos().y()) {
                return false;
            }
            var curNeighbours = PacMan.INSTANCE.getPos().intNeighbours();
            for (var n: curNeighbours) {
                if (direction.wall(config.getCell(n))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Méthode qui arrête la course de PacMan et retire sa direction si il rencontre un mur de face.
     * @param curPos  {@code RealCoordinates} actuelle de {@code Pac-Man}.
     * @param nextPos {@code RealCoordinates} supposée (= s'il ne traverse pas de mur) de {@code Pac-Man}.
     * @return Retourne {@code nextPos} modifié ou non.
     */

    public RealCoordinates calibratePacManPos(RealCoordinates curPos, RealCoordinates nextPos) {
        if (!curPos.intNeighbours().containsAll(nextPos.intNeighbours())) {
            for (var n: curPos.intNeighbours()) {
                if (PacMan.INSTANCE.getDirection().wall(config.getCell(n))) {
                    RealCoordinates newPos = PacMan.INSTANCE.getPos().adjustCoordinates(PacMan.INSTANCE.getDirection());
                    PacMan.INSTANCE.setDirection(Direction.NONE);
                    return newPos;
                }
            }
        }
        return nextPos;
    }

    /**
     * La méthode initialise la variable {@code start} qui garde l'heure UNIX au
     * lancement de la partie (lorsque Pac-Man bouge pour la première fois),
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
     * La méthode met à jour les targets de chaque fantôme selon le mode courant.
     * Elle met aussi à jour la direction de chaque fantôme
     * lorsqu'il entre dans une intersection. {@code INKY} et {@code CLYDE} ne
     * peuvent bouger seulement lorsqu'un score a été atteint.
     * 
     * @param deltaTns La variation du temps entre deux instants.
     */
    private void updateGhost(long deltaTns) {
        for (Critter critter : critters) {
            if (critter instanceof Ghost) critter.calibratePos(deltaTns, critter.getPos(), critter.nextPos(deltaTns), height, width);
            RealCoordinates pos = critter.getPos();

            // Lorsque le fantôme mangé retourne au spawn
            if (critter instanceof Ghost && ((Ghost) critter).isEaten() && pos.equals(new IntCoordinates(8, 10).toRealCoordinates(1.0))) {
                ((Ghost) critter).setEaten(false);
            }

            // Activation d'Inky à partir de 30 points
            if (critter == INKY && !INKY.isActivated() && score == 30) {
                INKY.setActivated(true);
                critter.setDirection(EAST);
                // Activation de Clyde lorsqu'un tier des points totaux a été obtenu
            } else if (critter == CLYDE && !CLYDE.isActivated() && score == 146 / 3) {
                CLYDE.setActivated(true);
                critter.setDirection(WEST);
                // Set direction aléatoire en FRIGHTENED mode
            } else if (critter instanceof Ghost && !((Ghost) critter).isEaten() && mode == Mode.FRIGHTENED && pos.x() % 1 == 0 && pos.y() % 1 == 0) {
                frightenedMode((Ghost) critter);
                // Set direction la plus proche du target
            } else if (critter instanceof Ghost && ((Ghost) critter).isActivated() && pos.x() % 1 == 0 && pos.y() % 1 == 0) {
                critter.setDirection(((Ghost) critter).directionClosestToTarget(config));
            }
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
            reverseGhostsDirection();
            mode = Mode.FRIGHTENED;
            // Changement du mode selon le temps de jeu
        } else if (!PacMan.INSTANCE.isEnergized()) {
            long now = Instant.now().toEpochMilli();
            long startMilli = start.toEpochMilli();
            
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
                    now > startMilli + (7 + 20 + 7 + 20 + 5 + 20) * 1000
                            && now <= startMilli + (7 + 20 + 7 + 20 + 5 + 20 + 5) * 1000) {
                if (mode == Mode.CHASE) {
                    Ghost.reverseGhostsDirection();
                }
                mode = Mode.SCATTER;
                scatterMode();
            }
        }
    }

    /**
     * La méthode met à jour les targets de chaque fantôme dans un coin de la map
     * qui leur est dédié durant le {@code SCATTER} mode.
     */
    private void scatterMode() {
        mode = Mode.SCATTER;
        BLINKY.setTarget(new IntCoordinates(16, 0).toRealCoordinates(1.0));
        INKY.setTarget(new IntCoordinates(16, 21).toRealCoordinates(1.0));
        PINKY.setTarget(new IntCoordinates(0, 0).toRealCoordinates(1.0));
        CLYDE.setTarget(new IntCoordinates(0, 21).toRealCoordinates(1.0));
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
    private void frightenedMode(Ghost ghost) {
        if (ghost.isActivated()) {
            ghost.setRandomDirection(config);
        }
    }

    // FIXME Pac-Man rules should somehow be in Pacman class (mettre updateScore et
    // updateCollision dans PacMan ?)

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

    private void setGrid(){
        Cell[][] maze = MazeConfig.getMaze("src/main/java/config/Level1.txt");
        for (int i=0;i<getHeight();i++) {
            for (int j=0;j<getWidth();j++){
                if(maze[i][j].initialContent()!=Cell.Content.valueOf("DOT")){
                    gridState[i][j]=true;
                }
            }
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
    }

    private void playerLost() {
        lives--;
        resetCritters();
    }

    private void resetCritter(Critter critter) {
        if (critter == BLINKY || critter == INKY && score >= 30)
            critter.setDirection(EAST);
        else if (critter == PINKY)
            critter.setDirection(NORTH);
        else if (critter == CLYDE && score >= 146 / 3)
            critter.setDirection(WEST);
        else
            critter.setDirection(NONE);
        critter.setPos(initialPos.get(critter));
    }

    private void resetCritters() {
        for (var critter : critters)
            resetCritter(critter);
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

    public Scene getGameScene() {
        return gameScene;
    }

    public int getTotalLive() {
        return totalLives;
    }
}