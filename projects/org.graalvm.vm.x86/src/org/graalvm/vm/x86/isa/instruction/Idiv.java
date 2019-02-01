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

import org.graalvm.vm.math.LongDivision;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.isa.RegisterOperand;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Idiv extends AMD64Instruction {
    private static final String DIV_ZERO = "division by zero";
    private static final String DIV_RANGE = "quotient too large";

    protected final Operand operand;

    @Child protected ReadNode readOperand;
    @Child protected ReadNode readA;
    @Child protected ReadNode readD;
    @Child protected WriteNode writeA;
    @Child protected WriteNode writeD;

    protected Idiv(long pc, byte[] instruction, Operand operand) {
        super(pc, instruction);
        this.operand = operand;
    }

    protected void createChildNodes(Register a, Register d) {
        ArchitecturalState state = getState();
        readOperand = operand.createRead(state, next());
        readA = state.getRegisters().getRegister(a).createRead();
        readD = state.getRegisters().getRegister(d).createRead();
        writeA = state.getRegisters().getRegister(a).createWrite();
        writeD = state.getRegisters().getRegister(d).createWrite();
    }

    public static class Idivb extends Idiv {
        public Idivb(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R8));

            setGPRReadOperands(operand, new RegisterOperand(Register.RAX));
        }

        @Override
        protected void createChildNodes() {
            ArchitecturalState state = getState();
            readOperand = operand.createRead(state, next());
            readA = state.getRegisters().getRegister(Register.AX).createRead();
            writeA = state.getRegisters().getRegister(Register.AL).createWrite();
            writeD = state.getRegisters().getRegister(Register.AH).createWrite();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            byte divisor = readOperand.executeI8(frame);
            if (divisor == 0) {
                CompilerDirectives.transferToInterpreter();
                throw new ArithmeticException(DIV_ZERO);
            }
            short dividend = readA.executeI16(frame);
            short quotient = (short) (dividend / divisor);
            byte remainder = (byte) (dividend % divisor);
            if ((byte) quotient != quotient) {
                CompilerDirectives.transferToInterpreter();
                throw new ArithmeticException(DIV_RANGE);
            }
            writeA.executeI8(frame, (byte) quotient);
            writeD.executeI8(frame, remainder);
            return next();
        }
    }

    public static class Idivw extends Idiv {
        public Idivw(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R16));

            setGPRReadOperands(operand, new RegisterOperand(Register.RAX), new RegisterOperand(Register.RDX));
            setGPRWriteOperands(new RegisterOperand(Register.RAX), new RegisterOperand(Register.RDX));
        }

        @Override
        protected void createChildNodes() {
            createChildNodes(Register.AX, Register.DX);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            short divisor = readOperand.executeI16(frame);
            if (divisor == 0) {
                CompilerDirectives.transferToInterpreter();
                throw new ArithmeticException(DIV_ZERO);
            }
            short dividendLow = readA.executeI16(frame);
            short dividendHigh = readD.executeI16(frame);
            int dividend = (Short.toUnsignedInt(dividendHigh) << 16) | Short.toUnsignedInt(dividendLow);
            int quotient = dividend / divisor;
            short remainder = (short) (dividend % divisor);
            if ((short) quotient != quotient) {
                CompilerDirectives.transferToInterpreter();
                throw new ArithmeticException(DIV_RANGE);
            }
            writeA.executeI16(frame, (short) quotient);
            writeD.executeI16(frame, remainder);
            return next();
        }
    }

    public static class Idivl extends Idiv {
        public Idivl(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R32));

            setGPRReadOperands(operand, new RegisterOperand(Register.RAX), new RegisterOperand(Register.RDX));
            setGPRWriteOperands(new RegisterOperand(Register.RAX), new RegisterOperand(Register.RDX));
        }

        @Override
        protected void createChildNodes() {
            createChildNodes(Register.EAX, Register.EDX);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            int divisor = readOperand.executeI32(frame);
            if (divisor == 0) {
                CompilerDirectives.transferToInterpreter();
                throw new ArithmeticException(DIV_ZERO);
            }
            int dividendLow = readA.executeI32(frame);
            int dividendHigh = readD.executeI32(frame);
            long dividend = (Integer.toUnsignedLong(dividendHigh) << 32) | Integer.toUnsignedLong(dividendLow);
            long quotient = dividend / divisor;
            int remainder = (int) (dividend % divisor);
            if ((int) quotient != quotient) {
                CompilerDirectives.transferToInterpreter();
                throw new ArithmeticException(DIV_RANGE);
            }
            writeA.executeI32(frame, (int) quotient);
            writeD.executeI32(frame, remainder);
            return next();
        }
    }

    public static class Idivq extends Idiv {
        public Idivq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R64));

            setGPRReadOperands(operand, new RegisterOperand(Register.RAX), new RegisterOperand(Register.RDX));
            setGPRWriteOperands(new RegisterOperand(Register.RAX), new RegisterOperand(Register.RDX));
        }

        @Override
        protected void createChildNodes() {
            createChildNodes(Register.RAX, Register.RDX);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long divisor = readOperand.executeI64(frame);
            if (divisor == 0) {
                CompilerDirectives.transferToInterpreter();
                throw new ArithmeticException(DIV_ZERO);
            }
            long dividendLow = readA.executeI64(frame);
            long dividendHigh = readD.executeI64(frame);
            LongDivision.Result result = LongDivision.divs128by64(dividendHigh, dividendLow, divisor);
            if (result.isInvalid()) {
                CompilerDirectives.transferToInterpreter();
                throw new ArithmeticException(DIV_RANGE);
            }
            long quotient = result.quotient;
            long remainder = result.remainder;
            writeA.executeI64(frame, quotient);
            writeD.executeI64(frame, remainder);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"idiv", operand.toString()};
    }
}
