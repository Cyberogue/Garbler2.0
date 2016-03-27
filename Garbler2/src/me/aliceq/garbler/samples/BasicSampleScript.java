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
package me.aliceq.garbler.samples;

import me.aliceq.garbler.GarblerAnalysis;
import me.aliceq.garbler.GarblerScript;
import me.aliceq.heatmap.HeatMap;

/**
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public class BasicSampleScript extends GarblerScript {

    @Override
    public void onStart() {
    }

    @Override
    public void onComplete(String context) {
        System.out.println(context);
    }

    @Override
    public void preIterate(String context) {
        // Pick a word length from the current character
        buffer = "" + (Character) GarblerAnalysis.pickRandom(analyzer("FIRSTCHAR").getProbabilities(context, ""));
        HeatMap correlation = analyzer("CHARCORRELATION").getProbabilities(context, buffer);

        if (correlation == null) {
            // Pick the word length at random
            iterations = (Integer) GarblerAnalysis.pickRandom(analyzer("WORDLENGTH").getProbabilities(context, ""));
        } else {
            // Pick the word length from the list of lengths
            // This is done to prevent 1-letter long words that don't make sense
            iterations = (Integer) GarblerAnalysis.pickRandom(correlation);
        }

        iterations -= 2;    // Padding for word endings
    }

    @Override
    public void onIterate(String context) {
        buffer += (Character) GarblerAnalysis.pickRandom(analyzer("LETTERS").getProbabilities(context, buffer));
    }

    @Override
    public void postIterate(String context) {
        // See if there exists an ending
        HeatMap probabilities = analyzer("ENDINGS").getProbabilities(context, buffer);
        if (probabilities != null) {
            String ending = (String) GarblerAnalysis.pickRandom(probabilities);
            if (ending != null) {
                buffer += ending;
            }
        }
    }
}
