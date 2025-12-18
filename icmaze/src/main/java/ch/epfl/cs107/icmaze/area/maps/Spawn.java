package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.ICMaze;
import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

public final class Spawn extends ICMazeArea {

    private final ICMaze game;

    public Spawn(ICMaze game) {
        super("SmallArea", 8); // 2.1: behavior commun
        this.game = game;
    }

    @Override
    protected void createArea() {
        registerActor(new Background(this, getBehaviorName()));

        ICMazePlayer player = new ICMazePlayer(game, this, Orientation.DOWN, new DiscreteCoordinates(5, 7));
        registerActor(player);
        setViewCandidate(player);

        registerActor(new Pickaxe(this, Orientation.DOWN, new DiscreteCoordinates(5, 4)));
        registerActor(new Heart(this, new DiscreteCoordinates(4, 5)));
        registerActor(new Key(this, Orientation.DOWN, new DiscreteCoordinates(6, 5), Integer.MAX_VALUE));
        registerActor(new Key(this, Orientation.DOWN, new DiscreteCoordinates(1, 2), Integer.MAX_VALUE - 1));
        registerActor(new ch.epfl.cs107.icmaze.actor.Rock(this, new DiscreteCoordinates(4, 2)));

        Portal eastPortal = new Portal(
                this,
                AreaPortals.E.getOrientation().opposite(),
                getPortalCoords(AreaPortals.E),
                "icmaze/SmallArea[1]",
                new DiscreteCoordinates(1, 4),
                Integer.MAX_VALUE);
        eastPortal.setState(Portal.State.LOCKED);
        registerActor(eastPortal);

        Portal northPortal = new Portal(this, AreaPortals.N.getOrientation().opposite(), getPortalCoords(AreaPortals.N),
                "icmaze/Spawn", new DiscreteCoordinates(5, 5), Portal.NO_KEY_ID);
        registerActor(northPortal);

        Portal westPortal = new Portal(this, AreaPortals.W.getOrientation().opposite(), getPortalCoords(AreaPortals.W),
                "icmaze/Spawn", new DiscreteCoordinates(5, 5), Portal.NO_KEY_ID);
        registerActor(westPortal);

        Portal southPortal = new Portal(this, AreaPortals.S.getOrientation().opposite(), getPortalCoords(AreaPortals.S),
                "icmaze/Spawn", new DiscreteCoordinates(5, 5), Portal.NO_KEY_ID);
        registerActor(southPortal);
    }

    // @Override
    /*
     * protected void createArea() {
     * registerActor(new Background(this, getBehaviorName()));
     * }
     */

    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() {
        return new DiscreteCoordinates(5, 7); // j'ai changer 2 et 10 avec 5 et 7
    }

    @Override
    public String getTitle() {
        return "icmaze/Spawn";
    }
}
