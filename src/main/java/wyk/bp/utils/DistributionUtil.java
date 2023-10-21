package wyk.bp.utils;

import org.nd4j.common.util.ArrayUtil;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.LongStream;

/**
 * Utility class to handle distribution.
 *
 * @author WYK
 */
public class DistributionUtil {

    /**
     * Move axis of an array to new position. <br/>
     *
     * Suppose {@code array} is an array with shape {@code [2, 3, 4]}.
     * <pre>
     *     array2 = moveaxis(array, {0, 1, 2}, {0, 2, 1})
     * </pre>
     * Then {@code array2} should have the shape {@code [2, 4, 3]}.<br/>
     *
     * This function is trying to mimic numpy function {@code np.moveaxis}. <br/>
     *
     * Also note that negative position is not allowed.
     *
     * @param array Source distribution
     * @param originDimensions Original positions of the axes to move. These must be unique
     * @param targetDimensions Destination positions for each of the original axis. These mush also be unique
     * @return Array with moved axes.
     * @throws java.lang.NullPointerException if given array is null.
     * @throws java.lang.IllegalArgumentException if originDimensions or targetDimensions contain duplicated position,
     * or number of position mismatch, or dimensions contain invalid position (smaller than 0 or exceed array limit).
     */
    public static INDArray moveaxis(INDArray array, int[] originDimensions, int[] targetDimensions) {
        Objects.requireNonNull(array, Log.genLogMsg("DistributionUtil", "Given array should not be null"));

        // Check is there only unique element
        if (originDimensions.length != Arrays.stream(originDimensions).distinct().count()) {
            throw new IllegalArgumentException(Log.genLogMsg("DistributionUtil", "Given originDimensions should be unique. But given " + Arrays.toString(originDimensions)));
        }
        if (targetDimensions.length != Arrays.stream(targetDimensions).distinct().count()) {
            throw new IllegalArgumentException(Log.genLogMsg("DistributionUtil", "Given targetDimensions should be unique. But given " + Arrays.toString(targetDimensions)));
        }

        // Number of position of originDimensions and targetDimensions should be the same
        if (originDimensions.length != targetDimensions.length) {
            throw new IllegalArgumentException(Log.genLogMsg("DistributionUtil", "Origin and target dimension arrays must have the same length."));
        }

        int numDims = array.shape().length;

        // Check invalid element in dimensions
        for (int i = 0; i < originDimensions.length; i++) {
            int originDimension = originDimensions[i];
            int targetDimension = targetDimensions[i];

            if (originDimension < 0 || originDimension >= numDims) {
                throw new IllegalArgumentException(Log.genLogMsg("DistributionUtil", "Invalid dimensions specified in originDimensions: " + originDimension));
            }

            if (targetDimension < 0 || targetDimension >= numDims) {
                throw new IllegalArgumentException(Log.genLogMsg("DistributionUtil", "Invalid dimension specified in targetDimensions: " + targetDimension));
            }
        }

        // Constructing new dimensions
        int[] newOrder = new int[numDims];
        for (int i = 0; i < numDims; i++) {
            int newDim = i;
            for (int j = 0; j < originDimensions.length; j++) {
                if (i == targetDimensions[j]) {
                    newDim = originDimensions[j];
                    break;
                }
            }
            newOrder[i] = newDim;
        }
        return array.permute(newOrder);
    }

    /**
     * Append dimension to the input array.
     * <pre>
     *     INDArray array = ... // shape = [2, 2]
     *     // Append two dimension to the front
     *     INDArray array2 = DistributionUtil.appendDimensions(array, 2, true); // array2 shape = [1, 1, 2, 2]
     * </pre>
     * @param inputArray Input array.
     * @param dimensionsToAppend Number of dimension to append.
     * @param atBeginning {@code True} to append dimension at front. Or else, append to the end.
     * @return Array with appended dimensions
     * @throws NullPointerException if {@code inputArray} is null
     * @throws IllegalArgumentException if {@code dimensionsToAppend} is invalid (smaller than 0).
     */
    public static INDArray appendDimensions(final INDArray inputArray, int dimensionsToAppend, boolean atBeginning) {
        Objects.requireNonNull(inputArray, Log.genLogMsg("DistributionUtil", "Given array should not be null"));
        if (dimensionsToAppend < 0) {
            throw new IllegalArgumentException(Log.genLogMsg("DistributionUtil", "Given dimensionsToAppend should not be less than one. But " + dimensionsToAppend + " is given"));
        }

        long[] currentShape = inputArray.shape();
        long[] newShape = null;
        if (atBeginning) {
            newShape = LongStream.concat(LongStream.generate(() -> 1).limit(dimensionsToAppend), Arrays.stream(currentShape)).toArray();
        } else {
            newShape = LongStream.concat(Arrays.stream(currentShape), LongStream.generate(() -> 1).limit(dimensionsToAppend)).toArray();
        }

        return inputArray.reshape(newShape);
    }

    public static INDArray create(float[] data) {
        return Nd4j.create(data);
    }
    public static INDArray create(double[] data) {
        return Nd4j.create(data);
    }

    public static INDArray create(float[][] data) {
        return Nd4j.create(data);
    }

    public static INDArray create(double[][] data) {
        return Nd4j.create(data);
    }

    public static INDArray create(double[][][] data) {
        return Nd4j.create(data);
    }

    public static INDArray create(float[][][] data) {
        return Nd4j.create(data);
    }

    public static INDArray create(double[][][][] data) {
        return Nd4j.create(data);
    }

    public static INDArray create(float[][][][] data) {
        return Nd4j.create(data);
    }
}
