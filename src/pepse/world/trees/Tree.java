package pepse.world.trees;

import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
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

public class Tree {
    private static final Color LOG_COLOR = new Color(100, 50, 20);
    private static final Color LEAFS_COLOR = new Color(50, 200, 30);
    private static final int MINIMUM_LOG_HEIGHT = 5;
    private static final int MAXIMUM_LOG_GROWTH = 2;
    public static final int BRANCH_SIZE = 2;
    private static final float LEAF_PROB = 0.8f;
    private static final float FRUIT_PROB = 0.2f;

    private List<Block> log;
    private List<Block> leafs;
    private List<Block> fruits;

    public Tree (float startLocationX, float startLocationY, int seed) {
        Random rand = new Random(Objects.hash(startLocationX, seed));
        create_log(startLocationX, startLocationY, rand);
        create_leafs_and_fruits(rand);
    }

    private void create_log(float startLocationX, float startLocationY, Random rand) {
        this.log = new ArrayList<>();
        Renderable renderer_log = new RectangleRenderable(ColorSupplier.approximateColor(LOG_COLOR));
        int logHeight = MINIMUM_LOG_HEIGHT + rand.nextInt(MAXIMUM_LOG_GROWTH);
        for (int i = 0; i < logHeight; i++) {
            this.log.add(new Block(new Vector2(startLocationX, startLocationY - (i+1) * Block.SIZE),
                    renderer_log));
            this.log.get(this.log.size()-1).setTag("log");
        }
    }

    private void create_leafs_and_fruits(Random rand) {
        this.leafs = new ArrayList<>();
        this.fruits = new ArrayList<>();
        for (int x = -BRANCH_SIZE; x <= BRANCH_SIZE; x++) {
            for (int y = -BRANCH_SIZE; y <= BRANCH_SIZE; y++) {
                if (rand.nextFloat() < LEAF_PROB) {
                    Renderable renderer_leaf = new RectangleRenderable(ColorSupplier.
                            approximateColor(LEAFS_COLOR));

                    leafs.add(create_leaf(new Vector2(log.get(log.size() - 1).getTopLeftCorner()
                            .add(new Vector2(x * Block.SIZE, y * Block.SIZE))), renderer_leaf, rand));
                }
                else if (x != 0 && rand.nextFloat() < FRUIT_PROB) {
                    Renderable renderer_fruit = new OvalRenderable(ColorSupplier.
                            approximateColor(Color.RED));
                    fruits.add(create_fruit(new Vector2(log.get(log.size() - 1).getTopLeftCorner()
                            .add(new Vector2(x * Block.SIZE, y * Block.SIZE))), renderer_fruit));
                }
            }
        }
    }

    private Block create_leaf(Vector2 pos, Renderable renderer_leaf, Random rand) {
        Block leaf = new Block(pos, renderer_leaf);
        leaf.setTag("leaf");
        //TODO: decide on good parameters to this
        Runnable supply_size = () -> new Transition<Float>(leaf,
                (Float a) -> leaf.renderer().setRenderableAngle(a),
                0f, 30f, Transition.LINEAR_INTERPOLATOR_FLOAT,
                2, Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
        Runnable supply_angle = () ->new Transition<Float>(leaf,
                (Float a) -> leaf.setDimensions(
                        new Vector2(Block.SIZE * a, Block.SIZE)),
                1f, 0.5f, Transition.LINEAR_INTERPOLATOR_FLOAT,
                2, Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
        float delay1 = rand.nextFloat();
        float delay2 = rand.nextFloat();
        new ScheduledTask(leaf, delay1, false, supply_size);
        new ScheduledTask(leaf, delay2, false, supply_angle);
        return leaf;
    }

    private Block create_fruit(Vector2 pos, Renderable renderer_fruit) {
        Block fruit = new Block(pos, renderer_fruit);
        fruit.setTag("fruit");
        return fruit;
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
