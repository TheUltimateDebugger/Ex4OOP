package pepse.world.daynight;

/**
 * raindrop class to represent a single raindrop on-screen
 * @author idomi
 */
@FunctionalInterface
public interface RainDropAction {
    /**
     * function that creates the rain drops
     * @param rainDrop a class of rain drop
     */
    void execute(RainDrop rainDrop);
}
