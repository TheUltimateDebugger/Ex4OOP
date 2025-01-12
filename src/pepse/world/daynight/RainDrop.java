package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.CollisionHandler;


/**
 * raindrop class to represent a single raindrop on-screen
 * @author idomi
 */
public class RainDrop extends GameObject {
    private static final int GRAVITY = 800; // Downward acceleration
    private final int window_height;
    private CollisionHandler collisionHandler = null;
    public RainDrop(Vector2 topLeftCorner, Renderable renderable, int window_height) {
        super(topLeftCorner, new Vector2(2, 10), renderable);
        this.window_height = window_height;
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY);
        setTag("raindrop");
    }

    public void setCollisionHandler(CollisionHandler collisionHandler) {
        this.collisionHandler = collisionHandler;
    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if (collisionHandler != null) {
            try {
                collisionHandler.handleCollision(this);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }
}