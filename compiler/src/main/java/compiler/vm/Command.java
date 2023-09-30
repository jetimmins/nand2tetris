package compiler.vm;

import java.util.Arrays;

import static java.util.Arrays.*;

public enum Command {
    ADD("+"),
    SUB("-"),
    NEG("-"),
    EQ("="),
    GT("&gt;"),
    LT("&lt;"),
    AND("&amp;"),
    OR("|"),
    NOT("~"),
    DIVIDE("/"),
    MULTIPLY("*"),
    NONE("");

    public static boolean isSoftware(Command command) {
        return command.equals(MULTIPLY) || command.equals(DIVIDE);
    }

    public static Command fromJack(String jack) {
        return stream(Command.values())
                .filter(command -> command.jack().equals(jack))
                .findFirst()
                .orElse(NONE);
    }

    private final String jack;

    public String jack() {
        return jack;
    }

    Command(String jack) {
        this.jack = jack;
    }
}
