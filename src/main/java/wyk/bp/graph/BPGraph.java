package wyk.bp.graph;

import org.jgrapht.graph.Pseudograph;

public class BPGraph<V extends BPGraphNode, E> extends Pseudograph<V, E> {

    private static final long serialVersionUID = -7574564204896552580L;

    public BPGraph(Class<? extends E> edgeClass) {
        super(edgeClass);
    }

    @Override
    public E addEdge(V sourceVertex, V targetVertex) {
        if (!this.isValidEdge(sourceVertex, targetVertex)) {
            throw new IllegalArgumentException("Edge cannot be build between 1. Vertex and Vertex and 2. Factor and Factor");
        }
        return super.addEdge(sourceVertex, targetVertex);
    }

    @Override
    public boolean addEdge(V sourceVertex, V targetVertex, E e) {
        if (!this.isValidEdge(sourceVertex, targetVertex)) {
            throw new IllegalArgumentException("Edge cannot be build between 1. Vertex and Vertex and 2. Factor and Factor");
        }
        return super.addEdge(sourceVertex, targetVertex, e);
    }

    protected boolean isValidEdge(final V sourceVertex, final V targetVertex) {
        return (sourceVertex instanceof Factor && targetVertex instanceof Vertex<?>) || (sourceVertex instanceof Vertex<?> && targetVertex instanceof Factor);
    }
}
