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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Alphabet for a Garbler library which transposes a standard character into a
 * variant character
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public abstract class GarblerTranslator {

    /**
     * Translator which simply transfers a character without modifying it
     */
    public static final GarblerTranslator None = new GarblerTranslator() {
        @Override
        public char transpose(char fromChar) {
            return fromChar;
        }
    };

    /**
     * Translator which returns the lowercase version of a character
     */
    public static final GarblerTranslator caseInsensitive = new GarblerTranslator() {
        @Override
        public char transpose(char fromChar) {
            return Character.toLowerCase(fromChar);
        }
    };

    /**
     * Hook method meant to transpose a standard character into a new character
     *
     * @param fromChar the character to transpose
     * @return the resulting character or string of characters
     */
    public abstract char transpose(char fromChar);

    /**
     * Translates an entire string line-by-line using the transpose(char)
     * method.
     *
     * @param fromString the string to transpose
     * @return a transposed string
     */
    public final String transpose(String fromString) {
        char[] result = fromString.toCharArray();
        for (int i = 0; i < result.length; i++) {
            result[i] = transpose(result[i]);
        }
        return String.valueOf(result);
    }

    /**
     * Creates a GarblerTranslator from a translation table
     *
     * @param translationTable mapping from each key to each value
     * @return a new GarblerTranslator instance
     */
    public static GarblerTranslator createFromMap(Map<Character, Character> translationTable) {
        return new GarblerTranslator.MapBased(translationTable);
    }

    /**
     * Creates a GarblerTranslator from a translator file.
     *
     * Each line in a translator file should be of the form oldChars=newChar,
     * where oldChars is a comma-separated list of keys. For example, to
     * transpose [c,k,q] into q's the file should contain the entry c,k=q
     *
     * @param file the file to read from
     * @return a new GarblerTranslator instance
     * @throws IOException if there was a problem reading the file
     */
    public static GarblerTranslator createFromFile(String file) throws IOException {
        return createFromFile(new File(file));
    }

    /**
     * Creates a GarblerTranslator from a translator file.
     *
     * Each line in a translator file should be of the form oldChars=newChar,
     * where oldChars is a comma-separated list of keys. For example, to
     * transpose [c,k,q] into q's the file should contain the entry c,k=q
     *
     * @param file the file to read from
     * @return a new GarblerTranslator instance
     * @throws IOException if there was a problem reading the file
     */
    public static GarblerTranslator createFromFile(File file) throws IOException {
        // Create input reader
        FileInputStream fis = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

        // Create map
        Map<Character, Character> mapping = new HashMap();

        // Read line by line
        for (String line; (line = reader.readLine()) != null;) {
            // Check if empty
            if (line.isEmpty()) {
                continue;
            }

            // Find the index of the equals
            int indexOf = line.indexOf('=');

            // Verify the line
            if (indexOf < 0 || line.length() > indexOf + 2) {
                throw new BadFileFormatException("Incorrectly formatted line in translator file");
            }

            // Extract the value as the character directly succeeding it
            char value = line.charAt(indexOf + 1);

            // Extract the string of keys
            String keys = line.substring(0, indexOf);

            // Read in charaters
            for (int i = 0; i < keys.length(); i += 2) {
                // Add key
                mapping.put(keys.charAt(i), value);
            }
        }

        return new GarblerTranslator.MapBased(mapping);
    }

    public static void main(String[] args) {
        try {
            GarblerTranslator t = GarblerTranslator.createFromFile("testfile.gtf");
            String s = "Hello World, my name is Alice Quiros and this is a test";
            s = GarblerTranslator.caseInsensitive.transpose(s);
            System.out.println(s);
            System.out.println(t.transpose(s));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Hashmap-based abstract implementation of GarblerTranslator
     */
    protected static class MapBased extends GarblerTranslator {

        // Internal hashmap which contains each translation
        private final Map<Character, Character> table;

        public MapBased() {
            this.table = new HashMap();
        }

        public MapBased(Map<Character, Character> fromMap) {
            this.table = fromMap;
        }

        /**
         * Registers a character exchange
         *
         * @param fromChar the character to transpose from
         * @param toChar the character[s] to transpose to
         * @return the previous registered value at fromChar, if any
         */
        protected char register(char fromChar, char toChar) {
            return table.put(fromChar, toChar);
        }

        /**
         * Pulls the appropriate value from the table. If none is registered,
         * the input character is simply passed through.
         *
         * @param fromChar the character to transpose
         * @return the new character
         */
        @Override
        public char transpose(char fromChar) {
            if (table.containsKey(fromChar)) {
                return table.get(fromChar);
            } else {
                return fromChar;
            }
        }
    }

    public static class BadFileFormatException extends RuntimeException {

        public BadFileFormatException(String message) {
            super(message);
        }
    }
}
