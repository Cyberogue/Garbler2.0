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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import me.aliceq.garbler.analyzer.WordLengthCorrelationAnalyzer;
import me.aliceq.garbler.analyzer.LetterInfluenceAnalyzer;
import me.aliceq.garbler.analyzer.WordLengthDistributionAnalyzer;
import me.aliceq.garbler.analyzer.InitialCharDistributionAnalyzer;
import me.aliceq.garbler.analyzer.CommonEndingAnalyzer;

/**
 * Class containing analyzers for a Garbler program. In addition, a library
 * contains an input filter used to interpret analyzed text.
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public final class GarblerLibrary {

    public static String DEFAULT_DELIM = "[\\s_;:\"„0-9\\[\\]()<>]+";

    private boolean selffeed = false;
    private final Map<String, GarblerAnalyzer<Comparable>> analyzers;
    private boolean lock = false;
    private int analyzed = 0;

    private final GarblerTranslator filter;

    /**
     * Creates an instance with no output filter and a case-insensitive input
     * filter.
     *
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
        if (filter == null) {
            throw new IllegalArgumentException("Null translator not allowed");
        }

        this.analyzers = new HashMap();
        this.filter = filter;
    }

    public int analyze(String text) {
        return analyze(text, null, Integer.MAX_VALUE);
    }

    public int analyze(String text, int maxWords) {
        return analyze(text, null, maxWords);
    }

    public int analyze(String text, String delim) {
        return analyze(text, delim, Integer.MAX_VALUE);
    }

    /**
     * Runs a word through all GarblerAnalyzers stored. Note that after this is
     * run no more analyzers may be added.
     *
     * @param text a series of words to analyze
     * @param delim regex delimiter to use for separating words, or null to use
     * the default
     * @param maxWords the maximum number of words to extract
     * @return the number of words that were garbled
     */
    public synchronized int analyze(String text, String delim, int maxWords) {
        long millis = System.currentTimeMillis();
        lock = true;
        int count = 0;
        String filtered = filter.transpose(text);

        String[] words = delim == null ? filtered.split(DEFAULT_DELIM) : filtered.split(delim);
        for (String word : words) {
            if (count >= maxWords) {
                break;
            }
            for (GarblerAnalyzer analyzer : analyzers.values()) {
                analyzer.analyze(word);
            }
            count++;
            analyzed++;
        }
        System.out.println("Parsed " + count + "[" + analyzed + "] words across " + analyzers.size() + " modules in " + (System.currentTimeMillis() - millis) + "ms");
        return count;
    }

    public int analyzeFromFile(String filepath) {
        return analyzeFromFile(new File(filepath), null);
    }

    public int analyzeFromFile(String filepath, String delim) {
        return analyzeFromFile(new File(filepath), delim, Integer.MAX_VALUE);
    }

    public int analyzeFromFile(String filepath, int maxWords) {
        return analyzeFromFile(new File(filepath), null, maxWords);
    }

    /**
     * Reads in a file line-by-line and analyzes each line.Note that after this
     * is run no more analyzers may be added.
     *
     * @param filepath location of the file to read
     * @param delim regex to use for separating words, or null to use the
     * default
     * @param maxWords the maximum number of words to extract
     * @return the number of words analyzed
     */
    public int analyzeFromFile(String filepath, String delim, int maxWords) {
        return analyzeFromFile(new File(filepath), delim, maxWords);
    }

    public int analyzeFromFile(File file) {
        return analyzeFromFile(file, null, Integer.MAX_VALUE);
    }

    public int analyzeFromFile(File file, int maxWords) {
        return analyzeFromFile(file, null, maxWords);
    }

    public int analyzeFromFile(File file, String delim) {
        return analyzeFromFile(file, delim, Integer.MAX_VALUE);
    }

    /**
     * Reads in a file line-by-line and analyzes each line. Note that after this
     * is run no more analyzers may be added.
     *
     * @param file the file to read
     * @param delim regex to use for separating words, or null to use the
     * default
     * @param maxWords the maximum number of words to extract
     * @return the number of words that were garbled
     */
    public synchronized int analyzeFromFile(File file, String delim, int maxWords) {

        int count = 0;
        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

            long millis = System.currentTimeMillis();
            for (String next; (next = reader.readLine()) != null;) {
                if (!next.isEmpty()) {
                    String filtered = filter.transpose(next);
                    String[] words = delim == null ? filtered.split(DEFAULT_DELIM) : filtered.split(delim);
                    for (String word : words) {
                        if (count >= maxWords) {
                            break;
                        }
                        for (GarblerAnalyzer analyzer : analyzers.values()) {
                            analyzer.analyze(word);
                        }
                        count++;
                        analyzed++;
                    }
                }
            }

            System.out.println("Parsed " + count + "[" + analyzed + "] words across " + analyzers.size() + " modules in " + (System.currentTimeMillis() - millis) + "ms");

            lock = true;
            return count;
        } catch (Exception e) {
            System.out.println("Error parsing data");
            e.printStackTrace();
        }
        return count;
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
    public GarblerAnalyzer<Comparable> getAnalyzer(String key) {
        return analyzers.get(key.toUpperCase());
    }

    /**
     * Runs a script to generate a new sentence with this instance as its
     * context
     *
     * @param script script to run
     * @param wordCount number of words to generate
     * @throws IllegalStateException if nothing has been analyzed
     */
    public void run(final GarblerScript script, final int wordCount) {
        run(script, wordCount, ' ');
    }

    /**
     * Runs a script to generate a new sentence with this instance as its
     * context
     *
     * @param script script to run
     * @param wordCount number of words to generate
     * @param separator separator to place between words
     * @throws IllegalStateException if nothing has been analyzed
     */
    public synchronized void run(final GarblerScript script, final int wordCount, final char separator) {
        if (analyzed == 0) {
            throw new IllegalStateException("Nothing has been analyzed");
        }

        script.library = this;
        String s = "";
        script.onStart();
        for (int i = 0; i < wordCount; i++) {
            s += script.createWord(s) + separator;
        }
        script.onComplete(s);
        if (selffeed) {
            script.library.analyze(s);
        }
    }

    /**
     * Clears all data from the library and analyzers but retains the current
     * analyzer set. This also releases the adding block so new analyzers may be
     * added.
     */
    public void clear() {
        for (GarblerAnalyzer ga : analyzers.values()) {
            ga.clear();
        }
        analyzed = 0;
        lock = false;
        System.gc();    // Probably just dumped a lot of data so call to clean it up
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
     * If the library is self-feeding, it will automatically analyze any
     * sentences it generates. This leads to more chaotic lines as more
     * sentences are generated.
     *
     * @param enable true to enable self-feeding
     */
    public void selfFeed(boolean enable) {
        this.selffeed = enable;
    }

    /**
     * Checks whether or not the library is self-feeding
     *
     * @return true if the library self-feeds
     */
    public boolean isSelfFeeding() {
        return this.selffeed;
    }

    /**
     * Loads the default set of analyzers. These are the following under the
     * given keys:<br>
     * <p>
     * LETTERS : LetterInfluenceAnalyzer<br>
     * WORDLENGTH : WordLengthDistributionAnalyzer<br>
     * FIRSTCHAR : InitialCharDistributionAnalyzer<br>
     * ENDINGS : WordEndAnalyzer<br>
     * CHARCORRELATION : WordLengthCorrelationAnalyzer<BR>
     *
     */
    public void loadDefaults() {
        addAnalyzer("LETTERS", new LetterInfluenceAnalyzer());
        addAnalyzer("WORDLENGTH", new WordLengthDistributionAnalyzer());
        addAnalyzer("FIRSTCHAR", new InitialCharDistributionAnalyzer());
        addAnalyzer("ENDINGS", new CommonEndingAnalyzer());
        addAnalyzer("CHARCORRELATION", new WordLengthCorrelationAnalyzer());
    }

    /**
     * Loads the default set of analyzers. These are the following under the
     * given keys:<br>
     * <p>
     * LETTERS : LetterInfluenceAnalyzer<br>
     * WORDLENGTH : WordLengthDistributionAnalyzer<br>
     * FIRSTCHAR : InitialCharDistributionAnalyzer<br>
     * ENDINGS : WordEndAnalyzer<br>
     * CHARCORRELATION : WordLengthCorrelationAnalyzer<BR>
     *
     * @param radius radius of influence
     * @param letterInfluence letter influence factor
     * @throws IllegalArgumentException if radius is less than 2
     */
    public void loadDefaults(int radius, float letterInfluence) {
        if (radius < 2) {
            throw new IllegalArgumentException("Radius has to be greater than 1");
        }
        addAnalyzer("LETTERS", new LetterInfluenceAnalyzer(radius, letterInfluence));
        addAnalyzer("WORDLENGTH", new WordLengthDistributionAnalyzer());
        addAnalyzer("FIRSTCHAR", new InitialCharDistributionAnalyzer());
        addAnalyzer("ENDINGS", new CommonEndingAnalyzer(radius));
        addAnalyzer("CHARCORRELATION", new WordLengthCorrelationAnalyzer());
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
