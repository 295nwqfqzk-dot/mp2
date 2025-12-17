package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.play.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

import java.util.Collections;
import java.util.List;

public abstract class ICMazeActor extends MovableAreaEntity {
    protected ICMazeActor(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);
    }
    // comme ghostplayer
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }
    @Override
    public boolean takeCellSpace() {
        return false; // puisque traversable
    }
    @Override
    public boolean isCellInteractable() {
        return true;
    }
    @Override
    public boolean isViewInteractable() {
        return false; // puisque il peut être l’objet d’interactions de contact uniquement
    }
    // recours à l'aide d'une LLM
    public void enterArea(Area area, DiscreteCoordinates position) {
        setOwnerArea(area);
        setCurrentPosition(position.toVector());
        area.registerActor(this);
    }
    public void leaveArea() {
        getOwnerArea().unregisterActor(this);
    }

}
