package pepse.world;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class representing the terrain of the game world, including the ground and noise-based variations.
 * @author Tomer Zilberman
 */
public class Terrain {
    // The ground height at x = 0 in the world
    private final int groundHeightAtX0;
    // The noise generator used to create ground height variations
    private final NoiseGenerator ng;
    // The factor representing the ground height fraction of the screen
    public static final float GROUND_HEIGHT = 2f/3f;
    // The depth of the terrain (how far the terrain extends vertically)
    private static final int TERRAIN_DEPTH = 20;
    // The noise factor used for generating the terrain
    private static final int NOISE_FACTOR = Block.SIZE * 7;
    // The base color of the ground
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    //the tag for the blocks
    private static final String GROUND_TAG = "ground";

    /**
     * Constructs the terrain with the given window dimensions and random seed.
     * @param windowDimensions - the dimensions of the game window.
     * @param seed - the seed for noise generation.
     */
    public Terrain(Vector2 windowDimensions, int seed) {
        this.groundHeightAtX0 = Math.round(windowDimensions.y() * GROUND_HEIGHT);
        this.ng = new NoiseGenerator(seed, this.groundHeightAtX0);
    }

    /**
     * Returns the ground height at a specific x-coordinate, influenced by noise.
     * @param x - the x-coordinate where the ground height is evaluated.
     * @return - the ground height at the given x-coordinate.
     */
    public float groundHeightAt(float x) {
        float temp = groundHeightAtX0 + (float) this.ng.noise(x, NOISE_FACTOR);
        return (float) Math.floor(temp / Block.SIZE) * Block.SIZE;
    }

    /**
     * Creates the terrain (blocks) within the specified x-coordinate range.
     * @param minX - the minimum x-coordinate for the terrain.
     * @param maxX - the maximum x-coordinate for the terrain.
     * @return - a list of Block objects representing the terrain.
     */
    public List<Block> createInRange(int minX, int maxX) {
        ArrayList<Block> result = new ArrayList<>();
        for (int i = minX; i < maxX; i+=Block.SIZE) {
            int highestY = (int)groundHeightAt(i);
            for (int j = 0; j < TERRAIN_DEPTH; j++) {
                Renderable renderable = new RectangleRenderable(ColorSupplier.
                        approximateColor(BASE_GROUND_COLOR));
                Block block = new Block(new Vector2(i, highestY + (Block.SIZE * j)), renderable);
                block.setTag(GROUND_TAG);
                result.add(block);
            }
        }
        return result;
    }
}
