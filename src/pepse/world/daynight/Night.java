package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * class for handling the night in the game.
 * @author Tomer Zilberman
 */
public class Night {
    private static final Float MIDNIGHT_OPACITY = 0.5f;
    public static final String NIGHT_TAG = "night";

    /**
     * creates the night object, sets it up, and applies a transition for opacity.
     * @param windowDimensions - the dimensions of the game window.
     * @param cycleLength - the length of the cycle to transition the opacity.
     * @return night - the GameObject representing the night.
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength) {
        Renderable renderable = new RectangleRenderable(Color.BLACK);
        GameObject night = new GameObject(Vector2.ZERO, windowDimensions, renderable);
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        night.setTag(NIGHT_TAG);
        new Transition<Float>(night, night.renderer()::setOpaqueness, 0f,
                MIDNIGHT_OPACITY, Transition.CUBIC_INTERPOLATOR_FLOAT, cycleLength/2,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
        return night;
    }

}
