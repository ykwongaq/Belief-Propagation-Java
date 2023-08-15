package wyk.bp.alg.interfaces;


import wyk.bp.graph.Entity;
import wyk.bp.probtable.ProbabilityTable;

@FunctionalInterface
public interface BeliefPropagationAlgorithm {
    public ProbabilityTable getBelief(final Entity<?> entity);

}
