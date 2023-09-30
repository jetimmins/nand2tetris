package assembler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Writer implements AutoCloseable {
    private final BufferedWriter writer;

    public Writer(String path) {
        try {
            writer = new BufferedWriter(new FileWriter(path.replace(".asm", ".hack")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeLine(String line) {
        try {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        writer.close();
    }
}
