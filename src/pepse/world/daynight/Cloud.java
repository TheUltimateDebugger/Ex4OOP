package pepse.world.daynight;
import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.ImageRenderable;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.AvatarJumpListener;
import pepse.util.ColorSupplier;
import pepse.world.Block;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static pepse.world.Block.SIZE;

/**
 * Represents a cloud that moves across the screen.
 * @author idomi
 */
public class Cloud extends GameObject implements AvatarJumpListener {
//    private static final Color BASE_CLOUD_COLOR =
//            new Color(255, 255, 255);
    private final int window_width;
    private static final Vector2 VELOCITY =
            new Vector2(20, 0);
    /**
     * Constructs a cloud object.
     * @param topLeftCorner - The initial position of the cloud.
     * @param renderable - The visual representation of the cloud.
     */
    public Cloud(Vector2 topLeftCorner, ImageRenderable renderable, int window_width) {
        super(topLeftCorner,
                new Vector2(renderable.width(), renderable.height()).mult(0.3f),
                renderable);
        this.window_width = window_width;
        this.setTag("cloud");
        setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        this.setTopLeftCorner(this.getTopLeftCorner().add(VELOCITY.mult(deltaTime)));
        if (this.getTopLeftCorner().x() > window_width ||
                this.getTopLeftCorner().x() < -this.getDimensions().x()) {
            this.setTopLeftCorner(new Vector2(-this.getDimensions().x(),
                    this.getTopLeftCorner().y()));
        }
    }

    @Override
    public void onAvatarJump() {

    }
}
