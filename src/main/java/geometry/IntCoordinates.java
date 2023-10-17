package geometry;

public record IntCoordinates(int x, int y) {
    public RealCoordinates toRealCoordinates(double scale) {
        return new RealCoordinates(x * scale, y * scale);
    }
}
