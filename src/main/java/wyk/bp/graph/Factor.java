package wyk.bp.graph;

import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import wyk.bp.utils.Log;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Factor extends ProbabilityTable implements FactorGraphNode {

    protected String name;

    protected final static String DEFAULT_NAME = "Default_Name";

    protected final static String NAME_NULL_ERROR = "Given name should not be null";
    public Factor(final Variable<?>... variables) {
        this(Factor.DEFAULT_NAME, Arrays.asList(variables));
    }

    public Factor(final List<Variable<?>> variables) {
        this(Factor.DEFAULT_NAME, Nd4j.ones(variables.stream().mapToLong(Variable::getStateCount).toArray()).castTo(DataType.DOUBLE), variables);
    }

    public Factor(final INDArray distribution, final Variable<?>... variables) {
        this(Factor.DEFAULT_NAME, distribution, Arrays.asList(variables));
    }

    public Factor(final INDArray distributions, final List<Variable<?>> variables) {
        this(Factor.DEFAULT_NAME, distributions, variables);
    }


    public Factor(final String name, final Variable<?>... variables) {
        this(name, Arrays.asList(variables));
    }

    public Factor(final String name, final List<Variable<?>> variables) {
        this(name, Nd4j.ones(variables.stream().mapToLong(Variable::getStateCount).toArray()).castTo(DataType.DOUBLE), variables);
    }

    public Factor(final String name, final INDArray distribution, final Variable<?>... variables) {
        this(name, distribution, Arrays.asList(variables));
    }

    public Factor(final String name, final INDArray distribution, final List<Variable<?>> variables) {
        super(distribution, variables);
        Objects.requireNonNull(name, Log.genLogMsg(this.getClass(), Factor.NAME_NULL_ERROR));
        this.name = name;
    }

    public Factor(final Factor factor) {
        super(factor);
        this.name = factor.name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        Objects.requireNonNull(name, Log.genLogMsg(this.getClass(), Factor.NAME_NULL_ERROR));
        this.name = name;
    }

    @Override
    public int hashCode() {
        int result = this.distribution.hashCode();
        for (Variable<?> variable : this.variables) {
            result = 17 * result + variable.hashCode();
        }
        result = 17 * result + this.name.hashCode();
        return result;
    }
    @Override
    public boolean equals(Object otherObj) {
        if (this == otherObj) return true;
        if (otherObj == null || this.getClass() != otherObj.getClass()) return false;
        Factor otherFactor = (Factor) otherObj;
        if (!this.name.equals(otherFactor.name)) {
            return false;
        }
        return super.equals(otherObj);
    }

    @Override
    public String toString() {
        return this.name + " : " + this.variables.toString();
    }
}
