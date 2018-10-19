package org.graalvm.vm.x86.node.debug.trace;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class LogStreamHandler extends Handler {
    private ExecutionTraceWriter out;

    public LogStreamHandler(ExecutionTraceWriter out) {
        this.out = out;
    }

    @Override
    public void publish(LogRecord record) {
        String msg = record.getMessage();
        Throwable throwable = record.getThrown();
        String clazz = record.getSourceClassName();
        String method = record.getSourceMethodName();
        String logger = record.getLoggerName();
        int threadID = record.getThreadID();
        long seq = record.getSequenceNumber();
        long time = record.getMillis();
        int level = record.getLevel().intValue();

        out.log(seq, time, level, threadID, logger, clazz, method, msg, throwable);
    }

    @Override
    public void flush() {
        // nothing
    }

    @Override
    public void close() throws SecurityException {
        // nothing
    }
}
