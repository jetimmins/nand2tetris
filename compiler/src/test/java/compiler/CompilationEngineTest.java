package compiler;

import compiler.vm.Kind;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static compiler.TestFiles.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public abstract class CompilationEngineTest {

    private CompilationEngine underTest;
    private Path source;

    public abstract CompilationEngine getEngine(Path src);
    public abstract Path resolveTarget(Path path);
    public Path resolveFixture(Path src) {
        Path target = resolveTarget(src);
        return target
                .getParent()
                .resolve("fixtures")
                .resolve(target.getFileName());
    }

    /**
     * Assertion that output behaviour remains the same. Update the fixtures with the new output if a change is intended
     */
    @AfterEach
    void tearDown() throws IOException {
        underTest.close();
        Path target = resolveTarget(source);
        assertThat(Files.mismatch(target, resolveFixture(source))).isEqualTo(-1L);
        target.toFile().deleteOnExit();
    }

    @Test
    void shouldCompileClass() {
        source = BASIC.src();
        underTest = getEngine(source);
        underTest.compileClass();
    }

    @Test
    void shouldCompileClassVarDec() {
        source = CLASS_VAR_DEC.src();
        underTest = getEngine(source);
        underTest.compileClass();
        underTest.symbolTable().ifPresent(table ->
                assertThat(table.varCount(Kind.FIELD)).isEqualTo(4));
    }

    @Test
    void shouldCompileVarDec() {
        source = VAR_DEC.src();
        underTest = getEngine(source);
        underTest.compileClass();
        underTest.symbolTable().ifPresent(table ->
                assertThat(table.varCount(Kind.VAR)).isEqualTo(4));
    }

    @Test
    void shouldCompileEmptySubroutine() {
        source = EMPTY_SUB.src();
        underTest = getEngine(source);
        underTest.compileClass();
    }

    @Test
    void shouldCompileAllSubroutineTypes() {
        source = EMPTY_SUBS.src();
        underTest = getEngine(source);
        underTest.compileClass();
    }

    @Test
    void shouldCompileParamList() {
        source = PARAM_LIST.src();
        underTest = getEngine(source);
        underTest.compileClass();
        underTest.symbolTable().ifPresent(table ->
                assertThat(table.varCount(Kind.ARG)).isEqualTo(2));
    }

    @Test
    void shouldCompileCallsNoExpressions() {
        source = CALL.src();
        underTest = getEngine(source);
        underTest.compileClass();
    }

    @Test
    void shouldCompileArraysExpressions() {
        source = ARRAY_TEST.src();
        underTest = getEngine(source);
        underTest.compileClass();
    }

    @Test
    void shouldCompileLetStatement() {
        source = LET.src();
        underTest = getEngine(source);
        underTest.compileClass();
    }

    @Test
    void shouldCompileDoStatement() {
        source = DO.src();
        underTest = getEngine(source);
        underTest.compileClass();
    }

    @Test
    void shouldCompileWhileStatement() {
        source = WHILE.src();
        underTest = getEngine(source);
        underTest.compileClass();
    }

    @Test
    void shouldCompileReturnStatement() {
        source = RETURN.src();
        underTest = getEngine(source);
        underTest.compileClass();
    }

    @Test
    void shouldCompileIfStatement() {
        source = IF_ELSE.src();
        underTest = getEngine(source);
        underTest.compileClass();
    }

    @Test
    void shouldCompileExpressions() {
        source = COMPLEX_EXPR.src();
        underTest = getEngine(source);
        underTest.compileClass();
    }

    @Test
    void shouldCompileObjectCall() {
        source = CALL_OBJECT.src();
        underTest = getEngine(source);
        underTest.compileClass();
    }
}
