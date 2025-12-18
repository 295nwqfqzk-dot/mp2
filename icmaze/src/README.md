# ICMaze - Guide Utilisateur

## Lancement du Jeu
Pour lancer le jeu, exécutez la classe principale :
`ch.epfl.cs107.icmaze.ICMaze` (ou `ch.epfl.cs107.Play` avec l'argument `ch.epfl.cs107.icmaze.ICMaze`).

## Contrôles
- **Flèches directionnelles** : Déplacer le personnage.
- **Touche 'L'** : Interagir (Ouvrir un coffre, ramasser un objet, parler).
- **Barre d'Espace** : Utiliser la Pioche (Attaquer/Briser les rochers).
- **Touche 'R'** : Réinitialiser le jeu.

## Comportements des Composants

### Joueur (ICMazePlayer)
- Peut se déplacer dans le labyrinthe.
- Peut ramasser des cœurs (soin) et des clés.
- Peut utiliser une **Pioche** pour briser les rochers fissurés ou attaquer les ennemis.
- Possède une période d'immunité après avoir subi des dégâts.

### LogMonster (Ennemi)
- **Comportement** : Reste endormi (`SLEEPING`) jusqu'à ce que le joueur entre dans son champ de vision ou le touche.
- **Réveil** : Se réveille **immédiatement** à la détection (modification par rapport à l'aléatoire suggéré) et poursuit le joueur (`CHASING`).
- **Attaque** : Inflige des dégâts au contact.
- **Vitesse** : Se déplace plus lentement que la normale pour permettre au joueur de réagir.
- **Pathfinding** : Capable de contourner les murs générés dynamiquement.

### Génération Procédurale (MazeGenerator)
- Utilise un algorithme de division récursive pour créer des labyrinthes uniques à chaque lancement.
- Assure qu'il existe toujours un chemin entre l'entrée et la sortie.
- Place stratégiquement les clés dans des zones accessibles.

## Scénario et Aires de Jeu

Le jeu se déroule en une suite de niveaux de difficulté croissante :

1.  **Spawn** : Zone de départ sûre. Pas d'ennemis.
2.  **Small Area** (Niveau 1) : Petit labyrinthe (8x8). Contient **1 LogMonster**. Il faut trouver la clé pour sortir.
3.  **Medium Area** (Niveau 2) : Labyrinthe moyen (16x16). Contient **3 LogMonsters**.
4.  **Large Area** (Niveau 3) : Grand labyrinthe complexe (32x32). Contient **5 LogMonsters**.

**Objectif** : Traverser les zones générées procéduralement en survivant aux monstres. Le projet s'arrête à l'étape de la génération procédurale (4.2.1).
