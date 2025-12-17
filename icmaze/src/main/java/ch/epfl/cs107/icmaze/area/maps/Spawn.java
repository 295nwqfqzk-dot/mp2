package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

public final class Spawn extends ICMazeArea {

    public Spawn() {
        super("SmallArea",8); // 2.1: behavior commun
    }

    @Override
    protected void createArea() {
        registerActor(new Background(this, getBehaviorName()));

        ICMazePlayer player = new ICMazePlayer(this, Orientation.DOWN, getPlayerSpawnPosition());
        registerActor(player);
        setViewCandidate(player);

        registerActor(new Pickaxe(this, Orientation.DOWN, new DiscreteCoordinates(5, 4)));
        registerActor(new Heart(this, new DiscreteCoordinates(4, 5)));
    }

   // @Override
   /* protected void createArea() {
        registerActor(new Background(this, getBehaviorName()));
    }*/

    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() {
        return new DiscreteCoordinates(5, 7); // j'ai changer 2 et 10 avec 5 et 7
    }

    @Override
    public String getTitle() {
        return "icmaze/Spawn";
    }
}
