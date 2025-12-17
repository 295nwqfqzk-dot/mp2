package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

public class Heart extends ICMazeCollectable {

    private static final int ANIMATION_DURATION = 24;
    private final Animation animation;

    public Heart(Area area, DiscreteCoordinates position) {
        super(area, position);

        animation = new Animation(
                "icmaze/heart",
                4,
                1,
                1,
                this,
                16,
                16,
                ANIMATION_DURATION / 4,
                true
        );
    }

    @Override
    public void update(float deltaTime) {
        animation.update(deltaTime);
        super.update(deltaTime);
    }

    @Override
    public void draw(Canvas canvas) {
        animation.draw(canvas);
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
    }
}
