package wyk.bp.graph;

import wyk.bp.utils.Log;
import java.util.Arrays;
import java.util.Objects;

/**
 * This class is used to represent high dimensional array. <br/>
 * The data of this array is stored in a 1D array. <br/>
 * For example, a 2x3 matrix with data [1, 2, 3, 4, 5, 6] can be represented as a 1D array with shape [2, 3]. <br/>
 */
public class HDArray implements Cloneable {
    /**
     * Shape of this array. For example, shape of a 2x3 matrix is [2, 3].
     */
    protected int[] shape;
    /**
     * Data of this array. For example, data of a 2x3 matrix is [1, 2, 3, 4, 5, 6].
     */
    protected double[] data;
    /**
     * Default value for array element.
     */
    protected static final double DEFAULT_VALUE = 0.0d;

    /**
     * Constructor with deep copy.
     * @param shape Shape of this array.
     * @param data Data of this array.
     */
    public HDArray(final int[] shape, final double[] data) {
        Objects.requireNonNull(shape, Log.genLogMsg(this.getClass(), "Given shape should not be null"));
        Objects.requireNonNull(data, Log.genLogMsg(this.getClass(), "Given data should not be null"));
        this.shape = shape.clone();
        this.data = data.clone();
    }

    /**
     * Constructor with give data.
     * @param data Data of this array.
     * @return HDArray with given data.
     */
    public static HDArray create(double data) {
        return new HDArray(new int[]{1}, new double[]{data});
    }

    /**
     * Constructor with give data.
     * @param data Data of this array.
     * @return HDArray with given data.
     * @throws NullPointerException if given data is null.
     * @throws IllegalArgumentException if given data is empty.
     */
    public static HDArray create(double[] data) {
        Objects.requireNonNull(data, Log.genLogMsg(HDArray.class, "Given data should not be null"));
        if (data.length == 0) {
            throw new IllegalArgumentException(Log.genLogMsg(HDArray.class, "Given data should not be empty"));
        }
        return new HDArray(new int[]{data.length}, data);
    }

    /**
     * Constructor with give data.
     * @param data Data of this array.
     * @return HDArray with given data.
     * @throws NullPointerException if given data is null.
     * @throws IllegalArgumentException if given data is empty.
     */
    public static HDArray create(double[][] data) {
        Objects.requireNonNull(data, Log.genLogMsg(HDArray.class, "Given data should not be null"));
        if (data.length == 0 || data[0].length == 0) {
            throw new IllegalArgumentException(Log.genLogMsg(HDArray.class, "Given data should not be empty"));
        }
        int[] shape = new int[]{data.length, data[0].length};
        double[] flattenData = new double[shape[0] * shape[1]];
        for (int i = 0; i < shape[0]; i++) {
            System.arraycopy(data[i], 0, flattenData, i * shape[1], shape[1]);
        }
        return new HDArray(shape, flattenData);
    }

    /**
     * Constructor with give data.
     * @param data Data of this array.
     * @return HDArray with given data.
     * @throws NullPointerException if given data is null.
     * @throws IllegalArgumentException if given data is empty.
     */
    public static HDArray create(final double[][][] data) {
        Objects.requireNonNull(data, Log.genLogMsg(HDArray.class, "Given data should not be null"));
        if (data.length == 0 || data[0].length == 0 || data[0][0].length == 0) {
            throw new IllegalArgumentException(Log.genLogMsg(HDArray.class, "Given data should not be empty"));
        }
        int[] shape = new int[]{data.length, data[0].length, data[0][0].length};
        double[] flattenData = new double[shape[0] * shape[1] * shape[2]];
        for (int i = 0; i < shape[0]; i++) {
            for (int j = 0; j < shape[1]; j++) {
                System.arraycopy(data[i][j], 0, flattenData, (i * shape[1] + j) * shape[2], shape[2]);
            }
        }
        return new HDArray(shape, flattenData);
    }

