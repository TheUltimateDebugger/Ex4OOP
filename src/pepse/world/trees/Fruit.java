package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;

public class Fruit extends Block {
//    Runnable on_collision;
    public Fruit(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, renderable);
    }

    // TODO - necessary?
//    @Override
//    public void onCollisionEnter(GameObject other, Collision collision) {
//        super.onCollisionEnter(other, collision);
//        this.on_collision.run();
//    }
}
