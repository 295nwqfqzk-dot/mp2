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
        // Spawn (Est) -> SmallArea (Ouest)
        // SmallArea (Est) -> MediumArea (Ouest)
        // MediumArea (Est) -> LargeArea (Ouest)
        // LargeArea (Est) -> BossArea (Ouest)

        // SmallArea -> MediumArea (Entrée Ouest : (1, 8))
        // Retour -> Spawn (Côté Est : (8, 5))
        addArea(new ch.epfl.cs107.icmaze.area.maps.SmallArea(1, Difficulty.EASY, "icmaze/MediumArea[2]",
                new ch.epfl.cs107.play.math.DiscreteCoordinates(1, 8),
                "icmaze/Spawn", new ch.epfl.cs107.play.math.DiscreteCoordinates(8, 5)));

        // MediumArea -> LargeArea (Entrée Ouest : (1, 16))
        // Retour -> SmallArea (Côté Est : (6, 4))
        addArea(new ch.epfl.cs107.icmaze.area.maps.MediumArea(2, Difficulty.HARD, "icmaze/LargeArea[3]",
                new ch.epfl.cs107.play.math.DiscreteCoordinates(1, 16),
                "icmaze/SmallArea[1]", new ch.epfl.cs107.play.math.DiscreteCoordinates(6, 4)));

        // LargeArea -> BossArea (Entrée Ouest : (1, 5))
        // Retour -> MediumArea (Côté Est : (14, 8))
        addArea(new ch.epfl.cs107.icmaze.area.maps.LargeArea(3, Difficulty.HARDEST, "icmaze/Boss",
                new ch.epfl.cs107.play.math.DiscreteCoordinates(1, 5),
                "icmaze/MediumArea[2]", new ch.epfl.cs107.play.math.DiscreteCoordinates(14, 8)));
    }

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (!super.begin(window, fileSystem))
            return false;

        createAreas();
        return setCurrentArea("icmaze/Spawn", true) != null;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        ch.epfl.cs107.play.window.Keyboard keyboard = getWindow().getKeyboard();
        if (keyboard.get(KeyBindings.RESET_GAME).isDown()) {
            reset();
        }
    }

    public void reset() {
        begin(getWindow(), getFileSystem());
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
