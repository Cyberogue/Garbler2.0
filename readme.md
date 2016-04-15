#CGarbler

##Intro
CGarbler is a console application wrapper for the Garbler2.0 library and is being developped alongside Garbler2.0. This program and the corresponding library are fully compatible with unicode text.

##Download
This can be downloaded [here](http://www.filedropper.com/cgarbler100)

##Execution
To run the program navigate to CGarbler.jar within command line or terminal and enter the line

java -jar CGarbler.jar

Additionally, the operation allows for initialization on run. This is done by appending -i <params>. This is identical to typing INIT <params> after startup. The recommended usage of this is simply loading the defaults.

java -jar CGarbler.jar -i -default

##Commands

###Help
Displays information about another command. Usage is help <command>.

###Init
Initializes the library using the specified configuration. A value of -default loads the default configuration. Any terms added after this replace the default configuration with custom parameters. 

This command can also be used to re-initialize a library with new values. This dumps the current library contents, if any as the currently stored data would be incompatible with the new values.

###Info
Displays current information about the library including how many words it has parsed and how many modules are loaded.

###Dump
Dumps all statistics the library has analyzed. This is identical to starting a new library with the same configuration as the current.

###Feed
Feeds text into the library, either from a file or from user-input

###Filter
Sets the output or input filter to a specified translator. A filter is simply a mapping from one character to another. The library contains two of these - one at the output and one at the input. 

The input mapping may be used to facilitate parsing of text internally or to interpret certain characters as different sets of characters for generation. By default this is set to case-insensitve.

THe output mapping changes specific characters in the generated text with new ones. This is useful for generating text that is visually distinct from its source but maintains all the other inherent similarities.

The only filters included internally are -c (clear) and -w (case-insensitve). Any additional filters must be loaded from a file (GTF, Garbler Translator File). View help for more info.

###Config
Sets the run-time configuration of the library. Currently the only option is to toggle self-feeding.

A self-feeding library feeds itself any text that it generates.

#Garble
Generates a line or lines of text from the stored data and prints it to output. (TODO: print to file instead)

###Quit / Exit
Exits the program

##Analyzers
There are currently 8 analyzers loaded into and used by the program.

###Letter Influence Analyzer
Keeps track of the relative positions and frequencies of certain letters with regards to other letters. For example, the probability that a particular letter will appear n spaces after another letter.

Parameters: 
Influence: used to determine how much influence previous letters have on the new letter. A low influence means that the newest letters have the most effect on newer letters whereas a high influence causes both new and old letters to affect new letters.

Radius: the maximum range a letter can exert its influence over. Any letters this distance away (or more) from the end will ahve no effect on current letters.

###Common Ending Analyzer
Searches for common endings and appends them to currently-generated words when appropriate.

Parameters:
Radius: The longest-possible chunk to consider a ending. This does not necessarily mean that all endings will be this length as many will be shorter.

###Word Length Analyzer
Keeps track of the distribution of word lengths throughout the analyzed text.

###Length Correlation Analyzer
Maintains a correlation between the first character in a word and its word length.

###Beginning Character Analyzer
Keeps track of the distribution of the first character throughout the analyzed text.

###Ending Character Analyzer
Keeps track of how often each character is found as the last character in a word. 

Note: not currently in use. This will be used to help filter out stray characters in the middle of words.

###Alphabet Analyzer
Keeps track of every single character used. This exists simply as a way to extract the full character set.

###Repetitions Analyzer
Keeps track of how many times certain letters are repeated to prevent long never-ending sequences.

Note: there is currently a minor issue with this one. I will be fixing it soon.
