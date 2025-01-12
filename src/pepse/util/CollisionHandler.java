package pepse.util;

import danogl.GameObject;

/**
 * Collision handler class
 * @author idomi
 */
public interface CollisionHandler {
    //TODO documentation
    void handleCollision(GameObject other) throws InterruptedException;
}
