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
import org.graalvm.vm.x86.trcview.io.BlockNode;
import org.graalvm.vm.x86.trcview.io.RecordNode;
import org.graalvm.vm.x86.trcview.ui.event.CallListener;

@SuppressWarnings("serial")
public class TraceView extends JPanel {
    private StackView stack;
    private StateView state;
    private InstructionView insns;

    public TraceView(Consumer<String> status) {
        super(new BorderLayout());
        JSplitPane content = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        content.setLeftComponent(insns = new InstructionView(status));
        content.setRightComponent(state = new StateView());
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setLeftComponent(stack = new StackView());
        split.setRightComponent(content);
        add(BorderLayout.CENTER, split);

        insns.addChangeListener(() -> {
            StepRecord step = insns.getSelectedInstruction();
            CallArgsRecord args = insns.getSelectedInstructionCallArguments();
            if (step != null) {
                state.setState(step);
                state.setCallArguments(args);
            }
        });

        insns.addCallListener(new CallListener() {
            public void call(BlockNode call) {
                stack.set(call);
                insns.set(call);
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

    private void up(BlockNode block) {
        BlockNode parent = block.getParent();
        if (parent != null) {
            stack.set(parent);
            insns.set(parent);
            insns.select(block);
        }
    }

    public void setRoot(BlockNode root) {
        stack.set(root);
        insns.set(root);
        state.setState(root.getFirstStep());
    }
}
