package org.graalvm.vm.x86.trcview.ui;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.graalvm.vm.x86.node.debug.trace.CallArgsRecord;
import org.graalvm.vm.x86.node.debug.trace.StepRecord;

@SuppressWarnings("serial")
public class StateView extends JPanel {
    private JTextArea text;

    private StepRecord step;
    private CallArgsRecord args;

    public StateView() {
        super(new BorderLayout());
        text = new JTextArea();
        text.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        text.setEditable(false);
        add(BorderLayout.CENTER, new JScrollPane(text));
    }

    public void setState(StepRecord step) {
        this.step = step;
        this.args = null;
        update();
    }

    public void setCallArguments(CallArgsRecord args) {
        this.args = args;
        update();
    }

    private void update() {
        if (step != null) {
            if (args != null) {
                text.setText(step.toString() + "\n\n" + args);
            } else {
                text.setText(step.toString());
            }
        } else {
            text.setText("");
        }
        text.setCaretPosition(0);
    }
}
