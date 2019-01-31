package org.graalvm.vm.x86.node.debug.trace;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import org.graalvm.vm.util.io.BEInputStream;
import org.graalvm.vm.util.io.WordInputStream;

public class ExecutionTraceReader implements Closeable {
    private WordInputStream in;

    public ExecutionTraceReader(InputStream in) {
        this.in = new BEInputStream(in);
    }

    public long tell() {
        return in.tell();
    }

    @Override
    public void close() throws IOException {
        in.close();
    }

    public <T extends Record> T read() throws IOException {
        return Record.read(in);
    }
}
