package BeliefPropagation.graph;

import BeliefPropagation.utils.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Probability table.
 * <p>
 *     A probability table is a probability distribution over a set of variables.
 *     It is a multidimensional array, where each dimension corresponds to a variable.
 *     The size of each dimension is the number of states of the corresponding variable.
 * </p>
 */
public class ProbabilityTable implements Cloneable {
    /**
     * Probability distributions array.
     */
    protected final HDArray probability;
    /**
     * List of variables considered in the distribution.
     */
    protected final List<Variable<?>> variables;

    /**
     * Constructor. Call {@link #ProbabilityTable(HDArray, List)}.
     * @param probability Probability distributions array.
     * @param variables Array of variables.
     */
    public ProbabilityTable(final HDArray probability, final Variable<?>... variables) {
        this(probability, Arrays.asList(variables));
    }

    /**
     * Copy constructor.
     * @param otherTable Another probability table.
     */
    public ProbabilityTable(final ProbabilityTable otherTable) {
        this(otherTable.probability, otherTable.variables);
    }

    /**
     * Constructor.
     * @param probability Probability distributions array.
     * @param variables List of variables.
     */
    public ProbabilityTable(final HDArray probability, final List<Variable<?>> variables) {
        Objects.requireNonNull(probability, Log.genLogMsg(
                this.getClass(), "Given probability should not be null")
        );
        Objects.requireNonNull(variables, Log.genLogMsg(this.getClass(), "Given variables should not be null"));

        // Given variables cannot be empty
        if (variables.isEmpty()) {
            throw new IllegalArgumentException(Log.genLogMsg(this.getClass(), "Given variables list is empty"));
        }

        // Given variables cannot contains null element
        if (variables.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(
                    Log.genLogMsg(this.getClass(), "Give variables list contain null element")
            );
        }

        // Number of variables and number of dimension of distribution mismatch
        if (variables.size() != probability.rank()) {
            throw new IllegalArgumentException(
                    Log.genLogMsg(this.getClass(),
                            "Number of variables and number of dimension of distribution mismatch")
            );
        }

        // Variable state and corresponding distribution dimension mismatch
        for (int i = 0; i < variables.size(); i++) {
            if (variables.get(i).getStateCount() != probability.shape[i]) {
                throw new IllegalArgumentException(
                        Log.genLogMsg(this.getClass(),
                                "Variable state and corresponding distribution dimension mismatch")
                );
            }
        }

        // Deep copy
        this.probability = probability.clone();
        this.variables = new ArrayList<>(variables);
    }

    /**
     * Get probability distribution.
     * @return Probability distribution.
     */
    public HDArray getProbability() {
        return this.probability;
    }

    /**
     * Get variable list.
     * @return Variable list.
     */
    public List<Variable<?>> getVariables() {
        return this.variables;
    }

    /**
     * Check whether this probability table and another probability table have the same variables.
     * @param otherTable Another probability table.
     * @return True if they have the same variables, false otherwise.
     */
    public boolean haveSameVariables(ProbabilityTable otherTable) {
        Objects.requireNonNull(otherTable, Log.genLogMsg(this.getClass(),
                "Given probability table should not be null"));
        return this.variables.equals(otherTable.variables);
    }

    /**
     * Check whether this probability table contains the given variable.
     * @param variable Given variable.
     * @return True if this probability table contains the given variable, false otherwise.
     */
    public boolean containsVariable(Variable<?> variable) {
        Objects.requireNonNull(variable, Log.genLogMsg(this.getClass(),
                "Given variable should not be null"));
        return this.variables.contains(variable);
    }

    @Override
    public int hashCode() {
        int result = this.probability.hashCode();
        for (Variable<?> variable : this.variables) {
            result = 17 * result + variable.hashCode();
        }
        return result;
    }

    @Override
    public boolean equals(Object otherObj) {
        if (this == otherObj) return true;
        if (otherObj == null || this.getClass() != otherObj.getClass()) return false;
        ProbabilityTable otherTable = (ProbabilityTable) otherObj;
        return this.probability.equals(otherTable.probability) && this.variables.equals(otherTable.variables);
    }

    @Override
    public String toString() {
        return "ProbabilityTable{" +
                "probability=" + probability +
                ", variables=" + variables +
                '}';
    }

    @Override
    public Object clone() {
        return new ProbabilityTable(this);
    }
}
