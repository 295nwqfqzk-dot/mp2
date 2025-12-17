package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.KeyBindings;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ICMazePlayer extends ICMazeActor implements Interactor {

    private final Set<Integer> keys = new HashSet<>();
    private final ICMazePlayerInteractionHandler handler = new ICMazePlayerInteractionHandler();
    private State state;
    private static final int STEP = 1;
    private static final KeyBindings.PlayerKeyBindings PLAYER_KEY_BINDINGS = KeyBindings.PLAYER_KEY_BINDINGS;
    private final OrientedAnimation animation;

    // mon enum(PAS celui de Swing)
    public enum State {
        IDLE,
        INTERACTING
    }

    public ICMazePlayer(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);
        state = State.IDLE;
        final Vector anchor = new Vector(0, 0);
        final Orientation[] orders = {
                Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT
        };

        animation = new OrientedAnimation(
                "icmaze/player",
                4,
                this,
                anchor,
                orders,
                4, 1, 2, 16, 32,
                true
        );
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        // rien pour l'instant mais n;oublie pas
    }
    @Override
    public boolean takeCellSpace() {
        return true; // joueur non traversable
    }

    @Override
    public void update(float deltaTime) {

        switch (state) {
            case IDLE:
                Keyboard keyboard = getOwnerArea().getKeyboard();

                moveIfPressed(Orientation.LEFT, keyboard.get(PLAYER_KEY_BINDINGS.left()));
                moveIfPressed(Orientation.UP, keyboard.get(PLAYER_KEY_BINDINGS.up()));
                moveIfPressed(Orientation.RIGHT, keyboard.get(PLAYER_KEY_BINDINGS.right()));
                moveIfPressed(Orientation.DOWN, keyboard.get(PLAYER_KEY_BINDINGS.down()));

                if (isDisplacementOccurs()) { // si on bouge ça update sinon on reset
                    animation.update(deltaTime);
                } else {
                    animation.reset();
                }
                break;

            case INTERACTING:
                animation.reset();
                break;
        }

        super.update(deltaTime);
    }

    private void moveIfPressed(Orientation orientation, Button button) {
        if (button.isDown()) {
            orientate(orientation);
            move(STEP);
        }
    }
    @Override
    public void draw(Canvas canvas) {
        animation.draw(canvas);
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(
                getCurrentMainCellCoordinates().jump(getOrientation().toVector())
        );
    }


    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    @Override
    public boolean wantsViewInteraction() {
        return state == State.INTERACTING;
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }
    private class ICMazePlayerInteractionHandler implements ICMazeInteractionVisitor {

        @Override
        public void interactWith(Pickaxe pickaxe, boolean isCellInteraction) {
            if (isCellInteraction) {
                pickaxe.collect();
            }
        }

        @Override
        public void interactWith(Heart heart, boolean isCellInteraction) {
            if (isCellInteraction) {
                heart.collect();
            }
        }
        @Override
        public void interactWith(Key key, boolean isCellInteraction) {
            if (isCellInteraction) {
                addKey(key.getId());
                key.collect();
            }
        }
    }


    public void addKey(int id) {
        keys.add(id);
    }

    public boolean hasKey(int id) {
        return keys.contains(id);
    }

}


