package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.CollisionHandler;

/**
 * class representing a raindrop object in the game.
 * @author idomi
 */
public class RainDrop extends GameObject {
    private static final int GRAVITY = 800; // downward acceleration
    private final RainDropAction onHitGround; // lambda to call when hitting ground
    private static final Vector2 DIMENSIONS = new Vector2(20, 30); // size
    private static final String RAINDROP_TAG = "raindrop"; // tag for objects

    /**
     * constructor for creating a raindrop object. 
     * @param topLeftCorner - the starting position of the raindrop. 
     * @param renderable - the visual representation of the raindrop. 
     * @param onHitGround - action to execute when the raindrop hits the ground. 
     */
    public RainDrop(Vector2 topLeftCorner, Renderable renderable, RainDropAction onHitGround) {
        super(topLeftCorner, DIMENSIONS, renderable);
        this.onHitGround = onHitGround;
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY);
        setTag(RAINDROP_TAG);
    }

    /**
     * called when the raindrop collides with something. 
     * @param other - the object that the raindrop collided with. 
     * @param collision - the collision details. 
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if (onHitGround != null) {
            onHitGround.execute(this); // execute action when hitting the ground
        }
    }

    /**
     * updates the raindrop. 
     * @param deltaTime - time since last update. 
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }
}
