/*
 * Copyright 2015-2020, Niklas Kyster Rasmussen, Flaming Candle
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
import java.util.List;
import uk.me.candle.eve.graph.Graph;
import uk.me.candle.eve.graph.Node;


public class NearestNeighbourIteration<T extends Node> extends RoutingAlgorithm<T> {

    long lastTime = -1;
    int lastDistance = -1;
    short[][] distances;

    public NearestNeighbourIteration() {
        super("Nearest Neighbour Iteration",
            "Nearest Neighbour Iteration runs Nearest Neighbour with each node as starting possistion - this is not an effetive algorithm");
    }

    /**
     *
     * @param progress
     * @param g
     * @param assetLocations
     * @return
     */
    @Override
    public List<T> execute(Progress progress, Graph<T> g, List<T> assetLocations) {
        long startTime = System.currentTimeMillis();
        progress.setMaximum(getReduceMaxProgress(assetLocations) + (assetLocations.size() * assetLocations.size() *  assetLocations.size() / 2));    
        distances = reduce(progress, g, assetLocations);
        int bestTotal = Integer.MAX_VALUE;
        List<T> bestRoute = new ArrayList<>();
        for (int a = 0; a < assetLocations.size(); a++) {
            if (getCancelService().isCancelled()) {
                return Collections.emptyList();
            }
            List<T> route = new ArrayList<>();
            route.add(assetLocations.get(a));
            int total = 0;
            while (route.size() < assetLocations.size()) {
                if (getCancelService().isCancelled()) {
                    return Collections.emptyList();
                }
                T lastRouteItem = route.get(route.size()-1);
                int locationsIdx = assetLocations.indexOf(lastRouteItem);
                int min = Integer.MAX_VALUE;
                T next = null;
                for (int i = 0; i < distances[locationsIdx].length; ++i) {
                    if (getCancelService().isCancelled()) {
                        return Collections.emptyList();
                    }
                    if (route.contains(assetLocations.get(i))) continue;
                    if (distances[locationsIdx][i] < min) {
                        min = distances[locationsIdx][i];
                        next = assetLocations.get(i);
                    }
                    progress.setValue(progress.getValue()+1);
                }
                route.add(next);
                total = total + min;
            }
            //Add end to start
            int end = assetLocations.indexOf(route.get(route.size()-1));
            int start = assetLocations.indexOf(route.get(0));
            total = total + distances[end][start];
            if (bestTotal > total) {
                bestRoute = route;
                bestTotal = total;
            }
        }
        orderList(bestRoute, assetLocations.get(0));
        lastDistance = bestTotal;
        lastTime = System.currentTimeMillis()-startTime;
        return bestRoute;
    }

    @Override
    public int getWaypointLimit() {
        return 400;
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
    protected String getSpeed() {
        return "Slow";
    }

    @Override
    protected String getRoute() {
        return "Poor";
    }

    @Override
    protected String getProgress() {
        return "Linear";
    }

}
