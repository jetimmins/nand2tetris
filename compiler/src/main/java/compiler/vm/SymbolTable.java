package compiler.vm;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static compiler.Keyword.THIS;
import static compiler.vm.Kind.*;

public class SymbolTable {

    private static final SymbolContext NONE_CTX = new SymbolContext("", NONE, 0);
    private final Map<String, SymbolContext> classTable = new HashMap<>();
    private final Map<String, SymbolContext> subroutineTable = new HashMap<>();

    public void startSubroutine() {
        subroutineTable.clear();
    }

    public void startMethod() {
        define(THIS.toString(), THIS.toString(), ARG);
    }

    public void define(String name, String type, Kind kind) {
        if (kind.equals(NONE)) {
            throw new IllegalArgumentException("Cannot register identifier with kind NONE");
        }

        int kindNumber = varCount(kind);
        SymbolContext context = new SymbolContext(type, kind, kindNumber);

        // handle same name as var and arg and same for class kinds
        if (kind.equals(VAR) || kind.equals(ARG)) {
            subroutineTable.merge(name, context, (old, nw) ->
            { throw new IllegalStateException("Identifier '%s' already defined in subroutine scope".formatted(name)); });
        } else {
            classTable.merge(name, context, (old, nw) ->
            { throw new IllegalStateException("Identifier '%s' already defined in class scope".formatted(name)); });
        }
    }

    public int varCount(Kind kind) {
        Map<String, SymbolContext> currentScope = kind.equals(VAR) || kind.equals(ARG) ? subroutineTable : classTable;
        return Math.toIntExact(currentScope.values().stream()
                .map(SymbolContext::kind)
                .filter(k -> k.equals(kind))
                .count());
    }

    public Kind kindOf(String name) {
        return subroutineTable.getOrDefault(name, classTable.getOrDefault(name, NONE_CTX)).kind();
    }

    public String typeOf(String name) {
        return subroutineTable.getOrDefault(name, classTable.getOrDefault(name, NONE_CTX)).type();
    }

    public int indexOf(String name) {
        return subroutineTable.getOrDefault(name, classTable.getOrDefault(name, NONE_CTX)).number();
    }
}
