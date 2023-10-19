package wyk.bp.alg.propagation;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import wyk.bp.alg.cycle.UndirectedCycleDetector;
import wyk.bp.graph.*;
import wyk.bp.utils.Log;

import java.util.ArrayList;
import java.util.List;

public class BeliefPropagation<E> extends BaseBeliefPropagationAlgorithm<E> {
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
        Message jointedMessage = Message.joinMessages(incomingMessages);
        jointedMessage.normalize();
        return jointedMessage;
    }
}
