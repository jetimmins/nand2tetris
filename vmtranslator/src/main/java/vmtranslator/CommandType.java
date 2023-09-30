package vmtranslator;

import java.util.Arrays;

import static vmtranslator.Asm.Jump.*;

public enum CommandType {
    ARITHMETIC(1),
    PUSH(2),
    POP(2),
    LABEL(1),
    GOTO(1),
    IF(1),
    FUNCTION(2),
    RETURN(0),
    CALL(2);

    enum Arithmetic {
        ADD("+"),
        SUB("-"),
        NEG("-"),
        EQ(JEQ.toString()),
        GT(JGT.toString()),
        LT(JLT.toString()),
        AND("&"),
        OR("|"),
        NOT("!");

        private final String asm;

        public String asm() {
            return this.asm;
        }

        Arithmetic(String asm) {
            this.asm = asm;
        }

        public static boolean isArithmetic(String command) {
            return Arrays.stream(values()).anyMatch(m -> m.toString().equals(command.toUpperCase()));
        }
    }

    private final int numArgs;

    public int numArgs() {
        return this.numArgs;
    }

    CommandType(int numArgs) {
        this.numArgs = numArgs;
    }
}
