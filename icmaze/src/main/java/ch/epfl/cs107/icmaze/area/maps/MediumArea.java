package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.area.Level;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public final class MediumArea extends Level {

    public MediumArea(int keyId, int difficulty, String nextArea, DiscreteCoordinates arrivalCoordinates, String previousArea, DiscreteCoordinates previousArrivalCoordinates) {
        super("MediumArea", 16, AreaPortals.W, AreaPortals.E, keyId, difficulty, nextArea, arrivalCoordinates, previousArea, previousArrivalCoordinates);
    }
}
