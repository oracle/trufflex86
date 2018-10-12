package org.graalvm.vm.x86.isa.instruction;

import static org.graalvm.vm.x86.Options.getString;
import static org.graalvm.vm.x86.isa.CpuidBits.getI32;
import static org.graalvm.vm.x86.isa.CpuidBits.getProcessorInfo;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.Options;
import org.graalvm.vm.x86.RegisterAccessFactory;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.CpuidBits;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.isa.RegisterOperand;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.profiles.IntValueProfile;

public class Cpuid extends AMD64Instruction {
    // at most 48 characters
    public static final String BRAND = getString(Options.CPUID_BRAND);

    // exactly 12 characters
    public static final String VENDOR_ID = getString(Options.VENDOR_ID);

    public static final int PROCESSOR_INFO = getProcessorInfo(0, 6, 1, 1);

    @CompilationFinal(dimensions = 1) public static final int[] BRAND_I32 = getI32(BRAND, 12);
    @CompilationFinal(dimensions = 1) public static final int[] VENDOR_ID_I32 = getI32(VENDOR_ID, 3);

    public static final int BRAND_INDEX = 0;
    public static final int CLFLUSH_LINE_SIZE = 8;

    private IntValueProfile profile;

    @Child private ReadNode readEAX;
    @Child private ReadNode readECX;
    @Child private WriteNode writeEAX;
    @Child private WriteNode writeEBX;
    @Child private WriteNode writeECX;
    @Child private WriteNode writeEDX;

    public Cpuid(long pc, byte[] instruction) {
        super(pc, instruction);
        profile = IntValueProfile.createIdentityProfile();

        setGPRReadOperands(new RegisterOperand(Register.EAX), new RegisterOperand(Register.RCX));
        setGPRWriteOperands(new RegisterOperand(Register.EAX), new RegisterOperand(Register.EBX), new RegisterOperand(Register.RCX), new RegisterOperand(Register.EDX));
    }

    @Override
    protected void createChildNodes() {
        ArchitecturalState state = getState();
        RegisterAccessFactory regs = state.getRegisters();
        readEAX = regs.getRegister(Register.EAX).createRead();
        readECX = regs.getRegister(Register.ECX).createRead();
        writeEAX = regs.getRegister(Register.EAX).createWrite();
        writeEBX = regs.getRegister(Register.EBX).createWrite();
        writeECX = regs.getRegister(Register.ECX).createWrite();
        writeEDX = regs.getRegister(Register.EDX).createWrite();
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        int level = readEAX.executeI32(frame);

        int a;
        int b;
        int c;
        int d;

        switch (profile.profile(level)) {
            case 0:
                // Get Vendor ID/Highest Function Parameter
                a = 7; // max supported function
                b = VENDOR_ID_I32[0];
                d = VENDOR_ID_I32[1];
                c = VENDOR_ID_I32[2];
                break;
            case 1:
                // Processor Info and Feature Bits
                // EAX:
                // 3:0 - Stepping
                // 7:4 - Model
                // 11:8 - Family
                // 13:12 - Processor Type
                // 19:16 - Extended Model
                // 27:20 - Extended Family
                a = PROCESSOR_INFO;
                b = BRAND_INDEX | (CLFLUSH_LINE_SIZE << 8);
                c = CpuidBits.SSE3 | CpuidBits.SSE41 | CpuidBits.SSE42 | CpuidBits.POPCNT | CpuidBits.RDRND;
                d = CpuidBits.TSC | CpuidBits.CMOV | CpuidBits.CLFSH | CpuidBits.FXSR | CpuidBits.SSE | CpuidBits.SSE2;
                break;
            case 7:
                // Extended Features (FIXME: assumption is ECX=0)
                a = 0;
                b = CpuidBits.RDSEED;
                c = 0;
                d = 0;
                break;
            case 0x80000000:
                // Get Highest Extended Function Supported
                a = 0x80000004;
                b = 0;
                c = 0;
                d = 0;
                break;
            case 0x80000001:
                // Extended Processor Info and Feature Bits
                a = 0;
                b = 0;
                c = CpuidBits.LAHF;
                d = CpuidBits.LM;
                break;
            case 0x80000002:
                // Processor Brand String
                a = BRAND_I32[0];
                b = BRAND_I32[1];
                c = BRAND_I32[2];
                d = BRAND_I32[3];
                break;
            case 0x80000003:
                // Processor Brand String
                a = BRAND_I32[4];
                b = BRAND_I32[5];
                c = BRAND_I32[6];
                d = BRAND_I32[7];
                break;
            case 0x80000004:
                // Processor Brand String
                a = BRAND_I32[8];
                b = BRAND_I32[9];
                c = BRAND_I32[10];
                d = BRAND_I32[11];
                break;
            default:
                // Fallback: bits cleared = feature(s) not available
                a = 0;
                b = 0;
                c = 0;
                d = 0;
        }
        writeEAX.executeI32(frame, a);
        writeEBX.executeI32(frame, b);
        writeECX.executeI32(frame, c);
        writeEDX.executeI32(frame, d);
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"cpuid"};
    }
}
