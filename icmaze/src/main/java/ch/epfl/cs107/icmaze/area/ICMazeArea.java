package ch.epfl.cs107.icmaze.area;

import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Window;

public abstract class ICMazeArea extends Area {

    public static final float DEFAULT_SCALE_FACTOR = 11.f;
    private final String behaviorName;
    private final int size;


    private boolean isStarted;

    protected ICMazeArea(String behaviorName, int size) {
        this.behaviorName = behaviorName;
        this.size = size;
    }
    public enum AreaPortals {
        N(Orientation.UP),
        W(Orientation.LEFT),
        S(Orientation.DOWN),
        E(Orientation.RIGHT);
        private final Orientation orientation;
        AreaPortals(Orientation orientation) {
            this.orientation = orientation;
        }
        public Orientation getOrientation() {
            return orientation;
        }
    }
    public DiscreteCoordinates getPortalCoords(AreaPortals portal) {
        return switch (portal) {
            case N -> new DiscreteCoordinates(size / 2, size + 1);
            case S -> new DiscreteCoordinates(size / 2, 0);
            case W -> new DiscreteCoordinates(0, size / 2);
            case E -> new DiscreteCoordinates(size + 1, size / 2);
        };
    }

    public DiscreteCoordinates getArrivalCoords(AreaPortals arrival) {
        return switch (arrival) {
            case N -> new DiscreteCoordinates(size / 2 + 1, size);
            case S -> new DiscreteCoordinates(size / 2 + 1, 1);
            case W -> new DiscreteCoordinates(1, size / 2 + 1);
            case E -> new DiscreteCoordinates(size, size / 2 + 1);
        };
    }

    public static AreaPortals opposite(AreaPortals p) {
        return switch (p) {
            case N -> AreaPortals.S;
            case S -> AreaPortals.N;
            case E -> AreaPortals.W;
            case W -> AreaPortals.E;
        };
    }

    protected final String getBehaviorName() {
        return behaviorName;
    }
    protected abstract void createArea();
    public abstract DiscreteCoordinates getPlayerSpawnPosition();

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            setBehavior(new ICMazeBehavior(window, behaviorName));
            if (!isStarted) {
                createArea();
                isStarted = true;
            }
            return true;
        }
        return false;
    }

    @Override
    public float getCameraScaleFactor() {
        return DEFAULT_SCALE_FACTOR;
    }
}
