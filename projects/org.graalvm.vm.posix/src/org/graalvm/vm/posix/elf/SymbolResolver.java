/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.graalvm.vm.posix.elf;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

public class SymbolResolver {
    private final NavigableMap<Long, Symbol> symbols;
    private final NavigableMap<Long, Symbol> globalSymbols;

    private final HashMap<String, Symbol> addresses;

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

        // compute global addr -> symbol
        addresses = new HashMap<>();
        for (Entry<Long, Symbol> entry : symbols.entrySet()) {
            Symbol sym = entry.getValue();
            if (sym.getBind() == Symbol.GLOBAL) {
                addresses.put(sym.getName(), sym);
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

    public Symbol getSymbol(String name) {
        return addresses.get(name);
    }

    public Symbol getSymbolExact(long pc) {
        return symbols.get(pc);
    }
}
