package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.play.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

import java.util.Collections;
import java.util.List;

public abstract class ICMazeCollectable extends CollectableAreaEntity {

    protected ICMazeCollectable(Area area, DiscreteCoordinates position) {
        super(area, Orientation.DOWN, position);
    }
    @Override
    public void update(float deltaTime) {
        if (isCollected()) {
            getOwnerArea().unregisterActor(this);
            return;
        }
        super.update(deltaTime);
    }
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }
    @Override
    public boolean takeCellSpace() {
        return false;
    }
    @Override
    public boolean isCellInteractable() {
        return true; // y a contact
    }
    @Override
    public boolean isViewInteractable() {
        return false; // nop pas à distance
    }
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        // n'oubli pas le visitor plus tard
    }
}
