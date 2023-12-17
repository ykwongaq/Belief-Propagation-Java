package BeliefPropagation.graph;

import BeliefPropagation.utils.Log;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Message class represents a message in a belief propagation algorithm.
 * Message is a probability table with a list of variables.
 * Message class provides methods to calculate marginalization and product of messages.
 * Message class is immutable.
 * @see ProbabilityTable
 */
public class Message extends ProbabilityTable {
    /**
     * Constructor. Call {@link ProbabilityTable#ProbabilityTable(HDArray, List)}.
     * @param probability Probability distributions array.
     * @param variables Array of variables.
     */
    public Message(final HDArray probability, final Variable<?>... variables) {
        super(probability, variables);
    }

    /**
     * Constructor. Call {@link ProbabilityTable#ProbabilityTable(HDArray, List)}.
     * @param probability Probability distributions array.
     * @param variables List of variables.
     */
    public Message(final HDArray probability, final List<Variable<?>> variables) {
        super(probability, variables);
    }

    /**
     * Constructor. Call {@link #Message(HDArray, List)}.
     * @param variables Array of variables.
     */
    public Message(final List<Variable<?>> variables) {
        this(HDArray.createBySizeWithValue(1.0d, variables.stream().mapToInt(Variable::getStateCount).toArray()),
                variables);
    }

    /**
     * Constructor. Call {@link #Message(List)}.
     * @param variables List of variables.
     */
    public Message(final Variable<?>... variables) {
        this(List.of(variables));
    }

    /**
     * Copy constructor.
     * @param otherMessage Another message.
     */
    public Message(final Message otherMessage) {
        super(otherMessage);
    }

    /**
     * Constructor. Call {@link ProbabilityTable#ProbabilityTable(ProbabilityTable)} }.
     * @param probabilityTable Probability table.
     */
    public Message(final ProbabilityTable probabilityTable) {
        super(probabilityTable);
    }

    /**
     * Permute the original dimensions to the target dimensions.
     * @param originalDimensions Original dimensions.
     * @param toDimensionIndices Target dimensions idx for each original axis.
     * @return A new message with permuted dimensions.
     * @see HDArray#moveAxis(int[], int[])
     * @throws NullPointerException If given original dimensions or target dimensions is null.
     * @throws IllegalArgumentException If given original dimensions and target dimensions have different length,
     * or given dimensions contain invalid index, or given dimensions contain duplicated index.
     */
    public Message moveAxis(final int[] originalDimensions, final int[] toDimensionIndices) {
        Objects.requireNonNull(originalDimensions, Log.genLogMsg(this.getClass(),
                "Given original dimensions should not be null"));
        Objects.requireNonNull(toDimensionIndices, Log.genLogMsg(this.getClass(),
                "Given target dimensions should not be null"));
        // Given original dimensions and target dimensions should have same length
        if (originalDimensions.length != toDimensionIndices.length) {
            throw new IllegalArgumentException(Log.genLogMsg(this.getClass(),
                    "Given original dimensions and target dimensions should have same length, but given" +
                            "original dimension is " + Arrays.toString(originalDimensions) + " and target dimension is "
                            + Arrays.toString(toDimensionIndices)));
        }
        // Check invalid index
        for (int i = 0; i < originalDimensions.length; i++) {
            if (originalDimensions[i] < 0 || originalDimensions[i] >= variables.size()) {
                throw new IllegalArgumentException(Log.genLogMsg(this.getClass(),
                        "Given original dimensions contain invalid index: " +
                                Arrays.toString(originalDimensions)));
            }
            if (toDimensionIndices[i] < 0 || toDimensionIndices[i] >= variables.size()) {
                throw new IllegalArgumentException(Log.genLogMsg(this.getClass(),
                        "Given target dimensions contain invalid index: " +
                                Arrays.toString(toDimensionIndices)));
            }
        }
        // Check duplicated index
        if (originalDimensions.length != Arrays.stream(originalDimensions).distinct().count()) {
            throw new IllegalArgumentException(Log.genLogMsg(this.getClass(),
                    "Given original dimensions contain duplicated index: " +
                            Arrays.toString(originalDimensions)));
        }
        if (toDimensionIndices.length != Arrays.stream(toDimensionIndices).distinct().count()) {
            throw new IllegalArgumentException(Log.genLogMsg(this.getClass(),
                    "Given target dimensions contain duplicated index: " +
                            Arrays.toString(toDimensionIndices)));
        }
        // Move axis for probability array
        final HDArray newProbability = this.probability.moveAxis(originalDimensions, toDimensionIndices);
        // Move axis for variable list
        List<Variable<?>> newVariables = new ArrayList<>(variables);
        for (int i = 0; i < originalDimensions.length; i++) {
            newVariables.set(toDimensionIndices[i], variables.get(originalDimensions[i]));
        }
        return new Message(newProbability, newVariables);
    }

