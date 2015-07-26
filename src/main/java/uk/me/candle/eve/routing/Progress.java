package uk.me.candle.eve.routing;

/**
 *
 * @author Candle
 */
public interface Progress {
    public int getMaximum();
    public void setMaximum(int maximum);
    public int getMinimum();
    public void setMinimum(int minimum);
    public int getValue();
    public void setValue(int value);
}
