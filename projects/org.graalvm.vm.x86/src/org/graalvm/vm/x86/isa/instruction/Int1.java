package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.posix.api.Errno;
import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.RegisterAccessFactory;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AVXRegisterOperand;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.isa.RegisterOperand;
import org.graalvm.vm.x86.node.AVXRegisterReadNode;
import org.graalvm.vm.x86.node.AVXRegisterWriteNode;
import org.graalvm.vm.x86.node.ReadFlagNode;
import org.graalvm.vm.x86.node.ReadFlagsNode;
import org.graalvm.vm.x86.node.RegisterReadNode;
import org.graalvm.vm.x86.node.RegisterWriteNode;
import org.graalvm.vm.x86.posix.PosixEnvironment;
import org.graalvm.vm.x86.posix.SyscallException;
import org.graalvm.vm.x86.posix.SyscallWrapper;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.frame.VirtualFrame;

public class Int1 extends AMD64Instruction {
    @Child private SyscallWrapper syscall = null;
    @Child private RegisterReadNode readRAX;
    @Child private RegisterReadNode readRDI;
    @Child private RegisterReadNode readRSI;
    @Child private RegisterReadNode readRDX;
    @Child private RegisterReadNode readRCX;
    @Child private RegisterReadNode readR8;
    @Child private RegisterReadNode readR9;
    @Child private ReadFlagsNode readFlags;
    @Child private RegisterWriteNode writeRAX;
    @Children private AVXRegisterReadNode[] readXMM;
    @Child private ReadFlagNode readCF;
    @Child private AVXRegisterWriteNode writeXMM0;

    @CompilationFinal private ContextReference<AMD64Context> ctxRef;

    public Int1(long pc, byte[] instruction) {
        super(pc, instruction);
        // Linux specific
        setGPRReadOperands(new RegisterOperand(Register.RDI), new RegisterOperand(Register.RSI), new RegisterOperand(Register.RDX), new RegisterOperand(Register.RCX), new RegisterOperand(Register.R8),
                        new RegisterOperand(Register.R9), new RegisterOperand(Register.RSP), new AVXRegisterOperand(0, 128), new AVXRegisterOperand(1, 128), new AVXRegisterOperand(2, 128),
                        new AVXRegisterOperand(3, 128), new AVXRegisterOperand(4, 128), new AVXRegisterOperand(5, 128), new AVXRegisterOperand(6, 128), new AVXRegisterOperand(7, 128));
        setGPRWriteOperands(new RegisterOperand(Register.RAX), new AVXRegisterOperand(0, 128));
    }

    @Override
    protected void createChildNodes() {
        assert syscall == null;

        ctxRef = getContextReference();
        AMD64Context ctx = ctxRef.get();
        RegisterAccessFactory reg = ctx.getState().getRegisters();
        PosixEnvironment posix = ctx.getPosixEnvironment();
        VirtualMemory memory = ctx.getMemory();
        syscall = insert(new SyscallWrapper(posix, memory));
        readRAX = reg.getRegister(Register.RAX).createRead();
        readRDI = reg.getRegister(Register.RDI).createRead();
        readRSI = reg.getRegister(Register.RSI).createRead();
        readRDX = reg.getRegister(Register.RDX).createRead();
        readRCX = reg.getRegister(Register.RCX).createRead();
        readR8 = reg.getRegister(Register.R8).createRead();
        readR9 = reg.getRegister(Register.R9).createRead();
        writeRAX = reg.getRegister(Register.RAX).createWrite();
        writeXMM0 = reg.getAVXRegister(0).createWrite();
        readXMM = new AVXRegisterReadNode[8];
        for (int i = 0; i < readXMM.length; i++) {
            readXMM[i] = reg.getAVXRegister(i).createRead();
        }
        readCF = reg.getCF().createRead();
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        long rax = readRAX.executeI64(frame);
        long rdi = readRDI.executeI64(frame);
        long rsi = readRSI.executeI64(frame);
        long rdx = readRDX.executeI64(frame);
        long rcx = readRCX.executeI64(frame);
        long r8 = readR8.executeI64(frame);
        long r9 = readR9.executeI64(frame);
        boolean cf = readCF.execute(frame);
        long result;

        long xmm0 = readXMM[0].executeI64(frame);
        long xmm1 = readXMM[1].executeI64(frame);
        long xmm2 = readXMM[2].executeI64(frame);
        long xmm3 = readXMM[3].executeI64(frame);
        long xmm4 = readXMM[4].executeI64(frame);
        long xmm5 = readXMM[5].executeI64(frame);
        long xmm6 = readXMM[6].executeI64(frame);
        long xmm7 = readXMM[7].executeI64(frame);

        int id = (int) (rax & 0xFFFF);
        try {
            result = interopCall(id, rdi, rsi, rdx, rcx, r8, r9, xmm0, xmm1, xmm2, xmm3, xmm4, xmm5, xmm6, xmm7);
        } catch (SyscallException ex) {
            result = -ex.getValue();
            if (ex.getValue() == Errno.ENOSYS) {
                throw new RuntimeException("Unknown interop function " + id);
            }
        }

        if (cf) {
            writeXMM0.executeI64(frame, result);
        } else {
            writeRAX.executeI64(frame, result);
        }
        return next();
    }

    @TruffleBoundary
    private long interopCall(int id, long a1, long a2, long a3, long a4, long a5, long a6, long f1, long f2, long f3, long f4, long f5, long f6, long f7, long f8) throws SyscallException {
        return ctxRef.get().interopCall(id, a1, a2, a3, a4, a5, a6, f1, f2, f3, f4, f5, f6, f7, f8);
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"int1"};
    }
}
