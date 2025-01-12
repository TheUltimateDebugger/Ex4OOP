/**
 * class representing the avatar character in the game with physics, animation, and energy management.
 * @author idomi
 */
package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.ImageRenderable;
import danogl.util.Vector2;
import pepse.util.AvatarJumpListener;
import pepse.util.CollisionHandler;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Avatar extends GameObject {
    private UserInputListener userInputListener;
    private static final int GRAVITY = 800, VELOCITY_X =200, VELOCITY_Y = -600;
    public static final double MAX_ENERGY = 100f, MOVE_COST = 0.5f,
            JUMP_COST = 10f, STATIC_GAIN = 1f;
    private double energy;
    private CollisionHandler collisionHandler = null;
    private final AnimationRenderable idleAnimation, jumpAnimation, runAnimation;
    private List<AvatarJumpListener> jumpListeners = new ArrayList<>();
    private Consumer<Double> onEnergyUpdate;

    /**
     * constructor for creating an avatar with specified position, input listener, and image reader.
     * @param topLeftCorner - the position of the avatar's top left corner.
     * @param inputListener - listener for user input.
     * @param imageReader - image reader for loading avatar animations.
     */
    public Avatar(Vector2 topLeftCorner, UserInputListener inputListener, ImageReader imageReader) {
        // call super
        super(topLeftCorner, new Vector2(Block.SIZE, 40),
        //TODO: magic numbers
                new AnimationRenderable(new ImageRenderable[]{
                        imageReader.readImage("./assets/idle_0.png", true),
                        imageReader.readImage("./assets/idle_1.png", true),
                        imageReader.readImage("./assets/idle_2.png", true),
                        imageReader.readImage("./assets/idle_3.png", true)
                }, 0.2f));
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY);
        this.userInputListener = inputListener;
        this.energy = MAX_ENERGY;
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

    /**
     * adds a listener to be notified when the avatar jumps.
     * @param listener - the listener to add.
     */
    public void addJumpListener(AvatarJumpListener listener) {
        jumpListeners.add(listener);
    }

    /**
     * sets a consumer to be notified whenever the avatar's energy is updated.
     * @param onEnergyUpdate - the consumer to call when energy is updated.
     */
    public void setOnEnergyUpdate(Consumer<Double> onEnergyUpdate) {
        this.onEnergyUpdate = onEnergyUpdate;
    }

    /**
     * updates the avatar's state, including movement, animation, and energy usage.
     * @param deltaTime - the time difference between frames.
     */
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
                if (transform().getVelocity().y() == 0) {
                    renderer().setRenderable(runAnimation);
                    energy -= MOVE_COST;
                    moved = true;
                }
            }
            transform().setVelocityX(xVel);
        }
        if(userInputListener.isKeyPressed(KeyEvent.VK_SPACE) && getVelocity().y() == 0
                && energy >= JUMP_COST) {
            renderer().setRenderable(jumpAnimation);
            transform().setVelocityY(VELOCITY_Y);
            energy -= JUMP_COST;
            moved = true;
            for (AvatarJumpListener listener : jumpListeners) {
                listener.onAvatarJump();
            }
        }
        if (!moved) {
            if (transform().getVelocity().y() == 0) {
                renderer().setRenderable(idleAnimation);
                energy += STATIC_GAIN;
            }
            if (energy > MAX_ENERGY)
                energy = MAX_ENERGY;
        }
        onEnergyUpdate.accept(energy);
    }

    /**
     * sets the collision handler for the avatar.
     * @param collisionHandler - the collision handler to use.
     */
    public void setCollisionHandler(CollisionHandler collisionHandler) {
        this.collisionHandler = collisionHandler;
    }

    /**
     * handles collisions when the avatar enters another game object.
     * @param other - the other game object involved in the collision.
     * @param collision - the collision data.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if(other.getTag().equals("block")){
            this.transform().setVelocityY(0);
        }

        if (collisionHandler != null) {
            try {
                collisionHandler.handleCollision(other);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * changes the avatar's energy by the specified amount.
     * @param energy - the amount to change the avatar's energy by.
     */
    public void changeEnergy(float energy) {
        this.energy += energy;
    }
}
