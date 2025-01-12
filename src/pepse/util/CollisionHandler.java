package pepse.util;

import danogl.GameObject;

/**
 * Collision handler class
 * @author idomi
 */
public interface CollisionHandler {
    void handleCollision(GameObject other) throws InterruptedException;
}
