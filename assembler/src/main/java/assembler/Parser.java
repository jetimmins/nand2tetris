package assembler;

import assembler.command.Command;

public interface Parser extends AutoCloseable {
    boolean hasMoreCommands();

    int getCurrentAddress();

    void advance();
    Command command();
}
