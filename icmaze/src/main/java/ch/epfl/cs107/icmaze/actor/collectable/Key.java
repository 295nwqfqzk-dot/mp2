package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

public class Key extends ICMazeEquipment {
    private final int id;

    public Key(Area area, Orientation orientation, DiscreteCoordinates position, int id) {
        super(area, orientation, position, "icmaze/key"); //
        this.id = id;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    public int getId() {
        return id;
    }
}
