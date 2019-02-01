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

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.IllegalInstructionException;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Cmpps extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    private final String name;
    private final byte type;

    @Child protected ReadNode readSrc1;
    @Child protected ReadNode readSrc2;
    @Child protected WriteNode writeDst;

    protected Cmpps(long pc, byte[] instruction, Operand operand1, Operand operand2, String name, byte type) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.name = name;
        this.type = type;

        setGPRReadOperands(operand1, operand2);
        setGPRWriteOperands(operand1);
    }

    public static Cmpps create(long pc, byte[] instruction, OperandDecoder operands, byte imm) {
        switch (imm & 0x7) {
            case 1:
                return new Cmpltps(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128));
            case 2:
                return new Cmpleps(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128));
            case 6:
                return new Cmpnleps(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128));
        }
        throw new IllegalInstructionException(pc, instruction, "unknown type " + imm);
    }

    @Override
    protected void createChildNodes() {
        ArchitecturalState state = getState();
        readSrc1 = operand1.createRead(state, next());
        readSrc2 = operand2.createRead(state, next());
        writeDst = operand1.createWrite(state, next());
    }

    public static class Cmpltps extends Cmpps {
        protected Cmpltps(long pc, byte[] instruction, Operand operand1, Operand operand2) {
            super(pc, instruction, operand1, operand2, "cmpltps", (byte) 2);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            Vector128 a = readSrc1.executeI128(frame);
            Vector128 b = readSrc2.executeI128(frame);
            Vector128 le = a.ltF32(b);
            writeDst.executeI128(frame, le);
            return next();
        }
    }

    public static class Cmpleps extends Cmpps {
        protected Cmpleps(long pc, byte[] instruction, Operand operand1, Operand operand2) {
            super(pc, instruction, operand1, operand2, "cmpleps", (byte) 2);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            Vector128 a = readSrc1.executeI128(frame);
            Vector128 b = readSrc2.executeI128(frame);
            Vector128 le = a.leF32(b);
            writeDst.executeI128(frame, le);
            return next();
        }
    }

    public static class Cmpnleps extends Cmpps {
        protected Cmpnleps(long pc, byte[] instruction, Operand operand1, Operand operand2) {
            super(pc, instruction, operand1, operand2, "cmnleps", (byte) 2);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            Vector128 a = readSrc1.executeI128(frame);
            Vector128 b = readSrc2.executeI128(frame);
            Vector128 le = a.gtF32(b);
            writeDst.executeI128(frame, le);
            return next();
        }

    }

    @Override
    protected String[] disassemble() {
        if (name == null) {
            return new String[]{"cmpps", operand1.toString(), operand2.toString(), Byte.toString(type)};
        } else {
            return new String[]{name, operand1.toString(), operand2.toString()};
        }
    }
}
