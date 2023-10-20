package wyk.bp.graph;

import org.jgrapht.Graphs;
import org.jgrapht.graph.Pseudograph;
import wyk.bp.utils.Log;

import java.util.HashSet;
import java.util.List;
import java.io.Serial;
import java.util.Objects;
import java.util.Set;

public class FactorGraph<E> extends Pseudograph<FactorGraphNode, E> {
    private static final String SAME_TYPE_CONNECTION_ERROR_MSG = "Cannot connect two vertex with same type 1. Vertex and Vertex or 2. Factor and Factor";
    @Serial
    private static final long serialVersionUID = -7574564204896552580L;

    public FactorGraph(Class<? extends E> edgeClass) {
        super(edgeClass);
    }

    public E addEdge(final Factor factor, final Variable<?> variable) {
        if (!factor.contains(variable)) {
            throw new IllegalArgumentException(Log.genLogMsg(this.getClass(), "Trying to add edge between unrelated variables. Factor variables: " + factor.getVariables() + " Variable: " + variable));
        }
        return super.addEdge(factor, variable);
    }


    public E addEdge(final Variable<?> variable, final Factor factor) {
        return this.addEdge(factor, variable);
    }

    public boolean addEdge(final Factor factor, final Variable<?> variable, final E edge) {
        if (!factor.contains(variable)) {
            throw new IllegalArgumentException(Log.genLogMsg(this.getClass(), "Trying to add edge between unrelated variables. Factor variables: " + factor.getVariables() + " Variable: " + variable));
        }
        return super.addEdge(factor, variable, edge);
    }

    public boolean addEdge(final Variable<?> variable, final Factor factor, final E edge) {
        return this.addEdge(factor, variable, edge);
    }

    @Override
    public E addEdge(FactorGraphNode sourceVertex, FactorGraphNode targetVertex) {
        if (!this.isValidEdge(sourceVertex, targetVertex)) {
            throw new IllegalArgumentException(FactorGraph.SAME_TYPE_CONNECTION_ERROR_MSG);
        }
        Factor factor = this.castFactor(sourceVertex, targetVertex);
        Variable<?> variable = this.castVariable(sourceVertex, targetVertex);
        return this.addEdge(factor, variable);
    }
    @Override
    public boolean addEdge(FactorGraphNode sourceVertex, FactorGraphNode targetVertex, E e) {
        if (!this.isValidEdge(sourceVertex, targetVertex)) {
            throw new IllegalArgumentException(FactorGraph.SAME_TYPE_CONNECTION_ERROR_MSG);
        }
        Factor factor = this.castFactor(sourceVertex, targetVertex);
        Variable<?> variable = this.castVariable(sourceVertex, targetVertex);
        return this.addEdge(factor, variable, e);
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

    protected Factor castFactor(final FactorGraphNode node1, final FactorGraphNode node2) {
        return node1 instanceof Factor ? (Factor) node1 : (Factor) node2;
    }

    protected Variable<?> castVariable(final FactorGraphNode node1, final FactorGraphNode node2) {
        return node1 instanceof Variable<?> ? (Variable<?>) node1 : (Variable<?>) node2;
    }

    public boolean isValid() {
        List<Factor> factors = this.vertexSet().stream().filter(vertex -> vertex instanceof Factor).map(vertex -> (Factor) vertex).toList();
        if (factors.isEmpty()) {
            return false;
        }
        for (Factor factor : factors) {
            for (Variable<?> variable : factor.getVariables()) {
                if (!this.containsEdge(factor, variable)) {
                    return false;
                }
            }
        }
        return true;
    }

    public List<? extends Variable<?>> getIncomingVariables(final Factor factor, final Variable<?> parentVariable) {
        return Graphs.neighborListOf(this, factor).stream()
                .filter(neighbor -> !neighbor.equals(parentVariable))
                .map(neighbor -> (Variable<?>) neighbor)
                .toList();
    }

    public List<Factor> getIncomingFactors(final Variable<?> variable, final Factor parentFactor) {
        return Graphs.neighborListOf(this, variable).stream().
                filter(neighbor -> !neighbor.equals(parentFactor))
                .map(neighbor -> (Factor) neighbor)
                .toList();
    }

    public Set<Variable<?>> variableSet() {
        return new HashSet<>(this.vertexSet().stream()
                .filter(vertex -> vertex instanceof Variable<?>)
                .map(variable -> (Variable<?>) variable)
                .toList());
    }

    public Set<Factor> factorSet() {
        return new HashSet<>(this.vertexSet().stream()
                .filter(vertex -> vertex instanceof Factor)
                .map(factor -> (Factor) factor)
                .toList());
    }

    public void fillEdges() {
        Set<Variable<?>> requiredVariables = new HashSet<>(this.factorSet().stream()
                        .flatMap(factor -> factor.getVariables().stream())
                        .toList());
        Set<Variable<?>> variables = this.variableSet();
        if (!variables.containsAll(requiredVariables)) {
            Set<Variable<?>> missingVariables = new HashSet<>(requiredVariables.stream()
                    .filter(variable -> !variables.contains(variable))
                    .toList());
            throw new RuntimeException(Log.genLogMsg(this.getClass(),
                    "There are missing variables: " + missingVariables));
        }
        for (Factor factor  : this.factorSet()) {
            for (Variable<?> variable : factor.getVariables()) {
                this.addEdge(factor, variable);
            }
        }
    }
}
