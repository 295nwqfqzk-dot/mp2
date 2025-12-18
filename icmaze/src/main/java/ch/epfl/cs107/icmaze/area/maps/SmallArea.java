package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.icmaze.area.Level;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public final class SmallArea extends Level {

    public SmallArea(int keyId, int difficulty, String nextArea, DiscreteCoordinates arrivalCoordinates, String previousArea, DiscreteCoordinates previousArrivalCoordinates) {
        super("SmallArea", 8, AreaPortals.W, AreaPortals.E, keyId, difficulty, nextArea, arrivalCoordinates, previousArea, previousArrivalCoordinates);
    }
}
