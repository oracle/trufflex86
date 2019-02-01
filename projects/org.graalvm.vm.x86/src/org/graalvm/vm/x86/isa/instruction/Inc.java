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
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteFlagNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Inc extends AMD64Instruction {
    private final Operand operand;

    @Child protected ReadNode read;
    @Child protected WriteNode write;

    @Child protected WriteFlagNode writeOF;
    @Child protected WriteFlagNode writeSF;
    @Child protected WriteFlagNode writeZF;
    @Child protected WriteFlagNode writeAF;
    @Child protected WriteFlagNode writePF;

    protected Inc(long pc, byte[] instruction, Operand operand) {
        super(pc, instruction);
        this.operand = operand;

        setGPRReadOperands(operand);
        setGPRWriteOperands(operand);
    }

    @Override
    protected void createChildNodes() {
        assert read == null;
        assert write == null;

        ArchitecturalState state = getState();
        RegisterAccessFactory regs = state.getRegisters();
        read = operand.createRead(state, next());
        write = operand.createWrite(state, next());
        writeOF = regs.getOF().createWrite();
        writeSF = regs.getSF().createWrite();
        writeZF = regs.getZF().createWrite();
        writeAF = regs.getAF().createWrite();
        writePF = regs.getPF().createWrite();
    }

    public static class Incb extends Inc {
        public Incb(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R8));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            byte val = read.executeI8(frame);
            byte result = (byte) (val + 1);
            write.executeI8(frame, result);

            boolean overflow = result < 0 && val > 0;
            boolean adjust = ((val ^ result) & 0x10) != 0;

            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writeZF.execute(frame, result == 0);
            writePF.execute(frame, Flags.getParity(result));
            writeAF.execute(frame, adjust);
            return next();
        }
    }

    public static class Incw extends Inc {
        public Incw(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R16));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            short val = read.executeI16(frame);
            short result = (short) (val + 1);
            write.executeI16(frame, result);

            boolean overflow = result < 0 && val > 0;
            boolean adjust = ((val ^ result) & 0x10) != 0;

            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writeZF.execute(frame, result == 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            writeAF.execute(frame, adjust);
            return next();
        }
    }

    public static class Incl extends Inc {
        public Incl(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R32));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            int val = read.executeI32(frame);
            int result = val + 1;
            write.executeI32(frame, result);

            boolean overflow = result < 0 && val > 0;
            boolean adjust = ((val ^ result) & 0x10) != 0;

            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writeZF.execute(frame, result == 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            writeAF.execute(frame, adjust);
            return next();
        }
    }

    public static class Incq extends Inc {
        public Incq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R64));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long val = read.executeI64(frame);
            long result = val + 1;
            write.executeI64(frame, result);

            boolean overflow = result < 0 && val > 0;
            boolean adjust = ((val ^ result) & 0x10) != 0;

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
        return new String[]{"inc", operand.toString()};
    }
}
