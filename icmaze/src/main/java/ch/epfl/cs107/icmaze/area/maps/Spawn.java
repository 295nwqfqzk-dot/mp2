package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public final class Spawn extends ICMazeArea {

    public Spawn() {
        super("SmallArea"); // 2.1: behavior commun
    }

    @Override
    protected void createArea() {
        registerActor(new Background(this, getBehaviorName()));
    }

    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() {
        return new DiscreteCoordinates(2, 10);
    }

    @Override
    public String getTitle() {
        return "icmaze/Spawn";
    }
}
