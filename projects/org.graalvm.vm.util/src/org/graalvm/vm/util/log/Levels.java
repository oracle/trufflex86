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

import java.util.logging.Level;

public class Levels {
    public static final Level FATAL = create("FATAL", 1000, 'F');
    public static final Level ERROR = create("ERROR", 950, 'E');
    public static final Level WARNING = create("WARNING", 900, 'W');
    public static final Level AUDIT = create("AUDIT", 850, 'A');
    public static final Level INFO = create("INFO", 800, 'I');
    public static final Level DEBUG = create("DEBUG", 500, 'D');
    public static final Level STDOUT = create("STDOUT", 1000, 'O');
    public static final Level STDERR = create("STDERR", 1000, 'R');

    private static final Level[] LEVELS = {DEBUG, INFO, AUDIT, WARNING, ERROR, FATAL};

    static Level create(String name, int value, char letter) {
        return new LogLevel(name, value, letter);
    }

    public static Level get(Level level) {
        if (level instanceof LogLevel) {
            return level;
        }
        int value = level.intValue();
        for (int i = 0; i < LEVELS.length; i++) {
            int lvl = LEVELS[i].intValue();
            if (value <= lvl) {
                return LEVELS[i];
            }
        }
        return level;
    }
}
