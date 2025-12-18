package ch.epfl.cs107.icmaze;

import java.util.Random;

import ch.epfl.cs107.play.math.DiscreteCoordinates;

/**
 * Classe utilitaire pour générer des labyrinthes rectangulaires avec division
 * récursive.
 */
public final class MazeGenerator {
    private static final int WALL = 1;
    private static final Random random = RandomGenerator.rng;

    private MazeGenerator() {
    }

    public static int[][] createMaze(int width, int height, int difficulty) {
        int[][] maze = new int[height][width];

        // Murs de bordure
        for (int x = 0; x < width; x++) {
            maze[0][x] = WALL; // Haut
            maze[height - 1][x] = WALL; // Bas
        }
        for (int y = 0; y < height; y++) {
            maze[y][0] = WALL; // Gauche
            maze[y][width - 1] = WALL; // Droite
        }

        // Division récursive
        divide(maze, 1, 1, width - 2, height - 2, difficulty);

        return maze;
    }

    private static void divide(int[][] maze, int x, int y, int width, int height, int difficulty) {
        // Cas de base : trop petit pour diviser
        if (width < difficulty || height < difficulty) {
            for (int j = y; j < y + height; j++) {
                for (int i = x; i < x + width; i++) {
                    maze[j][i] = 0; // Vider l'intérieur
                }
            }
            return;
        }

        // Choisir mur horizontal ou vertical
        // Favoriser la plus grande dimension
        boolean horizontal;
        if (width > height) {
            horizontal = false; // Coupe verticale
        } else if (height > width) {
            horizontal = true; // Coupe horizontale
        } else {
            horizontal = random.nextBoolean(); // Aléatoire si égal
        }

        if (horizontal) {
            // Coupe horizontale
            int wallY = y + randomOdd(height - 1);
            int gapX = x + randomEven(width - 1);

            for (int i = x; i < x + width; i++) {
                if (i != gapX) {
                    maze[wallY][i] = WALL;
                }
            }

            divide(maze, x, y, width, wallY - y, difficulty);
            divide(maze, x, wallY + 1, width, y + height - wallY - 1, difficulty);

        } else {
            // Coupe verticale
            int wallX = x + randomOdd(width - 1);
            int gapY = y + randomEven(height - 1);

            for (int i = y; i < y + height; i++) {
                if (i != gapY) {
                    maze[i][wallX] = WALL;
                }
            }

            divide(maze, x, y, wallX - x, height, difficulty);
            divide(maze, wallX + 1, y, x + width - wallX - 1, height, difficulty);
        }
    }

    /**
     * Afficher le labyrinthe
     */
    public static void printMaze(int[][] grid, DiscreteCoordinates start, DiscreteCoordinates end) {
        int height = grid.length;
        int width = grid[0].length;

        // Bordure haut
        System.out.print("┌");
        for (int i = 0; i < width; i++) {
            System.out.print("───");
        }
        System.out.println("┐");

        // Lignes du labyrinthe
        for (int y = 0; y < height; y++) {
            System.out.print("│");
            for (int x = 0; x < width; x++) {
                if (x == start.x && y == start.y)
                    System.out.print(" S ");
                else if (end != null && x == end.x && y == end.y)
                    System.out.print(" E ");
                else
                    System.out.print(grid[y][x] == WALL ? "███" : "   ");
            }
            System.out.println("│");
        }

        // Bordure bas
        System.out.print("└");
        for (int i = 0; i < width; i++) {
            System.out.print("───");
        }
        System.out.println("┘");
    }

    /**
     * Retourne un nombre impair aléatoire
     */
    private static int randomOdd(int max) {
        if (max < 1)
            return 1;
        return 1 + 2 * random.nextInt((max + 1) / 2);
    }

    /**
     * Retourne un nombre pair aléatoire
     */
    private static int randomEven(int max) {
        if (max < 0)
            return 0;
        return 2 * random.nextInt((max + 1) / 2);
    }
}
