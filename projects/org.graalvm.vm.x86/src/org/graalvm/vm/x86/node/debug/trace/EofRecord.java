package org.graalvm.vm.x86.node.debug.trace;

import java.io.IOException;

import com.everyware.util.io.WordInputStream;
import com.everyware.util.io.WordOutputStream;

public class EofRecord extends Record {
    public static final int MAGIC = 0x454f4630;

    public EofRecord() {
        super(MAGIC);
    }

    @Override
    protected int size() {
        return 0;
    }

    @Override
    protected void readRecord(WordInputStream in) throws IOException {
        // empty
    }

    @Override
    protected void writeRecord(WordOutputStream out) throws IOException {
        // empty
    }
}
