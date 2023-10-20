package wyk.bp.alg.propagation;

import org.jgrapht.graph.DefaultEdge;
import org.junit.jupiter.api.Test;
import org.nd4j.linalg.factory.Nd4j;
import wyk.bp.graph.Factor;
import wyk.bp.graph.FactorGraph;
import wyk.bp.graph.Message;
import wyk.bp.graph.Variable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BeliefPropagationTest {

    @Test
    void testInvalidGraph() {
        Variable<String> var1 = new Variable<>("a", 2);
        Variable<String> var2 = new Variable<>("b", 2);
        Variable<String> var3 = new Variable<>("c", 3);
        Variable<String> var4 = new Variable<>("d", 2);

        double[][] values1 = {
                {2.0d, 3.0d},
                {6.0d, 4.0d},
        };
        Factor factor1 = new Factor(Nd4j.create(values1), var1, var2);

        double[][][] values2 = {
                {{7.0d, 2.0d, 3.0d}, {1.0d, 5.0d, 2.0d}},
                {{8.0d, 3.0d, 9.0d}, {6.0d, 4.0d, 2.0d}},
        };
        Factor factor2 = new Factor(Nd4j.create(values2), var2, var4, var3);

        double[] values3 = {5.0d, 1.0d, 9.0d};
        Factor factor3 = new Factor(Nd4j.create(values3), var3);

        FactorGraph<DefaultEdge> factorGraph = new FactorGraph<>(DefaultEdge.class);

        factorGraph.addVariable(var1);
        factorGraph.addVariable(var2);
        factorGraph.addVariable(var3);
        factorGraph.addVariable(var4);
        factorGraph.addFactor(factor1);
        factorGraph.addFactor(factor2);
        factorGraph.addFactor(factor3);

        factorGraph.addEdge(var1, factor1);
        factorGraph.addEdge(var2, factor1);
        factorGraph.addEdge(factor2, var2, new DefaultEdge());
        factorGraph.addEdge(var3, factor2, new DefaultEdge());
        factorGraph.addEdge(factor2, var4);

        assertThrows(IllegalArgumentException.class, () -> new BeliefPropagation<>(factorGraph));
    }

    @Test
    void testBP1() {
        Variable<String> var1 = new Variable<>("a", 2);
        Variable<String> var2 = new Variable<>("b", 2);
        Variable<String> var3 = new Variable<>("c", 3);
        Variable<String> var4 = new Variable<>("d", 2);

        double[][] values1 = {
                {2.0d, 3.0d},
                {6.0d, 4.0d},
        };
        Factor factor1 = new Factor("f1", Nd4j.create(values1), var1, var2);

        double[][][] values2 = {
                {{7.0d, 2.0d, 3.0d}, {1.0d, 5.0d, 2.0d}},
                {{8.0d, 3.0d, 9.0d}, {6.0d, 4.0d, 2.0d}},
        };
        Factor factor2 = new Factor("f2", Nd4j.create(values2), var2, var4, var3);

        double[] values3 = {5.0d, 1.0d, 9.0d};
        Factor factor3 = new Factor("f3", Nd4j.create(values3), var3);

        FactorGraph<DefaultEdge> factorGraph = new FactorGraph<>(DefaultEdge.class);

        factorGraph.addVariable(var1);
        factorGraph.addVariable(var2);
        factorGraph.addVariable(var3);
        factorGraph.addVariable(var4);
        factorGraph.addFactor(factor1);
        factorGraph.addFactor(factor2);
        factorGraph.addFactor(factor3);

        factorGraph.addEdge(var1, factor1);
        factorGraph.addEdge(var2, factor1);
        factorGraph.addEdge(factor2, var2, new DefaultEdge());
        factorGraph.addEdge(var3, factor2, new DefaultEdge());
        factorGraph.addEdge(factor2, var4);
        factorGraph.addEdge(factor3, var3);

        BeliefPropagation<DefaultEdge> beliefPropagation = new BeliefPropagation<>(factorGraph);

        Message message_1 = beliefPropagation.getBelief(var1);
        double[] values_1 = {
                0.36178862d, 0.63821138d
        };
        Message expectedMessage_1 = new Message(Nd4j.create(values_1), var1);
        assertEquals(expectedMessage_1, message_1);

        Message message_2 = beliefPropagation.getBelief(var2);
        double[] values_2 = {
                0.37398374d, 0.62601626d
        };
        Message expectedMessage_2 = new Message(Nd4j.create(values_2), var2);
        assertEquals(expectedMessage_2, message_2);

        double[] values_3 = {
                0.41158537d, 0.05335366d, 0.53506098d
        };
        Message expectedMessage_3 = new Message(Nd4j.create(values_3), var3);
        assertEquals(expectedMessage_3, beliefPropagation.getBelief(var3));

        double[] values_4 = {
                0.70121951d, 0.29878049d
        };
        Message expectedMessage_4 = new Message(Nd4j.create(values_4), var4);
        assertEquals(expectedMessage_4, beliefPropagation.getBelief(var4));
    }
}