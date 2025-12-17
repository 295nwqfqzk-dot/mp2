package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.ICMaze;
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

    // mon enum
    public enum State {
        IDLE,
        INTERACTING
    }

    private State state = State.IDLE;

    private final Set<Integer> keys = new HashSet<>();

    private final OrientedAnimation animation;

    private static final int STEP = 1;
    private static final KeyBindings.PlayerKeyBindings PLAYER_KEY_BINDINGS = KeyBindings.PLAYER_KEY_BINDINGS;

    // demande d’interaction à distance pendant 1 frame
    private boolean requestViewInteraction = false;

    private final ICMazePlayerInteractionHandler handler = new ICMazePlayerInteractionHandler();

    public ICMazePlayer(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);

        final Vector anchor = new Vector(0, 0);
        final Orientation[] orders = {Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT};

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

    // --- Keys memory ---
    public void addKey(int id) { keys.add(id); }
    public boolean hasKey(int id) { return keys.contains(id); }

    // --- Interactor ---
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
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
        return requestViewInteraction;
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }

    // --- Actor ---
    @Override
    public boolean takeCellSpace() {
        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        animation.draw(canvas);
    }

    @Override
    public void update(float deltaTime) {
        Keyboard keyboard = getOwnerArea().getKeyboard();

        switch (state) {
            case IDLE:
                moveIfPressed(Orientation.LEFT, keyboard.get(PLAYER_KEY_BINDINGS.left()));
                moveIfPressed(Orientation.UP, keyboard.get(PLAYER_KEY_BINDINGS.up()));
                moveIfPressed(Orientation.RIGHT, keyboard.get(PLAYER_KEY_BINDINGS.right()));
                moveIfPressed(Orientation.DOWN, keyboard.get(PLAYER_KEY_BINDINGS.down()));

                // touche interaction (E par défaut)
                Button interactKey = keyboard.get(PLAYER_KEY_BINDINGS.interact());
                if (interactKey.isPressed()) {
                    requestViewInteraction = true;  // 1 frame
                    state = State.INTERACTING;
                }

                if (isDisplacementOccurs()) animation.update(deltaTime);
                else animation.reset();
                break;

            case INTERACTING:
                // pour l’instant : juste revenir à IDLE
                state = State.IDLE;
                animation.reset();
                break;
        }

        super.update(deltaTime);

        // on coupe la demande à distance après 1 update
        requestViewInteraction = false;
    }

    private void moveIfPressed(Orientation orientation, Button button) {
        if (button.isPressed()) {
            orientate(orientation);
            move(STEP);
        }
    }

    // --- Interactable (Visitor entry point) ---
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        if (v instanceof ICMazeInteractionVisitor) {
            ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
        }
    }

    // --- Handler ---
    private class ICMazePlayerInteractionHandler implements ICMazeInteractionVisitor {

        @Override
        public void interactWith(Pickaxe pickaxe, boolean isCellInteraction) {
            if (isCellInteraction) pickaxe.collect();
        }

        @Override
        public void interactWith(Heart heart, boolean isCellInteraction) {
            if (isCellInteraction) heart.collect();
        }

        @Override
        public void interactWith(Key key, boolean isCellInteraction) {
            if (isCellInteraction) {
                addKey(key.getId());
                key.collect();
            }
        }

        @Override
        public void interactWith(Portal portal, boolean isCellInteraction) {
            if (isCellInteraction) return;

            // ouverture si besoin
            if (portal.getState() != Portal.State.OPEN) {
                int needed = portal.getKeyId();
                if (needed == Portal.NO_KEY_ID || hasKey(needed)) {
                    portal.setState(Portal.State.OPEN);
                } else {
                    return;
                }
            }

            // IMPORTANT : la téléportation sera faite dans ICMaze (pas ici)
            // ex: game.switchArea(portal, ICMazePlayer.this);
        }
    }

}


