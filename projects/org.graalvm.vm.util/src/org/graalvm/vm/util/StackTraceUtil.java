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
package org.graalvm.vm.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StackTraceUtil {
    public static String getStackTrace(Throwable e) {
        StringWriter s = new StringWriter();
        e.printStackTrace(new PrintWriter(s));
        return s.toString();
    }

    public static StackTraceElement[] getTrace(int ignore) {
        Throwable t = new Throwable();
        StackTraceElement[] trace = t.getStackTrace();
        StackTraceElement[] result = new StackTraceElement[trace.length - ignore - 1];
        System.arraycopy(trace, ignore + 1, result, 0, result.length);
        return result;
    }

    public static StackTraceElement getLocation(String[] prefixes, int ignore) {
        if (prefixes.length == 0) {
            return null;
        }
        Throwable t = new Throwable();
        StackTraceElement[] trace = t.getStackTrace();
        for (int i = ignore + 1; i < trace.length; i++) {
            StackTraceElement entry = trace[i];
            for (String prefix : prefixes) {
                if (entry.getClassName().startsWith(prefix)) {
                    return entry;
                }
            }
        }
        return null;
    }

    public static String getStackTrace(int ignore, boolean indent) {
        StringBuilder result = new StringBuilder();
        Throwable t = new Throwable();
        StackTraceElement[] trace = t.getStackTrace();
        for (int i = ignore + 1; i < trace.length; i++) {
            StackTraceElement entry = trace[i];
            if (indent) {
                result.append('\t');
            }
            result.append("at ").append(entry.getClassName()).append(".").append(entry.getMethodName());
            String file = entry.getFileName();
            if (file != null) {
                int line = entry.getLineNumber();
                result.append('(').append(file).append(':').append(line).append(')');
            } else if (entry.isNativeMethod()) {
                result.append("(Native)");
            }
            result.append('\n');
        }
        if (indent) {
            return '\t' + result.toString().trim();
        } else {
            return result.toString().trim();
        }
    }
}
