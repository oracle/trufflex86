package org.graalvm.vm.x86;

import java.util.Collections;
import java.util.NavigableMap;

import org.graalvm.vm.memory.VirtualMemory;
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

    private final FrameSlot[] registers;
    private final FrameSlot pc;
    private final FrameDescriptor frameDescriptor;

    private final ArchitecturalState state;

    private NavigableMap<Long, Symbol> symbols;

    public AMD64Context(Env env, FrameDescriptor fd) {
        frameDescriptor = fd;
        memory = new VirtualMemory();
        posix = new PosixEnvironment(memory, ARCH_NAME);
        args = env.getApplicationArguments();
        assert REGISTER_NAMES.length == 16;
        registers = new FrameSlot[REGISTER_NAMES.length];
        for (int i = 0; i < REGISTER_NAMES.length; i++) {
            registers[i] = frameDescriptor.addFrameSlot(REGISTER_NAMES[i], FrameSlotKind.Long);
        }
        pc = frameDescriptor.addFrameSlot("rip", FrameSlotKind.Long);
        state = new ArchitecturalState(this);
        symbols = Collections.emptyNavigableMap();
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
        return registers[i];
    }

    public FrameSlot getPC() {
        return pc;
    }

    public FrameSlot[] getGPRs() {
        return registers;
    }

    public ArchitecturalState getState() {
        return state;
    }
}
