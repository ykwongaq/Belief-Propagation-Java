package wyk.bp.graph;

import org.jgrapht.Graphs;
import org.jgrapht.graph.Pseudograph;
import wyk.bp.utils.Log;

import java.util.HashSet;
import java.util.List;
import java.io.Serial;
import java.util.Set;

/**
 * Factor is one of the {@link Pseudograph} while adjacent graph node can only be {@link Factor} or {@link Variable}.
 * In other word, we do not allow edge between two {@link Variable} or two {@link Factor}.
 * @param <E> Edge type. You may directly use {@link org.jgrapht.graph.DefaultEdge}.
 */
public class FactorGraph<E> extends Pseudograph<FactorGraphNode, E> {
    private static final String SAME_TYPE_CONNECTION_ERROR_MSG = "Cannot connect two vertex with same type 1. Vertex and Vertex or 2. Factor and Factor";
    @Serial
    private static final long serialVersionUID = -7574564204896552580L;

    /**
     * Constructor.
     * @param edgeClass Class of the edge.
     * @see Pseudograph#Pseudograph(Class)
     */
    public FactorGraph(Class<? extends E> edgeClass) {
        super(edgeClass);
    }

    /**
     * Add edge between {@link Variable} and {@link Factor}.
     * @param factor Factor.
     * @param variable Random Variable.
     * @return The newly created edge if added to the graph, otherwise {@code null}.
     * @throws IllegalArgumentException if the given {@code variable} is not contained by {@code factor}.
     */
    public E addEdge(final Factor factor, final Variable<?> variable) {
        if (!factor.containsVariable(variable)) {
            throw new IllegalArgumentException(Log.genLogMsg(this.getClass(), "Trying to add edge between unrelated variables. Factor variables: " + factor.getVariables() + " Variable: " + variable));
        }
        return super.addEdge(factor, variable);
    }


    /**
     * Add edge between {@link Variable} and {@link Factor}. Same as {@link #addEdge(Factor, Variable)}.
     * @param variable Variable.
     * @param factor Factor
     * @return The newly created edge if added to the graph, otherwise {@code null}.
     * @see #addEdge(Factor, Variable)
     */
    public E addEdge(final Variable<?> variable, final Factor factor) {
        return this.addEdge(factor, variable);
    }

    /**
     * Add edge between {@link Factor} and {@link Variable}.
     * @param factor Factor.
     * @param variable Random variable.
     * @param edge Edge between them.
     * @return {@code True} if this graph did not already contain the specified edge.
     * @throws IllegalArgumentException if the given {@code factor} does not contain the {@code variable}.
     */
    public boolean addEdge(final Factor factor, final Variable<?> variable, final E edge) {
        if (!factor.containsVariable(variable)) {
            throw new IllegalArgumentException(Log.genLogMsg(this.getClass(), "Trying to add edge between unrelated variables. Factor variables: " + factor.getVariables() + " Variable: " + variable));
        }
        return super.addEdge(factor, variable, edge);
    }

    /**
     * Add edge between {@link Factor} and {@link Variable}. Same as {@link #addEdge(Factor, Variable, Object)}.
     * @param factor Factor.
     * @param variable Random variable.
     * @param edge The newly created edge if added to the graph, otherwise {@code null}.
     * @return {@code True} if this graph did not already contain the specified edge.
     * @see #addEdge(Factor, Variable, Object)
     */
    public boolean addEdge(final Variable<?> variable, final Factor factor, final E edge) {
        return this.addEdge(factor, variable, edge);
    }

    /**
     * Add edge between given {@link FactorGraphNode}.
     * @param sourceVertex Vertex 1.
     * @param targetVertex Vertex 2.
     * @return The newly created edge if added to the graph, otherwise {@code null}.
     * @throws IllegalArgumentException if the edge is invalid (no edge between two {@link Factor} or two {@link Variable}.
     * @see #addEdge(Factor, Variable)
     */
    @Override
    public E addEdge(FactorGraphNode sourceVertex, FactorGraphNode targetVertex) {
        if (!this.isValidEdge(sourceVertex, targetVertex)) {
            throw new IllegalArgumentException(FactorGraph.SAME_TYPE_CONNECTION_ERROR_MSG);
        }
        Factor factor = this.castFactor(sourceVertex, targetVertex);
        Variable<?> variable = this.castVariable(sourceVertex, targetVertex);
        return this.addEdge(factor, variable);
    }

    /**
     * Add edge between given {@link FactorGraphNode}.
     * @param sourceVertex Vertex 1.
     * @param targetVertex Vertex 2.
     * @param e Edge between them.
     * @return {@code True} if this graph did not already contain the specified edge.
     * @throws IllegalArgumentException if the edge is invalid (no edge between two {@link Factor} or two {@link Variable}.
     * @see #addEdge(Factor, Variable)
     */
    @Override
    public boolean addEdge(FactorGraphNode sourceVertex, FactorGraphNode targetVertex, E e) {
        if (!this.isValidEdge(sourceVertex, targetVertex)) {
            throw new IllegalArgumentException(FactorGraph.SAME_TYPE_CONNECTION_ERROR_MSG);
        }
        Factor factor = this.castFactor(sourceVertex, targetVertex);
        Variable<?> variable = this.castVariable(sourceVertex, targetVertex);
        return this.addEdge(factor, variable, e);
    }

