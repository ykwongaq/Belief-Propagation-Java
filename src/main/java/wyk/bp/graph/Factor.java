package wyk.bp.graph;

import org.nd4j.linalg.api.ndarray.INDArray;
import wyk.bp.utils.DistributionUtil;
import wyk.bp.utils.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class Factor implements FactorGraphNode {
    protected final INDArray distribution;
    protected final List<Variable<?>> variables;

    public Factor(final INDArray distribution, final Variable<?>... variables) {
        this(distribution, Arrays.asList(variables));
    }

    public Factor(final INDArray matrix, final List<Variable<?>> variables) {
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
        Objects.requireNonNull(factor1, Log.genLogMsg("Factor", "factor1 cannot be null"));
        Objects.requireNonNull(factor2, Log.genLogMsg("Factor", "factor2 cannot be null"));

        // There should be at least one common variables
        if (factor1.getVariables().stream().noneMatch(var -> factor2.getVariables().contains(var))) {
            throw new IllegalArgumentException(Log.genLogMsg("Factor", "There are no common variable between two given factors"));
        }

        // Define new variables list with following order
        // [Variables only in factor1], [Common Variables], [Variables only in factor2]
        List<Variable<?>> uniqueVariables1 = factor1.getVariables().stream().filter(var -> !factor2.getVariables().contains(var)).toList();
        List<Variable<?>> commonVariables = factor1.getVariables().stream().filter(var -> factor2.getVariables().contains(var)).toList();
        List<Variable<?>> uniqueVariables2 = factor2.getVariables().stream().filter(var -> !factor1.getVariables().contains(var)).toList();

        List<Variable<?>> newVariables = new ArrayList<>();
        newVariables.addAll(uniqueVariables1);
        newVariables.addAll(commonVariables);
        newVariables.addAll(uniqueVariables2);

        // Adjust dimensions
        final int[] originDims1 = IntStream.range(0, factor1.getVariables().size()).toArray();
        final int[] targetDims1 = Factor.findIndices(newVariables, factor1.getVariables());
        INDArray distribution1 = DistributionUtil.moveaxis(factor1.getDistribution(), originDims1, targetDims1);
        distribution1 = DistributionUtil.appendDimensions(distribution1, commonVariables.size(), false);

        final int[] originDims2 = IntStream.range(0, factor2.getVariables().size()).toArray();
        final int[] targetDims2 = Factor.findIndices(newVariables, factor2.getVariables());
        INDArray distribution2 = DistributionUtil.moveaxis(factor2.getDistribution(), originDims2, targetDims2);
        distribution2 = DistributionUtil.appendDimensions(distribution2, commonVariables.size(), true);

        // Calculate new distribution
        final INDArray newDistribution = distribution1.mul(distribution2);

        return new Factor(newDistribution, newVariables);
    }

    protected static int[] findIndices(final List<Variable<?>> variables, final List<Variable<?>> targetVariables) {
        List<Variable<?>> filteredVariables = variables.stream().filter(targetVariables::contains).toList();
        return targetVariables.stream().mapToInt(filteredVariables::indexOf).toArray();
    }
}
