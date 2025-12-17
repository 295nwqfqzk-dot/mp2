package ch.epfl.cs107.icmaze.handler;

import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
/**
 * InteractionVisitor for the ICMaze entities
 */

public interface ICMazeInteractionVisitor extends AreaInteractionVisitor {

    default void interactWith(ch.epfl.cs107.icmaze.area.ICMazeBehavior.ICMazeCell cell, boolean isCellInteraction) {}
    default void interactWith(ch.epfl.cs107.icmaze.actor.ICMazePlayer player, boolean isCellInteraction) {}
    default void interactWith(ch.epfl.cs107.icmaze.actor.collectable.Pickaxe pickaxe, boolean isCellInteraction) {}
    default void interactWith(ch.epfl.cs107.icmaze.actor.collectable.Heart heart, boolean isCellInteraction) {}
    default void interactWith(ch.epfl.cs107.icmaze.actor.collectable.Key key, boolean isCellInteraction) {}
  /// Add Interaction method with all non Abstract Interactable

}
