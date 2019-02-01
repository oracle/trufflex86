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
package org.graalvm.vm.x86.node.flow;

import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.CpuState;
import org.graalvm.vm.x86.node.AMD64Node;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.init.CopyToCpuStateNode;
import org.graalvm.vm.x86.node.init.InitializeFromCpuStateNode;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

public class CompiledTraceInterpreter extends AMD64Node {
    private final RootCallTarget callTarget;
    private final InterTraceCallTarget interpreter;

    @Child private ReadNode readPC;

    @Child private CopyToCpuStateNode readState = new CopyToCpuStateNode();
    @Child private InitializeFromCpuStateNode writeState = new InitializeFromCpuStateNode();

    public CompiledTraceInterpreter(TruffleLanguage<AMD64Context> language, FrameDescriptor fd) {
        interpreter = new InterTraceCallTarget(language, fd);
        callTarget = Truffle.getRuntime().createCallTarget(interpreter);

        ArchitecturalState state = language.getContextReference().get().getState();
        readPC = state.getRegisters().getPC().createRead();
    }

    public RootCallTarget getCallTarget() {
        return callTarget;
    }

    public long execute(VirtualFrame frame) {
        long pc = readPC.executeI64(frame);
        CpuState state = readState.execute(frame, pc);
        try {
            state = (CpuState) callTarget.call(state);
        } catch (RetException e) {
            state = e.getState();
        }
        writeState.execute(frame, state);
        return state.rip;
    }

    public long execute(VirtualFrame frame, long pc) {
        CpuState state = readState.execute(frame, pc);
        try {
            state = (CpuState) callTarget.call(state);
        } catch (RetException e) {
            state = e.getState();
        }
        writeState.execute(frame, state);
        return state.rip;
    }
}
