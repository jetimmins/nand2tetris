package compiler;

import compiler.vm.VMCompilationEngine;
import compiler.xml.XmlCompilationEngine;
import compiler.xml.XmlWriter;

import java.nio.file.Path;

import static compiler.TokenType.*;
import static compiler.io.FileUtils.isJackFile;
import static compiler.io.FileUtils.jackFilepaths;

public class JackAnalyser {

    public static void tokenise(Path src, Path targetDir) {
        if (isJackFile(src)) {
            tokeniseOne(src, targetDir);
        } else {
            jackFilepaths(src).forEach(jack -> tokeniseOne(jack, targetDir));
        }
    }

    public static void compile(Path src, Path targetDir, Format format) {
        if (isJackFile(src)) {
            compileOne(src, targetDir, format);
        } else {
            jackFilepaths(src).forEach(jack -> compileOne(jack, targetDir, format));
        }
    }

    private static void tokeniseOne(Path src, Path targetDir) {
        try (JackTokeniser tokeniser = new JackTokeniser(src);
             XmlWriter writer = new XmlWriter(src, targetDir, Format.TOKENS)) {
            while (tokeniser.hasMoreTokens()) {
                tokeniser.advance();
                Token token = tokeniser.token();
                if (token.type().equals(ERROR)) {
                    throw new RuntimeException("Failed to tokenise pattern '%s'".formatted(token.value()));
                }
                writer.writeElement(token.type().camel(), token.value());
            }
        }
    }

    private static void compileOne(Path src, Path targetDir, Format format) {
        CompilationEngine engine = switch (format) {
            case XML -> new XmlCompilationEngine(src, targetDir);
            case VM -> new VMCompilationEngine(src, targetDir);
            case TOKENS -> throw new RuntimeException("Attempted to use compilation engine for tokenisation");
        };

        engine.compileClass();
        engine.close();
    }
}

