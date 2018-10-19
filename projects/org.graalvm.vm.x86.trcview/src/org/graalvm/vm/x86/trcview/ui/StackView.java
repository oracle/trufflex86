package org.graalvm.vm.x86.trcview.ui;

import java.awt.BorderLayout;
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

import org.graalvm.vm.memory.util.HexFormatter;
import org.graalvm.vm.x86.node.debug.trace.StepRecord;
import org.graalvm.vm.x86.trcview.io.BlockNode;
import org.graalvm.vm.x86.trcview.ui.event.LevelUpListener;

import com.everyware.util.log.Trace;

@SuppressWarnings("serial")
public class StackView extends JPanel {
    private static final Logger log = Trace.create(StackView.class);

    private BlockNode current;
    private List<String> callStack;
    private List<BlockNode> callStackBlocks;

    private JList<String> entries;
    private ListModel model;

    private JButton up;

    private List<LevelUpListener> listeners;

    public StackView() {
        super(new BorderLayout());

        listeners = new ArrayList<>();

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
    }

    public void addLevelUpListener(LevelUpListener listener) {
        listeners.add(listener);
    }

    public void removeLevelUpListener(LevelUpListener listener) {
        listeners.remove(listener);
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
            callStackBlocks.add(null);
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
