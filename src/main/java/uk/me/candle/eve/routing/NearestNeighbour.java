package uk.me.candle.eve.routing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import uk.me.candle.eve.graph.Graph;
import uk.me.candle.eve.graph.Node;

/**
 *
 * @author Candle
 */
public class NearestNeighbour extends RoutingAlgorithm {
    long lastTime = -1;
    int lastDistance = -1;
    short[][] distances;

    public NearestNeighbour() {
        super("Nearest Neighbour"
            , "Nearest Neighbor:\n" +
            "Speed: Faster then Brute Force\n" +
            "Optimal Route: Not Guaranteed (But, most of the time)\n" +
            "Max Systems: unlimited.\n"
            , "This algorithm starts with the first waypoint and picks" +
            " the nearest neighbour to it each time. This should be significantly" +
            " faster then the Brute Force algorithm, and hence allows an unlimited number" +
            " of waypoints, however, it is not guaranteed to give an optimal route.");
    }

    public short[][] getLastDistanceMatrix() {
      return distances;
    }

    @Override
    public List<Node> execute(Progress progress, Graph g, List<? extends Node> assetLocations) {
        long startTime = System.currentTimeMillis();
        progress.setMaximum(assetLocations.size() * (assetLocations.size() + 1));
        distances = reduce(progress, g, assetLocations);

        List<Node> route = new ArrayList<Node>();
        route.add(assetLocations.get(0));

        while (route.size() < assetLocations.size()) {
            if (getCancelService().isCancelled()) {
                return Collections.emptyList();
            }
            Node lastRouteItem = route.get(route.size()-1);
            int locationsIdx = assetLocations.indexOf(lastRouteItem);
            int min = Integer.MAX_VALUE;
            Node next = null;
            for (int i = 0; i < distances[locationsIdx].length; ++i) {
                if (route.contains(assetLocations.get(i))) continue;
                if (distances[locationsIdx][i] < min) {
                    min = distances[locationsIdx][i];
                    next = assetLocations.get(i);
                }
            }
            route.add(next);
        }
        int[] ordering = new int[route.size()];
        for (int i = 0; i < route.size(); ++i) {
          ordering[i] = assetLocations.indexOf(route.get(i));
        }
        lastDistance = getTotalFor(ordering, distances);
        lastTime = System.currentTimeMillis()-startTime;
        return route;
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
    public int getWaypointLimit() {
        return Integer.MAX_VALUE;
    }

}
