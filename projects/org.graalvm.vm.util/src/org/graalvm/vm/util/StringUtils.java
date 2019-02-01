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

public class StringUtils {
    public static String repeat(String s, int count) {
        StringBuffer result = new StringBuffer(count * s.length());
        for (int i = 0; i < count; i++) {
            result.append(s);
        }
        return result.toString();
    }

    public static String fit(String s, int width) {
        if (s.length() > width) {
            if (width <= 7) {
                return s.substring(0, width);
            }
            int center = width / 2 + ((width & 1) == 0 ? 0 : 1);
            int p1 = center - 2;
            int p2 = s.length() - width + center + 1;
            String s1 = s.substring(0, p1);
            String s2 = s.substring(p2);
            return s1 + "..." + s2;
        } else {
            return s + repeat(" ", width - s.length());
        }
    }

    public static String pad(String s, int width) {
        return pad(s, width, true);
    }

    public static String rpad(String s, int width) {
        return pad(s, width, false);
    }

    public static String pad(String s, int width, boolean first) {
        if (s.length() > width) {
            if (first) {
                if (width <= 3) {
                    return s.substring(0, width);
                }
                return s.substring(0, width - 3) + "...";
            } else {
                if (width <= 3) {
                    return s.substring(s.length() - width);
                }
                return "..." + s.substring(s.length() - width + 3);
            }
        } else {
            return s + repeat(" ", width - s.length());
        }
    }

    public static String padWithOverflow(String s, int width) {
        if (s.length() >= width) {
            return s;
        } else {
            return s + repeat(" ", width - s.length());
        }
    }

    public static String tab(String s, int tabSize) {
        int pos = 0;
        StringBuilder buf = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c == '\t') {
                do {
                    pos++;
                    pos %= tabSize;
                    buf.append(' ');
                } while (pos != 0);
            } else if (c == '\n') {
                pos = 0;
                buf.append(c);
            } else {
                pos++;
                pos %= tabSize;
                buf.append(c);
            }
        }
        return buf.toString();
    }
}
