package config;

public record Cell(
    boolean northWall, boolean eastWall, boolean southWall, boolean westWall,
        Content initialContent, boolean restricted) {
    /** Contenu qu'a une {@link Cell}. */
    public enum Content {
        /** La {@link Cell} ne contient rien. */
        NOTHING,
        /** La {@link Cell} contient un Pac-Gomme que {@link PacMan} peut manger. */
        DOT,
        /** La {@link Cell} contient un Super Pac-Gomme que {@link PacMan}
         * peut manger pour être énergisé.
         */
        ENERGIZER,
        /** Indique que la {@link Cell} est une cellule du Spawn des {@link Ghost}.
         * <br><br> (N'affiche rien mais est utile pour les fonctions).
        */
        SPAWN,
        /** Indique que la {@link Cell} est une cellule d'un tunnel du jeu.
         * <br><br> (N'affiche rien mais est utile pour modifier la vitesse des {@link Ghost}).
        */
        TUNNEL
    }
    /** Type que qu'a une {@link Cell}. (fermé ou ouverte). */
    public enum Type {
        /** Indique que la {@link Cell} ne contient pas de mur. */
        OPEN,
        /** Indique que la {@link Cell} est totalement fermé (4 murs). */
        CLOSED
    }

    /**
     * Retourne une nouvelle cellule{@code Cell}de{@code Type}t.
     * @param t {@code Type}
     * Retourne une nouvelle cellule{@code Cell}de{@code Type}t.
     * @param t {@code Type}
     * @return Nouvelle cellule {@link #Cell}
     */
    public static Cell cell(final Type t) {
        switch (t) {
            case OPEN:
                return new Cell(false, false, false, false, Content.NOTHING, false);
            case CLOSED:
                return new Cell(true, true, true, true, Content.NOTHING, false);
            default:
                return null;
        }
    }

    /**
     * Retourne une nouvelle cellule{@code Cell}contenant{@code c}.
     * Si {@code restricted} est vraie, les {@link Ghost} ne peuvent
     * pas s'orienter vers le haut à l'intérieur de ces cellules.
     * @param c Contenu de la cellule
     * @param restricted {@code restricted}
     * @return {@code Cell}
     */
    public static Cell cell(final Content c, final boolean restricted) {
        return new Cell(false, false, false, false, c, restricted);
    }

    /**
     * Retourne une nouvelle cellule{@code Cell}contenant{@code c}.
     * @param c Contenu de la cellule
     * @return {@code Cell}
     */
    public static Cell cell(final Content c) {
        return new Cell(false, false, false, false, c, false);
    }

    /**
     * Méthode qui renvoie vraie si la cellule contient au moins un mur.
     * @return {@code boolean}
     */
    public boolean hasWall() {
        return northWall || eastWall || westWall || southWall;
    }

    /**
     * Renvoie vraie si le contenu de la cellule doit être affiché graphiquement.
     * (NOTHING, SPAWN et TUNNEL retournent false).
     * @return
     */
    public boolean isDrawn() {
        switch (initialContent) {
            case NOTHING:
            case SPAWN:
            case TUNNEL:
                return false;
            default:
                break;
        }
        return true;
    }
}
