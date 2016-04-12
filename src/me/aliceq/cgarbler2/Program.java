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
package me.aliceq.cgarbler2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.aliceq.garbler.GarblerLibrary;
import me.aliceq.garbler.GarblerTranslator;
import me.aliceq.garbler.analyzer.AlphabetAnalyzer;
import me.aliceq.garbler.analyzer.CharEndPositionAnalyzer;
import me.aliceq.garbler.analyzer.CommonEndingAnalyzer;
import me.aliceq.garbler.analyzer.InitialCharDistributionAnalyzer;
import me.aliceq.garbler.analyzer.LetterInfluenceAnalyzer;
import me.aliceq.garbler.analyzer.RepeatLetterAnalyzer;
import me.aliceq.garbler.analyzer.WordLengthCorrelationAnalyzer;
import me.aliceq.heatmap.HeatMapAnalysis;

/**
 * Class containing all the different handlers to separate utility and
 * functionality from main structure
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public class Program {

    private GarblerLibrary library;
    private int acount = Integer.MAX_VALUE;

    protected void HelpHandler(String args) {
        if (args.equals("")) {
            DisplayError("help");
            return;
        }

        String first = args.split("\\s+", 1)[0];
        System.out.println(HelpPrompts.getFormatted(first));
    }

    protected void InitHandler(String args) {
        // Create a new library
        library = new GarblerLibrary();

        // Extract parameters
        String[] split = SplitArgs(args);
        int lirad = 3, cerad = 3;
        float lifac = HeatMapAnalysis.SQRT_HALF;

        for (int i = 0; i < split.length; i++) {
            try {
                switch (split[i]) {
                    case "-liradius":
                        if (++i < split.length) {
                            lirad = Integer.parseUnsignedInt(split[i]);
                        }
                        break;
                    case "-lifactor":
                        if (++i < split.length) {
                            lifac = Float.parseFloat(split[i]);
                        }
                        break;
                    case "-ceradius":
                        if (++i < split.length) {
                            cerad = Integer.parseUnsignedInt(split[i]);
                        }
                        break;
                    case "-default":
                        lirad = 3;
                        cerad = 3;
                        lifac = HeatMapAnalysis.SQRT_HALF;
                        break;
                }
            } catch (Exception e) {

            }
        }

        // Set analyzers
        library.addAnalyzer("Influence", new LetterInfluenceAnalyzer(lirad, lifac));
        library.addAnalyzer("CommonEndings", new CommonEndingAnalyzer(cerad));
        library.addAnalyzer("WordLength", new WordLengthCorrelationAnalyzer());
        library.addAnalyzer("LengthCorrelation", new WordLengthCorrelationAnalyzer());
        library.addAnalyzer("CharBegin", new InitialCharDistributionAnalyzer());
        library.addAnalyzer("CharEnd", new CharEndPositionAnalyzer());
        library.addAnalyzer("Alphabet", new AlphabetAnalyzer());
        library.addAnalyzer("Repetitions", new RepeatLetterAnalyzer());

        System.out.println("Loaded " + library.analyzers().size() + " modules [" + lirad + ":" + lifac + ":" + cerad + "]");
    }

    protected void InfoHandler() {
        if (!VerifyLib()) {
            return;
        }

        System.out.println("  modules\t" + library.analyzers().size());
        System.out.println("  words  \t" + library.analyzedCount());
        System.out.println("  feeding\t" + library.isSelfFeeding());
    }

    protected void ClearHandler() {
        if (!VerifyLib()) {
            return;
        }

        library.clear();
        System.out.println("Emptied library contents");
    }

    protected void FeedHandler(String args) {
        if (!VerifyLib()) {
            return;
        }

        // Extract configuration
        String[] split = SplitArgs(args);
        boolean isFile = false;
        String source = "", delim = null;
        int count = Integer.MAX_VALUE;

        for (int i = 0; i < split.length; i++) {
            switch (split[i]) {
                case "-f":
                    if (++i < split.length) {
                        source = split[i];
                        isFile = true;
                    }
                    break;
                case "-t":
                    if (++i < split.length) {
                        source = split[i];
                        isFile = false;
                    }
                    break;
                case "-d":
                    if (++i < split.length) {
                        delim = split[i];
                    }
                    break;
                case "-C":
                    count = acount;
                    break;
                case "-c":
                    if (++i < split.length) {
                        try {
                            count = Integer.parseUnsignedInt(split[i]);
                        } catch (Exception e) {

                        }
                    }
                    break;
            }
        }

        if (source.equals("")) {
            DisplayError("feed");
            return;
        }

        int analyzed;
        if (delim == null) {
            if (isFile) {
                analyzed = library.analyzeFromFile(source, count);
            } else {
                analyzed = library.analyze(source, count);
            }
        } else if (isFile) {
            analyzed = library.analyzeFromFile(source, delim, count);
        } else {
            analyzed = library.analyze(source, delim, count);
        }

        acount = analyzed;
    }

    protected void FilterHandler(String args) {
        if (!VerifyLib()) {
            return;
        }

        // Extract configuration
        String[] split = SplitArgs(args);

        if (split.length == 0) {
            DisplayError("filter");
        }

        String mode = split[0];
        String source = split.length < 2 ? null : split[1];

        switch (mode) {
            case "-c":
                library.setFilter(GarblerTranslator.None);
                break;
            case "-w":
                library.setFilter(GarblerTranslator.caseInsensitive);
                break;
            case "-f":
                try {
                    library.setFilter(GarblerTranslator.createFromFile(source));
                } catch (IOException e) {
                    System.out.println("Unable to read translator file");
                }
                break;
            default:
                DisplayError("filter");
                break;
        }
    }

    /**
     * Parses a command with given contextual arguments and calls the
     * appropriate methods
     *
     * @param cmd the command
     * @param args space-separated arguments
     */
    public void parse(String cmd, String args) {
        switch (cmd.toUpperCase()) {
            case "HELP":
                HelpHandler(args);
                break;
            case "INIT":
                InitHandler(args);
                break;
            case "INFO":
                InfoHandler();
                break;
            case "CLEAR":
                ClearHandler();
            case "FEED":
                FeedHandler(args);
                break;
            case "FILTER":
                FilterHandler(args);
                break;
            case "QUIT":
            case "EXIT":
                System.exit(0);
                break;
            default:
                System.out.println("Unrecognized command '" + cmd + "'");
                break;
        }
    }

    protected void DisplayError(String cmd) {
        System.out.println("Invalid command usage. See 'help " + cmd + "' for more information.");
    }

    /**
     * Verifies that a library exists
     *
     * @return
     */
    protected boolean VerifyLib() {
        if (library == null) {
            System.out.println("Uninitialized library. View 'help init' for more information.");
            return false;
        }
        return true;
    }

    /**
     * Helper method which splits an argument String into tokens, splitting on
     * whitespace but ignoring spaces between quotes
     *
     * @param args
     * @return
     */
    protected String[] SplitArgs(String args) {
        List<String> list;
        list = new ArrayList();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(args);
        while (m.find()) {
            String s = m.group(1);
            if (s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
                s = s.substring(1, s.length() - 1);
            }
            list.add(s);
        }
        return list.toArray(new String[list.size()]);
    }
}
