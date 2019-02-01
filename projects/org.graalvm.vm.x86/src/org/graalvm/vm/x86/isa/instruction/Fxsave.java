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
import org.graalvm.vm.x86.isa.AVXRegisterOperand;
import org.graalvm.vm.x86.isa.MemoryOperand;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.AddressComputationNode;
import org.graalvm.vm.x86.node.MemoryWriteNode;
import org.graalvm.vm.x86.node.ReadNode;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;

public class Fxsave extends AMD64Instruction {
    private final MemoryOperand operand;

    @Child private AddressComputationNode address;
    @Child private MemoryWriteNode memory;
    @Children private ReadNode[] readXMM;

    private Fxsave(long pc, byte[] instruction, Operand operand) {
        super(pc, instruction);
        this.operand = (MemoryOperand) operand;

        Operand[] readOperands = new Operand[16];
        for (int i = 0; i < readOperands.length; i++) {
            readOperands[i] = new AVXRegisterOperand(i, 128);
        }
        setGPRReadOperands(readOperands);
        setGPRWriteOperands(operand);
    }

    public Fxsave(long pc, byte[] instruction, OperandDecoder operands) {
        this(pc, instruction, operands.getOperand1(OperandDecoder.R64));
    }

    @Override
    protected void createChildNodes() {
        ArchitecturalState state = getState();
        address = new AddressComputationNode(state, operand, next());
        memory = state.createMemoryWrite();
        readXMM = new ReadNode[16];
        for (int i = 0; i < readXMM.length; i++) {
            readXMM[i] = state.getRegisters().getAVXRegister(i).createRead();
        }
    }

    @Override
    @ExplodeLoop
    public long executeInstruction(VirtualFrame frame) {
        long addr = address.execute(frame);
        long ptr = addr + 160;
        for (int i = 0; i < readXMM.length; i++) {
            Vector128 xmm = readXMM[i].executeI128(frame);
            memory.executeI128(ptr, xmm);
            ptr += 16;
        }

        memory.executeI16(addr, (short) 0x037F); // default FCW
        memory.executeI32(addr + 24, 0x1F80); // default MXCSR
        ptr = addr + 32;
        for (int i = 0; i < 8; i++) { // set ST/MM registers to zero
            memory.executeI64(ptr + 16 * i, 0);
            memory.executeI64(ptr + 16 * i + 8, 0);
        }
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"fxsave", operand.toString()};
    }
}
