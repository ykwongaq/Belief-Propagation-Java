package wyk.bp.graph;

import wyk.bp.utils.Log;

public class Variable<T> implements FactorGraphNode {

    protected final T data;

    protected final int stateCount;

    public Variable(T data, final int stateCount) {
        if (stateCount <= 0) {
            throw new IllegalArgumentException(Log.genLogMsg(this.getClass(), "Given stateCount cannot be less than zero, but " + stateCount + " is given"));
        }
        this.data = data;
        this.stateCount = stateCount;
    }

    public T getData() {
        return this.data;
    }

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
}
