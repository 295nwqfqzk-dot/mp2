package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public final class BossArea extends ICMazeArea {

    private final AreaPortals entryPortal;

    public BossArea(AreaPortals entryPortal) {
        super("SmallArea", 8);
        this.entryPortal = entryPortal;
    }

    @Override
    protected void createArea() {
        registerActor(new Background(this, getBehaviorName()));
    }

    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() {
        return getArrivalCoords(entryPortal);
    }

    @Override
    public String getTitle() {
        return "icmaze/Boss";
    }
}
