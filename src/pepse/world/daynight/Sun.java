package pepse.world.daynight;
import danogl.GameObject;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

public class Sun {
    //TODO: change the sun parameters into the correct ones: size, position and movement radius
    public static GameObject create(Vector2 windowDimensions, float cycleLength) {
        Vector2 initialSunCenter = new Vector2(windowDimensions.x()/2, 100);
        GameObject sun = new GameObject(initialSunCenter,
                new Vector2(100, 100),
                new OvalRenderable(Color.YELLOW));
        sun.setTag("sun");
        new Transition<Float>(sun,
                value -> sun.setCenter(new Vector2(
                (float) (windowDimensions.x() / 2 + 500 * Math.cos(Math.toRadians(value))),
                (float) (windowDimensions.y() * 2/3 + 500 * Math.sin(Math.toRadians(value))))),
                0f, 360f,
                Transition.LINEAR_INTERPOLATOR_FLOAT, cycleLength,
                Transition.TransitionType.TRANSITION_LOOP, null);
        return sun;
    }
}
