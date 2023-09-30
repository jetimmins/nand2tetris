package compiler;

import static compiler.Format.*;
import static compiler.io.CmdUtils.*;
import static compiler.JackAnalyser.*;
import static java.util.Arrays.*;

// lets write a writer interface, implemented by an XMLWriter and have that write the output of both
// tokeniser and analyser.
// Might be better to have a generic writer which is composed using a formatter interface which writes the XML
// to string!
// todo: better exceptions (keyword and tokeniser)


/*
review
problems:
- You want to perhaps remove the dependency of the compilation engine on the writer and the method calls required
so the logic of the context free grammar is reusable for different output formats. This might be achieved by outputting
text from the compilation engine in some format that can then be written by any writer. How though can an XML writer
insert nested tags once the tokenisation knowledge is lost?
 */
public class Main {


    public static void main(String[] args) {
        parseArgs(args).ifPresent(arg -> {
            if (arg.format().equals(TOKENS)) {
                tokenise(arg.src(), arg.target());
            } else {
                compile(arg.src(), arg.target(), arg.format());
            }
        });
    }
}