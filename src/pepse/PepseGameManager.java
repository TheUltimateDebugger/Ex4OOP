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

import static pepse.world.Avatar.MAX_ENERGY;
import static pepse.world.daynight.Cloud.CLOUD_SIZE;


/**
 * Manages the core game logic, including initialization, game objects, chunks, and day-night cycle.
 * @author idomi
 */
public class PepseGameManager extends GameManager {
    // The length of the day-night cycle in game time.
    private static final int CYCLE_LENGTH = 30;
    // The render distance in terms of chunks.
    private static final int RENDER_DISTANT = 3;
    //fruit's energy to the player
    private static final int FRUIT_ENERGY = 10;
    private static final Vector2 AVATAR_SIZE = new Vector2(200, 200);
    //the seed of the game
    private static final int SEED = 1;
    // A queue of chunks to manage rendering.
    private Deque<Chunk> chunks;
    // The width of the window.
    private int windowWidth;
    // The display for energy information.
    private TextRenderable energyDisplay;
    // The terrain object representing the world’s ground.
    private Terrain terrain;
    // The flora object representing trees and plant life.
    private Flora flora;
    //the avatar of the game
    private Avatar avatar;
    //the image reader used to read images
    private ImageReader imageReader;
    //the camera object of the game
    private Camera camera;
    // camera dimensions factor
    private static final float CAMERA_FACTOR = 0.1f;
    // paths
    private static final String CLOUD_PATH = "./assets/cloud.jpg",
            RAINDROP_PATH = "./assets/raindrop.jpg";
    // energy HUD text
    private static final String ENERGY_TEXT = "Energy: ";
    private static final Vector2 ENERGY_DISPLAY_SIZE = new Vector2(200, 30);

    /**
     * Constructs the PepseGameManager.
     */
    public PepseGameManager() {
        chunks = new ArrayDeque<>();
        windowWidth = 0;
    }



    /**
     * Starts the game by initializing the necessary components.
     * @param args - command line arguments (not used).
     */
    public static void main(String[] args) {
        new PepseGameManager().run();
    }


    /**
     * Initializes the game, sets up the environment, and creates game objects.
     * @param imageReader - used to read images for game objects.
     * @param soundReader - used to read sound files (not currently used).
     * @param inputListener - used to listen to user inputs (keyboard).
     * @param windowController - provides the window dimensions.
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        this.imageReader = imageReader;
        this.windowWidth = (int)Math.ceil(windowController.getWindowDimensions().x()/Block.SIZE)*Block.SIZE;

        // Set up background and environment objects
        GameObject sky = Sky.create(windowController.getWindowDimensions());
        gameObjects().addGameObject(sky, Layer.BACKGROUND);
        // Initialize terrain and environment objects
        terrain = new Terrain(windowController.getWindowDimensions(), SEED);
        GameObject night = Night.create(windowController.getWindowDimensions(), CYCLE_LENGTH);
        gameObjects().addGameObject(night, Layer.FOREGROUND);
        GameObject sun = Sun.create(windowController.getWindowDimensions(), CYCLE_LENGTH);
        gameObjects().addGameObject(sun, Layer.BACKGROUND);
        System.out.println(windowController.getWindowDimensions());
        // Set up avatar
        avatar = new Avatar(AVATAR_SIZE, inputListener, imageReader);
        avatar.setCollisionHandler(other -> {
            if (other.getTag().equals(Tree.FRUIT_TAG)) {
                gameObjects().removeGameObject(other, Layer.STATIC_OBJECTS);
                avatar.changeEnergy(FRUIT_ENERGY);
                // function to create an apple if still in render distance
                new ScheduledTask(avatar, CYCLE_LENGTH, false, () -> {
                    if (other.getCenter().x() > chunks.peekFirst().getMinX() &&
                            other.getCenter().x() < chunks.peekLast().getMaxX())
                        gameObjects().addGameObject(other, Layer.STATIC_OBJECTS);
                });
            }
        });
        gameObjects().addGameObject(avatar, Layer.DEFAULT);

        // Set up sun halo and camera
        GameObject sun_halo = SunHalo.create(sun);
        gameObjects().addGameObject(sun_halo, Layer.BACKGROUND);
        flora = new Flora(SEED, terrain::groundHeightAt);
        camera = new Camera(avatar,
                windowController.getWindowDimensions().mult(CAMERA_FACTOR).subtract(
                        avatar.getTopLeftCorner()),
                windowController.getWindowDimensions(),
                windowController.getWindowDimensions());
        setCamera(camera);

        // Create and add cloud object
        ImageRenderable cloud_img = imageReader.readImage(CLOUD_PATH, true);
        Cloud cloud = new Cloud(CLOUD_SIZE, cloud_img,
                (int) windowController.getWindowDimensions().x(), getAddRainRunnable());
        gameObjects().addGameObject(cloud, Layer.BACKGROUND);
        avatar.addJumpListener(cloud);

        // Add terrain chunks to the game world
        for (int i = -RENDER_DISTANT; i <= RENDER_DISTANT; i++) {
            add_chunk(new Chunk(i * windowWidth, (i + 1) * windowWidth, terrain, flora));
        }

        // Set up energy display
        energyDisplay = new TextRenderable(ENERGY_TEXT + MAX_ENERGY);
        GameObject energyDisplayObject = new GameObject(
                Vector2.ZERO,
                ENERGY_DISPLAY_SIZE,
                energyDisplay
        );
        energyDisplayObject.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects().addGameObject(energyDisplayObject, Layer.UI);
        avatar.setOnEnergyUpdate((energy) -> { energyDisplay.setString(ENERGY_TEXT + energy); });
    }

    /**
     * updates the game (mainly the chunks)
     * @param deltaTime The time, in seconds, that passed since the last invocation
     *                  of this method (i.e., since the last frame). This is useful
     *                  for either accumulating the total time that passed since some
     *                  event, or for physics integration (i.e., multiply this by
     *                  the acceleration to get an estimate of the added velocity or
     *                  by the velocity to get an estimate of the difference in position).
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        //too close to the left
        if (avatar.getCenter().x() - chunks.peekFirst().getMinX() < windowWidth * (RENDER_DISTANT-1)) {
            add_chunk(new Chunk(chunks.peekFirst().getMinX() - windowWidth,
                    chunks.peekFirst().getMaxX() - windowWidth, terrain, flora));
            remove_chunk(false);

        }
        //too close to the right
        else if (chunks.peekLast().getMaxX() - avatar.getCenter().x() < windowWidth * (RENDER_DISTANT-1)) {
            remove_chunk(true);
            add_chunk(new Chunk(chunks.peekLast().getMinX() + windowWidth,
                    chunks.peekLast().getMaxX() + windowWidth, terrain, flora));
        }
    }

    /**
     * Updates the energy display.
     * @param energy - the current energy level.
     */
    public void updateEnergyDisplay(int energy) {
    }

