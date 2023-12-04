package config.MazeGenerator;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import config.MazeGenerator.MainGrid.BuildState;

public final class Piece {
    private static Random random = new Random();
    private static final int CHAR_TO_INT = 48;

    private static final int BAG_OF_PIECES = 4;
    /** Liste des placeholders des pièces. */
    private static String[][] possibleGridPieces = new String[BAG_OF_PIECES][];
    /** List de toutes les pièces chargées. */
    private static ArrayList<ArrayList<String[]>> gridPieces = new ArrayList<>();

    private static final int BORDER_INT = 10;
    // private static final int SPAWN_WALL_INT = 15;
    // private static final int SPAWN_INT = 19;
    // private static final int WALL = 20;

    private Piece() { }

    /**
     * Méthode récursive qui renvoie une liste des pièces placables sur le terrain(static).
     * Elle est utilisé par {@link MainGrid#backtrackGeneration()}.
     * @param grid
     * @param buildState
     * @return {@code ArrayList<String[]>}
     */
    public static ArrayList<String> possibles(final int[][] grid,
    final MainGrid.BuildState buildState) {
        ArrayList<String> possibles = new ArrayList<>();
        // belowSpawnPiece
        if (buildState == BuildState.BELOWSPAWN) {
            for (String piece : possibleGridPieces[0]) {
                if (isAValidPiece(grid, piece, buildState)) {
                    int randomIndex = random.nextInt(possibles.size() + 1);
                    possibles.add(randomIndex, piece);
                }
            }
        } else if (buildState == BuildState.MIDDLEMAP) {
            for (String piece : possibleGridPieces[1]) {
                if (isAValidPiece(grid, piece, buildState)) {
                    int randomIndex = random.nextInt(possibles.size() + 1);
                    possibles.add(randomIndex, piece);
                }
            }
        } else {
            for (String piece : possibleGridPieces[2]) {
                if (isAValidPiece(grid, piece, buildState)) {
                    int randomIndex = random.nextInt(possibles.size() + 1);
                    possibles.add(randomIndex, piece);
                }
            }
        }
        return possibles;
    }

