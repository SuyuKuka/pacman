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
    public MazeConfig(Cell[][] grid, IntCoordinates pacManPos, IntCoordinates blinkyPos, IntCoordinates pinkyPos,
                      IntCoordinates inkyPos, IntCoordinates clydePos) {
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

    private final Cell[][] grid;
    private final IntCoordinates pacManPos, blinkyPos, pinkyPos, inkyPos, clydePos;

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


    // simple example with a square shape
    // TODO: mazes should be loaded from a text file
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

    public static MazeConfig makeExample1() {
        List<String> allLines = getLines("src/main/java/config/mazestructure.txt");
        List<String[]> lineData = new ArrayList<>(); // Liste de chaque ligne sous tableau
        List<String[][]> lineData2 = new ArrayList<>(); // Liste de chaque ligne sous tableau avec chaque couple sous tableau

        for(String line : allLines) {
            lineData.add(line.split(" "));
        }
        for(String[] line : lineData) {
            List<String[]> allCouples = new ArrayList<>(); // Contient les tableaux de chaque couple [Type, Content] d'une ligne
            for(String couple : line) {
                allCouples.add(couple.split(","));
            }
            String[][] lineArray = new String[allCouples.size()][allCouples.get(0).length];
            lineArray = allCouples.toArray(lineArray);
            lineData2.add(lineArray);
        }

        Cell[][] maze = new Cell[lineData2.size()][lineData2.get(0).length];
        int i = 0;
        for(String[][] line : lineData2) {
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
