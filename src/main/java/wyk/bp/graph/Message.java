package wyk.bp.graph;

import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import wyk.bp.utils.DistributionUtil;
import wyk.bp.utils.Log;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Message which is going to be propagated across the {@link FactorGraph}.
 */
public class Message extends ProbabilityTable {

    /**
     * Constructor with default equal probability distribution.
     * @param variables Array of variables.
     * @see #Message(INDArray, Variable[])
     */
    public Message(final Variable<?>... variables) {
        this(Arrays.asList(variables));
    }

    /**
     * Constructor with default equal probability distribution.
     * @param variables List of variables.
     * @see #Message(INDArray, List)
     */
    public Message(final List<Variable<?>> variables) {
        this(Nd4j.ones(variables.stream().mapToLong(Variable::getStateCount).toArray()).castTo(DataType.DOUBLE), variables);
    }

    /**
     * Constructor.
     * @param distribution Probability distribution
     * @param variables Array of variables.
     * @see #Message(INDArray, List)
     */
    public Message(final INDArray distribution, final Variable<?>... variables) {
        this(distribution, Arrays.asList(variables));
    }

    /**
     * Constructor.
     * @param distribution Probability distribution.
     * @param variables List of variables.
     * @see ProbabilityTable#ProbabilityTable(INDArray, List)
     */
    public Message(final INDArray distribution, final List<Variable<?>> variables) {
        super(distribution, variables);
    }

    /**
     * Deep copy constructor
     * @param table Other Probability table.
     * @see ProbabilityTable#ProbabilityTable(ProbabilityTable)
     */
    public Message(final ProbabilityTable table) {
        super(table);
    }

    /**
     * Move axis of this {@code distribution} to new position.
     *
     * Suppose {@code message} have probability distribution with shape {@code [2, 3, 4]}.
     * <pre>
     *     message.moveaxis({0, 1, 2}, {0, 2, 1})
     * </pre>
     * Now the probability distribution should have shape {@code [2, 4, 3]}.<br/>
     *
     * This function is trying to mimic numpy function {@code np.moveaxis}. <br/>
     *
     * Also note that negative position is not allowed.
     * @param originDims Original positions of the axes to move. These must be unique.
     * @param targetDims Destination positions for each of the original axis. These mush also be unique.
     * @return Message with modified probability distribution.
     * @see DistributionUtil#moveaxis(INDArray, int[], int[])
     */
    public Message moveAxis(int[] originDims, int[] targetDims) {
        INDArray newDistribution = DistributionUtil.moveaxis(this.getDistribution(), originDims, targetDims);
        return new Message(newDistribution, this.getVariables());
    }

    /**
     * Normalize the probability distribution. This is an in-place operation.
     */
    public void normalize() {
        this.distribution.divi(this.distribution.sumNumber());
    }

    /**
     * Message multiplication that join two message together.
     * @param message1 First message
     * @param message2 Second message
     * @return Message product.
     * @throws NullPointerException if one of the {@link Message} is null
     * @throws IllegalArgumentException if there are no common variables between two {@link  Message}
     */
    public static Message messageProduct(final Message message1, final Message message2) {
        Objects.requireNonNull(message1, Log.genLogMsg("Message", "message1 cannot be null"));
        Objects.requireNonNull(message2, Log.genLogMsg("Message", "message2 cannot be null"));

        // There should be at least one common variables
        if (message1.getVariables().stream().noneMatch(var -> message2.getVariables().contains(var))) {
            throw new IllegalArgumentException(Log.genLogMsg("Message", "There are no common variable between two given factors"));
        }

        // Define new variables list with following order
        // [Variables only in message1], [Common Variables], [Variables only in message2]
        List<Variable<?>> uniqueVariables1 = message1.getVariables().stream().filter(var -> !message2.getVariables().contains(var)).toList();
        List<Variable<?>> commonVariables = message1.getVariables().stream().filter(var -> message2.getVariables().contains(var)).toList();
        List<Variable<?>> uniqueVariables2 = message2.getVariables().stream().filter(var -> !message1.getVariables().contains(var)).toList();

        List<Variable<?>> newVariables = new ArrayList<>();
        newVariables.addAll(uniqueVariables1);
        newVariables.addAll(commonVariables);
        newVariables.addAll(uniqueVariables2);

        // Adjust dimensions
        final int[] originDims1 = IntStream.range(0, message1.getVariables().size()).toArray();
        final int[] targetDims1 = Message.findIndices(newVariables.stream().filter(message1.getVariables()::contains).toList(), message1.getVariables());
        INDArray distribution1 = DistributionUtil.moveaxis(message1.getDistribution(), originDims1, targetDims1);
        distribution1 = DistributionUtil.appendDimensions(distribution1, newVariables.size() - message1.getVariables().size(), false);

        final int[] originDims2 = IntStream.range(0, message2.getVariables().size()).toArray();
        final int[] targetDims2 = Message.findIndices(newVariables.stream().filter(message2.getVariables()::contains).toList(), message2.getVariables());
        INDArray distribution2 = DistributionUtil.moveaxis(message2.getDistribution(), originDims2, targetDims2);
        distribution2 = DistributionUtil.appendDimensions(distribution2, newVariables.size() - message2.getVariables().size(), true);

        // Calculate new distribution
        final INDArray newDistribution = distribution1.mul(distribution2);

        return new Message(newDistribution, newVariables);
    }

