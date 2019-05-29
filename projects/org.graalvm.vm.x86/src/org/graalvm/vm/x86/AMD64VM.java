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

import static org.graalvm.vm.x86.Options.getBoolean;

import java.io.IOException;

import org.graalvm.nativeimage.ImageInfo;
import org.graalvm.nativeimage.RuntimeOptions;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.graalvm.vm.util.log.Levels;
import org.graalvm.vm.util.log.Trace;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.dsl.UnsupportedSpecializationException;

public class AMD64VM {
    private static final boolean PRINT_VM_BANNER = getBoolean(Options.STARTUP_BANNER);

    public static void main(String[] args) throws IOException {
        Trace.setupConsoleApplication(Levels.INFO);
        if (args.length == 0) {
            System.out.printf("Usage: %s program [args]\n", AMD64VM.class.getSimpleName());
            System.exit(1);
        }
        if (ImageInfo.inImageCode()) {
            RuntimeOptions.set("TruffleOSRCompilationThreshold", 10);
            RuntimeOptions.set("TruffleCompilationThreshold", 10);
        }
        Source source = Source.newBuilder(Vmx86.NAME, args[0], "<path>").build();
        System.exit(executeSource(source, args));
    }

    private static int executeSource(Source source, String[] args) {
        if (PRINT_VM_BANNER) {
            Trace.println("== running on " + Truffle.getRuntime().getName());
        }

        Context ctx = Context.newBuilder(Vmx86.NAME).arguments(Vmx86.NAME, args).allowCreateThread(true).build();

        try {
            Value result = ctx.eval(source);

            if (result == null) {
                throw new Exception("Error while executing file");
            }

            return result.asInt();
        } catch (Throwable ex) {
            /*
             * PolyglotEngine.eval wraps the actual exception in an IOException, so we have to
             * unwrap here.
             */
            Throwable cause = ex.getCause();
            if (cause instanceof UnsupportedSpecializationException) {
                cause.printStackTrace(System.err);
            } else {
                /* Unexpected error, just print out the full stack trace for debugging purposes. */
                ex.printStackTrace(System.err);
            }
            return 1;
        } finally {
            ctx.close();
        }
    }
}
