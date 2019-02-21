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
package org.graalvm.vm.util.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class MessageBox {
    public static void showMessageDialog(Component root, String message, String title, int options) {
        // for copying style
        JLabel label = new JLabel();
        Font font = label.getFont();
        Color fgcolor = label.getForeground();
        Color bgcolor = label.getBackground();

        // create some css from the label's font
        StringBuffer style = new StringBuffer("font-family:" + font.getFamily() + ";");
        style.append("font-weight:" + (font.isBold() ? "bold" : "normal") + ";");
        style.append("font-size:" + font.getSize() + "pt;");
        style.append("color: rgb(" + fgcolor.getRed() + "," + fgcolor.getGreen() + "," + fgcolor.getBlue() +
                        ");");
        style.append("background-color: rgb(" + bgcolor.getRed() + "," + bgcolor.getGreen() + "," +
                        bgcolor.getBlue() + ");");
        JEditorPane ep = new JEditorPane("text/html",
                        message.replace("<body>", "<body style=\"" + style + "\">").replace("\n", "<br/>"));
        ep.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
                    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                        try {
                            desktop.browse(e.getURL().toURI());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });
        ep.setEditable(false);
        ep.setBorder(null);
        // ep.getCaret().deinstall(ep);
        JOptionPane.showMessageDialog(root, ep, title, options);
    }

    public static void showError(Component root, Throwable t) {
        t.printStackTrace();
        String message;
        if (t.getMessage() != null) {
            message = t.getClass().getSimpleName() + ": " + t.getMessage();
        } else {
            message = t.toString();
        }
        message = "<html><body>" + message.replace("&", "&amp;").replace("<", "&lt;") + "</body></html>";
        showMessageDialog(root, message, t.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
    }
}
