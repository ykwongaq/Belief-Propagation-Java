package wyk.bp.graph;

import org.jgrapht.graph.Pseudograph;

public class FactorGraph<E> extends Pseudograph<FactorGraphNode, E> {
    private static final String SAME_TYPE_CONNECTION_ERROR_MSG = "Cannot connect two vertex with same type 1. Vertex and Vertex or 2. Factor and Factor";
    private static final long serialVersionUID = -7574564204896552580L;
    public FactorGraph(Class<? extends E> edgeClass) {
        super(edgeClass);
    }
    @Override
    public E addEdge(FactorGraphNode sourceVertex, FactorGraphNode targetVertex) {
        if (!this.isValidEdge(sourceVertex, targetVertex)) {
            throw new IllegalArgumentException(FactorGraph.SAME_TYPE_CONNECTION_ERROR_MSG);
        }
        return super.addEdge(sourceVertex, targetVertex);
    }

    @Override
    public boolean addEdge(FactorGraphNode sourceVertex, FactorGraphNode targetVertex, E e) {
        if (!this.isValidEdge(sourceVertex, targetVertex)) {
            throw new IllegalArgumentException(FactorGraph.SAME_TYPE_CONNECTION_ERROR_MSG);
        }
        return super.addEdge(sourceVertex, targetVertex, e);
    }

    public boolean addVariable(final Variable<?> variable) {
        return super.addVertex(variable);
    }

    public boolean addFactor(final Factor factor) {
        return super.addVertex(factor);
    }

    protected boolean isValidEdge(final FactorGraphNode sourceVertex, FactorGraphNode targetVertex) {
        return (sourceVertex instanceof Factor && targetVertex instanceof Variable<?>) || (sourceVertex instanceof Variable<?> && targetVertex instanceof Factor);
    }
}
