package BeliefPropagation.alg.interfaces;


import BeliefPropagation.graph.Message;
import BeliefPropagation.graph.Variable;

/**
 * Interface of Belief Propagation algorithm.
 */
@FunctionalInterface
public interface BeliefPropagationAlgorithm {
    /**
     * Get the belief (or probability distribution of different state) of given {@code variable}.
     * @param variable Target random variable.
     * @return Belief (or probability distribution of different state) of given {@code variable}.
     */
    Message getBelief(final Variable<?> variable);

}
