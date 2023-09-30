package vmtranslator;

import java.nio.file.Path;

public enum TestFiles {
    COMMANDS("src/test/resources/commands.vm"),
    PROGRAM("src/test/resources/behaviour.vm"),
    PROGRAM_CMP("src/test/resources/behaviour.asm"),
    BOOTSTRAP("src/test/resources/bootstrap.asm"),
    CONSTANT("src/test/resources/constant.asm"),
    BINARY_ARITHMETIC("src/test/resources/arithmetic.asm"),
    COMPARISONS("src/test/resources/comparisons.asm"),
    /**
     * This translator's latest stable output of the {@link TestFiles#PROGRAM_CMP} file.
     * The test {@link VMTranslatorTest#outputAsmMatchesPrevious()} can be run to ensure no changes in the code
     * cause changes in the output.
     * If the output behaviour is intended to be changed, the translator should first be retested against the verified
     * VMEmulator to capture any regressions, then this file should be updated with the new output for future tests.
     */
    VERIFIED_CMP("src/test/resources/cmp.asm");
    private final String filename;

    TestFiles(String filename) {
        this.filename = filename;
    }

    public Path asPath() {
        return Path.of(this.filename);
    }
}
