package org.graalvm.vm.x86;

import static org.graalvm.vm.x86.Options.getBoolean;

import java.io.File;
import java.io.IOException;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import com.everyware.util.log.Levels;
import com.everyware.util.log.Trace;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.dsl.UnsupportedSpecializationException;

public class AMD64VM {
    private static final boolean PRINT_VM_BANNER = getBoolean(Options.STARTUP_BANNER);

    public static void main(String[] args) throws IOException {
        Trace.setupConsoleApplication(Levels.INFO);
        if (args.length == 0) {
            System.out.printf("Usage: %s program [args]\n", AMD64VM.class.getSimpleName());
            System.exit(1);
        }
        Source source = Source.newBuilder(Vmx86.NAME, new File(args[0])).build();
        System.exit(executeSource(source, args));
    }

    private static int executeSource(Source source, String[] args) {
        if (PRINT_VM_BANNER) {
            Trace.println("== running on " + Truffle.getRuntime().getName());
        }

        Context ctx = Context.newBuilder(Vmx86.NAME).arguments(Vmx86.NAME, args).build();

        try {
            Value result = ctx.eval(source);

            if (result == null) {
                throw new Exception("Error while executing file");
            }

            return result.asInt();
        } catch (Throwable ex) {
            /*
             * PolyglotEngine.eval wraps the actual exception in an IOException, so we have to
             * unwrap here.
             */
            Throwable cause = ex.getCause();
            if (cause instanceof UnsupportedSpecializationException) {
                cause.printStackTrace(System.err);
            } else {
                /* Unexpected error, just print out the full stack trace for debugging purposes. */
                ex.printStackTrace(System.err);
            }
            return 1;
        } finally {
            ctx.close();
        }
    }
}
