package pepse.world.daynight;

import danogl.GameObject;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

public class SunHalo {
    private static final Color HALO_COLOR = new Color(255, 255, 0, 20);
    public static final float SIZE_FACTOR = 1.5f;
    public static GameObject create(GameObject sun) {
        GameObject sun_halo = new GameObject(sun.getTopLeftCorner(), sun.getDimensions().mult(SIZE_FACTOR),
                new OvalRenderable(HALO_COLOR));
        sun_halo.setCoordinateSpace(sun.getCoordinateSpace());
        sun_halo.addComponent(deltaTime -> sun_halo.setCenter(sun.getCenter()));
        return sun_halo;
    }
}