    /**
     * Normalize the probability distribution.
     * @see HDArray#normalize()
     */
    public void normalize() {
        this.probability.normalize();
    }

    /**
     * Find indices of target variables in the variable list.
     * @param variableList Variable list.
     * @param targetVariables Target variables.
     * @return Indices of target variables in the variable list.
     * @throws NullPointerException If given variable list or target variables is null.
     * @throws IllegalArgumentException If given target variables contain null element or
     * given target variables is not the subset of the variable list.
     */
    protected static int[] findIndices(final List<Variable<?>> variableList, final List<Variable<?>> targetVariables) {
        Objects.requireNonNull(variableList, Log.genLogMsg(Message.class, "Given variable list should not be null"));
        Objects.requireNonNull(targetVariables, Log.genLogMsg(Message.class, "Given target variables should not be null"));
        return targetVariables.stream().mapToInt(variableList::indexOf).toArray();
    }

    /**
     * Marginalize the message to the target variables.
     * Call {@link #messageMarginalization(Message, List)}.
     * @param message Message.
     * @param targetVariables Target variables.
     * @return A new message marginalized to the target variables.
     */
    public static Message messageMarginalization(final Message message, final Variable<?>... targetVariables) {
        return messageMarginalization(message, List.of(targetVariables));
    }

    /**
     * Marginalize the message to the target variables.
     * @param message Message.
     * @param targetVariables Target variables.
     * @return A new message marginalized to the target variables.
     * @throws NullPointerException If given message or target variables is null.
     * @throws IllegalArgumentException If given target variables contain null element or
     * given target variables is not the subset of the message variables.
     */
    public static Message messageMarginalization(final Message message, final List<Variable<?>> targetVariables) {
        Objects.requireNonNull(message, Log.genLogMsg(Message.class,
                "Given message should not be null"));
        Objects.requireNonNull(targetVariables, Log.genLogMsg(Message.class,
                "Given target variables should not be null"));

        if (targetVariables.isEmpty()) {
            return (Message) message.clone();
        }

        // targetVariables cannot contain null element
        if (targetVariables.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(Log.genLogMsg(Message.class,
                    "Given target variables contain null element"));
        }

        // targetVariables should be the subset of the message variables
        if (!new HashSet<>(message.variables).containsAll(targetVariables)) {
            throw new IllegalArgumentException(Log.genLogMsg(Message.class,
                    "Given target variables should be the subset of the message variables"));
        }

        int[] sumDimensions = Message.findIndices(message.variables, targetVariables);
        HDArray newProbability = message.probability.sumAlongAxis(sumDimensions);
        List<Variable<?>> newVariables =
                message.getVariables().stream().filter(var -> !targetVariables.contains(var)).toList();
        return new Message(newProbability, newVariables);
    }

