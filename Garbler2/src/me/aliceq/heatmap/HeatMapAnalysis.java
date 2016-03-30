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

import java.util.Map;

/**
 * An abstract class offering methods for combining and creating HeatMap data
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public abstract class HeatMapAnalysis {

    /**
     * Square root of 1/2 to ten decimal places
     */
    public static final float SQRT_HALF = .7071067812f;

    /**
     * Square root of 1/3 to ten decimal places
     */
    public static final float SQRT_THIRD = 0.5477225575f;

    private final static java.util.Random rand = new java.util.Random();

    /**
     * Finds the linear interpolation of a set of maps. If a key is contained in
     * one key but not in another, it is simply treated as a value of 0 where it
     * doesn't exist
     *
     * @param <K> the type of key
     * @param a the first HeatMap
     * @param b the second HeatMap
     * @param value a value from 0 to 1 (inclusive). A value <= 0 returns
     * HeatMap a, a value >= 1 returns HeatMap b and a value of 0.5 returns a
     * perfect average.
     * @return a new HeatMap instance whose keys and values are the result of
     * the calculation
     */
    public final static <K extends Comparable> HeatMap<K> lerp(HeatMap<K> a, HeatMap<K> b, float value) {
        HeatMap<K> result = new HeatMap(a.size() > b.size() ? a.size() : b.size());    // Minimize rewrites
        float multiplier = clamp0To1(value);

        // b first
        for (Map.Entry<K, Float> entry : b.getData()) {
            float v = result.getValue(entry.getKey()) + (float) entry.getValue() * multiplier;
            result.set(entry.getKey(), v);
        }

        multiplier = 1 - multiplier;
        // a second
        for (Map.Entry<K, Float> entry : a.getData()) {
            float v = result.getValue(entry.getKey()) + (float) entry.getValue() * multiplier;
            result.set(entry.getKey(), v);
        }

        return result;
    }

    /**
     * Finds the average of a set of maps. If a key is contained in one key but
     * not in another, it is simply treated as a value of 0 where it doesn't
     * exist. This is the same as calling lerp(a, b, 0.5f)
     *
     * @param <K> the type of key
     * @param a the first HeatMap
     * @param b the second HeatMap
     * @return a new HeatMap instance whose keys and values are the result of
     * the calculation
     */
    public final static <K extends Comparable> HeatMap<K> average(HeatMap<K> a, HeatMap<K> b) {
        return lerp(a, b, 0.5f);
    }

    /**
     * Finds the average of a set of maps. If a key is contained in one key but
     * not in another, it is simply treated as a value of 0 where it doesn't
     * exist. Similarly, a null map will be treated as zeroes;
     *
     * @param <K> the type of key
     * @param maps HeatMaps to average
     * @return a new HeatMap instance whose keys and values are the result of
     * the calculation
     */
    public final static <K extends Comparable> HeatMap<K> average(HeatMap<K>[] maps) {
        if (maps.length == 0) {
            return new HeatMap();
        }

        HeatMap<K> result = new HeatMap(maps[0].size());    // Minimize rewrites
        float multiplier = 1f / maps.length;

        for (HeatMap<K> map : maps) {
            if (map == null) {
                continue;
            }
            for (Map.Entry<K, Float> entry : map.getData()) {
                float v = result.getValue(entry.getKey()) + (float) entry.getValue() * multiplier;
                result.set(entry.getKey(), v);
            }
        }

        return result;
    }

    /**
     * Returns the power series of a set of maps. That is:<br>
     * result = a[0]k + a[1]k^2 + a[2]k^3 + a[3]k^4 + ... + a[n]k^n
     * <p>
     * In addition, the end result is divided by (k + k^2 + k^3 + ... + k^n) to
     * attempt to preserve normalization. Similarly, a null map will be treated
     * as zeroes;
     *
     * @param <K> the type of key
     * @param maps HeatMaps in the series from 0 to n
     * @param k k value. This should be between 0 and 1.
     * @return a new HeatMap instance whose keys and values are the result of
     * the calculation
     */
    public final static <K extends Comparable> HeatMap<K> powerSeries(HeatMap<K>[] maps, float k) {
        if (maps.length == 0) {
            return new HeatMap();
        }

        HeatMap<K> result = new HeatMap(maps[0].size());    // Attempt to minimize rewrites
        float kn = clamp0To1(k);
        float weight = 0;

        for (HeatMap<K> map : maps) {
            if (map == null) {
                continue;
            }
            for (Map.Entry<K, Float> entry : map.getData()) {
                float v = result.getValue(entry.getKey()) + (float) entry.getValue() * kn;
                result.set(entry.getKey(), v);
            }
            weight += kn;
            kn *= kn;
        }

        for (K key : result.keys()) {
            result.set(key, result.getValue(key) / weight);
        }

        return result;
    }

    /**
     * Returns a trimmed version of the HeatMap where any values lower than the
     * threshold are removed.
     *
     * @param <K> the type of key
     * @param map the source map
     * @param thresh threshold to trim. Any value lower than this will be
     * removed.
     * @return a new HeatMap instance whose keys and values are the result of
     * the calculation
     */
    public final static <K extends Comparable> HeatMap<K> trim(HeatMap<K> map, float thresh) {
        HeatMap<K> result = new HeatMap(map.size());

        for (Map.Entry<K, Float> entry : map.getData()) {
            if (entry.getValue() >= thresh) {
                result.set(entry.getKey(), entry.getValue());
            }
        }

        return result;
    }

    /**
     * Returns a trimmed version of the HeatMap where any values higher than the
     * threshold are removed.
     *
     * @param <K> the type of key
     * @param map the source map
     * @param thresh threshold to trim. Any value higher than this will be
     * removed.
     * @return a new HeatMap instance whose keys and values are the result of
     * the calculation
     */
    public final static <K extends Comparable> HeatMap<K> trimUpper(HeatMap<K> map, float thresh) {
        HeatMap<K> result = new HeatMap(map.size());

        for (Map.Entry<K, Float> entry : map.getData()) {
            if (entry.getValue() <= thresh) {
                result.set(entry.getKey(), entry.getValue());
            }
        }

        return result;
    }

    /**
     * Returns the cumulative form of a HeatMap. That is, each key is sorted by
     * rank with its value equal to the cumulative sum of the keys before it.
     * The final element in the series equals the source map's total sum.
     *
     * @param <K> the type of key
     * @param map the source map
     * @return a new HeatMap instance whose keys and values are the result of
     * the calculation
     */
    public final static <K extends Comparable> HeatMapDataSet<K> getCumulative(HeatMap<K> map) {
        HeatMap<K> result = new HeatMap(map.size());    // Attempt to minimize rewrites
        float sum = 0;

        for (Map.Entry<K, Float> entry : map.getData()) {
            sum += entry.getValue();
            result.set(entry.getKey(), sum);
        }

        return result.getData();
    }

    /**
     * Returns the cumulative distribution function of a normalized HeatMap.
     * This is similar to calling getCumulative after verifying normalization.
     *
     * @param <K> the type of key
     * @param map the source map
     * @return a new HeatMap instance whose keys and values are the result of
     * the calculation
     */
    public final static <K extends Comparable> HeatMapDataSet<K> getCDF(HeatMap<K> map) {
        HeatMap<K> result = new HeatMap(map.size());    // Attempt to minimize rewrites
        float m = 1f / map.getTotal();
        float sum = 0;

        for (Map.Entry<K, Float> entry : map.getData()) {
            sum += entry.getValue() * m;
            result.set(entry.getKey(), sum);
        }

        return result.getData();
    }

    /**
     * Returns a random key from the provided HeatMap based on its distribution.
     * If there are no elements in the map, a null value is returned.
     *
     * @param <K> the type of key
     * @param map the source map
     * @return a random key with probability [value] from the map
     */
    public static <K extends Comparable> K randomFromCDF(HeatMap<K> map) {
        if (map.isEmpty()) {
            return null;
        }
        HeatMapDataSet<K> data = getCDF(map);
        float value = rand.nextFloat();

        int size = map.size();
        for (int i = 0; i < size; i++) {
            if (data.getValue(i) > value) {
                return data.getKey(i);
            }
        }
        return data.getKey(size - 1);
    }

    /**
     * Returns a random key from the provided map with equal probability for
     * all. If there are no elements in the map, a null value is returned.
     *
     * @param <K> the type of key
     * @param map the source map
     * @return a random key from the map
     */
    public static <K extends Comparable> K randomKey(HeatMap<K> map) {
        if (map.isEmpty()) {
            return null;
        }
        return map.getEntry(rand.nextInt(map.size())).getKey();
    }

    private static float clamp0To1(float value) {
        return (value < 0 ? 0f : value > 1 ? 1f : value);
    }
}
