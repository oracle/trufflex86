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

import org.graalvm.vm.x86.RegisterAccessFactory;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.node.ReadFlagNode;
import org.graalvm.vm.x86.node.RegisterReadNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Jcc extends AMD64Instruction {
    private final String name;
    protected final long bta;

    protected Jcc(long pc, byte[] instruction, int offset, String name) {
        super(pc, instruction);
        this.bta = getPC() + getSize() + offset;
        this.name = name;
    }

    public static class Ja extends Jcc {
        @Child private ReadFlagNode readCF;
        @Child private ReadFlagNode readZF;

        public Ja(long pc, byte[] instruction, int offset) {
            super(pc, instruction, offset, "ja");
        }

        @Override
        protected void createChildNodes() {
            RegisterAccessFactory regs = getState().getRegisters();
            readCF = regs.getCF().createRead();
            readZF = regs.getZF().createRead();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean cf = readCF.execute(frame);
            boolean zf = readZF.execute(frame);
            return (!cf && !zf) ? bta : next();
        }
    }

    public static class Jae extends Jcc {
        @Child private ReadFlagNode readCF;

        public Jae(long pc, byte[] instruction, int offset) {
            super(pc, instruction, offset, "jae");
        }

        @Override
        protected void createChildNodes() {
            RegisterAccessFactory regs = getState().getRegisters();
            readCF = regs.getCF().createRead();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean cf = readCF.execute(frame);
            return !cf ? bta : next();
        }
    }

    public static class Jb extends Jcc {
        @Child private ReadFlagNode readCF;

        public Jb(long pc, byte[] instruction, int offset) {
            super(pc, instruction, offset, "jb");
        }

        @Override
        protected void createChildNodes() {
            RegisterAccessFactory regs = getState().getRegisters();
            readCF = regs.getCF().createRead();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean cf = readCF.execute(frame);
            return cf ? bta : next();
        }
    }

    public static class Jbe extends Jcc {
        @Child private ReadFlagNode readCF;
        @Child private ReadFlagNode readZF;

        public Jbe(long pc, byte[] instruction, int offset) {
            super(pc, instruction, offset, "jbe");
        }

        @Override
        protected void createChildNodes() {
            RegisterAccessFactory regs = getState().getRegisters();
            readCF = regs.getCF().createRead();
            readZF = regs.getZF().createRead();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean cf = readCF.execute(frame);
            boolean zf = readZF.execute(frame);
            return (cf || zf) ? bta : next();
        }
    }

    public static class Jrcxz extends Jcc {
        @Child private RegisterReadNode readRCX;

        public Jrcxz(long pc, byte[] instruction, int offset) {
            super(pc, instruction, offset, "jrcxz");
        }

        @Override
        protected void createChildNodes() {
            RegisterAccessFactory regs = getState().getRegisters();
            readRCX = regs.getRegister(Register.RCX).createRead();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long value = readRCX.executeI64(frame);
            return (value == 0) ? bta : next();
        }
    }

    public static class Je extends Jcc {
        @Child private ReadFlagNode readZF;

        public Je(long pc, byte[] instruction, int offset) {
            super(pc, instruction, offset, "je");
        }

        @Override
        protected void createChildNodes() {
            RegisterAccessFactory regs = getState().getRegisters();
            readZF = regs.getZF().createRead();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean zf = readZF.execute(frame);
            return zf ? bta : next();
        }
    }

    public static class Jg extends Jcc {
        @Child private ReadFlagNode readSF;
        @Child private ReadFlagNode readZF;
        @Child private ReadFlagNode readOF;

        public Jg(long pc, byte[] instruction, int offset) {
            super(pc, instruction, offset, "jg");
        }

        @Override
        protected void createChildNodes() {
            RegisterAccessFactory regs = getState().getRegisters();
            readSF = regs.getSF().createRead();
            readZF = regs.getZF().createRead();
            readOF = regs.getOF().createRead();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean sf = readSF.execute(frame);
            boolean zf = readZF.execute(frame);
            boolean of = readOF.execute(frame);
            return (!zf && (sf == of)) ? bta : next();
        }
    }

    public static class Jge extends Jcc {
        @Child private ReadFlagNode readSF;
        @Child private ReadFlagNode readOF;

        public Jge(long pc, byte[] instruction, int offset) {
            super(pc, instruction, offset, "jge");
        }

        @Override
        protected void createChildNodes() {
            RegisterAccessFactory regs = getState().getRegisters();
            readSF = regs.getSF().createRead();
            readOF = regs.getOF().createRead();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean sf = readSF.execute(frame);
            boolean of = readOF.execute(frame);
            return (sf == of) ? bta : next();
        }
    }

    public static class Jl extends Jcc {
        @Child private ReadFlagNode readSF;
        @Child private ReadFlagNode readOF;

        public Jl(long pc, byte[] instruction, int offset) {
            super(pc, instruction, offset, "jl");
        }

        @Override
        protected void createChildNodes() {
            RegisterAccessFactory regs = getState().getRegisters();
            readSF = regs.getSF().createRead();
            readOF = regs.getOF().createRead();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean sf = readSF.execute(frame);
            boolean of = readOF.execute(frame);
            return (sf != of) ? bta : next();
        }
    }

    public static class Jle extends Jcc {
        @Child private ReadFlagNode readSF;
        @Child private ReadFlagNode readZF;
        @Child private ReadFlagNode readOF;

        public Jle(long pc, byte[] instruction, int offset) {
            super(pc, instruction, offset, "jle");
        }

        @Override
        protected void createChildNodes() {
            RegisterAccessFactory regs = getState().getRegisters();
            readSF = regs.getSF().createRead();
            readZF = regs.getZF().createRead();
            readOF = regs.getOF().createRead();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean sf = readSF.execute(frame);
            boolean zf = readZF.execute(frame);
            boolean of = readOF.execute(frame);
            return (zf || (sf != of)) ? bta : next();
        }
    }

    public static class Jne extends Jcc {
        @Child private ReadFlagNode readZF;

        public Jne(long pc, byte[] instruction, int offset) {
            super(pc, instruction, offset, "jne");
        }

        @Override
        protected void createChildNodes() {
            RegisterAccessFactory regs = getState().getRegisters();
            readZF = regs.getZF().createRead();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean zf = readZF.execute(frame);
            return !zf ? bta : next();
        }
    }

    public static class Jno extends Jcc {
        @Child private ReadFlagNode readOF;

        public Jno(long pc, byte[] instruction, int offset) {
            super(pc, instruction, offset, "jno");
        }

        @Override
        protected void createChildNodes() {
            RegisterAccessFactory regs = getState().getRegisters();
            readOF = regs.getOF().createRead();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean of = readOF.execute(frame);
            return !of ? bta : next();
        }
    }

    public static class Jnp extends Jcc {
        @Child private ReadFlagNode readPF;

        public Jnp(long pc, byte[] instruction, int offset) {
            super(pc, instruction, offset, "jnp");
        }

        @Override
        protected void createChildNodes() {
            RegisterAccessFactory regs = getState().getRegisters();
            readPF = regs.getPF().createRead();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean pf = readPF.execute(frame);
            return !pf ? bta : next();
        }
    }

    public static class Jns extends Jcc {
        @Child private ReadFlagNode readSF;

        public Jns(long pc, byte[] instruction, int offset) {
            super(pc, instruction, offset, "jns");
        }

        @Override
        protected void createChildNodes() {
            RegisterAccessFactory regs = getState().getRegisters();
            readSF = regs.getSF().createRead();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean sf = readSF.execute(frame);
            return !sf ? bta : next();
        }
    }

    public static class Jo extends Jcc {
        @Child private ReadFlagNode readOF;

        public Jo(long pc, byte[] instruction, int offset) {
            super(pc, instruction, offset, "jo");
        }

        @Override
        protected void createChildNodes() {
            RegisterAccessFactory regs = getState().getRegisters();
            readOF = regs.getOF().createRead();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean of = readOF.execute(frame);
            return of ? bta : next();
        }
    }

    public static class Jp extends Jcc {
        @Child private ReadFlagNode readPF;

        public Jp(long pc, byte[] instruction, int offset) {
            super(pc, instruction, offset, "jp");
        }

        @Override
        protected void createChildNodes() {
            RegisterAccessFactory regs = getState().getRegisters();
            readPF = regs.getPF().createRead();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean pf = readPF.execute(frame);
            return pf ? bta : next();
        }
    }

    public static class Js extends Jcc {
        @Child private ReadFlagNode readSF;

        public Js(long pc, byte[] instruction, int offset) {
            super(pc, instruction, offset, "js");
        }

        @Override
        protected void createChildNodes() {
            RegisterAccessFactory regs = getState().getRegisters();
            readSF = regs.getSF().createRead();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean sf = readSF.execute(frame);
            return sf ? bta : next();
        }
    }

    @Override
    public boolean isControlFlow() {
        return true;
    }

    @Override
    public long[] getBTA() {
        return new long[]{bta, next()};
    }

    @Override
    protected String[] disassemble() {
        return new String[]{name, String.format("0x%x", bta)};
    }
}
