package wyk.bp.graph;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Factor implements FactorGraphNode {

    protected final INDArray matrix;
    protected final List<Variable<?>> variables;

    public Factor(final List<Variable<?>> variables, final INDArray matrix) {
        Objects.requireNonNull(variables, "Given variables list cannot be null");
        Objects.requireNonNull(matrix, "Given matrix cannot be null");
        this.variables = variables;
        this.matrix = matrix;
    }

    public void moveAxis(int[] originDims, int[] targetDims) {
        if (originDims.length != targetDims.length) {
            throw new IllegalArgumentException("Origin and target dimension array should have same size");
        }
        final int numDims = this.matrix.shape().length;
        if (Arrays.stream(originDims).anyMatch(originDim -> originDim < 0 || originDim > numDims)) {
            throw new IllegalArgumentException("Invalid origin dimensions specified: " + Arrays.toString(originDims));
        }
        if (Arrays.stream(targetDims).anyMatch(targetDim -> targetDim < 0 || targetDim > numDims)) {
            throw new IllegalArgumentException("Invalid target dimensions specified: " + Arrays.toString(targetDims));
        }

        int[] newOrders = new int[numDims];
        for (int idx=0; idx<targetDims.length; idx++) {
            final int targetDim = targetDims[idx];
            newOrders[targetDim] = originDims[idx];
        }
        this.matrix.permutei(newOrders);
    }
    public List<Variable<?>> getVariables() {
        return this.variables;
    }
    public INDArray getMatrix() {
        return this.matrix;
    }
    public boolean haveSameVariable(final Factor otherFactor) {
        return this.variables.equals(otherFactor.getVariables());
    }
    @Override
    public int hashCode() {
        int result = this.matrix.hashCode();
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
        return this.matrix.equals(otherFactor.matrix) && this.variables.equals(otherFactor.variables);
    }
}
