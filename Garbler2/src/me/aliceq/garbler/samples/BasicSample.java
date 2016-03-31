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

import me.aliceq.garbler.GarblerLibrary;
import me.aliceq.garbler.GarblerTranslator;
import me.aliceq.garbler.analyzer.AlphabetAnalyzer;
import me.aliceq.garbler.analyzer.CharEndPositionAnalyzer;
import me.aliceq.garbler.analyzer.WordLengthCorrelationAnalyzer;
import me.aliceq.garbler.analyzer.LetterInfluenceAnalyzer;
import me.aliceq.garbler.analyzer.InitialCharDistributionAnalyzer;
import me.aliceq.garbler.analyzer.CommonEndingAnalyzer;
import me.aliceq.garbler.analyzer.RepeatLetterAnalyzer;
import me.aliceq.heatmap.HeatMapAnalysis;

/**
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public class BasicSample {

    public static void main(String[] args) {
        // Create a new library and load it
        GarblerLibrary library = new GarblerLibrary();

        library.addAnalyzer("Influence", new LetterInfluenceAnalyzer(3, HeatMapAnalysis.SQRT_HALF));
        library.addAnalyzer("CommonEndings", new CommonEndingAnalyzer(3));
        library.addAnalyzer("WordLength", new WordLengthCorrelationAnalyzer());
        library.addAnalyzer("LengthCorrelation", new WordLengthCorrelationAnalyzer());
        library.addAnalyzer("CharBegin", new InitialCharDistributionAnalyzer());
        library.addAnalyzer("CharEnd", new CharEndPositionAnalyzer());
        library.addAnalyzer("Alphabet", new AlphabetAnalyzer());
        library.addAnalyzer("Repetitions", new RepeatLetterAnalyzer());

        System.out.println(library);

        // Analyze from files
        int count = library.analyzeFromFile("seeds/svenska.txt");
        library.analyzeFromFile("seeds/vietcraft.txt", count);

        // Create a new script and run it
        BasicSampleScript script = new BasicSampleScript();
        try {
            // script.setOutput(GarblerTranslator.createFromFile("alien.gtf"));
        } catch (Exception e) {

        }

        for (int i = 0; i < 10; i++) {
            library.run(script, 10);
        }
    }
}
