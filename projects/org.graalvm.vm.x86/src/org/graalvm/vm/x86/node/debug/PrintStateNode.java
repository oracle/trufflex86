package org.graalvm.vm.x86.node.debug;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.RegisterAccessFactory;
import org.graalvm.vm.x86.isa.CpuState;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.node.AMD64Node;
import org.graalvm.vm.x86.node.ReadFlagsNode;
import org.graalvm.vm.x86.node.ReadNode;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;

public class PrintStateNode extends AMD64Node {
    /*-
        RAX=0000000000000000 RBX=0000000000000000 RCX=0000000000000000 RDX=0000000000000000
        RSI=0000000000000000 RDI=0000000000000000 RBP=0000000000000000 RSP=00000040007ffc60
        R8 =0000000000000000 R9 =0000000000000000 R10=0000000000000000 R11=0000000000000000
        R12=0000000000000000 R13=0000000000000000 R14=0000000000000000 R15=0000000000000000
        RIP=00000000004001bf RFL=00000202 [-------] CPL=3 II=0 A20=1 SMM=0 HLT=0
        ES =0000 0000000000000000 00000000 00000000
        CS =0033 0000000000000000 ffffffff 00effb00 DPL=3 CS64 [-RA]
        SS =002b 0000000000000000 ffffffff 00cff300 DPL=3 DS   [-WA]
        DS =0000 0000000000000000 00000000 00000000
        FS =0000 0000000000000000 00000000 00000000
        GS =0000 0000000000000000 00000000 00000000
        LDT=0000 0000000000000000 0000ffff 00008200 DPL=0 LDT
        TR =0000 0000000000000000 0000ffff 00008b00 DPL=0 TSS64-busy
        GDT=     0000004000802000 0000007f
        IDT=     0000004000801000 000001ff
        CR0=80010001 CR2=0000000000000000 CR3=0000000000000000 CR4=00000220
        DR0=0000000000000000 DR1=0000000000000000 DR2=0000000000000000 DR3=0000000000000000
        DR6=00000000ffff0ff0 DR7=0000000000000400
        CCS=0000000000000000 CCD=0000000000000000 CCO=EFLAGS
        EFER=0000000000000500
    */

    @Child private ReadNode readRAX;
    @Child private ReadNode readRBX;
    @Child private ReadNode readRCX;
    @Child private ReadNode readRDX;
    @Child private ReadNode readRSI;
    @Child private ReadNode readRDI;
    @Child private ReadNode readRBP;
    @Child private ReadNode readRSP;
    @Child private ReadNode readR8;
    @Child private ReadNode readR9;
    @Child private ReadNode readR10;
    @Child private ReadNode readR11;
    @Child private ReadNode readR12;
    @Child private ReadNode readR13;
    @Child private ReadNode readR14;
    @Child private ReadNode readR15;
    @Child private ReadFlagsNode readFlags;

    private void createChildrenIfNecessary() {
        if (readRAX == null) {
            ArchitecturalState state = getContextReference().get().getState();
            RegisterAccessFactory regs = state.getRegisters();
            this.readRAX = regs.getRegister(Register.RAX).createRead();
            this.readRBX = regs.getRegister(Register.RBX).createRead();
            this.readRCX = regs.getRegister(Register.RCX).createRead();
            this.readRDX = regs.getRegister(Register.RDX).createRead();
            this.readRSI = regs.getRegister(Register.RSI).createRead();
            this.readRDI = regs.getRegister(Register.RDI).createRead();
            this.readRBP = regs.getRegister(Register.RBP).createRead();
            this.readRSP = regs.getRegister(Register.RSP).createRead();
            this.readR8 = regs.getRegister(Register.R8).createRead();
            this.readR9 = regs.getRegister(Register.R9).createRead();
            this.readR10 = regs.getRegister(Register.R10).createRead();
            this.readR11 = regs.getRegister(Register.R11).createRead();
            this.readR12 = regs.getRegister(Register.R12).createRead();
            this.readR13 = regs.getRegister(Register.R13).createRead();
            this.readR14 = regs.getRegister(Register.R14).createRead();
            this.readR15 = regs.getRegister(Register.R15).createRead();
            this.readFlags = insert(regs.createReadFlags());
        }
    }

    @TruffleBoundary
    private static void print(CpuState state) {
        System.out.println(state);
    }

    public void execute(VirtualFrame frame, long pc) {
        createChildrenIfNecessary();
        CpuState state = new CpuState();
        state.rax = readRAX.executeI64(frame);
        state.rbx = readRBX.executeI64(frame);
        state.rcx = readRCX.executeI64(frame);
        state.rdx = readRDX.executeI64(frame);
        state.rsi = readRSI.executeI64(frame);
        state.rdi = readRDI.executeI64(frame);
        state.rbp = readRBP.executeI64(frame);
        state.rsp = readRSP.executeI64(frame);
        state.r8 = readR8.executeI64(frame);
        state.r9 = readR9.executeI64(frame);
        state.r10 = readR10.executeI64(frame);
        state.r11 = readR11.executeI64(frame);
        state.r12 = readR12.executeI64(frame);
        state.r13 = readR13.executeI64(frame);
        state.r14 = readR14.executeI64(frame);
        state.r15 = readR15.executeI64(frame);
        state.rip = pc;
        state.rfl = readFlags.executeI64(frame);
        print(state);
    }
}
