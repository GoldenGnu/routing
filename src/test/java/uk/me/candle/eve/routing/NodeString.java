package uk.me.candle.eve.routing;

import uk.me.candle.eve.graph.Node;

/**
 *
 * @author Niklas
 */
public class NodeString extends Node {

	public NodeString(String name) {
		super(name);
	}

	@Override
	public String toString() {
		return getName();
	}
	
}
