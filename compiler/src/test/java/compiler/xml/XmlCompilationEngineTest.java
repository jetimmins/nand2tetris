package compiler.xml;

import compiler.CompilationEngine;
import compiler.CompilationEngineTest;

import java.nio.file.Path;

import static compiler.TestFiles.TARGET_DIR;

public class XmlCompilationEngineTest extends CompilationEngineTest {
    public CompilationEngine getEngine(Path src) {
        return new XmlCompilationEngine(src, TARGET_DIR.resolve("xml"));
    }

    @Override
    public Path resolveTarget(Path src) {
        return src.getParent().getParent()
                .resolve("xml")
                .resolve(src.getFileName().toString().replace(".jack", ".xml"));
    }
}
