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
package org.graalvm.vm.util.log;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.graalvm.vm.util.StackTraceUtil;
import org.graalvm.vm.util.StringUtils;

public class LogFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        String src = StringUtils.rpad(record.getSourceClassName() + "#" + record.getSourceMethodName(), 60);
        if (record.getSourceClassName() == null || record.getSourceMethodName() == null) {
            if (record.getLoggerName() != null) {
                src = StringUtils.rpad("<" + record.getLoggerName() + ">", 60);
            } else {
                src = StringUtils.rpad("unknown source", 60);
            }
        }
        Level lvl = Levels.get(record.getLevel());
        char level = 'Z';
        if (lvl instanceof LogLevel) {
            level = ((LogLevel) lvl).getLetter();
        }
        String error = "";
        if (record.getThrown() != null) {
            error = "\n" + StackTraceUtil.getStackTrace(record.getThrown());
        }
        String threadID = String.format("%08x", record.getThreadID());
        String line = String.format("[%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS:%1$tL] %2$s %3$s %4$c %5$s%6$s%n",
                        new Date(record.getMillis()), threadID, src, level, record.getMessage(), error);
        return line;
    }
}
