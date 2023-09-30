package assembler.command;


import assembler.AsmToBin;

import java.util.Optional;
import java.util.function.Function;

public class CommandFactory {
    private final Function<String, Optional<String>> symbolLookup;

    public CommandFactory(Function<String, Optional<String>> symbolLookup) {
        this.symbolLookup = symbolLookup;
    }

    public Command command(String asm) {
        if (asm.charAt(0) == '@') {
            String symbol = asm.substring(1);
            return symbolLookup.apply(symbol)
                    .map(AsmToBin::symbol)
                    .<Command>map(addr -> new ACommand(asm, addr))
                    .orElse(isInteger(symbol) ?
                            new ACommand(asm, AsmToBin.symbol(symbol)) :
                            new EmptyCommand(symbol));
        }

        if (asm.contains("=") || asm.contains(";")) {
            return new CCommand(asm);
        }

        if (asm.startsWith("(")) {
            return new LCommand(asm);
        }

        throw new IllegalArgumentException("Symbol %s is not a valid Hack instruction".formatted(asm));
    }

    private boolean isInteger(String s) {
        if (s.isBlank()) {
            return false;
        }

        for (int i = 0; i < s.length(); i++) {
            if (Character.digit(s.charAt(i), 10) < 0) {
                return false;
            }
        }

        return true;
    }
}
