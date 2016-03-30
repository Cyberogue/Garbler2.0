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

import me.aliceq.heatmap.HeatMap;

/**
 * Root interface for each analyzing module within Garbler
 *
 * @author Alice Quiros <email@aliceq.me>
 * @param <E> The type of return value, usually String, Character or Integer
 */
public interface GarblerAnalyzer<E extends Comparable> {

    /**
     * Analyzes a passed word
     *
     * @param word the word to parse
     */
    public void analyze(String word);

    /**
     * Hook method which returns the current normalized HeatMap of String
     * probabilities
     *
     * @param context the context the current word is in
     * @param wordPrefix the currently worked-on word
     * @return a normalized HeatMap of String probabilities for the current
     * analyzer
     */
    public HeatMap<E> next(String context, String wordPrefix);

    /**
     * Clears all data within the analyzer. This should be equivalent to making a
     * new instance with the same configuration.
     */
    public void clear();
}
