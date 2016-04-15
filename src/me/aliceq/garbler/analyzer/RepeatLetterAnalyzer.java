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

import me.aliceq.garbler.GarblerAnalyzer;
import me.aliceq.heatmap.HeatMap;

/**
 * Analyzer which keeps track of how certain letters are repeated. Non-repeated
 * letters are ignored, only letters with known sequences are stored.
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public class RepeatLetterAnalyzer implements GarblerAnalyzer<Character> {

    private HeatMap<Character>[] position;

    public RepeatLetterAnalyzer() {
        this(3);
    }

    public RepeatLetterAnalyzer(int max) {
        position = new HeatMap[max];
        for (int i = 0; i < position.length; i++) {
            position[i] = new HeatMap();
        }
    }

    @Override
    public void analyze(String word) {
        if (word.length() == 0) {
            return; // Ignored
        }
        char c = word.charAt(0);
        int repetitions = 0;
        for (int i = 1; i < word.length(); i++) {
            if (word.charAt(i) == c) {
                // Continue until end or different letter
                repetitions++;
            } else if (repetitions > 0) {
                // Store and continue);
                int index = (repetitions > position.length ? position.length : repetitions) - 1;
                if (position[index] == null) {
                    position[index] = new HeatMap();
                } else {
                    position[index].increment(c);
                }
                repetitions = 0;
            }
            c = word.charAt(i);
        }
        if (repetitions > 0) {
            int index = (repetitions > position.length ? position.length : repetitions) - 1;
            position[index].increment(c);
        }
    }

    /**
     * Returns a HeatMap of all the words of at least n-length (where n is the
     * number of repetitions at the endo f wordPrefix) and how often they occur
     * relative to each other.
     */
    @Override
    public HeatMap<Character> next(String context, String wordPrefix) {
        if (wordPrefix.length() == 0) {
            return null;
        }

        // Find length of end string
        char c = wordPrefix.charAt(wordPrefix.length() - 1);
        int rep = 0;
        for (int i = wordPrefix.length() - 2; i >= 0; i--) {
            if (wordPrefix.charAt(i) == c && ++rep < position.length - 1) {
                // ???
            } else {
                break;
            }
        }

        return position[rep];
    }

    @Override
    public void clear() {
        position = new HeatMap[position.length];
    }
}
