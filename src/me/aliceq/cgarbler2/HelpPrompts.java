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

import java.util.HashMap;

/**
 * Static class containing all the help prompts for different commands.
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public abstract class HelpPrompts {

    private static final String HELP_MSG = "Displays information about a specific command.";
    private static final String QUIT_MSG = "Exits the program with error code 0";

    private static final String INIT_MSG = "Initializes the program's library and script. If no arguments are provided this loads the default script";
    private static final String INFO_MSG = "Displays information about the current program.";
    private static final String CLEAR_MSG = "Clears all analyzed data.";

    private static final String FEED_MSG = "Feeds a chunk of text to the program.";
    private static final String FILTER_MSG = "Sets the input translator filter. By default Garbler uses a case-insensitive filter.";

    private static final String HELP_USAGE = "\thelp <command>";

    private static final String INIT_USAGE = "\tinit [-default | [-liradius <value>] [-lifactor <value>] [-ceradius <value>]]\n"
            + "-default : loads and uses the default configuration\n"
            + "-liradius <value> : sets the letter-influence radius (integer)\n"
            + "-lifactor <value> : sets the letter-influence factor (float)\n"
            + "-ceradius <vaule> : sets the common-endings radius (integer)\n"
            + "\nAdditionally, use argument -i <args...> to automatically initialize on start.";

    private static final String FEED_USAGE = "\tfeed <-f <file> | -t <text> [-c <count> | -C] [-d <delim>]\n\n"
            + "\t-f <file> : analyzes the contents of a file\n"
            + "\t-t <message> : analyzes a text string\n"
            + "\t-c <count> : analyzes up to a maximum number of words\n"
            + "\t-C : sets the analyze count to the previous number of words analyzed\n"
            + "\t-d <delim> : custom delimiter to split words with";

    private static final String FILTER_USAGE = "\tfilter <-c | -w | -f <file>>\n\n"
            + "\t-c : clears the input filter (no input filtering)\n"
            + "\t-w : case-insensitive input filtering\n"
            + "\t-f <file> : custom input filter from file. Each line in a translator file should be of the form oldChars=newChar, where oldChars is a comma-separated list of keys. "
            + "\n\t\tFor example, to transpose [q,p,d,b] into d's the file should contain the entry q,p,b=d";

    private static final HashMap<String, String> DESCRIPTORS = new HashMap() {
        {
            put("HELP", HELP_MSG);
            put("QUIT", QUIT_MSG);
            put("EXIT", QUIT_MSG);

            put("INIT", INIT_MSG);
            put("INFO", INFO_MSG);
            put("CLEAR", CLEAR_MSG);

            put("FEED", FEED_MSG);
            put("FILTER", FILTER_MSG);
        }
    };

    private static final HashMap<String, String> USAGE = new HashMap() {
        {
            put("HELP", HELP_USAGE);
            put("INIT", INIT_USAGE);
            put("FEED", FEED_USAGE);
            put("FILTER", FILTER_USAGE);
        }
    };

    public static final String getDescription(String arg) {
        return DESCRIPTORS.get(arg.toUpperCase());
    }

    public static final String getUsage(String arg) {
        return USAGE.get(arg.toUpperCase());
    }

    public static final String getFormatted(String arg) {
        String msg = getDescription(arg);
        String usage = getUsage(arg);

        if (msg == null) {
            return "No information found about on '" + arg + "'";
        } else {
            String s = "------\n" + arg.toUpperCase() + "\n" + msg;

            if (usage != null) {
                s += "\n\nUsage:\n" + usage;
            }

            s += "\n\nEnd of help";
            return s;
        }
    }

}
