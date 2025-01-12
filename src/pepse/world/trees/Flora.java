/**
 * class for generating and creating trees in the game world.
 * @author idomi
 */
package pepse.world.trees;
import pepse.world.Block;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

public class Flora {
    //the probability for a tree to be created (if it won't intersect other trees)
    private static final float PROB_OF_TREE = 0.2f;
    //how far to skip after placing a tree
    private static final int SKIP_AFTER_TREE = Block.SIZE * 2 * Tree.BRANCH_SIZE;
    //the seed of the game, Flora will adjust it based on location
    private final int seed;
    //the function to calculate the height in a given point
    private final Function<Float, Float> groundHeight;

    /**
     * constructor to initialize the flora generator with a seed and ground height function.
     * @param seed - the seed to initialize random number generation.
     * @param groundHeight - function to get the ground height at a given x position.
     */
    public Flora(int seed, Function<Float, Float> groundHeight) {
        this.seed = seed;
        this.groundHeight = groundHeight;
    }

    /**
     * creates a list of trees within the given x range.
     * @param minX - the starting x-coordinate for generating trees.
     * @param maxX - the ending x-coordinate for generating trees.
     * @return trees - the list of created Tree objects.
     */
    public List<Tree> createInRange(int minX, int maxX) {
        List<Tree> trees = new ArrayList<>();
        // create a random number generator based on seed and minX
        Random rand = new Random(Objects.hash(minX, seed));
        for (int x = minX + SKIP_AFTER_TREE; x < maxX; x+=Block.SIZE) {
            if (rand.nextFloat() < PROB_OF_TREE) {
                // add a new tree at this x position
                trees.add(new Tree(x, groundHeight.apply((float) x), seed));
                // skip ahead after placing a tree
                x += SKIP_AFTER_TREE;
            }
        }
        return trees; // return the list of created trees
    }
}
