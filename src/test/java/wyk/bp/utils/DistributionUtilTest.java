package wyk.bp.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import static org.junit.jupiter.api.Assertions.*;

class DistributionUtilTest {
    @Test
    void testMoveAxis() {
        double[][][] values = {
                {{0.2, 0.3, 0.3}, {0.9, 0.1, 0.0}}, {{0.5, 0.2, 0.8}, {0.2, 0.0, 0.1}}
        };
        INDArray matrix = Nd4j.create(values);
        int[] originDims = {0, 1, 2};
        int[] targetDims = {2, 0, 1};

        INDArray result = DistributionUtil.moveaxis(matrix, originDims, targetDims);

        double[][][] expectedValues = {
                {{0.2, 0.5}, {0.3, 0.2}, {0.3, 0.8}}, {{0.9, 0.2}, {0.1, 0.0}, {0.0, 0.1}}
        };
        INDArray expectedMatrix = Nd4j.create(expectedValues);

        assertEquals(expectedMatrix, result);
    }
}