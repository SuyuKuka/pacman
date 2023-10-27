package model;

import config.Cell.Content;
import config.MazeConfig;

import geometry.IntCoordinates;
import geometry.RealCoordinates;
import javafx.scene.image.Image;


/**
 * Implements Pac-Man character using singleton pattern. FIXME: check whether singleton is really a good idea.
 * (Faudra transformer Pac-Man en objet si on veut ajouter du multijoueur local et associer à chaque pacman un pacmanController)
 */
public final class PacMan implements Critter {
    /* Direction de PacMan */
    private Direction direction = Direction.NONE;
    private Direction lastDirection;
    /* Position de PacMan */
    private RealCoordinates pos;
    private int pacManMoved = 0;
    /* Buffer du Controller de PacMan */
    private Direction bufferDirection = Direction.NONE; // Direction stockée à l'appuie d'une touche.
    private final double bufferMaxTime = 0.7; // Temps en seconde avant que la direction stockée dans le buffer se retire.
    private double bufferDurationTime = 0; // Temps passé sans que l'utilisateur n'est appuyé une touche.
    /* Etat énergisé */
    private boolean energized;
    /*Timer:*/
    private double energizedTimer = 0;
    private double maxEnergizedTime = 3; //Temps de l'etat ENERGIZED en seconds

    private PacMan() {
    }

    public static final PacMan INSTANCE = new PacMan();

    @Override
    public RealCoordinates getPos() {
        return pos;
    }

