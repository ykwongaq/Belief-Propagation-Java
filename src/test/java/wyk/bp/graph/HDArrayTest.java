package wyk.bp.graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HDArrayTest {

    @Test
    public void testConstructor() {
        HDArray array = HDArray.create(0.5d);
        assertArrayEquals(new int[]{1}, array.shape());
        assertEquals(0.5d, array.get(0));
    }

    @Test
    public void test1DArray() {
        double[] data = {1.0, 2.0, 3.0, 4.0};
        HDArray array = HDArray.create(data);

        assertArrayEquals(new int[]{4}, array.shape());
        for (int i = 0; i < 4; i++) {
            double expectedValue = i + 1.0d;
            assertEquals(expectedValue, array.get(i));
        }
    }

    @Test
    public void test1DArray_withInvalidInput() {
        double[] data = {};
        assertThrows(IllegalArgumentException.class, () -> HDArray.create(data));
    }

    @Test
    public void test2DArray() {
        double[][] data = {{1.0, 2.0}, {3.0, 4.0}};
        HDArray array = HDArray.create(data);

        assertArrayEquals(new int[]{2, 2}, array.shape());
        assertEquals(1.0d, array.get(0, 0));
        assertEquals(2.0d, array.get(0, 1));
        assertEquals(3.0d, array.get(1, 0));
        assertEquals(4.0d, array.get(1, 1));
    }

    @Test
    public void test2DArray_withInvalidInput() {
        double[][] data = {};
        assertThrows(IllegalArgumentException.class, () -> HDArray.create(data));
    }

    @Test
    public void test3DArray() {
        double[][][] data = {
                {{1.0, 2.0}, {3.0, 4.0}},
                {{5.0, 6.0}, {7.0, 8.0}}
        };
        HDArray array = HDArray.create(data);

        assertArrayEquals(new int[]{2, 2, 2}, array.shape());
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++) {
                    final double expectedValue = 4.0d * i + 2.0d * j + k + 1.0;
                    assertEquals(expectedValue, array.get(i, j, k));
                }
            }
        }
    }

    @Test
    public void test3DArray_withInvalidInput() {
        double[][][] data = {};
        assertThrows(IllegalArgumentException.class, () -> HDArray.create(data));
    }

    @Test
    public void test4DArray() {
        double[][][][] data = {
                {
                        {{1.0, 2.0}, {3.0, 4.0}},
                        {{5.0, 6.0}, {7.0, 8.0}}
                },
                {
                        {{9.0, 10.0}, {11.0, 12.0}},
                        {{13.0, 14.0}, {15.0, 16.0}}
                }
        };
        HDArray array = HDArray.create(data);

        assertArrayEquals(new int[]{2, 2, 2, 2}, array.shape());
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++) {
                    for (int a = 0; a < 2; a++) {
                        final double expectedValue = 8.0d * i + 4.0d * j + 2.0d * k + a + 1.0d;
                        assertEquals(expectedValue, array.get(i, j, k, a));
                    }
                }
            }
        }
    }

    @Test
    public void test4DArray_withInvalidInput() {
        double[][][][] data = {};
        assertThrows(IllegalArgumentException.class, () -> HDArray.create(data));
    }

    @Test
    public void test5DArray() {
        HDArray array = HDArray.createBySizeWithValue(5.0d, 2, 2, 2, 2, 2);
        array.set(0.0d, 1, 1, 1, 1, 1);
        assertArrayEquals(new int[]{2, 2, 2, 2, 2}, array.shape());
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++) {
                    for (int a = 0; a < 2; a++) {
                        for (int b = 0; b < 2; b++) {
                            if (i == 1 && j == 1 && k == 1 && a == 1 && b == 1) {
                                assertEquals(0.0d, array.get(i, j, k, a, b));
                            } else {
                                assertEquals(5.0d, array.get(i, j, k, a, b));
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testEquals() {
        double[][][] data1 = {
                {{1.0, 2.0}, {3.0, 4.0}},
                {{5.0, 6.0}, {7.0, 8.0}}
        };
        HDArray array3D = HDArray.create(data1);
        HDArray sameArray3D = HDArray.create(data1);

        double[][][] data2 = {
                {{1.0, 2.0}, {3.0, 4.0}},
                {{5.0, 6.0}, {7.0, 9.0}} // Difference in value at indices (1, 1)
        };
        HDArray differentArray3D = HDArray.create(data2);

        assertEquals(array3D, sameArray3D);
        assertNotEquals(array3D, differentArray3D);
    }

    @Test
    public void testHashCode() {
        double[][][] data1 = {
                {{1.0, 2.0}, {3.0, 4.0}},
                {{5.0, 6.0}, {7.0, 8.0}}
        };
        HDArray array3D = HDArray.create(data1);
        HDArray sameArray3D = HDArray.create(data1);

        double[][][] data2 = {
                {{1.0, 2.0}, {3.0, 4.0}},
                {{5.0, 6.0}, {7.0, 9.0}} // Difference in value at indices (1, 1)
        };
        HDArray differentArray3D = HDArray.create(data2);

        assertEquals(array3D.hashCode(), sameArray3D.hashCode());
        assertNotEquals(array3D.hashCode(), differentArray3D.hashCode());
    }

    @Test
    public void testGet_withInvalidInput() {
        double[] data = {1.0, 2.0, 3.0, 4.0};
        HDArray array = HDArray.create(data);
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(5));
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(-1));
    }

    @Test
    public void testSet_withInvalidInput() {
        double[] data = {1.0, 2.0, 3.0, 4.0};
        HDArray array = HDArray.create(data);
        assertThrows(IndexOutOfBoundsException.class, () -> array.set(0.0d, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> array.set(0.0d, -1));
    }

    @Test
    public void testMoveAxis() {
        double[][][] data = {
                {{0.2, 0.5}, {0.6, 0.5}}, {{1.0, 0.6}, {0.2, 0.3}}
        };
        HDArray array = HDArray.create(data);

        double[][][] expectedData = {
                {{0.2, 1.0}, {0.5, 0.6}}, {{0.6, 0.2}, {0.5, 0.3}}
        };
        HDArray expectedArray = HDArray.create(expectedData);

        final int[] originDims = {0, 1, 2};
        final int[] targetDims = {2, 0, 1};

        assertEquals(expectedArray, array.moveAxis(originDims, targetDims));
    }

    @Test
    public void testMovieAxis_withInvalidInput() {
        double[][][] data = {
                {{0.2, 0.5}, {0.6, 0.5}}, {{1.0, 0.6}, {0.2, 0.3}}
        };
        HDArray array = HDArray.create(data);

        int[] originDims1 = {0, 1, 2};
        int[] targetDims1 = {2, 0};
        assertThrows(IllegalArgumentException.class, () -> array.moveAxis(originDims1, targetDims1));

        int[] originDims2 = {0, 1, 4};
        int[] targetDims2 = {2, 0, 1};
        assertThrows(IndexOutOfBoundsException.class, () -> array.moveAxis(originDims2, targetDims2));

        int[] originDims3 = {0, 1, 2};
        int[] targetDims3 = {2, 0, 4};
        assertThrows(IndexOutOfBoundsException.class, () -> array.moveAxis(originDims3, targetDims3));
    }

    @Test
    public void testToString() {
        HDArray array = HDArray.createBySize(2, 2, 2);

        int value = 1;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++) {
                    array.set(value++, i, j, k);
                }
            }
        }

        String expectedString = "[[[1.0, 2.0], [3.0, 4.0]], [[5.0, 6.0], [7.0, 8.0]]]";
        assertEquals(expectedString, array.toString());
    }

    @Test
    public void testReshape() {
        HDArray array = HDArray.createBySize(3, 4, 2);

        int value = 1;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 2; k++) {
                    array.set(value++, i, j, k);
                }
            }
        }
        array = array.reshape(2, 3, 4);

        assertArrayEquals(new int[]{2, 3, 4}, array.shape());

        HDArray expectedArray = HDArray.createBySize(2, 3, 4);
        int expectedValue = 1;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 4; k++) {
                    expectedArray.set(expectedValue++, i, j, k);
                }
            }
        }
        assertEquals(expectedArray, array);
    }

    @Test
    public void testReshape2() {
        HDArray array = HDArray.createBySize(3, 4, 2);

        int value = 1;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 2; k++) {
                    array.set(value++, i, j, k);
                }
            }
        }

        array = array.reshape(2, 3, 4, 1);

        assertArrayEquals(new int[]{2, 3, 4, 1}, array.shape());

        HDArray expectedArray = HDArray.createBySize(2, 3, 4, 1);
        int expectedValue = 1;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 4; k++) {
                    expectedArray.set(expectedValue++, i, j, k, 0);
                }
            }
        }
        assertEquals(expectedArray, array);
    }

    @Test
    public void testReshape_withInvalidInput() {
        HDArray array = HDArray.createBySize(3, 4, 2);
        assertThrows(IllegalArgumentException.class, () -> array.reshape(2, 3, 3));
        assertThrows(IllegalArgumentException.class, () -> array.reshape(-2, 3, 4));
    }

    @Test
    public void testMul() {
        double[][][] data1 = {
                {{0.5}, {0.8}}, {{0.1}, {0.0}}, {{0.3}, {0.9}}
        };
        HDArray array1 = HDArray.create(data1);

        double[][][] data2 = {
                {{0.5, 0.7}, {0.1, 0.2}}
        };
        HDArray array2 = HDArray.create(data2);
        double[][][] data3 = {
                {{0.25, 0.35}, {0.08, 0.16}},
                {{0.05, 0.07}, {0.0, 0.0}},
                {{0.15, 0.21}, {0.09, 0.18}}
        };
        HDArray array3 = HDArray.create(data3);

        assertEquals(array3, array1.mul(array2));
    }

    @Test
    public void testAdd() {
        double[][][] data1 = {
                {{0.5, 0.1}, {0.2, 0.8}}, {{0.1, 0.0}, {0.0, 0.0}}, {{0.3, 0.6}, {0.9, 0.5}}
        };
        HDArray array1 = HDArray.create(data1);

        double[][][] data2 = {
                {{0.5, 0.7}, {0.1, 0.2}}
        };
        HDArray array2 = HDArray.create(data2);
        double[][][] data3 = {
                {{1.0, 0.8}, {0.3, 1.0}},
                {{0.6, 0.7}, {0.1, 0.2}},
                {{0.8, 1.3}, {1.0, 0.7}}
        };
        HDArray array3 = HDArray.create(data3);

        assertEquals(array3, array1.add(array2));
    }

    @Test
    public void testSub() {
        double[][][] data1 = {
                {{0.5, 0.1}, {0.2, 0.8}}, {{0.1, 0.0}, {0.0, 0.0}}, {{0.3, 0.6}, {0.9, 0.5}}
        };
        HDArray array1 = HDArray.create(data1);

        double[][][] data2 = {
                {{0.5, 0.7}, {0.1, 0.2}}
        };
        HDArray array2 = HDArray.create(data2);
        double[][][] data3 = {{{0., -0.6},
                {0.1, 0.6}},

                {{-0.4, -0.7},
                        {-0.1, -0.2}},

                {{-0.2, -0.1},
                        {0.8, 0.3}}};
        HDArray array3 = HDArray.create(data3);

        assertEquals(array3, array1.sub(array2));
    }

    @Test
    public void testDiv() {
        double[][][] data1 = {
                {{0.5, 0.1}, {0.2, 0.8}}, {{0.1, 0.0}, {0.0, 0.0}}, {{0.3, 0.6}, {0.9, 0.5}}
        };
        HDArray array1 = HDArray.create(data1);

        double[][][] data2 = {
                {{0.5, 0.7}, {0.1, 0.2}}
        };
        HDArray array2 = HDArray.create(data2);
        double[][][] data3 = {{{1., 0.14285714},
                {2., 4.}},

                {{0.2, 0.},
                        {0., 0.}},

                {{0.6, 0.85714286},
                        {9., 2.5}}};
        HDArray array3 = HDArray.create(data3);

        assertEquals(array3, array1.div(array2));
    }

    @Test
    public void testSumAlongAxis() {
        double[][][] data = {
                {{0.5, 0.7}, {0.1, 0.2}},
                {{0.2, 0.3}, {0.4, 0.5}},
                {{0.3, 0.9}, {0.6, 0.7}}
        };
        HDArray array = HDArray.create(data);

        double[][] data1 = {
                {1.0, 1.9},
                {1.1, 1.4}
        };
        HDArray array1 = HDArray.create(data1);
        assertEquals(array1, array.sumAlongAxis(0));

        double[][] data2 = {
                {0.6, 0.9},
                {0.6, 0.8},
                {0.9, 1.6}
        };
        HDArray array2 = HDArray.create(data2);
        assertEquals(array2, array.sumAlongAxis(1));

        double[][] data3 = {
                {1.2, 0.3},
                {0.5, 0.9},
                {1.2, 1.3}
        };
        HDArray array3 = HDArray.create(data3);
        assertEquals(array3, array.sumAlongAxis(2));

        double[] data4 = {2.1, 3.3};
        HDArray array4 = HDArray.create(data4);
        assertEquals(array4, array.sumAlongAxis(0, 1));

        double[] data5 = {2.9, 2.5};
        HDArray array5 = HDArray.create(data5);
        assertEquals(array5, array.sumAlongAxis(0, 2));

        double[] data6 = {1.5, 1.4, 2.5};
        HDArray array6 = HDArray.create(data6);
        assertEquals(array6, array.sumAlongAxis(1, 2));

        double data7 = 5.3999999999999995;
        HDArray array7 = HDArray.create(data7);
        assertEquals(array7, array.sumAlongAxis(0, 1, 2));
    }
}