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
 * Parent class for BoundedHeatlist and UnboundHeatlist
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public abstract class Heatlist {

    protected int samples;
    protected final float[] values;

    /**
     * Constructor
     *
     * @param size the number of keys (indeces) in the Heatlist
     */
    public Heatlist(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Unable to make Heatlist of size " + size);
        }

        this.samples = 0;
        this.values = new float[size];
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
     * Checks whether or not a Heatlist is empty
     *
     * @return false if the sample size is less than 1
     */
    public boolean empty() {
        return samples <= 0;
    }

    /**
     * Sets the sample count
     *
     * @param samples the number of samples
     */
    protected void setSampleCount(int samples) {
        this.samples = samples;
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
     * Increments a given index by 1
     *
     * @param index the index to increment
     * @return the new value at the given index
     */
    public float increment(int index) {
        return increment(index, 1);
    }

    /**
     * Increments a given index by some amount of samples
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
        float invweight = 1 - (float) amount / samples;

        for (int i = 0; i < values.length; i++) {
            values[i] *= invweight;
        }
        return values[index] += (float) amount / samples;// values[index] += count / samples;
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
     * Returns the sum of all values
     *
     * @return the sum of all values
     */
    public float getTotal() {
        return getSum(0, values.length);
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
}