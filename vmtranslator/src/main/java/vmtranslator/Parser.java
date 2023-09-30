package vmtranslator;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Scanner;

import static java.lang.String.*;
import static vmtranslator.CommandType.*;
import static vmtranslator.CommandType.Arithmetic.*;

public class Parser implements AutoCloseable {

    private final Scanner scanner;
    private String currentLine;

    public Parser(Path path) {
        try {
            scanner = new Scanner(path.toFile());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean hasMoreCommands() {
        return scanner.hasNext();
    }

    public void advance() {
        if (!hasMoreCommands()) {
            throw new RuntimeException("Tried to advance when no more commands remain");
        }

        currentLine = scanner.nextLine();
        trimCommentsAndWhitespace();
        if (currentLine.isBlank() && hasMoreCommands()) {
            advance();
        }
    }

    private void trimCommentsAndWhitespace() {
        if (currentLine.contains("//")) {
            String[] parts = currentLine.split("//");
            currentLine = parts.length > 0 ? parts[0] : "";
        }

        currentLine = currentLine.trim();
    }

    public CommandType getCommandType() {
        if (currentLine.startsWith("push")) {
            return PUSH;
        } else if (currentLine.startsWith("pop")) {
            return POP;
        } else if (isArithmetic(currentLine)) {
            return ARITHMETIC;
        } else if (currentLine.startsWith("label")) {
            return LABEL;
        } else if (currentLine.startsWith("goto")) {
            return GOTO;
        } else if (currentLine.startsWith("if-goto")) {
            return IF;
        } else if (currentLine.startsWith("function")) {
            return FUNCTION;
        } else if (currentLine.startsWith("call")) {
            return CALL;
        } else if (currentLine.startsWith("return")) {
            return RETURN;
        } else {
            throw new IllegalArgumentException("Invalid command: " + currentLine);
        }
    }

    public String arg1() {
        if (getCommandType().numArgs() == 0) {
            throw new RuntimeException(format("Tried to get single arg for command type %s which has 0 args",
                    getCommandType()));
        }

        if (getCommandType() == ARITHMETIC) {
            return currentLine.toUpperCase();
        }

        return currentLine.split(" ")[1];
    }

    public String arg2() {
        if (getCommandType().numArgs() != 2) {
            throw new RuntimeException(format("Tried to get 2nd arg of command type %s which has %s arg%s",
                    getCommandType(), getCommandType().numArgs(), getCommandType().numArgs() == 0 ? "s" : ""));
        }

        return currentLine.split(" ")[2];
    }

    @Override
    public void close() {
        scanner.close();
    }
}