    /**
     * Constructor with give data.
     * @param data Data of this array.
     * @return HDArray with given data.
     * @throws NullPointerException if given data is null.
     * @throws IllegalArgumentException if given data is empty.
     */
    public static HDArray create(final double[][][][] data) {
        Objects.requireNonNull(data, Log.genLogMsg(HDArray.class, "Given data should not be null"));
        if (data.length == 0 || data[0].length == 0 || data[0][0].length == 0 || data[0][0][0].length == 0) {
            throw new IllegalArgumentException(Log.genLogMsg(HDArray.class, "Given data should not be empty"));
        }
        int[] shape = new int[]{data.length, data[0].length, data[0][0].length, data[0][0][0].length};
        double[] flattenData = new double[shape[0] * shape[1] * shape[2] * shape[3]];
        for (int i = 0; i < shape[0]; i++) {
            for (int j = 0; j < shape[1]; j++) {
                for (int k = 0; k < shape[2]; k++) {
                    System.arraycopy(data[i][j][k], 0, flattenData, ((i * shape[1] + j) * shape[2] + k) * shape[3], shape[3]);
                }
            }
        }
        return new HDArray(shape, flattenData);
    }

    /**
     * Constructor by size.
     * @param shape Shape of this array.
     * @return HDArray with given data.
     * @see #createBySizeWithValue(double, int...)
     */
    public static HDArray createBySize(final int... shape) {
        return HDArray.createBySizeWithValue(HDArray.DEFAULT_VALUE, shape);
    }

    /**
     * Constructor by size and return array is filled by default value.
     * @param defaultValue Default value of this array.
     * @param shape Shape of this array.
     * @return HDArray with given data.
     */
    public static HDArray createBySizeWithValue(final double defaultValue, final int... shape) {
        double[] data = new double[HDArray.countElement(shape)];
        Arrays.fill(data, defaultValue);
        return new HDArray(shape, data);
    }

    /**
     * Get the element by indices.
     * @param indices Indices of target element.
     * @return Element at given indices.
     * @throws NullPointerException if given indices is null.
     * @throws IllegalArgumentException if given indices length does not match with number of dimension of distribution.
     * @throws IndexOutOfBoundsException if given indices contain invalid index.
     */
    public double get(final int... indices) {
        this.verifyIndices(indices);
        return this.data[HDArray.indicesToFlattenIdx(indices, this.shape)];
    }

    /**
     * Set the element by indices.
     * @param value Value to set.
     * @param indices Indices of target element.
     * @throws NullPointerException if given indices is null.
     * @throws IllegalArgumentException if given indices length does not match with number of dimension of distribution.
     * @throws IndexOutOfBoundsException if given indices contain invalid index.
     */
    public void set(final double value, final int... indices) {
        this.verifyIndices(indices);
        this.data[HDArray.indicesToFlattenIdx(indices, this.shape)] = value;
    }

    /**
     * Get the shape of this array.
     * @return Shape of this array.
     */
    public int[] shape() {
        return this.shape;
    }

    /**
     * Get the rank of this array.
     * @return Rank of this array.
     */
    public int rank() {
        return this.shape.length;
    }

    /**
     * Fill the array by given value.
     * @param value Value to fill.
     */
    public void fill(final double value) {
        Arrays.fill(this.data, value);
    }

    /**
     * Get the sum of all elements.
     * @return Sum of all elements.
     */
    public double sum() {
        return Arrays.stream(this.data).sum();
    }

