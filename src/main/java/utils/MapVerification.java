package utils;

import java.util.Arrays;
import config.Cell;
import config.Cell.Content;

public final class MapVerification {
// CHECKSTYLE:OFF
    public final static boolean f = false;

    private MapVerification() {
    }

    /**
     * Méthode qui trouve et modifient l'attribut des cellules qui semblent être un tunnel.
     * @param c
     */
    public static void tunnelFinder(final Cell[][] c) {
        for (int i = 1; i < c.length - 1; i++) {
            boolean isTunnel = c[i - 1][0].hasWall() && c[i + 1][0].hasWall();
            if (c[i][0].initialContent() == Content.DOT && isTunnel) {
                c[i][0] = new Cell(false, false, false, false, Content.TUNNEL, false);
                c[i][c[0].length - 1] = new Cell(false, false, false, false, Content.TUNNEL, false);
                for (int j = 1; j < c[i].length; j++) {
                    isTunnel = c[i - 1][j].hasWall() && c[i + 1][j].hasWall();
                    if (c[i][j].initialContent() == Content.DOT && isTunnel) {
                        c[i][j] = new Cell(false, false, false, false, Content.TUNNEL, false);
                        c[i][c[0].length - j - 1] = new Cell(f, f, f, f, Content.TUNNEL, f);
                    } else {
                        break;
                    }
                }
            }
        }
    }

    /**
     * .
     * @param c
     * @return {@code boolean}
     */
    public static boolean isAValidWall(final Cell c) {
        if (c.initialContent() == Content.DOT
            || c.initialContent() == Content.ENERGIZER
            || c.initialContent() == Content.TUNNEL) {
            return true;
        }
        if (!c.northWall() && !c.eastWall() && !c.southWall()
            && !c.westWall() && c.initialContent() == Content.NOTHING) {
            return true;
        }
        return false;
    }

    /**
     * .
     * @param grid
     * @return {@code Cell[][]}
     */
    public static Cell[][] copyGrid(final Cell[][] grid) {
        Cell[][] copy = new Cell[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            copy[i] = Arrays.copyOf(grid[i], grid[i].length);
        }
        return copy;
    }

    /**
     * .
     * @param grid
     * @return {@code Cell[][]}
     */
    public static void removeUnnecessaryWall(final Cell[][] grid) {
        Cell[][] copy = copyGrid(grid);
        final int posYMaze = 3;
        final int posXMaze = grid[0].length - 1;
        for (int i = posYMaze; i < grid.length - 2; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j].hasWall()) {
                    final int nbWalls = 4;
                    // 0 : NORTH | 1 : EAST | 2 : SOUTH | 3 : WEST
                    boolean[] wall = new boolean[nbWalls];
                    wall[0] = (i == posYMaze || isAValidWall(grid[i - 1][j]));
                    wall[1] = (j == posXMaze || isAValidWall(grid[i][j + 1]));
                    wall[2] = (i == grid.length - posYMaze || isAValidWall(grid[i + 1][j]));
                    wall[3] = (j == 0 || isAValidWall(grid[i][j - 1]));
                    copy[i][j] = new Cell(wall[0], wall[1], wall[2], wall[3], Content.NOTHING, f);
                } else {
                    copy[i][j] = grid[i][j];
                }
            }
        }

        for (int i = 0; i < copy.length; i++) {
            for (int j = 0; j < copy[i].length; j++) {
                grid[i][j] = copy[i][j];
            }
        }
    }
    //CHECKSTYLE:ON
}
