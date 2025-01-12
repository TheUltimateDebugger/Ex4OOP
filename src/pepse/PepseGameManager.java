package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.components.ScheduledTask;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.gui.rendering.ImageRenderable;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import pepse.world.*;
import pepse.world.daynight.Cloud;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.Avatar;
import pepse.world.Block;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.daynight.*;
import pepse.world.trees.Flora;
import pepse.world.trees.Tree;

import java.util.ArrayDeque;
import java.util.Deque;

import java.awt.*;
import java.util.List;


public class PepseGameManager extends GameManager {
    //TODO must be 30
    private static final int CYCLE_LENGTH = 30;
    private static final int RENDER_DISTANT = 3;
    private Deque<Chunk> chunks;
    private int windowWidth;
    private TextRenderable energyDisplay;

    public PepseGameManager() {
        chunks = new ArrayDeque<>();
        windowWidth = 0;
    }

    private ImageReader imageReader;
    private Camera camera;

    public static void main(String[] args) {
        new PepseGameManager().run();
    }



    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener
            inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        this.imageReader = imageReader;
        this.windowWidth = (int)Math.ceil(windowController.getWindowDimensions().x()/Block.SIZE)*Block.SIZE;
        GameObject sky = Sky.create(windowController.getWindowDimensions());
        gameObjects().addGameObject(sky, Layer.BACKGROUND);
        int seed = 2;
        Terrain terrain = new Terrain(windowController.getWindowDimensions(), seed);
        GameObject night = Night.create(windowController.getWindowDimensions(), CYCLE_LENGTH);
        gameObjects().addGameObject(night, Layer.FOREGROUND);
        GameObject sun = Sun.create(windowController.getWindowDimensions(), CYCLE_LENGTH);
        gameObjects().addGameObject(sun, Layer.BACKGROUND);
        Avatar avatar = new Avatar(new Vector2(200, 200), inputListener, imageReader);
        avatar.setCollisionHandler(other -> {
            if (other.getTag().equals("fruit")) {
                Vector2 pos = other.getTopLeftCorner();
                gameObjects().removeGameObject(other, Layer.STATIC_OBJECTS);
                avatar.changeEnergy(10);
                new ScheduledTask(avatar, CYCLE_LENGTH, false, () -> {
                    gameObjects().addGameObject(other, Layer.STATIC_OBJECTS);
                });
            }
        });
        gameObjects().addGameObject(avatar, Layer.DEFAULT);
        GameObject sun_halo = SunHalo.create(sun);
        gameObjects().addGameObject(sun_halo, Layer.BACKGROUND);
//        Flora flora = new Flora(seed, 0.2f, terrain::groundHeightAt);
        Flora flora = new Flora(seed, terrain::groundHeightAt);
        camera = new Camera(avatar,
                windowController.getWindowDimensions().mult(0.1f).subtract(
                        avatar.getTopLeftCorner()),
                windowController.getWindowDimensions(),
                windowController.getWindowDimensions());
        setCamera(camera);
        ImageRenderable cloud_img = imageReader.readImage("./assets/cloud.jpg", true);
        Cloud cloud = new Cloud(new Vector2(100, 100), cloud_img,
                (int) windowController.getWindowDimensions().x(), getAddRainRunnable());
        gameObjects().addGameObject(cloud, Layer.BACKGROUND);
        avatar.addJumpListener(cloud);

        for (int i = -RENDER_DISTANT; i <= RENDER_DISTANT; i++) {
            add_chunk(new Chunk(i*windowWidth, (i+1)*windowWidth, terrain, flora));
        }


        energyDisplay = new TextRenderable("Energy: 100");
        GameObject energyDisplayObject = new GameObject(
                new Vector2(0, 0), // Top-left corner of the screen
                new Vector2(200, 30), // Example size
                energyDisplay
        );
        energyDisplayObject.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects().addGameObject(energyDisplayObject, Layer.UI);
        avatar.setOnEnergyUpdate((energy) -> {energyDisplay.setString("Energy: " + energy);});
    }

    public void updateEnergyDisplay(int energy) {
    }

    private void addTree(Tree tree) {
        for (Block log : tree.getLog())
        {
            gameObjects().addGameObject(log, Layer.STATIC_OBJECTS);
        }
        for (Block b : tree.getLeafs()) {
            gameObjects().addGameObject(b, Layer.BACKGROUND);
        }
        for (Block f : tree.getFruits()) {
            gameObjects().addGameObject(f, Layer.STATIC_OBJECTS);
        }
    }

    private void remove_tree(Tree tree) {
        for (Block log : tree.getLog())
        {
            gameObjects().removeGameObject(log, Layer.STATIC_OBJECTS);
        }
        for (Block b : tree.getLeafs()) {
            gameObjects().removeGameObject(b, Layer.BACKGROUND);
        }
        for (Block f : tree.getFruits()) {
            gameObjects().removeGameObject(f, Layer.STATIC_OBJECTS);
        }
    }

    private void add_chunk(Chunk chunk) {
        if (chunks.isEmpty()) {
            chunks.add(chunk);
        }
        else if (chunks.peekFirst().getMinX() > chunk.getMaxX()) {
            chunks.addFirst(chunk);
        }
        else if (chunks.peek().getMaxX() < chunk.getMinX()) {
            chunks.addLast(chunk);
        }
        for (Block b : chunk.getTerrainBlocks()) {
            gameObjects().addGameObject(b, Layer.STATIC_OBJECTS);
        }
        for (Tree t : chunk.getTrees()) {
            addTree(t);
        }
    }

    private void remove_chunk(boolean remove_first) {
        Chunk chunkToRemove;
        if (remove_first) {
            chunkToRemove = chunks.removeFirst();
        }
        else {
            chunkToRemove = chunks.removeLast();
        }
        for (Block b : chunkToRemove.getTerrainBlocks()) {
            gameObjects().removeGameObject(b, Layer.STATIC_OBJECTS);
        }
        for (Tree t : chunkToRemove.getTrees()) {
            remove_tree(t);
        }
    }

    public CloudAction getAddRainRunnable() {
        return (cloud) -> {
            int num = (int) (Math.random() * 4) + 1;
            for (int i = 0; i < num; i++) {
                RainDrop drop = new RainDrop(cloud.getVisualCenterInAbsoluteSpace(
                        camera.getTopLeftCorner()).subtract(new Vector2(
                                (int) (Math.random() * 40 - 20), 40 * i)),
                        imageReader.readImage("./assets/raindrop.jpg", true),
                        rainDrop -> {
                            gameObjects().removeGameObject(rainDrop, Layer.DEFAULT);
                        });
                gameObjects().addGameObject(drop, Layer.DEFAULT);
            }
        };
    }

}
