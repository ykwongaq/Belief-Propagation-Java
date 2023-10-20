package wyk.bp.alg.propagation;

import org.jgrapht.alg.util.Pair;
import org.nd4j.shade.errorprone.annotations.Var;
import wyk.bp.graph.*;
import wyk.bp.utils.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoopyBeliefPropagation<E> extends BaseBeliefPropagationAlgorithm<E> {

    protected int iteration;

    protected final static int DEFAULT_ITERATION = 5;

    protected final static String INVALID_ITERATION_ERROR = "Given iteration should be larger than 0";

    protected final static String INVALID_EDGE_ERROR = "Exist invalid edge";

    protected boolean loopFlag = false;


    public LoopyBeliefPropagation(FactorGraph<E> graph) {
        this(graph, LoopyBeliefPropagation.DEFAULT_ITERATION);
    }

    public LoopyBeliefPropagation(FactorGraph<E> graph, final int iteration) {
        super(graph);
        if (iteration <= 0) {
            throw new IllegalArgumentException(Log.genLogMsg(this.getClass(), LoopyBeliefPropagation.INVALID_ITERATION_ERROR));
        }
        this.iteration = iteration;
        this.messageTable.putAll(this.generateInitialMessageTable());
    }

    @Override
    public Message getBelief(Variable<?> variable) {
        if (!this.loopFlag) {
            this.loopPropagation();
        }
        List<Message> incomingMessages = new ArrayList<>();
        for (Factor neighborFactor : this.graph.getIncomingFactors(variable, null)) {
            Message message = this.getFactorToVariableMessage(neighborFactor, variable);
            incomingMessages.add(message);
        }
        Message jointedMessage = Message.joinMessages(incomingMessages);
        jointedMessage.normalize();
        return jointedMessage;
    }

    protected Map<Pair<FactorGraphNode, FactorGraphNode>, Message> generateInitialMessageTable() {
        Map<Pair<FactorGraphNode, FactorGraphNode>, Message> messages = new HashMap<>();
        for (E edge : this.graph.edgeSet()) {
            final FactorGraphNode source = this.graph.getEdgeSource(edge);
            final FactorGraphNode target = this.graph.getEdgeTarget(edge);

            Message message;
            if (source instanceof Factor && target instanceof Variable<?> targetVariable) {
                message = new Message(targetVariable);
            } else if (source instanceof Variable<?> targetVariable && target instanceof Factor) {
                message = new Message(targetVariable);
            } else {
                throw new IllegalArgumentException(Log.genLogMsg(this.getClass(), LoopyBeliefPropagation.INVALID_EDGE_ERROR));
            }

            Pair<FactorGraphNode, FactorGraphNode> key1 = Pair.of(source, target);
            Pair<FactorGraphNode, FactorGraphNode> key2 = Pair.of(target, source);

            messages.put(key1, message);
            messages.put(key2, new Message(message));
        }
        return messages;
    }

    public void loopPropagation() {
        Map<Pair<FactorGraphNode, FactorGraphNode>, Message> newMessageTable = this.generateInitialMessageTable();
        for (int itr=0; itr<this.iteration; itr++) {
            for (E edge : this.graph.edgeSet()) {
                final FactorGraphNode source = this.graph.getEdgeSource(edge);
                final FactorGraphNode target = this.graph.getEdgeTarget(edge);

                Variable<?> variable = null;
                Factor factor = null;

                if (source instanceof Factor && target instanceof Variable<?>) {
                    factor = (Factor) source;
                    variable = (Variable<?>) target;
                } else if (source instanceof Variable<?> && target instanceof Factor) {
                    factor = (Factor) target;
                    variable = (Variable<?>) source;
                } else {
                    throw new IllegalArgumentException(Log.genLogMsg(this.getClass(), LoopyBeliefPropagation.INVALID_EDGE_ERROR));
                }

                final Pair<FactorGraphNode, FactorGraphNode> key1 = Pair.of(factor, variable);
                final Pair<FactorGraphNode, FactorGraphNode> key2 = Pair.of(variable, factor);
                Message factorToVariableMessage = this.computeFactorToVariableMessage(factor, variable);
                newMessageTable.put(key1, factorToVariableMessage);
                Message variableToFactorMessage = this.computeVariableToFactorMessage(variable, factor);
                newMessageTable.put(key2, variableToFactorMessage);
            }
            this.messageTable.putAll(newMessageTable);
        }
        this.setLoopFlag(true);
    }

    public boolean isLooped() {
        return this.loopFlag;
    }

    public void setLoopFlag(final boolean loopFlag) {
        this.loopFlag = loopFlag;
    }

    public int getIteration() {
        return this.iteration;
    }

    public void setIteration(final int iteration) {
        if (iteration <= 0) {
            throw new IllegalArgumentException(Log.genLogMsg(this.getClass(), LoopyBeliefPropagation.INVALID_ITERATION_ERROR));
        }
        this.iteration = iteration;
    }
}
