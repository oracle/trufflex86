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
package org.graalvm.vm.x86.node;

import java.util.logging.Logger;

import org.graalvm.vm.memory.exception.SegmentationViolation;
import org.graalvm.vm.posix.api.ProcessExitException;
import org.graalvm.vm.posix.api.Signal;
import org.graalvm.vm.util.log.Trace;
import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.CpuRuntimeException;
import org.graalvm.vm.x86.Options;
import org.graalvm.vm.x86.isa.CpuState;
import org.graalvm.vm.x86.isa.IllegalInstructionException;
import org.graalvm.vm.x86.node.flow.AbstractDispatchNode;
import org.graalvm.vm.x86.node.flow.DispatchNode;
import org.graalvm.vm.x86.node.flow.InterTraceDispatchNode;
import org.graalvm.vm.x86.node.flow.RetException;
import org.graalvm.vm.x86.node.init.InitializeFromCpuStateNode;
import org.graalvm.vm.x86.node.init.InitializerNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

public class InterpreterThreadRootNode extends AMD64RootNode {
    private static final Logger log = Trace.create(InterpreterRootNode.class);

    private static final boolean SIMPLE_DISPATCH = Options.getBoolean(Options.SIMPLE_DISPATCH) || Options.getBoolean(Options.DEBUG_EXEC);

    @Child private InitializerNode initializer;
    @Child private AbstractDispatchNode interpreter;
    @Child private InitializeFromCpuStateNode writeState = new InitializeFromCpuStateNode();

    public InterpreterThreadRootNode(TruffleLanguage<AMD64Context> language, FrameDescriptor fd) {
        super(language, fd);
        ArchitecturalState state = language.getContextReference().get().getState();
        if (SIMPLE_DISPATCH) {
            log.warning("Using old and slow dispatch node");
            interpreter = new DispatchNode(state);
        } else {
            interpreter = new InterTraceDispatchNode(state);
        }
    }

    @Override
    public Object execute(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        CpuState state = (CpuState) args[0];

        writeState.execute(frame, state);

        try {
            while (true) {
                try {
                    return interpreter.execute(frame);
                } catch (RetException e) {
                    writeState.execute(frame, e.getState());
                    continue;
                }
            }
        } catch (ProcessExitException e) {
            return e.getCode();
        } catch (CpuRuntimeException e) {
            CompilerDirectives.transferToInterpreter();
            if (e.getCause() instanceof IllegalInstructionException) {
                return 128 + Signal.SIGILL;
            } else if (e.getCause() instanceof SegmentationViolation) {
                return 128 + Signal.SIGSEGV;
            } else {
                return 127;
            }
        } catch (Throwable t) {
            CompilerDirectives.transferToInterpreter();
            t.printStackTrace(Trace.log);
            return 127;
        }
    }
}
