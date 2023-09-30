package assembler.command;

import java.util.Optional;

class LCommand implements Command {

    private final String asm;
    private final String label;

    public LCommand(String asm) {
        this.asm = asm;
        this.label = formatLabel(asm);
    }

    private String formatLabel(String asm) {
        return asm.substring(1, asm.length() - 1);
    }

    @Override
    public Optional<String> asBinary() {
        return Optional.empty();
    }

    @Override
    public String asAsm() {
        return asm;
    }
    
    @Override
    public Optional<String> asLabel() {
        return Optional.of(label);
    }
}
