package pepse.world;

import danogl.GameObject;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.ImageRenderable;
import danogl.util.Vector2;
import java.awt.event.KeyEvent;

public class Avatar extends GameObject {
    private UserInputListener userInputListener;
    private static final int GRAVITY = 800, VELOCITY_X =400, VELOCITY_Y = -600;
    private static final double STARTING_ENERGY = 100f, MOVE_COST = 0.5f,
            JUMP_COST = 10f, STATIC_GAIN = 1f;
    private double energy;
    private final AnimationRenderable idleAnimation, jumpAnimation, runAnimation;
    public Avatar(Vector2 topLeftCorner, UserInputListener inputListener,
                  ImageReader imageReader) {
        super(topLeftCorner, new Vector2(Block.SIZE, 40),
                new AnimationRenderable(new ImageRenderable[]{
                        imageReader.readImage("./assets/idle_0.png", true),
                        imageReader.readImage("./assets/idle_1.png", true),
                        imageReader.readImage("./assets/idle_2.png", true),
                        imageReader.readImage("./assets/idle_3.png", true)
                }, 0.2f));
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY);
        this.userInputListener = inputListener;
        this.energy = STARTING_ENERGY;
        this.idleAnimation = new AnimationRenderable(new ImageRenderable[]{
                imageReader.readImage("./assets/idle_0.png", true),
                imageReader.readImage("./assets/idle_1.png", true),
                imageReader.readImage("./assets/idle_2.png", true),
                imageReader.readImage("./assets/idle_3.png", true)
        }, 0.2f);
        this.jumpAnimation = new AnimationRenderable(new ImageRenderable[]{
                imageReader.readImage("./assets/jump_0.png", true),
                imageReader.readImage("./assets/jump_1.png", true),
                imageReader.readImage("./assets/jump_2.png", true),
                imageReader.readImage("./assets/jump_3.png", true)
        }, 0.2f);
        this.runAnimation = new AnimationRenderable(new ImageRenderable[]{
                imageReader.readImage("./assets/run_0.png", true),
                imageReader.readImage("./assets/run_1.png", true),
                imageReader.readImage("./assets/run_2.png", true),
                imageReader.readImage("./assets/run_3.png", true),
                imageReader.readImage("./assets/run_4.png", true),
                imageReader.readImage("./assets/run_5.png", true)
        }, 0.2f);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float xVel = 0;
        boolean moved = false;
        if (energy >= MOVE_COST) {
            if (userInputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
                xVel -= VELOCITY_X;
                renderer().setIsFlippedHorizontally(true);
            }
            if (userInputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
                xVel += VELOCITY_X;
                renderer().setIsFlippedHorizontally(false);
            }
            if (xVel != 0) {
                if (transform().getVelocity().y() == 0)
                renderer().setRenderable(runAnimation);
                energy -= MOVE_COST;
                moved = true;
            }
            transform().setVelocityX(xVel);
        }
        // TODO fix glitch
        if(userInputListener.isKeyPressed(KeyEvent.VK_SPACE) && getVelocity().y() == 0
        && energy >= JUMP_COST) {
            renderer().setRenderable(jumpAnimation);
            transform().setVelocityY(VELOCITY_Y);
            energy -= JUMP_COST;
            moved = true;
        }
        if (!moved) {
            if (transform().getVelocity().y() == 0) {
                renderer().setRenderable(idleAnimation);
            }
            energy += STATIC_GAIN;
        }
    }

    public void changeEnergy(float energy) {
        this.energy += energy;
    }
    // TODO implement jump listener
}
