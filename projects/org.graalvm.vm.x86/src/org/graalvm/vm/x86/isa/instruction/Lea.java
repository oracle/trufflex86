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
import org.graalvm.vm.x86.isa.MemoryOperand;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.AddressComputationNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Lea extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child protected AddressComputationNode address;
    @Child protected WriteNode dst;

    protected Lea(long pc, byte[] instruction, OperandDecoder operands, int type) {
        super(pc, instruction);
        this.operand1 = operands.getOperand2(type);
        this.operand2 = operands.getOperand1(type);

        assert operand2 instanceof MemoryOperand;

        setGPRReadOperands(operand2);
        setGPRWriteOperands(operand1);
    }

    @Override
    protected void createChildNodes() {
        ArchitecturalState state = getState();
        dst = operand1.createWrite(state, next());
        address = new AddressComputationNode(state, (MemoryOperand) operand2, next());
    }

    public static class Leaw extends Lea {
        public Leaw(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands, OperandDecoder.R16);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long addr = address.execute(frame);
            dst.executeI16(frame, (short) addr);
            return next();
        }
    }

    public static class Leal extends Lea {
        public Leal(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands, OperandDecoder.R32);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long addr = address.execute(frame);
            dst.executeI32(frame, (int) addr);
            return next();
        }
    }

    public static class Leaq extends Lea {
        public Leaq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands, OperandDecoder.R64);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long addr = address.execute(frame);
            dst.executeI64(frame, addr);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"lea", operand1.toString(), operand2.toString()};
    }
}
