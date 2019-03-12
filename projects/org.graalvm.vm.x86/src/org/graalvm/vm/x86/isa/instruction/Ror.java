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
import org.graalvm.vm.x86.isa.ImmediateOperand;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteFlagNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.profiles.ConditionProfile;

public abstract class Ror extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child protected ReadNode readSrc;
    @Child protected ReadNode readShamt;
    @Child protected WriteNode writeDst;
    @Child protected WriteFlagNode writeCF;
    @Child protected WriteFlagNode writeOF;

    protected final ConditionProfile profileGTZ = ConditionProfile.createCountingProfile();
    protected final ConditionProfile profileEQO = ConditionProfile.createCountingProfile();

    protected Ror(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;

        setGPRReadOperands(operand1, operand2);
        setGPRWriteOperands(operand1);
    }

    @Override
    protected void createChildNodes() {
        ArchitecturalState state = getState();
        readSrc = operand1.createRead(state, next());
        readShamt = operand2.createRead(state, next());
        writeDst = operand1.createWrite(state, next());
        writeCF = state.getRegisters().getCF().createWrite();
        writeOF = state.getRegisters().getOF().createWrite();
    }

    public static class Rorb extends Ror {
        public Rorb(long pc, byte[] instruction, OperandDecoder operands, Operand op2) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R8), op2);
        }

        public Rorb(long pc, byte[] instruction, OperandDecoder operands, byte imm) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R8), new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            int src = readSrc.executeI8(frame) & 0xFF;
            int shift = readShamt.executeI8(frame) & 0x7;
            byte result = (byte) ((src >>> shift) | (src << (8 - shift)));
            writeDst.executeI8(frame, result);
            if (profileGTZ.profile(shift > 0)) {
                boolean cf = ((src >>> (shift - 1)) & 1) != 0;
                writeCF.execute(frame, cf);
            }
            if (profileEQO.profile(shift == 1)) {
                boolean of = (result < 0) ^ ((result & 0x40) != 0);
                writeOF.execute(frame, of);
            }
            return next();
        }
    }

    public static class Rorw extends Ror {
        public Rorw(long pc, byte[] instruction, OperandDecoder operands, Operand op2) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R16), op2);
        }

        public Rorw(long pc, byte[] instruction, OperandDecoder operands, byte imm) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R16), new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            int src = readSrc.executeI16(frame) & 0xFFFF;
            int shift = readShamt.executeI8(frame) & 0xF;
            short result = (short) ((src >>> shift) | (src << (16 - shift)));
            writeDst.executeI16(frame, result);
            if (profileGTZ.profile(shift > 0)) {
                boolean cf = ((src >>> (shift - 1)) & 1) != 0;
                writeCF.execute(frame, cf);
            }
            if (profileEQO.profile(shift == 1)) {
                boolean of = (result < 0) ^ ((result & 0x4000) != 0);
                writeOF.execute(frame, of);
            }
            return next();
        }
    }

    public static class Rorl extends Ror {
        public Rorl(long pc, byte[] instruction, OperandDecoder operands, Operand op2) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R32), op2);
        }

        public Rorl(long pc, byte[] instruction, OperandDecoder operands, byte imm) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R32), new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            int src = readSrc.executeI32(frame);
            int shift = readShamt.executeI8(frame) & 0x1F;
            int result = (src >>> shift) | (src << (32 - shift));
            writeDst.executeI32(frame, result);
            if (profileGTZ.profile(shift > 0)) {
                boolean cf = ((src >>> (shift - 1)) & 1) != 0;
                writeCF.execute(frame, cf);
            }
            if (profileEQO.profile(shift == 1)) {
                boolean of = (result < 0) ^ ((result & 0x40000000) != 0);
                writeOF.execute(frame, of);
            }
            return next();
        }
    }

    public static class Rorq extends Ror {
        public Rorq(long pc, byte[] instruction, OperandDecoder operands, Operand op2) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R64), op2);
        }

        public Rorq(long pc, byte[] instruction, OperandDecoder operands, byte imm) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R64), new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long src = readSrc.executeI64(frame);
            int shift = readShamt.executeI8(frame) & 0x3F;
            long result = (src >>> shift) | (src << (64 - shift));
            writeDst.executeI64(frame, result);
            if (profileGTZ.profile(shift > 0)) {
                boolean cf = ((src >>> (shift - 1)) & 1) != 0;
                writeCF.execute(frame, cf);
            }
            if (profileEQO.profile(shift == 1)) {
                boolean of = (result < 0) ^ ((result & 0x4000000000000000L) != 0);
                writeOF.execute(frame, of);
            }
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"ror", operand1.toString(), operand2.toString()};
    }
}