    /**
     * Verify the indices is valid to access element.
     * @param indices Indices of target element.
     * @throws NullPointerException if given indices is null.
     * @throws IllegalArgumentException if given indices length does not match with number of dimension of distribution.
     * @throws IndexOutOfBoundsException if given indices contain invalid index.
     */
    protected void verifyIndices(final int... indices) {
        if (indices.length != shape.length) {
            throw new IllegalArgumentException(
                    Log.genLogMsg(this.getClass(),
                            "Given indices length does not match with number of dimension " +
                                    "of distribution: Number of indices: " + indices.length +
                                    " Number of dimension: " + shape.length));
        }
        for (int i = 0; i < indices.length; i++) {
            if (indices[i] < 0 || indices[i] >= shape[i]) {
                throw new IndexOutOfBoundsException(
                        Log.genLogMsg(this.getClass(),
                                "Given index: " + indices[i] + " at dimension " + i + " is out of bound"));
            }
        }
    }

    /**
     * Sum along given axis.
     * @param axis Axis to sum.
     * @return Sum along given axis.
     * @throws NullPointerException if given axis is null.
     * @throws IllegalArgumentException if given axis contain duplicated index, or invalid index.
     */
    protected HDArray sumAlongAxis(final int... axis) {
        Objects.requireNonNull(axis, Log.genLogMsg(this.getClass(), "Given axis should not be null"));
        if (axis.length == 0) {
            return this.clone();
        }

        // Check is there only unique element
        if (axis.length != Arrays.stream(axis).distinct().count()) {
            throw new IllegalArgumentException(
                    Log.genLogMsg(this.getClass(),
                            "Given axis should be unique. But given " + Arrays.toString(axis)));
        }

        // Check is there any invalid axis
        for (int axi : axis) {
            if (axi < 0 || axi >= this.rank()) {
                throw new IllegalArgumentException(Log.genLogMsg(this.getClass(),
                        "Given axis contain invalid index"));
            }
        }

        // In case that all axis is reduced
        if (axis.length == this.rank()) {
            return HDArray.create(this.sum());
        }

        // Initialize new shape and new data
        int[] newShape = HDArray.removeElementByIndices(this.shape, axis);
        double[] newData = new double[HDArray.countElement(newShape)];
        Arrays.fill(newData, 0.0d);

        // Sum along axis
        for (int flattenIdx=0; flattenIdx<this.data.length; flattenIdx++) {
            int[] indices = HDArray.flattenIdxToIndices(flattenIdx, this.shape);
            int[] newIndices = HDArray.removeElementByIndices(indices, axis);
            int newFlattenIdx = HDArray.indicesToFlattenIdx(newIndices, newShape);
            newData[newFlattenIdx] += this.data[flattenIdx];
        }

        return new HDArray(newShape, newData);
    }

    /**
     * Reshape this array.
     * @param newShape New shape.
     * @return Reshaped array.
     * @throws NullPointerException if given new shape is null.
     * @throws IllegalArgumentException if given new shape does not have same number of elements with current shape.
     */
    public HDArray reshape(final int... newShape) {
        Objects.requireNonNull(newShape, Log.genLogMsg(this.getClass(), "Given new shape should not be null"));
        if (HDArray.countElement(newShape) != HDArray.countElement(this.shape)) {
            throw new IllegalArgumentException(
                    Log.genLogMsg(this.getClass(),
                            "Given new shape should have same number of elements with current shape"));
        }
        return new HDArray(newShape, this.data);
    }

