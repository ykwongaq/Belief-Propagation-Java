package BeliefPropagation.alg.propagation;

import BeliefPropagation.alg.cycle.UndirectedCycleDetector;
import BeliefPropagation.graph.*;
import BeliefPropagation.utils.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Belief Propagation algorithm is run in recursive manner. It requires {@link FactorGraph} to be a tree (without any cycle) or else
 * this algorithm will run into infinite loop.
 *
 * @param <E> Edge type.
 */
public class BeliefPropagation<E> extends BaseBeliefPropagationAlgorithm<E> {
    /**
     * Constructor.
     * @param graph Factor graph.
     * @throws IllegalArgumentException if the given graph contain cycle.
     * @see UndirectedCycleDetector#detectCycles()
     */
    public BeliefPropagation(FactorGraph<E> graph) {
        super(graph);
        UndirectedCycleDetector<FactorGraphNode, E> cycleDetector = new UndirectedCycleDetector<>(this.graph);
        if (cycleDetector.detectCycles()) {
            throw new IllegalArgumentException(Log.genLogMsg(getClass(), "Given factor graph should not contain cycle"));
        }
    }

    @Override
    public Message getBelief(Variable<?> variable) {
        List<Message> incomingMessages = new ArrayList<>();
        for (Factor neighborFactor : this.graph.getIncomingFactors(variable, null)) {
            Message message = this.getFactorToVariableMessage(neighborFactor, variable);
            incomingMessages.add(message);
        }
        Message jointedMessage = Message.messageProduct(incomingMessages);
        jointedMessage.normalize();
        return jointedMessage;
    }
}
