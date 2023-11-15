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

public class MazeConfig {
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
    private final PacMan pacman;
    private final List<Ghost> ghostList = new ArrayList<>();


    /**
     * Constructeur qui initialise les configs pour la carte courante.
     * @param pacman {@link PacMan}
     * @param configPath {@code String}Le path du fichier JSON de la map
     */
    public MazeConfig(final PacMan pacman, final String configPath) {
        grid = getJSONMaze(configPath);
        for (int i = 0; i < getHeight(); i++) {
            for (int j = 0; j < getWidth(); j++) {
                totalPacGomme += grid[i][j].initialContent() == Content.DOT ? 1 : 0;
            }
        }
        ghostStateSwap = getStateSwap(configPath);
        MapVerification.tunnelFinder(grid);
        MapVerification.removeUnnecessaryWall(grid);
        //CHECKSTYLE:OFF
        this.pacman = pacman;
        final int ghostSpawnX = getWidth() / 2 - 4;
        final int ghostSpawnY = getHeight() / 2 - 3;
        ghostSpawnPos[0] = new RealCoordinates(ghostSpawnX, ghostSpawnY);
        ghostSpawnPos[1] = new RealCoordinates(ghostSpawnX + 7, ghostSpawnY + 4);

        pacman.setSpawnPos(critterSpawnOrTargetPos(null, "critterSpawn", configPath));
        pacman.setPos(pacman.getSpawnPos());
        Ghost blinky = new Ghost(
            GhostType.BLINKY, pacman,
            critterSpawnOrTargetPos(GhostType.BLINKY, "critterSpawn", configPath),
            critterSpawnOrTargetPos(GhostType.BLINKY, "eatenTarget", configPath),
            critterSpawnOrTargetPos(GhostType.BLINKY, "target", configPath)
        );
        Ghost inky = new Ghost(
            GhostType.INKY, blinky, pacman,
            critterSpawnOrTargetPos(GhostType.INKY, "critterSpawn", configPath),
            critterSpawnOrTargetPos(GhostType.INKY, "eatenTarget", configPath),
            critterSpawnOrTargetPos(GhostType.INKY, "target", configPath)
        );
        Ghost pinky = new Ghost(
            GhostType.PINKY, pacman,
            critterSpawnOrTargetPos(GhostType.PINKY, "critterSpawn", configPath),
            critterSpawnOrTargetPos(GhostType.PINKY, "eatenTarget", configPath),
            critterSpawnOrTargetPos(GhostType.PINKY, "target", configPath)
        );
        Ghost clyde = new Ghost(
            GhostType.CLYDE, pacman,
            critterSpawnOrTargetPos(GhostType.CLYDE, "critterSpawn", configPath),
            critterSpawnOrTargetPos(GhostType.CLYDE, "eatenTarget", configPath),
            critterSpawnOrTargetPos(GhostType.CLYDE, "target", configPath)
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
     * @param config {@code String} "critterSpawn" / "target" / "eatenTarget"
     * @param path Le chemin du fichier à importer.
     * @return {@code RealCoordinates}
     */
    public static RealCoordinates critterSpawnOrTargetPos(
            final GhostType type, final String config, final String path) {
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
                pos = jArray.getString(4).split(";");
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
    public final void activateGhost() {
        for (Ghost ghost : ghostList) {
            switch (ghost.getGhostType()) {
                case BLINKY:
                    ghost.setActivated(true);
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
    public final void resetGhostPos(final MazeState state) {
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
     * Retourne un {@link MazeConfig} test.
     * @param pacman {@link PacMan}
     * @return {@link MazeConfig}
     */
    public static MazeConfig makeExample1(final PacMan pacman) {
        return new MazeConfig(pacman, "src/main/java/config/Level1.json");
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
        return critters;
    }

    /**
     * Renvoie la liste des {@link Ghost} qui ont pour type le paramètre donné.
     * @param ghostType {@link GhostType}
     * @return {@code Lit<Ghost>}
     */
    public final List<Ghost> getGhostKind(final GhostType ghostType) {
        List<Ghost> oneKind = new ArrayList<>();
        for (Ghost ghost : ghostList) {
            if (ghost.getGhostType() == ghostType)  {
                oneKind.add(ghost);
            }
        }
        return oneKind;
    }

    /**
     * Méthode qui renvoie vraie si le {@link RealCoordinates}
     * donné se trouve à l'entrée du spawn des {@link Ghost}.
     * @param pos {@link RealCoordinates}
     * @return true ou false
     */
    public boolean isAtSpawnEntrance(final RealCoordinates pos) {
        final double xEntrance = 3;
        double leftPosXEntrance = ghostSpawnPos[0].x() + xEntrance;
        if (pos.y() == ghostSpawnPos[0].y()
            && pos.x() >= leftPosXEntrance && pos.x() <= leftPosXEntrance + 1) {
            return true;
        }
        return false;
    }

    public final Cell[][] getGrid() {
        return grid;
    }

    public final int[] getGhostStateSwap() {
        return ghostStateSwap;
    }

    public final int getTotalPacGomme() {
        return totalPacGomme;
    }

    public final RealCoordinates[] getGhostSpawnPos() {
        return ghostSpawnPos;
    }

    public final PacMan getPacman() {
        return pacman;
    }

    public final List<Ghost> getGhostList() {
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