    /**
     * Move axis of this array.
     * @param originalDimensions Original positions of the axes to move. These must be unique.
     * @param toDimensionIndices Destination positions for each of the original axis. These mush also be unique.
     * @return Array with moved axes.
     * @throws NullPointerException if given original dimensions or target dimensions is null.
     * @throws IllegalArgumentException if given original dimensions or target dimensions contain duplicated position,
     * or number of position mismatch, or dimensions contain invalid position (smaller than 0 or exceed array limit).
     */
    public HDArray moveAxis(final int[] originalDimensions, final int[] toDimensionIndices) {
        Objects.requireNonNull(originalDimensions,
                Log.genLogMsg(this.getClass(), "Given original dimensions should not be null")
        );
        Objects.requireNonNull(toDimensionIndices,
                Log.genLogMsg(this.getClass(), "Given target dimensions should not be null")
        );

        // Given original dimensions and target dimensions should have same length
        if (originalDimensions.length != toDimensionIndices.length) {
            throw new IllegalArgumentException(
                    Log.genLogMsg(this.getClass(),
                            "Given original dimensions and target dimensions should have same length"));
        }

        // Check index out of bound
        for (int i = 0; i < originalDimensions.length; i++) {
            if (originalDimensions[i] < 0 || originalDimensions[i] >= this.shape.length) {
                throw new IndexOutOfBoundsException(Log.genLogMsg(this.getClass(),
                        "Given original dimensions contain invalid index"));
            }
            if (toDimensionIndices[i] < 0 || toDimensionIndices[i] >= this.shape.length) {
                throw new IndexOutOfBoundsException(Log.genLogMsg(this.getClass(),
                        "Given target dimensions contain invalid index"));
            }
        }

        // Check duplicated index
        if (originalDimensions.length != Arrays.stream(originalDimensions).distinct().count()) {
            throw new IllegalArgumentException(
                    Log.genLogMsg(this.getClass(), "Given original dimensions contain duplicated index"));
        }
        if (toDimensionIndices.length != Arrays.stream(toDimensionIndices).distinct().count()) {
            throw new IllegalArgumentException(
                    Log.genLogMsg(this.getClass(), "Given target dimensions contain duplicated index"));
        }

        int[] newOrder = new int[this.shape.length];
        for (int i=0; i<this.shape.length; i++) {
            int newDim = i;
            for (int j=0; j<originalDimensions.length; j++) {
                if (i == toDimensionIndices[j]) {
                    newDim = originalDimensions[j];
                    break;
                }
            }
            newOrder[i] = newDim;
        }

        int[] newShape = new int[this.rank()];
        for (int i=0; i<this.rank(); i++) {
            newShape[i] = this.shape[newOrder[i]];
        }

        double[] newData = new double[this.data.length];
        for (int flattenIdx=0; flattenIdx<this.data.length; flattenIdx++) {
            int[] indices = HDArray.flattenIdxToIndices(flattenIdx, this.shape);
            int[] newIndices = new int[indices.length];
            for (int i=0; i<indices.length; i++) {
                newIndices[i] = indices[newOrder[i]];
            }
            int newFlattenIdx = HDArray.indicesToFlattenIdx(newIndices, newShape);
            newData[newFlattenIdx] = this.data[flattenIdx];
        }

        return new HDArray(newShape, newData);
    }

    /**
     * Multiply this array by given value.
     * @param value Value to multiply.
     * @return Multiplied array.
     */
    public HDArray mul(final double value) {
        return this.operation(value, (a, b) -> a * b);
    }

    /**
     * Multiply this array by given value in place.
     * @param value Value to multiply.
     */
    public void muli(final double value) {
        this.operationInPlace(value, (a, b) -> a * b);
    }

    /**
     * Multiply this array by given array.
     * @param otherArray Array to multiply.
     * @return Multiplied array.
     * @throws NullPointerException if given array is null.
     * @throws IllegalArgumentException if given array shape does not match with this array.
     */
    public HDArray mul(final HDArray otherArray) {
        return this.broadcastOperation(otherArray, (a, b) -> a * b);
    }

    /**
     * Add given value to this array.
     * @param value Value to add.
     * @return Added array.
     */
    public HDArray add(final double value) {
        return this.operation(value, Double::sum);
    }

    /**
     * Add given value to this array in place.
     * @param value Value to add.
     */
    public void addi(final double value) {
        this.operationInPlace(value, Double::sum);
    }

    /**
     * Add given array to this array.
     * @param otherArray Array to add.
     * @return Added array.
     * @throws NullPointerException if given array is null.
     * @throws IllegalArgumentException if given array shape does not match with this array.
     */
    public HDArray add(final HDArray otherArray) {
        return this.broadcastOperation(otherArray, Double::sum);
    }

