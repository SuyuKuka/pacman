package config;

import geometry.IntCoordinates;
import geometry.RealCoordinates;
import model.Critter;
import model.Direction;
import model.Ghost;
import model.PacMan;
import model.Ghost.GhostType;
import utils.MapVerification;
import model.MazeState;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.json.JSONArray;

import static config.Cell.cell;
import static model.Direction.EAST;
import static model.Direction.NONE;
import static model.Direction.NORTH;
import static model.Direction.WEST;

import config.Cell.Content;
import config.Cell.Type;

public final class MazeConfig {
    private static final HashMap<String, String> LINE_TABLE = new HashMap<>();

    static {
        LINE_TABLE.put("/", "OPEN");
        LINE_TABLE.put("■", "CLOSED");
        LINE_TABLE.put("s", "SPAWN");
        LINE_TABLE.put("=", "TUNNEL");
        LINE_TABLE.put("◦", "DOT");
        LINE_TABLE.put("●", "ENERGIZER");
        LINE_TABLE.put("x", "NOTHING");
        LINE_TABLE.put("¤", "DOT");
    }

    /** La carte contenant chaque cellule {@link Cell}. */
    private final Cell[][] grid;
    private final int[] ghostStateSwap;

    private int totalPacGomme = 0;
    /**Les coordonnées du sommet haut-gauche et bas-droite du spawn des {@link model.Ghost}.*/
    private final RealCoordinates[] ghostSpawnPos = new RealCoordinates[2];
    private final RealCoordinates ghostSpawnEntrance;
    private final List<Ghost> ghostList = new ArrayList<>();
    private final PacMan pacman;
    /** Pac-Man 2 en Coop.*/
    private final PacMan pacman2;

    /**
     * Constructeur qui initialise les configs pour la carte courante.
     * @param versus Si on est en mode Versus
     * @param coop Si on est en mode Coop
     * @param configPath {@code String}Le path du fichier JSON de la map
     */
    public MazeConfig(final boolean versus, final boolean coop, final String configPath) {
        grid = getJSONMaze(configPath);
        for (int i = 0; i < getHeight(); i++) {
            for (int j = 0; j < getWidth(); j++) {
                totalPacGomme += grid[i][j].getContent() == Content.DOT ? 1 : 0;
            }
        }
        ghostStateSwap = getStateSwap(configPath);
        MapVerification.tunnelFinder(grid);
        MapVerification.removeUnnecessaryWall(grid);
        //CHECKSTYLE:OFF
        pacman = new PacMan();
        final int ghostSpawnX = getWidth() / 2 - 4;
        final int ghostSpawnY = getHeight() / 2 - 3;
        ghostSpawnPos[0] = new RealCoordinates(ghostSpawnX, ghostSpawnY);
        ghostSpawnPos[1] = new RealCoordinates(ghostSpawnX + 7, ghostSpawnY + 4);
        final double entranceY = -1;
        final double entranceX = 3.5;
        ghostSpawnEntrance = ghostSpawnPos[0].plus(new RealCoordinates(entranceX, entranceY));

        pacman.setSpawnPos(critterSpawnOrTargetPos(null, false, "critterSpawn", configPath));
        pacman.setPos(pacman.getSpawnPos());

        // Multijoueur Coop
        if (coop) {
            pacman2 = new PacMan();
            pacman2.setSpawnPos(critterSpawnOrTargetPos(null, true, "critterSpawn", configPath));
            pacman2.setPos(pacman2.getSpawnPos());
        } else {
            pacman2 = null;
        }

        Ghost blinky = new Ghost(
            GhostType.BLINKY, pacman, pacman2,
            versus
            ? new RealCoordinates(13, 15)
            : critterSpawnOrTargetPos(GhostType.BLINKY, false, "critterSpawn", configPath),
            critterSpawnOrTargetPos(GhostType.BLINKY, false, "eatenTarget", configPath),
            critterSpawnOrTargetPos(GhostType.BLINKY, false, "target", configPath)
        );
        Ghost inky = new Ghost(
            GhostType.INKY, blinky, pacman, pacman2,
            critterSpawnOrTargetPos(GhostType.INKY, false, "critterSpawn", configPath),
            critterSpawnOrTargetPos(GhostType.INKY, false, "eatenTarget", configPath),
            critterSpawnOrTargetPos(GhostType.INKY, false, "target", configPath)
        );

        Ghost pinky = new Ghost(
            GhostType.PINKY, pacman, pacman2,
            critterSpawnOrTargetPos(GhostType.PINKY, false, "critterSpawn", configPath),
            critterSpawnOrTargetPos(GhostType.PINKY, false, "eatenTarget", configPath),
            critterSpawnOrTargetPos(GhostType.PINKY, false, "target", configPath)
        );
        Ghost clyde = new Ghost(
            GhostType.CLYDE, pacman, pacman2,
            critterSpawnOrTargetPos(GhostType.CLYDE, false, "critterSpawn", configPath),
            critterSpawnOrTargetPos(GhostType.CLYDE, false, "eatenTarget", configPath),
            critterSpawnOrTargetPos(GhostType.CLYDE, false, "target", configPath)
        );
        ghostList.addAll(Arrays.asList(blinky, inky, pinky, clyde));
        //CHECKSTYLE:ON
    }

