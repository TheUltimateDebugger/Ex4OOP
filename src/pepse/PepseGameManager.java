package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.ScheduledTask;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.gui.rendering.ImageRenderable;
import danogl.util.Vector2;
import pepse.world.Avatar;
import pepse.world.Block;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.daynight.*;
import pepse.world.trees.Flora;
import pepse.world.trees.Tree;

import java.awt.*;
import java.util.List;


public class PepseGameManager extends GameManager {
    //TODO must be 30
    private static final int CYCLE_LENGTH = 30;
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
        GameObject sky = Sky.create(windowController.getWindowDimensions());
        gameObjects().addGameObject(sky, Layer.BACKGROUND);
        int seed = 1;
        Terrain terrain = new Terrain(windowController.getWindowDimensions(), seed);
        List<Block> blocks = terrain.createInRange(0, (int)windowController.getWindowDimensions().x());
        for (Block b : blocks) {
            gameObjects().addGameObject(b, Layer.STATIC_OBJECTS);
        }
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
                System.out.println("RAN1");
                new ScheduledTask(avatar, CYCLE_LENGTH, false, () -> {
                    gameObjects().addGameObject(other, Layer.STATIC_OBJECTS);
                });
            }
        });
        gameObjects().addGameObject(avatar, Layer.DEFAULT);
        GameObject sun_halo = SunHalo.create(sun);
        gameObjects().addGameObject(sun_halo, Layer.BACKGROUND);
        Flora flora = new Flora(seed, 0.2f, terrain::groundHeightAt);
        List<Tree> trees = flora.createInRange(0, (int)windowController.getWindowDimensions().x());
        for (Tree t : trees) {
            addTree(t);
        }
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

    public CloudAction getAddRainRunnable() {
        return (cloud) -> {
            int num = (int) (Math.random() * 3) + 1;
            for (int i = 0; i < num; i++) {
                RainDrop drop = new RainDrop(cloud.getVisualCenterInAbsoluteSpace(camera.getTopLeftCorner()).subtract(Vector2.UP.mult(40 * i)),
                        imageReader.readImage("./assets/raindrop.jpg", true),
                        rainDrop -> {
                            gameObjects().removeGameObject(rainDrop, Layer.DEFAULT);
                            System.out.println("REMOVED");
                        });
                gameObjects().addGameObject(drop, Layer.DEFAULT);
                System.out.println("ADDED DROP");
            }
        };
    }
}