    /**
     * Product of two messages.
     * @param message1 Message 1.
     * @param message2 Message 2.
     * @return A new message which is the product of message 1 and message 2.
     * @throws NullPointerException If given message 1 or message 2 is null.
     * @throws IllegalArgumentException If given message 1 and message 2 have at least one same variables.
     * @see #messageProduct(List), #messageProduct(Message...)
     */
    public static Message messageProduct(final Message message1, final Message message2) {
        Objects.requireNonNull(message1, Log.genLogMsg(Message.class,
                "Given message 1 should not be null"));
        Objects.requireNonNull(message2, Log.genLogMsg(Message.class,
                "Given message 2 should not be null"));

        // Check if message 1 and message 2 have at least one same variables
        if (Collections.disjoint(message1.variables, message2.variables)) {
            throw new IllegalArgumentException(Log.genLogMsg(Message.class,
                    "Given message 1 and message 2 should have at least one same variables"));
        }

        // Define new variable list with following order
        // [Variables only in message 1], [Common Variables], [Variables only in message2]
        List<Variable<?>> uniqueVariables1 =
                message1.variables.stream().filter(var -> !message2.variables.contains(var)).toList();
        List<Variable<?>> commonVariables = message1.variables.stream().filter(message2.variables::contains).toList();
        List<Variable<?>> uniqueVariables = message2.variables.stream().filter(var -> !message1.variables.contains(var)).toList();
        List<Variable<?>> newVariables = new ArrayList<>();
        newVariables.addAll(uniqueVariables1);
        newVariables.addAll(commonVariables);
        newVariables.addAll(uniqueVariables);

        // Adjust dimensions
        final int[] originDims1 = IntStream.range(0, message1.variables.size()).toArray();
        final int[] targetDims1 =
                Message.findIndices(newVariables.stream().filter(message1.variables::contains).toList(),
                        message1.variables);
        HDArray probability1 = message1.probability.moveAxis(originDims1, targetDims1);
        probability1 = probability1.appendDimension(newVariables.size(), false);

        final int[] originDims2 = IntStream.range(0, message2.variables.size()).toArray();
        final int[] targetDims2 =
                Message.findIndices(newVariables.stream().filter(message2.variables::contains).toList(),
                        message2.variables);
        HDArray probability2 = message2.probability.moveAxis(originDims2, targetDims2);
        probability2 = probability2.appendDimension(newVariables.size(), true);

        HDArray newProbability = probability1.mul(probability2);
        return new Message(newProbability, newVariables);
    }

    /**
     * Product of messages.
     * Call {@link #messageProduct(List)}.
     * @param messages Messages.
     * @return A new message which is the product of messages.
     * @see #messageProduct(List), #messageProduct(Message, Message)
     */
    public static Message messageProduct(final Message... messages) {
        return Message.messageProduct(List.of(messages));
    }

    /**
     * Product of messages.
     * Call {@link #messageProduct(Message, Message)}.
     * @param messages Messages.
     * @return A new message which is the product of messages.
     * @throws NullPointerException If given messages is null.
     * @throws IllegalArgumentException If given messages is empty or given messages contain null element.
     * @see #messageProduct(Message, Message), #messageProduct(Message...)
     */
    public static Message messageProduct(final List<Message> messages) {
        Objects.requireNonNull(messages, Log.genLogMsg(Message.class,
                "Given messages should not be null"));

        // Given messages cannot be empty
        if (messages.isEmpty()) {
            throw new IllegalArgumentException(Log.genLogMsg(Message.class,
                    "Given messages list is empty"));
        }

        // Given messages cannot contain null element
        if (messages.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(Log.genLogMsg(Message.class,
                    "Given messages list contain null element"));
        }

        if (messages.size() == 1) {
            return messages.get(0);
        }

        return messages.stream().reduce(Message::messageProduct).get();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object otherObj) {
        if (this == otherObj) return true;
        if (otherObj == null || this.getClass() != otherObj.getClass()) return false;
        return super.equals(otherObj);
    }

    @Override
    public String toString() {
        return "Message: " + variables;
    }

    @Override
    public Object clone() {
        return new Message((ProbabilityTable) super.clone());
    }
}
