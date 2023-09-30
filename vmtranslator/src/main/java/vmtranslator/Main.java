package vmtranslator;

import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0 || args.length > 2) {
            System.out.println("""
                    Usage: [path] [options]
                    path:    .vm file or dir containing .vm files.
                    options:
                             --no-bootstrap: do not output bootstrap asm
                    """);
            return;
        }

        Path source = Path.of(args[0]);
        boolean bootstrap = !(args.length == 2 && args[1].equals("--no-bootstrap"));
        VMTranslator translator = new VMTranslator(source, bootstrap);
        translator.translate();
    }
}