package uk.me.candle.eve.routing;

import java.util.List;
import uk.me.candle.eve.graph.Graph;
import uk.me.candle.eve.graph.Node;
import uk.me.candle.eve.routing.engines.simpleUnisexMutatorHibrid2Opt.SimpleUnisexMutatorHibrid2OptEngine;

/**
 *
 * @author Niklas
 */
public class SimpleUnisexMutatorHibrid2Opt extends AbstractEngine {

	public SimpleUnisexMutatorHibrid2Opt() {
		this(true);
	}

	public SimpleUnisexMutatorHibrid2Opt(boolean loop) {
		super("Mutation 2-opt",
			"Genetic Algorithm: Mutation\n" +
			"The pairwise exchange or 2-opt technique involves iteratively removing two edges and replacing these with two different edges that reconnect the fragments created by edge removal into a new and shorter tour.", loop);
	}

	@Override
	public int getWaypointLimit() {
		return 800;
	}

	@Override
	public List<Node> execute(Progress progress, Graph g, List<? extends Node> assetLocations) {
		return super.execute(progress, g, assetLocations, new SimpleUnisexMutatorHibrid2OptEngine());
	}

	@Override
	protected String getSpeed() {
		return "Slow";
	}

	@Override
	protected String getRoute() {
		return "Good";
	}
}