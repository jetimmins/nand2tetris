package assembler;

import assembler.command.Command;
import assembler.command.CommandFactory;

public class Assembler {

    private final String path;

    public Assembler(String path) {
        this.path = path;
    }

    public void assemble() {
        SymbolTable symbolTable = populateNewSymbolTable();
        CommandFactory commandFactory = new CommandFactory(symbolTable::getAddress);

        try (Parser parser = new AsmParser(path, commandFactory);
             Writer writer = new Writer(path)) {

            while (parser.hasMoreCommands()) {
                parser.advance();
                Command command = parser.command();
                command.asBinary().ifPresent(writer::writeLine);
                if (command.isEmpty()) {
                    throw new IllegalArgumentException(
                            "Invalid command, %s not found as a label and is not a valid unsigned integer".formatted(command.asAsm()));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error creating parser", e);
        }
    }

    private SymbolTable populateNewSymbolTable() {
        SymbolTable symbolTable = new SymbolTable();

        try (Parser parser = new AsmParser(path, new CommandFactory(symbolTable::getAddress))) {
            while (parser.hasMoreCommands()) {
                parser.advance();
                parser.command().asLabel().ifPresent(label ->
                        symbolTable.addEntry(label, parser.getCurrentAddress()));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error creating parser", e);
        }

        return symbolTable;
    }
}
