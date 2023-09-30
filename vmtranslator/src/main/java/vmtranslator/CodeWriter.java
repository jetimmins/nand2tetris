package vmtranslator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static vmtranslator.Asm.*;
import static vmtranslator.Asm.Register.*;
import static vmtranslator.Asm.Symbol.*;
import static vmtranslator.CommandType.Arithmetic;
import static vmtranslator.CommandType.Arithmetic.*;
import static vmtranslator.VRegister.THAT;
import static vmtranslator.VRegister.THIS;
import static vmtranslator.VRegister.*;

public class CodeWriter implements AutoCloseable {
    private final BufferedWriter writer;
    private String sourceFilename;

    // used and incremented when writing labels that require uniqueness
    private int uid = 0;

    public CodeWriter(Path target) {
        try {
            writer = new BufferedWriter(new FileWriter(target.toFile()));
        } catch (IOException e) {
            throw new RuntimeException("Failed creating write for target file", e);
        }
    }

    public void setSourceFilename(Path source) {
        this.sourceFilename = source.getFileName().toString();
    }

    public void writeBootstrap() {
        write(a(256));
        write(c(D, A));
        write(a(SP));
        write(c(M, D));
        writeCall("Sys.init", 0);
    }

    public void writeCommand(CommandType type, String arg) {
        switch (type) {
            case ARITHMETIC -> writeArithmetic(Arithmetic.valueOf(arg));
            case LABEL -> write(label(arg));
            case GOTO -> writeGoto(arg);
            case IF -> writeIf(arg);
            default -> throw new RuntimeException(format("Tried to call '%s' as a single arg command", type));
        }
    }

    public void writeCommand(CommandType type, String one, String two) {
        switch (type) {
            case PUSH, POP -> writePushPop(type, byVmCode(one), parseInt(two));
            case CALL -> writeCall(one, parseInt(two));
            case FUNCTION -> writeFunction(one, parseInt(two));
            default -> throw new RuntimeException(format("Tried to call '%s' as a two arg command", type));
        }
    }

    public void writeArithmetic(Arithmetic command) {
        switch (command) {
            case ADD -> binary(ADD);
            case SUB -> binary(SUB);
            case AND -> binary(AND);
            case OR -> binary(OR);
            case NEG -> unary(NEG);
            case NOT -> unary(NOT);
            case EQ -> compare(Jump.valueOf(EQ.asm()));
            case GT -> compare(Jump.valueOf(GT.asm()));
            case LT -> compare(Jump.valueOf(LT.asm()));
        }
    }

    public void writePushPop(CommandType type, VRegister vreg, int index) {
        switch (type) {
            case PUSH -> writePush(vreg, index);
            case POP -> writePop(vreg, index);
        }
    }

    private void writePop(VRegister vreg, int index) {
        switch (vreg) {
            case LOCAL, THAT, THIS, ARGUMENT, POINTER, TEMP -> pop(vreg, index);
            case STATIC -> popStatic(index);
            case CONSTANT -> throw new IllegalArgumentException("Cannot pop constant");
        }
    }

    private void writePush(VRegister vreg, int index) {
        switch (vreg) {
            case CONSTANT -> pushConstant(index);
            case LOCAL, THIS, THAT, ARGUMENT, POINTER, TEMP -> push(vreg, index);
            case STATIC -> pushStatic(index);
        }
    }

    public void writeGoto(String label) {
        write(a(label));
        write(jmp());
    }

    public void writeIf(String label) {
        popToD();
        write(a(label));
        write(c(D, Jump.JNE));
    }

    public void writeCall(String functionName, int numArgs) {
        String returnLabel = generateLabel("RET_" + functionName, uid++);
        // Push return address onto stack
        write(a(returnLabel));
        write(c(D, A));
        pushFromD();
        // Push current frame onto stack
        pushFromD(LOCAL);
        pushFromD(ARGUMENT);
        pushFromD(THIS);
        pushFromD(THAT);
        // Store SP in D, subtract number of function args
        addressToRegister(SP, D);
        write(a(numArgs));
        write(c(D, "D-A"));
        // Subtract a further 5
        write(a(5));
        write(c(D, "D-A"));
        // Write calculation into ARG
        dToAddress(ARG);
        // Write previous SP into LCL
        addressToAddress(SP, LCL);
        // Jump to function
        write(a(functionName));
        write(jmp());
        write(label(returnLabel));
    }