    /**
     * Add random variable into the graph.
     * @param variable Variable to add.
     * @return {@code True} if this graph did not already contain the specified variable.
     */
    public boolean addVariable(final Variable<?> variable) {
        return super.addVertex(variable);
    }

    /**
     * Add factor into the graph.
     * @param factor Factor to add.
     * @return {@code True} if this graph did not already contain the specified variable.
     */
    public boolean addFactor(final Factor factor) {
        return super.addVertex(factor);
    }

    /**
     * Check an edge can be added between two {@link FactorGraphNode}.
     * It is valid only if the edge is between {@link Factor} and {@link Variable}.
     * @param sourceVertex Node 1.
     * @param targetVertex Node 2.
     * @return {@code True} if the edge is valid to add.
     */
    protected boolean isValidEdge(final FactorGraphNode sourceVertex, FactorGraphNode targetVertex) {
        return (sourceVertex instanceof Factor && targetVertex instanceof Variable<?>) || (sourceVertex instanceof Variable<?> && targetVertex instanceof Factor);
    }

    /**
     * Cast {@link FactorGraphNode} into {@link Factor}. We assume only one of the node is {@link Factor}
     * while another one is {@link Variable}.
     * @param node1 Node 1.
     * @param node2 Node 2.
     * @return Cased {@code factor}.
     */
    protected Factor castFactor(final FactorGraphNode node1, final FactorGraphNode node2) {
        return node1 instanceof Factor ? (Factor) node1 : (Factor) node2;
    }

    /**
     * Cast {@link FactorGraphNode} into {@link Variable}. We assume only one of the node is {@link Variable}
     * while another one is {@link Factor}.
     * @param node1 Node 1.
     * @param node2 Node 2.
     * @return Cased {@code variable}.
     */
    protected Variable<?> castVariable(final FactorGraphNode node1, final FactorGraphNode node2) {
        return node1 instanceof Variable<?> ? (Variable<?>) node1 : (Variable<?>) node2;
    }

    /**
     * Check if this graph a valid {@link FactorGraph} or not. <br/>
     * Valid {@link FactorGraph} should:
     * <ul>
     *     <li>Contain at least one {@link Factor}</li>
     *     <li>Every {@link Factor} is connected to related {@link Variable}. You can check all related
     *     variable by {@link Factor#getVariables()}</li>
     * </ul>
     * @return {@code True} if this graph is valid.
     */
    public boolean isValid() {
        List<Factor> factors = this.vertexSet().stream().filter(vertex -> vertex instanceof Factor).map(vertex -> (Factor) vertex).toList();
        if (factors.isEmpty()) {
            return false;
        }
        for (Factor factor : factors) {
            for (Variable<?> variable : factor.getVariables()) {
                if (!this.containsEdge(factor, variable)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Find out what are the related variables of {@code variable} via {@code factor}. In other word, when
     * computing the message from {@code factor} to {@code variable}, what other variable needed to considered.
     * @param factor Reference factor.
     * @param variable Reference random variable.
     * @return List of related variables.
     */
    public List<? extends Variable<?>> getIncomingVariables(final Factor factor, final Variable<?> variable) {
        return Graphs.neighborListOf(this, factor).stream()
                .filter(neighbor -> !neighbor.equals(variable))
                .map(neighbor -> (Variable<?>) neighbor)
                .toList();
    }

    /**
     * Find out what are the related factor of given {@code factor} via {@code variable}. In other word, when
     * computing the message from {@code variable} to {@code factor}, what other factor needed to considered.
     * @param variable Reference variable.
     * @param factor Reference factor.
     * @return List of related factors.
     */
    public List<Factor> getIncomingFactors(final Variable<?> variable, final Factor factor) {
        return Graphs.neighborListOf(this, variable).stream().
                filter(neighbor -> !neighbor.equals(factor))
                .map(neighbor -> (Factor) neighbor)
                .toList();
    }

    /**
     * Get the set of all random variable in this graph.
     * @return Set of all random variable.
     */
    public Set<Variable<?>> variableSet() {
        return new HashSet<>(this.vertexSet().stream()
                .filter(vertex -> vertex instanceof Variable<?>)
                .map(variable -> (Variable<?>) variable)
                .toList());
    }

    /**
     * Get the set of factor in this graph.
     * @return Set of all factor.
     */
    public Set<Factor> factorSet() {
        return new HashSet<>(this.vertexSet().stream()
                .filter(vertex -> vertex instanceof Factor)
                .map(factor -> (Factor) factor)
                .toList());
    }

    /**
     * Automatically fill the required edges based on the given factors. Based on the existing factors, this method
     * will automatically add edges based on the related variables.
     * @throws RuntimeException if there are missing variables.
     */
    public void fillEdges() {
        Set<Variable<?>> requiredVariables = new HashSet<>(this.factorSet().stream()
                        .flatMap(factor -> factor.getVariables().stream())
                        .toList());
        Set<Variable<?>> variables = this.variableSet();
        if (!variables.containsAll(requiredVariables)) {
            Set<Variable<?>> missingVariables = new HashSet<>(requiredVariables.stream()
                    .filter(variable -> !variables.contains(variable))
                    .toList());
            throw new RuntimeException(Log.genLogMsg(this.getClass(),
                    "There are missing variables: " + missingVariables));
        }
        for (Factor factor  : this.factorSet()) {
            for (Variable<?> variable : factor.getVariables()) {
                this.addEdge(factor, variable);
            }
        }
    }
}
