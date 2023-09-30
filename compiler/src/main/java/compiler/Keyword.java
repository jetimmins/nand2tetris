package compiler;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum Keyword {
    CLASS,
    METHOD,
    FUNCTION,
    CONSTRUCTOR,
    INT,
    BOOLEAN,
    CHAR,
    VOID,
    VAR,
    STATIC,
    FIELD,
    LET,
    DO,
    IF,
    ELSE,
    WHILE,
    RETURN,
    TRUE,
    FALSE,
    NULL,
    THIS,
    ERROR;


    private static final Set<Keyword> STATEMENTS = Set.of(LET, IF, WHILE, DO, RETURN);
    private static final Set<Keyword> SUBROUTINES = Set.of(CONSTRUCTOR, METHOD, FUNCTION);
    private static final Set<Keyword> CLASS_VARS = Set.of(FIELD, STATIC);

    public static final Set<String> JACK_KEYWORDS = Arrays.stream(Keyword.values())
            .filter(keyword -> keyword != ERROR)
            .map(v -> v.toString().toLowerCase())
            .collect(Collectors.toSet());

    public static Keyword fromJack(String jack) {
        return Arrays.stream(Keyword.values())
                .filter(k -> k.toString().toLowerCase().equals(jack))
                .findFirst()
                .orElse(ERROR);
    }

    public static String toVm(Keyword keyword) {
        return keyword.toString().toLowerCase();
    }

    public static boolean isStatement(Keyword keyword) {
        return STATEMENTS.contains(keyword);
    }

    public static boolean isSubroutineDec(String keyword) {
        return SUBROUTINES.contains(fromJack(keyword));
    }

    public static boolean isClassVarDec(String keyword) {
        return CLASS_VARS.contains(fromJack(keyword));
    }
}
