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

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.graalvm.vm.posix.elf.Symbol;
import org.graalvm.vm.util.log.Trace;
import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.Options;
import org.graalvm.vm.x86.isa.CpuState;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.isa.ReturnException;
import org.graalvm.vm.x86.node.AMD64RootNode;
import org.graalvm.vm.x86.node.init.CopyToCpuStateNode;
import org.graalvm.vm.x86.node.init.InitializeFromCpuStateNode;
import org.graalvm.vm.x86.posix.InteropReturnException;
import org.graalvm.vm.x86.posix.InteropReturnResult;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

public class TraceCallTarget extends AMD64RootNode {
    private static final Logger log = Trace.create(TraceCallTarget.class);

    public static final boolean TRUFFLE_CALLS = getBoolean(Options.TRUFFLE_CALLS);

    @Child private InitializeFromCpuStateNode write = new InitializeFromCpuStateNode();
    @Child private CopyToCpuStateNode read = new CopyToCpuStateNode();
    @Child private TraceDispatchNode dispatch;

    private static final boolean CHECK = getBoolean(Options.TRACE_STATE_CHECK);

    protected TraceCallTarget(TruffleLanguage<AMD64Context> language, FrameDescriptor fd) {
        super(language, fd);
    }

    @CompilationFinal private Symbol sym = null;

    @CompilationFinal(dimensions = 1) private boolean[] gprReadMask = null;
    @CompilationFinal(dimensions = 1) private boolean[] gprWriteMask = null;
    @CompilationFinal(dimensions = 1) private boolean[] avxReadMask = null;
    @CompilationFinal(dimensions = 1) private boolean[] avxWriteMask = null;

    @CompilationFinal(dimensions = 1) private static final boolean[] allTrue = new boolean[16];
    static {
        for (int i = 0; i < 16; i++) {
            allTrue[i] = true;
        }
    }

    private boolean first = true;

    private static Register[] getRegisters(boolean[] mask) {
        int cnt = 0;
        for (boolean v : mask) {
            if (v) {
                cnt++;
            }
        }
        Register[] result = new Register[cnt];
        int j = 0;
        for (int i = 0; i < mask.length; i++) {
            if (mask[i]) {
                result[j++] = Register.get(i);
            }
        }
        return result;
    }

    @TruffleBoundary
    private void debug() {
        if (first) {
            first = false;
            Register[] reads = dispatch.getGPRReads();
            Register[] writes = dispatch.getGPRWrites();
            Register[] readMask = getRegisters(gprReadMask);
            Register[] writeMask = getRegisters(gprWriteMask);
            System.out.printf("GPR reads: %s (%s)\n", Stream.of(reads).map(Register::toString).collect(Collectors.joining(",")),
                            Stream.of(readMask).map(Register::toString).collect(Collectors.joining(",")));
            System.out.printf("GPR writes: %s (%s)\n", Stream.of(writes).map(Register::toString).collect(Collectors.joining(",")),
                            Stream.of(writeMask).map(Register::toString).collect(Collectors.joining(",")));
            if (reads.length == 0 || writes.length != 0) {
                System.out.printf("entry point: 0x%016x\n", dispatch.getStartAddress());
                dispatch.dump();
            }
        }
    }

    private void assertEq(String name, long ref, long act) {
        if (ref != act) {
            System.out.printf("%s: 0x%016x expected, was 0x%016x\n", name, ref, act);
            debug();
            throw new AssertionError();
        }
    }

