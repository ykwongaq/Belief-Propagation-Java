package wyk.bp.graph;

import wyk.bp.utils.Log;

import java.util.List;
import java.util.Objects;

/**
 * Factor.
 * <p>
 *     A factor is a probability distribution over a set of variables.
 *     It is a multidimensional array, where each dimension corresponds to a variable.
 *     The size of each dimension is the number of states of the corresponding variable.
 * </p>
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

    /**
     * Constructor.
     * @param name Factor name
     * @param probability Probability distribution
     * @param variables List of variables.
     */
    public Factor(final String name, final HDArray probability, final List<Variable<?>> variables) {
        super(probability, variables);
        Objects.requireNonNull(name, Log.genLogMsg(this.getClass(), "Given name should not be null"));
        this.name = name;
    }

    /**
     * Constructor.
     * @param name Factor name
     * @param probability Probability distribution
     * @param variables Array of variables.
     */
    public Factor(final String name, final HDArray probability, final Variable<?>... variables) {
        this(name, probability, List.of(variables));
    }

    /**
     * Constructor. Default factor name is used.
     * @param probability Probability distribution.
     * @param variables Array of variables.
     */
    public Factor(final HDArray probability, final Variable<?>... variables) {
        this(Factor.DEFAULT_NAME, probability, variables);
    }

    /**
     * Constructor. Default factor name is used
     * @param probability Probability distribution.
     * @param variables List of variables.
     */
    public Factor(final HDArray probability, final List<Variable<?>> variables) {
        this(Factor.DEFAULT_NAME, probability, variables);
    }

    /**
     * Copy constructor.
     * @param otherFactor Another factor.
     */
    public Factor(final Factor otherFactor) {
        this(otherFactor.name, otherFactor.probability, otherFactor.variables);
    }

    /**
     * Get name of this factor.
     * @return Name of this factor.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set name of this factor.
     * @param name New name.
     */
    public void setName(final String name) {
        Objects.requireNonNull(name, Log.genLogMsg(this.getClass(), "Given name should not be null"));
        this.name = name;
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
    public int hashCode() {
        int result = this.probability.hashCode();
        for (Variable<?> variable : this.variables) {
            result = 17 * result + variable.hashCode();
        }
        result = 17 * result + this.name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return this.name + " : " + this.variables.toString();
    }
}