    /**
     * Convertie les données d'un fichier .json en {@code Cell[][]}
     * utilisé pour la configuration de la carte. <br></br>
     * Le fichier doit contenir des données nécessaires pour
     * une carte rectangulaire.
     * @param path Le chemin du fichier à importer.
     * @return {@code Cell[][] maze}
     */
    public static Cell[][] getJSONMaze(final String path) {
        try {
            String file = FileUtils.readFileToString(
                new File(path), StandardCharsets.UTF_8
            );
            JSONObject jObject = new JSONObject(file);
            JSONArray jArray = new JSONArray(jObject.getJSONArray("Line1"));
            final int lineLength = (jArray.get(0).toString().length() + 1) / 2;
            Cell[][] maze = new Cell[jObject.length() - 1][lineLength];
            for (int i = 0; i < jObject.length() - 1; i++) {
                jArray = new JSONArray(jObject.getJSONArray("Line" + (i + 1)));
                for (int k = 0; k < lineLength; k++) {
                    String output = jArray.getString(0).charAt(k * 2) + "";
                    if (output.equals("■") || output.equals("/")) {
                        maze[i][k] = cell(Type.valueOf(LINE_TABLE.get(output)));
                    } else if (output.equals("¤") || output.equals("x")) {
                        maze[i][k] = cell(Content.valueOf(LINE_TABLE.get(output)), true);
                    } else {
                        maze[i][k] = cell(Content.valueOf(LINE_TABLE.get(output)));
                    }
                }
            }
            return maze;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Cell[0][];
    }

    /**
     * Méthode qui retourne le tableau contenant les différents instants pour
     * le changement de mode.
     * @param path Le chemin du fichier à importer.
     * @return {@code int[]}
     */
    public static int[] getStateSwap(final String path) {
        try {
            String file = FileUtils.readFileToString(
                new File(path), StandardCharsets.UTF_8
            );
            JSONObject jObject = new JSONObject(file).getJSONObject("config");
            JSONArray jArray = new JSONArray(jObject.getJSONArray("swapTime"));
            int[] stateSwap = new int[jArray.length()];
            for (int i = 0; i < stateSwap.length; i++) {
                stateSwap[i] = jArray.getInt(i);
            }
            return stateSwap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new int[0];
    }

    /**
     * Méthode qui retourne un{@code RealCoordinates}du spawn ou du target
     * en{@code SCATTER}d'un{@code Critter}depuis le fichier de configuration.
     * @param type {@link GhostType} du fantôme,{@code null} si Pac-Man
     * @param pacman2 Si en coop
     * @param config {@code String} "critterSpawn" / "target" / "eatenTarget"
     * @param path Le chemin du fichier à importer.
     * @return {@code RealCoordinates}
     */
    public static RealCoordinates critterSpawnOrTargetPos(
            final GhostType type, final boolean pacman2, final String config, final String path) {
        //CHECKSTYLE:OFFcheckstyle:magicnumber
        try {
            String file = FileUtils.readFileToString(
                new File(path), StandardCharsets.UTF_8
            );
            JSONObject jObject = new JSONObject(file).getJSONObject("config");
            JSONArray jArray;
            String[] pos = new String[2];
            if (type == null) {
                jArray = new JSONArray(jObject.getJSONArray("critterSpawn"));
                pos = jArray.getString(pacman2 ? 5 : 4).split(";");
                return new RealCoordinates(Double.valueOf(pos[0]), Double.valueOf(pos[1]));
            }
            jArray = new JSONArray(jObject.getJSONArray(config));
            switch (type) {
                case BLINKY:
                    pos = jArray.getString(0).split(";");
                    break;
                case INKY:
                    pos = jArray.getString(1).split(";");
                    break;
                case PINKY:
                    pos = jArray.getString(2).split(";");
                    break;
                case CLYDE:
                    pos = jArray.getString(3).split(";");
                    break;

                default:
                    break;
            }
            return new RealCoordinates(Double.valueOf(pos[0]), Double.valueOf(pos[1]));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new RealCoordinates(0, 0);
        //CHECKSTYLE:ON
    }

    /**
     * Méthode qui retourne un{@code RealCoordinates}du target
     * d'un{@code Critter}en{@code SCATTER}depuis le fichier de configuration.
     * @param type {@link GhostType} du fantôme,{@code null} si Pac-Man
     * @param path Le chemin du fichier à importer.
     * @return {@code RealCoordinates}
     */
    public static RealCoordinates critterTargetPos(final GhostType type, final String path) {
        //CHECKSTYLE:OFFcheckstyle:magicnumber
        try {
            String file = FileUtils.readFileToString(
                new File(path), StandardCharsets.UTF_8
            );
            JSONObject jObject = new JSONObject(file).getJSONObject("config");
            JSONArray jArray = new JSONArray(jObject.getJSONArray("target"));
            String[] pos = new String[2];
            switch (type) {
                case BLINKY:
                    pos = jArray.getString(0).split(";");
                    break;
                case INKY:
                    pos = jArray.getString(1).split(";");
                    break;
                case PINKY:
                    pos = jArray.getString(2).split(";");
                    break;
                case CLYDE:
                    pos = jArray.getString(3).split(";");
                    break;

                default:
                    break;
            }
            return new RealCoordinates(Double.valueOf(pos[0]), Double.valueOf(pos[1]));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new RealCoordinates(0, 0);
        //CHECKSTYLE:ON
    }

    /**
     * Initialise le jeu et active les {@link Ghost} du type
     * {@link GhostType#BLINKY} et {@link GhostType#PINKY}.
     */
    public void activateGhost() {
        for (Ghost ghost : ghostList) {
            switch (ghost.getGhostType()) {
                case BLINKY:
                    ghost.setActivated(true);
                    if (ghost.isPlayer()) {
                        break;
                    }
                    ghost.setDirection(Direction.EAST);
                    break;
                case PINKY:
                    ghost.setActivated(true);
                    ghost.setDirection(Direction.NORTH);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Réinitialise la position des fantômes après un reset.
     * @param state {@code MazeState} Etat actuel de la map
     */
    public void resetGhostPos(final MazeState state) {
        final int inkyThreshold = 30;
        final int clydeThreshold = state.getConfig().getTotalPacGomme() / 3;
        for (Ghost ghost : ghostList) {
            switch (ghost.getGhostType()) {
                case BLINKY:
                    ghost.setDirection(EAST);
                    break;
                case PINKY:
                    ghost.setDirection(NORTH);
                    break;
                case INKY:
                    if (state.getEatenPacgomme() >= inkyThreshold) {
                        ghost.setDirection(EAST);
                        break;
                    }
                    ghost.setDirection(NONE);
                    break;
                case CLYDE:
                    if (state.getEatenPacgomme() >= clydeThreshold) {
                        ghost.setDirection(WEST);
                        break;
                    }
                    ghost.setDirection(NONE);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Retourne un {@link MazeConfig}. Si{@code versus}est{@code true}, initialise
     * un {@link GhostType#BLINKY} jouable.
     * @param versus {@code true}si en mode Versus
     * @param coop {@code true}si en mode Coop
     * @return {@link MazeConfig}
     */
    public static MazeConfig classicalConfig(final boolean versus, final boolean coop) {
        return new MazeConfig(versus, coop, "src/main/java/config/Level1.json");
    }

    /**
     * Renvoie la liste des {@link Critter} du {@link MazeConfig}.
     * @return {@code List<Critter>}
     */
    public List<Critter> getCritters() {
        List<Critter> critters = new ArrayList<>();
        for (Ghost ghost : ghostList) {
            critters.add(ghost);
        }
        critters.add(pacman);
        if (pacman2 != null) {
            critters.add(pacman2);
        }
        return critters;
    }

    /**
     * Renvoie la liste des {@link Ghost} qui ont pour type le paramètre donné.
     * @param ghostType {@link GhostType}
     * @return {@code Lit<Ghost>}
     */
    public List<Ghost> getGhostKind(final GhostType ghostType) {
        List<Ghost> oneKind = new ArrayList<>();
        for (Ghost ghost : ghostList) {
            if (ghost.getGhostType() == ghostType)  {
                oneKind.add(ghost);
            }
        }
        return oneKind;
    }

    public boolean isCoop() {
        return pacman2 != null;
    }

    public Cell[][] getGrid() {
        return grid;
    }

    public int[] getGhostStateSwap() {
        return ghostStateSwap;
    }

    public int getTotalPacGomme() {
        return totalPacGomme;
    }

    public RealCoordinates[] getGhostSpawnPos() {
        return ghostSpawnPos;
    }

    public RealCoordinates getGhostSpawnEntrance() {
        return ghostSpawnEntrance;
    }

    public PacMan getPacman() {
        return pacman;
    }

    public PacMan getPacman2() {
        return pacman2;
    }

    public List<Ghost> getGhostList() {
        return ghostList;
    }

    /**
     * Retourne la largeur de la grille.
     * @return {@code int}largeur
     */
    public int getWidth() {
        return grid[0].length;
    }

    /**
     * Retourne la hauteur de la grille.
     * @return {@code int}hauteur
     */
    public int getHeight() {
        return grid.length;
    }

    /**
     * Retourne la{@code Cell}aux coordonnées{@code pos}.
     * @param pos Position de la cellule
     * @return {@link Cell}
     */
    public Cell getCell(final IntCoordinates pos) {
        int y = Math.floorMod(pos.y(), getHeight());
        int x = Math.floorMod(pos.x(), getWidth());
        return grid[y][x];
    }

}
