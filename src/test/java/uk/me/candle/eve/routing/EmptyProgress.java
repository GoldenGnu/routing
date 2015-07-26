
package uk.me.candle.eve.routing;

/**
 *
 * @author Niklas
 */
public class EmptyProgress implements Progress {
	@Override public int getMaximum() {return 0; }
	@Override public void setMaximum(int maximum) { }
	@Override public int getMinimum() {return 0; }
	@Override public void setMinimum(int minimum) { }
	@Override public int getValue() { return 0; }
	@Override public void setValue(int value) { }
}
