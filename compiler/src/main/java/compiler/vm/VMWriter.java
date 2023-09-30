package compiler.vm;

import compiler.Format;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static compiler.io.FileUtils.resolveTarget;

public class VMWriter implements AutoCloseable {
    private final BufferedWriter writer;

    public VMWriter(Path src, Path targetDir) {
        this(resolveTarget(src, targetDir, Format.VM));
    }

    public VMWriter(File target) {
        try {
            this.writer = new BufferedWriter(new FileWriter(target));
        } catch (IOException e) {
            throw new RuntimeException("Error creating writer for target '%s'".formatted(target), e);
        }
    }

    void writePush(Segment segment, int index) {
        write("push %s %s".formatted(segment.toString().toLowerCase(), index));
    }

    void writePop(Segment segment, int index) {
        write("pop %s %s".formatted(segment.toString().toLowerCase(), index));
    }

    void writeArithmetic(Command command) {
        if (Command.isSoftware(command)) {
            writeCall("Math", command.toString().toLowerCase(), 2);
        } else {
            write(command.toString().toLowerCase());
        }
    }

    void writeLabel(String label) {
        write("label %s".formatted(label));
    }

    void writeGoto(String label) {
        write("goto %s".formatted(label));
    }

    void writeIf(String label) {
        write("if-goto %s".formatted(label));
    }

    void writeCall(String className, String name, int numArgs) {
        write("call %s.%s %s".formatted(className, name, numArgs));
    }

    void writeFunction(String className, String name, int numLocals) {
        write("function %s.%s %s".formatted(className, name, numLocals));
    }

    void writeReturn() {
        write("return");
    }

    void writeStringConstant(String value) {
        writePush(Segment.CONSTANT, value.length());
        writeCall("String", "new", 1);
        for (char c : value.toCharArray()) {
            writePush(Segment.CONSTANT, c);
            writeCall("String", "appendChar", 2);
        }
    }

    private void write(String s) {
        try {
            writer.write(s);
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Error writing string '%s'".formatted(s), e);
        }
    }

    @Override
    public void close() throws Exception {
        writer.close();
    }
}
