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
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.isa.RegisterOperand;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteFlagNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Cmpxchg extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child protected ReadNode readA;
    @Child protected ReadNode readSrc;
    @Child protected ReadNode readDst;
    @Child protected WriteNode writeA;
    @Child protected WriteNode writeDst;
    @Child protected WriteFlagNode writeZF;
    @Child protected WriteFlagNode writeCF;
    @Child protected WriteFlagNode writePF;
    @Child protected WriteFlagNode writeAF;
    @Child protected WriteFlagNode writeSF;
    @Child protected WriteFlagNode writeOF;

    protected Cmpxchg(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;

        setGPRReadOperands(operand1, operand2, new RegisterOperand(Register.RAX));
        setGPRWriteOperands(operand1, new RegisterOperand(Register.RAX));
    }

    protected void createChildNodes(int size) {
        Register a = null;
        switch (size) {
            case OperandDecoder.R8:
                a = Register.AL;
                break;
            case OperandDecoder.R16:
                a = Register.AX;
                break;
            case OperandDecoder.R32:
                a = Register.EAX;
                break;
            case OperandDecoder.R64:
                a = Register.RAX;
                break;
            default:
                throw new IllegalArgumentException();
        }
        ArchitecturalState state = getState();
        readA = state.getRegisters().getRegister(a).createRead();
        readSrc = operand2.createRead(state, next());
        readDst = operand1.createRead(state, next());
        writeA = state.getRegisters().getRegister(a).createWrite();
        writeDst = operand1.createWrite(state, next());
        writeZF = state.getRegisters().getZF().createWrite();
        writeCF = state.getRegisters().getCF().createWrite();
        writePF = state.getRegisters().getPF().createWrite();
        writeAF = state.getRegisters().getAF().createWrite();
        writeSF = state.getRegisters().getSF().createWrite();
        writeOF = state.getRegisters().getOF().createWrite();
    }

    public static class Cmpxchgb extends Cmpxchg {
        public Cmpxchgb(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R8), operands.getOperand2(OperandDecoder.R8));
        }

        @Override
        protected void createChildNodes() {
            createChildNodes(OperandDecoder.R8);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            byte al = readA.executeI8(frame);
            byte src = readSrc.executeI8(frame);
            byte dst = readDst.executeI8(frame);
            if (al == dst) {
                writeZF.execute(frame, true);
                writeDst.executeI8(frame, src);
            } else {
                writeZF.execute(frame, false);
                writeA.executeI8(frame, dst);
                writeDst.executeI8(frame, dst); // always write dst
            }

            byte result = (byte) (al - dst);

            boolean overflow = ((al ^ dst) & (al ^ result)) < 0;
            boolean carry = Byte.toUnsignedInt(al) < Byte.toUnsignedInt(dst);
            boolean adjust = (((al ^ dst) ^ result) & 0x10) != 0;

            writeCF.execute(frame, carry);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writeZF.execute(frame, result == 0);
            writePF.execute(frame, Flags.getParity(result));
            writeAF.execute(frame, adjust);
            return next();
        }
    }

    public static class Cmpxchgw extends Cmpxchg {
        public Cmpxchgw(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R16), operands.getOperand2(OperandDecoder.R16));
        }

        @Override
        protected void createChildNodes() {
            createChildNodes(OperandDecoder.R16);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            short ax = readA.executeI16(frame);
            short src = readSrc.executeI16(frame);
            short dst = readDst.executeI16(frame);
            if (ax == dst) {
                writeZF.execute(frame, true);
                writeDst.executeI16(frame, src);
            } else {
                writeZF.execute(frame, false);
                writeA.executeI16(frame, dst);
                writeDst.executeI16(frame, dst); // always write dst
            }

            short result = (short) (ax - dst);

            boolean overflow = ((ax ^ dst) & (ax ^ result)) < 0;
            boolean carry = Short.toUnsignedInt(ax) < Short.toUnsignedInt(dst);
            boolean adjust = (((ax ^ dst) ^ result) & 0x10) != 0;

            writeCF.execute(frame, carry);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writeZF.execute(frame, result == 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            writeAF.execute(frame, adjust);
            return next();
        }
    }

    public static class Cmpxchgl extends Cmpxchg {
        public Cmpxchgl(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R32), operands.getOperand2(OperandDecoder.R32));
        }

        @Override
        protected void createChildNodes() {
            createChildNodes(OperandDecoder.R32);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            int eax = readA.executeI32(frame);
            int src = readSrc.executeI32(frame);
            int dst = readDst.executeI32(frame);
            if (eax == dst) {
                writeZF.execute(frame, true);
                writeDst.executeI32(frame, src);
            } else {
                writeZF.execute(frame, false);
                writeA.executeI32(frame, dst);
                writeDst.executeI32(frame, dst); // always write dst
            }

            int result = eax - dst;

            boolean overflow = ((eax ^ dst) & (eax ^ result)) < 0;
            boolean carry = Integer.compareUnsigned(eax, dst) < 0;
            boolean adjust = (((eax ^ dst) ^ result) & 0x10) != 0;

            writeCF.execute(frame, carry);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writeZF.execute(frame, result == 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            writeAF.execute(frame, adjust);
            return next();
        }
    }

    public static class Cmpxchgq extends Cmpxchg {
        public Cmpxchgq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R64), operands.getOperand2(OperandDecoder.R64));
        }

        @Override
        protected void createChildNodes() {
            createChildNodes(OperandDecoder.R64);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long rax = readA.executeI64(frame);
            long src = readSrc.executeI64(frame);
            long dst = readDst.executeI64(frame);
            if (rax == dst) {
                writeZF.execute(frame, true);
                writeDst.executeI64(frame, src);
            } else {
                writeZF.execute(frame, false);
                writeA.executeI64(frame, dst);
                writeDst.executeI64(frame, dst); // always write dst
            }

            long result = rax - dst;

            boolean overflow = ((rax ^ dst) & (rax ^ result)) < 0;
            boolean carry = Long.compareUnsigned(rax, dst) < 0;
            boolean adjust = (((rax ^ dst) ^ result) & 0x10) != 0;

            writeCF.execute(frame, carry);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writeZF.execute(frame, result == 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            writeAF.execute(frame, adjust);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"cmpxchg", operand1.toString(), operand2.toString()};
    }
}
