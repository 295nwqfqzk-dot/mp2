package ch.epfl.cs107.icmaze.actor;

import java.util.Queue;

import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.areagame.area.Area;

public abstract class PathFinderEnemy extends Enemy {

    private final int perceptionRadius;
    private static final int MOVE_DURATION = 10;

    public PathFinderEnemy(Area area, Orientation orientation, DiscreteCoordinates position, int maxHealth, int damage,
            int perceptionRadius) {
        super(area, orientation, position, maxHealth, damage);
        this.perceptionRadius = perceptionRadius;
    }

    protected abstract Orientation getNextOrientation();

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (isDisplacementOccurs()) {
            return;
        }

        Orientation next = getNextOrientation();
        if (next != null) {
            if (getOrientation() != next) {
                orientate(next);
            }
            move(getMoveDuration());
        }
    }

    protected int getMoveDuration() {
        return MOVE_DURATION;
    }

    protected Queue<Orientation> shortestPath(DiscreteCoordinates targetPos) {
        if (getOwnerArea() instanceof ICMazeArea) {
            return ((ICMazeArea) getOwnerArea()).shortestPath(getCurrentMainCellCoordinates(), targetPos);
        }
        return null;
    }

    protected boolean isTargetVisible(DiscreteCoordinates target) {
        if (target == null)
            return false;
        int dx = Math.abs(getCurrentMainCellCoordinates().x - target.x);
        int dy = Math.abs(getCurrentMainCellCoordinates().y - target.y);
        return dx <= perceptionRadius && dy <= perceptionRadius;
    }
}
