package assembler;

import assembler.command.Command;
import assembler.command.CommandFactory;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommandFactoryTest {
    private CommandFactory underTest;

    private final SymbolTable symbolTable = new SymbolTable();

    @Test
    void shouldCreateACommandWithConstant() {
        underTest = new CommandFactory(symbolTable::getAddress);
        Command command = underTest.command("@24");

        assertThat(command.asLabel()).isEmpty();
        assertThat(command.asAsm()).isEqualTo("@24");
        assertThat(command.asBinary()).isPresent();
    }

    @Test
    void shouldCreateACommandWithSymbol() {
        underTest = new CommandFactory(symbolTable::getAddress);
        symbolTable.addEntry("SYMBOL", 100);
        Command command = underTest.command("@SYMBOL");

        assertThat(command.asLabel()).isEmpty();
        assertThat(command.asAsm()).isEqualTo("@SYMBOL");
        assertThat(command.asBinary()).isPresent();
    }

    @Test
    void shouldReturnEmptyOnMissingSymbol() {
        underTest = new CommandFactory(symbolTable::getAddress);
        assertTrue(underTest.command("@SYMBOL").isEmpty());
    }

    @Test
    void shouldCreateLCommand() {
        underTest = new CommandFactory(symbolTable::getAddress);
        Command command = underTest.command("(LABEL)");

        assertThat(command.asLabel()).isPresent();
        assertThat(command.asLabel()).get().isEqualTo("LABEL");
        assertThat(command.asAsm()).isEqualTo("(LABEL)");
        assertThat(command.asBinary()).isEmpty();
    }

    @Test
    void shouldCreateCCommand() {
        underTest = new CommandFactory(symbolTable::getAddress);
        Command command = underTest.command("AD=M-1");

        assertThat(command.asLabel()).isEmpty();
        assertThat(command.asAsm()).isEqualTo("AD=M-1");
        assertThat(command.asBinary()).isPresent();
    }

    @Test
    void shouldThrowOnInvalidInstruction() {
        underTest = new CommandFactory(symbolTable::getAddress);
        assertThatThrownBy(() -> underTest.command("BOOM"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Symbol BOOM is not a valid Hack instruction");
    }

}
