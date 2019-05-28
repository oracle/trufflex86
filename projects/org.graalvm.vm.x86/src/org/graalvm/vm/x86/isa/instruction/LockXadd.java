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
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteFlagNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class LockXadd extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child protected ReadNode srcA;
    @Child protected ReadNode srcB;
    @Child protected WriteNode src;
    @Child protected WriteNode dst;
    @Child protected WriteFlagNode writeCF;
    @Child protected WriteFlagNode writeOF;
    @Child protected WriteFlagNode writeSF;
    @Child protected WriteFlagNode writeZF;
    @Child protected WriteFlagNode writePF;

    @Override
    protected void createChildNodes() {
        ArchitecturalState state = getState();
        srcA = operand1.createRead(state, next());
        srcB = operand2.createRead(state, next());
        dst = operand1.createWrite(state, next());
        src = operand2.createWrite(state, next());
        writeCF = state.getRegisters().getCF().createWrite();
        writeOF = state.getRegisters().getOF().createWrite();
        writeSF = state.getRegisters().getSF().createWrite();
        writeZF = state.getRegisters().getZF().createWrite();
        writePF = state.getRegisters().getPF().createWrite();
    }

    protected LockXadd(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;

        setGPRReadOperands(operand1, operand2);
        setGPRWriteOperands(operand1, operand2);
    }

    public static class LockXaddb extends LockXadd {
        public LockXaddb(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R8), operands.getOperand2(OperandDecoder.R8));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean ok;
            byte a;
            byte b;
            byte result;

            do {
                a = srcA.executeI8(frame);
                b = srcB.executeI8(frame);
                result = (byte) (a + b);
                ok = dst.executeCmpxchgI8(frame, a, result);
                if (ok) {
                    src.executeI8(frame, a);
                }
            } while (!ok);

            boolean overflow = (result < 0 && a > 0 && b > 0) || (result >= 0 && a < 0 && b < 0);
            boolean carry = ((a < 0 || b < 0) && result >= 0) || (a < 0 && b < 0);
            writeCF.execute(frame, carry);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writeZF.execute(frame, result == 0);
            writePF.execute(frame, Flags.getParity(result));
            return next();
        }
    }

    public static class LockXaddw extends LockXadd {
        public LockXaddw(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R16), operands.getOperand2(OperandDecoder.R16));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean ok;
            short a;
            short b;
            short result;

            do {
                a = srcA.executeI16(frame);
                b = srcB.executeI16(frame);
                result = (short) (a + b);
                ok = dst.executeCmpxchgI16(frame, a, result);
                if (ok) {
                    src.executeI16(frame, a);
                }
            } while (!ok);

            boolean overflow = (result < 0 && a > 0 && b > 0) || (result >= 0 && a < 0 && b < 0);
            boolean carry = ((a < 0 || b < 0) && result >= 0) || (a < 0 && b < 0);
            writeCF.execute(frame, carry);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writeZF.execute(frame, result == 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            return next();
        }
    }

    public static class LockXaddl extends LockXadd {
        public LockXaddl(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R32), operands.getOperand2(OperandDecoder.R32));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean ok;
            int a;
            int b;
            int result;

            do {
                a = srcA.executeI32(frame);
                b = srcB.executeI32(frame);
                result = a + b;
                ok = dst.executeCmpxchgI32(frame, a, result);
                if (ok) {
                    src.executeI32(frame, a);
                }
            } while (!ok);

            boolean overflow = (result < 0 && a > 0 && b > 0) || (result >= 0 && a < 0 && b < 0);
            boolean carry = ((a < 0 || b < 0) && result >= 0) || (a < 0 && b < 0);
            writeCF.execute(frame, carry);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writeZF.execute(frame, result == 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            return next();
        }
    }

    public static class LockXaddq extends LockXadd {
        public LockXaddq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R64), operands.getOperand2(OperandDecoder.R64));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean ok;
            long a;
            long b;
            long result;

            do {
                a = srcA.executeI64(frame);
                b = srcB.executeI64(frame);
                result = a + b;
                ok = dst.executeCmpxchgI64(frame, a, result);
                if (ok) {
                    src.executeI64(frame, a);
                }
            } while (!ok);

            boolean overflow = (result < 0 && a > 0 && b > 0) || (result >= 0 && a < 0 && b < 0);
            boolean carry = ((a < 0 || b < 0) && result >= 0) || (a < 0 && b < 0);
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
        return new String[]{"lock xadd", operand1.toString(), operand2.toString()};
    }
}
