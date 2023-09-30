package compiler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static compiler.Keyword.fromJack;
import static compiler.TestFiles.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class JackTokeniserTest {

    private JackTokeniser underTest;

    @AfterEach
    void tearDown() {
        underTest.close();
    }

    @Test
    void shouldReadPath() {
        underTest = new JackTokeniser(BASIC.src());
    }

    @Test
    void shouldHaveMoreTokens() {
        underTest = new JackTokeniser(BASIC.src());
        assertThat(underTest.hasMoreTokens()).isTrue();
    }

    @Test
    void shouldAdvance() {
        underTest = new JackTokeniser(BASIC.src());
        underTest.advance();
        assertThat(underTest.hasMoreTokens()).isTrue();
    }

    @Test
    void shouldParseClass() {
        underTest = new JackTokeniser(BASIC.src());

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.KEYWORD);
        assertThat(fromJack(underTest.token().value())).isEqualTo(Keyword.CLASS);

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(underTest.token().value()).isEqualTo("Basic");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.SYMBOL);
        assertThat(underTest.token().value()).isEqualTo("{");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.SYMBOL);
        assertThat(underTest.token().value()).isEqualTo("}");

        assertThat(underTest.hasMoreTokens()).isFalse();
    }

    @Test
    void shouldNotHaveMoreTokensWhenWhitespaceAndCommentsRemain() {
        underTest = new JackTokeniser(BASIC.src());

        underTest.advance();
        underTest.advance();
        underTest.advance();
        underTest.advance();

        assertThat(underTest.hasMoreTokens()).isFalse();
    }

    @Test
    void shouldParseProgram() {
        underTest = new JackTokeniser(PROGRAM.src());

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.KEYWORD);
        assertThat(fromJack(underTest.token().value())).isEqualTo(Keyword.CLASS);

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(underTest.token().value()).isEqualTo("Program");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.SYMBOL);
        assertThat(underTest.token().value()).isEqualTo("{");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.KEYWORD);
        assertThat(fromJack(underTest.token().value())).isEqualTo(Keyword.FIELD);

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.KEYWORD);
        assertThat(fromJack(underTest.token().value())).isEqualTo(Keyword.INT);

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(underTest.token().value()).isEqualTo("myField");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.SYMBOL);
        assertThat(underTest.token().value()).isEqualTo(";");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.KEYWORD);
        assertThat(fromJack(underTest.token().value())).isEqualTo(Keyword.STATIC);

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(underTest.token().value()).isEqualTo("String");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(underTest.token().value()).isEqualTo("myStaticField");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.SYMBOL);
        assertThat(underTest.token().value()).isEqualTo("=");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.STRING_CONSTANT);
        assertThat(underTest.token().value()).isEqualTo("static string");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.SYMBOL);
        assertThat(underTest.token().value()).isEqualTo(";");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.KEYWORD);
        assertThat(fromJack(underTest.token().value())).isEqualTo(Keyword.CONSTRUCTOR);

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(underTest.token().value()).isEqualTo("Program");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(underTest.token().value()).isEqualTo("new");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.SYMBOL);
        assertThat(underTest.token().value()).isEqualTo("(");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.KEYWORD);
        assertThat(fromJack(underTest.token().value())).isEqualTo(Keyword.INT);

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(underTest.token().value()).isEqualTo("x");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.SYMBOL);
        assertThat(underTest.token().value()).isEqualTo(")");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.SYMBOL);
        assertThat(underTest.token().value()).isEqualTo("{");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.KEYWORD);
        assertThat(fromJack(underTest.token().value())).isEqualTo(Keyword.LET);

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(underTest.token().value()).isEqualTo("myField");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.SYMBOL);
        assertThat(underTest.token().value()).isEqualTo("=");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.INTEGER_CONSTANT);
        assertThat(underTest.token().value()).isEqualTo("42");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.SYMBOL);
        assertThat(underTest.token().value()).isEqualTo(";");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.KEYWORD);
        assertThat(fromJack(underTest.token().value())).isEqualTo(Keyword.LET);

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(underTest.token().value()).isEqualTo("myField");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.SYMBOL);
        assertThat(underTest.token().value()).isEqualTo("=");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(underTest.token().value()).isEqualTo("x");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.SYMBOL);
        assertThat(underTest.token().value()).isEqualTo("+");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(underTest.token().value()).isEqualTo("x");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.SYMBOL);
        assertThat(underTest.token().value()).isEqualTo(";");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.SYMBOL);
        assertThat(underTest.token().value()).isEqualTo("}");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.SYMBOL);
        assertThat(underTest.token().value()).isEqualTo("}");

        assertThat(underTest.hasMoreTokens()).isFalse();
    }

    @Test
    void shouldParseStrings() {
        underTest = new JackTokeniser(SUB_WITH_STRINGS.src());

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.KEYWORD);
        assertThat(fromJack(underTest.token().value())).isEqualTo(Keyword.CLASS);

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(underTest.token().value()).isEqualTo("Program");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.SYMBOL);
        assertThat(underTest.token().value()).isEqualTo("{");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.KEYWORD);
        assertThat(fromJack(underTest.token().value())).isEqualTo(Keyword.CONSTRUCTOR);

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(underTest.token().value()).isEqualTo("Program");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(underTest.token().value()).isEqualTo("new");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.SYMBOL);
        assertThat(underTest.token().value()).isEqualTo("(");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.SYMBOL);
        assertThat(underTest.token().value()).isEqualTo(")");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.SYMBOL);
        assertThat(underTest.token().value()).isEqualTo("{");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.KEYWORD);
        assertThat(underTest.token().value()).isEqualTo("do");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(underTest.token().value()).isEqualTo("something");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.SYMBOL);
        assertThat(underTest.token().value()).isEqualTo(".");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(underTest.token().value()).isEqualTo("it");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.SYMBOL);
        assertThat(underTest.token().value()).isEqualTo("(");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.STRING_CONSTANT);
        assertThat(underTest.token().value()).isEqualTo("hi there let's see this");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.SYMBOL);
        assertThat(underTest.token().value()).isEqualTo(")");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.SYMBOL);
        assertThat(underTest.token().value()).isEqualTo(";");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.SYMBOL);
        assertThat(underTest.token().value()).isEqualTo("}");

        underTest.advance();
        assertThat(underTest.token().type()).isEqualTo(TokenType.SYMBOL);
        assertThat(underTest.token().value()).isEqualTo("}");


        assertThat(underTest.hasMoreTokens()).isFalse();
    }
}