    public void writeReturn() {
        // Store LCL
        addressToAddress(LCL, R13);
        // Load return address and store it
        write(a(5));
        write(c(A, "D-A"));
        write(c(D, M));
        dToAddress(R14);
        // Write popped value to ARG
        popToD();
        addressToRegister(ARG, A);
        write(c(M, D));
        // Write new SP
        write(c(D, "A+1"));
        dToAddress(SP);
        // Load previous frame registers by loading stored LCL, decrementing and writing to each register
        loadPreviousFrameVreg(THAT);
        loadPreviousFrameVreg(THIS);
        loadPreviousFrameVreg(ARGUMENT);
        loadPreviousFrameVreg(LOCAL);
        // Jump to return address
        addressToRegister(R14, A);
        write(jmp());
    }

    public void writeFunction(String functionName, int numArgs) {
        write(label(functionName));
        for (int i = numArgs; i > 0; i--) {
            writePush(CONSTANT, 0);
        }
    }

    private void peek() {
        write(a(SP));
        write(c(A, "M-1"));
    }

    private void addressToRegister(Symbol symbolicAddress, Register dest) {
        write(a(symbolicAddress));
        write(c(dest, M));
    }

    private void dToAddress(Symbol symbolicAddress) {
        write(a(symbolicAddress));
        write(c(M, D));
    }

    private void addressToAddress(Symbol from, Symbol to) {
        addressToRegister(from, D);
        dToAddress(to);
    }

    private void pushFromD() {
        write(a(SP));
        write(c(A, M));
        write(c(M, D));
        write(a(SP));
        write(c(M, "M+1"));
    }

    private void pushFromD(VRegister from) {
        addressToRegister(from.getAssemblyCode(), D);
        pushFromD();
    }

    private void popToD() {
        write(a(SP));
        write(c(M, "M-1"));
        write(c(A, M));
        write(c(D, M));
    }

    private void popToDThenPeek() {
        popToD();
        write(c(A, "A-1"));
    }

    private void unary(Arithmetic op) {
        peek();
        write(c(M, op.asm() + M));
    }

    private void binary(Arithmetic op) {
        popToDThenPeek();
        write(c(M, M + op.asm() + D));
    }

    private void compare(Jump jump) {
        String trueLabel = generateLabel("TRUE", uid++);
        String doneLabel = generateLabel("DONE", uid++);
        popToDThenPeek();
        write(c(D, "M-D"));
        write(a(trueLabel));
        write(c(D, jump));
        write(a(SP));
        write(c(A, "M-1"));
        write(c(M, 0));
        write(a(doneLabel));
        write(jmp());
        write(label(trueLabel));
        write(a(SP));
        write(c(A, "M-1"));
        write(c(M, -1));
        write(label(doneLabel));
    }

    private void pop(VRegister vreg, int index) {
        boolean ptr = isPointer(vreg);
        String loadMemIfNotPtr = ptr ? "" : c(A, M);
        Register handlePtr = ptr ? A : M;

        if (index == 0) {
            popToD();
            write(a(vreg));
            write(loadMemIfNotPtr);
            write(c(M, D));
        } else {
            storeIndexedVRegisterAddress(vreg, index, handlePtr);
            popToD();
            writeToStoredAddress();
        }
    }

    private void popStatic(int index) {
        popToD();
        write(a(sourceFilename, index));
        write(c(M, D));
    }

    private void push(VRegister vreg, int index) {
        boolean ptr = isPointer(vreg);
        String loadMemIfNotPtr = ptr ? "" : c(A, M);
        Register handlePtr = ptr ? A : M;

        if (index == 0) {
            write(a(vreg));
            write(loadMemIfNotPtr);
            write(c(D, M));
            pushFromD();
        } else {
            write(a(vreg));
            write(c(D, handlePtr));
            write(a(index));
            write(c(A, "A+D"));
            write(c(D, M));
            pushFromD();
        }
    }

    private void pushConstant(int index) {
        write(a(index));
        write(c(D, A));
        pushFromD();
    }

    private void pushStatic(int index) {
        write(a(sourceFilename, index));
        write(c(D, M));
        pushFromD();
    }

    private void storeIndexedVRegisterAddress(VRegister vreg, int index, Register handlePtr) {
        write(a(vreg));
        write(c(D, handlePtr));
        write(a(index));
        write(c(D, "D+A"));
        write(a(R13));
        write(c(M, D));
    }

    private void writeToStoredAddress() {
        addressToRegister(R13, A);
        write(c(M, D));
    }

    private void loadPreviousFrameVreg(VRegister vreg) {
        write(a(R13));
        write(c("AM", "M-1"));
        write(c(D, M));
        write(a(vreg));
        write(c(M, D));
    }

    private void write(String asm) {
        try {
            writer.write(asm);
        } catch (IOException e) {
            throw new RuntimeException("Error writing output to file", e);
        }
    }

    @Override
    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing writer", e);
        }
    }
}
