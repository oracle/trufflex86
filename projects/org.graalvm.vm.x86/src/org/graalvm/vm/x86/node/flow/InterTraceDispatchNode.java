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

import static org.graalvm.vm.x86.Options.getBoolean;

import org.graalvm.vm.util.log.Trace;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.Options;
import org.graalvm.vm.x86.isa.CpuState;
import org.graalvm.vm.x86.node.AMD64Node;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;
import org.graalvm.vm.x86.node.init.CopyToCpuStateNode;
import org.graalvm.vm.x86.node.init.InitializeFromCpuStateNode;
import org.graalvm.vm.x86.posix.InteropInitException;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.LoopNode;
import com.oracle.truffle.api.nodes.RepeatingNode;

public class InterTraceDispatchNode extends AbstractDispatchNode {
    @Child private ReadNode readPC;
    @Child private WriteNode writePC;

    @Child private CopyToCpuStateNode readState = new CopyToCpuStateNode();
    @Child private InitializeFromCpuStateNode writeState = new InitializeFromCpuStateNode();

    private final TraceRegistry traces;

    public static final boolean PRINT_STATS = getBoolean(Options.PRINT_DISPATCH_STATS);
    public static final boolean USE_LOOP_NODE = getBoolean(Options.USE_LOOP_NODE);

    private int noSuccessor = 0;
    private int hasSuccessor = 0;

    private long insncnt = 0;

    @Child private LoopNode loop = Truffle.getRuntime().createLoopNode(new LoopBody());

    private CpuState state;
    private CompiledTrace currentTrace;

    @CompilationFinal private CompiledTrace startTrace;

    public InterTraceDispatchNode(ArchitecturalState state) {
        readPC = state.getRegisters().getPC().createRead();
        writePC = state.getRegisters().getPC().createWrite();
        traces = state.getTraceRegistry();
    }

    @TruffleBoundary
    private void printStats() {
        Trace.log.printf("Traces: %d\n", traces.size());
        Trace.log.printf("Successor chain used: %d\n", hasSuccessor);
        Trace.log.printf("No successor chain used: %d\n", noSuccessor);
        Trace.log.printf("Executed instructions: %d\n", insncnt);
    }

    public CompiledTrace getStartTrace() {
        return startTrace;
    }

    private class LoopBody extends AMD64Node implements RepeatingNode {
        @Override
        public boolean executeRepeating(VirtualFrame frame) {
            try {
                state = (CpuState) currentTrace.callTarget.call(state);
            } catch (RetException e) {
                state = e.getState();
                throw e;
            }
            CompiledTrace next = currentTrace.getNext(state.rip);
            if (next == null) {
                noSuccessor++;
                next = traces.get(state.rip);
                currentTrace.setNext(next);
            } else {
                hasSuccessor++;
            }
            currentTrace = next;
            insncnt = state.instructionCount;
            return true;
        }
    }

    public CpuState execute(VirtualFrame frame, CpuState cpuState) {
        this.state = cpuState;
        currentTrace = startTrace;
        if (currentTrace == null || currentTrace.trace.getStartAddress() != state.rip) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            currentTrace = traces.get(state.rip);
            startTrace = currentTrace;
        }

        try {
            loop.executeLoop(frame);
        } catch (RetException e) {
            return e.getState();
        } catch (InteropInitException e) {
            writeState.execute(frame, state);
            throw e;
        }
        CompilerDirectives.transferToInterpreter();
        throw new AssertionError("loop node must not return");
    }

    @Override
    public long execute(VirtualFrame frame) {
        long pc = readPC.executeI64(frame);
        state = readState.execute(frame, pc);
        currentTrace = startTrace;
        if (currentTrace == null || currentTrace.trace.getStartAddress() != state.rip) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            currentTrace = traces.get(state.rip);
            startTrace = currentTrace;
        }

        try {
            if (USE_LOOP_NODE) {
                loop.executeLoop(frame);
                CompilerDirectives.transferToInterpreter();
                throw new AssertionError("loop node must not return");
            } else {
                while (true) {
                    pc = state.rip;
                    state = (CpuState) currentTrace.callTarget.call(state);
                    CompiledTrace next = currentTrace.getNext(state.rip);
                    if (next == null) {
                        noSuccessor++;
                        next = traces.get(state.rip);
                        currentTrace.setNext(next);
                    } else {
                        hasSuccessor++;
                    }
                    currentTrace = next;
                    insncnt = state.instructionCount;
                }
            }
        } catch (InteropInitException e) {
            writeState.execute(frame, state);
            throw e;
        }
    }
}