    /**
     * Subtract given value from this array.
     * @param value Value to subtract.
     * @return Subtracted array.
     */
    public HDArray sub(final double value) {
        return this.operation(value, (a, b) -> a - b);
    }

    /**
     * Subtract given value from this array in place.
     * @param value Value to subtract.
     */
    public void subi(final double value) {
        this.operationInPlace(value, (a, b) -> a - b);
    }

    /**
     * Subtract given array from this array.
     * @param otherArray Array to subtract.
     * @return Subtracted array.
     * @throws NullPointerException if given array is null.
     * @throws IllegalArgumentException if given array shape does not match with this array.
     */
    public HDArray sub(final HDArray otherArray) {
        return this.broadcastOperation(otherArray, (a, b) -> a - b);
    }

    /**
     * Divide this array by given value.
     * @param value Value to divide.
     * @return Divided array.
     */
    public HDArray div(final double value) {
        return this.operation(value, (a, b) -> a / b);
    }

    /**
     * Divide this array by given value in place.
     * @param value Value to divide.
     */
    public void divi(final double value) {
        this.operationInPlace(value, (a, b) -> a / b);
    }

    /**
     * Divide this array by given array.
     * @param otherArray Array to divide.
     * @return Divided array.
     * @throws NullPointerException if given array is null.
     * @throws IllegalArgumentException if given array shape does not match with this array.
     */
    public HDArray div(final HDArray otherArray) {
        return this.broadcastOperation(otherArray, (a, b) -> a / b);
    }

    /**
     * Perform given operation on this array.
     * @param operand Operand to perform.
     * @param operation Operation to perform.
     * @return Result array.
     * @throws NullPointerException if given operation is null.
     */
    protected HDArray operation(final double operand, final HDArray.elementOperation operation) {
        Objects.requireNonNull(operation, Log.genLogMsg(this.getClass(), "Given operation should not be null"));
        double[] newData = new double[this.data.length];
        Arrays.setAll(newData, idx -> operation.operate(this.data[idx], operand));
        return new HDArray(this.shape, newData);
    }

    /**
     * Perform given operation on this array in place.
     * @param operand Operand to perform.
     * @param operation Operation to perform.
     * @throws NullPointerException if given operation is null.
     */
    protected void operationInPlace(final double operand, final HDArray.elementOperation operation) {
        Objects.requireNonNull(operation, Log.genLogMsg(this.getClass(), "Given operation should not be null"));
        Arrays.setAll(this.data, idx -> operation.operate(this.data[idx], operand));
    }

    /**
     * Perform given operation on this array.
     * @param array Operand array.
     * @param operation Operation to perform.
     * @return Result array.
     * @throws NullPointerException if given array or operation is null.
     * @throws IllegalArgumentException if given array shape does not match with this array.
     */
    protected HDArray broadcastOperation(final HDArray array, final HDArray.elementOperation operation) {
        Objects.requireNonNull(array, Log.genLogMsg(this.getClass(), "Given array should not be null"));
        Objects.requireNonNull(operation, Log.genLogMsg(this.getClass(), "Given operation should not be null"));

        final int[] broadcastShape = HDArray.detectBroadcastShape(this.shape, array.shape);
        final HDArray array1 = HDArray.broadcastArray(this, broadcastShape);
        final HDArray array2 = HDArray.broadcastArray(array, broadcastShape);

        double[] resultData = new double[HDArray.countElement(broadcastShape)];
        Arrays.setAll(resultData, idx -> operation.operate(array1.data[idx], array2.data[idx]));

        return new HDArray(broadcastShape, resultData);
    }

    /**
     * Normalize the element in this array
     * @throws ArithmeticException if the sum of all elements is zero.
     * @see #sum()
     * @see #divi(double)
     */
    public void normalize() {
        this.divi(this.sum());
    }

