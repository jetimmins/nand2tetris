package vmtranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static vmtranslator.TestFiles.*;

public class VMTranslatorTest {

    @BeforeEach
    void setUp() throws IOException {
        Files.deleteIfExists(PROGRAM_CMP.asPath());
    }

    @Test
    void outputAsmMatchesPrevious() throws IOException {
        VMTranslator underTest = new VMTranslator(PROGRAM.asPath(), true);
        underTest.translate();
        long result = Files.mismatch(PROGRAM_CMP.asPath(), VERIFIED_CMP.asPath());

        assertThat(result).isEqualTo(-1L);
    }
}
