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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.graalvm.vm.util.HexFormatter;
import org.graalvm.vm.util.log.Trace;
import org.graalvm.vm.x86.node.debug.trace.StepRecord;
import org.graalvm.vm.x86.trcview.io.BlockNode;
import org.graalvm.vm.x86.trcview.ui.event.LevelPeekListener;
import org.graalvm.vm.x86.trcview.ui.event.LevelUpListener;

@SuppressWarnings("serial")
public class CallStackView extends JPanel {
    private static final Logger log = Trace.create(CallStackView.class);

    private BlockNode current;
    private List<String> callStack;
    private List<BlockNode> callStackBlocks;

    private JList<String> entries;
    private ListModel model;

    private JButton up;

    private List<LevelUpListener> listeners;
    private List<LevelPeekListener> peekListeners;

    public CallStackView() {
        super(new BorderLayout());

        listeners = new ArrayList<>();
        peekListeners = new ArrayList<>();

        up = new JButton("up");
        up.setEnabled(false);

        callStack = Collections.emptyList();
        callStackBlocks = Collections.emptyList();
        add(BorderLayout.CENTER, new JScrollPane(entries = new JList<>(model = new ListModel())));
        add(BorderLayout.SOUTH, up);

        entries.setFont(MainWindow.FONT);

        up.addActionListener(e -> {
            if (callStack.size() > 1) {
                fireUpEvent();
            }
        });

        entries.addListSelectionListener(e -> {
            if (callStack.size() > 1) {
                firePeekEvent();
            }
        });

        entries.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    fireGotoEvent();
                }
            }
        });
    }

    public void addLevelUpListener(LevelUpListener listener) {
        listeners.add(listener);
    }

    public void removeLevelUpListener(LevelUpListener listener) {
        listeners.remove(listener);
    }

    public void addLevelPeekListener(LevelPeekListener listener) {
        peekListeners.add(listener);
    }

    public void removeLevelPeekListener(LevelPeekListener listener) {
        peekListeners.remove(listener);
    }

    protected void fireUpEvent() {
        BlockNode block = callStackBlocks.get(callStackBlocks.size() - 1);
        for (LevelUpListener l : listeners) {
            try {
                l.levelUp(block);
            } catch (Throwable t) {
                log.log(Level.WARNING, "Error while running listener: " + t, t);
            }
        }
    }

    protected void firePeekEvent() {
        int selection = entries.getSelectedIndex();
        if (selection == -1) {
            return;
        }
        BlockNode block = callStackBlocks.get(selection);
        if (block == null) {
            return;
        }
        BlockNode select = null;
        if (selection + 1 < callStackBlocks.size()) {
            select = callStackBlocks.get(selection + 1);
        }
        for (LevelPeekListener l : peekListeners) {
            try {
                l.levelPeek(block, select);
            } catch (Throwable t) {
                log.log(Level.WARNING, "Error while running listener: " + t, t);
            }
        }
    }

    protected void fireGotoEvent() {
        int selection = entries.getSelectedIndex();
        if (selection == -1) {
            return;
        }
        int levels = callStackBlocks.size() - selection - 1;
        for (int i = 0; i < levels; i++) {
            fireUpEvent();
        }
    }

    private class ListModel extends AbstractListModel<String> {
        public int getSize() {
            return callStack.size();
        }

        public String getElementAt(int index) {
            return callStack.get(index);
        }

        public void changed() {
            fireContentsChanged(this, 0, getSize());
        }
    }

    private static String format(StepRecord step) {
        StringBuilder buf = new StringBuilder();
        buf.append("0x");
        buf.append(HexFormatter.tohex(step.getLocation().getPC(), 16));
        if (step.getLocation().getSymbol() != null) {
            buf.append(" <");
            buf.append(step.getLocation().getSymbol());
            buf.append('>');
        }
        buf.append(' ');
        buf.append(step.getLocation().getDisassembly().replace('\t', ' '));
        return buf.toString();
    }

    private void computeCallTrace() {
        callStack = new ArrayList<>();
        callStackBlocks = new ArrayList<>();
        BlockNode block = current;
        while (block != null && block.getHead() != null) {
            callStack.add(format(block.getHead()));
            callStackBlocks.add(block);
            block = block.getParent();
        }
        if (block != null && block.getHead() == null) {
            StepRecord first = block.getFirstStep();
            callStack.add("0x" + HexFormatter.tohex(first.getLocation().getPC(), 16) + " <_start>");
            callStackBlocks.add(block);
        }
        Collections.reverse(callStack);
        Collections.reverse(callStackBlocks);
    }

    public BlockNode get() {
        return current;
    }

    public void set(BlockNode block) {
        this.current = block;
        computeCallTrace();
        model.changed();
        entries.setSelectedIndex(callStack.size() - 1);
        up.setEnabled(callStack.size() > 1);
    }
}