    /**
     * Broadcast given array to target shape.
     * @param array Array to broadcast.
     * @param targetShape Target shape.
     * @return Broadcast array.
     * @throws NullPointerException if given array or target shape is null.
     * @throws IllegalArgumentException if given target shape does not have enough dimension to broadcast.
     * Or else, given array cannot be broadcast to target shape.
     */
    protected static HDArray broadcastArray(final HDArray array, int[] targetShape) {
        Objects.requireNonNull(array, Log.genLogMsg(HDArray.class, "Given array should not be null"));
        Objects.requireNonNull(targetShape,
                Log.genLogMsg(HDArray.class, "Given target shape should not be null"));
        if (array.rank() > targetShape.length) {
            throw new IllegalArgumentException(
                    Log.genLogMsg(HDArray.class,
                            "Given target shape should have at least " + array.rank() + " dimensions"));
        }

        for (int i=0; i<array.shape.length; i++) {
            final int originDim = array.shape[i];
            final int targetDim = targetShape[i];
            if (originDim != targetDim && originDim != 1) {
                throw new IllegalArgumentException(
                        Log.genLogMsg(HDArray.class,
                                "Given array with shape " + Arrays.toString(array.shape) + " cannot be " +
                                        "broadcast to target shape " + Arrays.toString(targetShape)));
            }
        }

        // Detect which axis should be appended
        final boolean[] appendAxis = new boolean[targetShape.length];
        for (int i=0; i<targetShape.length; i++) {
            appendAxis[i] = array.shape[i] != targetShape[i];
        }

        // Copy the element in the axis to be append
        HDArray newArray = HDArray.createBySize(targetShape);
        for (int flattenIdx=0; flattenIdx<newArray.data.length; flattenIdx++) {
            int[] targetIndices = HDArray.flattenIdxToIndices(flattenIdx, targetShape);
            int[] searchIndices = targetIndices.clone();
            for (int i=0; i<targetIndices.length; i++) {
                if (appendAxis[i]) {
                    searchIndices[i] = 0;
                }
            }
            final double value = array.get(searchIndices);
            newArray.set(value, targetIndices);
        }

        return newArray;
    }

    /**
     * Detect the broadcast shape of given two shapes.
     * @param shape1 Shape 1.
     * @param shape2 Shape 2.
     * @return Broadcast shape.
     * @throws NullPointerException if given shape1 or shape2 is null.
     * @throws IllegalArgumentException if given shape1 or shape2 is empty.
     */
    protected static int[] detectBroadcastShape(final int[] shape1, final int[] shape2) {
        Objects.requireNonNull(shape1, Log.genLogMsg(HDArray.class, "Given shape1 should not be null"));
        Objects.requireNonNull(shape2, Log.genLogMsg(HDArray.class, "Given shape2 should not be null"));

        final int targetRank = Math.max(shape1.length, shape2.length);
        final int[] paddedShape1 = HDArray.paddingArray(shape1, targetRank);
        final int[] paddedShape2 = HDArray.paddingArray(shape2, targetRank);

        final int[] broadcastShape = new int[targetRank];
        Arrays.setAll(broadcastShape, idx -> Math.max(paddedShape1[idx], paddedShape2[idx]));

        return broadcastShape;
    }

    /**
     * Padding given array to given length. <br/>
     * Default padding value is 0. <br/>
     * Padding in-front by default. <br/>
     * @param array Array to padding.
     * @param length Target length after padding.
     * @return Padded array.
     * @throws NullPointerException if given array is null.
     * @throws IllegalArgumentException if given length is smaller than array length.
     * @see #paddingArray(int[], int, int, boolean)
     */
    protected static int[] paddingArray(final int[] array, final int length) {
        return HDArray.paddingArray(array, length, 1, true);
    }

