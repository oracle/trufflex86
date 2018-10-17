package org.graalvm.vm.x86.trcview.io;

import org.graalvm.vm.x86.node.debug.trace.Record;

public class RecordNode extends Node {
    private Record record;

    public RecordNode(Record record) {
        this.record = record;
    }

    public Record getRecord() {
        return record;
    }

    @Override
    public String toString() {
        return record.toString();
    }
}
