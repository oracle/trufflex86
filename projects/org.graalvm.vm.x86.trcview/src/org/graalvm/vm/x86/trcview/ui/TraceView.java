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
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;

import org.graalvm.vm.x86.node.debug.trace.CallArgsRecord;
import org.graalvm.vm.x86.node.debug.trace.StepRecord;
import org.graalvm.vm.x86.trcview.analysis.SymbolTable;
import org.graalvm.vm.x86.trcview.io.BlockNode;
import org.graalvm.vm.x86.trcview.io.Node;
import org.graalvm.vm.x86.trcview.io.RecordNode;
import org.graalvm.vm.x86.trcview.ui.event.CallListener;

@SuppressWarnings("serial")
public class TraceView extends JPanel {
    private SymbolView symbols;
    private CallStackView stack;
    private StateView state;
    private InstructionView insns;

    public TraceView(Consumer<String> status) {
        super(new BorderLayout());
        JSplitPane content = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        content.setLeftComponent(insns = new InstructionView(status));
        content.setRightComponent(state = new StateView());
        content.setResizeWeight(1.0);
        content.setDividerLocation(400);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        JSplitPane leftSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        leftSplit.setLeftComponent(symbols = new SymbolView());
        leftSplit.setRightComponent(stack = new CallStackView());
        split.setLeftComponent(leftSplit);
        split.setRightComponent(content);
        add(BorderLayout.CENTER, split);

        symbols.addJumpListener(this::jump);

        insns.addChangeListener(() -> {
            StepRecord step = insns.getSelectedInstruction();
            StepRecord previous = insns.getPreviousInstruction();
            CallArgsRecord args = insns.getSelectedInstructionCallArguments();
            if (step != null) {
                if (previous != null) {
                    state.setState(previous, step);
                } else {
                    state.setState(step);
                }
                state.setCallArguments(args);
            }
        });

        insns.addCallListener(new CallListener() {
            public void call(BlockNode call) {
                stack.set(call);
                insns.set(call);
                insns.select(call.getNodes().get(0));
            }

            public void ret(RecordNode ret) {
                BlockNode parent = ret.getParent().getParent();
                if (parent != null) {
                    stack.set(parent);
                    insns.set(parent);
                    insns.select(ret.getParent());
                }
            }
        });

        stack.addLevelUpListener(this::up);
        stack.addLevelPeekListener(this::peek);

        KeyStroke esc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(esc, esc);
        getActionMap().put(esc, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                BlockNode block = stack.get();
                if (block != null) {
                    up(block);
                }
            }
        });
    }

    public void jump(Node node) {
        BlockNode block = node.getParent();
        stack.set(block);
        insns.set(block);
        insns.select(node);
    }

    private void up(BlockNode block) {
        BlockNode parent = block.getParent();
        if (parent != null) {
            stack.set(parent);
            insns.set(parent);
            insns.select(block);
        }
    }

    private void peek(BlockNode block, BlockNode select) {
        insns.set(block);
        if (select != null) {
            insns.select(select);
        } else {
            insns.select(block.getNodes().get(0));
        }
    }

    public void setRoot(BlockNode root) {
        stack.set(root);
        insns.set(root);
        insns.select(root.getNodes().get(0));
        state.setState(root.getFirstStep());
    }

    public void setSymbols(SymbolTable symbols) {
        this.symbols.setSymbols(symbols);
    }

    public Node getSelectedNode() {
        return insns.getSelectedNode();
    }

    public StepRecord getSelectedInstruction() {
        return insns.getSelectedInstruction();
    }
}
