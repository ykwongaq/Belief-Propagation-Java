package wyk.bp.alg.interfaces;


import wyk.bp.graph.Variable;
import wyk.bp.graph.ProbabilityTable;

@FunctionalInterface
public interface BeliefPropagationAlgorithm {
    public ProbabilityTable getBelief(final Variable<?> variable);

}
