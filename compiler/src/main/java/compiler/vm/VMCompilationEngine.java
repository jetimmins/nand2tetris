package compiler.vm;

import compiler.CompilationEngine;
import compiler.JackTokeniser;
import compiler.Keyword;
import compiler.Token;

import java.nio.file.Path;
import java.util.Optional;

import static compiler.Keyword.*;
import static compiler.vm.Kind.ARG;
import static compiler.vm.Kind.NONE;
import static java.util.Arrays.deepToString;
import static java.util.Arrays.stream;

public class VMCompilationEngine implements CompilationEngine {

    private final VMWriter writer;
    private final JackTokeniser tokeniser;
    private final SymbolTable symbolTable = new SymbolTable();

    private Token currentToken;
    private String className = "NONE";

    private int uid = 0;

    public VMCompilationEngine(Path src, Path targetDir) {
        this.writer = new VMWriter(src, targetDir);
        this.tokeniser = new JackTokeniser(src);
    }

    private void next() {
        tokeniser.advance();
        currentToken = tokeniser.token();
    }

    private void nextAsserting(String... assertions) {
        tokeniser.advance();
        currentToken = tokeniser.token();
        boolean pass = stream(assertions).anyMatch(assertion -> currentToken.value().equals(assertion));
        if (!pass) {
            throw new RuntimeException("Unexpected symbol '%s', expected '%s'"
                    .formatted(currentToken.value(), deepToString(assertions)));
        }
    }

    private void nextAsserting(Keyword... assertions) {
        nextAsserting(stream(assertions).map(Keyword::toVm).toArray(String[]::new));
    }

    @Override
    public void compileClass() {
        nextAsserting(CLASS);
        next();
        className = currentToken.value();
        nextAsserting("{");
        next();

        while (isClassVarDec(currentToken.value()) || isSubroutineDec(currentToken.value())) {
            if (isClassVarDec(currentToken.value())) {
                compileClassVarDec();
            } else {
                compileSubroutine();
            }
        }
    }

    private void defineSymbol(Kind kind, String type) {
        next();
        String name = currentToken.value();
        symbolTable.define(name, type, kind);
        nextAsserting(",", ";", ")");
    }

    @Override
    public void compileClassVarDec() {
        compileDec();
    }

    private int compileDec() {
        int numDec = 0;
        Kind kind = Kind.fromJack(currentToken.value());
        if (kind.equals(NONE)) {
            throw new RuntimeException("Tried to compile declaration with invalid kind for token %s"
                    .formatted(currentToken.value()));
        }

        next();
        String type = currentToken.value();
        defineSymbol(kind, type);
        numDec++;

        while (currentToken.value().equals(",")) {
            defineSymbol(kind, type);
            numDec++;
        }

        next();
        return numDec;
    }

    @Override
    public void compileSubroutine() {
        symbolTable.startSubroutine();
        Keyword subroutineType = Keyword.fromJack(currentToken.value());

        next();
        next();
        String name = currentToken.value();
        nextAsserting("(");

        compileParameterList(subroutineType);
        next();

        int numVar = compileVarDec();
        writer.writeFunction(className, name, numVar);

        if (subroutineType.equals(METHOD)) {
            writer.writePush(Segment.ARGUMENT, 0);
            writer.writePop(Segment.POINTER, 0);
        }

        if (subroutineType.equals(CONSTRUCTOR)) {
            int numFields = symbolTable.varCount(Kind.FIELD);
            writer.writePush(Segment.CONSTANT, numFields);
            writer.writeCall("Memory", "alloc", 1);
            writer.writePop(Segment.POINTER, 0);
        }

        compileStatements();
        next();
    }

    private void compileParameterList(Keyword subroutineType) {
        next();
        if (currentToken.value().equals(")")) {
            nextAsserting("{");
            return;
        }

        if (subroutineType.equals(METHOD)) {
            symbolTable.startMethod();
        }

        defineSymbol(ARG, currentToken.value());

        while (currentToken.value().equals(",")) {
            next();
            defineSymbol(ARG, currentToken.value());
        }

        nextAsserting("{");
    }