    @Override
    public double getSpeed() {
        return isEnergized() ? 7 : 5;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public void setPos(RealCoordinates pos) {
        this.pos = pos;
    }

    /**
     *
     * @return whether Pac-Man just ate an energizer
     */
    public boolean isEnergized() {
        return energized;
    }

    /* Décide si pacman est ENERGIZED */ 
    public void containsEnergizer (MazeConfig mazeConfig) {
        if (mazeConfig.getCell(new IntCoordinates(pos.round().x(), pos.round().y())).initialContent() == Content.ENERGIZER ) {
            energized = true;
        }
    }

    /* Durée de l'état ENERGIZER */
    public void increaseEnergizedTimer(long deltaTns) {
        if(isEnergized()) {
            energizedTimer += deltaTns / Math.pow(10,9);
            if (energizedTimer >= maxEnergizedTime) {
                energizedTimer = 0;
                energized = false;
            }
        }
    }
    /*Si pendant la durée, Pacman mange un autre Pac-gomme, la durée s'étend*/
    public void resetTimerSiRepasseEnergizer(MazeConfig mazeConfig) {
        if (isEnergized() && mazeConfig.getCell(new IntCoordinates(pos.round().x(), pos.round().y())).initialContent() == Content.ENERGIZER) {
            energizedTimer = 0;
        }
    }

    public Direction getLastDirection() {
        return lastDirection;
    }

    public int getPacManMoved() {
        return pacManMoved;
    }

    public void setPacManMoved(int pacManMoved) {
        this.pacManMoved = pacManMoved;
    }

    public void setBufferDurationTime(double bufferDurationTime) {
      this.bufferDurationTime = bufferDurationTime;
    }

    public Direction getBufferDirection() {
      return bufferDirection;
    }

    public void setBufferDirection(Direction bufferDirection) {
      this.bufferDirection = bufferDirection;
    }

    public void updateBufferDurationTime(long deltaTns) {
        bufferDurationTime += deltaTns / Math.pow(10, 9);
        if (bufferDurationTime >= bufferMaxTime) {
            bufferDurationTime = 0;
            bufferDirection = Direction.NONE;
        }
    }

    public void resetPacMan() {
        PacMan.INSTANCE.pacManMoved = 0;
        PacMan.INSTANCE.energized = false;
        PacMan.INSTANCE.bufferDirection = Direction.NONE;
    }

    public void resetPacManPos(MazeState state) {
        PacMan.INSTANCE.direction = Direction.NONE;
        PacMan.INSTANCE.pos = state.getInitialPos().get(PacMan.INSTANCE);
    }

    /**
     * La méthode arrête Pac-Man lorsqu'il rencontre un mur. Elle met aussi à jour
     * sa position lorsqu'il traverse une case pour faciliter les
     * virages.
     * 
     * @param deltaTns La variation du temps entre deux instants.
     */
    public void updatePacMan(long deltaTns, MazeState state) {
        var curPos = pos;
        var nextPos = nextPos(deltaTns);

        // Nécessaire pour éviter les problèmes de détermination du target selon la direction de Pac-Man
        if (direction != Direction.NONE) lastDirection = direction;

        // Modifie ou non la direction de PacMan en fonction du terrain du buffer
        if (canPacManTurn(bufferDirection, deltaTns, state.getConfig())) {
            nextPos = nextPos(deltaTns, bufferDirection);
            direction = bufferDirection;
            bufferDirection = Direction.NONE;
        }
        nextPos = calibratePacManPos(curPos, nextPos, state.getConfig());
        calibratePos(deltaTns, curPos, nextPos, state.getHeight(), state.getWidth());
        updateBufferDurationTime(deltaTns);

        // Si Pac-Man mange un ENERGIZER
        var pacPos = PacMan.INSTANCE.getPos().round();
        if (!state.getGridState(new IntCoordinates(pacPos.x(), pacPos.y()))) {
            containsEnergizer(state.getConfig());
            resetTimerSiRepasseEnergizer(state.getConfig());
        }
        increaseEnergizedTimer(deltaTns);
    }

    /**
    * Méthode qui calcul la position à la frame suivante de PacMan en fonction de {@code Direction}. <br><br>
    * Si cette position est valable, change la {@code Direction} de PacMan sinon, garde l'ancienne {@code Direction}.
    * 
    * @param Maze MazeConfig utilisée.
    * @param direction Calcul par rapport à cette direction.
    * @param deltaTns Temps écoulée.
    * @return Retourne vraie si un changement de direction est possible, faux sinon.
    */
   public boolean canPacManTurn(Direction direction, long deltaTns, MazeConfig config) {
       if (this.direction == direction || atSpawnEntry()) {
           return false;
       }
       var nextNeighbours = nextPos(deltaTns).intNeighbours();
       if (nextNeighbours.size() >= 4) {
           return false;
       }
       if (nextNeighbours.size() == 2) {
           if ((direction == Direction.NORTH || direction == Direction.SOUTH) && (int) pos.x() != pos.x()) {
               return false;
           }
           if ((direction == Direction.EAST || direction == Direction.WEST) && (int) pos.y() != pos.y()) {
               return false;
           }
           var curNeighbours = pos.intNeighbours();
           for (var n: curNeighbours) {
               if (direction.wall(config.getCell(n))) {
                   return false;
               }
           }
       }
       return true;
   }

    /**
     * Méthode qui arrête la course de PacMan et retire sa direction si il rencontre un mur de face.
     * @param curPos  {@code RealCoordinates} actuelle de {@code Pac-Man}.
     * @param nextPos {@code RealCoordinates} supposée (= s'il ne traverse pas de mur) de {@code Pac-Man}.
     * @return Retourne {@code nextPos} modifié ou non.
     */
     public RealCoordinates calibratePacManPos(RealCoordinates curPos, RealCoordinates nextPos, MazeConfig config) {
        if (!curPos.intNeighbours().containsAll(nextPos.intNeighbours())) {
            for (var n : curPos.intNeighbours()) {
                if (direction.wall(config.getCell(n))) {
                    RealCoordinates newPos = pos.adjustCoordinates(direction);
                    direction = Direction.NONE;
                    return newPos;
                }
            }
        }
        return nextPos;
    }

    public boolean atSpawnEntry() {
        return (int) pos.x() == 8 && (int) pos.y() == 8;
    }
}
