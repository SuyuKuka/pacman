package config;

public record Cell(
    boolean northWall, boolean eastWall, boolean southWall, boolean westWall,
    Cell.Content initialContent
    ) {
    /** Contenus dans chaque cellule. */
    public enum Content { NOTHING, DOT, ENERGIZER }
    /** Types de cellule selon les murs qu'elle possède. */
    public enum Type {
        /** Cellule sans mur. */
        OPEN,
        /** Cellule avec les 4 murs. */
        CLOSED,
        /** Cellule avec les murs Nord et Sud. */
        HPIPE,
        /** Cellule avec les murs Ouest et Est. */
        VPIPE,
        /** Cellule avec murs en coin. */
        NWCOR, NECOR, SWCOR, SECOR,
        /** Cellule en U. */
        NU, EU, SU, WU,
        /** Cellule avec un seul mur. */
        N, E, S, W
    }

    /**
     * Méthode qui renvoie une nouvelle cellule
     * d'après le {@link #Type} et le {@link #Content}.
     * @param t {@link #Type}de la cellule
     * @param c {@link #Content}de la cellule
     * @return Nouvelle cellule {@link #Cell}
     */
    public static Cell cell(final Type t, final Content c) {
        switch (t) {
            case OPEN:
                return new Cell(false, false, false, false, c);
            case CLOSED:
                return new Cell(true, true, true, true, c);
            // Straight pipes
            case HPIPE: // Horizontal
                return new Cell(true, false, true, false, c);
            case VPIPE: // Vertical
                return new Cell(false, true, false, true, c);
            // Corner pipes
            case NWCOR:
                return new Cell(true, false, false, true, c);
            case NECOR:
                return new Cell(true, true, false, false, c);
            case SWCOR:
                return new Cell(false, false, true, true, c);
            case SECOR:
                return new Cell(false, true, true, false, c);
            // U-shaped pipes
            case NU:
                return new Cell(false, true, true, true, c);
            case EU:
                return new Cell(true, false, true, true, c);
            case SU:
                return new Cell(true, true, false, true, c);
            case WU:
                return new Cell(true, true, true, false, c);
            // Single wall
            case N:
                return new Cell(true, false, false, false, c);
            case E:
                return new Cell(false, true, false, false, c);
            case S:
                return new Cell(false, false, true, false, c);
            case W:
                return new Cell(false, false, false, true, c);
            default:
                return null;
        }
    }
}
