package wyk.bp;

import org.jgrapht.graph.DefaultEdge;
import org.nd4j.linalg.factory.Nd4j;
import wyk.bp.alg.propagation.BeliefPropagation;
import wyk.bp.graph.Factor;
import wyk.bp.graph.FactorGraph;
import wyk.bp.graph.Message;
import wyk.bp.graph.Variable;

public class Main {
    public static void main(String[] args) {

        // Create an empty factor graph
        FactorGraph<DefaultEdge> factorGraph = new FactorGraph<>(DefaultEdge.class);

        // Specific the random variables and their corresponding number of states
        Variable<String> a = new Variable<>("a", 2);
        Variable<String> b = new Variable<>("b", 2);
        Variable<String> c = new Variable<>("c", 3);
        Variable<String> d = new Variable<>("d", 2);

        // Add random variable into graph
        factorGraph.addVariable(a);
        factorGraph.addVariable(b);
        factorGraph.addVariable(c);
        factorGraph.addVariable(d);

        // Create factor 1
        double[][] distribution1 = {
                {2.0d, 3.0d},
                {6.0d, 4.0d},
        };
        Factor factor1 = new Factor("f1", Nd4j.create(distribution1), a, b);

        // Create factor 2
        double[][][] distribution2 = {
                {{7.0d, 2.0d, 3.0d}, {1.0d, 5.0d, 2.0d}},
                {{8.0d, 3.0d, 9.0d}, {6.0d, 4.0d, 2.0d}},
        };
        Factor factor2 = new Factor("f2", Nd4j.create(distribution2), b, d, c);

        // Create factor 3
        double[] distribution3 = {5.0d, 1.0d, 9.0d};
        Factor factor3 = new Factor("f3", Nd4j.create(distribution3), c);

        // Add factors into graph
        factorGraph.addFactor(factor1);
        factorGraph.addFactor(factor2);
        factorGraph.addFactor(factor3);

        // Automatically link edges between related random variables and factors
        factorGraph.fillEdges();

        // Prepare belief propagation
        BeliefPropagation<DefaultEdge> beliefPropagation = new BeliefPropagation<>(factorGraph);
        Message message = beliefPropagation.getBelief(b);

        System.out.println("Probability for b_1: " + message.getDistribution().getDouble(0));
        System.out.println("Probability for b_2: " + message.getDistribution().getDouble(1));
    }
}
