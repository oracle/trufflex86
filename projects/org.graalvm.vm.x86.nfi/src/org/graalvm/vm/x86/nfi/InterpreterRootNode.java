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
package org.graalvm.vm.x86.nfi;

import static org.graalvm.vm.x86.Options.getBoolean;

import java.util.logging.Logger;

import org.graalvm.vm.memory.exception.SegmentationViolation;
import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.posix.api.ProcessExitException;
import org.graalvm.vm.posix.api.Signal;
import org.graalvm.vm.util.log.Trace;
import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.CpuRuntimeException;
import org.graalvm.vm.x86.InteropFunctionPointers;
import org.graalvm.vm.x86.Options;
import org.graalvm.vm.x86.isa.CpuState;
import org.graalvm.vm.x86.isa.IllegalInstructionException;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.node.AMD64Node;
import org.graalvm.vm.x86.node.MemoryWriteNode;
import org.graalvm.vm.x86.node.RegisterReadNode;
import org.graalvm.vm.x86.node.flow.AbstractDispatchNode;
import org.graalvm.vm.x86.node.flow.DispatchNode;
import org.graalvm.vm.x86.node.flow.InterTraceDispatchNode;
import org.graalvm.vm.x86.node.flow.RetException;
import org.graalvm.vm.x86.node.init.CopyToCpuStateNode;
import org.graalvm.vm.x86.node.init.InitializeFromCpuStateNode;
import org.graalvm.vm.x86.node.init.InitializerNode;
import org.graalvm.vm.x86.posix.InteropInitException;
import org.graalvm.vm.x86.posix.InteropReturnException;
import org.graalvm.vm.x86.posix.InteropReturnResult;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;

public class InterpreterRootNode extends AMD64Node {
    private static final Logger log = Trace.create(InterpreterRootNode.class);

    @CompilationFinal private static boolean SIMPLE_DISPATCH = getBoolean(Options.SIMPLE_DISPATCH) || getBoolean(Options.DEBUG_EXEC);
    @CompilationFinal private static boolean DEBUG_STATE = getBoolean(Options.DEBUG_PRINT_STATE);

    @Child private InitializerNode initializer;
    @Child private AbstractDispatchNode interpreter;
    @Child private InitializeFromCpuStateNode writeState = new InitializeFromCpuStateNode();

    @Child private RegisterReadNode readPC;
    @Child private RegisterReadNode readRAX;
    @Child private CopyToCpuStateNode readCpuState = new CopyToCpuStateNode();
    @Child private MemoryWriteNode writeMemory;

    public InterpreterRootNode(ArchitecturalState state) {
        if (SIMPLE_DISPATCH) {
            log.warning("Using old and slow dispatch node");
            interpreter = new DispatchNode(state);
        } else {
            interpreter = new InterTraceDispatchNode(state);
        }
        readPC = state.getRegisters().getPC().createRead();
        readRAX = state.getRegisters().getRegister(Register.RAX).createRead();
        writeMemory = new MemoryWriteNode(state.getMemory());
    }

    public InterpreterRootNode(ArchitecturalState state, String programName) {
        this(state);
        initializer = new InitializerNode(state, programName, new String[]{programName});
    }

    public InteropFunctionPointers executeInit(VirtualFrame frame) {
        initializer.execute(frame);
        try {
            while (true) {
                try {
                    interpreter.execute(frame);
                    throw new IllegalStateException("interpreter must not return");
                } catch (RetException e) {
                    writeState.execute(frame, e.getState());
                    continue;
                }
            }
        } catch (InteropInitException e) {
            CpuState state = readCpuState.execute(frame, readPC.executeI64(frame));
            AMD64Context ctx = getContextReference().get();
            ctx.setStateSnapshot(state);
            return new InteropFunctionPointers(e.getLoadLibrary(), e.getReleaseLibrary(), e.getSymbol(), e.getTruffleEnv());
        } catch (ProcessExitException e) {
            throw new UnsatisfiedLinkError("Initialization failed with error code " + e.getCode());
        }
    }

    public long executeInterop(VirtualFrame frame, long sp, long ret, long pc, long a1, long a2, long a3, long a4, long a5, long a6) {
        return executeInterop(frame, sp, ret, pc, a1, a2, a3, a4, a5, a6, 0, 0, 0, 0, 0, 0, 0, 0, false);
    }

    public long executeInterop(VirtualFrame frame, long sp, long ret, long pc, long a1, long a2, long a3, long a4, long a5, long a6, long f1, long f2, long f3, long f4, long f5, long f6,
                    long f7, long f8, boolean returnFloat) {
        AMD64Context ctx = getContextReference().get();
        CpuState state = new CpuState();
        state.rip = pc;
        state.rsp = sp;
        state.rdi = a1;
        state.rsi = a2;
        state.rdx = a3;
        state.rcx = a4;
        state.r8 = a5;
        state.r9 = a6;
        for (int i = 0; i < 16; i++) {
            state.xmm[i] = new Vector128();
        }
        state.xmm[0].setI64(1, f1);
        state.xmm[1].setI64(1, f2);
        state.xmm[2].setI64(1, f3);
        state.xmm[3].setI64(1, f4);
        state.xmm[4].setI64(1, f5);
        state.xmm[5].setI64(1, f6);
        state.xmm[6].setI64(1, f7);
        state.xmm[7].setI64(1, f8);
        state.fs = ctx.getStateSnapshot().fs;
        state.gs = ctx.getStateSnapshot().gs;
        writeState.execute(frame, state);
        writeMemory.executeI64(sp, ret);
        try {
            while (true) {
                try {
                    interpreter.execute(frame);
                    CompilerDirectives.transferToInterpreter();
                    throw new IllegalStateException("interpreter must not return");
                } catch (RetException e) {
                    writeState.execute(frame, e.getState());
                    continue;
                }
            }
        } catch (InteropReturnException e) {
            return e.getValue();
        } catch (InteropReturnResult e) {
            state = e.getState();
            if (returnFloat) {
                return state.xmm[0].getI64(1);
            } else {
                return state.rdi;
            }
        } catch (ProcessExitException e) {
            CompilerDirectives.transferToInterpreter();
            throw new UnsatisfiedLinkError();
        }
    }

    public int executeUntilExit(VirtualFrame frame) {
        CpuState state = getContextReference().get().getStateSnapshot();
        writeState.execute(frame, state);
        try {
            while (true) {
                try {
                    interpreter.execute(frame);
                    throw new IllegalStateException("interpreter must not return");
                } catch (RetException e) {
                    writeState.execute(frame, e.getState());
                    continue;
                }
            }
        } catch (ProcessExitException e) {
            return e.getCode();
        } catch (CpuRuntimeException e) {
            if (e.getCause() instanceof IllegalInstructionException) {
                return 128 + Signal.SIGILL;
            } else if (e.getCause() instanceof SegmentationViolation) {
                return 128 + Signal.SIGSEGV;
            } else {
                return 127;
            }
        } catch (Throwable t) {
            CompilerDirectives.transferToInterpreter();
            t.printStackTrace(Trace.log);
            return 127;
        }
    }
}
