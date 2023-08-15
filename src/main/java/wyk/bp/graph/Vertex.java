package wyk.bp.graph;

public class Vertex<T> implements BPGraphNode {

    protected final T data;

    public Vertex(T data) {
        this.data = data;
    }

    public T getData() {
        return this.data;
    }


}
