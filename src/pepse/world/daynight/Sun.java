package pepse.world.daynight;
import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import pepse.world.Terrain;

import java.awt.*;

public class Sun {

    private static final float SIZE = 100;

    //TODO: change the sun parameters: size, position and movement radius to constants
    public static GameObject create(Vector2 windowDimensions, float cycleLength) {
        Vector2 initialSunCenter = new Vector2(windowDimensions.x()/2, windowDimensions.y()
                * Terrain.GROUND_HEIGHT/2);
        GameObject sun = new GameObject(initialSunCenter,
                new Vector2(SIZE, SIZE),
                new OvalRenderable(Color.YELLOW));
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag("sun");
        Vector2 cycleCenter = new Vector2(windowDimensions.x()/2,
                windowDimensions.y()*Terrain.GROUND_HEIGHT);
        new Transition<Float>(sun,
                (Float angle) -> sun.setCenter
                        (initialSunCenter.subtract(cycleCenter)
                                .rotated(angle)
                                .add(cycleCenter)),
                0f, 360f,
                Transition.LINEAR_INTERPOLATOR_FLOAT, cycleLength,
                Transition.TransitionType.TRANSITION_LOOP, null);
        return sun;
    }
}
