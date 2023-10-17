package wyk.bp.graph;

import org.jgrapht.graph.DefaultEdge;
import org.junit.jupiter.api.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.shade.jackson.databind.ser.std.StdKeySerializers;

import static org.junit.jupiter.api.Assertions.*;

class FactorGraphTest {

    @Test
    void testGraphConstruction() {
        Variable<String> var1 = new Variable<>("a");
        Variable<String> var2 = new Variable<>("b");
        Variable<String> var3 = new Variable<>("c");
        Variable<String> var4 = new Variable<>("d");

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

        assertThrows(IllegalArgumentException.class, () -> factorGraph.addEdge(factor1, factor2));
        assertThrows(IllegalArgumentException.class, () -> factorGraph.addEdge(var1, var2));


    }

    @Test
    void testValidGraph() {
        Variable<String> var1 = new Variable<>("a");
        Variable<String> var2 = new Variable<>("b");
        Variable<String> var3 = new Variable<>("c");
        Variable<String> var4 = new Variable<>("d");

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
        assertFalse(factorGraph.isValid());

        factorGraph.addVariable(var1);
        factorGraph.addVariable(var2);
        factorGraph.addVariable(var3);
        factorGraph.addVariable(var4);
        factorGraph.addFactor(factor1);
        factorGraph.addFactor(factor2);
        factorGraph.addFactor(factor3);

        factorGraph.addEdge(var1, factor1);
        assertFalse(factorGraph.isValid());
        factorGraph.addEdge(var2, factor1);
        factorGraph.addEdge(factor2, var2, new DefaultEdge());
        factorGraph.addEdge(var3, factor2, new DefaultEdge());
        factorGraph.addEdge(factor2, var4);
        assertFalse(factorGraph.isValid());
        factorGraph.addEdge(factor3, var3);

        assertTrue(factorGraph.isValid());
    }
}