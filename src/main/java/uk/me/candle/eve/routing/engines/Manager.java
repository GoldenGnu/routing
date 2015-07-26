/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.me.candle.eve.routing.engines;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import uk.me.candle.eve.graph.Node;

/**
 *
 * @author Niklas
 */
public class Manager {
	// Holds our nodes
    private static List<Node> destinationNodes = new ArrayList<Node>();
	private static short[][] distances;
	private static Map<Node, Integer> indexMap = new HashMap<Node, Integer>();
	private static Random random;

    public static void initialize(List<Node> nodes, short[][] distances, long seed) {
		destinationNodes.clear();
        destinationNodes.addAll(nodes);
		Manager.distances = distances;
		for (int i = 0; i < nodes.size(); i++) {
			indexMap.put(nodes.get(i), i);
		}
		Manager.random = new Random(seed);
    }

	public static int getRandom(int n) {
		return random.nextInt(n);
	}
    
	public static short cost(Node from, Node to) {
		if (from.getName().equals(to.getName())) {
			return 0;
		} else {
			return distances[indexMap.get(from)][indexMap.get(to)];
		}
	}

	public static short distance(Node from, Node to) {
		return cost(from, to);
	}
}
