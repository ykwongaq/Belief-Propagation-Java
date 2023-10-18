package wyk.bp.graph;

import org.nd4j.linalg.api.ndarray.INDArray;
import wyk.bp.utils.DistributionUtil;
import wyk.bp.utils.Log;

import java.util.*;
import java.util.stream.IntStream;

public class Message extends ProbabilityTable {
    public Message(final INDArray distribution, final Variable<?>... variables) {
        this(distribution, Arrays.asList(variables));
    }

    public Message(final INDArray distribution, final List<Variable<?>> variables) {
        super(distribution, variables);
    }

    public Message(final Message message) {
        super(message);
    }

    public void moveAxis(int[] originDims, int[] targetDims) {
        if (originDims.length != targetDims.length) {
            throw new IllegalArgumentException(Log.genLogMsg(this.getClass(), "Origin and target dimension array should have same size"));
        }
        final int numDims = this.distribution.shape().length;
        if (Arrays.stream(originDims).anyMatch(originDim -> originDim < 0 || originDim > numDims)) {
            throw new IllegalArgumentException(Log.genLogMsg(this.getClass(), "Invalid origin dimensions specified: " + Arrays.toString(originDims)));
        }
        if (Arrays.stream(targetDims).anyMatch(targetDim -> targetDim < 0 || targetDim > numDims)) {
            throw new IllegalArgumentException(Log.genLogMsg(this.getClass(), "Invalid target dimensions specified: " + Arrays.toString(targetDims)));
        }

        int[] newOrders = new int[numDims];
        for (int idx=0; idx<targetDims.length; idx++) {
            final int targetDim = targetDims[idx];
            newOrders[targetDim] = originDims[idx];
        }
        this.distribution.permutei(newOrders);
    }

    public void normalize() {
        this.distribution.divi(this.distribution.sumNumber());
    }

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
        distribution1 = DistributionUtil.appendDimensions(distribution1, commonVariables.size(), false);

        final int[] originDims2 = IntStream.range(0, message2.getVariables().size()).toArray();
        final int[] targetDims2 = Message.findIndices(newVariables.stream().filter(message2.getVariables()::contains).toList(), message2.getVariables());
        INDArray distribution2 = DistributionUtil.moveaxis(message2.getDistribution(), originDims2, targetDims2);
        distribution2 = DistributionUtil.appendDimensions(distribution2, commonVariables.size(), true);

        // Calculate new distribution
        final INDArray newDistribution = distribution1.mul(distribution2);

        return new Message(newDistribution, newVariables);
    }

    protected static int[] findIndices(final List<Variable<?>> variables, final List<Variable<?>> targetVariables) {
        return targetVariables.stream().mapToInt(variables::indexOf).toArray();
    }

    public static Message messageMarginalization(final Message message, final Variable<?>... targetVariables) {
        return Message.messageMarginalization(message, Arrays.asList(targetVariables));
    }

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

    public static Message joinMessages(final Message... messages) {
        return Message.joinMessages(Arrays.asList(messages));
    }

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
}
