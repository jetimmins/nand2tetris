package vmtranslator;

import java.nio.file.Path;

import static vmtranslator.FileUtils.*;

public class VMTranslator {
    private final Path source;
    private final boolean bootstrap;

    public VMTranslator(Path source, boolean bootstrap) {
        this.source = source;
        this.bootstrap = bootstrap;
    }

    public void translate() {
        try (CodeWriter codeWriter = new CodeWriter(resolveAsmPath(source))) {
            if (isVmFile(source)) {
                compileOne(source, codeWriter);
            } else {
                vmDirFilepaths(source).forEach(p -> compileOne(p, codeWriter));
            }
        }
    }

    private void compileOne(Path path, CodeWriter codeWriter) {
        try (Parser parser = new Parser(path)) {
            codeWriter.setSourceFilename(path);
            if (bootstrap) {
                codeWriter.writeBootstrap();
            }
            while (parser.hasMoreCommands()) {
                parser.advance();
                CommandType type = parser.getCommandType();
                switch (type.numArgs()) {
                    case 0 -> codeWriter.writeReturn();
                    case 1 -> codeWriter.writeCommand(type, parser.arg1());
                    case 2 -> codeWriter.writeCommand(type, parser.arg1(), parser.arg2());
                }
            }
        }
    }
}
