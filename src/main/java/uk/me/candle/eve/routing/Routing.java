package uk.me.candle.eve.routing;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import uk.me.candle.eve.graph.Edge;
import uk.me.candle.eve.graph.Graph;
import uk.me.candle.eve.graph.Node;
import uk.me.candle.eve.graph.distances.Jumps;

/**
 *
 */
public class Routing {

    Random rand = new Random(2);

    public static void main(String[] args) {
        Routing r = new Routing();
        r.createGraph2();

        List<Node> assetNodes = r.getAssetNodes();

        for (Node a : assetNodes) {
            for (Node b : assetNodes) {
                if (a == b) continue;
                r.printRouteBetween(a, b);
            }
        }
    }

    private void printRouteBetween(Node a, Node b) {
        List<Node> route = graph.routeBetween(a, b);
        System.out.println("from: " + a.getName() + " to: " + b.getName());
        for (Node n : route) {
            System.out.print(n.getName() + " => ");
        }
        System.out.println();
    }

    Graph graph;

    public List<Node> getAssetNodes() {
        List<Node> nodes = new ArrayList<Node>();
        for (int i = 0; i < 5; ++i) {
            nodes.add(new ArrayList<Node>(graph.getNodes()).get(rand.nextInt(graph.getNodes().size())));
        }
        return nodes;
    }


    public Graph createGraph2() {
        graph = new Graph(new Jumps());

        List<Node> currentNodes = new ArrayList<Node>();
        {
            Node first = new Node("node " + 0);
            currentNodes.add(first);
            graph.addNode(first);
        }

        for (int i = 1; i < 40; ++i) {
            Node n = new Node("node " + i);
            int attach = rand.nextInt(currentNodes.size());
            currentNodes.add(n);
            graph.addEdge(new Edge(n, currentNodes.get(attach)));
            graph.addEdge(new Edge(currentNodes.get(attach), n));
        }

        for (int i = 0; i < 10; ++i) {
            int first = rand.nextInt(currentNodes.size());
            int second = rand.nextInt(currentNodes.size()-1);
            if (second >= first) second++;
            graph.addEdge(new Edge(currentNodes.get(first), currentNodes.get(second)));
            graph.addEdge(new Edge(currentNodes.get(second), currentNodes.get(first)));
        }

        return graph;
    }

    public Graph createGraph() {
        graph = new Graph(new Jumps());

        Node[] nodes = new Node[20];
        for (int i = 0; i < nodes.length; ++i) {
            nodes[i] = new Node("node " + i);
            graph.addNode(nodes[i]);
        }

        for (int i = 0; i < 20; ++i) {
            int first = rand.nextInt(nodes.length);
            int second = rand.nextInt(nodes.length-1);
            if (second >= first) second++;
            graph.addEdge(new Edge(nodes[first], nodes[second]));
            graph.addEdge(new Edge(nodes[second], nodes[first]));
        }
        while (graph.isDisconnected()) {
            for (int i = 0; i < nodes.length; ++i) {
                Node n = nodes[i];
                if (n.getIncommingEdges().size() == 0 && n.getOutgoingEdges().size() == 0) {
                    int second = rand.nextInt(nodes.length);
                    if (second >= i) ++second;
                    graph.addEdge(new Edge(n, nodes[second]));
                    graph.addEdge(new Edge(nodes[second], n));
                }
            }
        }
        return graph;
    }
}
