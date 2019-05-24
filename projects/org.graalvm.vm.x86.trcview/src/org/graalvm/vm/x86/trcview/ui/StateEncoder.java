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
package org.graalvm.vm.x86.trcview.ui;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.graalvm.vm.util.HexFormatter;
import org.graalvm.vm.util.StringUtils;
import org.graalvm.vm.x86.node.debug.trace.CpuStateRecord;
import org.graalvm.vm.x86.node.debug.trace.LocationRecord;
import org.graalvm.vm.x86.node.debug.trace.StepRecord;

public class StateEncoder {
    private static String html(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;");
    }

    public static String diff(String s1, String s2) {
        StringBuilder buf = new StringBuilder();
        String[] lines1 = s1.split("\\n");
        String[] lines2 = s2.split("\\n");
        if (lines1.length != lines2.length) {
            return html(s2);
        }
        for (int i = 0; i < lines1.length; i++) {
            String l1 = lines1[i];
            String l2 = lines2[i];
            if (l1.length() != l2.length()) {
                buf.append(html(l2)).append('\n');
            } else if (l1.equals(l2)) {
                buf.append(html(l2)).append('\n');
            } else {
                boolean eq = true;
                for (int j = 0; j < l1.length(); j++) {
                    char c1 = l1.charAt(j);
                    char c2 = l2.charAt(j);
                    if (c1 == c2) {
                        if (!eq) {
                            eq = true;
                            buf.append("</span>");
                        }
                    } else {
                        if (eq) {
                            eq = false;
                            buf.append("<span class=\"change\">");
                        }
                    }
                    buf.append(c2);
                }
                if (!eq) {
                    buf.append("</span>");
                }
                buf.append('\n');
            }
        }
        return buf.toString();
    }

    private static String str(String s, String style) {
        if (s == null) {
            return "";
        } else {
            if (style != null) {
                return "<span class=\"" + style + "\">" + html(s) + "</span>";
            } else {
                return html(s);
            }
        }
    }

    private static String pad(String s, int cnt) {
        int c = cnt - s.length();
        if (c < 1) {
            c = 1;
        }
        return StringUtils.repeat(" ", c);
    }

    private static String getDisassembly(LocationRecord loc) {
        String[] assembly = loc.getAssembly();
        if (assembly == null) {
            return null;
        }
        if (assembly.length == 1) {
            return str(assembly[0], "mnemonic");
        } else {
            return str(assembly[0], "mnemonic") + pad(assembly[0], 8) + html(Stream.of(assembly).skip(1).collect(Collectors.joining(",")));
        }
    }

    private static String encode(LocationRecord location) {
        StringBuilder buf = new StringBuilder();
        buf.append("IN: ");
        buf.append(str(location.getSymbol(), "symbol"));
        if (location.getFilename() != null) {
            buf.append(" # ");
            buf.append(html(location.getFilename()));
            buf.append(" @ 0x");
            buf.append(HexFormatter.tohex(location.getOffset(), 8));
        }
        buf.append("\n0x");
        buf.append(HexFormatter.tohex(location.getPC(), 8));
        buf.append(":\t");
        if (location.getAssembly() != null) {
            buf.append(getDisassembly(location));
            buf.append(" <span class=\"comment\">; ");
            buf.append(html(location.getPrintableBytes()));
            buf.append("</span>");
        }
        return buf.toString();
    }

    public static String encode(StepRecord step) {
        LocationRecord location = step.getLocation();
        CpuStateRecord state = step.getState();
        String loc = encode(location);
        return loc + "\n\n" + html(state.toString());
    }

    public static String encode(StepRecord previous, StepRecord current) {
        LocationRecord location = current.getLocation();
        CpuStateRecord state1 = previous.getState();
        CpuStateRecord state2 = current.getState();
        String loc = encode(location);
        return loc + "\n\n" + diff(state1.toString(), state2.toString());
    }
}
