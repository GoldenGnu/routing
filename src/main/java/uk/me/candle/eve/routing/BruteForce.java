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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import uk.me.candle.eve.graph.Graph;
import uk.me.candle.eve.graph.Node;

/**
 *
 * @author Candle
 */
public class BruteForce extends RoutingAlgorithm {
	private final boolean loop;
    long lastTime = -1;
    int lastDistance = -1;
    short[][] distances;

	public BruteForce() {
		this(true);
	}

    public BruteForce(boolean loop) {
        super("Brute Force"
            , "This algorithm is the only" +
            " way to get the optimal solution, however," +
            " it is also by far the slowest, with small datasets (20 waypoints or so)" +
            " theoretically taking several years of calculation." +
            "\n\nThe limit of 13 waypoints is based on the limit of" +
            "12 factorial; which is the biggest factorial that can fit" +
            " into a Java integer (4 bytes). There is an optimisation " +
            " which allows us to ignore one of the waypoints, hence the 12" +
            " and not 13." +
            "");
		this.loop = loop;
    }

    @Override
    public int getWaypointLimit() {
        return 13;
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
    public List<Node> execute(Progress progress, Graph g, List<? extends Node> waypoints) {
        long startTime = System.currentTimeMillis();
		lastTime = -1;
		lastDistance = -1;
        if (waypoints.size() < 2) throw new IllegalArgumentException("The size of the list of waypoints must be greater or equal to two; it's current size is " + waypoints.size());
        int max = factorial(waypoints.size()-1);
        progress.setMaximum((waypoints.size() * waypoints.size()) + max + waypoints.size());

        // reduce the graph to a fully connected graph, with distance weights between the nodes - so I need a NxN array.
        distances = reduce(progress, g, waypoints);

        int[] ordering = new int[waypoints.size()];
        int[] initialOrdering = new int[waypoints.size()];
        int[] bestOrdering = new int[waypoints.size()];
        for (int i = 0; i < ordering.length; ++i) {
            ordering[i] = i;
            initialOrdering[i] = i;
            bestOrdering[i] = i;
        }
        int bestTotal = Integer.MAX_VALUE;
        int distance = -1;
        for (int i = 0; i < max; ++i) {
            if (getCancelService().isCancelled()) {
                return Collections.emptyList();
            }
            changeOrdering(ordering, initialOrdering, i, 1, ordering.length-1);
            distance = getTotalFor(ordering, distances);
            if (distance < bestTotal) {
                for (int o  = 0; o < ordering.length; ++o) {
                    bestOrdering[o] = ordering[o];
                }
                bestTotal = distance;
            }
            progress.setValue(progress.getValue()+1);
        }


        List<Node> finalOrdering = new ArrayList<Node>();
        for (int i = 0; i < bestOrdering.length; ++i) {
            finalOrdering.add(waypoints.get(bestOrdering[i]));
            progress.setValue(progress.getValue()+1);
        }
        lastDistance = bestTotal;
        lastTime = System.currentTimeMillis()-startTime;
        return finalOrdering;
    }

    protected int factorial(int n) {
        int s = 1;
        for (int i = 2; i <= n; ++i) {
            s *= i;
        }
        return s;
    }

    /**
     * see http://projecteuler.net/index.php?section=problems&id=24 for this algorithm.
     * @param ordering this array is mutated.
     * @param initial
     * @param required
     */
    protected int[] changeOrdering(int[] ordering, int[] initial, int required) {
      return changeOrdering(ordering, initial, required, 0, ordering.length);
    }

	protected int getTotalFor(int[] ordering, short[][] distances) {
        int distance = 0;
        for (int i = 1; i < ordering.length; ++i) {
            distance += distances[ordering[i-1]][ordering[i]];
        }
		//TODO - Loop logic
		if (loop) {
			distance += distances[ordering[ordering.length-1]][ordering[0]];
		}
        return distance;
    }

    /**
     * see http://projecteuler.net/index.php?section=problems&id=24 for this algorithm.
     * @param ordering the array in which to put the new ordering (performance - object creation). This array needs to me the same length as the <pre>initial</pre> parameter.
     * @param initial the initial ordering of the elements. This array needs to me the same length as the <pre>ordering</pre> parameter.
     * @param required the required indexed ordering.
     * @param offset the offset in the initial array to start shuffling.
     * @param length the length in the initial array to shuffle.
     * @return the new ordering - the return value is == to the input <pre>ordering</pre> array.
     * @throws IllegalArgumentException if the two input arrays do not have equal length, or if the offset and/or length would cause an ArrayOutOfBoundsException
     */
    protected int[] changeOrdering(int[] ordering, int[] initial, int required, int offset, int length) {
        if (ordering.length != initial.length) throw new IllegalArgumentException("The lengths of the input arrays were not equal: " + ordering.length + " and " + initial.length);
        if (offset >= ordering.length) throw new IllegalArgumentException("The offset was too big: " + "offset = " + offset + " array length = " + initial.length);
        if (offset+length > ordering.length) throw new IllegalArgumentException("The length was too big: " + "offset = " + offset + " length = " + length + " array length = " + initial.length);
        long req = required;

        // define two lists, one that collects the result and one tha contains the remaining elements.
        List<Integer> elements = new LinkedList<Integer>(); // remove *should* be quicker with a linked list
        List<Integer> result = new LinkedList<Integer>(); // additions to the end of the list *should* be quicker, removals from the start of the lost *should* be quicker
        for (int i = offset; i < offset+length; ++i) {
            elements.add(initial[i]);
        }

        while(elements.size() > 0) {
          long fact = factorial(elements.size()-1);
          int idx = (int) (req/fact); // integer maths required.
          req = req - fact*idx;
          result.add(elements.remove(idx));
        }

        // rebuild the ordering array.
        for (int i = 0; i < offset; ++i) {
            ordering[i] = initial[i];
        }
        for (int i = offset; i < offset+length; ++i) {
            ordering[i] = result.remove(0);
        }
        for (int i = offset+length; i < initial.length; ++i) {
            ordering[i] = initial[i];
        }
        return ordering;
    }

	@Override
	protected String getSpeed() {
		return "Slowest";
	}

	@Override
	protected String getRoute() {
		return "Always optimal route";
	}

	@Override
	protected String getProgress() {
		return "Linear";
	}

	
}
