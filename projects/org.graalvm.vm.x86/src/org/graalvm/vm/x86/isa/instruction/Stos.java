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
import org.graalvm.vm.x86.node.MemoryWriteNode;
import org.graalvm.vm.x86.node.ReadFlagNode;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.profiles.ConditionProfile;

public abstract class Stos extends AMD64Instruction {
    private final String name;
    @Child protected ReadFlagNode readDF;
    @Child protected ReadNode readSrc;
    @Child protected ReadNode readDst;
    @Child protected WriteNode writeDst;
    @Child protected MemoryWriteNode writeMemory;

    protected final ConditionProfile profile = ConditionProfile.createBinaryProfile();

    protected Stos(long pc, byte[] instruction, String name) {
        super(pc, instruction);
        this.name = name;

        setGPRReadOperands(new RegisterOperand(Register.RAX), new RegisterOperand(Register.RDI));
        setGPRWriteOperands(new RegisterOperand(Register.RDI));
    }

    protected void createChildNodes(Register src) {
        ArchitecturalState state = getState();
        RegisterAccessFactory regs = state.getRegisters();
        readDF = regs.getDF().createRead();
        readSrc = regs.getRegister(src).createRead();
        readDst = regs.getRegister(Register.RDI).createRead();
        writeDst = regs.getRegister(Register.RDI).createWrite();
        writeMemory = state.createMemoryWrite();
    }

    public static class Stosb extends Stos {
        public Stosb(long pc, byte[] instruction) {
            super(pc, instruction, "stosb");
        }

        @Override
        protected void createChildNodes() {
            createChildNodes(Register.AL);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean df = readDF.execute(frame);
            byte al = readSrc.executeI8(frame);
            long rdi = readDst.executeI64(frame);
            writeMemory.executeI8(rdi, al);
            if (profile.profile(df)) {
                rdi--;
            } else {
                rdi++;
            }
            writeDst.executeI64(frame, rdi);
            return next();
        }
    }

    public static class Stosw extends Stos {
        public Stosw(long pc, byte[] instruction) {
            super(pc, instruction, "stosw");
        }

        @Override
        protected void createChildNodes() {
            createChildNodes(Register.AX);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean df = readDF.execute(frame);
            short ax = readSrc.executeI16(frame);
            long rdi = readDst.executeI64(frame);
            writeMemory.executeI16(rdi, ax);
            if (profile.profile(df)) {
                rdi -= 2;
            } else {
                rdi += 2;
            }
            writeDst.executeI64(frame, rdi);
            return next();
        }
    }

    public static class Stosd extends Stos {
        public Stosd(long pc, byte[] instruction) {
            super(pc, instruction, "stosd");
        }

        @Override
        protected void createChildNodes() {
            createChildNodes(Register.EAX);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean df = readDF.execute(frame);
            int eax = readSrc.executeI32(frame);
            long rdi = readDst.executeI64(frame);
            writeMemory.executeI32(rdi, eax);
            if (profile.profile(df)) {
                rdi -= 4;
            } else {
                rdi += 4;
            }
            writeDst.executeI64(frame, rdi);
            return next();
        }
    }

    public static class Stosq extends Stos {
        public Stosq(long pc, byte[] instruction) {
            super(pc, instruction, "stosq");
        }

        @Override
        protected void createChildNodes() {
            createChildNodes(Register.RAX);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean df = readDF.execute(frame);
            long rax = readSrc.executeI64(frame);
            long rdi = readDst.executeI64(frame);
            writeMemory.executeI64(rdi, rax);
            if (profile.profile(df)) {
                rdi -= 8;
            } else {
                rdi += 8;
            }
            writeDst.executeI64(frame, rdi);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{name};
    }
}
