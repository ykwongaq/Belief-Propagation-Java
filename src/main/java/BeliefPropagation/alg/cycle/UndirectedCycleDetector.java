package BeliefPropagation.alg.cycle;

import BeliefPropagation.utils.Log;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;

/**
 * Detect the undirected graph contain cycle or not. This class is implemented because the {@link org.jgrapht.alg.cycle.CycleDetector}
 * provided by jgrapht only work for directed graph.
 * @param <V> Graph node type.
 * @param <E> Graph edge type.
 */
public class UndirectedCycleDetector<V, E> {

    /**
     * Target graph.
     */
    protected final Graph<V, E> graph;

    /**
     * Visited vertexes.
     */
    protected final Set<V> visitedVertexes;

    /**
     * Constructor.
     * @param graph Graph to check.
     * @throws NullPointerException if given {@code graph} is null.
     */
    public UndirectedCycleDetector(final Graph<V, E> graph) {
        Objects.requireNonNull(graph, Log.genLogMsg(this.getClass(), "Given graph cannot be null"));
        this.graph = graph;
        this.visitedVertexes = new HashSet<>();
    }

    /**
     * Detect is the graph contain cycle or not.
     * @return {@code True} if the graph contain cycle.
     */
    public boolean detectCycles() {
        /*
         * Run depth first search on every unvisited graph node. If the travel encounter the visited node, then
         * the graph contain cycle.
         */
        for (V vertex : this.graph.vertexSet()) {
            if (!this.visitedVertexes.contains(vertex) && this.detectCycles(vertex)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Run depth first search on given vertex. If the travel encounter visited node, then the graph contain cycle.
     * @param startingVertex Starting vertex.
     * @return {@code True} if the travel encounter visited node.
     */
    protected boolean detectCycles(final V startingVertex) {

        // searchCandidates store the node to be searched. It starts from the startingVertex.
        Stack<VertexParentPair<V>> searchCandidates = new Stack<>();
        searchCandidates.push(new VertexParentPair<>(startingVertex, null));

        while (!searchCandidates.isEmpty()) {
            VertexParentPair<V> vertexParentPair = searchCandidates.pop();
            final V vertex = vertexParentPair.vertex;
            final V parent = vertexParentPair.parent;

            this.visitedVertexes.add(vertex);

            for (V neighbor : Graphs.neighborListOf(this.graph, vertex)) {
                // When checking visited vertexes, we need to ignore the parent node (previous node)
                if (!visitedVertexes.contains(neighbor)) {
                    searchCandidates.push(new VertexParentPair<>(neighbor, vertex));
                } else if (!neighbor.equals(parent)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Helper container to store each node and it's parent node
     * @param vertex Node
     * @param parent Parent node
     * @param <T> Type
     */
    protected record VertexParentPair<T>(T vertex, T parent) {}
}
