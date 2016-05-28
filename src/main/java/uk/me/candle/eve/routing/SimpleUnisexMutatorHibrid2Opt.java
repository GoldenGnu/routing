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
import uk.me.candle.eve.routing.engines.simpleUnisexMutatorHibrid2Opt.SimpleUnisexMutatorHibrid2OptEngine;


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
