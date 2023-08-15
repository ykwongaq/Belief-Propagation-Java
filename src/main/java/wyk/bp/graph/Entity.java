package wyk.bp.graph;

public class Entity<T> implements FactorGraphNode {

    protected final T data;

    public Entity(T data) {
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
        Entity<?> otherEntity = (Entity<?>) obj;
        return this.data.equals(otherEntity.data);
    }

    @Override
    public int hashCode() {
        return this.data.hashCode();
    }
}
