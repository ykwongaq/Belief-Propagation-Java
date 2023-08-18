package wyk.bp.alg.interfaces;


import wyk.bp.graph.Variable;
import wyk.bp.probtable.ProbabilityTable;

@FunctionalInterface
public interface BeliefPropagationAlgorithm {
    public ProbabilityTable getBelief(final Variable<?> variable);

}
