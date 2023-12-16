package wyk.bp.alg.propagation;

import org.jgrapht.alg.util.Pair;
import wyk.bp.graph.*;
import wyk.bp.utils.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link LoopyBeliefPropagation} run belief propagation on {@link FactorGraph} in iterative manner. It allows
 * {@link FactorGraph} to contain cycle. However, the computed belief is no longer exact value,
 * but rather an estimation. User can define the number of estimation during construction {@link #LoopyBeliefPropagation(FactorGraph, int)}.
 * The more iteration, the more accurate. Indeed, it takes more time as well.
 * @param <E> Edge type.
 */
public class LoopyBeliefPropagation<E> extends BaseBeliefPropagationAlgorithm<E> {
    /**
     * Number of iteration to run loop belief propagation.
     */
    protected int iteration;

    /**
     * Default number of iteration.
     */
    protected final static int DEFAULT_ITERATION = 5;

    protected final static String INVALID_ITERATION_ERROR = "Given iteration should be larger than 0";

    protected final static String INVALID_EDGE_ERROR = "Exist invalid edge";

    /**
     * {@code True} if the loop belief propagation is finished.
     */
    protected boolean loopFlag = false;

    /**
     * Constructor, with default iteration = 5.
     * @param graph Factor graph.
     * @see #LoopyBeliefPropagation(FactorGraph, int)
     */
    public LoopyBeliefPropagation(FactorGraph<E> graph) {
        this(graph, LoopyBeliefPropagation.DEFAULT_ITERATION);
    }

    /**
     * Constructor.
     * @param graph Factor graph
     * @param iteration Number of iteration
     * @see BaseBeliefPropagationAlgorithm#BaseBeliefPropagationAlgorithm(FactorGraph)
     * @throws IllegalArgumentException if {@code iteration} is smaller than 1.
     */
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
        Message jointedMessage = Message.messageProduct(incomingMessages);
        jointedMessage.normalize();
        return jointedMessage;
    }

    /**
     * Initialize a cache message table, so that the {@link BaseBeliefPropagationAlgorithm#getVariableToFactorMessage(Variable, Factor)}
     * and {@link BaseBeliefPropagationAlgorithm#getFactorToVariableMessage(Factor, Variable)} will not run into
     * infinite loop. The initialized message are all equal probability distribution.
     * @return Initialized message table.
     */
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

    /**
     * Run the belief propagation algorithm for given number of iteration. The computed message are stored in
     * cache message table.
     */
    public void loopPropagation() {
        Map<Pair<FactorGraphNode, FactorGraphNode>, Message> newMessageTable = this.generateInitialMessageTable();
        for (int itr=0; itr<this.iteration; itr++) {
            for (E edge : this.graph.edgeSet()) {
                // For all edge, get the source variable/factor and target factor/varaible
                final FactorGraphNode source = this.graph.getEdgeSource(edge);
                final FactorGraphNode target = this.graph.getEdgeTarget(edge);

                Variable<?> variable;
                Factor factor;
                if (source instanceof Factor && target instanceof Variable<?>) {
                    factor = (Factor) source;
                    variable = (Variable<?>) target;
                } else if (source instanceof Variable<?> && target instanceof Factor) {
                    factor = (Factor) target;
                    variable = (Variable<?>) source;
                } else {
                    throw new IllegalArgumentException(Log.genLogMsg(this.getClass(), LoopyBeliefPropagation.INVALID_EDGE_ERROR));
                }

                // Compute factor to variable message and store it in temporary table
                final Pair<FactorGraphNode, FactorGraphNode> key1 = Pair.of(factor, variable);
                Message factorToVariableMessage = this.computeFactorToVariableMessage(factor, variable);
                newMessageTable.put(key1, factorToVariableMessage);

                // Compute variable to factor message and store it in temporary table
                final Pair<FactorGraphNode, FactorGraphNode> key2 = Pair.of(variable, factor);
                Message variableToFactorMessage = this.computeVariableToFactorMessage(variable, factor);
                newMessageTable.put(key2, variableToFactorMessage);
            }

            // Update message cache table
            this.messageTable.putAll(newMessageTable);
        }
        this.setLoopFlag(true);
    }

    /**
     * Check is the loop algorithm is finished or not.
     * @return {@code True} if the algorithm is finished.
     */
    public boolean isLooped() {
        return this.loopFlag;
    }

    /**
     * Set {@code loopFlag} to indicate is the loopy belief propagation is finished or not.
     * @param loopFlag {@code True} if the loopy belief propagation is finished.
     */
    public void setLoopFlag(final boolean loopFlag) {
        this.loopFlag = loopFlag;
    }

    /**
     * Get number of iteration.
     * @return Number of iteration.
     */
    public int getIteration() {
        return this.iteration;
    }

    /**
     * Set the number of iteration.
     * @param iteration Number of iteration.
     * @throws IllegalArgumentException if number of iteration is smaller than 1.
     */
    public void setIteration(final int iteration) {
        if (iteration <= 0) {
            throw new IllegalArgumentException(Log.genLogMsg(this.getClass(), LoopyBeliefPropagation.INVALID_ITERATION_ERROR));
        }
        this.iteration = iteration;
    }
}
