package ch.epfl.cs107.icmaze.area;

import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public abstract class Level extends ICMazeArea {

    private final AreaPortals entry;
    private final AreaPortals exit;
    private final int keyId;
    private final int difficulty;
    private final String nextArea;
    private final DiscreteCoordinates arrivalCoordinates;
    private final String previousArea;
    private final DiscreteCoordinates previousArrivalCoordinates;

    protected Level(String behaviorName, int size, AreaPortals entry, AreaPortals exit,
                    int keyId, int difficulty, String nextArea, DiscreteCoordinates arrivalCoordinates,
                    String previousArea, DiscreteCoordinates previousArrivalCoordinates) {
        super(behaviorName, size);
        this.entry = entry;
        this.exit = exit;
        this.keyId = keyId;
        this.difficulty = difficulty;
        this.nextArea = nextArea;
        this.arrivalCoordinates = arrivalCoordinates;
        this.previousArea = previousArea;
        this.previousArrivalCoordinates = previousArrivalCoordinates;
    }

    @Override
    protected void createArea() {
        registerActor(new Background(this, getBehaviorName()));

        // Entry Portal (West) - Linking back to previous area
        Portal entryPortal = new Portal(this, entry.getOrientation().opposite(),
                getPortalCoords(entry), previousArea, previousArrivalCoordinates, Portal.NO_KEY_ID);
        entryPortal.setState(Portal.State.OPEN);
        registerActor(entryPortal);

        Portal exitPortal = new Portal(this, exit.getOrientation().opposite(),
                getPortalCoords(exit), nextArea, arrivalCoordinates, keyId);
        exitPortal.setState(Portal.State.LOCKED); // Locked by default (Step 3.3)
        registerActor(exitPortal);
        
        // Maze Generation Integration (Step 3.2.1)
        int[][] maze = ch.epfl.cs107.icmaze.MazeGenerator.createMaze(getWidth(), getHeight(), difficulty);
        
        // Force-clear Spawn and Portal locations to ensure solvability
        DiscreteCoordinates[] criticalCoords = {
            getArrivalCoords(entry), // West Spawn
            getPortalCoords(entry),  // West Portal
            getArrivalCoords(exit),  // East Spawn (Reverse)
            getPortalCoords(exit)    // East Portal
        };

        for (DiscreteCoordinates c : criticalCoords) {
             if (c != null && c.x >= 0 && c.x < maze[0].length && c.y >= 0 && c.y < maze.length) {
                 maze[c.y][c.x] = 0; // Clear cell
             }
        }
        
        // Strict Requirement: Block North/South paths, Ensure West/East paths are open
        int midY = getHeight() / 2;
        // Block South (x=mid, y=0) and North (x=mid, y=max)
        if (maze.length > 0) {
             maze[0][midY] = 1; // Block South
             maze[maze.length - 1][midY] = 1; // Block North
        }

        // Clear West Entrance Area (0..1, mid..mid+1) to ensure Portal(y) connects to Spawn(y+1)
        if (maze.length > midY + 1 && maze[0].length > 1) {
            maze[midY][0] = 0;
            maze[midY][1] = 0;
            maze[midY + 1][0] = 0;
            maze[midY + 1][1] = 0;
        }

        // Clear East Entrance Area (max-1..max, mid..mid+1)
        int maxX = getWidth() - 1;
        if (maze.length > midY + 1 && maxX > 1) {
            maze[midY][maxX] = 0;
            maze[midY][maxX - 1] = 0;
            maze[midY + 1][maxX] = 0;
            maze[midY + 1][maxX - 1] = 0;
        }
        
        // Check Solvability and Carve Path if needed
        DiscreteCoordinates startNode = new DiscreteCoordinates(1, midY + 1);
        DiscreteCoordinates endNode = new DiscreteCoordinates(getWidth() - 2, midY + 1);

        if (!solve(maze, startNode, endNode)) {
            // Carve a simple path if disconnected
            carvepath(maze, startNode, endNode);
        }
        
        // Debug Print
        ch.epfl.cs107.icmaze.MazeGenerator.printMaze(maze, getPortalCoords(entry), getPortalCoords(exit));
        
        // Key Placement (Step 3.3) - Use Reachable Cells ONLY
        java.util.Set<DiscreteCoordinates> reachable = getReachableCells(maze, startNode);
        java.util.List<DiscreteCoordinates> validKeySpots = new java.util.ArrayList<>();

        // Register Rocks and Collect Key Spots
        for (int y = 0; y < maze.length; ++y) {
            for (int x = 0; x < maze[y].length; ++x) {
                DiscreteCoordinates pos = new DiscreteCoordinates(x, y);
                if (maze[y][x] == 1) { // 1 = WALL
                    // Ensure not blocking portals:
                    if (!pos.equals(getPortalCoords(entry)) && !pos.equals(getPortalCoords(exit))) {
                         registerActor(new ch.epfl.cs107.icmaze.actor.Rock(this, pos));
                    }
                } else {
                    // Empty cell - check if suitable for key
                     if (reachable.contains(pos) && 
                        !pos.equals(getPortalCoords(entry)) && !pos.equals(getPortalCoords(exit)) &&
                        !pos.equals(getArrivalCoords(entry)) && !pos.equals(getArrivalCoords(exit))) {
                         validKeySpots.add(pos);
                     }
                }
            }
        }
        
        // Place Key randomly in GUARANTEED reachable spot
        if (!validKeySpots.isEmpty()) {
            int index = ch.epfl.cs107.icmaze.RandomGenerator.rng.nextInt(validKeySpots.size());
            DiscreteCoordinates keyPos = validKeySpots.get(index);
            registerActor(new Key(this, ch.epfl.cs107.play.math.Orientation.DOWN, keyPos, keyId));
        }
    }

    // --- Helper Methods using BFS for Connectivity ---

    private boolean solve(int[][] maze, DiscreteCoordinates start, DiscreteCoordinates end) {
        java.util.Set<DiscreteCoordinates> visited = new java.util.HashSet<>();
        java.util.Queue<DiscreteCoordinates> queue = new java.util.LinkedList<>();
        
        queue.add(start);
        visited.add(start);
        
        while(!queue.isEmpty()) {
            DiscreteCoordinates current = queue.poll();
            if (current.equals(end)) return true;
            
            for (DiscreteCoordinates neighbor : getNeighbors(current, maze)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        return false;
    }

    private java.util.Set<DiscreteCoordinates> getReachableCells(int[][] maze, DiscreteCoordinates start) {
        java.util.Set<DiscreteCoordinates> visited = new java.util.HashSet<>();
        java.util.Queue<DiscreteCoordinates> queue = new java.util.LinkedList<>();
        
        if (start.x < 0 || start.x >= maze[0].length || start.y < 0 || start.y >= maze.length) return visited;

        queue.add(start);
        visited.add(start);
        
        while(!queue.isEmpty()) {
            DiscreteCoordinates current = queue.poll();
            for (DiscreteCoordinates neighbor : getNeighbors(current, maze)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        return visited;
    }

    private void carvepath(int[][] maze, DiscreteCoordinates start, DiscreteCoordinates end) {
        // Simple "Dog leg" path: Move X then Move Y
        // This is crude but guarantees connectivity.
        int x = start.x;
        int y = start.y;
        
        // Move Horizontally
        while(x != end.x) {
            maze[y][x] = 0;
            if (x < end.x) x++;
            else x--;
        }
        // Move Vertically
        while(y != end.y) {
            maze[y][x] = 0;
            if (y < end.y) y++;
            else y--;
        }
        maze[end.y][end.x] = 0;
    }

    private java.util.List<DiscreteCoordinates> getNeighbors(DiscreteCoordinates c, int[][] maze) {
        java.util.List<DiscreteCoordinates> neighbors = new java.util.ArrayList<>();
        int[][] dirs = {{0,1}, {0,-1}, {1,0}, {-1,0}};
        
        for (int[] d : dirs) {
            int nx = c.x + d[0];
            int ny = c.y + d[1];
            if (nx >= 0 && nx < maze[0].length && ny >= 0 && ny < maze.length && maze[ny][nx] == 0) {
                neighbors.add(new DiscreteCoordinates(nx, ny));
            }
        }
        return neighbors;
    }
    
    @Override
    public String getTitle() {
        return "icmaze/" + getBehaviorName() + "[" + keyId + "]";
    }
    
    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() {
        return getArrivalCoords(entry);
    }
    
    @Override
    public float getCameraScaleFactor() {
        return Math.min(getWidth() * 1.375f, 30f);
    }
}
