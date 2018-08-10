package org.graalvm.vm.x86.nfi;

import static org.graalvm.vm.x86.Options.getBoolean;

import java.util.logging.Logger;

import org.graalvm.vm.memory.exception.SegmentationViolation;
import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.CpuRuntimeException;
import org.graalvm.vm.x86.Options;
import org.graalvm.vm.x86.isa.CpuState;
import org.graalvm.vm.x86.isa.IllegalInstructionException;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.node.AMD64Node;
import org.graalvm.vm.x86.node.MemoryWriteNode;
import org.graalvm.vm.x86.node.RegisterReadNode;
import org.graalvm.vm.x86.node.flow.AbstractDispatchNode;
import org.graalvm.vm.x86.node.flow.DispatchNode;
import org.graalvm.vm.x86.node.flow.InterTraceDispatchNode;
import org.graalvm.vm.x86.node.flow.RetException;
import org.graalvm.vm.x86.node.init.CopyToCpuStateNode;
import org.graalvm.vm.x86.node.init.InitializeFromCpuStateNode;
import org.graalvm.vm.x86.node.init.InitializerNode;
import org.graalvm.vm.x86.posix.InteropInitException;
import org.graalvm.vm.x86.posix.InteropReturnException;
import org.graalvm.vm.x86.posix.ProcessExitException;

import com.everyware.posix.api.Signal;
import com.everyware.util.log.Trace;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;

public class InterpreterRootNode extends AMD64Node {
    private static final Logger log = Trace.create(InterpreterRootNode.class);

    @CompilationFinal private static boolean SIMPLE_DISPATCH = getBoolean(Options.SIMPLE_DISPATCH) || getBoolean(Options.DEBUG_EXEC);
    @CompilationFinal private static boolean DEBUG_STATE = getBoolean(Options.DEBUG_PRINT_STATE);

    @Child private InitializerNode initializer;
    @Child private AbstractDispatchNode interpreter;
    @Child private InitializeFromCpuStateNode writeState = new InitializeFromCpuStateNode();

    @Child private RegisterReadNode readPC;
    @Child private RegisterReadNode readRAX;
    @Child private CopyToCpuStateNode readCpuState = new CopyToCpuStateNode();
    @Child private MemoryWriteNode writeMemory;

    public InterpreterRootNode(ArchitecturalState state) {
        if (SIMPLE_DISPATCH) {
            log.warning("Using old and slow dispatch node");
            interpreter = new DispatchNode(state);
        } else {
            interpreter = new InterTraceDispatchNode(state);
        }
        readPC = state.getRegisters().getPC().createRead();
        readRAX = state.getRegisters().getRegister(Register.RAX).createRead();
        writeMemory = new MemoryWriteNode(state.getMemory());
    }

    public InterpreterRootNode(ArchitecturalState state, String programName, String libraryName) {
        this(state);
        initializer = new InitializerNode(state, programName, new String[]{programName, libraryName});
    }

    public InteropFunctionPointers executeInit(VirtualFrame frame) {
        initializer.execute(frame);
        try {
            while (true) {
                try {
                    interpreter.execute(frame);
                    throw new IllegalStateException("interpreter must not return");
                } catch (RetException e) {
                    writeState.execute(frame, e.getState());
                    continue;
                }
            }
        } catch (InteropInitException e) {
            CpuState state = readCpuState.execute(frame, readPC.executeI64(frame));
            AMD64Context ctx = getContextReference().get();
            ctx.setStateSnapshot(state);
            return new InteropFunctionPointers(e.getLoadLibrary(), e.getReleaseLibrary(), e.getSymbol());
        }
    }

    public long executeInterop(VirtualFrame frame, long sp, long ret, long pc, long a1, long a2, long a3, long a4, long a5, long a6) {
        AMD64Context ctx = getContextReference().get();
        CpuState state = new CpuState();
        state.rip = pc;
        state.rsp = sp;
        state.rdi = a1;
        state.rsi = a2;
        state.rdx = a3;
        state.rcx = a4;
        state.r8 = a5;
        state.r9 = a6;
        for (int i = 0; i < 16; i++) {
            state.xmm[i] = new Vector128();
        }
        state.fs = ctx.getStateSnapshot().fs;
        state.gs = ctx.getStateSnapshot().gs;
        writeState.execute(frame, state);
        writeMemory.executeI64(sp, ret);
        try {
            while (true) {
                try {
                    interpreter.execute(frame);
                    throw new IllegalStateException("interpreter must not return");
                } catch (RetException e) {
                    writeState.execute(frame, e.getState());
                    continue;
                }
            }
        } catch (InteropReturnException e) {
            return e.getValue();
        }
    }

    public int executeUntilExit(VirtualFrame frame) {
        CpuState state = getContextReference().get().getStateSnapshot();
        writeState.execute(frame, state);
        try {
            while (true) {
                try {
                    interpreter.execute(frame);
                    throw new IllegalStateException("interpreter must not return");
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
