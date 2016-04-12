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

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Main entry point
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public class Main {
 
    public static void main(String[] args) {
        Program program = new Program();
        
        // Pass default arguments
        if (args.length > 0){
            if (args[0].equals("-i")){
                String s = "";
                for (int i = 1; i < args.length; i++){
                    s += args[i] + ' ';
                }
                program.InitHandler(s);
            }
        }
        
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(System.in);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            while (true) {
                String line = reader.readLine();
                String tag, pargs;

                int i = line.indexOf(' ');
                if (i >= 0) {
                    tag = line.substring(0, i);
                    pargs = line.substring(i + 1);
                } else {
                    tag = line;
                    pargs = "";
                }

                program.parse(tag, pargs);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            System.exit(1);
        }
    }
}