    @TruffleBoundary
    private void check(CpuState ok, CpuState reduced) {
        assertEq("rax", ok.rax, reduced.rax);
        assertEq("rbx", ok.rbx, reduced.rbx);
        assertEq("rcx", ok.rcx, reduced.rcx);
        assertEq("rdx", ok.rdx, reduced.rdx);
        assertEq("rsp", ok.rsp, reduced.rsp);
        assertEq("rbp", ok.rbp, reduced.rbp);
        assertEq("rsi", ok.rsi, reduced.rsi);
        assertEq("rdi", ok.rdi, reduced.rdi);
        assertEq("r8", ok.r8, reduced.r8);
        assertEq("r9", ok.r9, reduced.r9);
        assertEq("r10", ok.r10, reduced.r10);
        assertEq("r11", ok.r11, reduced.r11);
        assertEq("r12", ok.r12, reduced.r12);
        assertEq("r13", ok.r13, reduced.r13);
        assertEq("r14", ok.r14, reduced.r14);
        assertEq("r15", ok.r15, reduced.r15);
    }

    @Override
    public CpuState execute(VirtualFrame frame) {
        CpuState initialState = (CpuState) frame.getArguments()[0];
        if (dispatch == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            AMD64Context ctx = getContextReference().get();
            ArchitecturalState state = ctx.getState();
            dispatch = insert(new TraceDispatchNode(state));
            try {
                sym = ctx.getSymbolResolver().getSymbol(initialState.rip);
            } catch (Throwable t) {
                log.log(Level.WARNING, "Cannot resolve symbol: " + t, t);
            }
        }
        if (gprReadMask != null && !TRUFFLE_CALLS) {
            write.execute(frame, initialState, gprReadMask, avxReadMask);
        } else {
            write.execute(frame, initialState);
        }
        long pc;
        boolean ret = false;
        boolean interopRet = false;
        try {
            pc = dispatch.execute(frame);
        } catch (ReturnException e) {
            pc = e.getBTA();
            ret = true;
        } catch (InteropReturnException e) {
            pc = 0; // dummy value, never used
            interopRet = true;
        }
        if (gprReadMask == null && !TRUFFLE_CALLS) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            gprReadMask = new boolean[16];
            gprWriteMask = new boolean[16];
            Register[] reads = dispatch.getGPRReads();
            for (Register r : reads) {
                if (r.getID() < 16) {
                    gprReadMask[r.getID()] = true;
                }
            }
            Register[] writes = dispatch.getGPRWrites();
            for (Register r : writes) {
                if (r.getID() < 16) {
                    gprReadMask[r.getID()] = true; // initialize frames
                    gprWriteMask[r.getID()] = true;
                }
            }

            avxReadMask = new boolean[32];
            avxWriteMask = new boolean[32];
            int[] avxReads = dispatch.getAVXReads();
            for (int r : avxReads) {
                avxReadMask[r] = true;
            }
            int[] avxWrites = dispatch.getAVXWrites();
            for (int r : avxWrites) {
                avxReadMask[r] = true; // initialize frames
                avxWriteMask[r] = true;
            }
        }
        CpuState result;
        if (TRUFFLE_CALLS) {
            result = read.execute(frame, pc);
            if (ret) {
                throw new RetException(result);
            } else if (interopRet) {
                throw new InteropReturnResult(result);
            } else {
                return result;
            }
        } else {
            result = read.execute(frame, pc, initialState, gprWriteMask, avxWriteMask);
            if (CHECK) {
                CpuState full = read.execute(frame, pc);
                check(full, result);
            }
            if (ret) {
                CompilerDirectives.transferToInterpreter();
                throw new AssertionError("ret must not happen without TRUFFLE_CALLS");
            }
            if (interopRet) {
                throw new InteropReturnResult(result);
            }
            return result;
        }
    }

    public long getStartAddress() {
        return dispatch.getStartAddress();
    }

    @Override
    public String getName() {
        return toString();
    }

    @Override
    public String toString() {
        CompilerAsserts.neverPartOfCompilation();
        long addr = dispatch.getStartAddress();
        if (addr == -1) {
            return "TraceCallTarget[???]";
        } else {
            if (sym != null) {
                return String.format("TraceCallTarget[0x%016x#%s]", addr, sym.getName());
            } else {
                return String.format("TraceCallTarget[0x%016x]", addr);
            }
        }
    }
}
