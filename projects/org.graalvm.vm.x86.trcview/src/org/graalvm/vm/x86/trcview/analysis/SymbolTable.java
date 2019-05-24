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
package org.graalvm.vm.x86.trcview.analysis;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.graalvm.vm.util.HexFormatter;
import org.graalvm.vm.x86.node.debug.trace.StepRecord;
import org.graalvm.vm.x86.trcview.io.Node;
import org.graalvm.vm.x86.trcview.io.RecordNode;

public class SymbolTable {
    private final Map<Long, Symbol> symbols = new HashMap<>();

    private static Symbol sub(long pc) {
        String name = "sub_" + HexFormatter.tohex(pc, 1);
        return new Symbol(name, pc, Symbol.Type.SUBROUTINE);
    }

    private static Symbol loc(long pc) {
        String name = "loc_" + HexFormatter.tohex(pc, 1);
        return new Symbol(name, pc, Symbol.Type.LOCATION);
    }

    public void addSubroutine(long pc) {
        symbols.put(pc, sub(pc));
    }

    public void addSubroutine(long pc, String name) {
        symbols.put(pc, new Symbol(name, pc, Symbol.Type.SUBROUTINE));
    }

    public void addLocation(long pc) {
        Symbol sym = symbols.get(pc);
        if (sym == null) {
            symbols.put(pc, loc(pc));
        }
    }

    public void visit(Node node) {
        if (node instanceof RecordNode && ((RecordNode) node).getRecord() instanceof StepRecord) {
            StepRecord step = (StepRecord) ((RecordNode) node).getRecord();
            long pc = step.getLocation().getPC();
            Symbol sym = symbols.get(pc);
            if (sym != null) {
                sym.addVisit(node);
            }
        }
    }

    public Symbol get(long pc) {
        return symbols.get(pc);
    }

    public Set<Symbol> getSubroutines() {
        return symbols.values().stream().filter(s -> s.type == Symbol.Type.SUBROUTINE).collect(Collectors.toSet());
    }

    public Set<Symbol> getLocations() {
        return symbols.values().stream().filter(s -> s.type == Symbol.Type.LOCATION).collect(Collectors.toSet());
    }

    public Collection<Symbol> getSymbols() {
        return Collections.unmodifiableCollection(symbols.values());
    }
}
