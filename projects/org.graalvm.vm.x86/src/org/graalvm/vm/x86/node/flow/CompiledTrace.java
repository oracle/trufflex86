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
package org.graalvm.vm.x86.node.flow;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.nodes.InvalidAssumptionException;

public class CompiledTrace {
    public final RootCallTarget callTarget;
    public final TraceCallTarget trace;

    private int usedSuccessors;
    private final CompiledTrace[] successors;

    private final Object lock = new Object();

    private final Assumption singleThreaded;
    @CompilationFinal private boolean isSingleThreaded;

    public CompiledTrace(TraceCallTarget trace, Assumption singleThreaded) {
        this.trace = trace;
        this.singleThreaded = singleThreaded;
        isSingleThreaded = singleThreaded.isValid();
        callTarget = Truffle.getRuntime().createCallTarget(trace);
        usedSuccessors = 0;
        successors = new CompiledTrace[8];
    }

    public CompiledTrace getNext(long pc) {
        for (int i = 0; i < usedSuccessors; i++) {
            if (successors[i].trace.getStartAddress() == pc) {
                // TODO: sort as LRU
                return successors[i];
            }
        }
        return null;
    }

    private void doSetNext(CompiledTrace trc) {
        // trace already registered?
        for (int i = 0; i < usedSuccessors; i++) {
            if (successors[i] == trc) {
                return;
            }
        }

        if (usedSuccessors < successors.length) {
            successors[usedSuccessors] = trc;
        }
    }

    public void setNext(CompiledTrace trace) {
        if (isSingleThreaded) {
            try {
                singleThreaded.check();
                doSetNext(trace);
                return;
            } catch (InvalidAssumptionException e) {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                isSingleThreaded = false;
            }
        }

        // multithreaded cases
        synchronized (lock) {
            doSetNext(trace);
        }
    }
}
