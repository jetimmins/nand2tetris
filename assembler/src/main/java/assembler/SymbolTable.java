package assembler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SymbolTable {

    private final Map<String, Integer> addressesBySymbol;

    public SymbolTable() {
        addressesBySymbol = new HashMap<>();
        addressesBySymbol.put("SP", 0);
        addressesBySymbol.put("LCL", 1);
        addressesBySymbol.put("ARG", 2);
        addressesBySymbol.put("THIS", 3);
        addressesBySymbol.put("THAT", 4);
        addressesBySymbol.put("SCREEN", 16384);
        addressesBySymbol.put("KBD", 24576);
        addressesBySymbol.put("R0", 0);
        addressesBySymbol.put("R1", 1);
        addressesBySymbol.put("R2", 2);
        addressesBySymbol.put("R3", 3);
        addressesBySymbol.put("R4", 4);
        addressesBySymbol.put("R5", 5);
        addressesBySymbol.put("R6", 6);
        addressesBySymbol.put("R7", 7);
        addressesBySymbol.put("R8", 8);
        addressesBySymbol.put("R9", 9);
        addressesBySymbol.put("R10", 10);
        addressesBySymbol.put("R11", 11);
        addressesBySymbol.put("R12", 12);
        addressesBySymbol.put("R13", 13);
        addressesBySymbol.put("R14", 14);
        addressesBySymbol.put("R15", 15);
    }
    public void addEntry(String symbol, int address) {
        addressesBySymbol.put(symbol, address);
    }

    public Optional<String> getAddress(String symbol) {
        return Optional.ofNullable(addressesBySymbol.get(symbol)).map(Object::toString);
    }
}
