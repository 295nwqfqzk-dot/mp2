package ch.epfl.cs107.icmaze;

import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.area.maps.BossArea;
import ch.epfl.cs107.icmaze.area.maps.Spawn;
import ch.epfl.cs107.play.areagame.AreaGame;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.window.Window;

public final class ICMaze extends AreaGame {

    private void createAreas() {
        addArea(new Spawn(this));
        generateHardCodedLevel();
        addArea(new BossArea(ICMazeArea.AreaPortals.W));
    }

    private void generateHardCodedLevel() {
        // Spawn (East) -> SmallArea (West)
        // SmallArea (East) -> MediumArea (West)
        // MediumArea (East) -> LargeArea (West)
        // LargeArea (East) -> BossArea (West)

        // Coordinates: West arrival = (1, 5). East arrival = (size, 5) ? No, standard coordinates.
        // SmallArea (8): West entry (1, 4), East exit (6, 4).
        // MediumArea (16): West entry (1, 8), East exit (14, 8).
        // LargeArea (32): West entry (1, 16), East exit (30, 16).
        // Note: Level class constructor expects Arrival Coordinates for the NEXT area.
        
        // SmallArea -> MediumArea (Entry at West: (1, 8))
        // Reverse -> Spawn (East side: (8, 5))
        addArea(new ch.epfl.cs107.icmaze.area.maps.SmallArea(1, Difficulty.EASY, "icmaze/MediumArea[2]", new ch.epfl.cs107.play.math.DiscreteCoordinates(1, 8), 
                                                              "icmaze/Spawn", new ch.epfl.cs107.play.math.DiscreteCoordinates(8, 5)));
        
        // MediumArea -> LargeArea (Entry at West: (1, 16))
        // Reverse -> SmallArea (East side: (6, 4))
        addArea(new ch.epfl.cs107.icmaze.area.maps.MediumArea(2, Difficulty.HARD, "icmaze/LargeArea[3]", new ch.epfl.cs107.play.math.DiscreteCoordinates(1, 16),
                                                               "icmaze/SmallArea[1]", new ch.epfl.cs107.play.math.DiscreteCoordinates(6, 4)));
        
        // LargeArea -> BossArea (Entry at West: (1, 5)) - Wait, Boss West arrival is (1,5).
        // BossArea title is "icmaze/Boss".
        // Reverse -> MediumArea (East side: (14, 8))
        addArea(new ch.epfl.cs107.icmaze.area.maps.LargeArea(3, Difficulty.HARDEST, "icmaze/Boss", new ch.epfl.cs107.play.math.DiscreteCoordinates(1, 5),
                                                              "icmaze/MediumArea[2]", new ch.epfl.cs107.play.math.DiscreteCoordinates(14, 8)));
    }

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (!super.begin(window, fileSystem)) return false;

        createAreas();
        return setCurrentArea("icmaze/Spawn", true) != null;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        ch.epfl.cs107.play.window.Keyboard keyboard = getWindow().getKeyboard();
        if (keyboard.get(KeyBindings.RESET_GAME).isDown()) {
            begin(getWindow(), getFileSystem());
        }
    }

    @Override
    public String getTitle() {
        return "ICMaze";
    }

    public void switchArea(Portal portal, ICMazePlayer player) {
        // quitte l'aire actuelle
        player.leaveArea();

        // change l'aire courante (possible ici car on est dans AreaGame)
        Area next = setCurrentArea(portal.getDestinationArea(), false);

        // entre dans la nouvelle aire
        player.enterArea(next, portal.getDestinationCoordinates());

        // caméra sur le joueur
        next.setViewCandidate(player);
    }
}
