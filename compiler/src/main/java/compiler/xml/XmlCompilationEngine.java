package compiler.xml;

import compiler.*;
import compiler.vm.SymbolTable;

import java.nio.file.Path;
import java.util.Optional;

import static compiler.Keyword.*;
import static java.util.Arrays.deepToString;
import static java.util.Arrays.stream;

public class XmlCompilationEngine implements CompilationEngine {

    private final XmlWriter writer;
    private final JackTokeniser tokeniser;
    private Token currentToken;

    public XmlCompilationEngine(Path src, Path targetDir) {
        this.writer = new XmlWriter(src, targetDir, Format.XML);
        this.tokeniser = new JackTokeniser(src);
        nextToken();
    }

    private void nextToken() {
        tokeniser.advance();
        currentToken = tokeniser.token();
    }

    private void write(Token token) {
        writer.writeElement(token.type().camel(), token.value());
    }

    private void write() {
        write(currentToken);
    }

    private void writeAsserting(String... assertions) {
        boolean pass = stream(assertions).anyMatch(assertion -> currentToken.value().equals(assertion));
        if (!pass) {
            throw new RuntimeException("Unexpected symbol '%s', expected '%s'"
                    .formatted(currentToken.value(), deepToString(assertions)));
        }
        write();
    }

    private void writeAdvance() {
        write();
        nextToken();
    }

    private void writeAdvanceAsserting(String... assertion) {
        writeAsserting(assertion);
        nextToken();
    }

    private void writeAdvanceAsserting(Keyword... assertion) {
        writeAdvanceAsserting(stream(assertion).map(Keyword::toVm).toArray(String[]::new));
    }

    @Override
    public void compileClass() {
        writer.openParent(currentToken.value());
        writeAdvanceAsserting(CLASS);
        writeAdvance();
        writeAdvanceAsserting("{");

        while (isClassVarDec(currentToken.value()) || isSubroutineDec(currentToken.value())) {
            if (isClassVarDec(currentToken.value())) {
                compileClassVarDec();
            } else {
                compileSubroutine();
            }
        }

        writeAsserting("}");
        writer.closeParent();
    }

    // handle list
    @Override
    public void compileClassVarDec() {
        writer.openParent("classVarDec");
        writeAdvanceAsserting(FIELD, STATIC);
        write();

        do {
            nextToken();
            writeAdvance();
            writeAsserting(",", ";");
        } while (currentToken.value().equals(","));

        nextToken();
        writer.closeParent();
    }

    @Override
    public void compileSubroutine() {
        writer.openParent("subroutineDec");
        writeAdvanceAsserting(CONSTRUCTOR, FUNCTION, METHOD);
        writeAdvance();
        writeAdvance();

        compileParameterList();

        writer.openParent("subroutineBody");
        writeAdvanceAsserting("{");
        compileVarDec();
        compileStatements();
        writeAdvanceAsserting("}");
        writer.closeParent();

        writer.closeParent();
    }

    @Override
    public void compileParameterList() {
        writeAdvanceAsserting("(");
        writer.openParent("parameterList");
        if (currentToken.value().equals(")")) {
            writer.closeParent();
            writeAdvance();
            return;
        }

        writeAdvance();
        writeAdvance();

        while (currentToken.value().equals(",")) {
            writeAdvance();
            writeAdvance();
            writeAdvance();
        }

        writer.closeParent();
        writeAdvanceAsserting(")");
    }

    @Override
    public int compileVarDec() {
        int numVar = 0;
        while (fromJack(currentToken.value()).equals(VAR)) {
            writer.openParent("varDec");

            writeAdvanceAsserting(VAR);
            write();

            do {
                nextToken();
                writeAdvance();
                writeAsserting(",", ";");
                numVar++;
            } while (currentToken.value().equals(","));

            nextToken();
            writer.closeParent();
        }
        return numVar;
    }

    @Override
    public void compileStatements() {
        writer.openParent("statements");
        Keyword keyword = fromJack(currentToken.value());
        while (isStatement(keyword)) {
            switch (keyword) {
                case LET -> compileLet();
                case IF -> compileIf();
                case WHILE -> compileWhile();
                case DO -> compileDo();
                case RETURN -> compileReturn();
                default -> throw new RuntimeException("Unexpected keyword " + currentToken.value());
            }

            keyword = fromJack(currentToken.value());
        }

        writer.closeParent();
    }

