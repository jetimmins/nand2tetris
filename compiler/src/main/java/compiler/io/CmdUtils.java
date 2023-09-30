package compiler.io;

import compiler.Format;

import java.nio.file.Path;
import java.util.Optional;

import static compiler.Format.*;
import static compiler.io.FileUtils.setUpTarget;

public class CmdUtils {
    private static final String HELP = """
            Compiles a provided .jack file or directory containing .jack files.
            Emits compiled VM code to '[path]/target' directory as default.
            Usage: [path] [options]
            path:    .jack file or dir containing .jack files.
            options:
                     --tokenise: Outputs result of tokenised code instead of compiled code.
                     --xml:      Outputs in XML format instead of VM format.
                     --target:   An output directory for the compiled files instead of [path]/target.
            """;

    public static Optional<Args> parseArgs(String[] args) {
        if (args.length == 0 || args.length > 5) {
            return abort("Incorrect number of args.");
        }

        Path src = Path.of(args[0]);
        Format format = VM;
        String target = "";

        for (int i = 1; i < args.length; i++) {
            switch (args[i].trim()) {
                case "--tokenise" -> format = TOKENS;
                case "--xml" -> format = XML;
                case "--target" -> target = args[++i];
                case "" -> {
                }
                default -> {
                    return abort("Argument " + args[i] + " not recognised.");
                }
            }
        }

        return Optional.of(new Args(src, format, setUpTarget(src, target)));
    }

    private static Optional<Args> abort(String msg) {
        System.out.println(msg);
        System.out.println(HELP);
        return Optional.empty();
    }

    public record Args(Path src, Format format, Path target) {
    }
}
