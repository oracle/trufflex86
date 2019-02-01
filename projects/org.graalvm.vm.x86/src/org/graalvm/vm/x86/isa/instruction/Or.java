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
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Flags;
import org.graalvm.vm.x86.isa.ImmediateOperand;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteFlagNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Or extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child protected ReadNode srcA;
    @Child protected ReadNode srcB;
    @Child protected WriteNode dst;
    @Child protected WriteFlagNode writeCF;
    @Child protected WriteFlagNode writePF;
    @Child protected WriteFlagNode writeZF;
    @Child protected WriteFlagNode writeSF;
    @Child protected WriteFlagNode writeOF;

    protected Or(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;

        setGPRReadOperands(operand1, operand2);
        setGPRWriteOperands(operand1);
    }

    @Override
    protected void createChildNodes() {
        ArchitecturalState state = getState();
        srcA = operand1.createRead(state, next());
        srcB = operand2.createRead(state, next());
        dst = operand1.createWrite(state, next());

        writeCF = state.getRegisters().getCF().createWrite();
        writePF = state.getRegisters().getPF().createWrite();
        writeZF = state.getRegisters().getZF().createWrite();
        writeSF = state.getRegisters().getSF().createWrite();
        writeOF = state.getRegisters().getOF().createWrite();
    }

    protected static Operand getOp1(OperandDecoder operands, int type, boolean swap) {
        if (swap) {
            return operands.getOperand2(type);
        } else {
            return operands.getOperand1(type);
        }
    }

    protected static Operand getOp2(OperandDecoder operands, int type, boolean swap) {
        if (swap) {
            return operands.getOperand1(type);
        } else {
            return operands.getOperand2(type);
        }
    }

    public static class Orb extends Or {
        public Orb(long pc, byte[] instruction, OperandDecoder operands) {
            this(pc, instruction, operands, false);
        }

        public Orb(long pc, byte[] instruction, OperandDecoder operands, boolean swap) {
            super(pc, instruction, getOp1(operands, OperandDecoder.R8, swap), getOp2(operands, OperandDecoder.R8, swap));
        }

        public Orb(long pc, byte[] instruction, OperandDecoder operands, byte imm) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R8), new ImmediateOperand(imm));
        }

        public Orb(long pc, byte[] instruction, Operand operand, byte imm) {
            super(pc, instruction, operand, new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            byte a = srcA.executeI8(frame);
            byte b = srcB.executeI8(frame);
            byte result = (byte) (a | b);
            dst.executeI8(frame, result);
            writeCF.execute(frame, false);
            writeOF.execute(frame, false);
            writeZF.execute(frame, result == 0);
            writeSF.execute(frame, result < 0);
            writePF.execute(frame, Flags.getParity(result));
            return next();
        }
    }

    public static class Orw extends Or {
        public Orw(long pc, byte[] instruction, OperandDecoder operands) {
            this(pc, instruction, operands, false);
        }

        public Orw(long pc, byte[] instruction, OperandDecoder operands, boolean swap) {
            super(pc, instruction, getOp1(operands, OperandDecoder.R16, swap), getOp2(operands, OperandDecoder.R16, swap));
        }

        public Orw(long pc, byte[] instruction, OperandDecoder operands, short imm) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R16), new ImmediateOperand(imm));
        }

        public Orw(long pc, byte[] instruction, Operand operand, short imm) {
            super(pc, instruction, operand, new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            short a = srcA.executeI16(frame);
            short b = srcB.executeI16(frame);
            short result = (short) (a | b);
            dst.executeI16(frame, result);
            writeCF.execute(frame, false);
            writeOF.execute(frame, false);
            writeZF.execute(frame, result == 0);
            writeSF.execute(frame, result < 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            return next();
        }
    }

    public static class Orl extends Or {
        public Orl(long pc, byte[] instruction, OperandDecoder operands) {
            this(pc, instruction, operands, false);
        }

        public Orl(long pc, byte[] instruction, OperandDecoder operands, boolean swap) {
            super(pc, instruction, getOp1(operands, OperandDecoder.R32, swap), getOp2(operands, OperandDecoder.R32, swap));
        }

        public Orl(long pc, byte[] instruction, OperandDecoder operands, int imm) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R32), new ImmediateOperand(imm));
        }

        public Orl(long pc, byte[] instruction, Operand operand, int imm) {
            super(pc, instruction, operand, new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            int a = srcA.executeI32(frame);
            int b = srcB.executeI32(frame);
            int result = a | b;
            dst.executeI32(frame, result);
            writeCF.execute(frame, false);
            writeOF.execute(frame, false);
            writeZF.execute(frame, result == 0);
            writeSF.execute(frame, result < 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            return next();
        }
    }

    public static class Orq extends Or {
        public Orq(long pc, byte[] instruction, OperandDecoder operands) {
            this(pc, instruction, operands, false);
        }

        public Orq(long pc, byte[] instruction, OperandDecoder operands, boolean swap) {
            super(pc, instruction, getOp1(operands, OperandDecoder.R64, swap), getOp2(operands, OperandDecoder.R64, swap));
        }

        public Orq(long pc, byte[] instruction, OperandDecoder operands, long imm) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R64), new ImmediateOperand(imm));
        }

        public Orq(long pc, byte[] instruction, Operand operand, long imm) {
            super(pc, instruction, operand, new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long a = srcA.executeI64(frame);
            long b = srcB.executeI64(frame);
            long result = a | b;
            dst.executeI64(frame, result);
            writeCF.execute(frame, false);
            writeOF.execute(frame, false);
            writeZF.execute(frame, result == 0);
            writeSF.execute(frame, result < 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"or", operand1.toString(), operand2.toString()};
    }
}
