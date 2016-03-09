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
 * An extension of a Heatmap which does not guarantee a cumulative sum of
 * approximately 1.0f
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public class UnboundHeatmap extends Heatmap {

    public UnboundHeatmap(int keycount) {
        super(keycount);
    }

    public static UnboundHeatmap getCummulative(Heatmap source) {
        return UnboundHeatmap.getCummulative(source, 0, source.values.length);
    }

    public static UnboundHeatmap getCummulative(Heatmap source, int startIndex, int endIndex) {
        UnboundHeatmap map = new UnboundHeatmap(endIndex - startIndex);
        float sum = 0;
        for (int i = startIndex; i < endIndex; i++) {
            sum += source.values[i];
            map.values[i - startIndex] = sum;
        }
        return map;
    }

    public static UnboundHeatmap extract(Heatmap source, int startIndex, int length) {
        if (startIndex < 0 || startIndex >= source.values.length || length < 0 || startIndex + length > source.values.length) {
            throw new ArrayIndexOutOfBoundsException();
        }

        UnboundHeatmap map = new UnboundHeatmap(length);
        System.arraycopy(source.values, startIndex, map.values, 0, length);
        return map;
    }

    public static UnboundHeatmap getValueSum(Heatmap a, Heatmap b) {
        UnboundHeatmap result = new UnboundHeatmap(a.values.length);
        for (int i = 0; i < a.values.length; i++) {
            result.values[i] = a.values[i] + b.values[i];
        }
        return result;
    }

    public static UnboundHeatmap getValueProduct(Heatmap a, float b) {
        UnboundHeatmap result = new UnboundHeatmap(a.values.length);
        for (int i = 0; i < a.values.length; i++) {
            result.values[i] = a.values[i] * b;
        }
        return result;
    }

    public static UnboundHeatmap getSum(Heatmap a, Heatmap b) {
        UnboundHeatmap result = getValueSum(a, b);
        result.samples = a.samples + b.samples;
        return result;
    }

    public static UnboundHeatmap getProduct(Heatmap a, float b) {
        UnboundHeatmap result = getValueProduct(a, b);
        result.samples = (int) (a.samples * b);
        return result;
    }
}