    /**
     * Padding given array to given length and padding value. <br/>
     * Padding in-front by default. <br/>
     * @param array Array to padding.
     * @param length Target length after padding.
     * @param paddingValue Padding value.
     * @return Padded array.
     * @throws NullPointerException if given array is null.
     * @throws IllegalArgumentException if given length is smaller than array length.
     * @see #paddingArray(int[], int, int, boolean)
     */
    protected static int[] paddingArray(final int[] array, final int length, final int paddingValue) {
        return HDArray.paddingArray(array, length, paddingValue, true);
    }

    /**
     * Padding given array to given length and padding value. <br/>
     * @param array Array to padding.
     * @param length Target length after padding.
     * @param paddingValue Padding value.
     * @param atFront {@code True} to padding in-front. Or else, padding at the end.
     * @return Padded array.
     * @throws NullPointerException if given array is null.
     * @throws IllegalArgumentException if given length is smaller than array length.
     */
    protected static int[] paddingArray(final int[] array, final int length, final int paddingValue,
                                        final boolean atFront) {
        int[] newArray = new int[length];
        if (atFront) {
            System.arraycopy(array, 0, newArray, length - array.length, array.length);
            Arrays.fill(newArray, 0, length - array.length, paddingValue);
        } else {
            System.arraycopy(array, 0, newArray, 0, array.length);
            Arrays.fill(newArray, array.length, length, paddingValue);
        }
        return newArray;
    }

    /**
     * Padding dimension to this array.
     * @param targetLength Target length after padding.
     * @param atFront {@code True} to padding in-front. Or else, padding at the end.
     * @return Padded array.
     * @throws IllegalArgumentException if given target length is smaller than current length.
     */
    public HDArray appendDimension(final int targetLength, final boolean atFront) {
        if (targetLength < this.shape.length) {
            throw new IllegalArgumentException(
                    Log.genLogMsg(HDArray.class,
                            "Given target length: " + targetLength +" is smaller than current length:" +
                                    this.shape.length));
        }
        return this.reshape(HDArray.paddingArray(this.shape, targetLength, 1, atFront));
    }

    /**
     * Convert indices to flatten index.
     * @param indices Indices.
     * @param shape Shape of array.
     * @return Flatten index.
     * @throws NullPointerException if given indices or shape is null.
     * @throws IllegalArgumentException if given indices length does not match with number of dimension of distribution.
     * @throws IndexOutOfBoundsException if given indices contain invalid index.
     * @see #flattenIdxToIndices(int, int...)
     */
    protected static int indicesToFlattenIdx(final int[] indices, final int[] shape) {
        Objects.requireNonNull(indices, Log.genLogMsg(HDArray.class, "Given indices should not be null"));
        Objects.requireNonNull(shape, Log.genLogMsg(HDArray.class, "Given shape should not be null"));
        if (indices.length != shape.length) {
            throw new IllegalArgumentException(
                    Log.genLogMsg(HDArray.class,
                            "Given indices length does not match with number of dimension " +
                                    "of distribution: Number of indices: " + indices.length +
                                    " Number of dimension: " + shape.length));
        }
        for (int i = 0; i < indices.length; i++) {
            if (indices[i] < 0 || indices[i] >= shape[i]) {
                throw new IndexOutOfBoundsException(
                        Log.genLogMsg(HDArray.class,
                                "Given index: " + indices[i] + " at dimension " + i + " is out of bound"));
            }
        }
        int flattenIdx = 0;
        for (int i = 0; i < indices.length; i++) {
            flattenIdx *= shape[i];
            flattenIdx += indices[i];
        }
        return flattenIdx;
    }

