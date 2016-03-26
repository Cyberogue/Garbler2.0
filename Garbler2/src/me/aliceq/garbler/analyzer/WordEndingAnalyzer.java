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

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.aliceq.garbler.GarblerAnalyzer;
import me.aliceq.heatmap.HeatMap;

/**
 * Analyzer which reads the endings of each
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public class WordEndingAnalyzer implements GarblerAnalyzer {

    private final int minRadius;
    private final int maxRadius;
    private final Map<Character, List<String>> endings;

    /**
     * Constructor for an analyzer of endings min-2 max-3
     */
    public WordEndingAnalyzer() {
        this(2, 3);
    }

    /**
     * Constructor
     *
     * @param minRadius the minimum size to consider a word ending
     * @param maxRadius the maximum size to consider a word ending
     */
    public WordEndingAnalyzer(int minRadius, int maxRadius) {
        this.maxRadius = maxRadius + 1; // Additional slot needed to get associative letter
        this.minRadius = minRadius;
        this.endings = new HashMap();
    }

    @Override
    public void analyze(String word) {
        // Skip short words
        if (word.length() <= minRadius) {
            return;
        }

        // Create relevant data
        String ending;
        if (word.length() < maxRadius) {
            ending = word.substring(1);
        } else {
            ending = word.substring(word.length() - maxRadius + 1, word.length());
        }
        char key = word.charAt(word.length() - ending.length() - 1);
        String reversed = reverse(ending);
        String min = reversed.substring(0, minRadius);

        // Get related locations
        List<Map.Entry<Character, String>> relevant = new ArrayList(6);
        for (Map.Entry<Character, List<String>> entry : endings.entrySet()) {    //O(n2), gross
            for (String s : entry.getValue()) {
                if (s.startsWith(min)) {
                    relevant.add(new AbstractMap.SimpleImmutableEntry<>(entry.getKey(), s));
                }
            }
        }
        if (relevant.isEmpty()) {
            // No relevant key, just add the ending
            List<String> list = endings.get(key);
            if (list == null) {
                list = new ArrayList();
                endings.put(key, list);
                list.add(reversed);
            } else if (!list.contains(reversed)) {
                list.add(reversed);
            }
        } else {
            int newLength = reversed.length();
            for (Map.Entry<Character, String> entry : relevant) {
                // See how far the rabbit hole we go
                int stopAt = entry.getValue().length() < reversed.length() ? entry.getValue().length() : reversed.length();
                if (stopAt < newLength) {
                    newLength = stopAt;
                } else {
                    for (int i = minRadius; i < stopAt; i++) {
                        if (entry.getValue().charAt(i) != reversed.charAt(i)) {
                            newLength = i;
                        }
                    }
                }
            }

            // If there is no change, just add the new association
            if (newLength == reversed.length()) {
                // No change, add ending to new key
                List<String> list = endings.get(key);
                if (list == null) {
                    list = new ArrayList();
                    endings.put(key, list);
                    list.add(reversed);
                } else if (!list.contains(reversed)) {
                    list.add(reversed);
                }
            } else {
                // We need to merge multiple of these
                String newRev = reversed.substring(0, newLength);

                // Add current
                List<String> list = endings.get(reversed.charAt(newRev.length()));
                if (list == null) {
                    list = new ArrayList();
                    endings.put(reversed.charAt(newRev.length()), list);
                    list.add(newRev);
                } else if (!list.contains(newRev)) {
                    list.add(newRev);
                }

                // Modify old
                for (Map.Entry<Character, String> entry : relevant) {

                    if (!entry.getValue().equals(newRev)) {
                        // Remove the old version
                        endings.get(entry.getKey()).remove(entry.getValue());
                        // And add the new version if needed)
                        list = endings.get(entry.getValue().charAt(newRev.length()));
                        if (list == null) {
                            list = new ArrayList();
                            endings.put(entry.getValue().charAt(newRev.length()), list);
                            list.add(newRev);
                        } else if (!list.contains(reversed)) {
                            list.add(newRev);
                        }
                    }
                }
            }
        }
    }

    @Override
    public HeatMap<String> getProbabilities(String context, String wordPrefix) {
        if (wordPrefix.length() < 1) {
            return null;
        }

        char c = wordPrefix.charAt(wordPrefix.length() - 1);
        if (endings.containsKey(c)) {
            HeatMap<String> map = new HeatMap();
            for (String s : endings.get(c)) {
                map.increment(reverse(s));
            }
            return map;
        }

        return null;
    }

    /**
     * Helper method to reverse a string
     */
    private String reverse(String s) {
        char[] str = s.toCharArray();

        for (int i = 0; i < str.length / 2; i++) {
            // Swap from both ends
            int j = str.length - i - 1;
            str[i] = (char) (str[i] ^ str[j]);
            str[j] = (char) (str[i] ^ str[j]);
            str[i] = (char) (str[i] ^ str[j]);
        }

        return new String(str);
    }

}
