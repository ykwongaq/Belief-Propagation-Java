package BeliefPropagation.graph;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FactorTest {

    protected static HDArray distribution1;
    protected static List<Variable<?>> variables1;
    protected static HDArray distribution2;
    protected static List<Variable<?>> variables2;

    protected static HDArray distribution3;

    @BeforeAll
    static void initTestCase() {
        FactorTest.distribution1 = FactorTest.initDistribution1();
        FactorTest.variables1 = FactorTest.initVariables1();
        FactorTest.distribution2 = FactorTest.initDistribution2();
        FactorTest.variables2 = FactorTest.initVariables2();
        FactorTest.distribution3 = FactorTest.initDistribution3();
    }
    static HDArray initDistribution1() {
        double[][][] values = {
                {{0.2, 0.5}, {0.6, 0.5}}, {{1.0, 0.6}, {0.2, 0.3}}
        };
        return HDArray.create(values);
    }
    static List<Variable<?>> initVariables1() {
        List<Variable<?>> variables1 = new ArrayList<>();
        Variable<String> var1 = new Variable<>("a", 2);
        Variable<String> var2 = new Variable<>("b", 2);
        Variable<String> var3 = new Variable<>("d", 2);
        variables1.add(var1);
        variables1.add(var2);
        variables1.add(var3);
        return variables1;
    }

    static HDArray initDistribution2() {
        double[][] values = {
                {0.5, 0.7}, {0.1, 0.2}
        };
        return HDArray.create(values);
    }

    static List<Variable<?>> initVariables2() {
        List<Variable<?>> variables1 = new ArrayList<>();
        Variable<String> var1 = new Variable<>("b", 2);
        Variable<String> var2 = new Variable<>("c", 2);
        variables1.add(var1);
        variables1.add(var2);
        return variables1;
    }

    static HDArray initDistribution3() {
        double[][][] values = {
                {{0.2, 0.5}, {0.6, 0.4}}, {{1.0, 0.6}, {0.2, 0.3}}
        };
        return HDArray.create(values);
    }

    @Test
    void testConstructorWithNullArgument() {
        assertThrows(NullPointerException.class, () -> new Factor(FactorTest.distribution1, (List<Variable<?>>) null));
    }

    @Test
    void testConstructorWithEmptyVariables() {
        assertThrows(IllegalArgumentException.class, () -> new Factor(FactorTest.distribution1, new ArrayList<>()));
    }

    @Test
    void testConstructorWithNullElement() {
        List<Variable<?>> variables = new ArrayList<>();
        Variable<String> a = new Variable<>("a", 2);
        Variable<String> b = new Variable<>("b", 2);
        variables.add(a);
        variables.add(null);
        variables.add(b);

        assertThrows(IllegalArgumentException.class, () -> new Factor(FactorTest.distribution1, variables));
    }

    @Test
    void testConstructorWithMismatchRank() {
        Variable<String> a = new Variable<>("a", 2);
        Variable<String> b = new Variable<>("b", 2);
        assertThrows(IllegalArgumentException.class, () -> new Factor(FactorTest.distribution1, a, b));
    }

    @Test
    void testConstructionWithMismatchDimension() {
        Variable<String> var1 = new Variable<>("b", 2);
        Variable<String> var2 = new Variable<>("c", 2);
        double[][] values = {
                {0.5, 0.7, 0.1}, {0.1, 0.2, 0.3}
        };

        assertThrows(IllegalArgumentException.class, () -> new Factor(HDArray.create(values), var1, var2));
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

        assertTrue(factor1.haveSameVariables(factor3));
        assertFalse(factor1.haveSameVariables(factor2));
    }


}