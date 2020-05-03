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
package uk.me.candle.eve.routing.engines;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import uk.me.candle.eve.graph.Node;


public class Manager {
    // Holds our nodes
    private static final Map<Node, Integer> INDEX_MAP = new HashMap<>();
    private static short[][] distances;
    private static Random random;

    public static void initialize(List<? extends Node> nodes, short[][] distances, long seed) {
        Manager.distances = distances.clone();
        INDEX_MAP.clear();
        for (int i = 0; i < nodes.size(); i++) {
            INDEX_MAP.put(nodes.get(i), i);
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
            return distances[INDEX_MAP.get(from)][INDEX_MAP.get(to)];
        }
    }

    public static short distance(Node from, Node to) {
        return cost(from, to);
    }
}
