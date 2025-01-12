package pepse.world.daynight;
import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.Camera;
import danogl.gui.rendering.ImageRenderable;
import danogl.util.Vector2;
import pepse.util.AvatarJumpListener;

/**
 * Represents a cloud that moves across the screen.
 * @author idomi
 */
public class Cloud extends GameObject implements AvatarJumpListener {
    private final int window_width;
    private static final Vector2 VELOCITY =
            new Vector2(20, 0);
    private final CloudAction addRain;
    /**
     * Constructs a cloud object.
     * @param topLeftCorner - The initial position of the cloud.
     * @param renderable - The visual representation of the cloud.
     */
    public Cloud(Vector2 topLeftCorner, ImageRenderable renderable,
                 int window_width, CloudAction addRain) {
        super(topLeftCorner,
                new Vector2(renderable.width(), renderable.height()).mult(0.3f),
                renderable);
        this.window_width = window_width;
        this.addRain = addRain;
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
        addRain.execute(this);
    }

    public Vector2 getVisualCenterInAbsoluteSpace(Vector2 cameraTopLeft) {
        return cameraTopLeft.add(this.getTopLeftCorner()).add(this.getDimensions().mult(0.5f));
    }
}
