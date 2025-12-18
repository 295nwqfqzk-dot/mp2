package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.actor.Portal;
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
        Portal westPortal = new Portal(
                this,
                AreaPortals.W.getOrientation().opposite(),
                getPortalCoords(AreaPortals.W),
                "icmaze/LargeArea[3]",
                new DiscreteCoordinates(30, 16),
                Portal.NO_KEY_ID
        );
        westPortal.setState(Portal.State.OPEN);
        registerActor(westPortal);

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
