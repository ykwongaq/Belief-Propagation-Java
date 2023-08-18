package wyk.bp.graph;

public class Variable<T> implements FactorGraphNode {

    protected final T data;

    public Variable(T data) {
        this.data = data;
    }

    public T getData() {
        return this.data;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Variable<?> otherVariable = (Variable<?>) obj;
        return this.data.equals(otherVariable.data);
    }
    @Override
    public int hashCode() {
        return this.data.hashCode();
    }
}
