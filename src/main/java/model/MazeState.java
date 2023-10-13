package model;

import gui.App;
import gui.GameView;
import gui.PacmanController;
import config.MazeConfig;
import geometry.IntCoordinates;
import geometry.RealCoordinates;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import model.Direction;
import model.PacMan;

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
    private int lives = 3;

    private Scene gameScene;
    private Text text_score;
    private Group root = new Group();
    private Group livegroup1 = new Group();
    private Group livegroup2 = new Group();
    private Group livegroup3 = new Group();
    private int select = 1;
    private boolean lose;
    private PacmanController testtesController = new PacmanController();
    private Group Interface = new Group();
    private Rectangle Transparent;
    private Rectangle TextPos;
    private Text GameOver = new Text("GameOver");
    private Text Continue = new Text("Continue");
    private Text Retry = new Text("Retry");
    private Text Menu = new Text("Quit");
    private Text Option1 = new Text(">");
    private Text Option2 = new Text(">");
    private Text Option3 = new Text(">");
    private boolean retry = false;

    public MazeState(MazeConfig config, Scene gameScene) {
        this.config = config;
        this.gameScene = gameScene;

        height = config.getHeight();
        width = config.getWidth();
        critters = List.of(PacMan.INSTANCE, CLYDE, BLINKY, INKY, PINKY);
        gridState = new boolean[height][width];
        initialPos = Map.of(
                PacMan.INSTANCE, config.getPacManPos().toRealCoordinates(1.0),
                BLINKY, config.getBlinkyPos().toRealCoordinates(1.0),
                INKY, config.getInkyPos().toRealCoordinates(1.0),
                CLYDE, config.getClydePos().toRealCoordinates(1.0),
                PINKY, config.getPinkyPos().toRealCoordinates(1.0));
        resetCritters();
        displayLive();
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
        var curPos = PacMan.INSTANCE.getPos();
        var nextPos = PacMan.INSTANCE.nextPos(deltaTns);

        // Nécessaire pour éviter les problèmes de détermination du target selon la
        // direction de Pac-Man
        if (PacMan.INSTANCE.getDirection() != NONE)
            PacMan.INSTANCE.setLastDirection(PacMan.INSTANCE.getDirection());

        if (updatePacManDirection(deltaTns)) {
            nextPos = PacMan.INSTANCE.nextPos(deltaTns, PacMan.INSTANCE.getBufferDirection());
        }
        nextPos = updatePacManPos(curPos, nextPos);
        PacMan.INSTANCE.calibratePos(deltaTns, curPos, nextPos, height, width);
        PacMan.INSTANCE.increaseBufferDurationTime(deltaTns);
    }

    /**
     * Méthode qui actualise la {@code Direction} de {@code Pac-Man} en fonction du
     * terrain et de son {@code bufferDirection}.
     * 
     * @param deltaTns Temps écoulé en nanosecondes.
     * @return Retourne vraie si la {@code Direction} de {@code Pac-Man} devrait
     *         changer ou faux sinon.
     */
    public boolean updatePacManDirection(long deltaTns) {
        var curNeighbours = PacMan.INSTANCE.getPos().intNeighbours();
        var nextNeighbours = PacMan.INSTANCE.nextPos(deltaTns).intNeighbours();
        if (PacMan.INSTANCE.getDirection() != PacMan.INSTANCE.getBufferDirection() && PacMan.INSTANCE.getBufferDirection() != Direction.NONE) {
            boolean isBufferUsed = !(nextNeighbours.size() >= 4);
            if (isBufferUsed) {
                switch (PacMan.INSTANCE.getBufferDirection()) {
                case NORTH -> {
                    for (var n: curNeighbours) { 
                        if (config.getCell(n).northWall()) { 
                            isBufferUsed = false;
                            break;
                        }
                    }
                }
                case EAST -> {
                    for (var n: curNeighbours) {
                        if (config.getCell(n).eastWall()) {
                            isBufferUsed = false;
                        break;
                        }
                    }
                }
                case SOUTH -> {
                    for (var n: curNeighbours) {
                        if (config.getCell(n).southWall()) {
                            isBufferUsed = false;
                            break;
                        }
                    }
                }
                case WEST -> {
                    for (var n: curNeighbours) {
                        if (config.getCell(n).westWall()) { 
                            isBufferUsed = false;
                            break;
                        }
                    }
                }
                default -> {
                    break;
                }
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
     * Méthode qui actualise la position ({@code RealCoordinates}) de
     * {@code Pac-Man} en fonction du terrain.
     * 
     * @param curPos  {@code RealCoordinates} actuelle de {@code Pac-Man}.
     * @param nextPos {@code RealCoordinates} supposée (si il ne traverse pas de
     *                mur) de {@code Pac-Man}.
     * @return Retourne un {@code RealCoordinates} (modifié ou non).
     */
    public RealCoordinates updatePacManPos(RealCoordinates curPos, RealCoordinates nextPos) {
        var curNeighbours = curPos.intNeighbours();
        var nextNeighbours = nextPos.intNeighbours();
        if (!curNeighbours.containsAll(nextNeighbours)) { // the critter would overlap new cells. Do we allow it?
            switch (PacMan.INSTANCE.getDirection()) {
                case NORTH -> {
                    for (var n : curNeighbours)
                        if (config.getCell(n).northWall()) {
                            PacMan.INSTANCE.setDirection(Direction.NONE);
                            return nextPos = curPos.floorY();
                        }
                }
                case EAST -> {
                    for (var n : curNeighbours)
                        if (config.getCell(n).eastWall()) {
                            PacMan.INSTANCE.setDirection(Direction.NONE);
                            return nextPos = curPos.ceilX();
                        }
                }
                case SOUTH -> {
                    for (var n : curNeighbours)
                        if (config.getCell(n).southWall()) {
                            PacMan.INSTANCE.setDirection(Direction.NONE);
                            return nextPos = curPos.ceilY();
                        }
                }
                case WEST -> {
                    for (var n : curNeighbours)
                        if (config.getCell(n).westWall()) {
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
            if (critter instanceof Ghost)
                critter.calibratePos(deltaTns, critter.getPos(), critter.nextPos(deltaTns), height, width);
            RealCoordinates pos = critter.getPos();

            // Activation d'Inky à partir de 30 points
            if (critter == INKY && !INKY.isActivated() && score == 30) {
                INKY.setActivated(true);
                critter.setDirection(EAST);
                // Activation de Clyde lorsqu'un tier des points totaux a été obtenu
            } else if (critter == CLYDE && !CLYDE.isActivated() && score == 146 / 3) {
                CLYDE.setActivated(true);
                critter.setDirection(WEST);
                // Set direction aléatoire en FRIGHTENED mode
            } else if (critter instanceof Ghost && mode == Mode.FRIGHTENED && pos.x() % 1 == 0 && pos.y() % 1 == 0) {
                frightenedMode((Ghost) critter, dir);
                // Set direction la plus proche du target
            } else if (critter instanceof Ghost && ((Ghost) critter).isActivated() && pos.x() % 1 == 0
                    && pos.y() % 1 == 0) {
                critter.setDirection(directionClosestToTarget(config, (Ghost) critter, pos, dir));
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

    /**
     * La méthode appelle la méthode {@code playerList} lorsque Pac-Man est au
     * contact d'un fantôme. Si Pac-Man est {@code ENERGIZED}, le score
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
        if (text_score == null) {
            // Création d'un texte
            text_score = new Text();
            // Position du score // 30 = scale, 2 et 6 valeur arbitraire
            text_score.setX((width + height) * 30 / 2);
            text_score.setY((height + width) * 30 / 6);
            // Configuration du texte
            text_score.setFont(Font.loadFont("file:src\\main\\resources\\fonts\\slkscr.ttf", 24));
            text_score.setFill(Color.WHITE);
            // Ajoute le texte au groupe root
            root.getChildren().add(text_score);
            // Met le group dans la scene
            ((Pane) gameScene.getRoot()).getChildren().add(root);
        }
        // Affiche le score
        text_score.setText("Score: " + score); // Total : 154 score en classique
    }

    private ImageView setLive() {
        ImageView imageView = new ImageView();
        Image image = new Image("file:src\\main\\resources\\pacman.png");
        imageView.setImage(image);
        imageView.setY((height + width) * 30 / 2 - 50);
        imageView.setFitWidth(width * 1.4);
        imageView.setPreserveRatio(true);
        return imageView;
    }

    public void displayLive() {
        ImageView live_1 = setLive();
        ImageView live_2 = setLive();
        ImageView live_3 = setLive();
        live_1.setX(((width + height + 0) * 30 / 2));
        live_2.setX(((width + height + 1.9) * 30 / 2));
        live_3.setX(((width + height + 3.8) * 30 / 2));

        livegroup1.getChildren().add(live_1);
        livegroup2.getChildren().add(live_2);
        livegroup3.getChildren().add(live_3);

        if (!((Pane) gameScene.getRoot()).getChildren().contains(livegroup1)) {
            ((Pane) gameScene.getRoot()).getChildren().add(livegroup1);
        }

        if (!((Pane) gameScene.getRoot()).getChildren().contains(livegroup2)) {
            ((Pane) gameScene.getRoot()).getChildren().add(livegroup2);
        }

        if (!((Pane) gameScene.getRoot()).getChildren().contains(livegroup3)) {
            ((Pane) gameScene.getRoot()).getChildren().add(livegroup3);
        }
    }

    public void GameOver() {
        Transparent = new Rectangle(0, 0, height * 100, width * 100);
        Rectangle TextPos = new Rectangle(0, 0, height * 30, width * 30);
        lose = true;

        Transparent.setFill(Color.BLACK);
        Transparent.setOpacity(0.4);

        // TextPos.setFill(Color.WHITE);
        TextPos.setOpacity(0);

        GameOver.setX(TextPos.getWidth() / 4 + 42);
        GameOver.setY(200);
        GameOver.setFont(Font.loadFont("file:src\\main\\resources\\fonts\\small_pixel-7.ttf", 120));
        GameOver.setFill(Color.WHITE);

        Text Score = new Text("Score: " + score);
        Score.setX(TextPos.getWidth() / 3 + GameOver.getX() / 4 + 10);
        Score.setY(280);
        Score.setFont(Font.loadFont("file:src\\main\\resources\\fonts\\small_pixel-7.ttf", 75));
        Score.setFill(Color.WHITE);

        Continue.setX(TextPos.getWidth() / 3 + GameOver.getX() / 2 + 24);
        Continue.setY(400);
        Continue.setFont(Font.loadFont("file:src\\main\\resources\\fonts\\small_pixel-7.ttf", 42));
        Continue.setFill(Color.WHITE);

        Retry.setX(TextPos.getWidth() / 3 + GameOver.getX() / 2 + 42);
        Retry.setY(440);
        Retry.setFont(Font.loadFont("file:src\\main\\resources\\fonts\\small_pixel-7.ttf", 42));
        Retry.setFill(Color.WHITE);

        Menu.setX(TextPos.getWidth() / 3 + GameOver.getX() / 2 + 56);
        Menu.setY(480);
        Menu.setFont(Font.loadFont("file:src\\main\\resources\\fonts\\small_pixel-7.ttf", 42));
        Menu.setFill(Color.WHITE);

        Option1.setX(TextPos.getWidth() / 3 + GameOver.getX() / 2 + 5);
        Option1.setY(400);
        Option1.setFont(Font.loadFont("file:src\\main\\resources\\fonts\\small_pixel-7.ttf", 42));
        Option1.setFill(Color.WHITE);

        Option2.setX(TextPos.getWidth() / 3 + GameOver.getX() / 2 + 5);
        Option2.setY(440);
        Option2.setFont(Font.loadFont("file:src\\main\\resources\\fonts\\small_pixel-7.ttf", 42));
        Option2.setFill(Color.WHITE);

        Option3.setX(TextPos.getWidth() / 3 + GameOver.getX() / 2 + 5);
        Option3.setY(480);
        Option3.setFont(Font.loadFont("file:src\\main\\resources\\fonts\\small_pixel-7.ttf", 42));
        Option3.setFill(Color.WHITE);

        if (Interface.getChildren().isEmpty()) {
            Interface.getChildren().addAll(Transparent, TextPos, GameOver, Score, Continue, Retry, Menu, Option1);
        }

        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            Interface.getChildren().clear(); // Supprimer les anciens éléments
            if (!retry) {
                if (keyEvent.getCode() == KeyCode.UP) {
                    // Flèche vers le haut
                    select--;
                    if (select == 0 || select == -1) {
                        select = 3;
                    }
                    if (select == 1) {
                        Interface.getChildren().addAll(Transparent, TextPos, GameOver, Score, Continue, Retry, Menu,
                                Option1);
                    }
                    if (select == 2) {
                        Interface.getChildren().addAll(Transparent, TextPos, GameOver, Score, Continue, Retry, Menu,
                                Option2);
                    }
                    if (select == 3) {
                        Interface.getChildren().addAll(Transparent, TextPos, GameOver, Score, Continue, Retry, Menu,
                                Option3);
                    }
                } else if (keyEvent.getCode() == KeyCode.DOWN) {
                    // Flèche vers le bas
                    select++;
                    if (select == 4 || select == 5) {
                        select = 1;
                    }
                    if (select == 1) {
                        Interface.getChildren().addAll(Transparent, TextPos, GameOver, Score, Continue, Retry, Menu,
                                Option1);
                    }
                    if (select == 2) {
                        Interface.getChildren().addAll(Transparent, TextPos, GameOver, Score, Continue, Retry, Menu,
                                Option2);
                    }
                    if (select == 3) {
                        Interface.getChildren().addAll(Transparent, TextPos, GameOver, Score, Continue, Retry, Menu,
                                Option3);
                    }
                } else if (keyEvent.getCode() == KeyCode.ENTER) {
                    if (select == 1) {
                        // Continue sur le prochain niveau
                    }
                    if (select == 2) {
                        // Retry le jeu
                        /*
                         * lives = 3;
                         * retry = true;
                         * update(0);
                         */
                    }
                    if (select == 3) {
                        // Retourne au menu
                    }
                } else {
                    if (select == 1) {
                        Interface.getChildren().addAll(Transparent, TextPos, GameOver, Score, Continue, Retry, Menu,
                                Option1);
                    }
                    if (select == 2) {
                        Interface.getChildren().addAll(Transparent, TextPos, GameOver, Score, Continue, Retry, Menu,
                                Option2);
                    }
                    if (select == 3) {
                        Interface.getChildren().addAll(Transparent, TextPos, GameOver, Score, Continue, Retry, Menu,
                                Option3);
                    }
                }
            }
        });

        // Interface.getChildren().addAll(Transparent, TextPos, GameOver, Score, Retry,
        // Menu, Option1);

        ((Pane) gameScene.getRoot()).getChildren().add(Interface);
    }

    private void playerLost() {
        // FIXME: this should be displayed in the JavaFX view, not in the console. A
        // game over screen would be nice too.

        lives--;
        switch (lives) {
            case 0:
                livegroup1.setVisible(false);
                break;
            case 1:
                livegroup2.setVisible(false);
                break;
            case 2:
                livegroup3.setVisible(false);
                break;
        }

        if (lives == 0) { // interface a faire pour game over //arreter de bouger fantome + pacman?
            // PacMan.INSTANCE.setDirection(Direction.NONE);
            GameOver();
            // System.exit(0);
        }
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

    public int getLive() {
        return lives;
    }
}