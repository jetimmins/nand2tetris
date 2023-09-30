package assembler.command;

import java.util.Optional;

public class EmptyCommand implements Command {

    private final String asm;
    public EmptyCommand(String asm) {
        this.asm = asm;
    }

    @Override
    public Optional<String> asBinary() {
        return Optional.empty();
    }

    @Override
    public String asAsm() {
        return asm;
    }
}
