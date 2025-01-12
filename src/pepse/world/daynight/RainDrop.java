package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.CollisionHandler;


public class RainDrop extends GameObject {
    private static final int GRAVITY = 800; // Downward acceleration
    private CollisionHandler collisionHandler = null;
    private final RainDropAction onHitGround;

    public RainDrop(Vector2 topLeftCorner, Renderable renderable, RainDropAction onHitGround) {
        super(topLeftCorner, new Vector2(20, 30), renderable);
        this.onHitGround = onHitGround;
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY);
        setTag("raindrop");
    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if (onHitGround != null) {
            onHitGround.execute(this);
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }
}