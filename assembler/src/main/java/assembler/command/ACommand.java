package assembler.command;

import java.util.Optional;

public class ACommand implements Command {

    private final String asm;
    private final String binary;

    public ACommand(String asm, String binary) {
        this.asm = asm;
        this.binary = binary;
    }

    @Override
    public Optional<String> asBinary() {
        return Optional.of(binary);
    }

    @Override
    public String asAsm() {
        return asm;
    }
}
