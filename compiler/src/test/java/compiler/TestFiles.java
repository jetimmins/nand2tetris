package compiler;

import java.nio.file.Path;

public enum TestFiles {

    ARRAY_TEST(Path.of("src/test/resources/jack/arraytest.jack")),
    BASIC(Path.of("src/test/resources/jack/basic.jack")),
    CALL(Path.of("src/test/resources/jack/call.jack")),
    CALL_OBJECT(Path.of("src/test/resources/jack/callobject.jack")),
    CLASS_VAR_DEC(Path.of("src/test/resources/jack/classvardec.jack")),
    COMPLEX_EXPR(Path.of("src/test/resources/jack/complexexpressions.jack")),
    DO(Path.of("src/test/resources/jack/do.jack")),
    EMPTY_SUB(Path.of("src/test/resources/jack/emptysubroutine.jack")),
    IF_ELSE(Path.of("src/test/resources/jack/ifelse.jack")),
    LET(Path.of("src/test/resources/jack/let.jack")),
    PARAM_LIST(Path.of("src/test/resources/jack/paramlist.jack")),
    PROGRAM(Path.of("src/test/resources/jack/program.jack")),
    RETURN(Path.of("src/test/resources/jack/return.jack")),
    EMPTY_SUBS(Path.of("src/test/resources/jack/emptysubroutines.jack")),
    SUB_WITH_STRINGS(Path.of("src/test/resources/jack/subroutinewithstrings.jack")),
    VAR_DEC(Path.of("src/test/resources/jack/vardec.jack")),
    WHILE(Path.of("src/test/resources/jack/while.jack"));

    public static final Path TARGET_DIR = Path.of("src/test/resources");
    private final Path src;
    TestFiles(Path src) {
        this.src = src;
    }

    public Path src() {
        return this.src;
    }
}
