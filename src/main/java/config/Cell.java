package config;

public record Cell(boolean northWall, boolean eastWall, boolean southWall, boolean westWall, Cell.Content initialContent) {
    public enum Content {NOTHING, DOT, ENERGIZER}
    public enum Type {OPEN, CLOSED, HPIPE, VPIPE, NWCOR, NECOR, SWCOR, SECOR, NU, EU, SU, WU, N, E, S, W}

    public static Cell cell(Type t, Content c) {
        switch(t) {
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
        }
        return null;
    }
}
