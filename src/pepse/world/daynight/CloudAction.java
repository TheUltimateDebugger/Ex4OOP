/**
 * interface for cloud actions.
 * @author idomi
 */
package pepse.world.daynight;

@FunctionalInterface
public interface CloudAction {
    /**
     * execute some action on a cloud.
     * @param - the cloud to do something with.
     * @return - nothing.
     */
    void execute(Cloud cloud);
}
