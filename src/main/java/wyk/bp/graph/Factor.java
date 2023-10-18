package wyk.bp.graph;

import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.Arrays;
import java.util.List;

public class Factor extends ProbabilityTable implements FactorGraphNode {

    public Factor(final Variable<?>... variables) {
        this(Arrays.asList(variables));
    }

    public Factor(final List<Variable<?>> variables) {
        this(Nd4j.ones(variables.stream().mapToLong(Variable::getStateCount).toArray()).castTo(DataType.DOUBLE), variables);
    }

    public Factor(final INDArray distribution, final Variable<?>... variables) {
        super(distribution, variables);
    }

    public Factor(final INDArray distributions, final List<Variable<?>> variables) {
        super(distributions, variables);
    }

    public Factor(final Factor factor) {
        super(factor);
    }

    @Override
    public int hashCode() {
        int result = this.distribution.hashCode();
        for (Variable<?> variable : this.variables) {
            result = 17 * result + variable.hashCode();
        }
        return result;
    }
    @Override
    public boolean equals(Object otherObj) {
        if (this == otherObj) return true;
        if (otherObj == null || this.getClass() != otherObj.getClass()) return false;
        return super.equals(otherObj);
    }
}
