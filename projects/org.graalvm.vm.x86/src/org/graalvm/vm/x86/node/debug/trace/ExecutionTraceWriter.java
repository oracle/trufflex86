package org.graalvm.vm.x86.node.debug.trace;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.CpuState;

import com.everyware.util.io.BEOutputStream;
import com.everyware.util.io.WordOutputStream;
import com.everyware.util.log.Trace;

public class ExecutionTraceWriter implements Closeable {
    private static final Logger log = Trace.create(ExecutionTraceWriter.class);

    private WordOutputStream out;

    public ExecutionTraceWriter(File out) throws IOException {
        this(new BufferedOutputStream(new FileOutputStream(out)));
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
        LocationRecord locationRecord = new LocationRecord(filename, symbol, offset, state.rip, insn.getBytes(), insn.getDisassemblyComponents());
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

    public synchronized void log(long seq, long time, int level, int threadID, String logger, String clazz, String method, String msg, Throwable throwable) {
        SystemLogRecord record = new SystemLogRecord(seq, time, level, threadID, logger, clazz, method, msg, throwable);
        try {
            record.write(out);
        } catch (IOException e) {
            // don't log anything here because this method is called from a log handler!
        }
    }

    public synchronized void memoryAccess(long address, boolean write, int size) {
        MemoryEventRecord record = new MemoryEventRecord(address, write, size);
        try {
            record.write(out);
        } catch (IOException e) {
            log.log(Level.WARNING, "Error while writing memory access: " + e.getMessage(), e);
        }
    }

    public synchronized void memoryAccess(long address, boolean write, int size, long value) {
        MemoryEventRecord record = new MemoryEventRecord(address, write, size, value);
        try {
            record.write(out);
        } catch (IOException e) {
            log.log(Level.WARNING, "Error while writing memory access: " + e.getMessage(), e);
        }
    }

    public synchronized void memoryAccess(long address, boolean write, int size, Vector128 value) {
        MemoryEventRecord record = new MemoryEventRecord(address, write, size, value);
        try {
            record.write(out);
        } catch (IOException e) {
            log.log(Level.WARNING, "Error while writing memory access: " + e.getMessage(), e);
        }
    }

    public synchronized void flush() {
        try {
            out.flush();
        } catch (IOException e) {
            log.log(Level.WARNING, "Error while flushing trace data: " + e.getMessage(), e);
        }
    }
}
