package BeliefPropagation.graph;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {
    protected static HDArray distribution1;
    protected static List<Variable<?>> variables1;
    protected static HDArray distribution2;
    protected static List<Variable<?>> variables2;

    protected static HDArray distribution3;

    @BeforeAll
    static void initTestCase() {
        MessageTest.distribution1 = MessageTest.initDistribution1();
        MessageTest.variables1 = MessageTest.initVariables1();
        MessageTest.distribution2 = MessageTest.initDistribution2();
        MessageTest.variables2 = MessageTest.initVariables2();
        MessageTest.distribution3 = MessageTest.initDistribution3();
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
    void testVariableConstructor() {
        Variable<String> a = new Variable<>("a", 2);
        Variable<String> b = new Variable<>("b", 2);
        Message message = new Message(a, b);
        double[][] values = {
                {1.0d, 1.0d}, {1.0d, 1.0d}
        };
        Message expectedMessage = new Message(HDArray.create(values), a, b);
        assertEquals(expectedMessage, message);
    }
    @Test
    void testConstructorWithNullArgument() {
        assertThrows(NullPointerException.class, () -> new Message(null, MessageTest.variables1));
        assertThrows(NullPointerException.class, () -> new Message(MessageTest.distribution1, (List<Variable<?>>) null));
    }

    @Test
    void testConstructorWithEmptyVariables() {
        assertThrows(IllegalArgumentException.class, () -> new Message(MessageTest.distribution1, new ArrayList<>()));
    }

    @Test
    void testConstructorWithNullElement() {
        List<Variable<?>> variables = new ArrayList<>();
        Variable<String> a = new Variable<>("a", 2);
        Variable<String> b = new Variable<>("b", 2);
        variables.add(a);
        variables.add(null);
        variables.add(b);

        assertThrows(IllegalArgumentException.class, () -> new Message(MessageTest.distribution1, variables));
    }

    @Test
    void testConstructorWithMismatchRank() {
        Variable<String> a = new Variable<>("a", 2);
        Variable<String> b = new Variable<>("b", 2);
        assertThrows(IllegalArgumentException.class, () -> new Message(MessageTest.distribution1, a, b));
    }

    @Test
    void testConstructionWithMismatchDimension() {
        Variable<String> var1 = new Variable<>("b", 2);
        Variable<String> var2 = new Variable<>("c", 2);
        double[][] values = {
                {0.5, 0.7, 0.1}, {0.1, 0.2, 0.3}
        };

        assertThrows(IllegalArgumentException.class, () -> new Message(HDArray.create(values), var1, var2));
    }

    @Test
    void testMoveAxis() {
        double[][][] values = {
                {{0.2, 1.0}, {0.5, 0.6}}, {{0.6, 0.2}, {0.5, 0.3}}
        };
        HDArray expected = HDArray.create(values);

        int[] originDims = {0, 1, 2};
        int[] targetDims = {2, 0, 1};
        Message message = new Message(MessageTest.distribution1, MessageTest.variables1);
        message = message.moveAxis(originDims, targetDims);
        assertEquals(expected, message.getProbability());
    }

    @Test
    void testMoveAxis_withInvalidInput() {
        Message message = new Message(MessageTest.distribution1, MessageTest.variables1);
        int[] originDims1 = {0, 1, 2};
        int[] targetDims1 = {2, 0};
        assertThrows(IllegalArgumentException.class, () -> message.moveAxis(originDims1, targetDims1));

        int[] originDims2 = {0, 1, 4};
        int[] targetDims2 = {2, 0, 1};
        assertThrows(IllegalArgumentException.class, () -> message.moveAxis(originDims2, targetDims2));

        int[] originDims3 = {0, 1, 2};
        int[] targetDims3 = {2, 0, 4};
        assertThrows(IllegalArgumentException.class, () -> message.moveAxis(originDims3, targetDims3));
    }

    @Test
    void testEquals() {
        Message message1 = new Message(MessageTest.distribution1, MessageTest.variables1);
        Message message2 = new Message(MessageTest.distribution2, MessageTest.variables2);
        Message message3 = new Message(MessageTest.distribution3, MessageTest.variables1);
        Message message4 = new Message(MessageTest.distribution1, MessageTest.variables1);

        assertEquals(message1, message4);
        assertNotEquals(message1, message2);
        assertNotEquals(message1, message3);
    }

    @Test
    void testSameVariables() {
        Message message1 = new Message(MessageTest.distribution1, MessageTest.variables1);
        Message message2 = new Message(MessageTest.distribution2, MessageTest.variables2);
        Message message3 = new Message(MessageTest.distribution3, MessageTest.variables1);

        assertTrue(message1.haveSameVariables(message3));
        assertFalse(message1.haveSameVariables(message2));
    }

    @Test
    void testNormalize() {
        Variable<String> a = new Variable<>("a", 2);
        Variable<String> b = new Variable<>("b", 2);

        double[][] values = {
                {0.5, 0.8}, {0.1, 0.0}
        };
        Message message = new Message(HDArray.create(values), a, b);
        message.normalize();
        double[][] expectedValues = {
                {0.5 / 1.4, 0.8 / 1.4}, {0.1 / 1.4, 0.0}
        };
        Message expected = new Message(HDArray.create(expectedValues), a, b);

        assertEquals(expected, message);
    }

    @Test
    void testProduct1() {
        double[][] values1 = {
                {0.5, 0.8}, {0.1, 0.0}, {0.3, 0.9}
        };
        Variable<String> var1 = new Variable<>("a", 3);
        Variable<String> var2 = new Variable<>("b", 2);
        Message message1 = new Message(HDArray.create(values1), var1, var2);

        double[][] values2 = {
                {0.5, 0.7}, {0.1, 0.2}
        };
        Variable<String> var3 = new Variable<>("c", 2);
        Message message2 = new Message(HDArray.create(values2), var2, var3);


        double[][][] expectedValues = {
                {{0.25, 0.35}, {0.08, 0.16}},
                {{0.05, 0.07}, {0.0, 0.0}},
                {{0.15, 0.21}, {0.09, 0.18}}
        };
        Message expectedMessage = new Message(HDArray.create(expectedValues), var1, var2, var3);
        Message messageProduct = Message.messageProduct(message1, message2);
        assertEquals(expectedMessage, messageProduct);
    }

    @Test
    void testProduct2() {
        double[][] values1 = {
                {0.5, 0.8}, {0.1, 0.0}
        };
        Variable<String> var1 = new Variable<>("a", 2);
        Variable<String> var2 = new Variable<>("b", 2);
        Message message1 = new Message(HDArray.create(values1), var2, var1);

        double[][] values2 = {
                {0.5, 0.7}, {0.1, 0.2}
        };
        Variable<String> var3 = new Variable<>("c", 2);
        Message message2 = new Message(HDArray.create(values2), var2, var3);

        double[][][] expectedValues = {
                {{0.25, 0.35}, {0.01, 0.02}},
                {{0.4, 0.56}, {0.0, 0.0}}
        };
        Message expectedMessage = new Message(HDArray.create(expectedValues), var1, var2, var3);
        Message messageProduct = Message.messageProduct(message1, message2);
        assertEquals(expectedMessage, messageProduct);
    }

    @Test
    void testProduct3() {
        Variable<String> var1 = new Variable<>("a", 2);
        Variable<String> var2 = new Variable<>("b", 2);
        double[][] values1 = {
                {0.5, 0.8}, {0.1, 0.0}
        };
        double[] values2 = {
                0.5, 0.7
        };

        Message message1 = new Message(HDArray.create(values1), var1, var2);
        Message message2 = new Message(HDArray.create(values2), var1);
        Message result = Message.messageProduct(message1, message2);

        double[][] values3 = {
                {0.25, 0.07}, {0.4, 0.0}
        };
        Message expectedMessage = new Message(HDArray.create(values3), var2, var1);

        assertEquals(expectedMessage, result);
    }

    @Test
    void testProductWithNullArgument() {
        assertThrows(NullPointerException.class, () -> {
            Message message = new Message(MessageTest.distribution1, MessageTest.variables1);
            Message.messageProduct(message, null);
        });
        assertThrows(NullPointerException.class, () -> {
            Message message = new Message(MessageTest.distribution1, MessageTest.variables1);
            Message.messageProduct(null, message);
        });
    }

    @Test
    void testProductWithMismatchVariables() {
        Variable<String> var1 = new Variable<>("a", 2);
        Variable<String> var2 = new Variable<>("b", 2);
        Variable<String> var3 = new Variable<>("c", 2);
        Variable<String> var4 = new Variable<>("d", 2);
        Variable<String> var5 = new Variable<>("e", 2);
        Message message1 = new Message(MessageTest.distribution1, var1, var2, var3);
        Message message2 = new Message(MessageTest.distribution2, var4, var5);
        assertThrows(IllegalArgumentException.class, () -> Message.messageProduct(message1, message2));
    }

    @Test
    void testMarginalization1() {
        double[][][] values = {
                {{0.25, 0.35}, {0.08, 0.16}},
                {{0.05, 0.07}, {0.0, 0.0}},
                {{0.15, 0.21}, {0.09, 0.18}}
        };
        Variable<String> var1 = new Variable<>("a", 3);
        Variable<String> var2 = new Variable<>("b", 2);
        Variable<String> var3 = new Variable<>("c", 2);
        Message message = new Message(HDArray.create(values), var1, var2, var3);

        double[][] expectedValues = {
                {0.33, 0.51}, {0.05, 0.07}, {0.24, 0.39}
        };
        Message expectedMessage = new Message(HDArray.create(expectedValues), var1, var3);

        Message marginalizedMessage = Message.messageMarginalization(message, var2);
        assertEquals(expectedMessage, marginalizedMessage);
    }

    @Test
    void testMarginalization2() {
        double[][][] values = {
                {{0.25, 0.35}, {0.08, 0.16}},
                {{0.05, 0.07}, {0.0, 0.0}},
                {{0.15, 0.21}, {0.09, 0.18}}
        };
        Variable<String> var1 = new Variable<>("a", 3);
        Variable<String> var2 = new Variable<>("b", 2);
        Variable<String> var3 = new Variable<>("c", 2);
        Message message = new Message(HDArray.create(values), var1, var2, var3);

        double[] expectedValues = {
                0.84, 0.12, 0.63
        };
        Message expectedMessage = new Message(HDArray.create(expectedValues), var1);

        Message marginalizedMessage = Message.messageMarginalization(message, var2, var3);
        assertEquals(expectedMessage, marginalizedMessage);
    }

    @Test
    void testMarginalizationWithNullArgument() {
        assertThrows(NullPointerException.class, () -> Message.messageMarginalization(null, MessageTest.variables1));
        assertThrows(NullPointerException.class, () -> {
            Message message = new Message(MessageTest.distribution1, MessageTest.variables1);
            Message.messageMarginalization(message, (List<Variable<?>>) null);
        });
    }

    @Test
    void testMarginalizationWithEmptyVariables() {
        Message message = new Message(MessageTest.distribution1, MessageTest.variables1);
        assertEquals(message, Message.messageMarginalization(message, new ArrayList<>()));
    }

    @Test
    void testMarginalizationWithNullElement() {
        Message message = new Message(MessageTest.distribution1, MessageTest.variables1);
        List<Variable<?>> variables = new ArrayList<>();
        Variable<String> var1 = new Variable<>("a", 2);
        Variable<String> var2 = new Variable<>("b", 2);
        variables.add(var1);
        variables.add(null);
        variables.add(var2);
        assertThrows(IllegalArgumentException.class, () -> Message.messageMarginalization(message, variables));
    }

    @Test
    void testMarginalizationWithMismatchVariables() {
        assertThrows(IllegalArgumentException.class, () -> {
            double[][] values1 = {
                    {0.5, 0.8}, {0.1, 0.0}
            };
            Variable<String> var1 = new Variable<>("a", 2);
            Variable<String> var2 = new Variable<>("b", 2);
            Variable<String> var3 = new Variable<>("c", 2);
            Message message = new Message(HDArray.create(values1), var2, var1);
            Message.messageMarginalization(message, var3);
        });
    }
    @Test
    void testJoinMessages() {
        Variable<String> a = new Variable<>("a", 2);
        Variable<String> b = new Variable<>("b", 2);
        Variable<String> c = new Variable<>("c", 2);
        Variable<String> d = new Variable<>("d", 2);

        double[][] values1 = {
                {0.5, 0.8}, {0.1, 0.0},
        };
        Message message1 = new Message(HDArray.create(values1), a, b);

        double[][] values2 = {
                {0.5, 0.7}, {0.1, 0.2},
        };
        Message message2 = new Message(HDArray.create(values2), b, c);

        double[][] values3 = {
                {0.3, 0.2}, {0.3, 0.6},
        };
        Message message3 = new Message(HDArray.create(values3), c, d);

        double[][][][] values4 = {{{{0.075, 0.05 },
                {0.105, 0.21 }},
                {{0.024, 0.016},
                        {0.048, 0.096}}},
                {{{0.015, 0.01 },
                        {0.021, 0.042}},
                        {{0.0,    0.   },
                                {0.,    0.   }}}};
        Message expectedMessage = new Message(HDArray.create(values4), a, b, c, d);

        Message joinedMessage = Message.messageProduct(message1, message2, message3);
        assertEquals(expectedMessage, joinedMessage);
    }

    @Test
    void testJoinMessagesWithNullArgument() {
        assertThrows(NullPointerException.class, () -> Message.messageProduct((Message) null));
    }

    @Test
    void testJoinMessagesWithEmptyList() {
        assertThrows(IllegalArgumentException.class, () -> Message.messageProduct(new ArrayList<>()));
    }

    @Test
    void testJoinMessageWithNullElement() {
        Variable<String> a = new Variable<>("a", 2);
        Variable<String> b = new Variable<>("b", 2);
        Variable<String> c = new Variable<>("c", 2);
        Variable<String> d = new Variable<>("d", 2);

        double[][] values1 = {
                {0.5, 0.8}, {0.1, 0.0},
        };
        Message message1 = new Message(HDArray.create(values1), a, b);

        double[][] values2 = {
                {0.5, 0.7}, {0.1, 0.2},
        };
        Message message2 = new Message(HDArray.create(values2), b, c);

        double[][] values3 = {
                {0.3, 0.2}, {0.3, 0.6},
        };
        Message message3 = new Message(HDArray.create(values3), c, d);

        List<Message> factors = new ArrayList<>();
        factors.add(message1);
        factors.add(null);
        factors.add(message2);
        factors.add(message3);
        assertThrows(IllegalArgumentException.class, () -> Message.messageProduct(factors));
    }
}