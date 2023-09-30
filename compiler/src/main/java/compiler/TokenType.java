package compiler;

public enum TokenType {
    KEYWORD("keyword"),
    SYMBOL("symbol"),
    INTEGER_CONSTANT("integerConstant"),
    STRING_CONSTANT("stringConstant"),
    IDENTIFIER("identifier"),
    ERROR("error");

    private final String camel;

    public String camel() {
        return camel;
    }

    TokenType(String camel) {
        this.camel = camel;
    }
}
