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
package org.graalvm.vm.x86;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

import org.graalvm.vm.memory.MemoryOptions;
import org.graalvm.vm.memory.hardware.MMU;
import org.graalvm.vm.util.log.Trace;
import org.graalvm.vm.x86.node.InterpreterThreadRootNode;
import org.graalvm.vm.x86.node.debug.trace.ExecutionTraceWriter;
import org.graalvm.vm.x86.node.debug.trace.LogStreamHandler;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;

public abstract class AMD64Language extends TruffleLanguage<AMD64Context> {
    private static final Logger log = Trace.create(AMD64Language.class);

    public static final String MIME_TYPE = "application/x-executable";

    private static final boolean DEBUG = Options.getBoolean(Options.DEBUG_EXEC);
    private static final boolean DEBUG_TRACE = Options.getBoolean(Options.DEBUG_EXEC_TRACE);

    protected FrameDescriptor fd = new FrameDescriptor();

    @Override
    protected AMD64Context createContext(Env env) {
        try {
            // no need to load native extension if Java memory is used
            if (!MemoryOptions.MEM_VIRTUAL.get()) {
                // try to load libmemory from language home
                MMU.loadLibrary(getLanguageHome() + "/libmemory.so");
            }
        } catch (UnsatisfiedLinkError e) {
            // ignore
        }
        if (DEBUG && DEBUG_TRACE) {
            String traceFile = Options.getString(Options.DEBUG_EXEC_TRACEFILE);
            log.info("Opening trace file " + traceFile);
            try {
                OutputStream out = new BufferedOutputStream(new FileOutputStream(traceFile));
                ExecutionTraceWriter trace = new ExecutionTraceWriter(out);
                LogStreamHandler handler = new LogStreamHandler(trace);
                Logger.getLogger("").addHandler(handler);
                return new AMD64Context(this, env, fd, trace, handler);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return new AMD64Context(this, env, fd);
        }
    }

    @Override
    protected void initializeContext(AMD64Context ctx) {
        InterpreterThreadRootNode interpreter = new InterpreterThreadRootNode(this, fd);
        ctx.setInterpreter(Truffle.getRuntime().createCallTarget(interpreter));
        ctx.initialize();
    }

    @Override
    protected void initializeMultiThreading(AMD64Context context) {
        context.getSingleThreadedAssumption().invalidate();
    }

    @Override
    protected boolean patchContext(AMD64Context ctx, Env env) {
        ctx.patch(env);
        return true;
    }

    @Override
    protected boolean isObjectOfLanguage(Object object) {
        return false;
    }

    @Override
    protected void disposeContext(AMD64Context ctx) {
        ExecutionTraceWriter trace = ctx.getTraceWriter();
        Logger.getLogger("").removeHandler(ctx.getLogHandler());
        if (trace != null) {
            try {
                trace.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static ContextReference<AMD64Context> getCurrentContextReference() {
        return getCurrentLanguage(AMD64Language.class).getContextReference();
    }

    public static TruffleLanguage<AMD64Context> getCurrentLanguage() {
        return getCurrentLanguage(AMD64Language.class);
    }

    public static String getHome() {
        return getCurrentLanguage(AMD64Language.class).getLanguageHome();
    }

    @Override
    protected boolean isThreadAccessAllowed(Thread thread, boolean singleThreaded) {
        return true;
    }
}
