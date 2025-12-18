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
import ch.epfl.cs107.play.math.Transform;

public class ICMazePlayer extends ICMazeActor implements Interactor {

    // États du joueur
    public enum State {
        IDLE,
        INTERACTING,
        ATTACKING_WITH_PICKAXE
    }

    private State state = State.IDLE;

    private final Set<Integer> keys = new HashSet<>();
    private boolean hasPickaxe = false;

    private final OrientedAnimation animation;
    private final OrientedAnimation attackAnimation;

    private static final int MAX_HEALTH = 5;
    private static final int IMMUNITY_DURATION = 24;
    private final Health health;
    private final MyCooldown immunity; // Gestion manuelle du cooldown

    private static final int STEP = 1;
    private static final KeyBindings.PlayerKeyBindings PLAYER_KEY_BINDINGS = KeyBindings.PLAYER_KEY_BINDINGS;

    // Demande d'interaction vue (1 frame)
    private boolean requestViewInteraction = false;

    private final ICMazePlayerInteractionHandler handler = new ICMazePlayerInteractionHandler();
    private final ICMaze game;

    public ICMazePlayer(ICMaze game, Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);
        this.game = game;

        final Vector anchor = new Vector(0, 0);
        final Orientation[] orders = { Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT };

        animation = new OrientedAnimation(
                "icmaze/player",
                4,
                this,
                anchor,
                orders,
                4, 1, 2, 16, 32,
                true);

        final Vector attackAnchor = new Vector(-.5f, 0);
        // Ordre standard des sprites
        final Orientation[] attackOrders = { Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT };

        attackAnimation = new OrientedAnimation(
                "icmaze/player.pickaxe",
                8, // Plus lent pour être visible
                this,
                attackAnchor,
                attackOrders,
                4, 2, 2, 32, 32,
                false // Pas de boucle
        );

        health = new Health(this, Transform.I.translated(0, 1.75f), MAX_HEALTH, true);
        immunity = new MyCooldown(IMMUNITY_DURATION);
    }

    // --- Mémoire des clés ---
    public void addKey(int id) {
        keys.add(id);
    }

    public boolean hasKey(int id) {
        return keys.contains(id);
    }

    // --- Gestion des interactions ---
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(
                getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
    }

    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    @Override
    public boolean wantsViewInteraction() {
        return state == State.INTERACTING || (state == State.ATTACKING_WITH_PICKAXE && requestViewInteraction);
    }

    @Override
    public boolean isViewInteractable() {
        return true;
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }

    // --- Méthodes d'acteur ---
    @Override
    public boolean takeCellSpace() {
        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        // Clignoter si immunisé
        if (!immunity.isCompleted()) {
            if (immunity.getTicks() % 2 == 0) {
                drawPlayer(canvas);
            }
        } else {
            drawPlayer(canvas);
        }
        health.draw(canvas);
    }

    private void drawPlayer(Canvas canvas) {
        if (state == State.ATTACKING_WITH_PICKAXE) {
            attackAnimation.draw(canvas);
        } else {
            animation.draw(canvas);
        }
    }

    @Override
    public void update(float deltaTime) {
        // Effacer demande de collision précédente
        if (state != State.INTERACTING) {
            requestViewInteraction = false;
        }

        health.increase(0);
        immunity.update(); // Compteur manuel
        Keyboard keyboard = getOwnerArea().getKeyboard();

        switch (state) {
            case IDLE:
                moveIfPressed(Orientation.LEFT, keyboard.get(PLAYER_KEY_BINDINGS.left()));
                moveIfPressed(Orientation.UP, keyboard.get(PLAYER_KEY_BINDINGS.up()));
                moveIfPressed(Orientation.RIGHT, keyboard.get(PLAYER_KEY_BINDINGS.right()));
                moveIfPressed(Orientation.DOWN, keyboard.get(PLAYER_KEY_BINDINGS.down()));

                // Touche d'interaction
                Button interactKey = keyboard.get(PLAYER_KEY_BINDINGS.interact());
                if (interactKey.isPressed()) {
                    requestViewInteraction = true; // 1 frame
                    state = State.INTERACTING;
                }

                if (isDisplacementOccurs())
                    animation.update(deltaTime);
                else
                    animation.reset();
                break;

            case INTERACTING:
                // Si la touche n'est plus appuyée, on repasse en IDLE
                if (!keyboard.get(PLAYER_KEY_BINDINGS.interact()).isDown()) {
                    state = State.IDLE;
                    animation.reset();
                }
                break;
            case ATTACKING_WITH_PICKAXE:
                if (attackAnimation.isCompleted()) {
                    state = State.IDLE;
                    attackAnimation.reset();
                } else {
                    attackAnimation.update(deltaTime);
                }
                break;
        }

        // Déclenchement d'attaque
        if (state == State.IDLE && hasPickaxe && keyboard.get(PLAYER_KEY_BINDINGS.pickaxe()).isPressed()) {
            state = State.ATTACKING_WITH_PICKAXE;
            attackAnimation.reset();
            requestViewInteraction = true; // Déclenche l'interaction (coup)
        }

        super.update(deltaTime);
    }

    private void moveIfPressed(Orientation orientation, Button button) {
        if (button.isPressed()) {
            orientate(orientation);
            move(STEP);
        }
    }

    public void takeDamage(int damage) {
        if (immunity.isCompleted()) {
            health.decrease(damage);
            immunity.start();
            if (health.isOff()) {
                game.reset();
            }
        }
    }

    // --- Interactable (Point d'entrée visiteur) ---
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        if (v instanceof ICMazeInteractionVisitor) {
            ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
        }
    }

    // --- Gestionnaire ---
    private class ICMazePlayerInteractionHandler implements ICMazeInteractionVisitor {

        @Override
        public void interactWith(Pickaxe pickaxe, boolean isCellInteraction) {
            if (isCellInteraction) {
                hasPickaxe = true;
                pickaxe.collect();
            }
        }

        @Override
        public void interactWith(Rock rock, boolean isCellInteraction) {
            if (hasPickaxe && !isCellInteraction) {
                rock.withdraw();
            }
        }

        @Override
        public void interactWith(LogMonster monster, boolean isCellInteraction) {
            if (hasPickaxe && !isCellInteraction && state == State.ATTACKING_WITH_PICKAXE) {
                monster.takeDamage(1); // Le joueur inflige 1 dégât
            }
        }

        @Override
        public void interactWith(Heart heart, boolean isCellInteraction) {
            if (isCellInteraction) {
                health.increase(1);
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

        @Override
        public void interactWith(Portal portal, boolean isCellInteraction) {
            if (isCellInteraction) {
                game.switchArea(portal, ICMazePlayer.this);
                return;
            }

            if (portal.getState() != Portal.State.OPEN) {
                int needed = portal.getKeyId();
                if (needed == Portal.NO_KEY_ID || hasKey(needed)) {
                    portal.setState(Portal.State.OPEN);
                }
            }
        }
    }

    // Cooldown manuel simple
    private class MyCooldown {
        int duration;
        int current;

        public MyCooldown(int duration) {
            this.duration = duration;
            this.current = duration; // Commence terminé
        }

        public void update() {
            if (current < duration)
                current++;
        }

        public boolean isCompleted() {
            return current >= duration;
        }

        public int getTicks() {
            return current;
        }

        public void start() {
            current = 0;
        }
    }
}
