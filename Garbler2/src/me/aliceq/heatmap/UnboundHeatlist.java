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
 * An extension of a Heatlist which does not guarantee a cumulative sum of
 * approximately 1.0f
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public class UnboundHeatlist extends Heatlist {

    /**
     * Constructor
     *
     * @param size the number of keys (indeces) in the Heatlist
     */
    public UnboundHeatlist(int size) {
        super(size);
    }

    /**
     * Overwrites all field values with new values. The source object must be of
     * the same size as the destination object.
     *
     * @param newValues the new values to copy in
     */
    protected void overwriteValues(float[] newValues) {
        if (newValues.length != this.values.length) {
            throw new IllegalArgumentException("Mismatched array sizes");
        }
        overwriteValues(newValues, 0);
    }

    /**
     * Overwrites the current value set within a given range
     *
     * @param newValues The new values to write in
     * @param fromIndex the index to start writing into
     */
    protected void overwriteValues(float[] newValues, int fromIndex) {
        if (fromIndex + newValues.length > this.values.length) {
            throw new IllegalArgumentException("Overwrite range excedes source length");
        }

        System.arraycopy(newValues, 0, this.values, fromIndex, newValues.length);
    }

    /**
     * Obtains the cumulative Heatlist of a source Heatlist
     *
     * @param source the source Heatlist
     * @return the cumulative Heatlist of a source Heatlist
     */
    public static UnboundHeatlist getCumulative(Heatlist source) {
        return UnboundHeatlist.getCumulative(source, 0, source.values.length);
    }

    /**
     * Obtains the cumulative Heatlist of a source Heatlist within a provided
     * range
     *
     * @param source the source Heatlist
     * @param startIndex the index to start summing from
     * @param count the number of indeces to count
     * @return the cumulative Heatlist of a source Heatlist within a provided
     * range
     */
    public static UnboundHeatlist getCumulative(Heatlist source, int startIndex, int count) {
        if (startIndex < 0 || startIndex >= source.values.length || count < 0 || startIndex + count > source.values.length) {
            throw new ArrayIndexOutOfBoundsException();
        }

        UnboundHeatlist list = new UnboundHeatlist(count);
        float sum = 0;
        int endIndex = startIndex + count;
        for (int i = startIndex; i < endIndex; i++) {
            sum += source.values[i];
            list.values[i - startIndex] = sum;
        }
        return list;
    }

    /**
     * Returns a sub-Heatlist of another
     *
     * @param source the source Heatlist
     * @param startIndex the index to start summing from
     * @param count the number of indeces to count
     * @return a sub-Heatlist of another
     */
    public static UnboundHeatlist extract(Heatlist source, int startIndex, int count) {
        if (startIndex < 0 || startIndex >= source.values.length || count < 0 || startIndex + count > source.values.length) {
            throw new ArrayIndexOutOfBoundsException();
        }

        UnboundHeatlist list = new UnboundHeatlist(count);
        System.arraycopy(source.values, startIndex, list.values, 0, count);
        return list;
    }

    /**
     * Transform a source Heatlist into an unbound heatlist
     *
     * @param source the source to copy from
     * @return a new UnboundHeatlist instance
     */
    public static UnboundHeatlist makeFrom(Heatlist source) {
        UnboundHeatlist list = new UnboundHeatlist(source.values.length);
        System.arraycopy(source.values, 0, list.values, 0, source.values.length);
        return list;
    }

    /**
     * Adds the values and sample count of two Heatlists together
     *
     * @param a
     * @param b
     * @return a new Heatlist instance
     */
    public static UnboundHeatlist getSum(Heatlist a, Heatlist b) {
        UnboundHeatlist result = new UnboundHeatlist(a.values.length);
        for (int i = 0; i < a.values.length; i++) {
            result.values[i] = a.values[i] + b.values[i];
        }
        result.samples = a.samples + b.samples;
        return result;
    }

    /**
     * Scales a Heatlist's values and sample count by a float (rounded down)
     *
     * @param a
     * @param b
     * @param multiplySamples If true, multiply the sample count by b
     * @return a new Heatlist instance
     */
    public static UnboundHeatlist getProduct(Heatlist a, float b, boolean multiplySamples) {
        UnboundHeatlist result = new UnboundHeatlist(a.values.length);
        for (int i = 0; i < a.values.length; i++) {
            result.values[i] = a.values[i] * b;
        }
        if (multiplySamples) {
            result.samples = (int) (a.samples * b);
        }
        return result;
    }
}
