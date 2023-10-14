package wyk.bp.utils;

import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.Arrays;

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
        System.out.println(Arrays.toString(newOrder));
        return array.permute(newOrder);
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
