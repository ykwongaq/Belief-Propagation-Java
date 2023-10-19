package wyk.bp.alg.interfaces;


import wyk.bp.graph.Message;
import wyk.bp.graph.Variable;
import wyk.bp.graph.ProbabilityTable;

@FunctionalInterface
public interface BeliefPropagationAlgorithm {
    public Message getBelief(final Variable<?> variable);

}
