package BeliefPropagation.alg.propagation;

import BeliefPropagation.graph.*;
import org.jgrapht.graph.DefaultEdge;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoopyBeliefPropagationTest {

    @Test
    void testLoopyBeliefPropagationWithNull() {
        assertThrows(NullPointerException.class, () -> new LoopyBeliefPropagation<>(null, 3));
    }

    @Test
    void testLoopyBeliefPropagationWithInvalidIteration() {
        assertThrows(IllegalArgumentException.class, () -> new LoopyBeliefPropagation<>(new FactorGraph<>(DefaultEdge.class), -1));
    }

    @Test
    void testLoopyBeliefPropagation() {
        Variable<String> a = new Variable<>("a", 2);
        Variable<String> b = new Variable<>("b", 2);
        Variable<String> c = new Variable<>("c", 3);

        double[][] distribution_1 = {
                {2.0d, 3.0d}, {6.0d, 4.0d}
        };
        Factor factor1 = new Factor("f1", HDArray.create(distribution_1), a, b);

        double[][] distribution_2 = {
                {7.0d, 2.0d, 3.0d}, {1.0d, 5.0d, 2.0d}
        };
        Factor factor2 = new Factor("f2", HDArray.create(distribution_2), a, c);

        double[][] distribution_3 = {
                {7.0d, 9.0d, 3.0d}, {6.0d, 4.0d, 2.0d}
        };
        Factor factor3 = new Factor("f3", HDArray.create(distribution_3), b, c);

        FactorGraph<DefaultEdge> factorGraph = new FactorGraph<>(DefaultEdge.class);
        factorGraph.addVariable(a);
        factorGraph.addVariable(b);
        factorGraph.addVariable(c);
        factorGraph.addFactor(factor1);
        factorGraph.addFactor(factor2);
        factorGraph.addFactor(factor3);

        factorGraph.addEdge(a, factor1);
        factorGraph.addEdge(b, factor1);
        factorGraph.addEdge(a, factor2);
        factorGraph.addEdge(c, factor2);
        factorGraph.addEdge(b, factor3);
        factorGraph.addEdge(c, factor3);

        LoopyBeliefPropagation<DefaultEdge> loopyBeliefPropagation = new LoopyBeliefPropagation<>(factorGraph, 5);

        double[] expectedDistribution1 = {
                0.40469259d, 0.59530741d
        };
        Message expectedMessage1 = new Message(HDArray.create(expectedDistribution1), a);
        Assertions.assertEquals(expectedMessage1, loopyBeliefPropagation.getBelief(a));

        double[] expectedDistribution2 = {
                0.6366986d, 0.3633014d
        };
        Message expectedMessage2 = new Message(HDArray.create(expectedDistribution2), b);
        Assertions.assertEquals(expectedMessage2, loopyBeliefPropagation.getBelief(b));

        double[] expectedDistribution3 = {
                0.36615434d, 0.52037182d, 0.11347384d
        };
        Message expectedMessage3 = new Message(HDArray.create(expectedDistribution3), c);
        Assertions.assertEquals(expectedMessage3, loopyBeliefPropagation.getBelief(c));
    }
}