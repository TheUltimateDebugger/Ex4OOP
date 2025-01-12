package pepse.world;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * class representing a block in the game world with fixed size and immovable physics.
 * @author Tomer Zilberman
 */
public class Block extends GameObject {
    /**
     * size of each block in the game
     */
    public static final int SIZE = 30;

    /**
     * constructor for creating a block at a specific position with a specific renderable object.
     * @param topLeftCorner - the top left corner position of the block.
     * @param renderable - the renderable for the block's appearance.
     */
    public Block(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, Vector2.ONES.mult(SIZE), renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
    }
}
