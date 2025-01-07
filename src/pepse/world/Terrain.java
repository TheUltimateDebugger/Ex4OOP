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

public class Terrain {
    private NoiseGenerator ng;
    private int groundHeightAtX0;
    private static final int TERRAIN_DEPTH = 20;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);

    public Terrain(Vector2 windowDimensions, int seed) {
        this.groundHeightAtX0 = Math.round(windowDimensions.y() * 2/3);
        this.ng = new NoiseGenerator(seed, this.groundHeightAtX0);
    }
    public float groundHeightAt(float x) {
        float temp = groundHeightAtX0 + (float) this.ng.noise(x, Block.SIZE *7);
        return (float) Math.floor(temp / Block.SIZE) * Block.SIZE;
    }

    public List<Block> createInRange(int minX, int maxX) {
        ArrayList<Block> result = new ArrayList<>();
        for (int i = minX; i < maxX; i+=Block.SIZE) {
            int highestY = (int)groundHeightAt(i);
            System.out.println(highestY);
            for (int j = 0; j < TERRAIN_DEPTH; j++) {
                Renderable renderable = new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
                Block block = new Block(new Vector2(i, highestY + (Block.SIZE * j)), renderable);
                block.setTag("ground");
                result.add(block);
            }
        }
        return result;
    }



}
