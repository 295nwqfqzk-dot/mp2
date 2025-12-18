package ch.epfl.cs107.icmaze.actor;

import java.util.List;
import java.util.Queue;
import java.util.ArrayList;
import java.util.LinkedList;

import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.Path;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.window.Canvas;

public class LogMonster extends PathFinderEnemy implements Interactor {

    private static final int PERCEPTION_RADIUS = 3;
    private static final int HP = 3;
    private static final int DAMAGE = 1;

    private enum State {
        SLEEPING,
        RANDOM_MOVE,
        CHASING
    }

    private State state;
    private ICMazePlayer target;

    private final ch.epfl.cs107.icmaze.actor.util.Cooldown reorientationCD;
    private final ch.epfl.cs107.icmaze.actor.util.Cooldown transitionCD;

    private final Animation sleepAnim;
    private final Animation awakeAnim;

    private final int difficulty;
    private Path graphicPath; // Debug path visualization

    private final ICMazeInteractionVisitor interactionHandler = new ICMazeInteractionVisitor() {
        @Override
        public void interactWith(ch.epfl.cs107.icmaze.actor.ICMazePlayer player, boolean isCellInteraction) {
            memorizePlayer(player);

            if (state == State.SLEEPING) {
                state = State.CHASING;
            }
            if (state != State.SLEEPING) {
                if (isCellInteraction ||
                        player.getCurrentMainCellCoordinates()
                                .equals(getCurrentMainCellCoordinates().jump(getOrientation().toVector()))) {
                    player.takeDamage(DAMAGE);
                }
            }
        }
    };

    public LogMonster(Area area, Orientation orientation, DiscreteCoordinates position, int difficulty) {
        super(area, orientation, position, HP, DAMAGE, PERCEPTION_RADIUS);
        this.difficulty = difficulty;

        // Délais
        reorientationCD = new ch.epfl.cs107.icmaze.actor.util.Cooldown(0.75f);
        transitionCD = new ch.epfl.cs107.icmaze.actor.util.Cooldown(3f);

        // Probabilité de sommeil initiale selon la difficulté
        double sleepProb = Math.max(0, 1.0 - (difficulty * 0.1));
        if (RandomGenerator.rng.nextDouble() < sleepProb) {
            this.state = State.SLEEPING;
        } else {
            this.state = State.RANDOM_MOVE;
        }

        // Animations
        final Vector anchor = new Vector(0, 0);
        awakeAnim = new Animation("icmaze/logMonster", 4, 2f, 2f, this, 32, 32, anchor, 8, true);
        sleepAnim = new Animation("icmaze/logMonster.sleeping", 1, 2f, 2f, this, 32, 32, anchor, 8, true);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        // Debug état
        // System.out.println("LogMonster State: " + state + " Pos: " +
        // getCurrentMainCellCoordinates());

        switch (state) {
            case SLEEPING:
                if (target != null && isTargetVisible(target.getCurrentMainCellCoordinates())) {
                    state = State.CHASING;
                    break;
                }

                if (reorientationCD.ready(deltaTime)) {
                    orientate(Orientation.fromInt((getOrientation().ordinal() + 1) % 4)); // Tourne à gauche
                }

                // Réveil aléatoire (si joueur pas vu)
                if (transitionCD.ready(deltaTime)) {
                    double wakeProb = Math.min(0.9, 0.3 + (difficulty * 0.05));
                    if (RandomGenerator.rng.nextDouble() < wakeProb) {
                        state = State.RANDOM_MOVE;
                    }
                }
                break;

            case RANDOM_MOVE:
                if (reorientationCD.ready(deltaTime)) {
                    orientate(Orientation.fromInt(RandomGenerator.rng.nextInt(4)));
                }

                // Détection immédiate du joueur
                if (target != null && isTargetVisible(target.getCurrentMainCellCoordinates())) {
                    state = State.CHASING;
                    graphicPath = null;
                    return;
                }

                if (transitionCD.ready(deltaTime)) {
                    // Retourne dormir
                    double sleepBackProb = 0.4 / (1.0 + difficulty * 0.5);
                    if (RandomGenerator.rng.nextDouble() < sleepBackProb) {
                        state = State.SLEEPING;
                    }
                }
                break;

            case CHASING:
                if (target == null || !isTargetVisible(target.getCurrentMainCellCoordinates())) {
                    state = State.RANDOM_MOVE;
                    graphicPath = null;
                }
                break;
        }

        if (state == State.SLEEPING)
            sleepAnim.update(deltaTime);
        else
            awakeAnim.update(deltaTime);
    }

    @Override
    public void draw(Canvas canvas) {
        if (state == State.SLEEPING) {
            sleepAnim.draw(canvas);
        } else {
            awakeAnim.draw(canvas);
        }
        if (graphicPath != null) {
            graphicPath.draw(canvas);
        }
        super.draw(canvas);
    }

    @Override
    protected Orientation getNextOrientation() {
        if (state == State.SLEEPING)
            return null;

        if (state == State.RANDOM_MOVE) {
            return getOrientation();
        }

        if (state == State.CHASING && target != null) {
            Queue<Orientation> path = shortestPath(target.getCurrentMainCellCoordinates());
            if (path != null && !path.isEmpty()) {
                graphicPath = new Path(getPosition().add(new Vector(0, 0.5f)), new LinkedList<>(path)); // Offset for
                                                                                                        // visibility?
                return path.poll();
            } else {
                System.out.println("LogMonster: No path to target!");
            }
        }

        return null;
    }

    // --- Interaction ---
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        List<DiscreteCoordinates> view = new ArrayList<>();
        DiscreteCoordinates myPos = getCurrentMainCellCoordinates();
        for (int x = -PERCEPTION_RADIUS; x <= PERCEPTION_RADIUS; x++) {
            for (int y = -PERCEPTION_RADIUS; y <= PERCEPTION_RADIUS; y++) {
                view.add(myPos.jump(x, y));
            }
        }
        return view;
    }

    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    @Override
    public boolean wantsViewInteraction() {
        return true;
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(interactionHandler, isCellInteraction);
    }

    public void memorizePlayer(ICMazePlayer player) {
        this.target = player;
    }

    @Override
    protected int getMoveDuration() {
        return 20; // Plus lent que la normale (10)
    }
}
