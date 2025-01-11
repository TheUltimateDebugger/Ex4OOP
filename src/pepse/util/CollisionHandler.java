package pepse.util;

import danogl.GameObject;

public interface CollisionHandler {
    void handleCollision(GameObject other) throws InterruptedException;
}
