/**
 * Manages the core game logic, including initialization, game objects, chunks, and day-night cycle.
 * @author idomi
 */
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
    /** The length of the day-night cycle in game time. */
    private static final int CYCLE_LENGTH = 30;
    /** The render distance in terms of chunks. */
    private static final int RENDER_DISTANT = 1;
    /** A queue of chunks to manage rendering. */
    private Deque<Chunk> chunks;
    /** The width of the window. */
    private int windowWidth;
    /** The display for energy information. */
    private TextRenderable energyDisplay;
    /** The terrain object representing the world’s ground. */
    private Terrain terrain;
    /** The flora object representing trees and plant life. */
    private Flora flora;
    private Avatar avatar;

    /**
     * Constructs the PepseGameManager.
     */
    public PepseGameManager() {
        chunks = new ArrayDeque<>();
        windowWidth = 0;
    }

    private ImageReader imageReader;
    private Camera camera;

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
        int seed = 2;
        terrain = new Terrain(windowController.getWindowDimensions(), seed);
        GameObject night = Night.create(windowController.getWindowDimensions(), CYCLE_LENGTH);
        gameObjects().addGameObject(night, Layer.FOREGROUND);
        GameObject sun = Sun.create(windowController.getWindowDimensions(), CYCLE_LENGTH);
        gameObjects().addGameObject(sun, Layer.BACKGROUND);

        // Set up avatar
        avatar = new Avatar(new Vector2(200, 200), inputListener, imageReader);
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

        // Set up sun halo and camera
        GameObject sun_halo = SunHalo.create(sun);
        gameObjects().addGameObject(sun_halo, Layer.BACKGROUND);
        flora = new Flora(seed, terrain::groundHeightAt);
        camera = new Camera(avatar,
                windowController.getWindowDimensions().mult(0.1f).subtract(
                        avatar.getTopLeftCorner()),
                windowController.getWindowDimensions(),
                windowController.getWindowDimensions());
        setCamera(camera);

        // Create and add cloud object
        ImageRenderable cloud_img = imageReader.readImage("./assets/cloud.jpg", true);
        Cloud cloud = new Cloud(new Vector2(100, 100), cloud_img,
                (int) windowController.getWindowDimensions().x(), getAddRainRunnable());
        gameObjects().addGameObject(cloud, Layer.BACKGROUND);
        avatar.addJumpListener(cloud);

        // Add terrain chunks to the game world
        for (int i = -RENDER_DISTANT; i <= RENDER_DISTANT; i++) {
            add_chunk(new Chunk(i * windowWidth, (i + 1) * windowWidth, terrain, flora));
        }

        // Set up energy display
        energyDisplay = new TextRenderable("Energy: 100");
        GameObject energyDisplayObject = new GameObject(
                new Vector2(0, 0), // Top-left corner of the screen
                new Vector2(200, 30), // Example size
                energyDisplay
        );
        energyDisplayObject.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects().addGameObject(energyDisplayObject, Layer.UI);
        avatar.setOnEnergyUpdate((energy) -> { energyDisplay.setString("Energy: " + energy); });
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        //too left
        if (avatar.getCenter().x() - chunks.peekFirst().getMinX() < windowWidth * (RENDER_DISTANT-1)) {
            System.out.println("remove1: " + chunks.peekLast().getMinX() + " add: " + chunks.peekFirst().getMinX());
            add_chunk(new Chunk(chunks.peekFirst().getMinX() - windowWidth,
                    chunks.peekFirst().getMaxX() - windowWidth, terrain, flora));
            remove_chunk(false);

        }
        //too right
        else if (chunks.peekLast().getMaxX() - avatar.getCenter().x() < windowWidth * (RENDER_DISTANT-1)) {
            System.out.println("remove2: " + chunks.peekFirst().getMinX() + " add: "+ chunks.peekLast().getMinX());
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
