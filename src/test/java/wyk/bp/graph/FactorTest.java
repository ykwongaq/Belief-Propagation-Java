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
    void testMoveAxis() {
        double[][][] values = {
                {{0.2, 1.0}, {0.5, 0.6}}, {{0.6, 0.2}, {0.5, 0.3}}
        };
        INDArray expected = Nd4j.create(values);

        int[] originDims = {0, 1, 2};
        int[] targetDims = {2, 0, 1};
        Factor factor = new Factor(FactorTest.distribution1, FactorTest.variables1);
        factor.moveAxis(originDims, targetDims);
        assertEquals(expected, factor.getDistribution());
    }

    @Test
    void testMoveAxis_withInvalidInput() {
        Factor factor = new Factor(FactorTest.distribution1, FactorTest.variables1);
        int[] originDims1 = {0, 1, 2};
        int[] targetDims1 = {2, 0};
        assertThrows(IllegalArgumentException.class, () -> factor.moveAxis(originDims1, targetDims1));

        int[] originDims2 = {0, 1, 4};
        int[] targetDims2 = {2, 0, 1};
        assertThrows(IllegalArgumentException.class, () -> factor.moveAxis(originDims2, targetDims2));

        int[] originDims3 = {0, 1, 2};
        int[] targetDims3 = {2, 0, 4};
        assertThrows(IllegalArgumentException.class, () -> factor.moveAxis(originDims3, targetDims3));
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

    @Test
    void testProduct1() {
        double[][] values1 = {
                {0.5, 0.8}, {0.1, 0.0}, {0.3, 0.9}
        };
        Variable<String> var1 = new Variable<>("a");
        Variable<String> var2 = new Variable<>("b");
        Factor factor1 = new Factor(Nd4j.create(values1), var1, var2);

        double[][] values2 = {
                {0.5, 0.7}, {0.1, 0.2}
        };
        Variable<String> var3 = new Variable<>("c");
        Factor factor2 = new Factor(Nd4j.create(values2), var2, var3);


        double[][][] expectedValues = {
                {{0.25, 0.35}, {0.08, 0.16}},
                {{0.05, 0.07}, {0.0, 0.0}},
                {{0.15, 0.21}, {0.09, 0.18}}
        };
        Factor expectedFactor = new Factor(Nd4j.create(expectedValues), var1, var2, var3);
        Factor factorProduct = Factor.factorProduct(factor1, factor2);
        assertEquals(expectedFactor, factorProduct);
    }

    @Test
    void testProduct2() {
        double[][] values1 = {
                {0.5, 0.8}, {0.1, 0.0}
        };
        Variable<String> var1 = new Variable<>("a");
        Variable<String> var2 = new Variable<>("b");
        Factor factor1 = new Factor(Nd4j.create(values1), var2, var1);

        double[][] values2 = {
                {0.5, 0.7}, {0.1, 0.2}
        };
        Variable<String> var3 = new Variable<>("c");
        Factor factor2 = new Factor(Nd4j.create(values2), var2, var3);

        double[][][] expectedValues = {
                {{0.25, 0.35}, {0.01, 0.02}},
                {{0.4, 0.56}, {0.0, 0.0}}
        };
        Factor expectedFactor = new Factor(Nd4j.create(expectedValues), var1, var2, var3);
        Factor factorProduct = Factor.factorProduct(factor1, factor2);
        assertEquals(expectedFactor, factorProduct);
    }

    @Test
    void testProductWithNullArgument() {
        assertThrows(NullPointerException.class, () -> {
            Factor factor = new Factor(FactorTest.distribution1, FactorTest.variables1);
            Factor.factorProduct(factor, null);
        });
        assertThrows(NullPointerException.class, () -> {
            Factor factor = new Factor(FactorTest.distribution1, FactorTest.variables1);
            Factor.factorProduct(null, factor);
        });
    }

    @Test
    void testProductWithMismatchVariables() {
        Variable<String> var1 = new Variable<>("a");
        Variable<String> var2 = new Variable<>("b");
        Variable<String> var3 = new Variable<>("c");
        Variable<String> var4 = new Variable<>("d");
        Variable<String> var5 = new Variable<>("e");
        Factor factor1 = new Factor(FactorTest.distribution1, var1, var2, var3);
        Factor factor2 = new Factor(FactorTest.distribution2, var4, var5);
        assertThrows(IllegalArgumentException.class, () -> Factor.factorProduct(factor1, factor2));
    }

    @Test
    void testMarginalization1() {
        double[][][] values = {
                {{0.25, 0.35}, {0.08, 0.16}},
                {{0.05, 0.07}, {0.0, 0.0}},
                {{0.15, 0.21}, {0.09, 0.18}}
        };
        Variable<String> var1 = new Variable<>("a");
        Variable<String> var2 = new Variable<>("b");
        Variable<String> var3 = new Variable<>("c");
        Factor factor = new Factor(Nd4j.create(values), var1, var2, var3);

        double[][] expectedValues = {
                {0.33, 0.51}, {0.05, 0.07}, {0.24, 0.39}
        };
        Factor expectedFactor = new Factor(Nd4j.create(expectedValues), var1, var3);

        Factor marginalizedFactor = Factor.factorMarginalization(factor, var2);
        assertEquals(expectedFactor, marginalizedFactor);
    }

    @Test
    void testMarginalization2() {
        double[][][] values = {
                {{0.25, 0.35}, {0.08, 0.16}},
                {{0.05, 0.07}, {0.0, 0.0}},
                {{0.15, 0.21}, {0.09, 0.18}}
        };
        Variable<String> var1 = new Variable<>("a");
        Variable<String> var2 = new Variable<>("b");
        Variable<String> var3 = new Variable<>("c");
        Factor factor = new Factor(Nd4j.create(values), var1, var2, var3);

        double[] expectedValues = {
                0.84, 0.12, 0.63
        };
        Factor expectedFactor = new Factor(Nd4j.create(expectedValues), var1);

        Factor marginalizedFactor = Factor.factorMarginalization(factor, var2, var3);
        assertEquals(expectedFactor, marginalizedFactor);
    }

    @Test
    void testMarginalizationWithNullArgument() {
        assertThrows(NullPointerException.class, () -> Factor.factorMarginalization(null, FactorTest.variables1));
        assertThrows(NullPointerException.class, () -> {
            Factor factor = new Factor(FactorTest.distribution1, FactorTest.variables1);
            Factor.factorMarginalization(factor, (List<Variable<?>>) null);
        });
    }

    @Test
    void testMarginalizationWithEmptyVariables() {
        Factor factor = new Factor(FactorTest.distribution1, FactorTest.variables1);
        assertThrows(IllegalArgumentException.class, () -> Factor.factorMarginalization(factor, new ArrayList<Variable<?>>()));
    }

    @Test
    void testMarginalizationWithNullElement() {
        Factor factor = new Factor(FactorTest.distribution1, FactorTest.variables1);
        List<Variable<?>> variables = new ArrayList<>();
        Variable<String> var1 = new Variable<>("a");
        Variable<String> var2 = new Variable<>("b");
        variables.add(var1);
        variables.add(null);
        variables.add(var2);
        assertThrows(IllegalArgumentException.class, () -> Factor.factorMarginalization(factor, variables));
    }

    @Test
    void testMarginalizationWithMismatchVariables() {
        assertThrows(IllegalArgumentException.class, () -> {
            double[][] values1 = {
                    {0.5, 0.8}, {0.1, 0.0}
            };
            Variable<String> var1 = new Variable<>("a");
            Variable<String> var2 = new Variable<>("b");
            Variable<String> var3 = new Variable<>("c");
            Factor factor1 = new Factor(Nd4j.create(values1), var2, var1);
            Factor.factorMarginalization(factor1, var3);
        });
    }
    @Test
    void testJoinFactors() {
        Variable<String> a = new Variable<>("a");
        Variable<String> b = new Variable<>("b");
        Variable<String> c = new Variable<>("c");
        Variable<String> d = new Variable<>("d");

        double[][] values1 = {
                {0.5, 0.8}, {0.1, 0.0},
        };
        Factor factor1 = new Factor(Nd4j.create(values1), a, b);

        double[][] values2 = {
                {0.5, 0.7}, {0.1, 0.2},
        };
        Factor factor2 = new Factor(Nd4j.create(values2), b, c);

        double[][] values3 = {
                {0.3, 0.2}, {0.3, 0.6},
        };
        Factor factor3 = new Factor(Nd4j.create(values3), c, d);

        double[][][][] values4 = {{{{0.075, 0.05 },
                                   {0.105, 0.21 }},
                                  {{0.024, 0.016},
                                   {0.048, 0.096}}},
                                 {{{0.015, 0.01 },
                                   {0.021, 0.042}},
                                  {{0.0,    0.   },
                                   {0.,    0.   }}}};
        Factor expectedFactor = new Factor(Nd4j.create(values4), a, b, c, d);

        Factor joinedFactor = Factor.joinFactors(factor1, factor2, factor3);
        assertEquals(expectedFactor, joinedFactor);
    }

    @Test
    void testJoinFactorsWithNullArgument() {
        assertThrows(NullPointerException.class, () -> Factor.joinFactors((Collection<Factor>) null));
    }

    @Test
    void testJoinFactorsWithEmptyList() {
        assertThrows(IllegalArgumentException.class, () -> Factor.joinFactors(new ArrayList<Factor>()));
    }

    @Test
    void testJoinFactorWithNullElement() {
        Variable<String> a = new Variable<>("a");
        Variable<String> b = new Variable<>("b");
        Variable<String> c = new Variable<>("c");
        Variable<String> d = new Variable<>("d");

        double[][] values1 = {
                {0.5, 0.8}, {0.1, 0.0},
        };
        Factor factor1 = new Factor(Nd4j.create(values1), a, b);

        double[][] values2 = {
                {0.5, 0.7}, {0.1, 0.2},
        };
        Factor factor2 = new Factor(Nd4j.create(values2), b, c);

        double[][] values3 = {
                {0.3, 0.2}, {0.3, 0.6},
        };
        Factor factor3 = new Factor(Nd4j.create(values3), c, d);

        List<Factor> factors = new ArrayList<>();
        factors.add(factor1);
        factors.add(null);
        factors.add(factor2);
        factors.add(factor3);
        assertThrows(IllegalArgumentException.class, () -> Factor.joinFactors(factors));
    }

    @Test
    void testNormalize() {
        Variable<String> a = new Variable<>("a");
        Variable<String> b = new Variable<>("b");

        double[][] values = {
                {0.5, 0.8}, {0.1, 0.0}
        };
        Factor factor = new Factor(Nd4j.create(values), a, b);
        factor.normalize();
        double[][] expectedValues = {
                {0.5 / 1.4, 0.8 / 1.4}, {0.1 / 1.4, 0.0}
        };
        Factor expected = new Factor(Nd4j.create(expectedValues), a, b);

        assertEquals(expected, factor);
    }

}