package pepse.util;

/**
 * Listener class for Avatar jumps
 * @author idomi
 */
@FunctionalInterface
public interface AvatarJumpListener {
    /**
     * a listener function that activates when the avatar jumps
     */
    void onAvatarJump();
}