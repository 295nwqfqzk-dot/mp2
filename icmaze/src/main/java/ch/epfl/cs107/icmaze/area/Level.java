package ch.epfl.cs107.icmaze.area;

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

        // Portail d'entrée (Ouest)
        Portal entryPortal = new Portal(this, entry.getOrientation().opposite(),
                getPortalCoords(entry), previousArea, previousArrivalCoordinates, Portal.NO_KEY_ID);
        entryPortal.setState(Portal.State.OPEN);
        registerActor(entryPortal);

        Portal exitPortal = new Portal(this, exit.getOrientation().opposite(),
                getPortalCoords(exit), nextArea, arrivalCoordinates, keyId);
        exitPortal.setState(Portal.State.LOCKED); // Verrouillé par défaut
        registerActor(exitPortal);

        // Portails invisibles (Nord et Sud)
        Portal northPortal = new Portal(this, AreaPortals.N.getOrientation().opposite(), getPortalCoords(AreaPortals.N),
                getTitle(), getArrivalCoords(AreaPortals.N), Portal.NO_KEY_ID);
        registerActor(northPortal);

        Portal southPortal = new Portal(this, AreaPortals.S.getOrientation().opposite(), getPortalCoords(AreaPortals.S),
                getTitle(), getArrivalCoords(AreaPortals.S), Portal.NO_KEY_ID);
        registerActor(southPortal);

        // Génération du labyrinthe
        int[][] maze = ch.epfl.cs107.icmaze.MazeGenerator.createMaze(getWidth(), getHeight(), difficulty);

        // Dégager les zones de spawn et de portail
        DiscreteCoordinates[] criticalCoords = {
                getArrivalCoords(entry), // West Spawn
                getPortalCoords(entry), // West Portal
                getArrivalCoords(exit), // East Spawn (Reverse)
                getPortalCoords(exit) // East Portal
        };

        for (DiscreteCoordinates c : criticalCoords) {
            if (c != null && c.x >= 0 && c.x < maze[0].length && c.y >= 0 && c.y < maze.length) {
                maze[c.y][c.x] = 0; // Vide la cellule
            }
        }

        // Bloquer Nord/Sud, assurer Ouest/Est ouverts
        int midY = getHeight() / 2;
        // Bloquer Sud et Nord
        if (maze.length > 0) {
            maze[0][midY] = 1; // Bloquer Sud
            maze[maze.length - 1][midY] = 1; // Bloquer Nord
        }

        // Dégager l'entrée Ouest
        if (maze.length > midY + 1 && maze[0].length > 1) {
            maze[midY][0] = 0;
            maze[midY][1] = 0;
            maze[midY + 1][0] = 0;
            maze[midY + 1][1] = 0;
        }

        // Dégager l'entrée Est
        int maxX = getWidth() - 1;
        if (maze.length > midY + 1 && maxX > 1) {
            maze[midY][maxX] = 0;
            maze[midY][maxX - 1] = 0;
            maze[midY + 1][maxX] = 0;
            maze[midY + 1][maxX - 1] = 0;
        }

        // Vérifier la faisabilité et creuser si nécessaire
        DiscreteCoordinates startNode = new DiscreteCoordinates(1, midY + 1);
        DiscreteCoordinates endNode = new DiscreteCoordinates(getWidth() - 2, midY + 1);

        if (!solve(maze, startNode, endNode)) {
            // Creuser un chemin simple si déconnecté
            carvepath(maze, startNode, endNode);
        }

        // Affichage de debug
        ch.epfl.cs107.icmaze.MazeGenerator.printMaze(maze, getPortalCoords(entry), getPortalCoords(exit));

        // Placement de la clé (sur case accessible uniquement)
        java.util.Set<DiscreteCoordinates> reachable = getReachableCells(maze, startNode);
        java.util.List<DiscreteCoordinates> validKeySpots = new java.util.ArrayList<>();

        // Placer les rochers et repérer les places pour la clé
        for (int y = 0; y < maze.length; ++y) {
            for (int x = 0; x < maze[y].length; ++x) {
                DiscreteCoordinates pos = new DiscreteCoordinates(x, y);
                if (maze[y][x] == 1) { // 1 = MUR
                    // Ne pas bloquer les portails
                    if (!pos.equals(getPortalCoords(entry)) && !pos.equals(getPortalCoords(exit))) {
                        registerActor(new ch.epfl.cs107.icmaze.actor.Rock(this, pos));
                    }
                } else {
                    // Case vide - vérifier si ok pour la clé
                    if (reachable.contains(pos) &&
                            !pos.equals(getPortalCoords(entry)) && !pos.equals(getPortalCoords(exit)) &&
                            !pos.equals(getArrivalCoords(entry)) && !pos.equals(getArrivalCoords(exit))) {
                        validKeySpots.add(pos);
                    }
                }
            }
        }

        // Placer la clé au hasard
        if (!validKeySpots.isEmpty()) {
            int index = ch.epfl.cs107.icmaze.RandomGenerator.rng.nextInt(validKeySpots.size());
            DiscreteCoordinates keyPos = validKeySpots.get(index);
            registerActor(new Key(this, ch.epfl.cs107.play.math.Orientation.DOWN, keyPos, keyId));
        }

        // Apparition des monstres
        // Nombre fixe de monstres
        int targetCount = 0;
        if (getWidth() <= 12)
            targetCount = 1;
        else if (getWidth() <= 24)
            targetCount = 3;
        else
            targetCount = 5;

        java.util.List<DiscreteCoordinates> validSpawnSpots = new java.util.ArrayList<>();
        for (int y = 0; y < maze.length - 1; ++y) {
            for (int x = 0; x < maze[y].length - 1; ++x) {
                // Chercher un espace 2x2 vide
                if (maze[y][x] == 0 && maze[y][x + 1] == 0 &&
                        maze[y + 1][x] == 0 && maze[y + 1][x + 1] == 0) {
                    DiscreteCoordinates pos = new DiscreteCoordinates(x, y);
                    validSpawnSpots.add(pos);
                }
            }
        }

        java.util.Collections.shuffle(validSpawnSpots);

        int spawned = 0;
        for (DiscreteCoordinates pos : validSpawnSpots) {
            if (spawned >= targetCount)
                break;

            registerActor(new ch.epfl.cs107.icmaze.actor.LogMonster(this,
                    ch.epfl.cs107.play.math.Orientation.DOWN, pos, difficulty));
            spawned++;
        }

        // Mettre à jour le graphe de pathfinding
        setMaze(maze);
    }

    // --- Helper Methods using BFS for Connectivity ---

    private boolean solve(int[][] maze, DiscreteCoordinates start, DiscreteCoordinates end) {
        java.util.Set<DiscreteCoordinates> visited = new java.util.HashSet<>();
        java.util.Queue<DiscreteCoordinates> queue = new java.util.LinkedList<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            DiscreteCoordinates current = queue.poll();
            if (current.equals(end))
                return true;

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

        if (start.x < 0 || start.x >= maze[0].length || start.y < 0 || start.y >= maze.length)
            return visited;

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
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
        while (x != end.x) {
            maze[y][x] = 0;
            if (x < end.x)
                x++;
            else
                x--;
        }
        // Move Vertically
        while (y != end.y) {
            maze[y][x] = 0;
            if (y < end.y)
                y++;
            else
                y--;
        }
        maze[end.y][end.x] = 0;
    }

    private java.util.List<DiscreteCoordinates> getNeighbors(DiscreteCoordinates c, int[][] maze) {
        java.util.List<DiscreteCoordinates> neighbors = new java.util.ArrayList<>();
        int[][] dirs = { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 } };

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
