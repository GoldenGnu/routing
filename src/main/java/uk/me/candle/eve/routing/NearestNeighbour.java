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

/**
 *
 * @author Candle
 * @param <T>
 */
public class NearestNeighbour<T extends Node> extends RoutingAlgorithm<T> {
    long lastTime = -1;
    int lastDistance = -1;
    short[][] distances;

    public NearestNeighbour() {
        super("Nearest Neighbour"
            , "This algorithm starts with the first waypoint and picks" +
            " the nearest neighbour to it each time. This should be significantly" +
            " faster then the Brute Force algorithm, and hence allows an unlimited number" +
            " of waypoints, however, it is not guaranteed to give an optimal route.");
    }

    @Override
    public List<T> execute(Progress progress, Graph<T> g, List<T> assetLocations) {
        long startTime = System.currentTimeMillis();
        lastTime = -1;
        lastDistance = -1;
        progress.setMaximum(getReduceMaxProgress(assetLocations) + (assetLocations.size() * assetLocations.size() / 2));    
        distances = reduce(progress, g, assetLocations);

        List<T> route = new ArrayList<>();
        route.add(assetLocations.get(0));

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
                if (route.contains(assetLocations.get(i))) continue;
                if (distances[locationsIdx][i] < min) {
                    min = distances[locationsIdx][i];
                    next = assetLocations.get(i);
                }
                progress.setValue(progress.getValue()+1);
            }
            total = total + min;
            route.add(next);
        }
        //Add end to start
        int end = assetLocations.indexOf(route.get(route.size()-1));
        int start = assetLocations.indexOf(route.get(0));
        total = total + distances[end][start];
        lastDistance = total;
        lastTime = System.currentTimeMillis()-startTime;
        return route;
    }

    @Override
    public int getWaypointLimit() {
        return Integer.MAX_VALUE;
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
        return "Fastest";
    }

    @Override
    protected String getRoute() {
        return "Worst";
    }

    @Override
    protected String getProgress() {
        return "Linear";
    }

    
}
