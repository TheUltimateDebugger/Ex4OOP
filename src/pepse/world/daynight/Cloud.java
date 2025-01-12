package pepse.world.daynight;
import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.Camera;
import danogl.gui.rendering.ImageRenderable;
import danogl.util.Vector2;
import pepse.util.AvatarJumpListener;

/**
 * represents a cloud that moves across the screen.
 * @author idomi
 */
public class Cloud extends GameObject implements AvatarJumpListener {
    private final int window_width; // the width of the game window, used to reset cloud position
    private static final Vector2 VELOCITY =
            new Vector2(20, 0); // constant velocity for cloud movement
    private final CloudAction addRain; // action to add rain when the avatar jumps

    /**
     * constructs a cloud object.
     * @param topLeftCorner - the initial position of the cloud.
     * @param renderable - the visual representation of the cloud.
     * @param window_width - the width of the game window.
     * @param addRain - the action to execute for rain creation.
     */
    public Cloud(Vector2 topLeftCorner, ImageRenderable renderable,
                 int window_width, CloudAction addRain) {
        super(topLeftCorner,
                new Vector2(renderable.width(), renderable.height()).mult(0.3f),
                renderable);
        this.window_width = window_width;
        this.addRain = addRain;
        this.setTag("cloud"); // set a tag to identify this object as a cloud
        setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES); // make the cloud move with the camera
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
        addRain.execute(this); // trigger rain effect when avatar jumps
    }

    /**
     * calculates the visual center of the cloud in absolute coordinates.
     * @param cameraTopLeft - the top-left corner of the camera.
     * @return the visual center of the cloud.
     */
    public Vector2 getVisualCenterInAbsoluteSpace(Vector2 cameraTopLeft) {
        return cameraTopLeft.add(this.getTopLeftCorner()).add(this.getDimensions().mult(0.5f));
    }
}