    @Override
    public void compileParameterList() {
        next();

        if (currentToken.value().equals(")")) {
            nextAsserting("{");
            return;
        }

        defineSymbol(ARG, currentToken.value());

        while (currentToken.value().equals(",")) {
            next();
            defineSymbol(ARG, currentToken.value());
        }

        nextAsserting("{");
    }

    @Override
    public int compileVarDec() {
        int numVar = 0;
        while (Keyword.fromJack(currentToken.value()).equals(Keyword.VAR)) {
            numVar += compileDec();
        }
        return numVar;
    }

    @Override
    public void compileStatements() {
        Keyword keyword = Keyword.fromJack(currentToken.value());
        while (isStatement(keyword)) {
            switch (keyword) {
                case LET -> compileLet();
                case IF -> compileIf();
                case WHILE -> compileWhile();
                case DO -> compileDo();
                case RETURN -> compileReturn();
                default -> throw new RuntimeException("Unexpected keyword '%s'".formatted(currentToken.value()));
            }

            keyword = Keyword.fromJack(currentToken.value());
        }
    }

    @Override
    public void compileDo() {
        next();
        String identifier = currentToken.value();
        nextAsserting("(", ".");
        writeSubroutineCall(identifier);
        next();
        writer.writePop(Segment.TEMP, 0);
    }

    private void writeSubroutineCall(String identifier) {
        int numArgs;
        String methodName;

        if (currentToken.value().equals("(")) { // self method
            writer.writePush(Segment.POINTER, 0); // push self
            numArgs = compileExpressionList();
            writer.writeCall(className, identifier, numArgs + 1);
        } else { // calling another object subroutine (or other/self function)
            Kind kind = symbolTable.kindOf(identifier);
            if (kind.equals(NONE)) { // function
                next();
                methodName = currentToken.value();
                nextAsserting("(");
                numArgs = compileExpressionList();
                writer.writeCall(identifier, methodName, numArgs);
            } else { // object method
                int index = symbolTable.indexOf(identifier);
                String type = symbolTable.typeOf(identifier);
                writer.writePush(kind.segment(), index);
                next();
                methodName = currentToken.value();
                nextAsserting("(");
                numArgs = compileExpressionList();
                writer.writeCall(type, methodName, numArgs + 1);
            }
        }
    }

    @Override
    public void compileLet() {
        next();
        Kind kind = symbolTable.kindOf(currentToken.value());
        int index = symbolTable.indexOf(currentToken.value());
        nextAsserting(",", "[", "=");

        if (currentToken.value().equals("[")) {
            next();
            compileExpression();
            writer.writePush(kind.segment(), index);
            nextAsserting("=");
            next();
            writer.writeArithmetic(Command.ADD);
            compileExpression();
            writer.writePop(Segment.TEMP, 0);
            writer.writePop(Segment.POINTER, 1); // set array base + index to that
            writer.writePush(Segment.TEMP, 0);
            writer.writePop(Segment.THAT, 0);

            next();
        } else {
            next();
            compileExpression();
            writer.writePop(kind.segment(), index);
            next();
        }
    }

    @Override
    public void compileWhile() {
        int unique = uid++;
        writer.writeLabel("while_start_%s".formatted(unique));
        nextAsserting("(");
        compileExpression();
        writer.writeArithmetic(Command.NOT);
        writer.writeIf("while_end_%s".formatted(unique));

        next();
        compileStatements();
        writer.writeGoto("while_start_%s".formatted(unique));
        writer.writeLabel("while_end_%s".formatted(unique));
        next();
    }

    @Override
    public void compileReturn() {
        next();
        if (currentToken.value().equals(";")) {
            writer.writePush(Segment.CONSTANT, 0);
        } else {
            compileExpression();
        }

        writer.writeReturn();
        next();
    }

