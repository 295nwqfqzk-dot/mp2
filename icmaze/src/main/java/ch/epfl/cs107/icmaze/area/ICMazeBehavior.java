package ch.epfl.cs107.icmaze.area;

import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.AreaBehavior;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.window.Window;

import ch.epfl.cs107.play.areagame.AreaGraph;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

import java.util.Queue;

public final class ICMazeBehavior extends AreaBehavior {

    private AreaGraph graph;

    public ICMazeBehavior(Window window, String name) {
        super(window, name);
        int height = getHeight();
        int width = getWidth();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                ICMazeCellType cellType = ICMazeCellType.toType(getRGB(height - 1 - y, x));
                setCell(x, y, new ICMazeCell(x, y, cellType));
            }
        }
        // Construire le graphe initial vide
        updateGraph(null);
    }

    public void updateGraph(int[][] maze) {
        graph = new AreaGraph();
        int height = getHeight();
        int width = getWidth();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boolean isWalkable = ((ICMazeCell) getCell(x, y)).type.isWalkable;
                if (maze != null && y < maze.length && x < maze[y].length) {
                    if (maze[y][x] == 1)
                        isWalkable = false;
                }

                if (isWalkable) {
                    boolean hasLeft = x > 0 && isWalkableAt(x - 1, y, maze);
                    boolean hasUp = y < height - 1 && isWalkableAt(x, y + 1, maze);
                    boolean hasRight = x < width - 1 && isWalkableAt(x + 1, y, maze);
                    boolean hasDown = y > 0 && isWalkableAt(x, y - 1, maze);
                    graph.addNode(new DiscreteCoordinates(x, y), hasLeft, hasUp, hasRight, hasDown);
                }
            }
        }
    }

    private boolean isWalkableAt(int x, int y, int[][] maze) {
        boolean isWalkable = ((ICMazeCell) getCell(x, y)).type.isWalkable;
        if (maze != null && y < maze.length && x < maze[y].length) {
            if (maze[y][x] == 1)
                return false;
        }
        return isWalkable;
    }

    public Queue<Orientation> shortestPath(DiscreteCoordinates from, DiscreteCoordinates to) {
        return graph.shortestPath(from, to);
    }

    public enum ICMazeCellType {
        NONE(0, false),
        GROUND(-16777216, true),
        WALL(-14112955, false),
        HOLE(-65536, true);

        final int type;
        final boolean isWalkable;

        ICMazeCellType(int type, boolean isWalkable) {
            this.type = type;
            this.isWalkable = isWalkable;
        }

        public static ICMazeCellType toType(int type) {
            for (ICMazeCellType t : ICMazeCellType.values()) {
                if (t.type == type)
                    return t;
            }
            System.out.println(type);
            return NONE;
        }
    }

    public class ICMazeCell extends Cell {
        private final ICMazeCellType type;

        public ICMazeCell(int x, int y, ICMazeCellType type) {
            super(x, y);
            this.type = type;
        }

        @Override
        protected boolean canLeave(Interactable entity) {
            return true;
        }

        @Override
        protected boolean canEnter(Interactable entity) {
            // 1) la cellule doit être traversable selon le décor
            if (!type.isWalkable)
                return false;

            // 2) si l'entité ne prend pas de place, elle peut entrer
            if (!entity.takeCellSpace())
                return true;

            // 3) sinon, pas deux "takeCellSpace()" dans la même cellule
            for (Interactable other : entities) {
                if (other.takeCellSpace())
                    return false;
            }
            return true;
        }

        @Override
        public boolean isCellInteractable() {
            return true;
        }

        @Override
        public boolean isViewInteractable() {
            return false;
        }

        @Override
        public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
            // 2.1: vide
        }
    }
}
