package ch.epfl.cs107.icmaze.area;

import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.AreaBehavior;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.window.Window;

public final class ICMazeBehavior extends AreaBehavior {

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
                if (t.type == type) return t;
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
            if (!type.isWalkable) return false;

            // 2) si l'entité ne prend pas de place, elle peut entrer
            if (!entity.takeCellSpace()) return true;

            // 3) sinon, pas deux "takeCellSpace()" dans la même cellule
            for (Interactable other : entities) {
                if (other.takeCellSpace()) return false;
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
