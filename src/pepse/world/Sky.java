package pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import java.awt.*;

/**
 * Class representing the sky in the game world.
 * @author Tomer Zilberman
 */
public class Sky {
    //The basic color of the sky
    private static final Color BASIC_SKY_COLOR = Color.decode("#80C6E5");
    //the sky tag
    private static final String SKY_TAG = "sky";

    /**
     * Creates a GameObject representing the sky with the specified window dimensions.
     * @param windowDimensions - the dimensions of the window (width and height).
     * @return - the GameObject representing the sky.
     */
    public static GameObject create(Vector2 windowDimensions) {
        GameObject sky = new GameObject(
                Vector2.ZERO, windowDimensions,
                new RectangleRenderable(BASIC_SKY_COLOR));
        sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sky.setTag(SKY_TAG);
        return sky;
    }
}
