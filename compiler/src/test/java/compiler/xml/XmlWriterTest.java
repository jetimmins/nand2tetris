package compiler.xml;

import compiler.Format;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static compiler.TestFiles.TARGET_DIR;

public class XmlWriterTest {

    private XmlWriter underTest;
    private static final Path TARGET = TARGET_DIR.resolve("xml");

    @AfterEach
    void tearDown() {
        underTest.close();
    }

    @Test
    void shouldConstruct() {
        underTest = new XmlWriter(TARGET.resolve("empty.xml").toFile(), Format.XML);
    }

    @Test
    void shouldWriteElement() {
        underTest = new XmlWriter(TARGET.resolve("element.xml").toFile(), Format.XML);
        underTest.writeElement("intVal", "42");
    }

    @Test
    void shouldWriteNested() {
        underTest = new XmlWriter(TARGET.resolve("nested.xml").toFile(), Format.XML);
        underTest.openParent("class");
        underTest.writeElement("intVal", "42");
        underTest.closeParent();
    }

    @Test
    void shouldWriteTokens() {
        underTest = new XmlWriter(TARGET.resolve("tokens.xml").toFile(), Format.TOKENS);
        underTest.writeElement("intVal", "42");
    }
}
