package ch.epfl.cs107.icmaze.actor;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.window.Canvas;

public abstract class Enemy extends ICMazeActor implements Interactor {

    private final Health health;
    private int immunityDuration = 24;
    private int immunityTimer = 24;

    private final int damage;

    public Enemy(Area area, Orientation orientation, DiscreteCoordinates position, int maxHealth, int damage) {
        super(area, orientation, position);
        this.damage = damage;
        this.health = new Health(this, Transform.I.translated(0, 1.2f), maxHealth, false); // False = Enemy (Red)
        this.immunityTimer = 0; // Start vulnerable
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (immunityTimer > 0) {
            immunityTimer--;
        }
    }

    public void takeDamage(int amount) {
        if (immunityTimer <= 0) {
            health.decrease(amount);
            immunityTimer = immunityDuration;
            if (health.isOff()) {
                // Die
                leaveArea();
                // Spawn Cloud
                new ch.epfl.cs107.icmaze.actor.Cloud(getOwnerArea(), getCurrentMainCellCoordinates());
                // Drop Heart 50%? or just die.
                // Handout check: "Une Heart est parfois laissée par un ennemi qui meurt (50%)"
                if (ch.epfl.cs107.icmaze.RandomGenerator.rng.nextDouble() < 0.5) {
                    getOwnerArea().registerActor(new ch.epfl.cs107.icmaze.actor.collectable.Heart(getOwnerArea(),
                            getCurrentMainCellCoordinates()));
                }
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        health.draw(canvas);
    }

    @Override
    public boolean takeCellSpace() {
        return true;
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        return true;
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    // Default: deal damage to player on touch?
    // Handout: "si se trouve juste en face... il lui fait perdre un nombre fixe"
    // This is specific to LogMonster logic (attack).
    // Generic Enemy just exists.

    public int getDamage() {
        return damage;
    }
}
