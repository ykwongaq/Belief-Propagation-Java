package wyk.bp.graph;

import org.nd4j.linalg.api.ndarray.INDArray;
import wyk.bp.utils.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class ProbabilityTable {

    protected final INDArray distribution;
    protected final List<Variable<?>> variables;


    public ProbabilityTable(final INDArray distribution, final Variable<?>... variables) {
        this(distribution, Arrays.asList(variables));
    }

    public ProbabilityTable(final ProbabilityTable table) {
        this(table.distribution, table.variables);
    }

    public ProbabilityTable(final INDArray distribution, final List<Variable<?>> variables) {
        Objects.requireNonNull(variables, Log.genLogMsg(this.getClass(), "Given variables list cannot be null"));
        Objects.requireNonNull(distribution, Log.genLogMsg(this.getClass(), "Given distribution cannot be null"));

        // Given list cannot be empty
        if (variables.isEmpty()) {
            throw new IllegalArgumentException(Log.genLogMsg(this.getClass(), "Given variables list is empty"));
        }

        // Given list cannot contains null element
        if (variables.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(Log.genLogMsg(this.getClass(), "Give variables list contain null element"));
        }

        if (variables.size() != distribution.rank()) {
            throw new IllegalArgumentException(Log.genLogMsg(this.getClass(), "Given variables list size does not match with number of dimension of distribution: Number of variables: " + variables.size() + " Number of dimension: " + distribution.rank()));
        }
        this.variables = new ArrayList<>();
        this.variables.addAll(variables);
        this.distribution = distribution.dup();
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

    public boolean contains(final Variable<?> variable) {
        return this.variables.contains(variable);
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
        ProbabilityTable otherFactor = (ProbabilityTable) otherObj;
        return this.distribution.equals(otherFactor.distribution) && this.variables.equals(otherFactor.variables);
    }
}
