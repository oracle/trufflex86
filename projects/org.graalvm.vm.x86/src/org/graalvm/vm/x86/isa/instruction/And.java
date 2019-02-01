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

public abstract class And extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child protected ReadNode readOperand1;
    @Child protected ReadNode readOperand2;
    @Child protected WriteNode writeResult;
    @Child protected WriteFlagNode writeCF;
    @Child protected WriteFlagNode writePF;
    @Child protected WriteFlagNode writeZF;
    @Child protected WriteFlagNode writeSF;
    @Child protected WriteFlagNode writeOF;

    protected And(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;

        setGPRReadOperands(operand1, operand2);
        setGPRWriteOperands(operand1);
    }

    @Override
    protected void createChildNodes() {
        assert readOperand1 == null;
        assert readOperand2 == null;
        ArchitecturalState state = getState();
        readOperand1 = operand1.createRead(state, next());
        readOperand2 = operand2.createRead(state, next());
        writeResult = operand1.createWrite(state, next());
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

    public static class Andb extends And {
        public Andb(long pc, byte[] instruction, OperandDecoder decoder) {
            this(pc, instruction, decoder, false);
        }

        public Andb(long pc, byte[] instruction, OperandDecoder decoder, boolean swap) {
            super(pc, instruction, getOp1(decoder, OperandDecoder.R8, swap), getOp2(decoder, OperandDecoder.R8, swap));
        }

        public Andb(long pc, byte[] instruction, OperandDecoder decoder, byte imm) {
            super(pc, instruction, decoder.getOperand1(OperandDecoder.R8), new ImmediateOperand(imm));
        }

        public Andb(long pc, byte[] instruction, Operand operand, byte imm) {
            super(pc, instruction, operand, new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            byte a = readOperand1.executeI8(frame);
            byte b = readOperand2.executeI8(frame);
            byte val = (byte) (a & b);
            writeResult.executeI8(frame, val);
            writeCF.execute(frame, false);
            writeOF.execute(frame, false);
            writeZF.execute(frame, val == 0);
            writeSF.execute(frame, val < 0);
            writePF.execute(frame, Flags.getParity(val));
            return next();
        }
    }

    public static class Andw extends And {
        public Andw(long pc, byte[] instruction, OperandDecoder decoder) {
            this(pc, instruction, decoder, false);
        }

        public Andw(long pc, byte[] instruction, OperandDecoder decoder, boolean swap) {
            super(pc, instruction, getOp1(decoder, OperandDecoder.R16, swap), getOp2(decoder, OperandDecoder.R16, swap));
        }

        public Andw(long pc, byte[] instruction, OperandDecoder decoder, short imm) {
            super(pc, instruction, decoder.getOperand1(OperandDecoder.R16), new ImmediateOperand(imm));
        }

        public Andw(long pc, byte[] instruction, Operand operand, short imm) {
            super(pc, instruction, operand, new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            short a = readOperand1.executeI16(frame);
            short b = readOperand2.executeI16(frame);
            short val = (short) (a & b);
            writeResult.executeI16(frame, val);
            writeCF.execute(frame, false);
            writeOF.execute(frame, false);
            writeZF.execute(frame, val == 0);
            writeSF.execute(frame, val < 0);
            writePF.execute(frame, Flags.getParity((byte) val));
            return next();
        }
    }

    public static class Andl extends And {
        public Andl(long pc, byte[] instruction, OperandDecoder decoder) {
            this(pc, instruction, decoder, false);
        }

        public Andl(long pc, byte[] instruction, OperandDecoder decoder, boolean swap) {
            super(pc, instruction, getOp1(decoder, OperandDecoder.R32, swap), getOp2(decoder, OperandDecoder.R32, swap));
        }

        public Andl(long pc, byte[] instruction, OperandDecoder decoder, int imm) {
            super(pc, instruction, decoder.getOperand1(OperandDecoder.R32), new ImmediateOperand(imm));
        }

        public Andl(long pc, byte[] instruction, Operand operand, int imm) {
            super(pc, instruction, operand, new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            int a = readOperand1.executeI32(frame);
            int b = readOperand2.executeI32(frame);
            int val = a & b;
            writeResult.executeI32(frame, val);
            writeCF.execute(frame, false);
            writeOF.execute(frame, false);
            writeZF.execute(frame, val == 0);
            writeSF.execute(frame, val < 0);
            writePF.execute(frame, Flags.getParity((byte) val));
            return next();
        }
    }

    public static class Andq extends And {
        public Andq(long pc, byte[] instruction, OperandDecoder decoder) {
            this(pc, instruction, decoder, false);
        }

        public Andq(long pc, byte[] instruction, OperandDecoder decoder, boolean swap) {
            super(pc, instruction, getOp1(decoder, OperandDecoder.R64, swap), getOp2(decoder, OperandDecoder.R64, swap));
        }

        public Andq(long pc, byte[] instruction, OperandDecoder decoder, long imm) {
            super(pc, instruction, decoder.getOperand1(OperandDecoder.R64), new ImmediateOperand(imm));
        }

        public Andq(long pc, byte[] instruction, Operand operand, long imm) {
            super(pc, instruction, operand, new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long a = readOperand1.executeI64(frame);
            long b = readOperand2.executeI64(frame);
            long val = a & b;
            writeResult.executeI64(frame, val);
            writeCF.execute(frame, false);
            writeOF.execute(frame, false);
            writeZF.execute(frame, val == 0);
            writeSF.execute(frame, val < 0);
            writePF.execute(frame, Flags.getParity((byte) val));
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"and", operand1.toString(), operand2.toString()};
    }
}
