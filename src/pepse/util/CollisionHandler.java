package pepse.util;

import danogl.GameObject;

/**
 * Collision handler class
 * @author idomi
 */
public interface CollisionHandler {
    /**
     * a listener function that activates when the avatar collide with an object
     * @param other the object the avatar collide with
     */
    void handleCollision(GameObject other);
}
