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
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Xchg extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child protected ReadNode readOp1;
    @Child protected ReadNode readOp2;
    @Child protected WriteNode writeOp1;
    @Child protected WriteNode writeOp2;

    protected Xchg(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;

        setGPRReadOperands(operand1, operand2);
        setGPRWriteOperands(operand1, operand2);
    }

    @Override
    protected void createChildNodes() {
        ArchitecturalState state = getState();
        readOp1 = operand1.createRead(state, next());
        readOp2 = operand2.createRead(state, next());
        writeOp1 = operand1.createWrite(state, next());
        writeOp2 = operand2.createWrite(state, next());
    }

    public static class Xchgb extends Xchg {
        public Xchgb(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R8), operands.getOperand2(OperandDecoder.R8));
        }

        public Xchgb(long pc, byte[] instruction, Operand operand1, Operand operand2) {
            super(pc, instruction, operand1, operand2);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            byte a = readOp1.executeI8(frame);
            byte b = readOp2.executeI8(frame);
            writeOp1.executeI8(frame, b);
            writeOp2.executeI8(frame, a);
            return next();
        }
    }

    public static class Xchgw extends Xchg {
        public Xchgw(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R16), operands.getOperand2(OperandDecoder.R16));
        }

        public Xchgw(long pc, byte[] instruction, Operand operand1, Operand operand2) {
            super(pc, instruction, operand1, operand2);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            short a = readOp1.executeI16(frame);
            short b = readOp2.executeI16(frame);
            writeOp1.executeI16(frame, b);
            writeOp2.executeI16(frame, a);
            return next();
        }
    }

    public static class Xchgl extends Xchg {
        public Xchgl(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R32), operands.getOperand2(OperandDecoder.R32));
        }

        public Xchgl(long pc, byte[] instruction, Operand operand1, Operand operand2) {
            super(pc, instruction, operand1, operand2);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            int a = readOp1.executeI32(frame);
            int b = readOp2.executeI32(frame);
            writeOp1.executeI32(frame, b);
            writeOp2.executeI32(frame, a);
            return next();
        }
    }

    public static class Xchgq extends Xchg {
        public Xchgq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R64), operands.getOperand2(OperandDecoder.R64));
        }

        public Xchgq(long pc, byte[] instruction, Operand operand1, Operand operand2) {
            super(pc, instruction, operand1, operand2);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long a = readOp1.executeI64(frame);
            long b = readOp2.executeI64(frame);
            writeOp1.executeI64(frame, b);
            writeOp2.executeI64(frame, a);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"xchg", operand1.toString(), operand2.toString()};
    }
}
