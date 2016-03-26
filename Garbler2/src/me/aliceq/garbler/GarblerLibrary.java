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

import java.util.HashMap;
import java.util.Map;

/**
 * Class containing analyzers for a Garbler program
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public class GarblerLibrary {

    private final Map<String, GarblerAnalyzer> analyzers;
    private boolean lock = false;
    private int analyzed = 0;

    /**
     * Constructor
     */
    public GarblerLibrary() {
        this.analyzers = new HashMap();
    }

    /**
     * Runs a word through all GarblerAnalyzers stored. Note that after this is
     * run no more analyzers may be added.
     *
     * @param word the word to analyze and store
     */
    public void analyze(String word) {
        lock = true;
        for (GarblerAnalyzer analyzer : analyzers.values()) {
            analyzer.analyze(word);
        }
        analyzed++;
    }

    /**
     * Adds an analyzer under the given key to the library
     *
     * @param key the key of the analyzer
     * @param analyzer the analyzer to add
     * @throws UnsupportedOperationException if this is called after analysis
     * has begun
     */
    public void addAnalyzer(String key, GarblerAnalyzer analyzer) {
        if (lock) {
            throw new UnsupportedOperationException("Analyzers may only be added before analysis");
        }
        analyzers.put(key.toUpperCase(), analyzer);
    }

    /**
     * Returns the analyzer corresponding to the given key
     *
     * @param key key to check
     * @return a GarblerAnalyzer instance or null
     */
    public GarblerAnalyzer getAnalyzer(String key) {
        return analyzers.get(key.toUpperCase());
    }

    /**
     * Runs a script on a separate thread with this instance as its context
     *
     * @param script script to run
     * @param wordCount number of words to generate
     */
    public void run(final GarblerScript script, final int wordCount) {
        run(script, wordCount, ' ');
    }

    /**
     * Runs a script on a separate thread with this instance as its context
     *
     * @param script script to run
     * @param wordCount number of words to generate
     * @param separator separator to place between words
     */
    public void run(final GarblerScript script, final int wordCount, final char separator) {
        script.library = this;
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                String s = "";
                script.onStart();
                for (int i = 0; i < wordCount; i++) {
                    s += script.createWord(s) + separator;
                }
                script.onComplete(s);
            }
        }
        );
        thread.setDaemon(false);
        thread.setPriority(Thread.NORM_PRIORITY);
        thread.start();
    }

    /**
     * Gets the number of words that have been analyzed
     *
     * @return the number of words that have been analyzed
     */
    public int analyzedCount() {
        return analyzed;
    }
}
