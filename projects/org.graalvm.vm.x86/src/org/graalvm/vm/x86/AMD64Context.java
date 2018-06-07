package org.graalvm.vm.x86;

import java.util.Collections;
import java.util.NavigableMap;

import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.x86.node.flow.TraceRegistry;
import org.graalvm.vm.x86.posix.PosixEnvironment;

import com.everyware.posix.elf.Symbol;
import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;

public class AMD64Context {
    private static final String ARCH_NAME = "x86_64";
    private static final String[] REGISTER_NAMES = {"rax", "rcx", "rdx", "rbx", "rsp", "rbp", "rsi", "rdi", "r8", "r9", "r10", "r11", "r12", "r13", "r14", "r15"};

    private final VirtualMemory memory;
    private final PosixEnvironment posix;
    private final String[] args;

    private final FrameDescriptor frameDescriptor;
    private final FrameSlot[] gpr;
    private final FrameSlot[] zmm;
    private final FrameSlot fs;
    private final FrameSlot gs;
    private final FrameSlot pc;
    private final FrameSlot cf;
    private final FrameSlot pf;
    private final FrameSlot af;
    private final FrameSlot zf;
    private final FrameSlot sf;
    private final FrameSlot df;
    private final FrameSlot of;

    private final FrameSlot instructionCount;

    private final ArchitecturalState state;

    private NavigableMap<Long, Symbol> symbols;

    private TraceRegistry traces;

    public AMD64Context(AMD64Language language, Env env, FrameDescriptor fd) {
        frameDescriptor = fd;
        memory = new VirtualMemory();
        posix = new PosixEnvironment(memory, ARCH_NAME);
        posix.setStandardIn(env.in());
        posix.setStandardOut(env.out());
        posix.setStandardErr(env.err());
        args = env.getApplicationArguments();
        assert REGISTER_NAMES.length == 16;
        gpr = new FrameSlot[REGISTER_NAMES.length];
        for (int i = 0; i < REGISTER_NAMES.length; i++) {
            gpr[i] = frameDescriptor.addFrameSlot(REGISTER_NAMES[i], FrameSlotKind.Long);
        }
        zmm = new FrameSlot[32];
        for (int i = 0; i < zmm.length; i++) {
            zmm[i] = frameDescriptor.addFrameSlot("zmm" + i, FrameSlotKind.Object);
        }
        fs = frameDescriptor.addFrameSlot("fs", FrameSlotKind.Long);
        gs = frameDescriptor.addFrameSlot("gs", FrameSlotKind.Long);
        pc = frameDescriptor.addFrameSlot("rip", FrameSlotKind.Long);
        cf = frameDescriptor.addFrameSlot("cf", FrameSlotKind.Boolean);
        pf = frameDescriptor.addFrameSlot("pf", FrameSlotKind.Boolean);
        af = frameDescriptor.addFrameSlot("af", FrameSlotKind.Boolean);
        zf = frameDescriptor.addFrameSlot("zf", FrameSlotKind.Boolean);
        sf = frameDescriptor.addFrameSlot("sf", FrameSlotKind.Boolean);
        df = frameDescriptor.addFrameSlot("df", FrameSlotKind.Boolean);
        of = frameDescriptor.addFrameSlot("of", FrameSlotKind.Boolean);
        instructionCount = frameDescriptor.addFrameSlot("instructionCount", FrameSlotKind.Long);
        traces = new TraceRegistry(language, frameDescriptor);
        state = new ArchitecturalState(this);
        symbols = Collections.emptyNavigableMap();
    }

    public FrameDescriptor getFrameDescriptor() {
        return frameDescriptor;
    }

    public VirtualMemory getMemory() {
        return memory;
    }

    public PosixEnvironment getPosixEnvironment() {
        return posix;
    }

    public void setSymbols(NavigableMap<Long, Symbol> symbols) {
        this.symbols = symbols;
    }

    public NavigableMap<Long, Symbol> getSymbols() {
        return symbols;
    }

    public String[] getArguments() {
        return args;
    }

    public FrameSlot getGPR(int i) {
        return gpr[i];
    }

    public FrameSlot getZMM(int i) {
        return zmm[i];
    }

    public FrameSlot getFS() {
        return fs;
    }

    public FrameSlot getGS() {
        return gs;
    }

    public FrameSlot getPC() {
        return pc;
    }

    public FrameSlot[] getGPRs() {
        return gpr;
    }

    public FrameSlot[] getZMMs() {
        return zmm;
    }

    public FrameSlot getCF() {
        return cf;
    }

    public FrameSlot getPF() {
        return pf;
    }

    public FrameSlot getAF() {
        return af;
    }

    public FrameSlot getZF() {
        return zf;
    }

    public FrameSlot getSF() {
        return sf;
    }

    public FrameSlot getDF() {
        return df;
    }

    public FrameSlot getOF() {
        return of;
    }

    public FrameSlot getInstructionCount() {
        return instructionCount;
    }

    public ArchitecturalState getState() {
        return state;
    }

    public SymbolResolver getSymbolResolver() {
        return new SymbolResolver(symbols);
    }

    public TraceRegistry getTraceRegistry() {
        return traces;
    }
}
