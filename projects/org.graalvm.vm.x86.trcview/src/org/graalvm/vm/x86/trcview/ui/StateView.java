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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.graalvm.vm.x86.node.debug.trace.CallArgsRecord;
import org.graalvm.vm.x86.node.debug.trace.StepRecord;

@SuppressWarnings("serial")
public class StateView extends JPanel {
    private static final String STYLE = "<style>\n" +
                    "html, body, pre {\n" +
                    "    padding: 0;\n" +
                    "    margin: 0;\n" +
                    "}\n" +
                    "pre {\n" +
                    "    font-family: " + Font.MONOSPACED + ";\n" +
                    "    font-size: 11pt;\n" +
                    "}\n" +
                    ".change {\n" +
                    "    color: red;\n" +
                    "}\n" +
                    ".comment {\n" +
                    "    color: " + color(Color.LIGHT_GRAY) + ";\n" +
                    "}\n" +
                    ".symbol {\n" +
                    "    color: " + color(Color.BLUE) + ";\n" +
                    "}\n" +
                    ".mnemonic {\n" +
                    "    color: " + color(Color.BLUE) + ";\n" +
                    "}\n" +
                    "</style>";
    private JTextPane text;

    private StepRecord step;
    private StepRecord previous;
    private CallArgsRecord args;

    public StateView() {
        super(new BorderLayout());
        text = new JTextPane();
        text.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        text.setEditable(false);
        text.setContentType("text/html");
        add(BorderLayout.CENTER, new JScrollPane(text));
    }

    private static String color(Color color) {
        return "rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")";
    }

    public void setState(StepRecord step) {
        this.previous = null;
        this.step = step;
        this.args = null;
        update();
    }

    public void setState(StepRecord previous, StepRecord current) {
        this.previous = previous;
        this.step = current;
        this.args = null;
        update();
    }

    public void setCallArguments(CallArgsRecord args) {
        this.args = args;
        update();
    }

    private static String encodeHTML(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;");
    }

    private String get() {
        if (previous != null) {
            return StateEncoder.encode(previous, step);
        } else {
            return StateEncoder.encode(step);
        }
    }

    private void update() {
        String content;
        if (step != null) {
            if (args != null) {
                content = get() + "\n\n" + encodeHTML(args.toString());
            } else {
                content = get();
            }
        } else {
            content = "";
        }
        String html = "<html><head>" + STYLE + "</head><body><pre>" + content + "</pre></body></html>";
        text.setText(html);
        text.setCaretPosition(0);
    }
}
