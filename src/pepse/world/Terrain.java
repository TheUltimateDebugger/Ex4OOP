package pepse.world;

import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Terrain {
    private int groundHeightAtX0;
    private static final int TERRAIN_DEPTH = 20;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);

    public Terrain(Vector2 windowDimensions, int seed) {
        this.groundHeightAtX0 = Math.round(windowDimensions.y() * 2/3);
    }
    //TODO: change it so we can switch the behavior
    public float groundHeightAt(float x) {
        return groundHeightAtX0;
    }

    public List<Block> createInRange(int minX, int maxX) {
        Renderable renderable = new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
        ArrayList<Block> result = new ArrayList<>();
        for (int i = minX; i < maxX; i+=Block.SIZE) {
            int highestY = (int)Math.floor(groundHeightAt(60) / Block.SIZE) * Block.SIZE;
            for (int j = 0; j < TERRAIN_DEPTH; j++) {
                Block block = new Block(new Vector2(i, highestY + (Block.SIZE * j)), renderable);
                block.setTag("ground");
                result.add(block);
            }
        }
        return result;
    }



}
