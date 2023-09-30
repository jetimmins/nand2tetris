package assembler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AsmToBin {
    private static final Map<String, String> dest = Map.of(
            "M", "001",
            "D", "010",
            "MD", "011",
            "A", "100",
            "AD", "110");
    private static final Map<String, String> comp = new HashMap<>();
    private static final Map<String, String> jump = Map.of(
            "JGT", "001",
            "JEQ", "010",
            "JGE", "011",
            "JLT", "100",
            "JNE", "101",
            "JLE", "110",
            "JMP", "111");

    static {
        comp.put("0", "1110101010");
        comp.put("1", "1110111111");
        comp.put("-1", "1110111010");
        comp.put("D", "1110001100");
        comp.put("A", "1110110000");
        comp.put("!D", "1110001101");
        comp.put("!A", "1110110001");
        comp.put("-D", "1110001111");
        comp.put("-A", "1110110011");
        comp.put("D+1", "1110011111");
        comp.put("A+1", "1110110111");
        comp.put("D-1", "1110001110");
        comp.put("A-1", "1110110010");
        comp.put("D+A", "1110000010");
        comp.put("D-A", "1110010011");
        comp.put("A-D", "1110000111");
        comp.put("D&A", "1110000000");
        comp.put("D|A", "1110010101");
        comp.put("M", "1111110000");
        comp.put("!M", "1111110001");
        comp.put("-M", "1111110011");
        comp.put("M+1", "1111110111");
        comp.put("M-1", "1111110010");
        comp.put("D+M", "1111000010");
        comp.put("D-M", "1111010011");
        comp.put("M-D", "1111000111");
        comp.put("D&M", "1111000000");
        comp.put("D|M", "1111010101");
    }

    public static String symbol(String asm) {
        int base2 = Integer.parseInt(asm);
        return String.format("%1$16s", Integer.toBinaryString(base2)).replace(' ', '0');
    }

    // should translate destination to 3 bits
    public static String dest(String asm) {
        return dest.getOrDefault(asm, "000");
    }

    // should translate computation to 7 bits
    public static String comp(String asm) {
        return Optional.ofNullable(comp.get(asm))
                .orElseThrow(() -> new IllegalArgumentException("No comp component found in C Command"));
    }

    // should translate jump to 3 bits
    public static String jump(String asm) {
        return jump.getOrDefault(asm, "000");
    }

}

