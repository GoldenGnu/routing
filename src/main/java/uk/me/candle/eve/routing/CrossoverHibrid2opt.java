/*
 * Copyright 2015-2016, Niklas Kyster Rasmussen, Flaming Candle
 *
 * This file is part of Routing
 *
 * Routing is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * Routing is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Routing; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package uk.me.candle.eve.routing;

import java.util.List;
import uk.me.candle.eve.graph.Graph;
import uk.me.candle.eve.graph.Node;
import uk.me.candle.eve.routing.engines.crossoverHibrid2opt.GreedyCrossoverHibrid2OptEngine;


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
