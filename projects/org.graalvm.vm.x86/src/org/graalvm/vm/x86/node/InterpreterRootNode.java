package org.graalvm.vm.x86.node;

import static org.graalvm.vm.x86.Options.getBoolean;

import java.util.logging.Logger;

import org.graalvm.vm.memory.exception.SegmentationViolation;
import org.graalvm.vm.posix.api.Signal;
import org.graalvm.vm.util.log.Trace;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.CpuRuntimeException;
import org.graalvm.vm.x86.Options;
import org.graalvm.vm.x86.isa.IllegalInstructionException;
import org.graalvm.vm.x86.node.flow.AbstractDispatchNode;
import org.graalvm.vm.x86.node.flow.DispatchNode;
import org.graalvm.vm.x86.node.flow.InterTraceDispatchNode;
import org.graalvm.vm.x86.node.flow.RetException;
import org.graalvm.vm.x86.node.init.InitializeFromCpuStateNode;
import org.graalvm.vm.x86.node.init.InitializerNode;
import org.graalvm.vm.x86.posix.ProcessExitException;

import com.oracle.truffle.api.frame.VirtualFrame;

public class InterpreterRootNode extends AMD64Node {
    private static final Logger log = Trace.create(InterpreterRootNode.class);

    private static final boolean SIMPLE_DISPATCH = getBoolean(Options.SIMPLE_DISPATCH) || getBoolean(Options.DEBUG_EXEC);

    @Child private InitializerNode initializer;
    @Child private AbstractDispatchNode interpreter;
    @Child private InitializeFromCpuStateNode writeState = new InitializeFromCpuStateNode();

    public InterpreterRootNode(ArchitecturalState state, String programName) {
        initializer = new InitializerNode(state, programName);
        if (SIMPLE_DISPATCH) {
            log.warning("Using old and slow dispatch node");
            interpreter = new DispatchNode(state);
        } else {
            interpreter = new InterTraceDispatchNode(state);
        }
    }

    public Object execute(VirtualFrame frame) {
        initializer.execute(frame);
        try {
            while (true) {
                try {
                    return interpreter.execute(frame);
                } catch (RetException e) {
                    writeState.execute(frame, e.getState());
                    continue;
                }
            }
        } catch (ProcessExitException e) {
            return e.getCode();
        } catch (CpuRuntimeException e) {
            if (e.getCause() instanceof IllegalInstructionException) {
                return 128 + Signal.SIGILL;
            } else if (e.getCause() instanceof SegmentationViolation) {
                return 128 + Signal.SIGSEGV;
            } else {
                return 127;
            }
        } catch (Throwable t) {
            t.printStackTrace(Trace.log);
            return 127;
        }
    }
}
