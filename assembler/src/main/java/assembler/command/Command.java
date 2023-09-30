package assembler.command;

import java.util.Optional;

// This abstraction isn't great as 'Labels' aren't really 'Commands'
// The parser could return an Either<Label, Command> and handle each case
// But it's out of scope here to model an Either type for Java
// It's also not neat to enforce an invariant that either binary or label must be present.
public interface Command {
    Optional<String> asBinary();

    // Slightly redundant here but nice for logging etc.
    String asAsm();
    
    default Optional<String> asLabel() {
        return Optional.empty();
    }

    default boolean isEmpty() {
        return asBinary().isEmpty() && asLabel().isEmpty();
    }
}
