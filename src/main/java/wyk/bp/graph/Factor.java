package wyk.bp.graph;

import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import wyk.bp.utils.Log;

public class Factor implements FactorGraphNode {
    protected final INDArray distribution;
    protected final List<Variable<?>> variables;

    public Factor(final List<Variable<?>> variables, final INDArray matrix) {
        Objects.requireNonNull(variables, "Given variables list cannot be null");
        Objects.requireNonNull(matrix, "Given matrix cannot be null");
        this.variables = variables;
        this.distribution = matrix;
    }
    public void moveAxis(int[] originDims, int[] targetDims) {
        if (originDims.length != targetDims.length) {
            throw new IllegalArgumentException("Origin and target dimension array should have same size");
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
    public List<Variable<?>> getVariables() {
        return this.variables;
    }
    public INDArray getDistribution() {
        return this.distribution;
    }
    public boolean haveSameVariable(final Factor otherFactor) {
        return this.variables.equals(otherFactor.getVariables());
    }
    @Override
    public int hashCode() {
        int result = this.distribution.hashCode();
        for (Variable<?> variable : this.variables) {
            result = 31 * result + variable.hashCode();
        }
        return result;
    }
    @Override
    public boolean equals(Object otherObj) {
        if (this == otherObj) return true;
        if (otherObj == null || this.getClass() != otherObj.getClass()) return false;
        Factor otherFactor = (Factor) otherObj;
        return this.distribution.equals(otherFactor.distribution) && this.variables.equals(otherFactor.variables);
    }

    public static Factor factorProduct(final Factor factor1, final Factor factor2) {


        return null;
    }
}
