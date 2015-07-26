package uk.me.candle.eve.routing.cancel;

/**
 *
 * @author Candle
 */
public class DefaultCancelService implements CancelService {
    boolean cancelled = false;
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void cancel() {
        cancelled = true;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
