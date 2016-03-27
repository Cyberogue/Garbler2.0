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

/**
 * Abstract class which is run by a GarblerLibrary used to specify how the
 * program is run and how words are created. A GarblerScript relies on a
 * GarblerLibrary to contain all of its relevant information.
 * <p>
 * A GarblerScript features a GarblerTranslator at the output to filter output
 * messages. By default, there is no filter but if one is desired it can be
 * specified in the constructor.
 * <p>
 * For creation the script offers three hook methods - preIterate, onIterate and
 * postIterate. preIterate and postIterate are called once per word, before and
 * after (respectively) any iterations are done. onIterate is called multiple
 * times per word. The amount of based on the value of the <b>iterations</b>
 * member at the time of iteration.
 * <p>
 * The following members are available:<br>
 * buffer : String : A buffer of characters for word creation. During onIterate,
 * any modifications to the word should be done here.<br>
 * iterations : int : The number of times to call onIterate. This should be set
 * during preIterat. Any changes done during iteration will be ignored.<br>
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public abstract class GarblerScript {

    protected GarblerLibrary library;
    public String buffer;
    public int iterations;

    private final GarblerTranslator out;

    /**
     * Creates an instance with no output filtering
     */
    public GarblerScript() {
        this(GarblerTranslator.None);
    }

    /**
     * Creates an instance with a custom output filter
     *
     * @param out output filter
     */
    public GarblerScript(GarblerTranslator out) {
        this.out = out;
    }

    protected synchronized String createWord(String context) {
        buffer = "";
        iterations = 1;
        preIterate(context);
        final int iter_lock = iterations;
        for (int i = 0; i < iter_lock; i++) {
            onIterate(context);
        }
        postIterate(context);

        String result = out.transpose(buffer);
        buffer = "";
        return result;
    }

    /**
     * Run once at the start of a script
     */
    public void onStart() {

    }

    /**
     * Run once at the end of the script
     *
     * @param context the context created
     */
    public void onComplete(String context) {
        System.out.println(context);
    }

    /**
     * Called once before creation of a new word
     *
     * @param context the word context
     */
    public void preIterate(String context) {

    }

    /**
     * Called multiple times during creation of a word. The amount is changed by
     * setting the value of <b>iterations</b> during preIterate.
     *
     * @param context the word context
     */
    public void onIterate(String context) {

    }

    /**
     * Called once after creation of a new word, before transposing through the
     * output filter.
     *
     * @param context the word context
     */
    public void postIterate(String context) {

    }

    /**
     * Gets the corresponding analyzer from the script's library
     *
     * @param key the key the analyzer is under
     * @return a GarblerAnalyzer instance
     * @throws IllegalArgumentException if an invalid (non-existent) key is
     * provided
     */
    public final GarblerAnalyzer<Comparable> analyzer(final String key) {
        GarblerAnalyzer<Comparable> a = library.getAnalyzer(key);
        if (a == null) {
            throw new IllegalArgumentException("Invalid analyzer key");
        }
        return a;
    }
}
