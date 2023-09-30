package assembler.command;

import assembler.AsmToBin;

import java.util.Optional;

class CCommand implements Command {

    private final String asm;
    private String binary;
    public CCommand(String asm) {
        this.asm = asm;
    }

    @Override
    public Optional<String> asBinary() {
        if (binary == null) {
            binary = compComponent(asm) +
                    destComponent(asm) +
                    jumpComponent(asm);
        }

        return Optional.of(binary);
    }

    @Override
    public String asAsm() {
        return asm;
    }

    private String destComponent(String asm) {
        String asmDest = asm.contains("=") ? asm.split("=")[0] : "";
        return AsmToBin.dest(asmDest);
    }

    private String compComponent(String asm) {
        String asmComp = asm.contains("=") ?
                asm.split("=")[1].split(";")[0] :
                asm.split(";")[0];
        return AsmToBin.comp(asmComp);
    }

    private String jumpComponent(String asm) {
        String asmJump = "";

        if (asm.contains("=")) {
            if (asm.contains(";")) {
                asmJump = asm.split("=")[1].split(";")[1];
            }
        } else if (asm.contains(";")) {
            asmJump = asm.split(";")[1];
        }

        return AsmToBin.jump(asmJump);
    }
}
