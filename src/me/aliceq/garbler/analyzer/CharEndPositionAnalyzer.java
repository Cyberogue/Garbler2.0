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
 * This analyzer keeps track of how often a character is found as the last
 * character in a word
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public class CharEndPositionAnalyzer implements GarblerAnalyzer<Integer> {

    private final int radius;
    private final Map<Character, HeatMap<Integer>> maps = new HashMap();

    /**
     * Creates an instance of distance 1
     */
    public CharEndPositionAnalyzer() {
        this(1);
    }

    /**
     * Creates an instance of specified distance
     *
     * @param maxDistance the number of positions to track
     */
    public CharEndPositionAnalyzer(int maxDistance) {
        radius = maxDistance;
    }

    @Override
    public void analyze(String word) {
        if (word.length() == 0) {
            return;     // Ignored
        }

        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(word.length() - 1 - i);
            HeatMap<Integer> map = maps.get(c);
            if (map == null) {
                map = new HeatMap(radius);
                maps.put(c, map);
            }
            map.increment(i > radius ? radius : i);
        }
    }

    /**
     * This returns an Integer HeatMap demonstrating how often the character is
     * found at one of the last positions in a word. The highest and last index
     * of the map corresponds to any time the character appears outside the
     * important range.
     */
    @Override
    public HeatMap<Integer> next(String context, String wordPrefix) {
        if (wordPrefix.length() == 0) {
            return null;
        }
        return maps.get(wordPrefix.charAt(wordPrefix.length() - 1));
    }

    @Override
    public void clear() {
        maps.clear();
    }
}
