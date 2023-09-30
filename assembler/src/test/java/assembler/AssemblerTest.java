package assembler;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static assembler.TestFiles.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AssemblerTest {

    @Test
    void shouldAssemble() {
        Assembler underTest = new Assembler(PROGRAM.filename());

        underTest.assemble();
    }

    @Test
    void shouldMatchVerifiedAssembler() throws IOException {
        Assembler underTest = new Assembler(PROGRAM.filename());

        underTest.assemble();
        long result = Files.mismatch(Path.of(VERIFIED_CMP.filename()), Path.of(PROGRAM_CMP.filename()));

        assertThat(result).isEqualTo(-1L);
    }
}
