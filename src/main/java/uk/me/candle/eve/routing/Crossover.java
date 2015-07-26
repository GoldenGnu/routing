
package uk.me.candle.eve.routing;

import java.util.List;
import uk.me.candle.eve.graph.Graph;
import uk.me.candle.eve.graph.Node;
import uk.me.candle.eve.routing.engines.crossover.GreedyCrossoverEngine;

/**
 *
 * @author Niklas
 */
public class Crossover extends AbstractEngine{

	public Crossover() {
		this(true);
	}

	public Crossover(boolean loop) {
		super("Crossover", "Genetic Algorithm: Crossover", loop);
	}

	@Override
	public List<Node> execute(Progress progress, Graph g, List<? extends Node> assetLocations) {
		return super.execute(progress, g, assetLocations, new GreedyCrossoverEngine());
	}

	@Override
	public int getWaypointLimit() {
		return 800;
	}

	@Override
	protected String getSpeed() {
		return "Fast";
	}

	@Override
	protected String getRoute() {
		return "Average";
	}
}
