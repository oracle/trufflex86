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

import static org.graalvm.vm.x86.Options.getBoolean;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.Options;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.isa.RegisterOperand;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;

public class Rdtsc extends AMD64Instruction {
    @Child private WriteNode writeEAX;
    @Child private WriteNode writeEDX;

    @CompilationFinal private FrameSlot insncntslot;
    private static final boolean useInstructionCount = getBoolean(Options.RDTSC_USE_INSTRUCTION_COUNT);

    public Rdtsc(long pc, byte[] instruction) {
        super(pc, instruction);

        setGPRWriteOperands(new RegisterOperand(Register.RAX), new RegisterOperand(Register.RDX));
    }

    @Override
    protected void createChildNodes() {
        ArchitecturalState state = getState();
        writeEAX = state.getRegisters().getRegister(Register.EAX).createWrite();
        writeEDX = state.getRegisters().getRegister(Register.EDX).createWrite();

        if (useInstructionCount) {
            insncntslot = state.getInstructionCount();
        }
    }

    @TruffleBoundary
    private static long rdtsc() {
        return System.currentTimeMillis();
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        long time;
        if (useInstructionCount) {
            time = FrameUtil.getLongSafe(frame, insncntslot);
        } else {
            time = rdtsc();
        }
        int high = (int) (time >> 32);
        int low = (int) time;
        writeEAX.executeI32(frame, low);
        writeEDX.executeI32(frame, high);
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"rdtsc"};
    }
}
