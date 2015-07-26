package uk.me.candle.eve.routing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.log4j.Logger;
import uk.me.candle.eve.graph.Graph;
import uk.me.candle.eve.graph.Node;
import uk.me.candle.eve.routing.cancel.CancelService;
import uk.me.candle.eve.routing.cancel.DefaultCancelService;

/**
 *
 * @author Candle
 */
public abstract class RoutingAlgorithm {
    private static final Logger logger = Logger.getLogger(RoutingAlgorithm.class);
    private static SortedSet<Class<? extends RoutingAlgorithm>> registered = new TreeSet<Class<? extends RoutingAlgorithm>>(new Comparator<Class<? extends RoutingAlgorithm>>() {
        @Override
        public int compare(Class<? extends RoutingAlgorithm> o1, Class<? extends RoutingAlgorithm> o2) {
            return o1.getName().compareTo(o2.getName());
        }
    });
    static {
        registerRoutingAlgorithm(BruteForce.class);
        registerRoutingAlgorithm(NearestNeighbour.class);
    }

    String basicDescription;
    String technicalDescription;
    String name;
    CancelService cancelService = new DefaultCancelService();

    public RoutingAlgorithm(String name, String basicDescription, String technicalDescription) {
        this.basicDescription = basicDescription;
        this.technicalDescription = technicalDescription;
        this.name = name;
    }

    public abstract List<Node> execute(Progress progress, Graph g, List<? extends Node> assetLocations);
    public abstract int getWaypointLimit();
    public abstract long getLastTimeTaken();
    public abstract int getLastDistance();
    public abstract short[][] getLastDistanceMatrix();

    /**
     * create a NxN
     * @param progress
     * @param g
     * @param waypoints
     * @return
     */
    protected short[][] reduce(Progress progress, Graph g, List<? extends Node> waypoints) {

        short[][] distances = new short[waypoints.size()][waypoints.size()]; // let's try to reduce the memory footprint by using shorts.
        for (int a = 0; a < waypoints.size(); ++a) {
            for (int b = a; b < waypoints.size(); ++b) {
                Node na = waypoints.get(a);
                Node nb = waypoints.get(b);
                distances[a][b] = (short)g.distanceBetween(na, nb);
                distances[b][a] = distances[a][b];
                progress.setValue(progress.getValue()+1);
            }
        }
        return distances;
    }

    public String getTechnicalDescription() {
        return technicalDescription;
    }

    public String getBasicDescription() {
        return basicDescription;
    }

    public String getName() {
        return name;
    }

    public void setCancelService(CancelService cancelService) {
        this.cancelService = cancelService;
    }

    public CancelService getCancelService() {
        return cancelService;
    }

    protected int getTotalFor(int[] ordering, short[][] distances) {
        int distance = 0;
        for (int i = 1; i < ordering.length; ++i) {
            distance += distances[ordering[i-1]][ordering[i]];
        }
        distance += distances[ordering[ordering.length-1]][ordering[0]];
        return distance;
    }

    public static List<RoutingAlgorithm> getRegisteredList() {
        List<RoutingAlgorithm> list = new ArrayList<RoutingAlgorithm>();
        for (Class<? extends RoutingAlgorithm> clz : registered) {
            try {
                list.add(clz.newInstance());
            } catch (InstantiationException ex) {
                logger.error("failed to create: " + clz.getName(), ex);
            } catch (IllegalAccessException ex) {
                logger.error("failed to create: " + clz.getName(), ex);
            }
        }
        return list;
    }

    public static void registerRoutingAlgorithm(Class<? extends RoutingAlgorithm> alg) {
        registered.add(alg);
    }
}
