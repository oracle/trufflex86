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
package org.graalvm.vm.x86.node.debug;

import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.memory.exception.SegmentationViolation;
import org.graalvm.vm.posix.elf.Symbol;
import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.RegisterAccessFactory;
import org.graalvm.vm.x86.SymbolResolver;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.node.AMD64Node;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.debug.trace.ExecutionTraceWriter;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;

public class TraceArgumentsNode extends AMD64Node {
    @CompilationFinal private VirtualMemory memory;
    @CompilationFinal private SymbolResolver resolver;
    @CompilationFinal private ExecutionTraceWriter traceWriter;

    @Child private ReadNode readRDI;
    @Child private ReadNode readRSI;
    @Child private ReadNode readRDX;
    @Child private ReadNode readRCX;
    @Child private ReadNode readR8;
    @Child private ReadNode readR9;

    private byte[] getMemory(long value) {
        byte[] result = new byte[0];
        if (memory.contains(value)) {
            int length = 0;
            for (int i = 64; i > 0; i -= 16) {
                if (memory.contains(value + i)) {
                    length = i;
                    break;
                }
            }
            if (length == 0) {
                return result;
            }
            result = new byte[length];
            try {
                for (int i = 0; i < length; i++) {
                    result[i] = memory.peek(value + i);
                }
            } catch (SegmentationViolation e) {
                result = null;
            }
        }
        return result;
    }

    @TruffleBoundary
    private void trace(long pc, long rdi, long rsi, long rdx, long rcx, long r8, long r9) {
        Symbol sym = resolver.getSymbol(pc);
        String name = sym != null ? sym.getName() : null;
        long[] args = new long[]{rdi, rsi, rdx, rcx, r8, r9};
        byte[][] mem = new byte[args.length][];
        mem[0] = getMemory(rdi);
        mem[1] = getMemory(rsi);
        mem[2] = getMemory(rdx);
        mem[3] = getMemory(rcx);
        mem[4] = getMemory(r8);
        mem[5] = getMemory(r9);

        traceWriter.callArgs(pc, name, args, mem);
    }

    public void execute(VirtualFrame frame, long pc) {
        if (readRDI == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            AMD64Context ctx = getContextReference().get();
            ArchitecturalState state = ctx.getState();
            RegisterAccessFactory regs = state.getRegisters();
            readRDI = regs.getRegister(Register.RDI).createRead();
            readRSI = regs.getRegister(Register.RSI).createRead();
            readRDX = regs.getRegister(Register.RDX).createRead();
            readRCX = regs.getRegister(Register.RCX).createRead();
            readR8 = regs.getRegister(Register.R8).createRead();
            readR9 = regs.getRegister(Register.R9).createRead();
            memory = state.getMemory();
            resolver = ctx.getSymbolResolver();
            traceWriter = ctx.getTraceWriter();
        }
        long rdi = readRDI.executeI64(frame);
        long rsi = readRSI.executeI64(frame);
        long rdx = readRDX.executeI64(frame);
        long rcx = readRCX.executeI64(frame);
        long r8 = readR8.executeI64(frame);
        long r9 = readR9.executeI64(frame);
        trace(pc, rdi, rsi, rdx, rcx, r8, r9);
    }
}
