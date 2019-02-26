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

import java.util.HashSet;
import java.util.Set;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.isa.RegisterOperand;
import org.graalvm.vm.x86.node.AMD64Node;
import org.graalvm.vm.x86.node.ReadFlagNode;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.LoopNode;
import com.oracle.truffle.api.nodes.RepeatingNode;

public class Rep extends AMD64Instruction {
    private final AMD64Instruction insn; // not a child, just for lazy initialization of loop node
    @Child private LoopNode loop;
    private final String[] disasm;

    public Rep(long pc, byte[] instruction, AMD64Instruction insn) {
        this(pc, instruction, "rep", insn);
    }

    protected Rep(long pc, byte[] instruction, String name, AMD64Instruction insn) {
        super(pc, instruction);
        this.insn = insn;
        String[] asm = insn.getDisassemblyComponents();
        disasm = new String[asm.length + 1];
        disasm[0] = name;
        System.arraycopy(asm, 0, disasm, 1, asm.length);
    }

    private static interface UsesRegisters {
        public Operand[] getReadOperands();

        public Operand[] getWriteOperands();
    }

    private static class RepBody extends AMD64Node implements RepeatingNode, UsesRegisters {
        @Child private ReadNode readRCX;
        @Child private WriteNode writeRCX;
        @Child private AMD64Instruction insn;

        @CompilationFinal protected FrameSlot insncnt;

        public RepBody(ArchitecturalState state, AMD64Instruction insn) {
            this.insn = insn;
            readRCX = state.getRegisters().getRegister(Register.RCX).createRead();
            writeRCX = state.getRegisters().getRegister(Register.RCX).createWrite();
            insncnt = state.getInstructionCount();
        }

        public boolean executeRepeating(VirtualFrame frame) {
            long rcx = readRCX.executeI64(frame);
            if (rcx != 0) {
                insn.executeInstruction(frame);
                rcx--;
                writeRCX.executeI64(frame, rcx);
                long cnt = FrameUtil.getLongSafe(frame, insncnt);
                frame.setLong(insncnt, cnt + 1);
            }
            return rcx != 0;
        }

        @Override
        public Operand[] getReadOperands() {
            Register[] reads = insn.getUsedGPRRead();
            Set<Register> regs = new HashSet<>();
            for (Register r : reads) {
                regs.add(r);
            }
            regs.add(Register.RCX);
            Operand[] ops = new Operand[regs.size()];
            int i = 0;
            for (Register r : regs) {
                ops[i++] = new RegisterOperand(r);
            }
            return ops;
        }

        @Override
        public Operand[] getWriteOperands() {
            Register[] writes = insn.getUsedGPRWrite();
            Set<Register> regs = new HashSet<>();
            for (Register r : writes) {
                regs.add(r);
            }
            regs.add(Register.RCX);
            Operand[] ops = new Operand[regs.size()];
            int i = 0;
            for (Register r : regs) {
                ops[i++] = new RegisterOperand(r);
            }
            return ops;
        }

        @Override
        public String toString() {
            CompilerAsserts.neverPartOfCompilation();
            return String.format("RepBody[0x%016x]", insn.getPC());
        }
    }

    private static class RepzBody extends AMD64Node implements RepeatingNode, UsesRegisters {
        @Child private ReadNode readRCX;
        @Child private ReadFlagNode readZF;
        @Child private WriteNode writeRCX;
        @Child private AMD64Instruction insn;

        @CompilationFinal protected FrameSlot insncnt;

        public RepzBody(ArchitecturalState state, AMD64Instruction insn) {
            this.insn = insn;
            readRCX = state.getRegisters().getRegister(Register.RCX).createRead();
            writeRCX = state.getRegisters().getRegister(Register.RCX).createWrite();
            readZF = state.getRegisters().getZF().createRead();
            insncnt = state.getInstructionCount();
        }

        public boolean executeRepeating(VirtualFrame frame) {
            long rcx = readRCX.executeI64(frame);
            if (rcx != 0) {
                insn.executeInstruction(frame);
                rcx--;
                writeRCX.executeI64(frame, rcx);
                long cnt = FrameUtil.getLongSafe(frame, insncnt);
                frame.setLong(insncnt, cnt + 1);
            }
            boolean zf = readZF.execute(frame);
            return rcx != 0 && zf;
        }

