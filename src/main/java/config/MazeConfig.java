package config;

import geometry.IntCoordinates;
import java.util.List;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

import static config.Cell.Content.DOT; // Permet de ne pas écrire la classe (Ex: DOT au lieu de Cell.DOT)
import static config.Cell.*;
import static config.Cell.Content.NOTHING;

public class MazeConfig {


    private final Cell[][] grid;
    private final IntCoordinates pacManPos, blinkyPos, pinkyPos, inkyPos, clydePos;

    public MazeConfig(Cell[][] grid, IntCoordinates pacManPos, IntCoordinates blinkyPos, IntCoordinates pinkyPos, IntCoordinates inkyPos, IntCoordinates clydePos) {
        this.grid = new Cell[grid.length][grid[0].length];
        for (int i = 0; i < getHeight(); i++) {
            if (getWidth() >= 0) System.arraycopy(grid[i], 0, this.grid[i], 0, getHeight());
        }
        this.pacManPos = pacManPos;
        this.blinkyPos = blinkyPos;
        this.inkyPos = inkyPos;
        this.pinkyPos = pinkyPos;
        this.clydePos = clydePos;
    }

    public IntCoordinates getPacManPos() {
        return pacManPos;
    }

    public IntCoordinates getBlinkyPos() {
        return blinkyPos;
    }

    public IntCoordinates getPinkyPos() {
        return pinkyPos;
    }

    public IntCoordinates getInkyPos() {
        return inkyPos;
    }

    public IntCoordinates getClydePos() {
        return clydePos;
    }

    public int getWidth() {
        return grid[0].length;
    }

    public int getHeight() {
        return grid.length;
    }

    public Cell getCell(IntCoordinates pos) {
        return grid[Math.floorMod(pos.y(), getHeight())][Math.floorMod(pos.x(), getWidth())];
    }


    // Lit la structure d'un niveau à partir d'un fichier texte
    public static List<String> getLines(String path) {
        Path file = Paths.get(path);
        try {
            List<String> allLines = Files.readAllLines(file, StandardCharsets.UTF_8);
            return allLines;
        } catch(IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    // La map est restreinte à un rectangle pour l'instant
    public static MazeConfig makeExample1() {
        List<String> allLines = getLines("src/main/java/config/mazestructure.txt");
        List<String[]> lineData = new ArrayList<>(); // Liste de chaque ligne sous forme d'array : [(Type,Content); ...], [(Type,Content); ...], ...
        List<String[][]> lineData2 = new ArrayList<>(); // Liste de chaque ligne sous forme d'array contenant chaque couple aussi en array : [ligne][couples], [ligne][couples], ...

        for(String line : allLines) { // On parcourt chaque ligne du fichier .txt et on transforme chaque ligne en un array [(Type,Content), ...] contenant chaque String de couple (Type,Content)
            lineData.add(line.split(" "));
        }
        for(String[] line : lineData) { // On parcourt les array de 'lineData'
            List<String[]> allCouples = new ArrayList<>(); // Chaque couple sous forme d'array [Type, Content] d'une ligne
            for(String couple : line) { // On parcourt chaque ligne et on ajoute les couples [Type, Content] dans 'allCouples'
                allCouples.add(couple.split(","));
            }
            String[][] lineArray = new String[allCouples.size()][allCouples.get(0).length]; // Une ligne et ses couples sous forme de tableau de tableaux : [ligne][couples]
            lineArray = allCouples.toArray(lineArray);
            lineData2.add(lineArray);
        }

        Cell[][] maze = new Cell[lineData2.size()][lineData2.get(0).length]; // Tableau de tableaux contenant les cellules
        int i = 0;
        for(String[][] line : lineData2) { // Ajout des cellules dans 'maze' en utilisant les données de 'lineData2'
            Cell[] oneLine = new Cell[lineData2.get(0).length];
            int j = 0;
            for(String[] couple : line) {
                oneLine[j] = cell(Type.valueOf(couple[0]), Content.valueOf(couple[1]));
                j++;
            }
            maze[i] = oneLine;
            i++;
        }

        return new MazeConfig(
            maze,
            new IntCoordinates(3, 0), // Pacman
            new IntCoordinates(0, 3), // Rouge
            new IntCoordinates(3, 5), // Rose
            new IntCoordinates(5, 5), // Bleu
            new IntCoordinates(5, 1)  // Orange
        );
    }
}
