package uk.me.candle.eve.routing;

import java.util.List;
import uk.me.candle.eve.graph.Graph;
import uk.me.candle.eve.graph.Node;
import uk.me.candle.eve.routing.engines.simpleUnisexMutator.SimpleUnisexMutatorEngine;

/**
 *
 * @author Niklas
 */
public class SimpleUnisexMutator extends AbstractEngine {

	public SimpleUnisexMutator() {
		this(true);
	}

	public SimpleUnisexMutator(boolean loop) {
		super("SimpleUnisexMutator", "SimpleUnisexMutator", loop);
	}

	@Override
	public List<Node> execute(Progress progress, Graph g, List<? extends Node> assetLocations) {
		return super.execute(progress, g, assetLocations, new SimpleUnisexMutatorEngine());
	}

	@Override
	protected String getSpeed() {
		return "Fast";
	}

	@Override
	protected String getRoute() {
		return "Worst";
	}

	
}
