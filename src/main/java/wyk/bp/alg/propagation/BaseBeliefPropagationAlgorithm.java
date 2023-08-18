package wyk.bp.alg.propagation;

import org.jgrapht.Graph;
import org.jgrapht.alg.util.Pair;
import wyk.bp.alg.interfaces.BeliefPropagationAlgorithm;
import wyk.bp.graph.Factor;
import wyk.bp.graph.FactorGraphNode;
import wyk.bp.graph.Variable;
import wyk.bp.probtable.ProbabilityTable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

abstract public class BaseBeliefPropagationAlgorithm<E> implements BeliefPropagationAlgorithm {
    protected final Graph<FactorGraphNode, E> graph;
    protected final Map<Pair<FactorGraphNode, FactorGraphNode>, ProbabilityTable> messageTable;
    public BaseBeliefPropagationAlgorithm(Graph<FactorGraphNode, E> graph) {
        Objects.requireNonNull(graph, "Graph cannot be null");
        this.graph = graph;
        this.messageTable = new HashMap<>();
    }
    abstract public ProbabilityTable getBelief(final Variable<?> variable);
    protected ProbabilityTable getMessage(final FactorGraphNode fromNode, final FactorGraphNode toNode) {
        return null;
    }
    protected ProbabilityTable getFactorToVariableMessage(final Factor factor, final Variable<?> variable) {
        Pair<FactorGraphNode, FactorGraphNode> key = Pair.of(factor, variable);
        return this.messageTable.getOrDefault(key, this.calFactorToVariableMessage(factor, variable));
    }
    protected ProbabilityTable getVariableToFactorMessage(final Variable<?> variable, final Factor factor) {
        Pair<FactorGraphNode, FactorGraphNode> key = Pair.of(variable, factor);
        return this.messageTable.getOrDefault(key, this.calVariableToFactorMessage(variable, factor));
    }
    protected ProbabilityTable calFactorToVariableMessage(final Factor factor, final Variable<?> variable) {
        return null;
    }
    protected ProbabilityTable calVariableToFactorMessage(final Variable<?> variable, final Factor factor) {
        return null;
    }
}
