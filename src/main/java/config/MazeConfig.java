package config;

import geometry.IntCoordinates;

import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.IOException;

import static config.Cell.*;

public class MazeConfig {


    private final Cell[][] grid;
    private final IntCoordinates pacManPos, blinkyPos, pinkyPos, inkyPos, clydePos;

    public MazeConfig(Cell[][] grid, IntCoordinates pacManPos, IntCoordinates blinkyPos, IntCoordinates pinkyPos, IntCoordinates inkyPos, IntCoordinates clydePos) {
        this.grid = new Cell[grid.length][grid[0].length];
        for (int i = 0; i < getHeight(); i++) {
            if (getWidth() >= 0) System.arraycopy(grid[i], 0, this.grid[i], 0, getWidth());
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


    /**
     * Convertie les données d'un fichier .txt en {@code Cell[][]} utilisé pour la configuration de la carte.
     * Le fichier doit contenir des données nécessaires pour une carte rectangulaire.
     * @param path Le chemin du fichier à importer.
     * @return {@code Cell[][] maze}
    */
    public static Cell[][] getMaze(String path) {
        try (Scanner sc = new Scanner(new File(path));) {
            ArrayList<String[]> set = new ArrayList<>();
            while (sc.hasNextLine()) {
                set.add(sc.nextLine().split(" "));
            }
            Cell[][] maze = new Cell[set.size()][set.get(0).length];
            int i = 0;
            for (String[] line : set) {
                for (int j=0; j<set.get(i).length; j++) {
                    String[] couple = line[j].split(",");
                    maze[i][j] = cell(Type.valueOf(couple[0]), Content.valueOf(couple[1]));
                }
                i++;
            }
            return maze;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Cell[0][];
    }

    public static MazeConfig makeExample1() {
        return new MazeConfig(
            getMaze("src/main/java/config/Level1.txt"),
            new IntCoordinates(8, 14), // Pacman
            new IntCoordinates(8, 6), // Rouge
            new IntCoordinates(8, 8), // Rose
            new IntCoordinates(7, 8), // Bleu
            new IntCoordinates(9, 8)  // Orange
        );
    }
}
