package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

public abstract class ICMazeEquipment extends ICMazeCollectable {

    protected final Sprite sprite;

    protected ICMazeEquipment(Area area, Orientation orientation, DiscreteCoordinates position, String spriteName) {
        super(area, position);
        orientate(orientation);
        sprite = new Sprite(spriteName, .75f, .75f, this);
    }
    @Override
    public void draw(Canvas canvas) {
        sprite.draw(canvas);
    }
}
