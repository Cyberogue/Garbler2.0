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
import java.util.HashMap;
import java.util.Map;
import me.aliceq.garbler.GarblerAnalyzer;
import me.aliceq.garbler.GarblerTranslator;
import me.aliceq.heatmap.HeatMap;
import me.aliceq.heatmap.HeatMapFilter;

/**
 * Analyzer module which maintains information on each character's influence on
 * future characters
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public class LetterInfluenceAnalyzer implements GarblerAnalyzer<Character> {

    // The maximum distance a character can have influence
    private final int maxRadius;

    // Filter used to merge results
    private final HeatMapFilter filter;

    // Map of the distance-based heatmaps for each character
    private final Map<Character, HeatMap<Character>[]> heatmaps = new HashMap();

    /**
     * Creates an analyzer of radius 3
     */
    public LetterInfluenceAnalyzer() {
        this(3, 0.5f);
    }

    /**
     * Creates a letter influence analyzer of influence 0.5
     *
     * @param maxRadius the maximum distance a character can have influence. The
     * higher the influence radius the more memory is used.
     */
    public LetterInfluenceAnalyzer(int maxRadius) {
        this(maxRadius, 0.5f);
    }

    /**
     * Creates a letter influence analyzer of radius 3
     *
     * @param influence the value to interpolate between successive HeatMaps.
     * This should be a value between 0 and 1
     */
    public LetterInfluenceAnalyzer(float influence) {
        this(3, influence);
    }

    /**
     * Creates a letter influence analyzer
     *
     * @param maxRadius the maximum distance a character can have influence. The
     * higher the influence radius the more memory is used.
     * @param influence the value to interpolate between successive HeatMaps.
     * This should be a value between 0 and 1
     */
    public LetterInfluenceAnalyzer(int maxRadius, float influence) {
        if (maxRadius <= 0) {
            throw new IllegalArgumentException("Radius must be greater than 0");
        }
        this.maxRadius = maxRadius;

        final float i = influence;
        this.filter = HeatMapFilter.createFrom(new HeatMapFilter.SimpleFilter() {
            private final float weight = i;
            private float m;

            @Override
            public void preprocess() {
                this.m = 1f;
            }

            @Override
            public float process(float currentValue, float sourceValue, Comparable key) {
                return currentValue + sourceValue * m;
            }

            @Override
            public void preprocessSource() {
                m *= weight;
            }
        });
    }

    /**
     * Returns the maximum radius of influence
     *
     * @return the maximum radius of influence
     */
    public int getMaxRadius() {
        return maxRadius;
    }

    @Override
    public void analyze(String word) {
        // Only works for words > 2 long
        if (word.length() <= 2) {
            return;
        }

        // For each letter
        for (int i = 0; i < word.length() - 1; i++) {
            // Find the number of letters to count
            int letterCount = word.length() - i - 1;
            if (letterCount > maxRadius) {
                letterCount = maxRadius;
            }

            // Fetch current entry
            HeatMap<Character>[] imap = heatmaps.get(word.charAt(i));

            // Check if we need a new entry
            if (imap == null || imap.length < letterCount) {
                HeatMap<Character>[] maps = new HeatMap[letterCount]; // Make a new array

                int copy = 0; // Index to copy
                if (imap != null) {
                    // Copy any values
                    copy = imap.length;
                    System.arraycopy(imap, 0, maps, 0, copy);
                }

                // Initialize values
                for (int j = copy; j < maps.length; j++) {
                    maps[j] = new HeatMap();
                }

                heatmaps.put(word.charAt(i), maps); // Add the map

                imap = maps; // Set the current heatmap
            }

            // System.out.println(current.length);
            for (int j = i; j < i + letterCount; j++) {
                int dif = j - i;
                imap[dif].increment(word.charAt(j + 1));
            }
        }
    }

    @Override
    public HeatMap<Character> getProbabilities(String context, String wordPrefix) {
        // Find the number of letters to count
        int letterCount = wordPrefix.length() > maxRadius ? maxRadius : wordPrefix.length();
        int minSize = 10;

        // Loop to get all maps
        ArrayList<HeatMap> sources = new ArrayList();
        for (int i = 0; i < letterCount; i++) {
            char c = wordPrefix.charAt(wordPrefix.length() - 1 - i);

            // Check if a heatmap exists
            HeatMap[] current = heatmaps.get(c);
            if (current != null && current.length > i) {
                sources.add(current[i]);
                if (current[i].size() < minSize) {
                    minSize = current[i].size();
                }
            } else {
                sources.add(null);
            }
        }
        // Interpolate all the maps
        HeatMap result = new HeatMap(minSize);
        filter.applyFilter(result, sources);
        result.normalizeAll();

        return result;
    }
}
