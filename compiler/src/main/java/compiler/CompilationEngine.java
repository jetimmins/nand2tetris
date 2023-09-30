package compiler;

import compiler.vm.SymbolTable;

import java.util.Optional;
import java.util.Set;

public interface CompilationEngine extends AutoCloseable {
    Set<String> BINARY_OPERATORS = Set.of("+", "-", "*", "/", "&amp;", "|", "&lt;", "&gt;", "=");
    Set<String> UNARY_OPERATORS = Set.of("-", "~");

    void compileClass();

    void compileClassVarDec();

    void compileSubroutine();

    void compileParameterList();

    int compileVarDec();

    void compileStatements();

    void compileDo();

    void compileLet();

    void compileWhile();

    void compileReturn();

    void compileIf();

    void compileExpression();

    void compileTerm();

    int compileExpressionList();

    Optional<SymbolTable> symbolTable();

    @Override
    void close();
}
