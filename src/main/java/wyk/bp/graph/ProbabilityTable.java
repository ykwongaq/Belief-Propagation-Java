package wyk.bp.graph;

import org.nd4j.linalg.api.ndarray.INDArray;
import wyk.bp.utils.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * ProbabilityTable represents a probability distribution, which is a multidimensional {@link INDArray}. Each axis corresponds
 * to a particular {@link Variable}.<br/>
 *
 * The number of dimension always match with the number of variables. <br/>
 *
 * Example of Probability Table:
 * <table border="1">
 *     <caption>caption</caption>
 *     <tr>
 *         <td></td>
 *         <td>b<sub>1</sub></td>
 *         <td>b<sub>2</sub></td>
 *     </tr>
 *     <tr>
 *         <td>a<sub>1</sub></td>
 *         <td>0.2</td>
 *         <td>0.2</td>
 *     </tr>
 *     <tr>
 *         <td>a<sub>2</sub></td>
 *         <td>0.3</td>
 *         <td>0.3</td>
 *     </tr>
 * </table>
 *
 * The above table show two variable a and b, where both of them have two states. Every entry show the probability
 * for each state to appear. For example, the probability for (a<sub>1</sub>, b<sub>1</sub>) is 0.2.
 *
 * @author WYK
 */
public abstract class ProbabilityTable {

    /**
     * Probability distribution of the corresponding variables.
     */
    protected final INDArray distribution;
    /**
     * List of variables considered in the distribution.
     */
    protected final List<Variable<?>> variables;


    /**
     * Constructor. Call {@link #ProbabilityTable(INDArray, List)}.
     * @param distribution Probability distributions array.
     * @param variables Array of variables.
     * @see #ProbabilityTable(INDArray, List)
     */
    public ProbabilityTable(final INDArray distribution, final Variable<?>... variables) {
        this(distribution, Arrays.asList(variables));
    }

    /**
     * Deep Copy Constructor
     * @param table Other probability table
     */
    public ProbabilityTable(final ProbabilityTable table) {
        this(table.distribution, table.variables);
    }

    /**
     * Constructor.
     * @param distribution Probability Distribution array.
     * @param variables List of variables.
     * @throws NullPointerException if given {@code distribution} or {@code variables} is null.
     * @throws IllegalArgumentException if the following condition happen:
     * <ol>
     *     <li>Variable list is empty</li>
     *     <li>Variable list contain null element</li>
     *     <li>Number of variables and number of dimension of distribution mismatch</li>
     *     <li>Variable state and corresponding distribution dimension mismatch</li>
     * </ol>
     */
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

        long[] dimensions = distribution.shape();
        for (int idx=0; idx <variables.size(); idx++) {
            if (dimensions[idx] != variables.get(idx).getStateCount()) {
                throw new IllegalArgumentException(Log.genLogMsg(this.getClass(), "Mismatch between dimension and variable state count. Dimension: " + dimensions[idx] + ", Variable state count: " + variables.get(idx).getStateCount()));
            }
        }
        this.variables = new ArrayList<>();
        this.variables.addAll(variables);
        this.distribution = distribution.dup();
    }

    /**
     * Get variable list.
     * @return Variable list
     */
    public List<Variable<?>> getVariables() {
        return this.variables;
    }

    /**
     * Get distribution
     * @return Distribution
     */
    public INDArray getDistribution() {
        return this.distribution;
    }

    /**
     * Check is other {@link ProbabilityTable} contain the same variables.
     * @param otherTable Other {@link ProbabilityTable}.
     * @return {@code True} if they contain the same variables. Otherwise, {@code False}.
     * @throws NullPointerException if given {@code otherTable} is null.
     */
    public boolean haveSameVariable(final Factor otherTable) {
        Objects.requireNonNull(otherTable, Log.genLogMsg(this.getClass(), "Given other table should not be null"));
        return this.variables.equals(otherTable.getVariables());
    }

    /**
     * Check is this {@link ProbabilityTable} contain given {@code variable} or not.
     * @param variable Target variable
     * @return {@code True} if contain. Otherwise {@code False}.
     */
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
