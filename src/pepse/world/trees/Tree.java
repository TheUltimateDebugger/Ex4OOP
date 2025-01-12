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

/**
 * class representing a tree with a log, leaves, and fruits.
 * @author Tomer Zilbeman
 */
public class Tree {
    // color of the tree's log
    private static final Color LOG_COLOR = new Color(100, 50, 20);
    // color of the tree's leaves
    private static final Color LEAFS_COLOR = new Color(50, 200, 30);
    // minimum height of the log
    private static final int MINIMUM_LOG_HEIGHT = 5;
    // maximum additional growth for the log
    private static final int MAXIMUM_LOG_GROWTH = 2;
    // branch size (how wide the tree branches out)
    public static final int BRANCH_SIZE = 2;
    // probability of a leaf at a given position
    private static final float LEAF_PROB = 0.8f;
    // probability of fruit on a branch if there are no leafs there
    private static final float FRUIT_PROB = 0.2f;
    //tag for tree
    public static final String LOG_TAG = "tree";
    //tag for leaf
    public static final String LEAF_TAG = "leaf";
    //tag for fruit
    public static final String FRUIT_TAG = "fruit";

    // constant angles
    private static final float INITIAL_LEAF_ANGLE = 0f;
    private static final float FINAL_LEAF_ANGLE = 30f;
    private static final float LEAF_TRANSITION_TIME = 2f;
    private static final float LEAF_INITIAL_SIZE_FACTOR = 1f;
    private static final float LEAF_FINAL_SIZE_FACTOR = 0.5f;



    private List<Block> log;
    private List<Block> leafs;
    private List<Block> fruits;

    /**
     * constructor for creating a tree at a given location with a seed for randomness.
     * @param startLocationX - the starting x-coordinate of the tree.
     * @param startLocationY - the starting y-coordinate of the tree.
     * @param seed - seed for random number generation.
     */
    public Tree (float startLocationX, float startLocationY, int seed) {
        Random rand = new Random(Objects.hash(startLocationX, seed));
        create_log(startLocationX, startLocationY, rand);
        create_leafs_and_fruits(rand);
    }

    /**
     * creates the tree's log with a random height.
     * @param startLocationX - the x-coordinate of the log's starting point.
     * @param startLocationY - the y-coordinate of the log's starting point.
     * @param rand - random number generator for tree variation.
     */
    private void create_log(float startLocationX, float startLocationY, Random rand) {
        this.log = new ArrayList<>();
        Renderable renderer_log = new RectangleRenderable(ColorSupplier.approximateColor(LOG_COLOR));
        int logHeight = MINIMUM_LOG_HEIGHT + rand.nextInt(MAXIMUM_LOG_GROWTH);
        for (int i = 0; i < logHeight; i++) {
            this.log.add(new Block(new Vector2(startLocationX, startLocationY - (i+1) * Block.SIZE),
                    renderer_log));
            this.log.get(this.log.size()-1).setTag(LOG_TAG);
        }
    }

    /**
     * creates the tree's leaves and fruits based on random probabilities.
     * @param rand - random number generator for leaf and fruit placement.
     */
    private void create_leafs_and_fruits(Random rand) {
        this.leafs = new ArrayList<>();
        this.fruits = new ArrayList<>();
        for (int x = -BRANCH_SIZE; x <= BRANCH_SIZE; x++) {
            for (int y = -BRANCH_SIZE; y <= BRANCH_SIZE; y++) {
                if (rand.nextFloat() < LEAF_PROB) { // create a leaf
                    Renderable renderer_leaf = new RectangleRenderable(ColorSupplier.
                            approximateColor(LEAFS_COLOR));

                    leafs.add(create_leaf(new Vector2(log.get(log.size() - 1).getTopLeftCorner()
                            .add(new Vector2(x * Block.SIZE,
                                    y * Block.SIZE))), renderer_leaf, rand));
                }
                else if (x != 0 && rand.nextFloat() < FRUIT_PROB) { // create a fruit
                    Renderable renderer_fruit = new OvalRenderable(ColorSupplier.
                            approximateColor(Color.RED));
                    fruits.add(create_fruit(new Vector2(log.get(log.size() - 1).getTopLeftCorner()
                            .add(new Vector2(x * Block.SIZE,
                                    y * Block.SIZE))), renderer_fruit));
                }
            }
        }
    }

    /**
     * creates a leaf block with an animation.
     * @param pos - the position of the leaf block.
     * @param renderer_leaf - the visual representation of the leaf.
     * @param rand - random number generator for leaf behavior.
     * @return leaf - the created leaf block.
     */
    private Block create_leaf(Vector2 pos, Renderable renderer_leaf, Random rand) {
        Block leaf = new Block(pos, renderer_leaf);
        leaf.setTag(LEAF_TAG);
        Runnable supply_angle = () -> new Transition<>(leaf,
                (Float a) -> leaf.renderer().setRenderableAngle(a),
                INITIAL_LEAF_ANGLE, FINAL_LEAF_ANGLE, Transition.LINEAR_INTERPOLATOR_FLOAT,
                LEAF_TRANSITION_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
        Runnable supply_size = () ->new Transition<>(leaf,
                (Float a) -> leaf.setDimensions(
                        new Vector2(Block.SIZE * a, Block.SIZE)),
                LEAF_INITIAL_SIZE_FACTOR, LEAF_FINAL_SIZE_FACTOR,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                LEAF_TRANSITION_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
        float delay1 = rand.nextFloat();
        float delay2 = rand.nextFloat();
        new ScheduledTask(leaf, delay1, false, supply_size);
        new ScheduledTask(leaf, delay2, false, supply_angle);
        return leaf;
    }

    /**
     * creates a fruit block.
     * @param pos - the position of the fruit block.
     * @param renderer_fruit - the visual representation of the fruit.
     * @return fruit - the created fruit block.
     */
    private Block create_fruit(Vector2 pos, Renderable renderer_fruit) {
        Block fruit = new Block(pos, renderer_fruit);
        fruit.setTag(FRUIT_TAG);
        return fruit;
    }

    /**
     * gets the list of blocks representing the tree's log.
     * @return log - the list of tree log blocks.
     */
    public List<Block> getLog() {
        return log;
    }

    /**
     * gets the list of blocks representing the tree's leaves.
     * @return leafs - the list of tree leaf blocks.
     */
    public List<Block> getLeafs() {
        return leafs;
    }

    /**
     * gets the list of blocks representing the tree's fruits.
     * @return fruits - the list of tree fruit blocks.
     */
    public List<Block> getFruits() {
        return fruits;
    }
}
