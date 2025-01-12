/**
 * class representing a chunk of terrain with trees and terrain blocks.
 * @author idomi
 */
package pepse.world;

import danogl.gui.WindowController;
import danogl.util.Vector2;
import pepse.world.trees.Flora;
import pepse.world.trees.Tree;
import java.util.List;

public class Chunk {
    // the minimum x-coordinate of the chunk
    private int minX;
    // the maximum x-coordinate of the chunk
    private int maxX;
    // the list of terrain blocks in the chunk
    private List<Block> terrain;
    // the list of trees in the chunk
    private List<Tree> trees;

    /**
     * constructor to create a chunk of terrain with trees.
     * @param minX - the minimum x-coordinate of the chunk.
     * @param maxX - the maximum x-coordinate of the chunk.
     * @param terrain - the terrain generator used to create terrain blocks.
     * @param flora - the flora generator used to create trees.
     */
    public Chunk(int minX, int maxX, Terrain terrain, Flora flora) {
        this.minX = minX;
        this.maxX = maxX;
        this.terrain = terrain.createInRange(minX, maxX);
        this.trees = flora.createInRange(minX, maxX);
    }

    /**
     * gets the minimum x-coordinate of the chunk.
     * @return - the minimum x-coordinate.
     */
    public int getMinX() {
        return minX;
    }

    /**
     * gets the maximum x-coordinate of the chunk.
     * @return - the maximum x-coordinate.
     */
    public int getMaxX() {
        return maxX;
    }

    /**
     * gets the terrain blocks in the chunk.
     * @return - the list of terrain blocks.
     */
    public List<Block> getTerrainBlocks() {
        return terrain;
    }

    /**
     * gets the trees in the chunk.
     * @return - the list of trees.
     */
    public List<Tree> getTrees() {
        return trees;
    }
}
