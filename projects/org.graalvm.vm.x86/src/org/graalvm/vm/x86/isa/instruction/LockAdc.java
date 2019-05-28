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
import org.graalvm.vm.x86.isa.Flags;
import org.graalvm.vm.x86.isa.ImmediateOperand;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadFlagNode;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteFlagNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class LockAdc extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child protected ReadNode srcA;
    @Child protected ReadNode srcB;
    @Child protected WriteNode dst;
    @Child protected ReadFlagNode readCF;
    @Child protected WriteFlagNode writeCF;
    @Child protected WriteFlagNode writeOF;
    @Child protected WriteFlagNode writeSF;
    @Child protected WriteFlagNode writeZF;
    @Child protected WriteFlagNode writePF;

    @Override
    protected void createChildNodes() {
        assert srcA == null;
        assert srcB == null;
        assert dst == null;

        ArchitecturalState state = getState();
        srcA = operand1.createRead(state, next());
        srcB = operand2.createRead(state, next());
        dst = operand1.createWrite(state, next());
        RegisterAccessFactory regs = state.getRegisters();
        readCF = regs.getCF().createRead();
        writeCF = regs.getCF().createWrite();
        writeOF = regs.getOF().createWrite();
        writeSF = regs.getSF().createWrite();
        writeZF = regs.getZF().createWrite();
        writePF = regs.getPF().createWrite();
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

    protected LockAdc(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;

        setGPRReadOperands(operand1, operand2);
        setGPRWriteOperands(operand1);
    }

    public static class Adcb extends LockAdc {
        public Adcb(long pc, byte[] instruction, OperandDecoder operands) {
            this(pc, instruction, operands, false);
        }

        public Adcb(long pc, byte[] instruction, OperandDecoder operands, boolean swap) {
            super(pc, instruction, getOp1(operands, OperandDecoder.R8, swap), getOp2(operands, OperandDecoder.R8, swap));
        }

        public Adcb(long pc, byte[] instruction, OperandDecoder operands, byte imm) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R8), new ImmediateOperand(imm));
        }

        public Adcb(long pc, byte[] instruction, Operand operand, byte imm) {
            super(pc, instruction, operand, new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean ok;
            byte a;
            byte b;
            int c;
            byte result;
            byte result1;

            do {
                a = srcA.executeI8(frame);
                b = srcB.executeI8(frame);
                c = readCF.execute(frame) ? 1 : 0;
                result = (byte) (a + b + c);
                result1 = (byte) (a + b);
                byte expected = a; // srcA = dst
                ok = dst.executeCmpxchgI8(frame, expected, result);
            } while (!ok);

            boolean overflow = (result1 < 0 && a > 0 && b > 0) || (result1 >= 0 && a < 0 && b < 0);
            overflow |= (result < 0 && result1 > 0 && c > 0);
            overflow &= !(result < 0 && a < 0 && b < 0) && !(result >= 0 && a >= 0 && b >= 0);
            boolean carry = ((a < 0 || b < 0) && result1 >= 0) || (a < 0 && b < 0);
            carry |= ((result1 < 0) && result >= 0);
            writeCF.execute(frame, carry);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writeZF.execute(frame, result == 0);
            writePF.execute(frame, Flags.getParity(result));
            return next();
        }
    }

    public static class Adcw extends LockAdc {
        public Adcw(long pc, byte[] instruction, OperandDecoder operands) {
            this(pc, instruction, operands, false);
        }

        public Adcw(long pc, byte[] instruction, OperandDecoder operands, boolean swap) {
            super(pc, instruction, getOp1(operands, OperandDecoder.R16, swap), getOp2(operands, OperandDecoder.R16, swap));
        }

        public Adcw(long pc, byte[] instruction, OperandDecoder operands, short imm) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R16), new ImmediateOperand(imm));
        }

        public Adcw(long pc, byte[] instruction, Operand operand, short imm) {
            super(pc, instruction, operand, new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean ok;
            short a;
            short b;
            short result;
            short result1;
            int c;

            do {
                a = srcA.executeI16(frame);
                b = srcB.executeI16(frame);
                c = readCF.execute(frame) ? 1 : 0;
                result1 = (short) (a + b);
                result = (short) (a + b + c);
                short expected = a; // srcA = dst
                ok = dst.executeCmpxchgI16(frame, expected, result);
            } while (!ok);

            boolean overflow = (result1 < 0 && a > 0 && b > 0) || (result1 >= 0 && a < 0 && b < 0);
            overflow |= (result < 0 && result1 > 0 && c > 0);
            overflow &= !(result < 0 && a < 0 && b < 0) && !(result >= 0 && a >= 0 && b >= 0);
            boolean carry = ((a < 0 || b < 0) && result1 >= 0) || (a < 0 && b < 0);
            carry |= ((result1 < 0) && result >= 0);
            writeCF.execute(frame, carry);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writeZF.execute(frame, result == 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            return next();
        }
    }

    public static class Adcl extends LockAdc {
        public Adcl(long pc, byte[] instruction, OperandDecoder operands) {
            this(pc, instruction, operands, false);
        }

        public Adcl(long pc, byte[] instruction, OperandDecoder operands, boolean swap) {
            super(pc, instruction, getOp1(operands, OperandDecoder.R32, swap), getOp2(operands, OperandDecoder.R32, swap));
        }

        public Adcl(long pc, byte[] instruction, OperandDecoder operands, int imm) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R32), new ImmediateOperand(imm));
        }

        public Adcl(long pc, byte[] instruction, Operand operand, int imm) {
            super(pc, instruction, operand, new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean ok;
            int a;
            int b;
            int c;
            int result;
            int result1;

            do {
                a = srcA.executeI32(frame);
                b = srcB.executeI32(frame);
                c = readCF.execute(frame) ? 1 : 0;
                result = a + b + c;
                result1 = a + b;
                int expected = a; // srcA = dst
                ok = dst.executeCmpxchgI32(frame, expected, result);
            } while (!ok);

            boolean overflow = (result1 < 0 && a > 0 && b > 0) || (result1 >= 0 && a < 0 && b < 0);
            overflow |= (result < 0 && result1 > 0 && c > 0);
            overflow &= !(result < 0 && a < 0 && b < 0) && !(result >= 0 && a >= 0 && b >= 0);
            boolean carry = ((a < 0 || b < 0) && result1 >= 0) || (a < 0 && b < 0);
            carry |= ((result1 < 0) && result >= 0);
            writeCF.execute(frame, carry);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writeZF.execute(frame, result == 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            return next();
        }
    }

    public static class Adcq extends LockAdc {
        public Adcq(long pc, byte[] instruction, OperandDecoder operands) {
            this(pc, instruction, operands, false);
        }

        public Adcq(long pc, byte[] instruction, OperandDecoder operands, boolean swap) {
            super(pc, instruction, getOp1(operands, OperandDecoder.R64, swap), getOp2(operands, OperandDecoder.R64, swap));
        }

        public Adcq(long pc, byte[] instruction, OperandDecoder operands, long imm) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R64), new ImmediateOperand(imm));
        }

        public Adcq(long pc, byte[] instruction, Operand operand, long imm) {
            super(pc, instruction, operand, new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean ok;
            long a;
            long b;
            int c;
            long result1;
            long result;

            do {
                a = srcA.executeI64(frame);
                b = srcB.executeI64(frame);
                c = readCF.execute(frame) ? 1 : 0;
                result1 = a + b;
                result = a + b + c;
                long expected = a;
                ok = dst.executeCmpxchgI64(frame, expected, result);
            } while (!ok);

            boolean overflow = (result1 < 0 && a > 0 && b > 0) || (result1 >= 0 && a < 0 && b < 0);
            overflow |= (result < 0 && result1 > 0 && c > 0);
            overflow &= !(result < 0 && a < 0 && b < 0) && !(result >= 0 && a >= 0 && b >= 0);
            boolean carry = ((a < 0 || b < 0) && result1 >= 0) || (a < 0 && b < 0);
            carry |= ((result1 < 0) && result >= 0);
            writeCF.execute(frame, carry);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writeZF.execute(frame, result == 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"lock adc", operand1.toString(), operand2.toString()};
    }
}
