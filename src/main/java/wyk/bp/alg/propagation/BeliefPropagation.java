package wyk.bp.alg.propagation;

import org.jgrapht.Graph;
import org.jgrapht.alg.util.Pair;
import wyk.bp.graph.FactorGraph;
import wyk.bp.graph.FactorGraphNode;
import wyk.bp.graph.Entity;
import wyk.bp.probtable.ProbabilityTable;

import java.util.Map;

public class BeliefPropagation<E> extends BaseBeliefPropagationAlgorithm<E> {
    public BeliefPropagation(FactorGraph<E> graph) {
        super(graph);
    }
    @Override
    public ProbabilityTable getBelief(Entity<?> entity) {
        return null;
    }
}
