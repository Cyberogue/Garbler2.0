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
package me.aliceq.garbler.analyzer;

import java.util.HashMap;
import java.util.Map;
import me.aliceq.garbler.GarblerAnalyzer;
import me.aliceq.heatmap.HeatMap;

/**
 * Analyzer which keeps track of the correlation between the first letter in a
 * word and its length
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public class WordLengthCorrelationAnalyzer implements GarblerAnalyzer<Integer> {

    private final Map<Character, HeatMap<Integer>> map = new HashMap();

    @Override
    public void analyze(String word) {
        if (word.length() < 0) {
            return;
        }

        char first = word.charAt(0);
        int length = word.length();

        HeatMap<Integer> heatmap = map.get(first);
        if (heatmap == null) {
            heatmap = new HeatMap();
            map.put(first, heatmap);
        }
        heatmap.increment(length);
    }

    @Override
    public HeatMap<Integer> next(String context, String wordPrefix) {
        if (wordPrefix.length() < 1) {
            return null;
        }
        char c = wordPrefix.charAt(0);
        HeatMap<Integer> heatmap = map.get(c);
        return heatmap;
    }

    @Override
    public void clear() {
        map.clear();
    }

}
