package config;

public record Cell(
    boolean northWall, boolean eastWall, boolean southWall, boolean westWall,
    Content initialContent) {
    /** Contenus dans chaque cellule. */
    public enum Content { NOTHING, DOT, ENERGIZER, SPAWN, TUNNEL, RESTRICTED }
    /** Types de cellule selon les murs qu'elle possède. */
    public enum Type { CLOSED, OPEN }

    /**
     * Retourne une nouvelle cellule{@code Cell}de{@code Type}t.
     * @param t {@code Type}
     * @return Nouvelle cellule {@link #Cell}
     */
    public static Cell cell(final Type t) {
        switch (t) {
            case OPEN:
                return new Cell(false, false, false, false, Content.NOTHING);
            case CLOSED:
                return new Cell(true, true, true, true, Content.NOTHING);
            default:
                return null;
        }
    }

    /**
     * Retourne une nouvelle cellule{@code Cell}contenant{@code c}.
     * @param c Contenu de la cellule
     * @return {@code Cell}
     */
    public static Cell cell(final Content c) {
        return new Cell(false, false, false, false, c);
    }

    /**
     * Retourne true si la cellule contient au moins un mur.
     * @return {@code boolean}
     */
    public boolean hasWall() {
        return northWall || eastWall || westWall || southWall;
    }
}
