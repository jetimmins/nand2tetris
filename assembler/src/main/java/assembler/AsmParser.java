package assembler;

import assembler.command.Command;
import assembler.command.CommandFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class AsmParser implements Parser {

    private final Scanner scanner;
    private final CommandFactory commandFactory;
    private Command currentCommand;
    private int currentAddress = 0;


    public AsmParser(String path, CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
        try {
            this.scanner = new Scanner(new File(path));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean hasMoreCommands() {
        return scanner.hasNext();
    }

    public int getCurrentAddress() {
        return currentAddress;
    }

    public void advance() {
        if (hasMoreCommands()) {
            String currentLine = scanner.nextLine().trim();
            if (shouldSkip(currentLine)) {
                advance();
            } else {
                currentCommand = commandFactory.command(currentLine);

                if (shouldIncrement()) {
                    currentAddress++;
                }
            }
        } else {
            throw new RuntimeException("Advanced with no more remaining commands");
        }
    }

    private boolean shouldIncrement() {
        return currentCommand.asLabel().isEmpty();
    }

    private boolean shouldSkip(String currentLine) {
        return (currentLine.startsWith("//") || currentLine.isBlank()) && hasMoreCommands();
    }

    public Command command() {
        return currentCommand;
    }

    @Override
    public void close() {
        scanner.close();
    }
}
