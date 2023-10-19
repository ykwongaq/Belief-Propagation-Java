package wyk.bp.utils;

import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.LongStream;

public class DistributionUtil {

    public static INDArray moveaxis(INDArray array, int[] originDimensions, int[] targetDimensions) {
        long[] shape = array.shape();
        int numDims = getNumDims(originDimensions, targetDimensions, shape);

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

    private static int getNumDims(int[] originDimensions, int[] targetDimensions, long[] shape) {
        int numDims = shape.length;

        if (originDimensions.length != targetDimensions.length) {
            throw new IllegalArgumentException("Origin and target dimension arrays must have the same length.");
        }

        for (int i = 0; i < originDimensions.length; i++) {
            int originDimension = originDimensions[i];
            int targetDimension = targetDimensions[i];

            if (originDimension < 0 || originDimension >= numDims || targetDimension < 0 || targetDimension >= numDims) {
                throw new IllegalArgumentException("Invalid dimensions specified.");
            }
        }
        return numDims;
    }
}
