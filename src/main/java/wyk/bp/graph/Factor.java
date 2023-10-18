package wyk.bp.graph;

import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.List;

public class Factor extends ProbabilityTable implements FactorGraphNode {

    public Factor(final INDArray distribution, final Variable<?>... variables) {
        super(distribution, variables);
    }

    public Factor(final INDArray distributions, final List<Variable<?>> variables) {
        super(distributions, variables);
    }

    public Factor(final Factor factor) {
        super(factor);
    }
}
