package ch.epfl.cs107.icmaze;

import java.util.Random;

import ch.epfl.cs107.play.math.DiscreteCoordinates;

/**
 * Utility class for generating rectangular mazes using the recursive division algorithm.
 * Provides additional helpers to ensure solvability and visualize the maze.
 */
public final class MazeGenerator {
    private static final int WALL = 1;
    private static final Random random = RandomGenerator.rng;

    /**
     * Create a maze of width x height using recursive division.
     * @param width (int): width of the maze
     * @param height (int): height of the maze
     * @param difficulty (int): minimum size of sub-regions to split
     * @return (int[][]): the maze grid (0 = empty, 1 = wall)
     */
    public static int[][] createMaze(int width, int height, int difficulty) {
        int[][] maze = new int[height][width];
        // Initialize with 0 (empty) is default in Java, but good to be explicit mentally.
        
        recursiveDivision(maze, 0, 0, width, height, difficulty);
        return maze;
    }

    /**
     * Recursive division algorithm.
     * @param maze (int[][]): the grid to fill
     * @param x (int): top-left x of the sub-region
     * @param y (int): top-left y of the sub-region
     * @param width (int): width of the sub-region
     * @param height (int): height of the sub-region
     * @param difficulty (int): minimum size threshold
     */
    private static void recursiveDivision(int[][] maze, int x, int y, int width, int height, int difficulty) {
        // 1. Base case: if sub-region is too small, stop.
        if (width <= difficulty || height <= difficulty) {
            return;
        }

        // 2. Choose orientation: Horizontal or Vertical
        // Prefer the longer side to keep it homogeneous.
        boolean horizontal = width < height; // If height is larger, cut horizontally.
        if (width == height) {
            horizontal = random.nextBoolean();
        }

        if (horizontal) {
            // Horizontal Wall
            // 3. Choose wall position (random odd)
            // Range for wall: y + 1 to y + height - 2 (to verify)
            // Wall must be at odd index relative to whole grid? 
            // Handout: "placer à une position impaire". This refers to absolute coordinates.
            // We need a helper or careful math.
            // randomOdd(max) returns [1, max] odd.
            // We want y + something odd. 
            // Actually, if we use local offset, we must verify global parity.
            // It's easier to pick a random odd Y in range [y+1, y+height-2].
            
            // Valid range for wall: strictly inside the region.
            // Let's assume absolute coordinates must be odd.
            // Available odd integers in range [y+1, y+height-2].
            
            // Simplified approach based on handout logic:
            // "Choose randomly a place for the wall, ensuring it is at an odd position."
            
            // We need a Wall Y.
            int wallY = -1;
            // Attempt to find a valid odd y.
            // Range: [y + 1, y + height - 2].
            // If y is even, then y+1 is odd.
            // Let's just pick one.
            
            // Using randomOdd from helper?
            // randomOdd(max) gives odd in [1, max].
            // We want odd in [2, height-2] relative to y? No.
            
            // Let's try simpler logic matching standard recursive division for grid graphs.
            // Wall at 2*k + 1.
            
            // Generate list of valid wall positions?
            // Or loop until valid?
            
            // Let's implement robust logic:
            // Valid range relative to y: 
            // We want global Y to be odd.
            // Range of valid Ys: y+1 to y+height-2.
            
            // Let's optimize:
            // 1. Calculate min and max valid Y.
            // 2. Adjust to nearest odd inside.
            // 3. Pick random odd step.

            // Let's use a simpler heuristic if allowed, but strictness is better.
            // Let's pick a random integer, if even make odd?
            // The handout helper `randomEven` and `randomOdd` suggest we might work with relative coords?
            // "on choisit aléatoirement un emplacement pour le mur, en veillant à le placer à une position impaire"
            
            // Let's try:
            // Wall Y relative to y? 
            // Let's work with local coordinates and map to global.
            
            // Try to find a split point 'wy' such that (y + wy) is odd.
            // wy in [1, height-2].
            
            // To ensure we have space on both sides, usually we avoid edges. 
            
            int wy = randomOdd(height - 2); // returns odd in [0, height-2]? No [1, height-2]?
            // Helper: randomOdd(max) -> 1 + 2 * rng((max+1)/2). Returns 1, 3, 5... <= max.
            // So wy is odd.
            // Global Y = y + wy.
            // If y is even, Global Y is odd. Perfect.
            // If y is odd, Global Y is even. Bad.
            
            // So we need Global coordinate to be odd.
            // If y is even, we need wy to be odd. use randomOdd.
            // If y is odd, we need wy to be even. use randomEven.
            
            // But wall must be at ODD global coordinate (Handout point 3).
            
            if ((y % 2) == 0) {
                 // y is even. Need odd offset.
                 wy = randomOdd(height - 2); // 1, 3, ...
            } else {
                 // y is odd. Need even offset.
                 wy = randomEven(height - 2); // 0, 2, ...
                 if (wy == 0) wy = 2; // ensure > 0? 
                 // randomEven(max) can return 0? Yes. 
                 // Wait, we need it strictly inside. 0 would be top edge.
                 // We need at least 1 row of space.
                 // So we need even number >= 2.
                 // randomEven(max) -> 0, 2, 4...
                 // If 0, shift?
                 if (wy == 0) wy = 2;
            }
             
            // Boundary checks:
            if (wy >= height - 1) wy = height - 2; // Safety clamp?
            // If the region is too small, we shouldn't be here (base case).
            
            int wallGlobalY = y + wy;
            
            // 4. Gap strictly at even position (Handout point 4).
            // Gap X in [x, x+width-1].
            // Global X must be EVEN.
            
            int gapGlobalX = -1;
            // Same logic.
            int wx = 0;
            if ((x % 2) == 0) {
                wx = randomEven(width - 1);
            } else {
                wx = randomOdd(width - 1);
            }
            gapGlobalX = x + wx;

            // Draw Wall
            for (int i = 0; i < width; i++) {
                if (x + i != gapGlobalX) {
                    maze[wallGlobalY][x + i] = WALL;
                } else {
                    maze[wallGlobalY][x + i] = 0; // Gap
                }
            }

            // Recurse
            // Top: y to y+wy
            recursiveDivision(maze, x, y, width, wy, difficulty);
            // Bottom: y+wy+1 to y+height. 
            // Height is height - (wy + 1).
            recursiveDivision(maze, x, y + wy + 1, width, height - wy - 1, difficulty);

        } else {
            // Vertical Wall
            // Wall X must be ODD.
            int wx;
            if ((x % 2) == 0) {
                wx = randomOdd(width - 2);
            } else {
                wx = randomEven(width - 2);
                if (wx == 0) wx = 2; 
            }
            
            // Clamp
             if (wx >= width - 1) wx = width - 2;

            int wallGlobalX = x + wx;
            
            // Gap Y must be EVEN.
            int wy;
            if ((y % 2) == 0) {
                wy = randomEven(height - 1);
            } else {
                wy = randomOdd(height - 1);
            }
            int gapGlobalY = y + wy;
            
            // Draw Wall
            for (int j = 0; j < height; j++) {
                if (y + j != gapGlobalY) {
                    maze[y + j][wallGlobalX] = WALL;
                } else {
                    maze[y + j][wallGlobalX] = 0;
                }
            }
            
            // Recurse
            // Left: x to x+wx
            recursiveDivision(maze, x, y, wx, height, difficulty);
            // Right: x+wx+1
            recursiveDivision(maze, x + wx + 1, y, width - wx - 1, height, difficulty);
        }
    }
    // Restored Helpers

    /**
     * Print the maze
     */
    public static void printMaze(int[][] grid, DiscreteCoordinates start, DiscreteCoordinates end) {
        int height = grid.length;
        int width = grid[0].length;

        // Print top border
        System.out.print("┌");
        for (int i = 0; i < width; i++) {
            System.out.print("───");
        }
        System.out.println("┐");

        // Print maze rows
        for (int y = 0; y < height; y++) {
            System.out.print("│");
            for (int x = 0; x < width; x++) {
                if (x == start.x && y == start.y) System.out.print(" S ");
                else if (x == end.x && y == end.y) System.out.print(" E ");
                else System.out.print(grid[y][x] == 1 ? "███" : "   ");
            }
            System.out.println("│");
        }

        // Print bottom border
        System.out.print("└");
        for (int i = 0; i < width; i++) {
            System.out.print("───");
        }
        System.out.println("┘");
    }

    /**
     * Returns a random odd number in [1, max] (assuming max > 0).
     */
    private static int randomOdd(int max) {
        return 1 + 2 * random.nextInt((max + 1) / 2);
    }

    /**
     * Returns a random even number in [0, max] (assuming max >= 0).
     */
    private static int randomEven(int max) {
        return 2 * random.nextInt((max + 1) / 2);
    }
}

