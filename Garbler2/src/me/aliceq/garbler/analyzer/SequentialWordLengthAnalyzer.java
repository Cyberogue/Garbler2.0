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

import java.util.ArrayList;
import java.util.List;
import me.aliceq.garbler.GarblerAnalyzer;
import me.aliceq.heatmap.HeatMap;

/**
 * A more complex analyzer which keeps track of the previous length and
 * retrieves the next length based on the current
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public class SequentialWordLengthAnalyzer implements GarblerAnalyzer<Integer> {

    private final List<HeatMap<Integer>> lengths = new ArrayList();
    private int previous = -1;

    @Override
    public void analyze(String word) {
        if (word.length() < 1) {
            return;
        }
        if (previous < 0) { // Starting condition
            previous = word.length();
            return;
        }

        // Verify that we have a big enough list
        for (int i = lengths.size(); i < previous; i++) {
            lengths.add(null);
        }

        // Add to the specified spot, offset by 1
        if (lengths.get(previous - 1) == null) {
            HeatMap<Integer> map = new HeatMap();
            map.increment(word.length());
            lengths.set(previous - 1, map);
        } else {
            lengths.get(previous - 1).increment(word.length());
        }

        previous = word.length();
    }

    @Override
    public HeatMap<Integer> getProbabilities(String context, String wordPrefix) {
        int index = wordPrefix.length() - 1;
        if (index > lengths.size() || index == 0) {
            return null;
        } else {
            return lengths.get(index);
        }

    }

    @Override
    public void clear() {
        lengths.clear();
    }
}
