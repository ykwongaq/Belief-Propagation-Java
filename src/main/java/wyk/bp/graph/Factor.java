package wyk.bp.graph;

import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import wyk.bp.utils.Log;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * {@link Factor} represent the relationship between {@link Variable} in terms of conditional probability or weight.
 */
public class Factor extends ProbabilityTable implements FactorGraphNode {

    /**
     * Name of this factor. Just for readability and let people to easily identify different {@link Factor}
     */
    protected String name;

    /**
     * Default name
     */
    protected final static String DEFAULT_NAME = "Default_Name";

    protected final static String NAME_NULL_ERROR = "Given name should not be null";

    /**
     * Constructor. Default factor name is used.
     * @param distribution Probability distribution.
     * @param variables Array of variables.
     * @see #Factor(String, INDArray, List)
     */
    public Factor(final INDArray distribution, final Variable<?>... variables) {
        this(Factor.DEFAULT_NAME, distribution, Arrays.asList(variables));
    }

    /**
     * Constructor. Default factor name is used
     * @param distributions Probability distribution.
     * @param variables List of variables.
     * @see #Factor(String, INDArray, List)
     */
    public Factor(final INDArray distributions, final List<Variable<?>> variables) {
        this(Factor.DEFAULT_NAME, distributions, variables);
    }

    /**
     * Constructor.
     * @param name Factor name
     * @param distribution Probability distribution
     * @param variables Array of variables.
     * @see #Factor(String, INDArray, List)
     */
    public Factor(final String name, final INDArray distribution, final Variable<?>... variables) {
        this(name, distribution, Arrays.asList(variables));
    }

    /**
     * Constructor.
     * @param name Factor name
     * @param distribution Probability distribution
     * @param variables List of variables
     * @throws NullPointerException if given name is null
     * @see ProbabilityTable#ProbabilityTable(INDArray, List)
     */
    public Factor(final String name, final INDArray distribution, final List<Variable<?>> variables) {
        super(distribution, variables);
        Objects.requireNonNull(name, Log.genLogMsg(this.getClass(), Factor.NAME_NULL_ERROR));
        this.name = name;
    }

    /**
     * Deep copy constructor
     * @param factor Other factor
     * @see ProbabilityTable#ProbabilityTable(INDArray, List)
     */
    public Factor(final Factor factor) {
        super(factor);
        this.name = factor.name;
    }

    /**
     * Get the name of this factor.
     * @return Name of this factor.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the name of this factor.
     * @param name Name of this factor.
     */
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
