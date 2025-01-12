package pepse.world.trees;

import pepse.util.NoiseGenerator;
import pepse.world.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

public class Flora {
    private static final float PROB_OF_TREE = 0.2f;
    private static final int SKIP_AFTER_TREE = Block.SIZE * 2 * Tree.BRANCH_SIZE;
    private int seed;
    private final Function<Float, Float> groundHeight;


    public Flora(int seed, Function<Float, Float> groundHeight) {
        this.seed = seed;
        this.groundHeight = groundHeight;
    }

    public List<Tree> createInRange(int minX, int maxX) {
        List<Tree> trees = new ArrayList<>();
        Random rand = new Random(Objects.hash(minX, seed));
        for (int x = minX + SKIP_AFTER_TREE; x < maxX; x+=Block.SIZE) {
            if (rand.nextFloat() < PROB_OF_TREE) {
                trees.add(new Tree(x, groundHeight.apply((float) x), seed));
                x += SKIP_AFTER_TREE;
            }
        }
        return trees;
    }
}
