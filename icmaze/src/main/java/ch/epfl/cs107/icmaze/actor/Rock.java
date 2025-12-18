package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public class Rock extends AreaEntity {

    private final Sprite sprite;
    private int hp = 3;
    private final ch.epfl.cs107.icmaze.actor.Health health;

    public Rock(Area area, DiscreteCoordinates position) {
        super(area, Orientation.DOWN, position);
        sprite = new Sprite("rock.2", 1, 1, this);
        health = new ch.epfl.cs107.icmaze.actor.Health(this, ch.epfl.cs107.play.math.Transform.I.translated(0, 1.1f), 3,
                false);
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public boolean takeCellSpace() {
        return true;
    }

    @Override
    public boolean isCellInteractable() {
        return false;
    }

    @Override
    public boolean isViewInteractable() {
        return true;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    @Override
    public void draw(Canvas canvas) {
        sprite.draw(canvas);
        if (hp < 3) {
            health.draw(canvas);
        }
    }

    public void withdraw() {
        hp--;
        health.decrease(1);
        if (hp <= 0) {
            getOwnerArea().unregisterActor(this);
            getOwnerArea().registerActor(new Cloud(getOwnerArea(), getCurrentMainCellCoordinates()));
            if (ch.epfl.cs107.icmaze.RandomGenerator.rng.nextBoolean()) {
                getOwnerArea().registerActor(new ch.epfl.cs107.icmaze.actor.collectable.Heart(getOwnerArea(),
                        getCurrentMainCellCoordinates()));
            }
        }
    }
}
