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
package me.aliceq.garbler;

/**
 * Class which maintains a normalized heat map of float values. At any time the
 * sum of all values should remain at 1.0f.
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public class Heatmap {

    protected int samples;
    protected final float[] values;

    /**
     * Constructor
     *
     * @param size the number of keys (indeces) in the Heatmap
     */
    public Heatmap(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Unable to make Heatmap of size " + size);
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
    public float inc(int index) {
        return inc(index, 1);
    }

    /**
     * Increments a given index by some amount of samples
     *
     * @param index the index to increment
     * @param count the number of samples to increment
     * @return the new value at the given index
     */
    public float inc(int index, int count) {
        if (index < 0 || index >= values.length) {
            throw new ArrayIndexOutOfBoundsException();
        } else if (count <= 0) {
            throw new IllegalArgumentException("Count has to be greater than zero");
        }

        samples += count;
        float invweight = 1 - (float) count / samples;

        for (int i = 0; i < values.length; i++) {
            values[i] *= invweight;
        }
        return values[index] += (float) count / samples;// values[index] += count / samples;
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

    public float getSum() {
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
     * Returns the maximum value in the Heatmap
     *
     * @return the maximum value in the Heatmap
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
     * Returns the minimum value in the Heatmap
     *
     * @return the minimum value in the Heatmap
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
     * Sets every value to an equal value with a total sum of 1.0
     *
     * @return the new value of all indeces
     */
    public float equalize() {
        float value = 1 / values.length;
        for (int i = 0; i < values.length; i++) {
            values[i] = value;
        }
        return value;
    }

    /**
     * Create a copy of the Heatmap instance with the same sample count and
     * values
     *
     * @return a copy of the Heatmap instance
     */
    public Heatmap copy() {
        Heatmap map = new Heatmap(this.values.length);
        System.arraycopy(this.values, 0, map.values, 0, this.values.length);
        map.samples = this.samples;
        return map;
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
     * Returns a Heatmap whose values are the average of two other's
     *
     * @param a
     * @param b
     * @return a Heatmap whose values are the average of two other's
     */
    public static Heatmap average(Heatmap a, Heatmap b) {
        return interpolate(a, b, 0.5f);
    }

    /**
     * Returns a Heatmap whose values are interpolated between two other's
     *
     * @param a
     * @param b
     * @param value The interpolation value. A value of 0 returns Heatmap a and
     * a value of 1 returns Heatmap b.
     * @return a Heatmap whose values are the average of two other's
     */
    public static Heatmap interpolate(Heatmap a, Heatmap b, float value) {
        if (a.values.length != b.values.length) {
            throw new IllegalArgumentException("Referenced heatmaps must be of same size");
        } else if (value < 0 || value > 1) {
            throw new IllegalArgumentException("Interpolation value must be between 0 and 1");
        }

        float va = value;
        float vb = 1f - value;

        Heatmap result = new Heatmap(a.values.length);
        result.samples = a.samples + b.samples;
        for (int i = 0; i < a.values.length; i++) {
            result.values[i] = a.values[i] * va + b.values[i] * vb;
        }
        return result;
    }

    public static void main(String[] args) {
        Heatmap map = new Heatmap(16);

        java.util.Random r = new java.util.Random();
        for (int i = 0; i < 100000; i++) {
            float f = r.nextFloat();
            map.inc((int) (f * f * 16));
        }

        System.out.println(map + " SUM " + map.getSum());
        System.out.println("MIN " + map.getMin());
        System.out.println("MAX " + map.getMax());
        System.out.println("CUM " + UnboundHeatmap.getCumulative(map));
        System.out.println("CUM " + UnboundHeatmap.extract(map, 1, 5));
    }

}
