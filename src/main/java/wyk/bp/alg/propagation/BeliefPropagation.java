package wyk.bp.alg.propagation;

import wyk.bp.graph.FactorGraph;
import wyk.bp.graph.Variable;
import wyk.bp.probtable.ProbabilityTable;

public class BeliefPropagation<E> extends BaseBeliefPropagationAlgorithm<E> {
    public BeliefPropagation(FactorGraph<E> graph) {
        super(graph);
    }
    @Override
    public ProbabilityTable getBelief(Variable<?> variable) {
        return null;
    }
}
