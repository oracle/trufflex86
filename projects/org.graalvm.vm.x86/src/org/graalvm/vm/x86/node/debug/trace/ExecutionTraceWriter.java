package org.graalvm.vm.x86.node.debug.trace;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.CpuState;

import com.everyware.util.io.BEOutputStream;
import com.everyware.util.io.WordOutputStream;
import com.everyware.util.log.Trace;

public class ExecutionTraceWriter implements Closeable {
    private static final Logger log = Trace.create(ExecutionTraceWriter.class);

    private WordOutputStream out;

    public ExecutionTraceWriter(File out) throws IOException {
        this(new FileOutputStream(out));
    }

    public ExecutionTraceWriter(OutputStream out) {
        this.out = new BEOutputStream(out);
    }

    @Override
    public void close() throws IOException {
        out.close();
    }

    public synchronized void step(CpuState state, String filename, String symbol, long offset, AMD64Instruction insn) {
        CpuStateRecord stateRecord = new CpuStateRecord(state);
        LocationRecord locationRecord = new LocationRecord(filename, symbol, offset, state.rip, insn.toString());
        StepRecord record = new StepRecord(locationRecord, stateRecord);
        try {
            record.write(out);
        } catch (IOException e) {
            log.log(Level.WARNING, "Error while writing step event: " + e.getMessage(), e);
        }
    }

    public synchronized void callArgs(long pc, String name, long[] args, byte[][] memory) {
        CallArgsRecord record = new CallArgsRecord(pc, name, args, memory);
        try {
            record.write(out);
        } catch (IOException e) {
            log.log(Level.WARNING, "Error while writing call args: " + e.getMessage(), e);
        }
    }
}
