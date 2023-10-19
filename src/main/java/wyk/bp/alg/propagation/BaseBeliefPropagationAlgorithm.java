package wyk.bp.alg.propagation;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.util.Pair;
import wyk.bp.alg.interfaces.BeliefPropagationAlgorithm;
import wyk.bp.graph.*;
import wyk.bp.utils.Log;

import java.util.*;

abstract public class BaseBeliefPropagationAlgorithm<E> implements BeliefPropagationAlgorithm {

    protected final FactorGraph<E> graph;
    protected final Map<Pair<FactorGraphNode, FactorGraphNode>, Message> messageTable;
    public BaseBeliefPropagationAlgorithm(FactorGraph<E> graph) {
        Objects.requireNonNull(graph, "Graph cannot be null");
        if (!graph.isValid()) {
            throw new IllegalArgumentException(Log.genLogMsg(this.getClass(), "Given factor graph is not valid"));
        }
        this.graph = graph;
        this.messageTable = new HashMap<>();
    }
    abstract public Message getBelief(final Variable<?> variable);

    protected Message getVariableToFactorMessage(final Variable<?> variable, final Factor factor) {
        Objects.requireNonNull(variable, Log.genLogMsg(this.getClass(), "Given variable cannot be null"));
        Objects.requireNonNull(factor, Log.genLogMsg(this.getClass(), "Given factor cannot be null"));

        if (!this.graph.containsVertex(variable)) {
            throw new IllegalArgumentException(Log.genLogMsg(this.getClass(), "Graph does not contain variable: " + variable));
        }

        if (!this.graph.containsVertex(factor)) {
            throw new IllegalArgumentException(Log.genLogMsg(this.getClass(), "Graph does not contain factor: " + factor));
        }

        Pair<FactorGraphNode, FactorGraphNode> key = this.genKey(variable, factor);
        if (!this.messageTable.containsKey(key)) {
            final Message message = this.computeVariableToFactorMessage(variable, factor);
            this.messageTable.put(key, message);
        }
        return this.messageTable.get(key);

    }

    protected Message computeVariableToFactorMessage(final Variable<?> variable, final Factor factor) {
        List<Message> incomingMessages = new ArrayList<>();

        for (Factor neighborFactor : this.graph.getIncomingFactors(variable, factor)) {
            Message message = this.getFactorToVariableMessage(neighborFactor, variable);
            incomingMessages.add(message);
        }
        Graphs.neighborListOf(this.graph, variable);
        if (incomingMessages.isEmpty()) {
            return new Message(variable);
        } else {
            Message message = Message.joinMessages(incomingMessages);
            message.normalize();
            return message;
        }
    }

    protected Message getFactorToVariableMessage(final Factor factor, Variable<?> variable) {
        Objects.requireNonNull(variable, Log.genLogMsg(this.getClass(), "Given variable cannot be null"));
        Objects.requireNonNull(factor, Log.genLogMsg(this.getClass(), "Given factor cannot be null"));

        if (!this.graph.containsVertex(variable)) {
            throw new IllegalArgumentException(Log.genLogMsg(this.getClass(), "Graph does not contain variable: " + variable));
        }

        if (!this.graph.containsVertex(factor)) {
            throw new IllegalArgumentException(Log.genLogMsg(this.getClass(), "Graph does not contain factor: " + factor));
        }

        Pair<FactorGraphNode, FactorGraphNode> key = this.genKey(factor, variable);
        if (!this.messageTable.containsKey(key)) {
            final Message message = this.computeFactorToVariableMessage(factor, variable);
            this.messageTable.put(key, message);
        }
        return this.messageTable.get(key);
    }

    protected Message computeFactorToVariableMessage(final Factor factor, Variable<?> variable) {
        List<Message> incomingMessages = new ArrayList<>();
        incomingMessages.add(new Message(factor));

        List<Variable<?>> marginalizationVariables = new ArrayList<>();
        for (Variable<?> variableNeighbor : this.graph.getIncomingVariables(factor, variable)) {
            marginalizationVariables.add(variableNeighbor);
            Message message = this.getVariableToFactorMessage(variableNeighbor, factor);
            incomingMessages.add(message);
        }

        if (incomingMessages.size() == 1) {
            return incomingMessages.get(0);
        } else {
            Message joinedMessage = Message.joinMessages(incomingMessages);
            Message marginalizedMessage = Message.messageMarginalization(joinedMessage, marginalizationVariables);
            marginalizedMessage.normalize();
            return marginalizedMessage;
        }
    }

    protected Pair<FactorGraphNode, FactorGraphNode> genKey(final FactorGraphNode sourceNode, final FactorGraphNode targetNode) {
        return new Pair<>(sourceNode, targetNode);
    }
}
