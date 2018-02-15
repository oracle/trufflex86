package org.graalvm.vm.x86.node;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.RegisterAccessFactory;
import org.graalvm.vm.x86.isa.Flags;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public class ReadFlagsNode extends ReadNode {
    @Child private ReadFlagNode readCF;
    @Child private ReadFlagNode readPF;
    @Child private ReadFlagNode readAF;
    @Child private ReadFlagNode readZF;
    @Child private ReadFlagNode readSF;
    @Child private ReadFlagNode readDF;
    @Child private ReadFlagNode readOF;

    private static final long RESERVED = bit(1, true) | bit(Flags.IF, true);

    private static long bit(long shift, boolean value) {
        return value ? (1L << shift) : 0;
    }

    private void createChildrenIfNecessary() {
        if (readCF == null) {
            CompilerDirectives.transferToInterpreter();
            ArchitecturalState state = getContextReference().get().getState();
            RegisterAccessFactory regs = state.getRegisters();
            readCF = regs.getCF().createRead();
            readPF = regs.getPF().createRead();
            readAF = regs.getAF().createRead();
            readZF = regs.getZF().createRead();
            readSF = regs.getSF().createRead();
            readDF = regs.getDF().createRead();
            readOF = regs.getOF().createRead();
        }
    }

    private long getRFLAGS(VirtualFrame frame) {
        createChildrenIfNecessary();
        boolean cf = readCF.execute(frame);
        boolean pf = readPF.execute(frame);
        boolean af = readAF.execute(frame);
        boolean zf = readZF.execute(frame);
        boolean sf = readSF.execute(frame);
        boolean df = readDF.execute(frame);
        boolean of = readOF.execute(frame);
        return bit(Flags.CF, cf) | bit(Flags.PF, pf) | bit(Flags.AF, af) | bit(Flags.ZF, zf) | bit(Flags.SF, sf) | bit(Flags.DF, df) | bit(Flags.OF, of) | RESERVED;
    }

    @Override
    public byte executeI8(VirtualFrame frame) {
        return (byte) getRFLAGS(frame);
    }

    @Override
    public short executeI16(VirtualFrame frame) {
        return (short) getRFLAGS(frame);
    }

    @Override
    public int executeI32(VirtualFrame frame) {
        return (int) getRFLAGS(frame);
    }

    @Override
    public long executeI64(VirtualFrame frame) {
        return getRFLAGS(frame);
    }
}
