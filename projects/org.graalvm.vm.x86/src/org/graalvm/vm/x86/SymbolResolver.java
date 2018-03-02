package org.graalvm.vm.x86;

import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.everyware.posix.elf.Symbol;

public class SymbolResolver {
    private final NavigableMap<Long, Symbol> symbols;
    private final NavigableMap<Long, Symbol> globalSymbols;

    public SymbolResolver(NavigableMap<Long, Symbol> symbols) {
        this.symbols = symbols;

        // compute global symbols
        this.globalSymbols = new TreeMap<>();
        for (Entry<Long, Symbol> entry : symbols.entrySet()) {
            Symbol sym = entry.getValue();
            if (sym.getBind() == Symbol.GLOBAL) {
                globalSymbols.put(sym.getValue(), sym);
            }
        }
    }

    public Symbol getSymbol(long pc) {
        Symbol global = getSymbol(pc, globalSymbols);
        if (global == null) {
            return getSymbol(pc, symbols);
        } else {
            return global;
        }
    }

    private static Symbol getSymbol(long pc, NavigableMap<Long, Symbol> symbols) {
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

    public Symbol getSymbolExact(long pc) {
        return symbols.get(pc);
    }
}