    @Override
    public void compileIf() {
        int unique = uid++;
        nextAsserting("(");
        compileExpression();
        writer.writeIf("if_true_%s".formatted(unique));
        writer.writeGoto("else_start_%s".formatted(unique));
        writer.writeLabel("if_true_%s".formatted(unique));
        next();
        compileStatements();
        next();

        boolean elseBlock = Keyword.fromJack(currentToken.value()).equals(ELSE);
        if (elseBlock) {
            writer.writeGoto("if_else_end_%s".formatted(unique));
        }
        writer.writeLabel("else_start_%s".formatted(unique));
        if (elseBlock) {
            nextAsserting("{");
            next();
            compileStatements();
            next();
            writer.writeLabel("if_else_end_%s".formatted(unique));
        }
    }

    @Override
    public void compileExpression() {
        compileTerm();

        while (BINARY_OPERATORS.contains(currentToken.value())) {
            String binaryOp = currentToken.value();
            next();
            compileTerm();
            writer.writeArithmetic(Command.fromJack(binaryOp));
        }
    }

    @Override
    public void compileTerm() {
        switch (currentToken.type()) {
            case INTEGER_CONSTANT -> compileIntConstant();
            case KEYWORD -> compileTermKeyword();
            case STRING_CONSTANT -> compileStringConstant();
            case IDENTIFIER -> compileTermIdentifier();
            case SYMBOL -> compileTermSymbol();
        }
    }

    private void compileStringConstant() {
        writer.writeStringConstant(currentToken.value());
        nextAsserting(",", ")", "]", ";");
    }

    private void compileIntConstant() {
        writer.writePush(Segment.CONSTANT, Integer.parseInt(currentToken.value()));
        next();
    }

    private void compileTermKeyword() {
        switch (Keyword.fromJack(currentToken.value())) {
            case TRUE -> {
                writer.writePush(Segment.CONSTANT, 1);
                writer.writeArithmetic(Command.NEG);
            }
            case NULL, FALSE -> writer.writePush(Segment.CONSTANT, 0);
            case THIS -> writer.writePush(Segment.POINTER, 0);
            default -> throw new IllegalStateException("Cannot compile keyword %s as term"
                    .formatted(currentToken.value()));
        }

        next();
    }

    private void compileTermSymbol() {
        if (currentToken.value().equals("(")) {
            next();
            compileExpression();
            next();
        } else if (UNARY_OPERATORS.contains(currentToken.value())) {
            String unaryOp = currentToken.value();
            next();
            compileTerm();
            writer.writeArithmetic(Command.fromJack(unaryOp));
        } else throw new IllegalStateException("Can't compile symbol %s".formatted(currentToken.value()));
    }

    private void compileTermIdentifier() {
        String identifier = currentToken.value();
        next();
        if (currentToken.value().equals("(") || currentToken.value().equals(".")) { // subroutine
            writeSubroutineCall(identifier);
        } else if (currentToken.value().equals("[")) {                              // array
            next();
            compileExpression();
            Kind kind = symbolTable.kindOf(identifier);
            int index = symbolTable.indexOf(identifier);
            writer.writePush(kind.segment(), index);
            writer.writeArithmetic(Command.ADD);
            writer.writePop(Segment.POINTER, 1);
            writer.writePush(Segment.THAT, 0);
            next();
        } else {                                                                    // variable
            Kind kind = symbolTable.kindOf(identifier);
            int index = symbolTable.indexOf(identifier);
            writer.writePush(kind.segment(), index);
        }
    }

    @Override
    public int compileExpressionList() {
        int numExpr = 0;
        next();

        while (!currentToken.value().equals(")")) {
            if (currentToken.value().equals(",")) {
                next();
            }
            compileExpression();
            numExpr++;
        }

        nextAsserting(")", ";");
        return numExpr;
    }

    public Optional<SymbolTable> symbolTable() {
        return Optional.of(symbolTable);
    }

    @Override
    public void close() {
        try {
            writer.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
