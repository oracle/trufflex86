package org.graalvm.vm.x86.trcview.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.graalvm.vm.x86.node.debug.trace.ExecutionTraceReader;
import org.graalvm.vm.x86.node.debug.trace.Record;
import org.graalvm.vm.x86.node.debug.trace.StepRecord;

public class BlockNode extends Node {
    private StepRecord head;
    private List<Node> children;

    public BlockNode(StepRecord head, List<Node> children) {
        this.head = head;
        this.children = children;
        for (Node n : children) {
            n.setParent(this);
        }
    }

    public StepRecord getHead() {
        return head;
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
        List<Node> nodes = new ArrayList<>();
        Node node;
        while ((node = parseRecord(in)) != null) {
            nodes.add(node);
        }
        return new BlockNode(null, nodes);
    }

    private static Node parseRecord(ExecutionTraceReader in) throws IOException {
        Record record = in.read();
        if (record == null) {
            return null;
        }
        if (record instanceof StepRecord) {
            StepRecord step = (StepRecord) record;
            if (step.getLocation().getAssembly().startsWith("call")) {
                List<Node> result = new ArrayList<>();
                while (true) {
                    Node child = parseRecord(in);
                    if (child == null) {
                        break;
                    }
                    result.add(child);
                    if (child instanceof RecordNode && ((RecordNode) child).getRecord() instanceof StepRecord) {
                        StepRecord s = (StepRecord) ((RecordNode) child).getRecord();
                        if (s.getLocation().getAssembly().startsWith("ret")) {
                            break;
                        }
                    }
                }
                return new BlockNode(step, result);
            } else {
                return new RecordNode(record);
            }
        } else {
            return new RecordNode(record);
        }
    }
}
