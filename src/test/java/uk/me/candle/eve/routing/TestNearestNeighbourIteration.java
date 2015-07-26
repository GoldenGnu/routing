
package uk.me.candle.eve.routing;

import java.util.Arrays;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import org.junit.Test;
import uk.me.candle.eve.graph.Edge;
import uk.me.candle.eve.graph.Graph;
import uk.me.candle.eve.graph.Node;
import uk.me.candle.eve.graph.distances.Jumps;

/**
 *
 * @author Niklas
 */
public class TestNearestNeighbourIteration extends NearestNeighbourIteration {
	@Test
    public void testExecute() {
        Graph gr = new Graph(new Jumps());
        Node a = new NodeString("a");
        Node b = new NodeString("b");
        Node c = new NodeString("c");
        Node d = new NodeString("d");
        Node e = new NodeString("e");
        Node f = new NodeString("f");
        Node g = new NodeString("g");
        Node h = new NodeString("h");
        gr.addEdge(new Edge(a, b)); gr.addEdge(new Edge(b, a));
        gr.addEdge(new Edge(a, c)); gr.addEdge(new Edge(c, a));
        gr.addEdge(new Edge(a, d)); gr.addEdge(new Edge(d, a));
        gr.addEdge(new Edge(e, d)); gr.addEdge(new Edge(d, e));
        gr.addEdge(new Edge(d, c)); gr.addEdge(new Edge(c, d));
        gr.addEdge(new Edge(b, c)); gr.addEdge(new Edge(c, b));
        gr.addEdge(new Edge(a, f)); gr.addEdge(new Edge(f, a));
        gr.addEdge(new Edge(f, g)); gr.addEdge(new Edge(g, f));
        gr.addEdge(new Edge(g, h)); gr.addEdge(new Edge(h, g));
        gr.addEdge(new Edge(h, c)); gr.addEdge(new Edge(c, h));
        List<Node> waypoints = Arrays.asList(e, f, h, a);
        List<Node> route = execute(new EmptyProgress(), gr, waypoints);
        Node[] expected = new Node[]{e, a, f, h};
        for (int i = 0; i < route.size(); ++i) {
            assertEquals("index: " + i, expected[i], route.get(i));
        }
		assertEquals(8, getLastDistance());
		
		for (int i = 0; i < route.size(); i++) {
			Node last = route.get(route.size() - 1); //Last index
			int total = 0;
			for (Node node : route) {
				int distanceBetween = gr.distanceBetween(last, node);
				total = total + distanceBetween;
				last = node;
			}
			assertEquals(8, total);
			firstToLast(route);
		}
    }
}
