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
        addArea(new Spawn());
        addArea(new BossArea(ICMazeArea.AreaPortals.W));
    }

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (!super.begin(window, fileSystem)) return false;

        createAreas();
        return setCurrentArea("icmaze/Spawn", true) != null;
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