    @Override
    public void compileDo() {
        writer.openParent("doStatement");
        writeAdvanceAsserting(DO);
        Token identifierToken = currentToken;
        nextToken();
        writeSubroutineCall(identifierToken);

        writeAdvanceAsserting(";");
        writer.closeParent();
    }

    @Override
    public void compileLet() {
        writer.openParent("letStatement");
        writeAdvanceAsserting(LET);
        writeAdvance();

        if (currentToken.value().equals("[")) {
            writeAdvanceAsserting("[");
            compileExpression();
            writeAdvanceAsserting("]");
        }

        writeAdvance();
        compileExpression();
        writeAdvanceAsserting(";");
        writer.closeParent();
    }

    @Override
    public void compileWhile() {
        writer.openParent("whileStatement");
        writeAdvanceAsserting(WHILE);
        writeAdvanceAsserting("(");
        compileExpression();
        writeAdvanceAsserting(")");

        writeAdvanceAsserting("{");
        compileStatements();
        writeAdvanceAsserting("}");
        writer.closeParent();
    }

    @Override
    public void compileReturn() {
        writer.openParent("returnStatement");
        writeAdvanceAsserting(RETURN);
        if (!currentToken.value().equals(";")) {
            compileExpression();
        }

        writeAdvanceAsserting(";");
        writer.closeParent();
    }

    @Override
    public void compileIf() {
        writer.openParent("ifStatement");
        writeAdvanceAsserting(IF);

        writeAdvanceAsserting("(");
        compileExpression();
        writeAdvanceAsserting(")");

        writeAdvanceAsserting("{");
        compileStatements();
        writeAdvanceAsserting("}");

        if (fromJack(currentToken.value()).equals(ELSE)) {
            writeAdvanceAsserting(ELSE);
            writeAdvanceAsserting("{");
            compileStatements();
            writeAdvanceAsserting("}");
        }

        writer.closeParent();
    }

    @Override
    public void compileExpression() {
        writer.openParent("expression");

        // require some error handling here with lookaheads?
        compileTerm();

        while (BINARY_OPERATORS.contains(currentToken.value())) {
            writeAdvance();
            compileTerm();
        }

        writer.closeParent();
    }

    @Override
    public void compileTerm() {
        writer.openParent("term");
        switch (currentToken.type()) {
            case INTEGER_CONSTANT, KEYWORD, STRING_CONSTANT -> writeAdvance();
            case IDENTIFIER -> compileTermIdentifier();
            case SYMBOL -> compileTermSymbol();
        }
        writer.closeParent();
    }

    private void compileTermSymbol() {
        if (currentToken.value().equals("(")) {
            writeAdvance();
            compileExpression();
            writeAdvance();
        } else if (UNARY_OPERATORS.contains(currentToken.value())) {
            writeAdvance();
            compileTerm();
        }
    }

    private void compileTermIdentifier() {
        Token identifierToken = currentToken;
        nextToken();

        if (currentToken.value().equals("(") || currentToken.value().equals(".")) {
            writeSubroutineCall(identifierToken);
        } else {
            write(identifierToken);
        }

        if (currentToken.value().equals("[")) {
            writeAdvance();
            compileExpression();
            writeAdvanceAsserting("]");
        }
    }

    private void writeSubroutineCall(Token identifierToken) {
        write(identifierToken);
        if (currentToken.value().equals("(")) {
            compileExpressionList();
        } else {
            writeAdvanceAsserting(".");
            writeAdvance();
            compileExpressionList();
        }
    }

    @Override
    public int compileExpressionList() {
        int numExpr = 0;
        writeAdvance();
        writer.openParent("expressionList");

        while (!currentToken.value().equals(")")) {
            if (currentToken.value().equals(",")) {
                writeAdvance();
            }
            compileExpression();
            numExpr++;
        }

        writer.closeParent();
        writeAdvanceAsserting(")");
        return numExpr;
    }

    public Optional<SymbolTable> symbolTable() {
        return Optional.empty();
    }

    @Override
    public void close() {
        try {
            writer.close();
            tokeniser.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
