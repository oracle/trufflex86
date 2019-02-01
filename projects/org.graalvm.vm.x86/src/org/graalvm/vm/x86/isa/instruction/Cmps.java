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
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.isa.RegisterOperand;
import org.graalvm.vm.x86.node.MemoryReadNode;
import org.graalvm.vm.x86.node.ReadFlagNode;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteFlagNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Cmps extends AMD64Instruction {
    private final String name;

    @Child protected ReadNode readRSI;
    @Child protected ReadNode readRDI;
    @Child protected MemoryReadNode readMemory;
    @Child protected ReadFlagNode readDF;
    @Child protected WriteNode writeRSI;
    @Child protected WriteNode writeRDI;
    @Child protected WriteFlagNode writeCF;
    @Child protected WriteFlagNode writeOF;
    @Child protected WriteFlagNode writeSF;
    @Child protected WriteFlagNode writeZF;
    @Child protected WriteFlagNode writePF;
    @Child protected WriteFlagNode writeAF;

    protected Cmps(long pc, byte[] instruction, String name) {
        super(pc, instruction);
        this.name = name;

        setGPRReadOperands(new RegisterOperand(Register.RSI), new RegisterOperand(Register.RDI));
        setGPRWriteOperands(new RegisterOperand(Register.RSI), new RegisterOperand(Register.RDI));
    }

    @Override
    protected void createChildNodes() {
        ArchitecturalState state = getState();
        RegisterAccessFactory regs = state.getRegisters();
        readRSI = regs.getRegister(Register.RSI).createRead();
        readRDI = regs.getRegister(Register.RDI).createRead();
        readDF = regs.getDF().createRead();
        readMemory = state.createMemoryRead();
        writeRSI = regs.getRegister(Register.RSI).createWrite();
        writeRDI = regs.getRegister(Register.RDI).createWrite();
        writeCF = regs.getCF().createWrite();
        writeOF = regs.getOF().createWrite();
        writeSF = regs.getSF().createWrite();
        writeZF = regs.getZF().createWrite();
        writePF = regs.getPF().createWrite();
        writeAF = regs.getAF().createWrite();
    }

    public static class Cmpsb extends Cmps {
        public Cmpsb(long pc, byte[] instruction) {
            super(pc, instruction, "cmpsb");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long rsi = readRSI.executeI64(frame);
            long rdi = readRDI.executeI64(frame);
            byte a = readMemory.executeI8(rsi);
            byte b = readMemory.executeI8(rdi);

            byte result = (byte) (a - b);

            boolean overflow = (byte) ((a ^ b) & (a ^ result)) < 0;
            boolean carry = Byte.toUnsignedInt(a) < Byte.toUnsignedInt(b);
            boolean adjust = (((a ^ b) ^ result) & 0x10) != 0;

            writeCF.execute(frame, carry);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writeZF.execute(frame, result == 0);
            writePF.execute(frame, Flags.getParity(result));
            writeAF.execute(frame, adjust);

            boolean df = readDF.execute(frame);
            if (df) {
                rsi--;
                rdi--;
            } else {
                rsi++;
                rdi++;
            }

            writeRSI.executeI64(frame, rsi);
            writeRDI.executeI64(frame, rdi);

            return next();
        }
    }

    public static class Cmpsw extends Cmps {
        public Cmpsw(long pc, byte[] instruction) {
            super(pc, instruction, "cmpsw");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long rsi = readRSI.executeI64(frame);
            long rdi = readRDI.executeI64(frame);
            short a = readMemory.executeI16(rsi);
            short b = readMemory.executeI16(rdi);

            short result = (short) (a - b);

            boolean overflow = (short) ((a ^ b) & (a ^ result)) < 0;
            boolean carry = Short.toUnsignedInt(a) < Short.toUnsignedInt(b);
            boolean adjust = (((a ^ b) ^ result) & 0x10) != 0;

            writeCF.execute(frame, carry);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writeZF.execute(frame, result == 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            writeAF.execute(frame, adjust);

            boolean df = readDF.execute(frame);
            if (df) {
                rsi -= 2;
                rdi -= 2;
            } else {
                rsi += 2;
                rdi += 2;
            }

            writeRSI.executeI64(frame, rsi);
            writeRDI.executeI64(frame, rdi);

            return next();
        }
    }

    public static class Cmpsd extends Cmps {
        public Cmpsd(long pc, byte[] instruction) {
            super(pc, instruction, "cmpsd");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long rsi = readRSI.executeI64(frame);
            long rdi = readRDI.executeI64(frame);
            int a = readMemory.executeI32(rsi);
            int b = readMemory.executeI32(rdi);

            int result = a - b;

            boolean overflow = ((a ^ b) & (a ^ result)) < 0;
            boolean carry = Integer.compareUnsigned(a, b) < 0;
            boolean adjust = (((a ^ b) ^ result) & 0x10) != 0;

            writeCF.execute(frame, carry);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writeZF.execute(frame, result == 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            writeAF.execute(frame, adjust);

            boolean df = readDF.execute(frame);
            if (df) {
                rsi -= 4;
                rdi -= 4;
            } else {
                rsi += 4;
                rdi += 4;
            }

            writeRSI.executeI64(frame, rsi);
            writeRDI.executeI64(frame, rdi);

            return next();
        }
    }

    public static class Cmpsq extends Cmps {
        public Cmpsq(long pc, byte[] instruction) {
            super(pc, instruction, "cmpsq");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long rsi = readRSI.executeI64(frame);
            long rdi = readRDI.executeI64(frame);
            long a = readMemory.executeI64(rsi);
            long b = readMemory.executeI64(rdi);

            long result = a - b;

            boolean overflow = ((a ^ b) & (a ^ result)) < 0;
            boolean carry = Long.compareUnsigned(a, b) < 0;
            boolean adjust = (((a ^ b) ^ result) & 0x10) != 0;

            writeCF.execute(frame, carry);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writeZF.execute(frame, result == 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            writeAF.execute(frame, adjust);

            boolean df = readDF.execute(frame);
            if (df) {
                rsi -= 8;
                rdi -= 8;
            } else {
                rsi += 8;
                rdi += 8;
            }

            writeRSI.executeI64(frame, rsi);
            writeRDI.executeI64(frame, rdi);

            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{name};
    }
}
