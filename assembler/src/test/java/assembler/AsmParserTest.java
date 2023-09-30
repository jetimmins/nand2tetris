package assembler;

import assembler.command.CommandFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static assembler.TestFiles.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class AsmParserTest {

    private Parser underTest;
    private final SymbolTable symbolTable = new SymbolTable();
    private final CommandFactory commandFactory = new CommandFactory(symbolTable::getAddress);

    @AfterEach
    void cleanUp() {
        try {
            underTest.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldReadFile() {
        underTest = new AsmParser(INSTRUCTIONS.filename(), commandFactory);
    }

    @Test
    void shouldHaveCommands() {
        underTest = new AsmParser(INSTRUCTIONS.filename(), commandFactory);
        assertTrue(underTest.hasMoreCommands());
    }

    @Test
    void shouldAdvance() {
        underTest = new AsmParser(INSTRUCTIONS.filename(), commandFactory);

        underTest.advance();
        assertTrue(underTest.hasMoreCommands());
        underTest.advance();
        assertTrue(underTest.hasMoreCommands());
    }

    @Test
    void shouldSkipCommentAndWhitespace() {
        underTest = new AsmParser(SYMBOLS_COMMENTS_WHITESPACE.filename(), commandFactory);

        underTest.advance();
        underTest.advance();
        assertThat(underTest.getCurrentAddress()).isEqualTo(2);

        underTest.advance(); // skip comment and whitespace
        assertThat(underTest.command().asLabel()).get().isEqualTo("LABEL");
    }

    @Test
    void shouldNotIncrementOnLabel() {
        underTest = new AsmParser(SYMBOLS_COMMENTS_WHITESPACE.filename(), commandFactory);

        underTest.advance();
        underTest.advance();
        assertThat(underTest.getCurrentAddress()).isEqualTo(2);

        underTest.advance(); // label
        assertThat(underTest.getCurrentAddress()).isEqualTo(2);

        underTest.advance();
        assertThat(underTest.getCurrentAddress()).isEqualTo(3);
    }
}
