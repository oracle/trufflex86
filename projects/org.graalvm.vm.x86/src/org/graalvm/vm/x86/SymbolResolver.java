package org.graalvm.vm.x86;

import java.util.Map.Entry;
import java.util.NavigableMap;

import com.everyware.posix.elf.Symbol;

public class SymbolResolver {
    private final NavigableMap<Long, Symbol> symbols;

    public SymbolResolver(NavigableMap<Long, Symbol> symbols) {
        this.symbols = symbols;
    }

    public Symbol getSymbol(long pc) {
        Symbol sym = symbols.get(pc);
        if (sym != null) {
            return sym;
        }
        Entry<Long, Symbol> entry = symbols.floorEntry(pc);
        if (entry != null) {
            sym = entry.getValue();
            long start = sym.getValue();
            long end = start + sym.getSize();
            if (Long.compareUnsigned(pc, start) >= 0 && Long.compareUnsigned(pc, end) < 0) {
                return sym;
            }
        }
        return null;
    }
}
