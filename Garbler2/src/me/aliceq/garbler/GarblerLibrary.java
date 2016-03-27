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
import java.util.Set;
import me.aliceq.garbler.analyzer.LetterInfluenceAnalyzer;
import me.aliceq.garbler.analyzer.SimpleWordLengthAnalyzer;
import me.aliceq.garbler.analyzer.WordBeginAnalyzer;
import me.aliceq.garbler.analyzer.WordEndingAnalyzer;

/**
 * Class containing analyzers for a Garbler program. In addition, a library
 * contains an input filter used to interpret analyzed text.
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public final class GarblerLibrary {

    private final Map<String, GarblerAnalyzer> analyzers;
    private boolean lock = false;
    private int analyzed = 0;

    private final GarblerTranslator filter;

    /**
     * Creates an instance with no output filter and a case-insensitive input
     * filter
     */
    public GarblerLibrary() {
        this(GarblerTranslator.caseInsensitive);
    }

    /**
     * Creates an instance with custom input and output filters
     *
     * @param filter the input filter
     */
    public GarblerLibrary(GarblerTranslator filter) {
        this.analyzers = new HashMap();
        this.filter = filter;
    }

    /**
     * Runs a word through all GarblerAnalyzers stored. Note that after this is
     * run no more analyzers may be added. By default, the delimiter used for
     * splitting strings is [^a-bA-B0-9`'-]
     *
     * @param text a series of words to analyze
     */
    public void analyze(String text) {
        analyze(text, "[^a-bA-B0-9`'-]");
    }

    /**
     * Runs a word through all GarblerAnalyzers stored. Note that after this is
     * run no more analyzers may be added.
     *
     * @param text a series of words to analyze
     * @param delim regex delimiter to use for separating words
     */
    public void analyze(String text, String delim) {
        long millis = System.currentTimeMillis();
        lock = true;
        int count = 0;
        String filtered = filter.transpose(text);
        for (String word : filtered.split(delim)) {
            for (GarblerAnalyzer analyzer : analyzers.values()) {
                analyzer.analyze(word);
            }
            count++;
            analyzed++;
        }
        System.out.println("Parsed " + count + "[" + analyzed + "] words across " + analyzers.size() + " modules in " + (System.currentTimeMillis() - millis) + "ms");
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

    /**
     * Loads the default set of analyzers. These are the following under the
     * given keys:<br>
     * <p>
     * LETTERS : LetterInfluenceAnalyzer<br>
     * WORDLENGTH : SimpleWordLengthAnalyzer<br>
     * FIRSTCHAR : WordBeginAnalyzer<br>
     * ENDINGS : WordEndAnalyzer<br>
     *
     */
    public void loadDefaults() {
        addAnalyzer("LETTERS", new LetterInfluenceAnalyzer());
        addAnalyzer("WORDLENGTH", new SimpleWordLengthAnalyzer());
        addAnalyzer("FIRSTCHAR", new WordBeginAnalyzer());
        addAnalyzer("ENDINGS", new WordEndingAnalyzer());
    }

    /**
     * Returns a set of the keys currently containing analyzers
     *
     * @return a set of String keys
     */
    public Set<String> analyzers() {
        return analyzers.keySet();
    }

    @Override
    public String toString() {
        return analyzers.keySet() + "[" + analyzed + "]";
    }
}
