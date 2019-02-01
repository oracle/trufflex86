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

import org.graalvm.vm.x86.AMD64Register;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.isa.RegisterOperand;
import org.graalvm.vm.x86.node.MemoryWriteNode;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.RegisterReadNode;
import org.graalvm.vm.x86.node.RegisterWriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Push extends AMD64Instruction {
    private final Operand operand;

    @Child protected ReadNode readSrc;
    @Child protected RegisterReadNode readRSP;
    @Child protected RegisterWriteNode writeRSP;
    @Child protected MemoryWriteNode writeMemory;

    protected Push(long pc, byte[] instruction, Operand src) {
        super(pc, instruction);
        this.operand = src;

        setGPRReadOperands(operand, new RegisterOperand(Register.RSP));
        setGPRWriteOperands(new RegisterOperand(Register.RSP));
    }

    @Override
    protected void createChildNodes() {
        assert readRSP == null;
        assert writeRSP == null;
        assert writeMemory == null;

        ArchitecturalState state = getState();
        AMD64Register rsp = state.getRegisters().getRegister(Register.RSP);
        readSrc = operand.createRead(state, next());
        readRSP = rsp.createRead();
        writeRSP = rsp.createWrite();
        writeMemory = state.createMemoryWrite();
    }

    public static class Pushb extends Push {
        public Pushb(long pc, byte[] instruction, Operand src) {
            super(pc, instruction, src);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            byte value = readSrc.executeI8(frame);
            long rsp = readRSP.executeI64(frame);
            rsp--;
            writeMemory.executeI8(rsp, value);
            writeRSP.executeI64(frame, rsp);
            return next();
        }
    }

    public static class Pushw extends Push {
        public Pushw(long pc, byte[] instruction, Operand src) {
            super(pc, instruction, src);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            short value = readSrc.executeI16(frame);
            long rsp = readRSP.executeI64(frame);
            rsp -= 2;
            writeMemory.executeI16(rsp, value);
            writeRSP.executeI64(frame, rsp);
            return next();
        }
    }

    public static class Pushl extends Push {
        public Pushl(long pc, byte[] instruction, Operand src) {
            super(pc, instruction, src);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            int value = readSrc.executeI32(frame);
            long rsp = readRSP.executeI64(frame);
            rsp -= 4;
            writeMemory.executeI32(rsp, value);
            writeRSP.executeI64(frame, rsp);
            return next();
        }
    }

    public static class Pushq extends Push {
        public Pushq(long pc, byte[] instruction, Operand src) {
            super(pc, instruction, src);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long value = readSrc.executeI64(frame);
            long rsp = readRSP.executeI64(frame);
            rsp -= 8;
            writeMemory.executeI64(rsp, value);
            writeRSP.executeI64(frame, rsp);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"push", operand.toString()};
    }
}
