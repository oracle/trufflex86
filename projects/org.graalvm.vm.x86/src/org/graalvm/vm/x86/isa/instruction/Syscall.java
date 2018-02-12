package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.RegisterAccessFactory;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.node.RegisterReadNode;
import org.graalvm.vm.x86.node.RegisterWriteNode;
import org.graalvm.vm.x86.posix.PosixEnvironment;
import org.graalvm.vm.x86.posix.SyscallException;
import org.graalvm.vm.x86.posix.SyscallWrapper;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public class Syscall extends AMD64Instruction {
    @Child private SyscallWrapper syscall = null;
    @Child private RegisterReadNode readRAX;
    @Child private RegisterReadNode readRDI;
    @Child private RegisterReadNode readRSI;
    @Child private RegisterReadNode readRDX;
    @Child private RegisterReadNode readR10;
    @Child private RegisterReadNode readR8;
    @Child private RegisterReadNode readR9;
    @Child private RegisterWriteNode writeRAX;

    public Syscall(long pc, byte[] instruction) {
        super(pc, instruction);
    }

    private void createChildren() {
        assert syscall == null;
        CompilerDirectives.transferToInterpreter();
        AMD64Context ctx = getContextReference().get();
        RegisterAccessFactory reg = ctx.getState().getRegisters();
        PosixEnvironment posix = ctx.getPosixEnvironment();
        syscall = new SyscallWrapper(posix);
        readRAX = reg.getRegister(Register.RAX).createRead();
        readRDI = reg.getRegister(Register.RDI).createRead();
        readRSI = reg.getRegister(Register.RSI).createRead();
        readRDX = reg.getRegister(Register.RDX).createRead();
        readR10 = reg.getRegister(Register.R10).createRead();
        readR8 = reg.getRegister(Register.R8).createRead();
        readR9 = reg.getRegister(Register.R9).createRead();
        writeRAX = reg.getRegister(Register.RAX).createWrite();
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        if (syscall == null) {
            createChildren();
        }
        long rax = readRAX.executeI64(frame);
        long rdi = readRDI.executeI64(frame);
        long rsi = readRSI.executeI64(frame);
        long rdx = readRDX.executeI64(frame);
        long r10 = readR10.executeI64(frame);
        long r8 = readR8.executeI64(frame);
        long r9 = readR9.executeI64(frame);
        long result;
        try {
            result = syscall.executeI64((int) rax, rdi, rsi, rdx, r10, r8, r9, 0);
        } catch (SyscallException e) {
            result = -e.getValue();
        }
        writeRAX.executeI64(frame, result);
        return next();
    }

    @Override
    public boolean isControlFlow() {
        return true;
    }

    @Override
    public long[] getBTA() {
        // return new long[]{next()};
        return null;
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"syscall"};
    }
}
