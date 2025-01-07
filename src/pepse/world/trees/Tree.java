package pepse.world.trees;

import danogl.GameObject;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Block;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

public class Tree {
    private static final Color LOG_COLOR = new Color(100, 50, 20);
    private static final Color LEAFS_COLOR = new Color(50, 200, 30);
    private static final int MINIMUM_LOG_HEIGHT = 4;
    private static final int MAXIMUM_LOG_GROWTH = 3;
    private static final int BRANCH_SIZE = 2;
    private static final float LEAF_PROB = 0.8f;

    private List<Block> log;
    private List<Block> leafs;
    private List<Block> fruits;

    public Tree (float startLocationX, float startLocationY, int seed) {
        this.log = new ArrayList<>();
        this.leafs = new ArrayList<>();
        Random rand = new Random(Objects.hash(startLocationX, seed));
        Renderable renderer_log = new RectangleRenderable(ColorSupplier.approximateColor(LOG_COLOR));
        int logHeight = MINIMUM_LOG_HEIGHT + rand.nextInt(MAXIMUM_LOG_GROWTH);
        for (int i = 0; i < logHeight; i++) {
            this.log.add(new Block(new Vector2(startLocationX, startLocationY - (i+1) * Block.SIZE),
                    renderer_log));
        }
        for (int x = -BRANCH_SIZE; x <= BRANCH_SIZE; x++) {
            for (int y = -BRANCH_SIZE; y <= BRANCH_SIZE; y++) {
                if (rand.nextFloat() < LEAF_PROB) {
                    Renderable renderer_leafs = new RectangleRenderable(ColorSupplier.
                            approximateColor(LEAFS_COLOR));
                    Block leaf = new Block(new Vector2(log.get(log.size() - 1).getTopLeftCorner()
                            .add(new Vector2(x * Block.SIZE, y * Block.SIZE))), renderer_leafs);
                    leafs.add(leaf);
                }
            }
        }

    }

    public List<Block> getLog() {
        return log;
    }

    public List<Block> getLeafs() {
        return leafs;
    }

    public List<Block> getFruits() {
        return fruits;
    }
}
