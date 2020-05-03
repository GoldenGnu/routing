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
import uk.me.candle.eve.routing.cancel.CancelService;
import uk.me.candle.eve.routing.cancel.DefaultCancelService;

/**
 *
 * @author Candle
 * @param <T>
 */
public abstract class RoutingAlgorithm<T extends Node> {

    private static short[][] distancesCache = null;
    private static List<? extends Node> waypointsCache = new ArrayList<Node>();
    private static boolean cache = false;

    private final String technicalDescription;
    private final String name;
    protected final boolean loop = true; //Does not work correctly with loop off

    private String basicDescription = null;
    
    private CancelService cancelService = new DefaultCancelService();
    
    
    public RoutingAlgorithm(String name, String technicalDescription) {
        this.technicalDescription = technicalDescription;
        this.name = name;
    }

    public abstract List<T> execute(Progress progress, Graph<T> g, List<T> assetLocations);
    public abstract int getWaypointLimit();
    protected abstract String getSpeed();
    protected abstract String getRoute();
    protected abstract String getProgress();
    public abstract long getLastTimeTaken();
    public abstract int getLastDistance();
    public abstract short[][] getLastDistanceMatrix();

    public static void setCache(boolean cache) {
        if (!cache) { //clear cache
            distancesCache = null;
            waypointsCache = new ArrayList<>();
        }
        RoutingAlgorithm.cache = cache;
    }

    protected int getReduceMaxProgress(List<? extends Node> waypoints) {
        if (reduceWaypoints(waypoints)) {
            return waypoints.size() / 2 * waypoints.size();
        } else {
            return 0;
        }
    }

    /**
     * create a NxN
     * @param progress
     * @param g
     * @param waypoints
     * @return
     */
    protected short[][] reduce(Progress progress, Graph<T> g, List<T> waypoints) {
        if (reduceWaypoints(waypoints)) {
            waypointsCache = new ArrayList<>(waypoints);
            distancesCache = new short[waypoints.size()][waypoints.size()]; // let's try to reduce the memory footprint by using shorts.
            for (int a = 0; a < waypoints.size(); a++) {
                T na = waypoints.get(a);
                for (int b = a; b < waypoints.size(); b++) {
                    if (getCancelService().isCancelled()) {
                        return new short[0][0];
                    }
                    T nb = waypoints.get(b);
                    distancesCache[a][b] = (short)g.distanceBetween(na, nb);
                    distancesCache[b][a] = distancesCache[a][b];
                    progress.setValue(progress.getValue()+1);
                }
            }
        }
        return distancesCache;
    }

    private boolean reduceWaypoints(List<? extends Node> waypoints) {
        return !waypoints.equals(waypointsCache) || distancesCache == null || !cache;
    }

    protected void orderList(List<T> best, T first) {
        int end = best.indexOf(first);
        Collections.rotate(best, best.size() - end);
    }

    protected void firstToLast(List<T> list) {
        if (list.isEmpty()) {
            return;
        }
        T node = list.get(0);
        list.remove(0);
        list.add(node);
    }

    public String getTechnicalDescription() {
        return technicalDescription;
    }

    public String getBasicDescription() {
        if (basicDescription == null) {
            StringBuilder builder = new StringBuilder();
            //builder.append(name);
            //builder.append(":");
            //builder.append("\n");
            builder.append("Speed: ");
            builder.append(getSpeed());
            builder.append("\n");
            builder.append("Route: ");
            builder.append(getRoute());
            builder.append("\n");
            builder.append("Progress: ");
            builder.append(getProgress());
            builder.append("\n");
            builder.append("Max Waypoints: ");
            if (getWaypointLimit() == Integer.MAX_VALUE) {
                builder.append("Unlimited");
            } else {
                builder.append(getWaypointLimit());
            }
            this.basicDescription = builder.toString();
        }
        return basicDescription;
    }

    public String getName() {
        return name;
    }

    public void setCancelService(CancelService cancelService) {
        this.cancelService = cancelService;
    }

    public void resetCancelService() {
        this.cancelService = new DefaultCancelService();
    }

    public CancelService getCancelService() {
        return cancelService;
    }
}
