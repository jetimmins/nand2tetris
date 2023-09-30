package vmtranslator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static vmtranslator.TestFiles.*;

public class CodeWriterTest {

    private CodeWriter underTest;

    @AfterEach
    void cleanUp() {
        try {
            underTest.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void shouldBootstrap() {
        underTest = new CodeWriter(BOOTSTRAP.asPath());
    }

    @Test
    void shouldWritePushConstant() throws IOException {
        underTest = new CodeWriter(CONSTANT.asPath());
        underTest.writePushPop(CommandType.PUSH, VRegister.CONSTANT, 42);
    }

    @Test
    void shouldWriteArithmetic() throws IOException {
        underTest = new CodeWriter(BINARY_ARITHMETIC.asPath());
        underTest.writePushPop(CommandType.PUSH, VRegister.CONSTANT, 2);
        underTest.writePushPop(CommandType.PUSH, VRegister.CONSTANT, 4);
        underTest.writeArithmetic(CommandType.Arithmetic.ADD);
    }

    @Test
    void shouldWriteComparisons() throws IOException {
        underTest = new CodeWriter(COMPARISONS.asPath());
        underTest.writePushPop(CommandType.PUSH, VRegister.CONSTANT, 2);
        underTest.writePushPop(CommandType.PUSH, VRegister.CONSTANT, 4);
        underTest.writeArithmetic(CommandType.Arithmetic.ADD);
        underTest.writePushPop(CommandType.PUSH, VRegister.CONSTANT, 4);
        underTest.writeArithmetic(CommandType.Arithmetic.GT);
        underTest.writePushPop(CommandType.PUSH, VRegister.CONSTANT, 4);
        underTest.writePushPop(CommandType.PUSH, VRegister.CONSTANT, 2);
        underTest.writeArithmetic(CommandType.Arithmetic.LT);
        underTest.writeArithmetic(CommandType.Arithmetic.ADD);
    }
}
