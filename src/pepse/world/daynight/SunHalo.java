package pepse.world.daynight;

import danogl.GameObject;
import danogl.gui.rendering.OvalRenderable;

import java.awt.*;

/**
 * class representing the sun's halo in the game world.
 * @author Tomer Zilberman
 */
public class SunHalo {
    private static final Color HALO_COLOR = new Color(255, 255, 0, 20); // light yellow with transparency
    public static final float SIZE_FACTOR = 1.5f; // size of the halo relative to the sun's size

    /**
     * creates the sun halo object based on the sun's properties.
     * @param sun - the sun object that the halo will be based on.
     * @return sun_halo - the GameObject representing the sun's halo.
     */
    public static GameObject create(GameObject sun) {
        GameObject sun_halo = new GameObject(sun.getTopLeftCorner(), sun.getDimensions().mult(SIZE_FACTOR),
                new OvalRenderable(HALO_COLOR)); // make it look like a faint yellow halo
        sun_halo.setCoordinateSpace(sun.getCoordinateSpace());
        sun_halo.addComponent(deltaTime -> sun_halo.setCenter(sun.getCenter())); // follow the sun around
        return sun_halo; // return the halo object
    }
}
