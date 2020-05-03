package uk.me.candle.eve.routing;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import uk.me.candle.eve.graph.Edge;
import uk.me.candle.eve.graph.Graph;
import uk.me.candle.eve.graph.Node;
import static junit.framework.Assert.*;
import uk.me.candle.eve.graph.distances.Jumps;

/**
 *
 * @author Candle
 */
public class TestBruteForce extends BruteForce<Node> {

    @Test
    public void testFactorial() {
        assertEquals(6, factorial(3));
        assertEquals(24, factorial(4));
        assertEquals(120, factorial(5));
    }

    @Test
    public void testChangeOrdering() {
        int[] ordering = new int[]{0,1,2};
        changeOrdering(ordering, ordering, 1);
        int[] expected = new int[]{0,2,1};
        for (int i = 0; i < ordering.length; ++i) {
            assertEquals("index: " + i, expected[i] , ordering[i]);
        }
    }

    @Test
    public void testChangeOrdering2() {
        int[] ordering = new int[]{0,1,2};
        int[] initial = new int[]{0,1,2};
        int[][] expected = new int[][]{
             {0,1,2}
            ,{0,2,1}
            ,{1,0,2}
            ,{1,2,0}
            ,{2,0,1}
            ,{2,1,0}
        };
        for (int i = 0; i < 6; ++i) {
            changeOrdering(ordering, initial, i);
            for (int j = 0; j < ordering.length; ++j) {
                assertEquals("index: " + i + " / " + j, expected[i][j] , ordering[j]);
            }
        }
    }

    @Test
    public void testChangeOrderingOffset() {
        int[] ordering = new int[]{0,1,2,3,4,5};
        int[] initial = new int[]{0,1,2,3,4,5};
        int[][] expected = new int[][]{
             {0,1,2,3,4,5}
            ,{0,1,2,4,3,5}
            ,{0,1,3,2,4,5}
            ,{0,1,3,4,2,5}
            ,{0,1,4,2,3,5}
            ,{0,1,4,3,2,5}
        };
        for (int i = 0; i < 6; ++i) {
            changeOrdering(ordering, initial, i, 2, 3);
            for (int j = 0; j < ordering.length; ++j) {
                assertEquals("index: " + i + " / " + j, expected[i][j] , ordering[j]);
            }
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testChangeOrderingOutOfBounds1() {
        int[] ordering = new int[]{0,1,2,3,4,5};
        int[] initial = new int[]{0,1,2,3,4,5};
        changeOrdering(ordering, initial, 3, 10, 3);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testChangeOrderingOutOfBounds2() {
        int[] ordering = new int[]{0,1,2,3,4,5};
        int[] initial = new int[]{0,1,2,3,4,5};
        changeOrdering(ordering, initial, 3, 1, 10);
    }

    @Test
    public void testExecute1() {
        Graph<Node> g = new Graph<>(new Jumps<>());
        Node a = new Node("a");
        Node b = new Node("b");
        List<Node> waypoints = Arrays.asList(a, b);
        g.addEdge(new Edge<>(a, b));
        g.addEdge(new Edge<>(b, a));
        List<Node> route = execute(new EmptyProgress(), g, waypoints);
        Node[] expected = new Node[]{a, b};
        for (int i = 0; i < expected.length; ++i) {
            assertEquals("index: " + i, expected[i], route.get(i));
        }
    }

    @Test
    public void testExecute2() {
        Graph<Node> gr = new Graph<>(new Jumps<>());
        Node a = new Node("a");
        Node b = new Node("b");
        Node c = new Node("c");
        Node d = new Node("d");
        Node e = new Node("e");
        Node f = new Node("f");
        Node g = new Node("g");
        Node h = new Node("h");
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
        Node[] expected = new Node[]{e, h, f, a};
        for (int i = 0; i < expected.length; ++i) {
            assertEquals("index: " + i, expected[i], route.get(i));
        }
        assertEquals(8, getLastDistance());
    }
}