        @Override
        public Operand[] getReadOperands() {
            Register[] reads = insn.getUsedGPRRead();
            Set<Register> regs = new HashSet<>();
            for (Register r : reads) {
                regs.add(r);
            }
            regs.add(Register.RCX);
            Operand[] ops = new Operand[regs.size()];
            int i = 0;
            for (Register r : regs) {
                ops[i++] = new RegisterOperand(r);
            }
            return ops;
        }

        @Override
        public Operand[] getWriteOperands() {
            Register[] writes = insn.getUsedGPRWrite();
            Set<Register> regs = new HashSet<>();
            for (Register r : writes) {
                regs.add(r);
            }
            regs.add(Register.RCX);
            Operand[] ops = new Operand[regs.size()];
            int i = 0;
            for (Register r : regs) {
                ops[i++] = new RegisterOperand(r);
            }
            return ops;
        }

        @Override
        public String toString() {
            CompilerAsserts.neverPartOfCompilation();
            return String.format("RepzBody[0x%016x]", insn.getPC());
        }
    }

    private static class RepnzBody extends AMD64Node implements RepeatingNode, UsesRegisters {
        @Child private ReadNode readRCX;
        @Child private ReadFlagNode readZF;
        @Child private WriteNode writeRCX;
        @Child private AMD64Instruction insn;

        @CompilationFinal protected FrameSlot insncnt;

        public RepnzBody(ArchitecturalState state, AMD64Instruction insn) {
            this.insn = insn;
            readRCX = state.getRegisters().getRegister(Register.RCX).createRead();
            writeRCX = state.getRegisters().getRegister(Register.RCX).createWrite();
            readZF = state.getRegisters().getZF().createRead();
            insncnt = state.getInstructionCount();
        }

        public boolean executeRepeating(VirtualFrame frame) {
            long rcx = readRCX.executeI64(frame);
            if (rcx != 0) {
                insn.executeInstruction(frame);
                rcx--;
                writeRCX.executeI64(frame, rcx);
                long cnt = FrameUtil.getLongSafe(frame, insncnt);
                frame.setLong(insncnt, cnt + 1);
            }
            boolean zf = readZF.execute(frame);
            return rcx != 0 && !zf;
        }

        @Override
        public Operand[] getReadOperands() {
            Register[] reads = insn.getUsedGPRRead();
            Set<Register> regs = new HashSet<>();
            for (Register r : reads) {
                regs.add(r);
            }
            regs.add(Register.RCX);
            Operand[] ops = new Operand[regs.size()];
            int i = 0;
            for (Register r : regs) {
                ops[i++] = new RegisterOperand(r);
            }
            return ops;
        }

        @Override
        public Operand[] getWriteOperands() {
            Register[] writes = insn.getUsedGPRWrite();
            Set<Register> regs = new HashSet<>();
            for (Register r : writes) {
                regs.add(r);
            }
            regs.add(Register.RCX);
            Operand[] ops = new Operand[regs.size()];
            int i = 0;
            for (Register r : regs) {
                ops[i++] = new RegisterOperand(r);
            }
            return ops;
        }

        @Override
        public String toString() {
            CompilerAsserts.neverPartOfCompilation();
            return String.format("RepnzBody[0x%016x]", insn.getPC());
        }
    }

    public static class Repz extends Rep {
        public Repz(long pc, byte[] instruction, AMD64Instruction insn) {
            super(pc, instruction, "repz", insn);
        }

        @Override
        protected RepeatingNode createRepeatingNode(ArchitecturalState state, AMD64Instruction body) {
            return new RepzBody(state, body);
        }
    }

    public static class Repnz extends Rep {
        public Repnz(long pc, byte[] instruction, AMD64Instruction insn) {
            super(pc, instruction, "repnz", insn);
        }

        @Override
        protected RepeatingNode createRepeatingNode(ArchitecturalState state, AMD64Instruction body) {
            return new RepnzBody(state, body);
        }
    }

    protected RepeatingNode createRepeatingNode(ArchitecturalState state, AMD64Instruction body) {
        return new RepBody(state, body);
    }

    @Override
    protected void createChildNodes() {
        insn.createChildren();
        ArchitecturalState state = getState();
        RepeatingNode body = createRepeatingNode(state, insn);
        UsesRegisters r = (UsesRegisters) body;
        setGPRReadOperands(r.getReadOperands());
        setGPRWriteOperands(r.getWriteOperands());
        loop = insert(Truffle.getRuntime().createLoopNode(body));
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        loop.executeLoop(frame);
        return next();
    }

    @Override
    protected String[] disassemble() {
        return disasm;
    }
}
