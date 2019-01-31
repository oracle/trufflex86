package org.graalvm.vm.x86;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

import org.graalvm.vm.memory.hardware.MMU;
import org.graalvm.vm.util.log.Trace;
import org.graalvm.vm.x86.node.debug.trace.ExecutionTraceWriter;
import org.graalvm.vm.x86.node.debug.trace.LogStreamHandler;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;

public abstract class AMD64Language extends TruffleLanguage<AMD64Context> {
    private static final Logger log = Trace.create(AMD64Language.class);

    public static final String MIME_TYPE = "application/x-executable";

    private static final boolean DEBUG = Options.getBoolean(Options.DEBUG_EXEC);
    private static final boolean DEBUG_TRACE = Options.getBoolean(Options.DEBUG_EXEC_TRACE);

    protected FrameDescriptor fd = new FrameDescriptor();

    @Override
    protected AMD64Context createContext(Env env) {
        try {
            // try to load libmemory from language home
            MMU.loadLibrary(getLanguageHome() + "/libmemory.so");
        } catch (UnsatisfiedLinkError e) {
            // ignore
        }
        if (DEBUG && DEBUG_TRACE) {
            String traceFile = Options.getString(Options.DEBUG_EXEC_TRACEFILE);
            log.info("Opening trace file " + traceFile);
            try {
                OutputStream out = new BufferedOutputStream(new FileOutputStream(traceFile));
                ExecutionTraceWriter trace = new ExecutionTraceWriter(out);
                LogStreamHandler handler = new LogStreamHandler(trace);
                Logger.getLogger("").addHandler(handler);
                return new AMD64Context(this, env, fd, trace, handler);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return new AMD64Context(this, env, fd);
        }
    }

    @Override
    protected boolean isObjectOfLanguage(Object object) {
        return false;
    }

    @Override
    protected void disposeContext(AMD64Context ctx) {
        ExecutionTraceWriter trace = ctx.getTraceWriter();
        Logger.getLogger("").removeHandler(ctx.getLogHandler());
        if (trace != null) {
            try {
                trace.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static ContextReference<AMD64Context> getCurrentContextReference() {
        return getCurrentLanguage(AMD64Language.class).getContextReference();
    }

    public static TruffleLanguage<AMD64Context> getCurrentLanguage() {
        return getCurrentLanguage(AMD64Language.class);
    }

    public static String getHome() {
        return getCurrentLanguage(AMD64Language.class).getLanguageHome();
    }
}
