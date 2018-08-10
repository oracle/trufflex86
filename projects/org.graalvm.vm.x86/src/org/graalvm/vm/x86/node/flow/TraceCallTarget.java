package org.graalvm.vm.x86.node.flow;

import static org.graalvm.vm.x86.Options.getBoolean;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.Options;
import org.graalvm.vm.x86.isa.CpuState;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.isa.ReturnException;
import org.graalvm.vm.x86.node.AMD64RootNode;
import org.graalvm.vm.x86.node.init.CopyToCpuStateNode;
import org.graalvm.vm.x86.node.init.InitializeFromCpuStateNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

public class TraceCallTarget extends AMD64RootNode {
    @CompilationFinal public static boolean TRUFFLE_CALLS = getBoolean(Options.TRUFFLE_CALLS);

    @Child private InitializeFromCpuStateNode write = new InitializeFromCpuStateNode();
    @Child private CopyToCpuStateNode read = new CopyToCpuStateNode();
    @Child private TraceDispatchNode dispatch;

    @CompilationFinal private static boolean CHECK = false;

    protected TraceCallTarget(TruffleLanguage<AMD64Context> language, FrameDescriptor fd) {
        super(language, fd);
    }

    @CompilationFinal(dimensions = 1) private boolean[] gprReadMask = null;
    @CompilationFinal(dimensions = 1) private boolean[] gprWriteMask = null;
    @CompilationFinal(dimensions = 1) private boolean[] avxReadMask = null;
    @CompilationFinal(dimensions = 1) private boolean[] avxWriteMask = null;

    @CompilationFinal(dimensions = 1) private static final boolean[] allTrue = new boolean[16];
    static {
        for (int i = 0; i < 16; i++) {
            allTrue[i] = true;
        }
    }

    private boolean first = true;

    private static Register[] getRegisters(boolean[] mask) {
        int cnt = 0;
        for (boolean v : mask) {
            if (v) {
                cnt++;
            }
        }
        Register[] result = new Register[cnt];
        int j = 0;
        for (int i = 0; i < mask.length; i++) {
            if (mask[i]) {
                result[j++] = Register.get(i);
            }
        }
        return result;
    }

    @TruffleBoundary
    private void debug() {
        if (first) {
            first = false;
            Register[] reads = dispatch.getGPRReads();
            Register[] writes = dispatch.getGPRWrites();
            Register[] readMask = getRegisters(gprReadMask);
            Register[] writeMask = getRegisters(gprWriteMask);
            System.out.printf("GPR reads: %s (%s)\n", Stream.of(reads).map(Register::toString).collect(Collectors.joining(",")),
                            Stream.of(readMask).map(Register::toString).collect(Collectors.joining(",")));
            System.out.printf("GPR writes: %s (%s)\n", Stream.of(writes).map(Register::toString).collect(Collectors.joining(",")),
                            Stream.of(writeMask).map(Register::toString).collect(Collectors.joining(",")));
            if (reads.length == 0 || writes.length != 0) {
                System.out.printf("entry point: 0x%016x\n", dispatch.getStartAddress());
                dispatch.dump();
            }
        }
    }

    private void assertEq(String name, long ref, long act) {
        if (ref != act) {
            System.out.printf("%s: 0x%016x expected, was 0x%016x\n", name, ref, act);
            debug();
            throw new AssertionError();
        }
    }

    @TruffleBoundary
    private void check(CpuState ok, CpuState reduced) {
        assertEq("rax", ok.rax, reduced.rax);
        assertEq("rbx", ok.rbx, reduced.rbx);
        assertEq("rcx", ok.rcx, reduced.rcx);
        assertEq("rdx", ok.rdx, reduced.rdx);
        assertEq("rsp", ok.rsp, reduced.rsp);
        assertEq("rbp", ok.rbp, reduced.rbp);
        assertEq("rsi", ok.rsi, reduced.rsi);
        assertEq("rdi", ok.rdi, reduced.rdi);
        assertEq("r8", ok.r8, reduced.r8);
        assertEq("r9", ok.r9, reduced.r9);
        assertEq("r10", ok.r10, reduced.r10);
        assertEq("r11", ok.r11, reduced.r11);
        assertEq("r12", ok.r12, reduced.r12);
        assertEq("r13", ok.r13, reduced.r13);
        assertEq("r14", ok.r14, reduced.r14);
        assertEq("r15", ok.r15, reduced.r15);
    }

    @Override
    public CpuState execute(VirtualFrame frame) {
        if (dispatch == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            ArchitecturalState state = getContextReference().get().getState();
            dispatch = insert(new TraceDispatchNode(state));
        }
        CpuState initialState = (CpuState) frame.getArguments()[0];
        if (gprReadMask != null && !TRUFFLE_CALLS) {
            write.execute(frame, initialState, gprReadMask, avxReadMask);
        } else {
            write.execute(frame, initialState);
        }
        long pc;
        boolean ret = false;
        try {
            pc = dispatch.execute(frame);
        } catch (ReturnException e) {
            pc = e.getBTA();
            ret = true;
        }
        if (gprReadMask == null && !TRUFFLE_CALLS) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            gprReadMask = new boolean[16];
            gprWriteMask = new boolean[16];
            Register[] reads = dispatch.getGPRReads();
            for (Register r : reads) {
                if (r.getID() < 16) {
                    gprReadMask[r.getID()] = true;
                }
            }
            Register[] writes = dispatch.getGPRWrites();
            for (Register r : writes) {
                if (r.getID() < 16) {
                    gprReadMask[r.getID()] = true; // initialize frames
                    gprWriteMask[r.getID()] = true;
                }
            }

            avxReadMask = new boolean[32];
            avxWriteMask = new boolean[32];
            int[] avxReads = dispatch.getAVXReads();
            for (int r : avxReads) {
                avxReadMask[r] = true;
            }
            int[] avxWrites = dispatch.getAVXWrites();
            for (int r : avxWrites) {
                avxReadMask[r] = true; // initialize frames
                avxWriteMask[r] = true;
            }
        }
        CpuState result;
        if (TRUFFLE_CALLS) {
            result = read.execute(frame, pc);
            if (ret) {
                throw new RetException(result);
            } else {
                return result;
            }
        } else {
            result = read.execute(frame, pc, initialState, gprWriteMask, avxWriteMask);
            if (CHECK) {
                CpuState full = read.execute(frame, pc);
                check(full, result);
            }
            if (ret) {
                CompilerDirectives.transferToInterpreter();
                throw new AssertionError("ret must not happen without TRUFFLE_CALLS");
            }
            return result;
        }
    }

    public long getStartAddress() {
        return dispatch.getStartAddress();
    }

    @Override
    public String getName() {
        return toString();
    }

    @Override
    public String toString() {
        long addr = dispatch.getStartAddress();
        if (addr == -1) {
            return "TraceCallTarget[???]";
        } else {
            return String.format("TraceCallTarget[0x%016x]", addr);
        }
    }
}
