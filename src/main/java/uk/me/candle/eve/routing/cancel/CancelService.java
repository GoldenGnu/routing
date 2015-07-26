package uk.me.candle.eve.routing.cancel;

/**
 *
 * @author Candle
 */
public interface CancelService {
    public boolean isCancelled();
    public void cancel();
}
