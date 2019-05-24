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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.graalvm.vm.util.HexFormatter;
import org.graalvm.vm.util.StringUtils;
import org.graalvm.vm.util.log.Trace;
import org.graalvm.vm.x86.trcview.analysis.Symbol;
import org.graalvm.vm.x86.trcview.analysis.SymbolTable;
import org.graalvm.vm.x86.trcview.io.Node;
import org.graalvm.vm.x86.trcview.ui.event.JumpListener;

@SuppressWarnings("serial")
public class SymbolView extends JPanel {
    private static final Logger log = Trace.create(SymbolView.class);

    private JList<String> syms;
    private List<Symbol> symbols;
    private List<JumpListener> jumpListeners;

    public SymbolView() {
        super(new BorderLayout());
        jumpListeners = new ArrayList<>();
        symbols = Collections.emptyList();
        syms = new JList<>(new DefaultListModel<>());
        syms.setFont(MainWindow.FONT);
        syms.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int i = syms.getSelectedIndex();
                    if (i == -1) {
                        return;
                    }
                    Symbol sym = symbols.get(i);
                    log.info("jumping to first execution of " + sym.name);
                    fireJumpEvent(sym.visits.get(0));
                }
            }
        });
        add(BorderLayout.CENTER, new JScrollPane(syms));
    }

    private static final String format(Symbol sym, int width) {
        int len = 32 - sym.name.length();
        if (len < 1) {
            len = 1;
        }
        String cnt = Integer.toString(sym.visits.size());
        if (cnt.length() < width) {
            cnt = StringUtils.repeat(" ", width - cnt.length()) + cnt;
        }
        return "0x" + HexFormatter.tohex(sym.address, 12) + " [" + cnt + "] " + sym.name;
    }

    public void setSymbols(SymbolTable symbols) {
        List<Symbol> sym = new ArrayList<>();
        DefaultListModel<String> model = new DefaultListModel<>();
        OptionalInt max = symbols.getSubroutines().stream().mapToInt(s -> s.visits.size()).max();
        if (max.isPresent()) {
            int m = max.getAsInt();
            int width = (m == 0 ? 0 : (int) Math.ceil(Math.log10(m)));
            symbols.getSubroutines().stream().sorted((a, b) -> Long.compareUnsigned(a.address, b.address)).forEach(s -> {
                model.addElement(format(s, width));
                sym.add(s);
            });
        }
        syms.setModel(model);
        this.symbols = sym;
    }

    public void addJumpListener(JumpListener listener) {
        jumpListeners.add(listener);
    }

    public void removeJumpListener(JumpListener listener) {
        jumpListeners.remove(listener);
    }

    protected void fireJumpEvent(Node node) {
        for (JumpListener l : jumpListeners) {
            try {
                l.jump(node);
            } catch (Throwable t) {
                log.log(Level.WARNING, "Error while running listener: " + t, t);
            }
        }
    }
}
