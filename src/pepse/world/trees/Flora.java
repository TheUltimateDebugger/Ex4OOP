package pepse.world.trees;

import pepse.util.NoiseGenerator;
import pepse.world.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

public class Flora {
    private int seed;
    private float probOfTree;
    private final Function<Float, Float> groundHeight;


    public Flora(int seed, float probOfTree, Function<Float, Float> groundHeight) {
        this.seed = seed;
        this.probOfTree = probOfTree;
        this.groundHeight = groundHeight;
    }

    public List<Tree> createInRange(int minX, int maxX) {
        List<Tree> trees = new ArrayList<>();
        for (int x = minX; x < maxX; x+=Block.SIZE) {
            Random rand = new Random(Objects.hash(x, seed));
            if (rand.nextFloat() < probOfTree) {
                trees.add(new Tree(x, groundHeight.apply((float) x), seed));

            }
        }
        return trees;
    }
}
