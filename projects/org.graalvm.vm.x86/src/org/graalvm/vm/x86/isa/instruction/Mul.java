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
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.isa.RegisterOperand;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteFlagNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Mul extends AMD64Instruction {
    protected final Operand operand;

    @Child protected WriteFlagNode writeCF;
    @Child protected WriteFlagNode writeOF;
    @Child protected WriteFlagNode writeSF;
    @Child protected WriteFlagNode writePF;

    protected Mul(long pc, byte[] instruction, Operand operand) {
        super(pc, instruction);
        this.operand = operand;
    }

    protected void createWriteFlagNodes() {
        ArchitecturalState state = getState();
        RegisterAccessFactory regs = state.getRegisters();
        writeCF = regs.getCF().createWrite();
        writeOF = regs.getOF().createWrite();
        writeSF = regs.getSF().createWrite();
        writePF = regs.getPF().createWrite();
    }

    public static class Mulb extends Mul {
        @Child private ReadNode readAL;
        @Child private ReadNode readOp;
        @Child private WriteNode writeAX;

        public Mulb(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R8));

            setGPRReadOperands(operand, new RegisterOperand(Register.RAX));
            setGPRWriteOperands(new RegisterOperand(Register.RAX));
        }

        @Override
        protected void createChildNodes() {
            createWriteFlagNodes();
            ArchitecturalState state = getState();
            readAL = state.getRegisters().getRegister(Register.AL).createRead();
            readOp = operand.createRead(state, next());
            writeAX = state.getRegisters().getRegister(Register.AX).createWrite();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            byte al = readAL.executeI8(frame);
            byte op = readOp.executeI8(frame);
            int result = Byte.toUnsignedInt(al) * Byte.toUnsignedInt(op);
            writeAX.executeI16(frame, (short) result);
            boolean of = (byte) (result >> 8) != 0;
            writeCF.execute(frame, of);
            writeOF.execute(frame, of);
            writeSF.execute(frame, (byte) result < 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            return next();
        }
    }

    public static class Mulw extends Mul {
        @Child private ReadNode readAX;
        @Child private ReadNode readOp;
        @Child private WriteNode writeAX;
        @Child private WriteNode writeDX;

        public Mulw(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R16));

            setGPRReadOperands(operand, new RegisterOperand(Register.RAX));
            setGPRWriteOperands(new RegisterOperand(Register.RAX), new RegisterOperand(Register.RDX));
        }

        @Override
        protected void createChildNodes() {
            createWriteFlagNodes();
            ArchitecturalState state = getState();
            readAX = state.getRegisters().getRegister(Register.AX).createRead();
            readOp = operand.createRead(state, next());
            writeAX = state.getRegisters().getRegister(Register.AX).createWrite();
            writeDX = state.getRegisters().getRegister(Register.DX).createWrite();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            short ax = readAX.executeI16(frame);
            short op = readOp.executeI16(frame);
            int result = Short.toUnsignedInt(ax) * Short.toUnsignedInt(op);
            short resultL = (short) result;
            short resultH = (short) (result >> 16);
            writeAX.executeI16(frame, resultL);
            writeDX.executeI16(frame, resultH);
            boolean of = resultH != 0;
            writeCF.execute(frame, of);
            writeOF.execute(frame, of);
            writeSF.execute(frame, resultL < 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            return next();
        }
    }

    public static class Mull extends Mul {
        @Child private ReadNode readEAX;
        @Child private ReadNode readOp;
        @Child private WriteNode writeEAX;
        @Child private WriteNode writeEDX;

        public Mull(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R32));

            setGPRReadOperands(operand, new RegisterOperand(Register.RAX));
            setGPRWriteOperands(new RegisterOperand(Register.RAX), new RegisterOperand(Register.RDX));
        }

        @Override
        protected void createChildNodes() {
            createWriteFlagNodes();
            ArchitecturalState state = getState();
            readEAX = state.getRegisters().getRegister(Register.EAX).createRead();
            readOp = operand.createRead(state, next());
            writeEAX = state.getRegisters().getRegister(Register.EAX).createWrite();
            writeEDX = state.getRegisters().getRegister(Register.EDX).createWrite();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            int eax = readEAX.executeI32(frame);
            int op = readOp.executeI32(frame);
            long result = Integer.toUnsignedLong(eax) * Integer.toUnsignedLong(op);
            int resultL = (int) result;
            int resultH = (int) (result >> 32);
            writeEAX.executeI32(frame, resultL);
            writeEDX.executeI32(frame, resultH);
            boolean of = resultH != 0;
            writeCF.execute(frame, of);
            writeOF.execute(frame, of);
            writeSF.execute(frame, resultL < 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            return next();
        }
    }

    public static class Mulq extends Mul {
        @Child private ReadNode readRAX;
        @Child private ReadNode readOp;
        @Child private WriteNode writeRAX;
        @Child private WriteNode writeRDX;

        public Mulq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R64));

            setGPRReadOperands(operand, new RegisterOperand(Register.RAX));
            setGPRWriteOperands(new RegisterOperand(Register.RAX), new RegisterOperand(Register.RDX));
        }

        @Override
        protected void createChildNodes() {
            createWriteFlagNodes();
            ArchitecturalState state = getState();
            readRAX = state.getRegisters().getRegister(Register.RAX).createRead();
            readOp = operand.createRead(state, next());
            writeRAX = state.getRegisters().getRegister(Register.RAX).createWrite();
            writeRDX = state.getRegisters().getRegister(Register.RDX).createWrite();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long rax = readRAX.executeI64(frame);
            long op = readOp.executeI64(frame);
            long resultL = rax * op;
            long resultH = LongMultiplication.multiplyHighUnsigned(rax, op);
            writeRAX.executeI64(frame, resultL);
            writeRDX.executeI64(frame, resultH);
            boolean of = resultH != 0;
            writeCF.execute(frame, of);
            writeOF.execute(frame, of);
            writeSF.execute(frame, resultL < 0);
            writePF.execute(frame, Flags.getParity((byte) resultL));
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"mul", operand.toString()};
    }
}
