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

import java.util.Collection;
import java.util.Map;
import java.util.Random;
import me.aliceq.heatmap.HeatList;
import me.aliceq.heatmap.HeatMap;
import me.aliceq.heatmap.HeatMapFilter;

/**
 * Abstract utility class which simply offers methods for selecting a random
 * object from HeatMaps returned by GarblerAnalyzers
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public abstract class GarblerAnalysis {

    private static final Random r = new Random();
    private static final HeatMapFilter filter = new HeatMapFilter() {

        @Override
        public void process(HeatMap source) {
            destination.Clear();
            HeatList cumulative = HeatList.getCumulative(source.getHeatList());
            for (int i = 0; i < source.size(); i++) {
                Comparable key = source.getKey(i);
                destination.touch(key);
                destination.getHeatList().overwriteValue(i, cumulative.getValue(i));
            }
            destination.verifyNormalization();
        }
    };

    /**
     * Picks a random element from a
     *
     * @param map
     * @return
     */
    public static Comparable pickRandom(HeatMap<Comparable> map) {
        if (map == null) {
            return null;
        } else if (!map.normalized()) {
            map = map.copy();
            map.normalizeAll();
        }
        HeatMap cum = filter.applyFilter(new HeatMap(), map);

        float value = r.nextFloat();
        Collection<Map.Entry<Comparable, Float>> entries = cum.entries();
        for (Map.Entry<Comparable, Float> entry : entries) {
            if (entry.getValue() >= value) {
                return entry.getKey();
            }
        }
        return null;
    }
}
