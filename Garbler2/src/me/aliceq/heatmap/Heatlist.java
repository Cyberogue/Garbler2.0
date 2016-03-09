/*
 * The MIT License
 *
 * Copyright 2016 Alice Quiros <email@aliceq.me>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package me.aliceq.heatmap;

/**
 * A class containing a list of floating point values with normalized and
 * unnormalized methods.
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public class Heatlist {

    private int samples;
    private float[] values;
    private boolean normalized; // Turned off if an unnormalizing method is called

    /**
     * Constructor for a single-element Heatlist
     */
    public Heatlist() {
        this(1);
    }

    /**
     * Constructor
     *
     * @param size the number of elements (indeces) in the Heatlist
     */
    public Heatlist(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Unable to make Heatlist of size " + size);
        }

        samples = 0;
        values = new float[size];
        normalized = true;
    }

    /**
     * Returns the number of samples taken
     *
     * @return the number of samples taken
     */
    public int getSampleCount() {
        return samples;
    }

    /**
     * Returns the map's size
     *
     * @return the size of the map
     */
    public int getSize() {
        return values.length;
    }

    /**
     * Returns an array of all values in the map
     *
     * @return an array of all values in the map
     */
    public float[] getValues() {
        return values.clone();
    }

    /**
     * Gets a single value at a specified index
     *
     * @param index the index to retrieve
     * @return a floating point value
     */
    public float getValue(int index) {
        if (index < 0 || index >= values.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return values[index];
    }

    /**
     * Returns the sum of all values
     *
     * @return the sum of all values
     */
    public float getTotal() {
        float sum = 0;
        for (int i = 0; i < values.length; i++) {
            sum += values[i];
        }
        return sum;
    }

    /**
     * Returns the sum of all the values in a given range
     *
     * @param startIndex the index to start searching from (inclusive)
     * @param endIndex the index to search up to (non-inclusive)
     * @return the sum all the of values in a given range
     */
    public float getSum(int startIndex, int endIndex) {
        if (startIndex < 0 || startIndex > values.length || endIndex < 0 || endIndex > values.length) {
            throw new ArrayIndexOutOfBoundsException();
        }

        float sum = 0;
        for (int i = startIndex; i < endIndex; i++) {
            sum += values[i];
        }
        return sum;
    }

    /**
     * Returns the maximum value in the Heatlist
     *
     * @return the maximum value in the Heatlist
     */
    public float getMax() {
        return getMax(0, values.length);
    }

    /**
     * Returns the maximum value in a range
     *
     * @param startIndex the index to start searching from (inclusive)
     * @param endIndex the index to search up to (non-inclusive)
     * @return the maximum value in a range
     */
    public float getMax(int startIndex, int endIndex) {
        if (startIndex < 0 || startIndex >= values.length || endIndex < 0 || endIndex > values.length) {
            throw new ArrayIndexOutOfBoundsException();
        }

        float max = values[startIndex];
        for (int i = startIndex + 1; i < endIndex; i++) {
            if (values[i] > max) {
                max = values[i];
            }
        }
        return max;
    }

    /**
     * Returns the minimum value in the Heatlist
     *
     * @return the minimum value in the Heatlist
     */
    public float getMin() {
        return getMin(0, values.length);
    }

    /**
     * Returns the minimum value in a range
     *
     * @param startIndex the index to start searching from (inclusive)
     * @param endIndex the index to search up to (non-inclusive)
     * @return the minimum value in a range
     */
    public float getMin(int startIndex, int endIndex) {
        if (startIndex < 0 || startIndex >= values.length || endIndex < 0 || endIndex > values.length) {
            throw new ArrayIndexOutOfBoundsException();
        }

        float min = values[startIndex];
        for (int i = startIndex + 1; i < endIndex; i++) {
            if (values[i] < min) {
                min = values[i];
            }
        }
        return min;
    }

    /**
     * Returns the first index containing a value greater than the threshold
     *
     * @param threshold the threshold to check
     * @return the first index containing a value greater than the threshold, or
     * -1 if none
     */
    public int indexOfGreater(int threshold) {
        return indexOfGreater(0, values.length, threshold);
    }

    /**
     * Returns the first index containing a value greater than the threshold
     *
     * @param startIndex the index to start searching from (inclusive)
     * @param endIndex the index to search up to (non-inclusive)
     * @param threshold the threshold to check
     * @return the first index containing a value greater than the threshold, or
     * -1 if none
     */
    public int indexOfGreater(int startIndex, int endIndex, int threshold) {
        if (startIndex < 0 || startIndex > values.length || endIndex < 0 || endIndex > values.length) {
            throw new ArrayIndexOutOfBoundsException();
        }

        for (int i = startIndex; i < endIndex; i++) {
            if (i > threshold) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the last index containing a value greater than the threshold
     *
     * @param threshold the threshold to check
     * @return the first index containing a value greater than the threshold, or
     * -1 if none
     */
    public int lastIndexOfGreater(int threshold) {
        return lastIndexOfGreater(0, values.length, threshold);
    }

    /**
     * Returns the last index containing a value greater than the threshold
     *
     * @param startIndex the index to start searching from (inclusive)
     * @param endIndex the index to search up to (non-inclusive)
     * @param threshold the threshold to check
     * @return the first index containing a value greater than the threshold, or
     * -1 if none
     */
    public int lastIndexOfGreater(int startIndex, int endIndex, int threshold) {
        if (startIndex < 0 || startIndex > values.length || endIndex < 0 || endIndex > values.length) {
            throw new ArrayIndexOutOfBoundsException();
        }

        for (int i = endIndex - 1; i >= startIndex; i--) {
            if (i > threshold) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Increments a given index by 1
     *
     * @param index the index to increment
     * @return the new value at the given index
     */
    public float increment(int index) {
        return increment(index, 1);
    }

    /**
     * Increments a given index by some amount of samples.
     *
     * @param index the index to increment
     * @param amount the number of samples to increment
     * @return the new value at the given index
     */
    public float increment(int index, int amount) {
        if (index < 0 || index >= values.length) {
            throw new ArrayIndexOutOfBoundsException();
        } else if (amount <= 0) {
            throw new IllegalArgumentException("Count has to be greater than zero");
        }

        samples += amount;
        float weight = (float) amount / samples;
        float invweight = 1 - weight;

        for (int i = 0; i < values.length; i++) {
            values[i] *= invweight;
        }
        return values[index] += weight;
    }

    /**
     * Scales all values in the Heatlist so that their collective sum equals 1.
     */
    public void normalize() {
        float nFactor = 1 / getTotal();  // Normalizing factor
        for (int i = 0; i < values.length; i++) {
            values[i] *= nFactor;
        }
    }

    /**
     * Equalizes and normalizes all values in the Heatlist
     *
     * @return the equalized value
     */
    public float equalize() {
        float value = 1 / samples;
        for (int i = 0; i < values.length; i++) {
            values[i] = value;
        }

        return value;
    }

    /**
     * Resets all values in the map to zero
     */
    public void reset() {
        for (int i = 0; i < values.length; i++) {
            values[i] = 0;
        }
    }

    /**
     * Clears all values and samples in the map
     */
    public void clear() {
        for (int i = 0; i < values.length; i++) {
            values[i] = 0;
        }
        samples = 0;
    }

    /**
     * Checks whether or not a Heatlist is empty
     *
     * @return false if the sample size is less than 1
     */
    public boolean empty() {
        return samples <= 0;
    }

    /**
     * Verifies that the list is normalized
     *
     * @return true when normalized, otherwise false
     */
    public boolean isNormalized() {
        return normalized;
    }

    /**
     * Marks a Heatlist as denormalized
     */
    public void markDirty() {
        normalized = false;
    }

    /**
     * Creates a new value at an index initialized to 0. Previous values get
     * shifted right.
     *
     * @param index the index to push a zero into
     */
    public void addNewAtIndex(int index) {
        if (index < 0 || index > values.length) {
            throw new ArrayIndexOutOfBoundsException();
        }

        float[] newValues = new float[values.length + 1];

        System.arraycopy(values, 0, newValues, 0, index);
        System.arraycopy(values, index, newValues, index + 1, values.length - index);
        newValues[index] = 0;

        values = newValues;
    }

    @Override
    public String toString() {
        if (values.length <= 0) {
            return "";
        }

        String s = "{" + values[0];
        for (int i = 1; i < values.length; i++) {
            s += "," + values[i];
        }
        s += "}";
        return s;
    }

    /**
     * Create a copy of the Heatlist instance with the same sample count and
     * values
     *
     * @return a copy of the Heatlist instance
     */
    public Heatlist copy() {
        Heatlist map = new Heatlist(values.length);
        System.arraycopy(values, 0, map.values, 0, values.length);
        map.samples = samples;
        return map;
    }

    /**
     * Merges two heatlists together. The resulting values are the average of
     * the two and the resulting sample count is the sum of the two. The result
     * is normalized if both input lists are normalized. If the lists are of
     * different sizes an exception is thrown.
     *
     * @param a The first heatlist
     * @param b The second heatlist
     * @return The average of two Heatlists
     */
    public static Heatlist merge(Heatlist a, Heatlist b) {
        return interpolate(a, b, 0.5f);
    }

    /**
     * Merges multiple heatlists together. The resulting values are the average
     * of all the lists and the resulting sample count is their sum. The result
     * is normalized if all input lists are normalized. If the lists are of
     * different sizes an exception is thrown.
     *
     * @param lists
     * @return
     */
    public static Heatlist merge(Heatlist[] lists) {
        Heatlist result = new Heatlist(lists[0].values.length);
        float weight = 1 / lists[0].values.length;

        for (int i = 0; i < lists.length; i++) {
            if (lists[i].values.length != lists[0].values.length) {
                throw new IllegalArgumentException("Referenced heatlists must be of same size");
            }

            for (int j = 0; j < lists[i].values.length; j++) {
                result.values[j] += lists[i].values[j] * weight;
            }
            result.samples += lists[i].samples;
            result.normalized &= lists[i].normalized;
        }

        return result;
    }

    /**
     * Interpolates between two Heatlists. The resulting values are interpolated
     * between both lists and the resulting sample count is the sum of the
     * two.The result is normalized if both input lists are normalized. If the
     * lists are of different sizes an exception is thrown.
     *
     * @param a
     * @param b
     * @param value The interpolation value. A value of 0 returns Heatlist a and
     * a value of 1 returns Heatlist b.
     * @return a Heatlist whose values are the average of two other's
     */
    public static Heatlist interpolate(Heatlist a, Heatlist b, float value) {
        if (a.values.length != b.values.length) {
            throw new IllegalArgumentException("Referenced heatlists must be of same size");
        } else if (value < 0 || value > 1) {
            throw new IllegalArgumentException("Interpolation value must be between 0 and 1");
        }

        float va = value;
        float vb = 1f - value;

        Heatlist result = new Heatlist(a.values.length);
        result.samples = a.samples + b.samples;
        for (int i = 0; i < a.values.length; i++) {
            result.values[i] = a.values[i] * va + b.values[i] * vb;
        }
        result.normalized = a.normalized && b.normalized;
        return result;
    }

    /**
     * Creates a cumulative-sum Heatlist from a source list.
     *
     * @param source the source list
     * @return a new Heatlist
     */
    public static Heatlist getCumulative(Heatlist source) {
        Heatlist result = new Heatlist(source.values.length);
        float sum = 0;
        for (int i = 0; i < result.values.length; i++) {
            sum += source.values[i];
            result.values[i] = sum;
        }
        result.normalized = false;
        return result;
    }
}
