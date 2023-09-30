package vmtranslator;

import vmtranslator.Asm.Symbol;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static vmtranslator.Asm.Symbol.*;

public enum VRegister {
    STATIC("static", null),
    ARGUMENT("argument", ARG),
    LOCAL("local", LCL),
    THIS("this", Symbol.THIS),
    THAT("that", Symbol.THAT),
    POINTER("pointer", Symbol.THIS),
    TEMP("temp", R5),
    CONSTANT("constant", null);

    private static final Map<String, VRegister> vRegistersByVmCode = new HashMap<>();

    static {
        Arrays.stream(VRegister.values()).forEach(v -> vRegistersByVmCode.put(v.vmCode, v));
    }

    private final String vmCode;

    private final Symbol assemblyCode;

    VRegister(String vmCode, Symbol assemblyCode) {
        this.vmCode = vmCode;
        this.assemblyCode = assemblyCode;
    }

    public static VRegister byVmCode(String vmCode) {
        return vRegistersByVmCode.get(vmCode);
    }

    public static boolean isPointer(VRegister vreg) {
        return vreg.equals(POINTER) || vreg.equals(TEMP);
    }

    public Symbol getAssemblyCode() {
        return assemblyCode;
    }
}
