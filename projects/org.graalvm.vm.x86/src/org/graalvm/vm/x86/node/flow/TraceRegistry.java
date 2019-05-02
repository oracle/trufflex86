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

import java.util.HashMap;
import java.util.Map;

import org.graalvm.vm.util.HexFormatter;
import org.graalvm.vm.x86.AMD64Context;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.frame.FrameDescriptor;

public class TraceRegistry {
    private final TruffleLanguage<AMD64Context> language;
    private final FrameDescriptor frameDescriptor;

    private final Map<Long, CompiledTrace> traces;
    private final ContextReference<AMD64Context> ctxref;
    private final Object lock = new Object();
    @CompilationFinal private Assumption singleThreaded;

    public TraceRegistry(TruffleLanguage<AMD64Context> language, FrameDescriptor frameDescriptor) {
        this.language = language;
        this.frameDescriptor = frameDescriptor;
        traces = new HashMap<>();
        ctxref = language.getContextReference();
    }

    public void initialize() {
        singleThreaded = ctxref.get().getSingleThreadedAssumption();
    }

    @TruffleBoundary
    private CompiledTrace doGet(long pc) {
        CompiledTrace trace = traces.get(pc);
        if (trace == null) {
            TraceCallTarget target = new TraceCallTarget(language, frameDescriptor, pc);
            trace = new CompiledTrace(target, ctxref.get().getSingleThreadedAssumption());
            traces.put(pc, trace);
        }
        if (trace.trace.getStartAddress() != pc) {
            CompilerDirectives.transferToInterpreter();
            throw new RuntimeException("error: " + HexFormatter.tohex(trace.trace.getStartAddress(), 16) + " vs " + HexFormatter.tohex(pc, 16));
        }
        return trace;
    }

    public CompiledTrace get(long pc) {
        if (singleThreaded.isValid()) {
            return doGet(pc);
        } else {
            synchronized (lock) {
                return doGet(pc);
            }
        }
    }

    public int size() {
        return traces.size();
    }
}
