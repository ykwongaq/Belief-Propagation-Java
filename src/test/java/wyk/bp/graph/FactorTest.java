package wyk.bp.graph;

import org.apache.commons.math3.transform.FastCosineTransformer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.shade.errorprone.annotations.Var;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FactorTest {

    protected static INDArray matrix1;
    protected static List<Variable<?>> variables1;
    protected static INDArray matrix2;
    protected static List<Variable<?>> variables2;
    @BeforeAll
    static void initTestCase() {
        FactorTest.matrix1 = FactorTest.initMatrix1();
        FactorTest.variables1 = FactorTest.initVariables1();
        FactorTest.matrix2 = FactorTest.initMatrix2();
        FactorTest.variables2 = FactorTest.initVariables2();
    }
    static INDArray initMatrix1() {
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

    static INDArray initMatrix2() {
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

    @Test()
    void testConstructor() {
        assertThrows(NullPointerException.class, () -> new Factor(null, variables1));

        Factor factor = new Factor(matrix1, variables1);
        assertIterableEquals(FactorTest.variables1, factor.getVariables());
        assertEquals(FactorTest.matrix1, factor.getDistribution());

        List<Variable<?>> vars1 = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> new Factor(matrix1, vars1));

        List<Variable<?>> vars2 = new ArrayList<>();
        Variable<String> var1 = new Variable<>("a");
        vars2.add(var1);
        vars2.add(null);

        assertThrows(IllegalArgumentException.class, () -> new Factor(matrix1, vars2));

        assertThrows(IllegalArgumentException.class, () -> new Factor(matrix1, variables2));
    }

    @Test
    void testMoveAxis() {
        double[][][] values = {
                {{0.2, 1.0}, {0.5, 0.6}}, {{0.6, 0.2}, {0.5, 0.3}}
        };
        INDArray expected = Nd4j.create(values);

        int[] originDims = {0, 1, 2};
        int[] targetDims = {2, 0, 1};
        Factor factor = new Factor(FactorTest.matrix1, FactorTest.variables1);
        factor.moveAxis(originDims, targetDims);
        assertEquals(expected, factor.getDistribution());
    }

    @Test
    void testMoveAxis_withInvalidInput() {
        Factor factor = new Factor(FactorTest.matrix1, FactorTest.variables1);
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
        Factor factor1 = new Factor(FactorTest.matrix1, FactorTest.variables1);
        Factor factor2 = new Factor(FactorTest.matrix2, FactorTest.variables1);
        Factor factor3 = new Factor(FactorTest.matrix1, FactorTest.variables2);
        Factor factor4 = new Factor(FactorTest.matrix2, FactorTest.variables2);
        Factor factor5 = new Factor(FactorTest.matrix1, FactorTest.variables1);

        assertEquals(factor1, factor5);
        assertTrue(factor1.haveSameVariable(factor2));
        assertFalse(factor1.haveSameVariable(factor3));
        assertNotEquals(factor1, factor3);
        assertNotEquals(factor1, factor4);
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
    void testProduct3() {
        assertThrows(NullPointerException.class, () -> {
            Factor factor = new Factor(FactorTest.matrix1, FactorTest.variables1);
            Factor.factorProduct(factor, null);
        });

        assertThrows(NullPointerException.class, () -> {
            Factor factor = new Factor(FactorTest.matrix1, FactorTest.variables1);
            Factor.factorProduct(null, factor);
        });

        Variable<String> var1 = new Variable<>("a");
        Variable<String> var2 = new Variable<>("b");
        Factor factor1 = new Factor(FactorTest.matrix1, var1);
        Factor factor2 = new Factor(FactorTest.matrix2, var2);
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
    void testMarginalization3() {
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

}