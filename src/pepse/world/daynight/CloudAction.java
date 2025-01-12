package pepse.world.daynight;

/**
 * interface for cloud actions.
 * @author idomi
 */
@FunctionalInterface
public interface CloudAction {
    /**
     * execute some action on a cloud.
     * @param cloud  - the cloud to do something with.
     */
    void execute(Cloud cloud);
}
