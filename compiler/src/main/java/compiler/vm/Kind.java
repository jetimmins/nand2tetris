package compiler.vm;

import java.util.Arrays;

public enum Kind {
    STATIC(Segment.STATIC),
    FIELD(Segment.THIS),
    ARG(Segment.ARGUMENT),
    VAR(Segment.LOCAL),
    NONE(null);

    public static Kind fromJack(String jack) {
        return Arrays.stream(Kind.values())
                .filter(k -> k.toString().toLowerCase().equals(jack))
                .findFirst()
                .orElse(NONE);
    }

    private final Segment segment;

    public Segment segment() {
        return segment;
    }

    Kind(Segment segment) {
        this.segment = segment;
    }
}
