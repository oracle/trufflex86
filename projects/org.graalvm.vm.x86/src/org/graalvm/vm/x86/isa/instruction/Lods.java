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

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.RegisterAccessFactory;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.isa.RegisterOperand;
import org.graalvm.vm.x86.node.MemoryReadNode;
import org.graalvm.vm.x86.node.RegisterReadNode;
import org.graalvm.vm.x86.node.RegisterWriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Lods extends AMD64Instruction {
    private final String name;

    @Child protected RegisterReadNode readRSI;
    @Child protected RegisterWriteNode writeRSI;
    @Child protected RegisterWriteNode writeA;
    @Child protected MemoryReadNode readMemory;

    protected Lods(long pc, byte[] instruction, String name) {
        super(pc, instruction);
        this.name = name;

        setGPRReadOperands(new RegisterOperand(Register.RSI));
        setGPRWriteOperands(new RegisterOperand(Register.RSI), new RegisterOperand(Register.RAX));
    }

    protected void createChildNodes(Register a) {
        assert readRSI == null;
        assert writeRSI == null;
        assert writeA == null;
        assert readMemory == null;

        ArchitecturalState state = getState();
        RegisterAccessFactory regs = state.getRegisters();
        readRSI = regs.getRegister(Register.RSI).createRead();
        writeRSI = regs.getRegister(Register.RSI).createWrite();
        writeA = regs.getRegister(a).createWrite();
        readMemory = state.createMemoryRead();
    }

    public static class Lodsb extends Lods {
        public Lodsb(long pc, byte[] instruction) {
            super(pc, instruction, "lodsb");
        }

        @Override
        protected void createChildNodes() {
            createChildNodes(Register.AL);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long rsi = readRSI.executeI64(frame);
            byte al = readMemory.executeI8(rsi);
            writeA.executeI8(frame, al);
            writeRSI.executeI64(frame, rsi + 1);
            return next();
        }
    }

    public static class Lodsw extends Lods {
        public Lodsw(long pc, byte[] instruction) {
            super(pc, instruction, "lodsw");
        }

        @Override
        protected void createChildNodes() {
            createChildNodes(Register.AX);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long rsi = readRSI.executeI64(frame);
            short ax = readMemory.executeI16(rsi);
            writeA.executeI16(frame, ax);
            writeRSI.executeI64(frame, rsi + 2);
            return next();
        }
    }

    public static class Lodsd extends Lods {
        public Lodsd(long pc, byte[] instruction) {
            super(pc, instruction, "lodsd");
        }

        @Override
        protected void createChildNodes() {
            createChildNodes(Register.EAX);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long rsi = readRSI.executeI64(frame);
            int eax = readMemory.executeI32(rsi);
            writeA.executeI32(frame, eax);
            writeRSI.executeI64(frame, rsi + 4);
            return next();
        }
    }

    public static class Lodsq extends Lods {
        public Lodsq(long pc, byte[] instruction) {
            super(pc, instruction, "lodsq");
        }

        @Override
        protected void createChildNodes() {
            createChildNodes(Register.RAX);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long rsi = readRSI.executeI64(frame);
            long rax = readMemory.executeI64(rsi);
            writeA.executeI64(frame, rax);
            writeRSI.executeI64(frame, rsi + 8);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{name};
    }
}
