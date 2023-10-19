package wyk.bp.alg.cycle;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UndirectedCycleDetectorTest {

    @Test
    void testDetectCycles1() {
        Graph<String, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addVertex("D");
        graph.addVertex("E");
        graph.addVertex("F");
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.addEdge("C", "A");
        graph.addEdge("D", "E");
        graph.addEdge("F", "C");

        UndirectedCycleDetector<String, DefaultEdge> cycleDetector = new UndirectedCycleDetector<>(graph);
        assertTrue(cycleDetector.detectCycles());
    }

    @Test
    void testDetectCycles2() {
        Graph<String, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addVertex("D");
        graph.addVertex("E");
        graph.addVertex("F");
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.addEdge("D", "E");
        graph.addEdge("F", "C");

        UndirectedCycleDetector<String, DefaultEdge> cycleDetector = new UndirectedCycleDetector<>(graph);
        assertFalse(cycleDetector.detectCycles());
    }

    @Test
    void testDetectCycles3() {
        Graph<String, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addVertex("D");
        graph.addVertex("E");
        graph.addVertex("F");
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.addEdge("D", "E");
        graph.addEdge("F", "C");
        graph.addEdge("A", "D");
        graph.addEdge("F", "E");

        UndirectedCycleDetector<String, DefaultEdge> cycleDetector = new UndirectedCycleDetector<>(graph);
        assertTrue(cycleDetector.detectCycles());
    }
}