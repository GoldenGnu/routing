package uk.me.candle.eve.routing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
	private static List<Class<? extends RoutingAlgorithm>> registered = new ArrayList<Class<? extends RoutingAlgorithm>>();
	/*
    private static SortedSet<Class<? extends RoutingAlgorithm>> registered = new TreeSet<Class<? extends RoutingAlgorithm>>(new Comparator<Class<? extends RoutingAlgorithm>>() {
        @Override
        public int compare(Class<? extends RoutingAlgorithm> o1, Class<? extends RoutingAlgorithm> o2) {
            return o1.getName().compareTo(o2.getName());
        }
    });
	*/
    static {
        registerRoutingAlgorithm(BruteForce.class);
		//registerRoutingAlgorithm(CrossoverHibrid2opt.class);
		//registerRoutingAlgorithm(SimpleUnisexMutator.class);
		registerRoutingAlgorithm(SimpleUnisexMutatorHibrid2Opt.class);
		registerRoutingAlgorithm(Crossover.class);
		//registerRoutingAlgorithm(NearestNeighbourIteration.class);
		registerRoutingAlgorithm(NearestNeighbour.class);
    }

    private String basicDescription = null;
    private String technicalDescription;
    private String name;
    private CancelService cancelService = new DefaultCancelService();
	private static short[][] distancesCache = null;
	private static List<? extends Node> waypointsCache = new ArrayList<Node>();
	private static boolean cache = false;

    public RoutingAlgorithm(String name, String technicalDescription) {
        this.technicalDescription = technicalDescription;
        this.name = name;
    }

    public abstract List<Node> execute(Progress progress, Graph g, List<? extends Node> assetLocations);
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
			waypointsCache = new ArrayList<Node>();
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
    protected short[][] reduce(Progress progress, Graph g, List<? extends Node> waypoints) {
		if (reduceWaypoints(waypoints)) {
			waypointsCache = new ArrayList<Node>(waypoints);
			distancesCache = new short[waypoints.size()][waypoints.size()]; // let's try to reduce the memory footprint by using shorts.
			for (int a = 0; a < waypoints.size(); ++a) {
				for (int b = a; b < waypoints.size(); ++b) {
					if (getCancelService().isCancelled()) {
						return new short[0][0];
					}
					Node na = waypoints.get(a);
					Node nb = waypoints.get(b);
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

	protected void orderList(List<Node> best, Node first) {
		int end = best.indexOf(first);
		Collections.rotate(best, best.size() - end);
	}

	protected void firstToLast(List<Node> list) {
		if (list.isEmpty()) {
			return;
		}
		Node node = list.get(0);
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