    /**
     * Méthode qui renvoie la cellule la plus proche (en partant de gauche à droite
     * puis haut en bas) du nombre "n" donnée en paramètre.
     * @param grid
     * @param n
     * @return {@code int[] de taille 2, int[0] = colonne, int[1] = ligne}
     */
    public static int[] getFirstNumberOfGridHorizontal(final int[][] grid, final int n) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == n) {
                    return new int[] {i, j};
                }
            }
        }
        return new int[2];
    }

    /**
     * Méthode qui renvoie la cellule la plus proche (en partant de haut en bas
     * puis gauche à droite) du nombre "n" donnée en paramètre.
     * @param grid
     * @param n
     * @return {@code int[] de taille 2, int[0] = colonne, int[1] = ligne}
     */
    public static int[] getFirstNumberOfGridVertical(final int[][] grid, final int n) {
        for (int i = 0; i < grid.length * grid[0].length; i++) {
            if (grid[i % grid.length][i / grid.length] == n) {
                return new int[] {i % grid.length, i / grid.length};
            }
        }
        return new int[2];
    }

    /**
     * Méthode qui vérifie si la donnée en paramètre, peut rentrer.
     * @param grid
     * @param piece
     * @param buildState
     * @return {@code boolean} Vrai si oui, faux sinon.
     */
    public static boolean isAValidPiece(final int[][] grid,
        final String piece, final MainGrid.BuildState buildState) {
        int[] indice;
        if (buildState == BuildState.BELOWSPAWN) {
            final int belowSpawnWall = 3;
            indice = new int[] {(grid.length / 2) + belowSpawnWall, 0};
        } else if (buildState == BuildState.MIDDLEMAP) {
            indice = Piece.getFirstNumberOfGridVertical(grid, 0);
        } else {
            indice = Piece.getFirstNumberOfGridHorizontal(grid, 0);
        }
        if (isOutsideMaze(grid, indice, piece)) {
            return false;
        }
        if (isBlockedByOtherPiece(grid, indice, piece)) {
            return false;
        }
        if (isBlockingFuturePiece(grid, indice, piece)) {
            return false;
        }
        if (buildState == BuildState.FREE && isMakingUnreachableSpace(grid, indice, piece)) {
            return false;
        }
        for (int i = 0; i < (int) piece.charAt(0) - CHAR_TO_INT; i++) {
            for (int j = 0; j < (int) piece.charAt(2) - CHAR_TO_INT; j++) {
                if (grid[indice[0] + i][indice[1] + j] > 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Méthode qui vérifie si la piece donnée va provoquer des blocs non valides.
     * @param grid
     * @param indice
     * @param piece
     * @return {@code boolean}
     */
    public static boolean isMakingUnreachableSpace(final int[][] grid, final int[] indice,
    final String piece) {
        int[] futurePos = {indice[0] + piece.charAt(0) - CHAR_TO_INT,
            indice[1] + piece.charAt(2) - CHAR_TO_INT};
        for (int j = indice[1]; j < futurePos[1]; j++) {
            if (grid[indice[0] - 1][j] <= 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Méthode qui vérifie si la pièce donnée est bloqué par des pièces.
     * @param grid
     * @param indice
     * @param piece
     * @return {@code boolean}
     */
    public static boolean isBlockedByOtherPiece(final int[][] grid, final int[] indice,
    final String piece) {
        int[] futurePos = {indice[0] + piece.charAt(0) - CHAR_TO_INT,
            indice[1] + piece.charAt(2) - CHAR_TO_INT};
        for (int i = indice[0] - 1; i < futurePos[0] + 1; i++) {
            for (int j = indice[1] - 1; j < futurePos[1] + 1; j++) {
                if (i < 0 || j < 0) {
                    continue;
                }
                if (grid[i][j] >= BORDER_INT) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Méthode qui vérifie si la pièce donnée bloquera de futur pièce.
     * @param grid
     * @param indice
     * @param piece
     * @return {@code boolean}
     */
    public static boolean isBlockingFuturePiece(final int[][] grid, final int[] indice,
    final String piece) {
        int[] pos = {indice[0] + piece.charAt(0) - CHAR_TO_INT,
            indice[1] + piece.charAt(2) - CHAR_TO_INT};
        if (indice[1] != 0) {
            for (int i = indice[0]; i < pos[0]; i++) {
                for (int j = 0; j < indice[1]; j++) {
                    if (grid[i][j] == 0) {
                        return true;
                    }
                }
            }
        }
        int[] nb0H = new int[2];
        for (int i = pos[0]; i < grid.length; i++) {
            if (grid[i][indice[1]] == 0) {
                nb0H[0]++;
            } else {
                break;
            }
        }
        for (int i = pos[0]; i < grid.length; i++) {
            if (grid[i][pos[1]] == 0) {
                nb0H[1]++;
            } else {
                break;
            }
        }
        int[] nb0L = new int[2];
        for (int i = pos[1]; i < grid[0].length; i++) {
            if (grid[indice[0]][i] == 0) {
                nb0L[0]++;
            } else {
                break;
            }
        }
        for (int i = pos[1]; i < grid[0].length; i++) {
            if (grid[pos[0]][i] == 0) {
                nb0L[1]++;
            } else {
                break;
            }
        }
        return nb0H[0] == 1 || nb0H[0] == 2 || nb0H[1] == 1 || nb0H[1] == 2
        || nb0L[0] == 1 || nb0L[0] == 2 || nb0L[1] == 1 || nb0L[1] == 2;
    }

    /**
     * Méthode qui vérifie si la pièce donnée se trouve en dehors du terrain.
     * @param grid
     * @param indice
     * @param piece
     * @return Vraie si en dehors, faux sinon.
     */
    public static boolean isOutsideMaze(final int[][] grid, final int[] indice,
    final String piece) {
        // CHECKSTYLE:OFF
        if (indice[0] - 1 + (int) piece.charAt(0) - CHAR_TO_INT > grid.length - 3) {
            return true;
        }
        if (indice[1] - 1 + (int) piece.charAt(2) - CHAR_TO_INT > grid[0].length - 3) {
            return true;
        }
        return false;
        // CHECKSTYLE:OFF
    }

    /**
     * Affiche dans la console une pièce du jeu.
     * @param piece
     */
    public static void printPiece(final String[] piece) {
        for (int i = 0; i < piece.length; i++) {
            System.out.println(piece[0]);
        }
        System.out.println("==================");
    }

    /**
     * Initialise les tailles possibles des pièces du terrain en fonction de sa taille.
     * @param fileName
     * @param jArray
     */
    public static void loadPossiblesPiece(final String fileName, final JSONArray jArray) {
        final String[] possiblePieces = new String[jArray.length()];
        for (int i = 0; i < jArray.length(); i++) {
            possiblePieces[i] = jArray.getString(i);
        }
        possibleGridPieces[jsonToInt(fileName)] = possiblePieces;
    }

    /**
     * Renvoie un int en fonction du nom du fichier donné.
     * @param fileName
     * @return 0 = small, 1 = classic, 2 = big
     */
    public static int jsonToInt(final String fileName) {
        // CHECKSTYLE:OFF
        if (fileName.equals("spawnWall.json")) {
            return 0;
        } else if (fileName.equals("middleWall.json")) {
            return 1;
        } else if (fileName.equals("freeWall.json")) {
            return 2;
        } else {
            return 3;
        }
        // CHECKSTYLE:ON
    }

    /**
     * Initialise les pièces du jeu à partir des fichiers json du dossier.
     */
    public static void initialiseGridPieces() {
        try {
            final String path = "src/main/resources/maps/MazeGeneratorPieces/";
            final String[] jsonPath = {
                "spawnWall.json", "middleWall.json", "freeWall.json", "tunnel.json"};
            for (String json : jsonPath) { // Chaque fichier
                gridPieces.add(new ArrayList<String[]>());
                final String str =
                    FileUtils.readFileToString(new File(path + json), StandardCharsets.UTF_8);
                final JSONObject jObject = new JSONObject(str); // Contenu du fichier -> String
                loadPossiblesPiece(json, jObject.getJSONArray("model"));
                for (int i = 0; i < jObject.names().length(); i++) { // Chaque pièce
                    if (jObject.names().getString(i).equals("model")) {
                        continue;
                    }
                    final JSONArray jArray = jObject.getJSONArray(jObject.names().getString(i));
                    String[] piece = new String[jArray.length()];
                    for (int j = 0; j < jArray.length(); j++) { // Chaque ligne de la pièce
                        piece[j] = jArray.getString(j);
                    }
                    gridPieces.get(jsonToInt(json)).add(piece);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode qui vérifie si la pièce est compatible avec la taille du terrain donnée.
     * @param fileName
     * @param jArray
     * @return {@code boolean} Vrai si compatible, faux sinon.
     */
    public static boolean isCompatible(final String fileName, final JSONArray jArray) {
        final int h = 0;
        final int l = 2;
        boolean isCompatible = false;
        for (String dimensionPiece : possibleGridPieces[jsonToInt(fileName)]) {
            if (jArray.length() == (int) dimensionPiece.charAt(h) - CHAR_TO_INT
            && jArray.getString(0).length() == (int) dimensionPiece.charAt(l) - CHAR_TO_INT) {
                isCompatible = true;
            }
        }
        return isCompatible;
    }

    /**
     * Méthode qui renvoie simplement dans le terminal, la taille de chaque sac de pièces.
     */
    public static void printSizeOfPiecesBag() {
        // CHECKSTYLE:OFF
        System.out.println("belowSpawnPieces | taille : " + gridPieces.get(0).size());
        System.out.println("middlePieces | taille : " + gridPieces.get(1).size());
        System.out.println("freePieces | taille : " + gridPieces.get(2).size());
        System.out.println("tunnelPieces | taille : " + gridPieces.get(3).size());
        // CHECKSTYLE:ON
    }

    public static String[][] getPossibleGridPieces() {
        return possibleGridPieces;
    }

    public static ArrayList<ArrayList<String[]>> getGridPieces() {
        return gridPieces;
    }

}
