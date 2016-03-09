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
 * Class which maintains a normalized heat map of float values. At any time the
 * sum of all values should remain at 1.0f.
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public class BoundedHeatlist extends Heatlist {

    public BoundedHeatlist(int size) {
        super(size);
    }

    /**
     * Create a copy of the Heatlist instance with the same sample count and
     * values
     *
     * @return a copy of the Heatlist instance
     */
    public Heatlist copy() {
        Heatlist map = new BoundedHeatlist(this.values.length);
        System.arraycopy(this.values, 0, map.values, 0, this.values.length);
        map.samples = this.samples;
        return map;
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
     * Returns a Heatlist whose values are the average of two other's
     *
     * @param a
     * @param b
     * @return a Heatlist whose values are the average of two other's
     */
    public static Heatlist average(BoundedHeatlist a, BoundedHeatlist b) {
        return interpolate(a, b, 0.5f);
    }

    /**
     * Returns a Heatlist whose values are interpolated between two other's
     *
     * @param a
     * @param b
     * @param value The interpolation value. A value of 0 returns Heatlist a and
     * a value of 1 returns Heatlist b.
     * @return a Heatlist whose values are the average of two other's
     */
    public static Heatlist interpolate(BoundedHeatlist a, BoundedHeatlist b, float value) {
        if (a.values.length != b.values.length) {
            throw new IllegalArgumentException("Referenced heatmaps must be of same size");
        } else if (value < 0 || value > 1) {
            throw new IllegalArgumentException("Interpolation value must be between 0 and 1");
        }

        float va = value;
        float vb = 1f - value;

        Heatlist result = new BoundedHeatlist(a.values.length);
        result.samples = a.samples + b.samples;
        for (int i = 0; i < a.values.length; i++) {
            result.values[i] = a.values[i] * va + b.values[i] * vb;
        }
        return result;
    }

    /**
     * Converts a Heatlist into a BoundedHeatlist if allowable. An allowable
     * Heatlist is one with an integral sum of 1.0f (+/-0.00001) or is empty in
     * 100000. If the source is invalid an IllegalArgumentException is thrown.
     *
     * @param source the source to copy from
     * @return A copy of the passed Heatlist under new Bounded rules
     */
    public static BoundedHeatlist makeFrom(Heatlist source) {
        BoundedHeatlist list = new BoundedHeatlist(source.values.length);
        if (source.empty()) {
            return list;
        }

        float sum = 0f;

        for (int i = 0; i < source.values.length; i++) {
            list.values[i] = source.values[i];
            sum += source.values[i];
        }

        // Very small threshold
        if (sum >= 1.00001 || sum < 0.99999) {
            throw new IllegalArgumentException("Source is outside allowable range. Use either tryMakeFrom or bindFrom instead.");
        }

        return list;
    }

    /**
     * Converts a Heatlist into a BoundedHeatlist if allowable. An allowable
     * Heatlist is one with an integral sum of 1.0f (+/-0.00001) or is empty in
     * 100000. If the source is invalid null is returned
     *
     * @param source the source to copy from
     * @return A copy of the passed Heatlist under new Bounded rules or null
     */
    public static BoundedHeatlist tryMakeFrom(Heatlist source) {
        BoundedHeatlist list = new BoundedHeatlist(source.values.length);
        if (source.empty()) {
            return list;
        }

        float sum = 0f;

        for (int i = 0; i < source.values.length; i++) {
            list.values[i] = source.values[i];
            sum += source.values[i];
        }

        // Very small threshold
        if (sum >= 1.00001 || sum < 0.99999) {
            return null;
        }

        return list;
    }

    /**
     * Converts a Heatlist into a BoundedHeatlist by normalizing all of its
     * values, creating a net sum of 1.0f (or 0.0f if empty)
     *
     * @param source the source to copy from
     * @return A new BoundedHeatlist with scaled values of the source Heatlist
     */
    public static BoundedHeatlist bindFrom(Heatlist source) {
        BoundedHeatlist list = new BoundedHeatlist(source.values.length);
        if (source.empty()) {
            return list;
        }

        float nFactor = 1 / source.getTotal();  // Normalizing factor
        for (int i = 0; i < source.values.length; i++) {
            list.values[i] = source.values[i] * nFactor;
        }
        return list;
    }
}
