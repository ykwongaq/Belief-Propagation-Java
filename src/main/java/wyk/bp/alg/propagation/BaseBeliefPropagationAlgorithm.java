package wyk.bp.alg.propagation;

import org.jgrapht.Graph;
import org.jgrapht.alg.util.Pair;
import wyk.bp.alg.interfaces.BeliefPropagationAlgorithm;
import wyk.bp.graph.Factor;
import wyk.bp.graph.FactorGraphNode;
import wyk.bp.graph.Entity;
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
    abstract public ProbabilityTable getBelief(final Entity<?> entity);
    protected ProbabilityTable getMessage(final FactorGraphNode fromNode, final FactorGraphNode toNode) {
        return null;
    }
    protected ProbabilityTable getFactorToEntityMessage(final Factor factor, final Entity<?> entity) {
        Pair<FactorGraphNode, FactorGraphNode> key = Pair.of(factor, entity);
        return this.messageTable.getOrDefault(key, this.calFactorToEntityMessage(factor, entity));
    }
    protected ProbabilityTable getEntityToFactorMessage(final Entity<?> entity, final Factor factor) {
        Pair<FactorGraphNode, FactorGraphNode> key = Pair.of(entity, factor);
        return this.messageTable.getOrDefault(key, this.calEntityToFactorMessage(entity, factor));
    }
    protected ProbabilityTable calFactorToEntityMessage(final Factor factor, final Entity<?> entity) {
        return null;
    }
    protected ProbabilityTable calEntityToFactorMessage(final Entity<?> entity, final Factor factor) {
        return null;
    }
}
