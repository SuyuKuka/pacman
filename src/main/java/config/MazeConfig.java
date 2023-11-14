package config;

import geometry.IntCoordinates;
import geometry.RealCoordinates;
import model.Direction;
import utils.MapVerification;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.json.JSONArray;

import static config.Cell.cell;

import config.Cell.Content;
import config.Cell.Type;

public class MazeConfig {
    /** La carte contenant chaque cellule {@link Cell}. */
    private final Cell[][] grid;
    /** Les coordonnées initiales de {@link model.PacMan}. */
    private final IntCoordinates pacManPos;
    /** Les coordonnées initiales de {@link model.Ghost#BLINKY}. */
    private final IntCoordinates blinkyPos;
    /** Les coordonnées initiales de {@link model.Ghost#PINKY}. */
    private final IntCoordinates pinkyPos;
    /** Les coordonnées initiales de {@link model.Ghost#INKY}. */
    private final IntCoordinates inkyPos;
    /** Les coordonnées initiales de {@link model.Ghost#CLYDE}. */
    private final IntCoordinates clydePos;

    /**
     * Constructeur qui initialise les configs pour la carte courante.
     * @param newGrid Grille pour {@link #grid}
     * @param newPacManPos Position de {@link #pacManPos}
     * @param newBlinkyPos Position de {@link #blinkyPos}
     * @param newPinkyPos Position de {@link #pinkyPos}
     * @param newInkyPos Position de {@link #inkyPos}
     * @param newClydePos Position de {@link #clydePos}
     */
    public MazeConfig(
        final Cell[][] newGrid,
        final IntCoordinates newPacManPos,
        final IntCoordinates newBlinkyPos, final IntCoordinates newPinkyPos,
        final IntCoordinates newInkyPos, final IntCoordinates newClydePos
        ) {
        grid = new Cell[newGrid.length][newGrid[0].length];
        for (int i = 0; i < getHeight(); i++) {
            if (getWidth() >= 0) {
                System.arraycopy(newGrid[i], 0, grid[i], 0, getWidth());
            }
        }
        MapVerification.tunnelFinder(grid);
        MapVerification.removeUnnecessaryWall(grid);
        pacManPos = newPacManPos;
        blinkyPos = newBlinkyPos;
        inkyPos = newInkyPos;
        pinkyPos = newPinkyPos;
        clydePos = newClydePos;
    }

    /**
     * Retourne la position initiale de {@link model.PacMan}.
     * @return {@link IntCoordinates}
     */
    public IntCoordinates getPacManPos() {
        return pacManPos;
    }

    /**
     * Retourne la position initiale de {@link model.Ghost#BLINKY}.
     * @return {@link IntCoordinates}
     */
    public IntCoordinates getBlinkyPos() {
        return blinkyPos;
    }

    /**
     * Retourne la position initiale de {@link model.Ghost#PINKY}.
     * @return {@link IntCoordinates}
     */
    public IntCoordinates getPinkyPos() {
        return pinkyPos;
    }

    /**
     * Retourne la position initiale de {@link model.Ghost#INKY}.
     * @return {@link IntCoordinates}
     */
    public IntCoordinates getInkyPos() {
        return inkyPos;
    }

    /**
     * Retourne la position initiale de {@link model.Ghost#CLYDE}.
     * @return {@link IntCoordinates}
     */
    public IntCoordinates getClydePos() {
        return clydePos;
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

    /**
     * Retourne la grille {@link #grid}.
     * @return {@link #grid}
     */
    public Cell[][] getGrid() {
        return grid;
    }

    /**
     * Convertie les données d'un fichier .json en {@code Cell[][]}
     * utilisé pour la configuration de la carte. <br></br>
     * Le fichier doit contenir des données nécessaires pour
     * une carte rectangulaire.
     * @param path Le chemin du fichier à importer.
     * @return {@code Cell[][] maze}
     */
    public static Cell[][] getJsonMaze(final String path) {
        try {
            String file = FileUtils.readFileToString(
                new File(path), StandardCharsets.UTF_8
            );
            JSONObject jObject = new JSONObject(file);
            JSONArray jArray = new JSONArray(jObject.getJSONArray("Line1"));
            Cell[][] maze = new Cell[jObject.length()][jArray.length()];
            for (int i = 0; i < jObject.length(); i++) {
                jArray = new JSONArray(jObject.getJSONArray("Line" + (i + 1)));
                for (int k = 0; k < jArray.length(); k++) {
                    String output = jArray.getString(k);
                    if (output.equals("CLOSED") || output.equals("OPEN")) {
                        maze[i][k] = cell(Type.valueOf(output));
                        continue;
                    }
                    maze[i][k] = cell(Content.valueOf(output));
                }
            }
            return maze;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Cell[0][];
    }

    /**
     * La méthode utilise la méthode {@code getAdjacentCellsCoordinates()}
     * pour obtenir les cellules {@code Cell} adjacentes au fantôme.<br></br>
     * Si une coordonnée indique une position en dehors de la map,
     * une cellule de type {@code CLOSED} sera ajoutée à la liste.
     * @param pos La position actuelle du fantôme.
     * @param dir La direction actuelle du fantôme.
     * @return Une {@code List<Cell>} contenant les cellules
     * adjacentes au fantôme. Triées dans le sens horaire.
     */
    public List<Cell> getAdjacentCells(
        final RealCoordinates pos, final Direction dir
        ) {
        List<IntCoordinates> adjacentCellCoordinates =
        pos.getAdjacentCellsCoordinates(dir);

        List<Cell> adjacentCells = new ArrayList<>();

        for (IntCoordinates intCoordinates : adjacentCellCoordinates) {
            // Si la cellule est en dehors de la map,
            // elle sera considérée comme CLOSED
            if (intCoordinates.x() < 0 || intCoordinates.x() == grid[0].length
            || intCoordinates.y() < 0 || intCoordinates.y() == grid.length) {
                adjacentCells.add(new Cell(true, true, true, true, null));
            } else {
                adjacentCells.add(grid[intCoordinates.y()][intCoordinates.x()]);
            }
        }
        return adjacentCells;
    }

    /**
     * Retourne un {@link MazeConfig} test.
     * @return {@link MazeConfig}
     */
    public static MazeConfig makeExample1() {
        final int pacX = 13;
        final int pacY = 26;
        final int blinkyX = pacX;
        final int blinkyY = 14;
        final int pinkyX = pacX;
        final int inkyX = 11;
        final int clydeX = 15;
        final int otherGhostY = 17;
        return new MazeConfig(
                getJsonMaze("src/main/java/config/Level1.json"),
                new IntCoordinates(pacX, pacY), // Pacman
                new IntCoordinates(blinkyX, blinkyY), // Blinky (Rouge)
                new IntCoordinates(pinkyX, otherGhostY), // Pinky (Rose)
                new IntCoordinates(inkyX, otherGhostY), // Inky (Bleu)
                new IntCoordinates(clydeX, otherGhostY) // Clyde (Orange)
        );
    }
}
