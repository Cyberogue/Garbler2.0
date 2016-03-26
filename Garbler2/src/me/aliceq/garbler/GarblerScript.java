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
 * program is run and how words are created. A GarblerScript uses a Map of
 * String keys and analyzers for communication between methods. This is utilized
 * in a pipeline-like manner, implementing custom hook methods to modify the
 * data at each step.
 * <p>
 * A GarblerScript features two GarblerTranslators - one at the input and one at
 * the output. By default these translators are simple pass-through translators
 * unless specified in the constructor. Any input strings will be run through
 * the input translator before analysis, and output word will be run through the
 * output translator after creation.
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

    private final GarblerTranslator in, out;

    /**
     * Creates an instance with a case-insensitive input and pass-through output
     * filters
     */
    public GarblerScript() {
        this(GarblerTranslator.caseInsensitive, GarblerTranslator.None);
    }

    /**
     * Creates an instance with a case-insensitive input and custom output
     * filters
     *
     * @param out output filter
     */
    public GarblerScript(GarblerTranslator out) {
        this(GarblerTranslator.caseInsensitive, out);
    }

    /**
     * Creates an instance with custom input and output filters
     *
     * @param in input filter
     * @param out output filter
     */
    public GarblerScript(GarblerTranslator in, GarblerTranslator out) {
        this.in = in;
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

        String result = out.transpose(context);
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
     * @return a GarblerAnalyzer instance or null
     */
    public final GarblerAnalyzer analyzer(String key) {
        return library.getAnalyzer(key);
    }
}
