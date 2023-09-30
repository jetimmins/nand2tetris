package compiler.vm;

import org.junit.jupiter.api.Test;

import static compiler.vm.Kind.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class SymbolTableTest {

    private final SymbolTable underTest = new SymbolTable();

    @Test
    void shouldVarCount() {
        underTest.define("one", "type", ARG);
        underTest.define("two", "type", ARG);
        underTest.define("three", "type", VAR);

        assertThat(underTest.varCount(ARG)).isEqualTo(2);
        assertThat(underTest.varCount(VAR)).isEqualTo(1);
    }

    @Test
    void shouldClearSubroutineScopeOnStart() {
        underTest.define("one", "type", ARG);
        underTest.define("two", "type", VAR);
        underTest.startSubroutine();

        assertThat(underTest.varCount(ARG)).isEqualTo(0);
        assertThat(underTest.varCount(VAR)).isEqualTo(0);
    }

    @Test
    void shouldGetType() {
        underTest.define("one", "type", ARG);

        assertThat(underTest.typeOf("one")).isEqualTo("type");
    }

    @Test
    void shouldGetKind() {
        underTest.define("one", "type", ARG);

        assertThat(underTest.kindOf("one")).isEqualTo(ARG);
    }

    @Test
    void shouldGetIndex() {
        underTest.define("one", "type", ARG);
        underTest.define("two", "type", ARG);

        assertThat(underTest.indexOf("one")).isEqualTo(0);
        assertThat(underTest.indexOf("two")).isEqualTo(1);
    }

    @Test
    void shouldThrowOnDuplicateNameInClassScope() {
        underTest.define("one", "type", FIELD);
        assertThatThrownBy(() -> underTest.define("one", "type", STATIC))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Identifier 'one' already defined in class scope");
    }

    @Test
    void shouldThrowOnDuplicateNameInSubroutineScope() {
        underTest.define("one", "type", ARG);
        assertThatThrownBy(() -> underTest.define("one", "type", VAR))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Identifier 'one' already defined in subroutine scope");
    }

    @Test
    void shouldAllowDuplicateNamesAcrossScopes() {
        underTest.define("one", "type", ARG);
        underTest.define("one", "type", STATIC);
    }
}
