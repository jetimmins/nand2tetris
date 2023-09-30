package vmtranslator;

public class Asm {
    public enum Register {
        D,
        A,
        M
    }

    public enum Jump {
        JGT,
        JEQ,
        JGE,
        JLT,
        JNE,
        JLE,
        JMP
    }

    public enum Symbol {
        SP,
        LCL,
        ARG,
        THIS,
        THAT,
        SCREEN,
        KBD,
        R0,
        R1,
        R2,
        R3,
        R4,
        R5,
        R6,
        R7,
        R8,
        R9,
        R10,
        R11,
        R12,
        R13,
        R14,
        R15
    }

    public static String generateLabel(String label, int uid) {
        return "%s_%s".formatted(label, uid);
    }

    public static String label(String label) {
        return "(%s)\n".formatted(label);
    }

    public static String a(String label) {
        return "@%s\n".formatted(label);
    }

    public static String a(Symbol label) {
        return a(label.toString());
    }

    public static String a(int constant) {
        return a(String.valueOf(constant));
    }

    public static String a(VRegister register) {
        return a(register.getAssemblyCode());
    }

    public static String a(String filename, int address) {
        return "@%s.%s\n".formatted(filename, address);
    }

    public static String c(String dest, String comp) {
        return "%s=%s\n".formatted(dest, comp);
    }

    public static String c(Register dest, String comp) {
        return c(dest.toString(), comp);
    }

    public static String c(Register dest, int comp) {
        return c(dest.toString(), String.valueOf(comp));
    }

    public static String c(Register dest, Register register) {
        return c(dest.toString(), register.toString());
    }

    public static String c(Register register, Jump jump) {
        return "%s;%s\n".formatted(register, jump);
    }

    public static String jmp() {
        return "0;%s\n".formatted(Jump.JMP);
    }
}
