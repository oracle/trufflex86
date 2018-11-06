package org.graalvm.vm.x86.node.debug.trace;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.everyware.util.StackTraceUtil;
import com.everyware.util.StringUtils;
import com.everyware.util.io.WordInputStream;
import com.everyware.util.io.WordOutputStream;

public class SystemLogRecord extends Record {
    public static final int MAGIC = 0x4c4f4752; // LOGR

    private long seq;
    private long time;
    private int level;
    private int threadID;
    private String logger;
    private String clazz;
    private String method;
    private String message;
    private String throwable;

    SystemLogRecord() {
        super(MAGIC);
    }

    public SystemLogRecord(long seq, long time, int level, int threadID, String logger, String clazz, String method, String message, Throwable throwable) {
        this();
        this.seq = seq;
        this.time = time;
        this.level = level;
        this.threadID = threadID;
        this.logger = logger;
        this.clazz = clazz;
        this.method = method;
        this.message = message;
        this.throwable = throwable != null ? StackTraceUtil.getStackTrace(throwable) : null;
    }

    public LogRecord getLogRecord() {
        @SuppressWarnings("serial")
        Level lvl = new Level("level-" + level, level) {
        };
        LogRecord r = new LogRecord(lvl, clazz);
        r.setSequenceNumber(seq);
        r.setMillis(time);
        r.setThreadID(threadID);
        r.setLoggerName(logger);
        r.setSourceClassName(clazz);
        r.setSourceMethodName(method);
        r.setMessage(message);
        return r;
    }

    public String getThrown() {
        return throwable;
    }

    @Override
    protected int size() {
        int size = 2 * 8 + 2 * 4 + 5 * 2;
        if (logger != null) {
            size += logger.getBytes().length;
        }
        if (clazz != null) {
            size += clazz.getBytes().length;
        }
        if (method != null) {
            size += method.getBytes().length;
        }
        if (message != null) {
            size += message.getBytes().length;
        }
        if (throwable != null) {
            size += throwable.getBytes().length;
        }
        return size;
    }

    @Override
    protected void readRecord(WordInputStream in) throws IOException {
        seq = in.read64bit();
        time = in.read64bit();
        level = in.read32bit();
        threadID = in.read32bit();
        logger = readString(in);
        clazz = readString(in);
        method = readString(in);
        message = readString(in);
        throwable = readString(in);
    }

    @Override
    protected void writeRecord(WordOutputStream out) throws IOException {
        out.write64bit(seq);
        out.write64bit(time);
        out.write32bit(level);
        out.write32bit(threadID);
        writeString(out, logger);
        writeString(out, clazz);
        writeString(out, method);
        writeString(out, message);
        writeString(out, throwable);
    }

    @Override
    public String toString() {
        String src = StringUtils.rpad(clazz + "#" + method, 60);
        if (clazz == null || method == null) {
            if (logger != null) {
                src = StringUtils.rpad("<" + logger + ">", 60);
            } else {
                src = StringUtils.rpad("unknown source", 60);
            }
        }
        char lvl = '?';
        String error = "";
        if (throwable != null) {
            error = "\n" + throwable;
        }
        return String.format("[%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS:%1$tL] %2$08x %3$s %4$c %5$s%6$s", new Date(time), threadID, src, lvl, message, error);
    }
}
