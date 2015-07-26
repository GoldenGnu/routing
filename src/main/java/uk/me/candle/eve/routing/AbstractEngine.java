/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.me.candle.eve.routing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import uk.me.candle.eve.graph.Graph;
import uk.me.candle.eve.graph.Node;
import uk.me.candle.eve.routing.engines.Manager;
import uk.me.candle.eve.routing.engines.TSPChromosome;
import uk.me.candle.eve.routing.engines.TSPConfiguration;
import uk.me.candle.eve.routing.engines.TSPEngine;

/**
 *
 * @author Niklas
 */
public abstract class AbstractEngine extends RoutingAlgorithm {
	
	long lastTime = -1;
	int lastDistance = -1;
	short[][] distances;
	boolean loop;

	public AbstractEngine(String name, String technicalDescription, boolean loop) {
		super(name, technicalDescription);
		this.loop = loop;
	}

	public List<Node> execute(Progress progress, Graph g, List<? extends Node> assetLocations, TSPEngine engine) {
		long startTime = System.currentTimeMillis();
		lastDistance = -1;
		lastTime = -1;
		TSPConfiguration configuration = new TSPConfiguration();
		configuration.setThreadPriority(Thread.MIN_PRIORITY);
		//configuration.setThreadMaxCount(0);
		
		progress.setMaximum(getReduceMaxProgress(assetLocations) + (configuration.getMaxBestCostAge() * 5));
		distances = reduce(progress, g, assetLocations);
		if (getCancelService().isCancelled()) {
			return Collections.emptyList();
		}
		List<Node> nodes = new ArrayList<Node>(assetLocations);

		Manager.initialize(nodes, distances, 4509809);

		engine.initialize(configuration, nodes.toArray(new Node[nodes.size()]), loop);

		boolean stopRequestFlag = false;
		int bestCostAge = 0;
		TSPChromosome bestChromosome = null;
		double bestCost;
		double previewCost = 0;
		int maxCostAge = 0;
		//repeat the evolotion until stop is required 
		while (!stopRequestFlag) {
			if (getCancelService().isCancelled()) {
				return Collections.emptyList();
			}
			//get best chromosome
			bestChromosome = engine.getBestChromosome();
			bestCost = bestChromosome.getTotalDistance();
			if (previewCost == bestCost) {
				bestCostAge++;
				if (bestCostAge > maxCostAge) {
					maxCostAge = bestCostAge;
					progress.setValue(progress.getValue()+5);
				}
			} else {
				bestCostAge = 0;
			}
			if (bestCostAge >= configuration.getMaxBestCostAge()) {
				stopRequestFlag = true;
			}
			previewCost = bestCost;

			engine.nextGeneration();
		} //while ! stop
		if (bestChromosome == null) {
			return Collections.emptyList(); 
		}
		
		ArrayList<Node> best = new ArrayList<Node>(Arrays.asList(bestChromosome.getCities()));
		orderList(best, assetLocations.get(0));
		lastDistance = (int)bestChromosome.getTotalDistance();
		lastTime = System.currentTimeMillis() - startTime;
		return best;
	}

	@Override
	public int getWaypointLimit() {
		return Short.MAX_VALUE;
	}

	@Override
	public long getLastTimeTaken() {
		return lastTime;
	}

	@Override
	public int getLastDistance() {
		return lastDistance;
	}

	@Override
	public short[][] getLastDistanceMatrix() {
		return distances;
	}

	@Override
	protected String getProgress() {
		return "Nonlinear";
	}

	
}