    /**
     * Adds a tree to the game world.
     * @param tree - the tree to add.
     */
    private void addTree(Tree tree) {
        for (Block log : tree.getLog()) {
            gameObjects().addGameObject(log, Layer.STATIC_OBJECTS);
        }
        for (Block b : tree.getLeafs()) {
            gameObjects().addGameObject(b, Layer.BACKGROUND);
        }
        for (Block f : tree.getFruits()) {
            gameObjects().addGameObject(f, Layer.STATIC_OBJECTS);
        }
    }

    /**
     * Removes a tree from the game world.
     * @param tree - the tree to remove.
     */
    private void remove_tree(Tree tree) {
        for (Block log : tree.getLog()) {
            gameObjects().removeGameObject(log, Layer.STATIC_OBJECTS);
        }
        for (Block b : tree.getLeafs()) {
            gameObjects().removeGameObject(b, Layer.BACKGROUND);
        }
        for (Block f : tree.getFruits()) {
            gameObjects().removeGameObject(f, Layer.STATIC_OBJECTS);
        }
    }

    /**
     * Adds a chunk to the game world.
     * @param chunk - the chunk to add.
     */
    private void add_chunk(Chunk chunk) {
        if (chunks.isEmpty()) {
            chunks.add(chunk);
        }
        else if (chunks.peekFirst().getMinX() >= chunk.getMaxX()) {
            chunks.addFirst(chunk);
        }
        else if (chunks.peekLast().getMaxX() <= chunk.getMinX()) {
            chunks.addLast(chunk);
        }
        for (Block b : chunk.getTerrainBlocks()) {
            gameObjects().addGameObject(b, Layer.STATIC_OBJECTS);
        }
        for (Tree t : chunk.getTrees()) {
            addTree(t);
        }
    }

    /**
     * Removes a chunk from the game world.
     * @param remove_first - true to remove the first chunk, false to remove the last.
     */
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

    /**
     * Provides a runnable action to create rain when invoked.
     * @return - the action to generate rain.
     */
    public CloudAction getAddRainRunnable() {
        final int RANGE = 40, MAX_DROPS = 4;
        return (cloud) -> {
            int num = (int) (Math.random() * MAX_DROPS) + 1;
            for (int i = 0; i < num; i++) {
                RainDrop drop = new RainDrop(cloud.getVisualCenterInAbsoluteSpace(
                        camera.getTopLeftCorner()).subtract(new Vector2(
                        (int) (Math.random() * RANGE - RANGE / 2), RANGE * i)),
                        imageReader.readImage(RAINDROP_PATH, true),
                        rainDrop -> {
                            gameObjects().removeGameObject(rainDrop, Layer.DEFAULT);
                        });
                gameObjects().addGameObject(drop, Layer.DEFAULT);
            }
        };
    }
}
