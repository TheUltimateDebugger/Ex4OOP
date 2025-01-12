package pepse.world.daynight;

/**
 * raindrop class to represent a single raindrop on-screen
 * @author idomi
 */
@FunctionalInterface
public interface RainDropAction {
    void execute(RainDrop rainDrop);
}
