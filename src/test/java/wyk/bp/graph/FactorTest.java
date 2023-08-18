package wyk.bp.graph;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

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
        assertThrows(NullPointerException.class, () -> {
            Factor factor = new Factor(null, matrix1);
        });
        assertThrows(NullPointerException.class, () -> {
            Factor factor = new Factor(variables1, null);
        });
        Factor factor = new Factor(variables1, matrix1);
        assertIterableEquals(FactorTest.variables1, factor.getVariables());
        assertEquals(FactorTest.matrix1, factor.getMatrix());
    }

    @Test
    void testMoveAxis() {
        double[][][] values = {
                {{0.2, 1.0}, {0.5, 0.6}}, {{0.6, 0.2}, {0.5, 0.3}}
        };
        INDArray expected = Nd4j.create(values);

        int[] originDims = {0, 1, 2};
        int[] targetDims = {2, 0, 1};
        Factor factor = new Factor(FactorTest.variables1, FactorTest.matrix1);
        factor.moveAxis(originDims, targetDims);
        assertEquals(expected, factor.getMatrix());
    }

    @Test
    void testMoveAxis_withInvalidInput() {
        Factor factor = new Factor(FactorTest.variables1, FactorTest.matrix1);
        int[] originDims1 = {0, 1, 2};
        int[] targetDims1 = {2, 0};
        assertThrows(IllegalArgumentException.class, () -> {
           factor.moveAxis(originDims1, targetDims1);
        });

        int[] originDims2 = {0, 1, 4};
        int[] targetDims2 = {2, 0, 1};
        assertThrows(IllegalArgumentException.class, () -> {
            factor.moveAxis(originDims2, targetDims2);
        });

        int[] originDims3 = {0, 1, 2};
        int[] targetDims3 = {2, 0, 4};
        assertThrows(IllegalArgumentException.class, () -> {
            factor.moveAxis(originDims3, targetDims3);
        });
    }

    @Test
    void testEquals() {
        Factor factor1 = new Factor(FactorTest.variables1, FactorTest.matrix1);
        Factor factor2 = new Factor(FactorTest.variables1, FactorTest.matrix2);
        Factor factor3 = new Factor(FactorTest.variables2, FactorTest.matrix1);
        Factor factor4 = new Factor(FactorTest.variables2, FactorTest.matrix2);
        Factor factor5 = new Factor(FactorTest.variables1, FactorTest.matrix1);

        assertEquals(factor1, factor5);
        assertTrue(factor1.haveSameVariable(factor2));
        assertFalse(factor1.haveSameVariable(factor3));
        assertNotEquals(factor1, factor3);
        assertNotEquals(factor1, factor4);
    }
}