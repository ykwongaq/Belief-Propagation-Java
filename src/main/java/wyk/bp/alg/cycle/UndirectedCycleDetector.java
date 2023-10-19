package wyk.bp.alg.cycle;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import wyk.bp.utils.Log;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;

public class UndirectedCycleDetector<V, E> {

    protected final Graph<V, E> graph;

    protected final Set<V> visitedVertexes;

    public UndirectedCycleDetector(final Graph<V, E> graph) {
        Objects.requireNonNull(graph, Log.genLogMsg(this.getClass(), "Given graph cannot be null"));
        this.graph = graph;
        this.visitedVertexes = new HashSet<>();
    }

    public boolean detectCycles() {
        for (V vertex : this.graph.vertexSet()) {
            if (!this.visitedVertexes.contains(vertex) && this.detectCycles(vertex)) {
                return true;
            }
        }
        return false;
    }

    protected boolean detectCycles(final V startingVertex) {
        Stack<VertexParentPair<V>> searchCandidates = new Stack<>();
        searchCandidates.push(new VertexParentPair<>(startingVertex, null));

        while (!searchCandidates.isEmpty()) {
            VertexParentPair<V> vertexParentPair = searchCandidates.pop();
            final V vertex = vertexParentPair.vertex;
            final V parent = vertexParentPair.parent;

            this.visitedVertexes.add(vertex);

            for (V neighbor : Graphs.neighborListOf(this.graph, vertex)) {
                if (!visitedVertexes.contains(neighbor)) {
                    searchCandidates.push(new VertexParentPair<>(neighbor, vertex));
                } else if (!neighbor.equals(parent)) {
                    return true;
                }
            }
        }

        return false;
    }

    protected record VertexParentPair<T>(T vertex, T parent) {}
}
