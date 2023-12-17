package BeliefPropagation.alg.propagation;

import BeliefPropagation.alg.interfaces.BeliefPropagationAlgorithm;
import BeliefPropagation.graph.*;
import BeliefPropagation.utils.Log;
import org.jgrapht.alg.util.Pair;

import java.util.*;

/**
 * Base class of all Belief Propagation algorithm.
 * @param <E> Edge type.
 */
abstract public class BaseBeliefPropagationAlgorithm<E> implements BeliefPropagationAlgorithm {

    /**
     * Factor graph to run.
     */
    protected final FactorGraph<E> graph;
    /**
     * Message cache used to save computation cast.
     */
    protected final Map<Pair<FactorGraphNode, FactorGraphNode>, Message> messageTable;

    /**
     *  Constructor.
     * @param graph Factor graph.
     * @throws NullPointerException if {@code graph} is null.
     * @throws IllegalArgumentException if {@code graph} is invalid. See {@link FactorGraph#isValid()}.
     */
    public BaseBeliefPropagationAlgorithm(FactorGraph<E> graph) {
        Objects.requireNonNull(graph, "Graph cannot be null");
        if (!graph.isValid()) {
            throw new IllegalArgumentException(Log.genLogMsg(this.getClass(), "Given factor graph is not valid"));
        }
        this.graph = graph;
        this.messageTable = new HashMap<>();
    }

    abstract public Message getBelief(final Variable<?> variable);

    /**
     * Get the message from {@code variable} to {@code factor} from the {@code messageTable}. If the message is not in
     * the cache table, it will call {@link #computeVariableToFactorMessage(Variable, Factor)} to calculate the message.
     * The calculated message will store into the {@code memoryTable} afterward.
     * @param variable Source random variable.
     * @param factor Target factor.
     * @return Message send from {@code variable} to {@code factor}.
     * @throws NullPointerException if {@code variable} or {@code factor} is null.
     * @throws IllegalArgumentException if the graph does not contain {@code variable} or {@code factor}.
     * @see #computeVariableToFactorMessage(Variable, Factor)
     */
    protected Message getVariableToFactorMessage(final Variable<?> variable, final Factor factor) {
        Objects.requireNonNull(variable, Log.genLogMsg(this.getClass(), "Given variable cannot be null"));
        Objects.requireNonNull(factor, Log.genLogMsg(this.getClass(), "Given factor cannot be null"));

        // Check is graph contain variable
        if (!this.graph.containsVertex(variable)) {
            throw new IllegalArgumentException(Log.genLogMsg(this.getClass(), "Graph does not contain variable: " + variable));
        }

        // Check is graph contain factor
        if (!this.graph.containsVertex(factor)) {
            throw new IllegalArgumentException(Log.genLogMsg(this.getClass(), "Graph does not contain factor: " + factor));
        }

        // If the cache table does not have required message, compute it.
        Pair<FactorGraphNode, FactorGraphNode> key = this.genKey(variable, factor);
        if (!this.messageTable.containsKey(key)) {
            final Message message = this.computeVariableToFactorMessage(variable, factor);
            this.messageTable.put(key, message);
        }
        return this.messageTable.get(key);

    }

    /**
     * Compute the message send from {@code variable} to {@code factor}.
     * @param variable Source variable.
     * @param factor Target factor.
     * @return Message send from {@code variable} to {@code factor}.
     */
    protected Message computeVariableToFactorMessage(final Variable<?> variable, final Factor factor) {

        // To compute the message send from given variable, we need to calculate the messages
        // send to that variable first.
        List<Message> incomingMessages = new ArrayList<>();
        for (Factor neighborFactor : this.graph.getIncomingFactors(variable, factor)) {
            Message message = this.getFactorToVariableMessage(neighborFactor, variable);
            incomingMessages.add(message);
        }

        // Afterward, join all the incoming message together.
        if (incomingMessages.isEmpty()) {
            return new Message(variable);
        } else {
            Message message = Message.messageProduct(incomingMessages);
            message.normalize();
            return message;
        }
    }

    /**
     * Get the message from {@code factor} to {@code variable} from the {@code messageTable}. If the message is not in
     * the cache table, it will call {@link #computeFactorToVariableMessage(Factor, Variable)} to calculate the message.
     * The calculated message will store into the {@code memoryTable} afterward.
     * @param factor Source factor.
     * @param variable Target variable.
     * @return Message from {@code factor} to {@code variable}
     * @throws NullPointerException if {@code factor} or {@code variable} is null.
     * @throws IllegalArgumentException if graph does not contain {@code factor}  or {@code varaible}.
     * @see #computeFactorToVariableMessage(Factor, Variable)
     */
    protected Message getFactorToVariableMessage(final Factor factor, Variable<?> variable) {
        Objects.requireNonNull(variable, Log.genLogMsg(this.getClass(), "Given variable cannot be null"));
        Objects.requireNonNull(factor, Log.genLogMsg(this.getClass(), "Given factor cannot be null"));

        // Check is graph contain variable
        if (!this.graph.containsVertex(variable)) {
            throw new IllegalArgumentException(Log.genLogMsg(this.getClass(), "Graph does not contain variable: " + variable));
        }

        // Check is graph contain factor
        if (!this.graph.containsVertex(factor)) {
            throw new IllegalArgumentException(Log.genLogMsg(this.getClass(), "Graph does not contain factor: " + factor));
        }

        // Check the cache table contain the required message. If not, compute it.
        Pair<FactorGraphNode, FactorGraphNode> key = this.genKey(factor, variable);
        if (!this.messageTable.containsKey(key)) {
            final Message message = this.computeFactorToVariableMessage(factor, variable);
            this.messageTable.put(key, message);
        }
        return this.messageTable.get(key);
    }

    /**
     * Compute the message from {@code factor} to {@code variable}.
     * @param factor Source factor.
     * @param variable Target variable.
     * @return Message from {@code factor} to {@code variable}.
     */
    protected Message computeFactorToVariableMessage(final Factor factor, Variable<?> variable) {
        // To compute the message send from given factor, we need to get the messages sending to that factor first.
        // Note that we also need to join the probability distribution of the factor itself.
        List<Message> incomingMessages = new ArrayList<>();
        incomingMessages.add(new Message(factor));
        List<Variable<?>> marginalizationVariables = new ArrayList<>();
        for (Variable<?> variableNeighbor : this.graph.getIncomingVariables(factor, variable)) {
            marginalizationVariables.add(variableNeighbor);
            Message message = this.getVariableToFactorMessage(variableNeighbor, factor);
            incomingMessages.add(message);
        }

        // Afterward, joni all the message together.
        if (incomingMessages.size() == 1) {
            return incomingMessages.get(0);
        } else {
            Message joinedMessage = Message.messageProduct(incomingMessages);
            Message marginalizedMessage = Message.messageMarginalization(joinedMessage, marginalizationVariables);
            marginalizedMessage.normalize();
            return marginalizedMessage;
        }
    }

    /**
     * Generated the key to the cache table. The key is a pair of {@link FactorGraphNode}, where the first element is
     * the source, and the second element is the target.
     * @param sourceNode Source node.
     * @param targetNode Target node.
     * @return Generated key to cache table.
     */
    protected Pair<FactorGraphNode, FactorGraphNode> genKey(final FactorGraphNode sourceNode, final FactorGraphNode targetNode) {
        return new Pair<>(sourceNode, targetNode);
    }
}
