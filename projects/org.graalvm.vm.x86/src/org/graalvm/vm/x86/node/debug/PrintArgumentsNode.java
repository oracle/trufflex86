package org.graalvm.vm.x86.node.debug;

import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.memory.exception.SegmentationViolation;
import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.RegisterAccessFactory;
import org.graalvm.vm.x86.SymbolResolver;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.node.AMD64Node;
import org.graalvm.vm.x86.node.ReadNode;

import com.everyware.posix.elf.Symbol;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;

public class PrintArgumentsNode extends AMD64Node {
    @CompilationFinal private VirtualMemory memory;
    @CompilationFinal private SymbolResolver resolver;

    @Child private ReadNode readRDI;
    @Child private ReadNode readRSI;
    @Child private ReadNode readRDX;
    @Child private ReadNode readRCX;
    @Child private ReadNode readR8;
    @Child private ReadNode readR9;

    private void print(String name, long value) {
        System.out.printf("%s = 0x%016x\n", name, value);
        if (memory.contains(value)) {
            try {
                memory.dump(value, 16);
            } catch (SegmentationViolation e) {
                System.out.printf("Cannot read %d byte(s) from 0x%016x\n", 16, value);
            }
        }
    }

    @TruffleBoundary
    private void print(long pc, long rdi, long rsi, long rdx, long rcx, long r8, long r9) {
        System.out.println("========================");
        Symbol sym = resolver.getSymbol(pc);
        if (sym != null) {
            System.out.printf("Calling 0x%016x <%s>:\n", pc, sym.getName());
        } else {
            System.out.printf("Calling 0x%016x:\n", pc);
        }
        print("arg0", rdi);
        print("arg1", rsi);
        print("arg2", rdx);
        print("arg3", rcx);
        print("arg4", r8);
        print("arg5", r9);
        System.out.println("========================");
    }

    public void execute(VirtualFrame frame, long pc) {
        if (readRDI == null) {
            CompilerDirectives.transferToInterpreter();
            AMD64Context ctx = getContextReference().get();
            ArchitecturalState state = ctx.getState();
            RegisterAccessFactory regs = state.getRegisters();
            readRDI = regs.getRegister(Register.RDI).createRead();
            readRSI = regs.getRegister(Register.RSI).createRead();
            readRDX = regs.getRegister(Register.RDX).createRead();
            readRCX = regs.getRegister(Register.RCX).createRead();
            readR8 = regs.getRegister(Register.R8).createRead();
            readR9 = regs.getRegister(Register.R9).createRead();
            memory = state.getMemory();
            resolver = ctx.getSymbolResolver();
        }
        long rdi = readRDI.executeI64(frame);
        long rsi = readRSI.executeI64(frame);
        long rdx = readRDX.executeI64(frame);
        long rcx = readRCX.executeI64(frame);
        long r8 = readR8.executeI64(frame);
        long r9 = readR9.executeI64(frame);
        print(pc, rdi, rsi, rdx, rcx, r8, r9);
    }
}
