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
package org.graalvm.vm.x86.isa.instruction;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.posix.api.Errno;
import org.graalvm.vm.util.log.Trace;
import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.RegisterAccessFactory;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.isa.RegisterOperand;
import org.graalvm.vm.x86.node.ReadFlagsNode;
import org.graalvm.vm.x86.node.RegisterReadNode;
import org.graalvm.vm.x86.node.RegisterWriteNode;
import org.graalvm.vm.x86.posix.PosixEnvironment;
import org.graalvm.vm.x86.posix.SyscallException;
import org.graalvm.vm.x86.posix.SyscallNames;
import org.graalvm.vm.x86.posix.SyscallWrapper;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.frame.VirtualFrame;

public class Syscall extends AMD64Instruction {
    private static final Logger log = Trace.create(Syscall.class);

    @Child private SyscallWrapper syscall = null;
    @Child private RegisterReadNode readRAX;
    @Child private RegisterReadNode readRDI;
    @Child private RegisterReadNode readRSI;
    @Child private RegisterReadNode readRDX;
    @Child private RegisterReadNode readR10;
    @Child private RegisterReadNode readR8;
    @Child private RegisterReadNode readR9;
    @Child private ReadFlagsNode readFlags;
    @Child private RegisterWriteNode writeRAX;
    @Child private RegisterWriteNode writeRCX;
    @Child private RegisterWriteNode writeR11;

    @CompilationFinal private ContextReference<AMD64Context> ctxRef;

    public Syscall(long pc, byte[] instruction) {
        super(pc, instruction);

        // Linux specific
        setGPRReadOperands(new RegisterOperand(Register.RAX), new RegisterOperand(Register.RDI), new RegisterOperand(Register.RSI), new RegisterOperand(Register.RDX),
                        new RegisterOperand(Register.R10), new RegisterOperand(Register.R8), new RegisterOperand(Register.R9));
        setGPRWriteOperands(new RegisterOperand(Register.RAX), new RegisterOperand(Register.RCX), new RegisterOperand(Register.R11));
    }

    @Override
    protected void createChildNodes() {
        assert syscall == null;

        ctxRef = getContextReference();
        AMD64Context ctx = ctxRef.get();
        RegisterAccessFactory reg = ctx.getState().getRegisters();
        PosixEnvironment posix = ctx.getPosixEnvironment();
        VirtualMemory memory = ctx.getMemory();
        syscall = insert(new SyscallWrapper(posix, memory));
        readRAX = reg.getRegister(Register.RAX).createRead();
        readRDI = reg.getRegister(Register.RDI).createRead();
        readRSI = reg.getRegister(Register.RSI).createRead();
        readRDX = reg.getRegister(Register.RDX).createRead();
        readR10 = reg.getRegister(Register.R10).createRead();
        readR8 = reg.getRegister(Register.R8).createRead();
        readR9 = reg.getRegister(Register.R9).createRead();
        writeRAX = reg.getRegister(Register.RAX).createWrite();
        writeRCX = reg.getRegister(Register.RCX).createWrite();
        writeR11 = reg.getRegister(Register.R11).createWrite();
        readFlags = reg.createReadFlags();
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        long rax = readRAX.executeI64(frame);
        long rdi = readRDI.executeI64(frame);
        long rsi = readRSI.executeI64(frame);
        long rdx = readRDX.executeI64(frame);
        long r10 = readR10.executeI64(frame);
        long r8 = readR8.executeI64(frame);
        long r9 = readR9.executeI64(frame);
        long result;
        try {
            result = syscall.executeI64(frame, (int) rax, rdi, rsi, rdx, r10, r8, r9, pc);
        } catch (SyscallException e) {
            result = -e.getValue();
            if (e.getValue() == Errno.ENOSYS) {
                if ((int) (rax >> 16) == 0xBEEF) {
                    // interop call
                    int id = (int) (rax & 0xFFFF);
                    try {
                        result = interopCall(id, rdi, rsi, rdx, r10, r8, r9);
                    } catch (SyscallException ex) {
                        result = -ex.getValue();
                        if (ex.getValue() == Errno.ENOSYS) {
                            log(rax);
                        }
                    }
                } else {
                    log(rax);
                }
            }
        }
        long rflags = readFlags.executeI64(frame);
        writeRAX.executeI64(frame, result);
        writeRCX.executeI64(frame, next());
        writeR11.executeI64(frame, rflags);
        return next();
    }

    @TruffleBoundary
    private long interopCall(int id, long a1, long a2, long a3, long a4, long a5, long a6) throws SyscallException {
        return ctxRef.get().interopCall(id, a1, a2, a3, a4, a5, a6);
    }

    @TruffleBoundary
    private static void log(long nr) {
        String name = SyscallNames.getName(nr);
        if (name != null) {
            log.log(Level.WARNING, "Unsupported syscall " + name + " (#" + nr + ")");
        } else {
            log.log(Level.WARNING, "Unsupported syscall " + nr);
        }
    }

    @Override
    public boolean isControlFlow() {
        return true;
    }

    @Override
    public long[] getBTA() {
        // return new long[]{next()};
        return null;
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"syscall"};
    }
}
