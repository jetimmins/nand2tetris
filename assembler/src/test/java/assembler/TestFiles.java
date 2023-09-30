package assembler;

public enum TestFiles {
    INSTRUCTIONS("src/test/resources/instructions.asm"),
    SYMBOLS_COMMENTS_WHITESPACE("src/test/resources/comments-labels-whitespace.asm"),
    PROGRAM("src/test/resources/program.asm"),
    PROGRAM_CMP("src/test/resources/program.hack"),
    VERIFIED_CMP("src/test/resources/cmp.hack");

    private final String filename;

    private TestFiles(String filename) {
        this.filename = filename;
    }

    public String filename() {
        return this.filename;
    }
}