    /**
     * Convert flatten index to indices.
     * @param flattedIdx Flatten index.
     * @param shape Shape of array.
     * @return Indices.
     * @throws NullPointerException if given shape is null.
     * @throws IllegalArgumentException if given flatten index is smaller than 0, or larger than number of elements.
     * @see #indicesToFlattenIdx(int[], int...)
     */
    protected static int[] flattenIdxToIndices(final int flattedIdx, final int... shape) {
        Objects.requireNonNull(shape, Log.genLogMsg(HDArray.class, "Given shape should not be null"));
        if (flattedIdx < 0 || flattedIdx >= HDArray.countElement(shape)) {
            throw new IllegalArgumentException(
                    Log.genLogMsg(HDArray.class,
                            "Given flatten index is out of bound: " + flattedIdx));
        }
        int[] indices = new int[shape.length];
        int tmpIdx = flattedIdx;
        for (int i = shape.length - 1; i >= 0; i--) {
            indices[i] = tmpIdx % shape[i];
            tmpIdx /= shape[i];
        }
        return indices;
    }

    /**
     * Count number of elements in given shape.
     * @param shape Shape of array.
     * @return Number of elements.
     * @throws NullPointerException if given shape is null.
     */
    protected static int countElement(int... shape) {
        Objects.requireNonNull(shape, Log.genLogMsg(HDArray.class, "Given shape should not be null"));
        return Arrays.stream(shape).reduce(1, (a, b) -> a * b);
    }

    /**
     * Remove element from given array by indices.
     * @param array Array to remove.
     * @param indices Indices to remove.
     * @return Array without removed element.
     * @throws NullPointerException if given array or indices is null.
     * @throws IllegalArgumentException if given indices contain duplicated index, or invalid index.
     */
    protected static int[] removeElementByIndices(final int[] array, final int[] indices) {
        Objects.requireNonNull(array, Log.genLogMsg(HDArray.class, "Given array should not be null"));
        Objects.requireNonNull(indices, Log.genLogMsg(HDArray.class, "Given indices should not be null"));
        if (indices.length == 0) {
            return array.clone();
        }
        int[] newArray = new int[array.length - indices.length];
        int newArrayIdx = 0;
        for (int i = 0; i < array.length; i++) {
            final int finalI = i;
            if (Arrays.stream(indices).noneMatch(idx -> idx == finalI)) {
                newArray[newArrayIdx++] = array[i];
            }
        }
        return newArray;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(this.data);
        result = 31 * result + Arrays.hashCode(this.shape);
        return result;
    }

    @Override
    public boolean equals(Object otherObj) {
        if (this.getClass() != otherObj.getClass()) return false;
        HDArray otherArray = (HDArray) otherObj;
        if (!Arrays.equals(this.shape, otherArray.shape)) return false;
        for (int idx=0; idx<this.data.length; idx++) {
            if (Math.abs(this.data[idx] - otherArray.data[idx]) > 1e-6) return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        this.toStringHelper(result, this.data, this.shape, 0, new int[this.shape.length]);
        return result.toString();
    }

    protected void toStringHelper(StringBuilder result, double[] array, int[] dimensions, int depth, int[] indices) {
        if (depth == dimensions.length - 1) {
            // Base case: print elements at the last dimension
            result.append("[");
            for (int i = 0; i < dimensions[depth]; i++) {
                indices[depth] = i;
                result.append(array[HDArray.indicesToFlattenIdx(indices, this.shape)]);

                if (i < dimensions[depth] - 1) {
                    result.append(", ");
                }
            }
            result.append("]");
        } else {
            // Recursive case: print elements at the current dimension and move to the next dimension
            result.append("[");
            for (int i = 0; i < dimensions[depth]; i++) {
                indices[depth] = i;
                toStringHelper(result, array, dimensions, depth + 1, indices);

                if (i < dimensions[depth] - 1) {
                    result.append(", ");
                }
            }
            result.append("]");
        }
    }

    @Override
    public HDArray clone() {
        try {
            HDArray clone = (HDArray) super.clone();
            clone.data = this.data.clone();
            clone.shape = this.shape.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    protected interface elementOperation {
        double operate(double a, double b);
    }
}
