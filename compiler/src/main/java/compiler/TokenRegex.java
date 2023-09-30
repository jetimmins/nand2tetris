package compiler;

import java.util.StringJoiner;
import java.util.regex.Pattern;

import static compiler.Keyword.JACK_KEYWORDS;

public class TokenRegex {
    public static Pattern tokeniserRegex() {
        return Pattern.compile(new StringJoiner("|")
                .add(commentBlockRegex().pattern())
                .add(symbolRegex().pattern())
                .add(stringRegex().pattern())
                .add(numberRegex().pattern())
                .add(keywordRegex().pattern())
                .add(idRegex().pattern())
                .toString());
    }

    public static Pattern commentBlockRegex() {
        return Pattern.compile("(/[*]{1,2})|(\\*/)");
    }


    public static Pattern symbolRegex() {
        return Pattern.compile("[{}()\\[\\].,;+\\-*/&|<>=~]");
    }

    public static Pattern keywordRegex() {
        return Pattern.compile(String.join("$|", JACK_KEYWORDS));
    }

    public static Pattern numberRegex() {
        return Pattern.compile("\\d+");
    }

    public static Pattern stringRegex() {
        return Pattern.compile("\"([^\"\n])*\"");
    }

    public static Pattern idRegex() {
        return Pattern.compile("[A-Za-z_][A-Za-z_0-9]*");
    }
}
