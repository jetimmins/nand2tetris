package vmtranslator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static vmtranslator.CommandType.*;
import static vmtranslator.CommandType.Arithmetic.*;
import static vmtranslator.TestFiles.*;

public class ParserTest {

    private Parser underTest;

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
        underTest = new Parser(COMMANDS.asPath());
    }

    @Test
    void shouldHaveCommands() {
        underTest = new Parser(COMMANDS.asPath());
        assertTrue(underTest.hasMoreCommands());
    }

    @Test
    void shouldAdvance() {
        underTest = new Parser(COMMANDS.asPath());

        underTest.advance();
        assertTrue(underTest.hasMoreCommands());
        underTest.advance();
        assertTrue(underTest.hasMoreCommands());
    }

    @Test
    void shouldGetCommandType() {
        underTest = new Parser(COMMANDS.asPath());

        underTest.advance();
        assertThat(underTest.getCommandType()).isEqualTo(ARITHMETIC);
        underTest.advance();
        assertThat(underTest.getCommandType()).isEqualTo(ARITHMETIC);
        underTest.advance();
        assertThat(underTest.getCommandType()).isEqualTo(ARITHMETIC);
        underTest.advance();
        assertThat(underTest.getCommandType()).isEqualTo(ARITHMETIC);
        underTest.advance();
        assertThat(underTest.getCommandType()).isEqualTo(ARITHMETIC);
        underTest.advance();
        assertThat(underTest.getCommandType()).isEqualTo(ARITHMETIC);
        underTest.advance();
        assertThat(underTest.getCommandType()).isEqualTo(ARITHMETIC);
        underTest.advance();
        assertThat(underTest.getCommandType()).isEqualTo(ARITHMETIC);
        underTest.advance();
        assertThat(underTest.getCommandType()).isEqualTo(ARITHMETIC);

        underTest.advance();
        assertThat(underTest.getCommandType()).isEqualTo(PUSH);
        underTest.advance();
        assertThat(underTest.getCommandType()).isEqualTo(POP);
        underTest.advance();
        assertThat(underTest.getCommandType()).isEqualTo(LABEL);
        underTest.advance();
        assertThat(underTest.getCommandType()).isEqualTo(GOTO);
        underTest.advance();
        assertThat(underTest.getCommandType()).isEqualTo(IF);
        underTest.advance();
        assertThat(underTest.getCommandType()).isEqualTo(FUNCTION);
        underTest.advance();
        assertThat(underTest.getCommandType()).isEqualTo(RETURN);
        underTest.advance();
        assertThat(underTest.getCommandType()).isEqualTo(CALL);
    }

    @Test
    void shouldReturnArgsForCommands() {
        underTest = new Parser(COMMANDS.asPath());

        underTest.advance();
        assertThat(underTest.arg1()).isEqualTo(ADD.toString());
        underTest.advance();
        assertThat(underTest.arg1()).isEqualTo(SUB.toString());
        underTest.advance();
        assertThat(underTest.arg1()).isEqualTo(NEG.toString());
        underTest.advance();
        assertThat(underTest.arg1()).isEqualTo(EQ.toString());
        underTest.advance();
        assertThat(underTest.arg1()).isEqualTo(GT.toString());
        underTest.advance();
        assertThat(underTest.arg1()).isEqualTo(LT.toString());
        underTest.advance();
        assertThat(underTest.arg1()).isEqualTo(AND.toString());
        underTest.advance();
        assertThat(underTest.arg1()).isEqualTo(OR.toString());
        underTest.advance();
        assertThat(underTest.arg1()).isEqualTo(NOT.toString());

        underTest.advance();
        assertThat(underTest.arg1()).isEqualTo("constant");
        assertThat(underTest.arg2()).isEqualTo("0");
        underTest.advance();
        assertThat(underTest.arg1()).isEqualTo("local");
        assertThat(underTest.arg2()).isEqualTo("1");
        underTest.advance();
        assertThat(underTest.arg1()).isEqualTo("thing");
        underTest.advance();
        assertThat(underTest.arg1()).isEqualTo("thing");
        underTest.advance();
        assertThat(underTest.arg1()).isEqualTo("thing");
        underTest.advance();
        assertThat(underTest.arg1()).isEqualTo("func");
        assertThat(underTest.arg2()).isEqualTo("0");
        underTest.advance();
        assertThatThrownBy(() -> underTest.arg1())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Tried to get single arg for command type RETURN which has 0 args");
        underTest.advance();
        assertThat(underTest.arg1()).isEqualTo("func");
    }

    @Test
    void shouldThrowWhenGettingWrongArgs() {
        underTest = new Parser(COMMANDS.asPath());

        underTest.advance();
        assertThatThrownBy(() -> underTest.arg2())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Tried to get 2nd arg of command type ARITHMETIC which has 1 arg");

        underTest.advance();
        assertThatThrownBy(() -> underTest.arg2())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Tried to get 2nd arg of command type ARITHMETIC which has 1 arg");

        underTest.advance();
        assertThatThrownBy(() -> underTest.arg2())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Tried to get 2nd arg of command type ARITHMETIC which has 1 arg");

        underTest.advance();
        assertThatThrownBy(() -> underTest.arg2())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Tried to get 2nd arg of command type ARITHMETIC which has 1 arg");

        underTest.advance();
        assertThatThrownBy(() -> underTest.arg2())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Tried to get 2nd arg of command type ARITHMETIC which has 1 arg");

        underTest.advance();
        assertThatThrownBy(() -> underTest.arg2())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Tried to get 2nd arg of command type ARITHMETIC which has 1 arg");

        underTest.advance();
        assertThatThrownBy(() -> underTest.arg2())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Tried to get 2nd arg of command type ARITHMETIC which has 1 arg");

        underTest.advance();
        assertThatThrownBy(() -> underTest.arg2())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Tried to get 2nd arg of command type ARITHMETIC which has 1 arg");

        underTest.advance();
        assertThatThrownBy(() -> underTest.arg2())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Tried to get 2nd arg of command type ARITHMETIC which has 1 arg");


        underTest.advance(); // 2 args
        underTest.advance(); // 2 args
        underTest.advance();
        assertThatThrownBy(() -> underTest.arg2())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Tried to get 2nd arg of command type LABEL which has 1 arg");

        underTest.advance();
        assertThatThrownBy(() -> underTest.arg2())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Tried to get 2nd arg of command type GOTO which has 1 arg");

        underTest.advance();
        assertThatThrownBy(() -> underTest.arg2())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Tried to get 2nd arg of command type IF which has 1 arg");

        underTest.advance(); // 2 args
        underTest.advance();
        assertThatThrownBy(() -> underTest.arg1())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Tried to get single arg for command type RETURN which has 0 args");
    }
}
