package compiler.vm;

import compiler.CompilationEngine;
import compiler.CompilationEngineTest;

import java.nio.file.Path;

import static compiler.TestFiles.TARGET_DIR;

public class VMCompilationEngineTest extends CompilationEngineTest {

    public CompilationEngine getEngine(Path src) {
        return new VMCompilationEngine(src, TARGET_DIR.resolve("vm"));
    }

    @Override
    public Path resolveTarget(Path src) {
        return src.getParent().getParent()
                .resolve("vm")
                .resolve(src.getFileName().toString().replace(".jack", ".vm"));
    }
}
