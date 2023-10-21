package wyk.bp.graph;

import wyk.bp.utils.Log;

/**
 * Random variable used in the {@link FactorGraph}. The number of state need to be indicated.
 * @param <T> Object type that associate with the random variable.
 */
public class Variable<T> implements FactorGraphNode {

    /**
     * Data that associate with this random variable.
     */
    protected final T data;

    /**
     * Number of state that this random variable has.
     */
    protected final int stateCount;

    /**
     * Constructor.
     * @param data Data that associate with this random variable.
     * @param stateCount Number of state this random variable has.
     */
    public Variable(T data, final int stateCount) {
        if (stateCount <= 0) {
            throw new IllegalArgumentException(Log.genLogMsg(this.getClass(), "Given stateCount cannot be less than zero, but " + stateCount + " is given"));
        }
        this.data = data;
        this.stateCount = stateCount;
    }

    /**
     * Get the data that associate with this random variable.
     * @return Data.
     */
    public T getData() {
        return this.data;
    }

    /**
     * Get the number of state.
     * @return Number of state.
     */
    public int getStateCount() {
        return this.stateCount;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Variable<?> otherVariable = (Variable<?>) obj;
        return this.data.equals(otherVariable.data) && this.stateCount == otherVariable.stateCount;
    }
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + this.data.hashCode();
        result = 31 * result + this.stateCount;
        return result;
    }

    @Override
    public String toString() {
        return "Variable: " + this.data.toString();
    }
}
