package wyk.bp.graph;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FactorTest {

    protected static INDArray distribution1;
    protected static List<Variable<?>> variables1;
    protected static INDArray distribution2;
    protected static List<Variable<?>> variables2;

    protected static INDArray distribution3;

    @BeforeAll
    static void initTestCase() {
        FactorTest.distribution1 = FactorTest.initDistribution1();
        FactorTest.variables1 = FactorTest.initVariables1();
        FactorTest.distribution2 = FactorTest.initDistribution2();
        FactorTest.variables2 = FactorTest.initVariables2();
        FactorTest.distribution3 = FactorTest.initDistribution3();
    }
    static INDArray initDistribution1() {
        double[][][] values = {
                {{0.2, 0.5}, {0.6, 0.5}}, {{1.0, 0.6}, {0.2, 0.3}}
        };
        return Nd4j.create(values);
    }
    static List<Variable<?>> initVariables1() {
        List<Variable<?>> variables1 = new ArrayList<>();
        Variable<String> var1 = new Variable<>("a");
        Variable<String> var2 = new Variable<>("b");
        Variable<String> var3 = new Variable<>("d");
        variables1.add(var1);
        variables1.add(var2);
        variables1.add(var3);
        return variables1;
    }

    static INDArray initDistribution2() {
        double[][] values = {
                {0.5, 0.7}, {0.1, 0.2}
        };
        return Nd4j.create(values);
    }

    static List<Variable<?>> initVariables2() {
        List<Variable<?>> variables1 = new ArrayList<>();
        Variable<String> var1 = new Variable<>("b");
        Variable<String> var2 = new Variable<>("c");
        variables1.add(var1);
        variables1.add(var2);
        return variables1;
    }

    static INDArray initDistribution3() {
        double[][][] values = {
                {{0.2, 0.5}, {0.6, 0.4}}, {{1.0, 0.6}, {0.2, 0.3}}
        };
        return Nd4j.create(values);
    }
    @Test
    void testConstructorWithNullArgument() {
        assertThrows(NullPointerException.class, () -> new Factor(null, FactorTest.variables1));
        assertThrows(NullPointerException.class, () -> new Factor(FactorTest.distribution1, (List<Variable<?>>) null));
    }

    @Test
    void testConstructorWithEmptyVariables() {
        assertThrows(IllegalArgumentException.class, () -> new Factor(FactorTest.distribution1, new ArrayList<Variable<?>>()));
    }

    @Test
    void testConstructorWithNullElement() {
        List<Variable<?>> variables = new ArrayList<>();
        Variable<String> a = new Variable<>("a");
        Variable<String> b = new Variable<>("b");
        variables.add(a);
        variables.add(null);
        variables.add(b);

        assertThrows(IllegalArgumentException.class, () -> new Factor(FactorTest.distribution1, variables));
    }

    @Test
    void testConstructorWithMismatchRank() {
        Variable<String> a = new Variable<>("a");
        Variable<String> b = new Variable<>("b");
        assertThrows(IllegalArgumentException.class, () -> new Factor(FactorTest.distribution1, a, b));
    }

    @Test
    void testEquals() {
        Factor factor1 = new Factor(FactorTest.distribution1, FactorTest.variables1);
        Factor factor2 = new Factor(FactorTest.distribution2, FactorTest.variables2);
        Factor factor3 = new Factor(FactorTest.distribution3, FactorTest.variables1);
        Factor factor4 = new Factor(FactorTest.distribution1, FactorTest.variables1);

        assertEquals(factor1, factor4);
        assertNotEquals(factor1, factor2);
        assertNotEquals(factor1, factor3);
    }

    @Test
    void testSameVariables() {
        Factor factor1 = new Factor(FactorTest.distribution1, FactorTest.variables1);
        Factor factor2 = new Factor(FactorTest.distribution2, FactorTest.variables2);
        Factor factor3 = new Factor(FactorTest.distribution3, FactorTest.variables1);

        assertTrue(factor1.haveSameVariable(factor3));
        assertFalse(factor1.haveSameVariable(factor2));
    }


}