package ch.epfl.cs107.icmaze.area;

import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

public abstract class ICMazeArea extends Area {

    public static final float DEFAULT_SCALE_FACTOR = 11.f;
    private final String behaviorName;

    protected ICMazeArea(String behaviorName) {
        this.behaviorName = behaviorName;
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
            createArea();
            return true;
        }
        return false;
    }

    @Override
    public final float getCameraScaleFactor() {
        return DEFAULT_SCALE_FACTOR;
    }
}
