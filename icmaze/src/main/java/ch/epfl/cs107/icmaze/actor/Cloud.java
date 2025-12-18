package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public class Cloud extends AreaEntity {

    private final Animation animation;
    // ANIMATION_DURATION vaut 24 (selon enoncé)
    private static final int ANIMATION_DURATION = 24;

    public Cloud(Area area, DiscreteCoordinates position) {
        super(area, Orientation.DOWN, position);
        // new Animation("icmaze/vanish", 7, 2, 2, this , 32, 32, new Vector(-0.5f,
        // 0.0f), ANIMATION_DURATION/7, false);
        this.animation = new Animation(
                "icmaze/vanish",
                7,
                2, 2,
                this,
                32, 32,
                new Vector(-0.5f, 0.0f),
                ANIMATION_DURATION / 7,
                false);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        animation.update(deltaTime);
        if (animation.isCompleted()) {
            getOwnerArea().unregisterActor(this);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        animation.draw(canvas);
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
        return false;
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        // No interaction
    }
}
