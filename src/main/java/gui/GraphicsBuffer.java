package gui;

public final class GraphicsBuffer {
    // Buffer pour les images des critters
    private double updateBuffer = 0;
    private double alternateBuffer = 0;
    /** Boolean indiquant s'il faut clignoter les fantômes. */
    private boolean alternate = false;
    /** Boolean indiquant s'il faut remettre l'image par défaut du Pac-Man. */
    private boolean resetPacImg = false;
    /** Boolean indiquant si la bouche du Pac-Man est ouverte. */
    private boolean isPacManMouthOpen = false;

    public double getUpdateBuffer() {
        return updateBuffer;
    }

    public double getAlternateBuffer() {
        return alternateBuffer;
    }

    public void setUpdateBuffer(final double graphicsBuffer) {
        this.updateBuffer = graphicsBuffer;
    }

    public void setAlternateBuffer(final double alternateBuffer) {
        this.alternateBuffer = alternateBuffer;
    }

    public boolean isAlternate() {
        return alternate;
    }

    public void setAlternate(final boolean alternate) {
        this.alternate = alternate;
    }

    public void setResetPacImg(final boolean resetPacImg) {
        this.resetPacImg = resetPacImg;
    }

    public boolean isResetPacImg() {
        return resetPacImg;
    }

    public boolean isPacManMouthOpen() {
        return isPacManMouthOpen;
    }

    public void setPacManMouthOpen(final boolean mouthOpen) {
        isPacManMouthOpen = mouthOpen;
    }
}
