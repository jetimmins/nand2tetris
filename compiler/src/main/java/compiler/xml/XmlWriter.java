package compiler.xml;

import compiler.Format;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.IntStream;

import static compiler.Format.*;
import static compiler.io.FileUtils.resolveTarget;

public class XmlWriter implements AutoCloseable {

    private static final String INDENT = "  ";
    private static final String TAG_OPEN = "<%s>";
    private static final String TAG_CLOSE = "</%s>";
    private final Format format;
    private final BufferedWriter writer;
    private final Deque<String> stack = new ArrayDeque<>();

    public XmlWriter(Path src, Path targetDir, Format format) {
        this(resolveTarget(src, targetDir, format), format);
    }

    public XmlWriter(File target, Format format) {
        try {
            this.writer = new BufferedWriter(new FileWriter(target));
        } catch (IOException e) {
            throw new RuntimeException("Error creating writer for target '%s'".formatted(target), e);
        }

        switch (format) {
            case TOKENS -> openParent("tokens");
            case VM -> throw new RuntimeException("Tried to write VM file with XML Writer");
        }

        this.format = format;
    }

    public void openParent(String elementName) {
        indent();
        openTag(elementName);
    }

    // handle empty stack
    public void closeParent() {
        String parent = stack.pop();
        indent();
        write(TAG_CLOSE.formatted(parent));
        newLine();
    }

    public void writeElement(String elementName, String value) {
        indent();
        writeFullTag(elementName, value);
    }

    private void openTag(String elementName) {
        write(TAG_OPEN.formatted(elementName));
        newLine();
        stack.push(elementName);
    }

    private void writeFullTag(String elementName, String value) {
        write(TAG_OPEN.formatted(elementName));
        write(value);
        write(TAG_CLOSE.formatted(elementName));
        newLine();
    }

    private void indent() {
        IntStream.range(0, stack.size()).forEach(i -> write(INDENT));
    }

    private void write(String s) {
        try {
            writer.write(s);
        } catch (IOException e) {
            throw new RuntimeException("Error writing string '%s'".formatted(s), e);
        }
    }

    private void newLine() {
        try {
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Error writing new line", e);
        }
    }

    @Override
    public void close() {
        try {
            if (format.equals(TOKENS)) {
                closeParent();
            }
            writer.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
