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

import org.graalvm.vm.math.LongMultiplication;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.RegisterAccessFactory;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Flags;
import org.graalvm.vm.x86.isa.ImmediateOperand;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.isa.RegisterOperand;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteFlagNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Imul extends AMD64Instruction {
    protected final Operand operand1;
    protected final Operand operand2;
    protected final Operand operand3;

    @Child protected WriteFlagNode writeCF;
    @Child protected WriteFlagNode writeOF;
    @Child protected WriteFlagNode writeSF;
    @Child protected WriteFlagNode writePF;

    protected Imul(long pc, byte[] instruction, Operand operand1) {
        this(pc, instruction, operand1, null, null);
    }

    protected Imul(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        this(pc, instruction, operand1, operand2, null);
    }

    protected Imul(long pc, byte[] instruction, Operand operand1, Operand operand2, Operand operand3) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.operand3 = operand3;
    }

    protected void createFlagNodes(ArchitecturalState state) {
        RegisterAccessFactory regs = state.getRegisters();
        writeCF = regs.getCF().createWrite();
        writeOF = regs.getOF().createWrite();
        writeSF = regs.getSF().createWrite();
        writePF = regs.getPF().createWrite();
    }

    private static abstract class Imul1 extends Imul {
        @Child protected ReadNode readOp;
        @Child protected ReadNode readA;
        @Child protected WriteNode writeA;
        @Child protected WriteNode writeD;

        protected Imul1(long pc, byte[] instruction, OperandDecoder operands, int type) {
            super(pc, instruction, operands.getOperand1(type));

            setGPRReadOperands(operand1, new RegisterOperand(Register.RAX));
            setGPRWriteOperands(new RegisterOperand(Register.RAX), new RegisterOperand(Register.RDX));
        }

        protected void createChildNodes(Register ra, Register wa) {
            createChildNodes(ra, wa, null);
        }

        protected void createChildNodes(Register ra, Register wa, Register wd) {
            ArchitecturalState state = getState();
            RegisterAccessFactory regs = state.getRegisters();
            readOp = operand1.createRead(state, next());
            readA = regs.getRegister(ra).createRead();
            writeA = regs.getRegister(wa).createWrite();
            if (wd != null) {
                writeD = regs.getRegister(wd).createWrite();
            }
            createFlagNodes(state);
        }
    }

    public static class Imul1b extends Imul1 {
        public Imul1b(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands, OperandDecoder.R8);
            setGPRWriteOperands(new RegisterOperand(Register.RAX));
        }

        @Override
        protected void createChildNodes() {
            createChildNodes(Register.AL, Register.AX);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            byte a = readOp.executeI8(frame);
            byte b = readA.executeI8(frame);
            int result = a * b;
            writeA.executeI16(frame, (short) result);
            boolean overflow = result != (byte) result;
            writeCF.execute(frame, overflow);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, (byte) result < 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            return next();
        }
    }

    public static class Imul1w extends Imul1 {
        public Imul1w(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands, OperandDecoder.R16);
        }

        @Override
        protected void createChildNodes() {
            createChildNodes(Register.AX, Register.AX, Register.DX);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            short a = readOp.executeI16(frame);
            short b = readA.executeI16(frame);
            int result = a * b;
            writeA.executeI16(frame, (short) result);
            writeD.executeI16(frame, (short) (result >> 16));
            boolean overflow = result != (short) result;
            writeCF.execute(frame, overflow);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, (short) result < 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            return next();
        }
    }

    public static class Imul1l extends Imul1 {
        public Imul1l(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands, OperandDecoder.R32);
        }

        @Override
        protected void createChildNodes() {
            createChildNodes(Register.EAX, Register.EAX, Register.EDX);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            int a = readOp.executeI32(frame);
            int b = readA.executeI32(frame);
            long result = (long) a * (long) b;
            writeA.executeI32(frame, (int) result);
            writeD.executeI32(frame, (int) (result >> 32));
            boolean overflow = result != (int) result;
            writeCF.execute(frame, overflow);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, (int) result < 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            return next();
        }
    }

    public static class Imul1q extends Imul1 {
        public Imul1q(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands, OperandDecoder.R64);
        }

        @Override
        protected void createChildNodes() {
            createChildNodes(Register.RAX, Register.RAX, Register.RDX);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long a = readOp.executeI64(frame);
            long b = readA.executeI64(frame);
            long resultL = a * b;
            long resultH = LongMultiplication.multiplyHigh(a, b);
            writeA.executeI64(frame, resultL);
            writeD.executeI64(frame, resultH);
            boolean overflow = resultH != 0 && resultH != -1;
            writeCF.execute(frame, overflow);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, resultL < 0);
            writePF.execute(frame, Flags.getParity((byte) resultL));
            return next();
        }
    }

    private static abstract class Imul2 extends Imul {
        @Child protected ReadNode readOp1;
        @Child protected ReadNode readOp2;
        @Child protected WriteNode writeDst;

        private final Operand srcA;
        private final Operand srcB;
        private final Operand dst;

        protected Imul2(long pc, byte[] instruction, OperandDecoder operands, int type) {
            super(pc, instruction, operands.getOperand2(type), operands.getOperand1(type));
            this.dst = operand1;
            this.srcA = operand1;
            this.srcB = operand2;

            setGPRReadOperands(srcA, srcB);
            setGPRWriteOperands(dst);
        }

        protected Imul2(long pc, byte[] instruction, OperandDecoder operands, short imm, int type) {
            super(pc, instruction, operands.getOperand2(type), operands.getOperand1(type), new ImmediateOperand(imm));
            this.dst = operand1;
            this.srcA = operand2;
            this.srcB = operand3;

            setGPRReadOperands(srcA, srcB);
            setGPRWriteOperands(dst);
        }

        protected Imul2(long pc, byte[] instruction, OperandDecoder operands, int imm, int type) {
            super(pc, instruction, operands.getOperand2(type), operands.getOperand1(type), new ImmediateOperand(imm));
            this.dst = operand1;
            this.srcA = operand2;
            this.srcB = operand3;

            setGPRReadOperands(srcA, srcB);
            setGPRWriteOperands(dst);
        }

        protected Imul2(long pc, byte[] instruction, OperandDecoder operands, long imm, int type) {
            super(pc, instruction, operands.getOperand2(type), operands.getOperand1(type), new ImmediateOperand(imm));
            this.dst = operand1;
            this.srcA = operand2;
            this.srcB = operand3;

            setGPRReadOperands(srcA, srcB);
            setGPRWriteOperands(dst);
        }

        @Override
        protected void createChildNodes() {
            ArchitecturalState state = getState();
            readOp1 = srcA.createRead(state, next());
            readOp2 = srcB.createRead(state, next());
            writeDst = dst.createWrite(state, next());
            createFlagNodes(state);
        }
    }

    public static class Imulw extends Imul2 {
        public Imulw(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands, OperandDecoder.R16);
        }

        public Imulw(long pc, byte[] instruction, OperandDecoder operands, short imm) {
            super(pc, instruction, operands, imm, OperandDecoder.R16);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            short op1 = readOp1.executeI16(frame);
            short op2 = readOp2.executeI16(frame);
            int result = op1 * op2;
            writeDst.executeI16(frame, (short) result);
            boolean overflow = result != (short) result;
            writeCF.execute(frame, overflow);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, (short) result < 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            return next();
        }
    }

    public static class Imull extends Imul2 {
        public Imull(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands, OperandDecoder.R32);
        }

        public Imull(long pc, byte[] instruction, OperandDecoder operands, int imm) {
            super(pc, instruction, operands, imm, OperandDecoder.R32);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            int op1 = readOp1.executeI32(frame);
            int op2 = readOp2.executeI32(frame);
            long result = (long) op1 * (long) op2;
            writeDst.executeI32(frame, (int) result);
            boolean overflow = result != (int) result;
            writeCF.execute(frame, overflow);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, (int) result < 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            return next();
        }
    }

    public static class Imulq extends Imul2 {
        public Imulq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands, OperandDecoder.R64);
        }

        public Imulq(long pc, byte[] instruction, OperandDecoder operands, long imm) {
            super(pc, instruction, operands, imm, OperandDecoder.R64);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long op1 = readOp1.executeI64(frame);
            long op2 = readOp2.executeI64(frame);
            long resultL = op1 * op2;
            long resultH = LongMultiplication.multiplyHigh(op1, op2);
            writeDst.executeI64(frame, resultL);
            boolean ok1 = resultH != 0 || resultL >= 0; // resultH == 0 -> resultL >= 0;
            boolean ok2 = resultH != -1 || resultL < 0; // resultH == -1 -> resultL < 0;
            boolean ok3 = resultH == 0 || resultH == -1;
            boolean overflow = !(ok1 && ok2 && ok3);
            writeCF.execute(frame, overflow);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, resultL < 0);
            writePF.execute(frame, Flags.getParity((byte) resultL));
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        if (operand3 != null) {
            return new String[]{"imul", operand1.toString(), operand2.toString(), operand3.toString()};
        } else if (operand2 != null) {
            return new String[]{"imul", operand1.toString(), operand2.toString()};
        } else {
            return new String[]{"imul", operand1.toString()};
        }
    }
}
