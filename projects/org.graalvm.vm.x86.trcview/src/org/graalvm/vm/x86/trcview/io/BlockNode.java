package org.graalvm.vm.x86.trcview.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.graalvm.vm.x86.node.debug.trace.CallArgsRecord;
import org.graalvm.vm.x86.node.debug.trace.ExecutionTraceReader;
import org.graalvm.vm.x86.node.debug.trace.Record;
import org.graalvm.vm.x86.node.debug.trace.StepRecord;

public class BlockNode extends Node {
    private StepRecord head;
    private CallArgsRecord callArgs;
    private List<Node> children;

    public BlockNode(StepRecord head, List<Node> children) {
        this(head, children, null);
    }

    public BlockNode(StepRecord head, List<Node> children, CallArgsRecord args) {
        this.head = head;
        this.callArgs = args;
        this.children = children;
        for (Node n : children) {
            n.setParent(this);
        }
    }

    public StepRecord getHead() {
        return head;
    }

    public CallArgsRecord getCallArguments() {
        return callArgs;
    }

    public List<Node> getNodes() {
        return Collections.unmodifiableList(children);
    }

    public StepRecord getFirstStep() {
        for (Node n : children) {
            if (n instanceof RecordNode && ((RecordNode) n).getRecord() instanceof StepRecord) {
                return (StepRecord) ((RecordNode) n).getRecord();
            }
        }
        return null;
    }

    public static BlockNode read(ExecutionTraceReader in) throws IOException {
        return read(in, null);
    }

    public static BlockNode read(ExecutionTraceReader in, ProgressListener progress) throws IOException {
        List<Node> nodes = new ArrayList<>();
        Node node;
        while ((node = parseRecord(in, progress)) != null) {
            nodes.add(node);
        }
        return new BlockNode(null, nodes);
    }

    private static Node parseRecord(ExecutionTraceReader in, ProgressListener progress) throws IOException {
        Record record = in.read();
        if (record == null) {
            return null;
        }
        if (record instanceof StepRecord) {
            StepRecord step = (StepRecord) record;
            if (step.getLocation().getMnemonic().equals("call")) {
                if (progress != null) {
                    progress.progressUpdate(in.tell());
                }
                List<Node> result = new ArrayList<>();
                CallArgsRecord args = null;
                while (true) {
                    Node child = parseRecord(in, progress);
                    if (child == null) {
                        break;
                    }
                    result.add(child);
                    if (args == null && child instanceof RecordNode && ((RecordNode) child).getRecord() instanceof CallArgsRecord) {
                        args = (CallArgsRecord) ((RecordNode) child).getRecord();
                    }
                    if (child instanceof RecordNode && ((RecordNode) child).getRecord() instanceof StepRecord) {
                        StepRecord s = (StepRecord) ((RecordNode) child).getRecord();
                        if (s.getLocation().getMnemonic().equals("ret")) {
                            if (progress != null) {
                                progress.progressUpdate(in.tell());
                            }
                            break;
                        }
                    }
                }
                return new BlockNode(step, result, args);
            } else {
                return new RecordNode(record);
            }
        } else {
            return new RecordNode(record);
        }
    }
}