    /**
     * Find the position index of {@code targetVariables} in {@code variables}.
     * @param variables Reference variable list.
     * @param targetVariables List of target variables.
     * @return Array of integer representing the corresponding position index.
     * @throws NullPointerException if any to parameter is null.
     */
    protected static int[] findIndices(final List<Variable<?>> variables, final List<Variable<?>> targetVariables) {
        return targetVariables.stream().mapToInt(variables::indexOf).toArray();
    }

    /**
     * Probability marginalization.
     * @param message Reference message
     * @param targetVariables Variable to be marginalized, which will be removed from the variable list.
     * @return Marginalized message.
     * @see #messageMarginalization(Message, List)
     */
    public static Message messageMarginalization(final Message message, final Variable<?>... targetVariables) {
        return Message.messageMarginalization(message, Arrays.asList(targetVariables));
    }

    /**
     * Probability marginalization.
     * @param message Reference message
     * @param targetVariables Variable to be marginalized, which will be removed from the variable list.
     * @return Marginalized message.
     * @throws NullPointerException if any of the arguments is null.
     * @throws IllegalArgumentException if any of the condition happen:
     * <ol>
     *     <li>{@code targetVariables} is empty</li>
     *     <li>{@code targetVariables} contain null element</li>
     *     <li>{@code targetVariables} is not the subset of the variables in {@code message}</li>
     * </ol>
     */
    public static Message messageMarginalization(final Message message, final List<Variable<?>> targetVariables) {
        Objects.requireNonNull(message, Log.genLogMsg("Message", "Given message cannot be null"));
        Objects.requireNonNull(targetVariables, Log.genLogMsg("Message", "Given targetVariables cannot be null"));

        // targetVariables cannot be empty
        if (targetVariables.isEmpty()) {
            throw new IllegalArgumentException(Log.genLogMsg("Message", "Given targetVariables cannot be empty"));
        }

        // targetVariables cannot contain null element
        if (targetVariables.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(Log.genLogMsg("Message", "Given targetVariables cannot contain null element"));
        }

        // targetVariables should be the subset of message variables
        if (!new HashSet<>(message.getVariables()).containsAll(targetVariables)) {
            throw new IllegalArgumentException(Log.genLogMsg("Message", "Given targetVariables should be the subset of message variables"));
        }

        int[] sumDimensions = Message.findIndices(message.getVariables(), targetVariables);
        INDArray newDistribution = message.getDistribution().sum(sumDimensions);
        List<Variable<?>> newVariables = message.getVariables().stream().filter(var -> !targetVariables.contains(var)).toList();
        return new Message(newDistribution, newVariables);
    }

    /**
     * Join all message together (keep doing {@link #messageProduct(Message, Message)}.
     * @param messages Array of message.
     * @return Joined message.
     * @see #messageMarginalization(Message, List)
     */
    public static Message joinMessages(final Message... messages) {
        return Message.joinMessages(Arrays.asList(messages));
    }

    /**
     * Join all message together {keep doing {@link #messageProduct(Message, Message)}}.
     * @param messages List of message.
     * @return Joined message
     * @throws NullPointerException if given list of message is null.
     * @throws IllegalArgumentException if following condition happen:
     * <ol>
     *     <li>{@code messages} is empty.</li>
     *     <li>{@code messages} contain null element</li>
     * </ol>
     */
    public static Message joinMessages(final Collection<Message> messages) {
        Objects.requireNonNull(messages, Log.genLogMsg("Message", "Given messages cannot be null"));

        // Given messages cannot contain null element
        if (messages.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(Log.genLogMsg("Message", "Given messages list cannot contain"));
        }

        // Given messages cannot be empty
        if (messages.isEmpty()) {
            throw new IllegalArgumentException(Log.genLogMsg("Message", "Give messages list cannot be empty"));
        }

        Message message = messages.stream().reduce(Message::messageProduct).orElse(null);
        if (message == null) {
            throw new RuntimeException(Log.genLogMsg("Message", "Joint result is null"));
        }
        return message;
    }

    @Override
    public int hashCode() {
        int result = this.distribution.hashCode();
        for (Variable<?> variable : this.variables) {
            result = 7 * result + variable.hashCode();
        }
        return result;
    }

    @Override
    public boolean equals(Object otherObj) {
        if (this == otherObj) return true;
        if (otherObj == null || this.getClass() != otherObj.getClass()) return false;
        return super.equals(otherObj);
    }

    @Override
    public String toString() {
        return "Message: " + this.variables;
    }
}
