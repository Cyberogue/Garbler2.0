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
public class UnboundHeatlist extends BoundedHeatlist {

    /**
     * Constructor
     *
     * @param size the number of keys (indeces) in the Heatlist
     */
    public UnboundHeatlist(int size) {
        super(size);
    }

    /**
     * Obtains the cumulative Heatlist of a source Heatlist
     *
     * @param source the source Heatlist
     * @return the cumulative Heatlist of a source Heatlist
     */
    public static UnboundHeatlist getCumulative(BoundedHeatlist source) {
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
    public static UnboundHeatlist getCumulative(BoundedHeatlist source, int startIndex, int count) {
        if (startIndex < 0 || startIndex >= source.values.length || count < 0 || startIndex + count > source.values.length) {
            throw new ArrayIndexOutOfBoundsException();
        }

        UnboundHeatlist map = new UnboundHeatlist(count);
        float sum = 0;
        int endIndex = startIndex + count;
        for (int i = startIndex; i < endIndex; i++) {
            sum += source.values[i];
            map.values[i - startIndex] = sum;
        }
        return map;
    }

    /**
     * Returns a sub-Heatlist of another
     *
     * @param source the source Heatlist
     * @param startIndex the index to start summing from
     * @param count the number of indeces to count
     * @return a sub-Heatlist of another
     */
    public static UnboundHeatlist extract(BoundedHeatlist source, int startIndex, int count) {
        if (startIndex < 0 || startIndex >= source.values.length || count < 0 || startIndex + count > source.values.length) {
            throw new ArrayIndexOutOfBoundsException();
        }

        UnboundHeatlist map = new UnboundHeatlist(count);
        System.arraycopy(source.values, startIndex, map.values, 0, count);
        return map;
    }

    /**
     * Adds the values and sample count of two Heatlists together
     *
     * @param a
     * @param b
     * @return a new Heatlist instance
     */
    public static UnboundHeatlist getSum(BoundedHeatlist a, BoundedHeatlist b) {
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
     * @return a new Heatlist instance
     */
    public static UnboundHeatlist getProduct(BoundedHeatlist a, float b) {
        UnboundHeatlist result = new UnboundHeatlist(a.values.length);
        for (int i = 0; i < a.values.length; i++) {
            result.values[i] = a.values[i] * b;
        }
        result.samples = (int) (a.samples * b);
        return result;
    }
}
