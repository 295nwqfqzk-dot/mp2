# Conception du projet ICMaze

## 1. Nouvelles Classes et Interfaces

### `ch.epfl.cs107.icmaze.area.Level`
Cette classe abstraite étend `ICMazeArea` et sert de fondation pour toutes les aires générées procéduralement (`SmallArea`, `MediumArea`, `LargeArea`).
**But** : Centraliser la logique complexe de génération de niveau, incluant :
- La création des bordures et portails.
- L'appel au `MazeGenerator` pour créer le labyrinthe.
- Le placement automatique des `Key` (clés) dans des zones accessibles.
- Le placement procédural des ennemis (`LogMonster`) selon la difficulté et la taille de la zone.
- La mise à jour dynamique du graphe de pathfinding.

### `ch.epfl.cs107.icmaze.MazeGenerator`
**But** : Implémente un algorithme de "Soustraction Récursive" (Recursive Division) pour générer des labyrinthes parfaits (sans boucles) et garantis solvables. Fournit une méthode statique `createMaze`.

## 2. Modifications de l'Architecture et Justifications

### Pathfinding Dynamique (`ICMazeBehavior`)
**Modification** : Ajout de la méthode `updateGraph(int[][] maze)`.
**Justification** : L'architecture de base construisait le graphe de pathfinding (`AreaGraph`) uniquement à partir de l'image de fond statique de l'aire. Comme nos murs sont générés dynamiquement sous forme d'acteurs (`Rock`), le graphe par défaut les ignorait, causant le blocage des ennemis. Cette modification permet de reconstruire le graphe en prenant en compte la structure réelle du labyrinthe généré.

### Vitesse des Ennemis (`PathFinderEnemy`)
**Modification** : Remplacement de la constante `MOVE_DURATION` par une méthode `protected int getMoveDuration()`.
**Justification** : Le `LogMonster` nécessitait une vitesse de déplacement plus lente pour être jouable (temps de réaction du joueur). Cette modification permet aux sous-classes de redéfinir la vitesse sans dupliquer toute la logique de mouvement de `PathFinderEnemy`.

### Détection du Joueur (`ICMazePlayer`)
**Modification** : Surcharge de la méthode `isViewInteractable()` pour retourner `true`.
**Justification** : Par défaut, le joueur n'était pas considéré comme une cible visible pour les interactions distantes. Cela empêchait le `LogMonster` de le détecter via son champ de vision (`FieldOfView`). Cette modification rend le joueur détectable.

### `LogMonster` State Machine
**Modification** : Ajout d'une transition déterministe de l'état `SLEEPING` vers `CHASING` lors de la détection (`interactWith`).
**Justification** : Pour assurer un gameplay réactif ("réveil immédiat"), nous avons forcé le changement d'état dès la première interaction, au lieu de dépendre uniquement de probabilités aléatoires.

## 3. Extensions

### Génération Procédurale et Difficulté Dynamique
Nous avons étendu le système pour supporter des aires de tailles variables générées à la volée. Le nombre de monstres s'adapte automatiquement à la taille de l'aire (1 pour Small, 3 pour Medium, 5 pour Large) via la classe `Level`, offrant une progression de difficulté cohérente.
