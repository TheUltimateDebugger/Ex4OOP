package pepse.world;

import danogl.gui.WindowController;
import danogl.util.Vector2;
import pepse.world.trees.Flora;
import pepse.world.trees.Tree;
import java.util.List;

public class Chunk {
    private int minX;
    private int maxX;
    private List<Block> terrain;
    private List<Tree> trees;

    public Chunk(int minX, int maxX, Terrain terrain, Flora flora) {
        this.minX = minX;
        this.maxX = maxX;
        this.terrain = terrain.createInRange(minX, maxX);
        this.trees = flora.createInRange(minX, maxX);
    }

    public int getMinX() {
        return minX;
    }

    public int getMaxX() {
        return maxX;
    }

    public List<Block> getTerrainBlocks() {
        return terrain;
    }

    public List<Tree> getTrees() {
        return trees;
    }
}
