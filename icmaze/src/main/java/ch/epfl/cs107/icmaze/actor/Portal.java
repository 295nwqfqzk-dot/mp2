package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.List;

public class Portal extends AreaEntity implements Interactable {
    public enum State { OPEN, LOCKED, INVISIBLE }

    public static final int NO_KEY_ID = Integer.MIN_VALUE;

    private State state;

    private final String destinationArea;
    private final DiscreteCoordinates destinationCoordinates;
    private final int keyId;

    private final Sprite invisibleSprite;
    private final Sprite lockedSprite;

    public Portal(Area area,
                  Orientation orientation,
                  DiscreteCoordinates position,
                  String destinationArea,
                  DiscreteCoordinates destinationCoordinates,
                  int keyId) {

        super(area, orientation, position);

        this.destinationArea = destinationArea;
        this.destinationCoordinates = destinationCoordinates;
        this.keyId = keyId;

        this.state = State.INVISIBLE; // par défaut

        // sprites donnés dans l'énoncé
        invisibleSprite = new Sprite(
                "icmaze/invisibleDoor_" + orientation.ordinal(),
                (orientation.ordinal() + 1) % 2 + 1,
                orientation.ordinal() % 2 + 1,
                this
        );

        lockedSprite = new Sprite(
                "icmaze/chained_wood_" + orientation.ordinal(),
                (orientation.ordinal() + 1) % 2 + 1,
                orientation.ordinal() % 2 + 1,
                this
        );
    }

    // getters utiles pour apres
    public String getDestinationArea() { return destinationArea; }
    public DiscreteCoordinates getDestinationCoordinates() { return destinationCoordinates; }
    public int getKeyId() { return keyId; }

    public State getState() { return state; }
    public void setState(State state) { this.state = state; }

    @Override
    public void draw(Canvas canvas) {
        if (state == State.INVISIBLE) {
            invisibleSprite.draw(canvas);
        } else if (state == State.LOCKED) {
            lockedSprite.draw(canvas);
        }
        // OPEN rien
    }

    @Override
    public boolean takeCellSpace() {
        // traversable si OPEN
        return state != State.OPEN;
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
    public List<DiscreteCoordinates> getCurrentCells() {
        DiscreteCoordinates coord = getCurrentMainCellCoordinates();
        return List.of(
                coord,
                coord.jump(new Vector(
                        (getOrientation().ordinal() + 1) % 2,
                        getOrientation().ordinal() % 2
                ))
        );
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

}
