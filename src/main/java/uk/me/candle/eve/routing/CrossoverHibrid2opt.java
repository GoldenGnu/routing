package uk.me.candle.eve.routing;

import java.util.List;
import uk.me.candle.eve.graph.Graph;
import uk.me.candle.eve.graph.Node;
import uk.me.candle.eve.routing.engines.crossoverHibrid2opt.GreedyCrossoverHibrid2OptEngine;

/**
 *
 * @author Niklas
 */
public class CrossoverHibrid2opt extends AbstractEngine {

	public CrossoverHibrid2opt() {
		this(true);
	}

	public CrossoverHibrid2opt(boolean loop) {
		super("GA: Crossover & 2-opt",
			"Genetic algorithm: Crossover\n"
			+ "Iterative improvement: Pairwise exchange (2-opt)", loop);
	}

	@Override
	public List<Node> execute(Progress progress, Graph g, List<? extends Node> assetLocations) {
		return super.execute(progress, g, assetLocations, new GreedyCrossoverHibrid2OptEngine());
	}

	@Override
	protected String getSpeed() {
		return "Very Slow";
	}

	@Override
	protected String getRoute() {
		return "Good";
	}
}
