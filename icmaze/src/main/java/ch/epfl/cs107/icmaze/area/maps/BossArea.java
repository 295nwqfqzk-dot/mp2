package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public final class BossArea extends ICMazeArea {

    public BossArea() {
        super("SmallArea");
    }

    @Override
    protected void createArea() {
        registerActor(new Background(this, getBehaviorName()));
    }


    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() {
        return new DiscreteCoordinates(5, 15);
    }

    @Override
    public String getTitle() {
        return "icmaze/Boss";
    }
}
