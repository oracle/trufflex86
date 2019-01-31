package org.graalvm.vm.x86.node.debug.trace;

import java.io.IOException;

import org.graalvm.vm.util.io.WordInputStream;
import org.graalvm.vm.util.io.WordOutputStream;

public class StepRecord extends Record {
    public static final int MAGIC = 0x53544550; // STEP

    private CpuStateRecord state;
    private LocationRecord location;

    StepRecord() {
        super(MAGIC);
    }

    public StepRecord(LocationRecord location, CpuStateRecord state) {
        this();
        this.location = location;
        this.state = state;
    }

    public CpuStateRecord getState() {
        return state;
    }

    public LocationRecord getLocation() {
        return location;
    }

    @Override
    protected int size() {
        return state.size() + location.size() + 4 * 4;
    }

    @Override
    protected void readRecord(WordInputStream in) throws IOException {
        Record r1 = Record.read(in);
        Record r2 = Record.read(in);

        if (r1 instanceof LocationRecord) {
            location = (LocationRecord) r1;
        } else if (r1 instanceof CpuStateRecord) {
            state = (CpuStateRecord) r1;
        } else {
            throw new IOException("Unknown record " + r1.getClass().getSimpleName());
        }

        if (r2 instanceof LocationRecord) {
            location = (LocationRecord) r2;
        } else if (r2 instanceof CpuStateRecord) {
            state = (CpuStateRecord) r2;
        } else {
            throw new IOException("Unknown record " + r1.getClass().getSimpleName());
        }
    }

    @Override
    protected void writeRecord(WordOutputStream out) throws IOException {
        location.write(out);
        state.write(out);
    }

    @Override
    public String toString() {
        return location + "\n" + state;
    }
}
