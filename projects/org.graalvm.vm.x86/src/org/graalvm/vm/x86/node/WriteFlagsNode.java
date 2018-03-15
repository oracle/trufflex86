package org.graalvm.vm.x86.node;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.RegisterAccessFactory;
import org.graalvm.vm.x86.isa.Flags;

import com.everyware.util.BitTest;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public class WriteFlagsNode extends WriteNode {
    @Child private WriteFlagNode writeCF;
    @Child private WriteFlagNode writePF;
    @Child private WriteFlagNode writeAF;
    @Child private WriteFlagNode writeZF;
    @Child private WriteFlagNode writeSF;
    @Child private WriteFlagNode writeDF;
    @Child private WriteFlagNode writeOF;

    private static long bit(long shift) {
        return bit(shift, true);
    }

    private static long bit(long shift, boolean value) {
        return value ? (1L << shift) : 0;
    }

    private void createChildrenIfNecessary() {
        if (writeCF == null) {
            CompilerDirectives.transferToInterpreter();
            ArchitecturalState state = getContextReference().get().getState();
            RegisterAccessFactory regs = state.getRegisters();
            writeCF = regs.getCF().createWrite();
            writePF = regs.getPF().createWrite();
            writeAF = regs.getAF().createWrite();
            writeZF = regs.getZF().createWrite();
            writeSF = regs.getSF().createWrite();
            writeDF = regs.getDF().createWrite();
            writeOF = regs.getOF().createWrite();
        }
    }

    @Override
    public void executeI8(VirtualFrame frame, byte value) {
        createChildrenIfNecessary();
        boolean cf = BitTest.test(value, bit(Flags.CF));
        boolean pf = BitTest.test(value, bit(Flags.PF));
        boolean af = BitTest.test(value, bit(Flags.AF));
        boolean zf = BitTest.test(value, bit(Flags.ZF));
        boolean sf = BitTest.test(value, bit(Flags.SF));
        writeCF.execute(frame, cf);
        writePF.execute(frame, pf);
        writeAF.execute(frame, af);
        writeZF.execute(frame, zf);
        writeSF.execute(frame, sf);
    }

    @Override
    public void executeI16(VirtualFrame frame, short value) {
        createChildrenIfNecessary();
        boolean cf = BitTest.test(value, bit(Flags.CF));
        boolean pf = BitTest.test(value, bit(Flags.PF));
        boolean af = BitTest.test(value, bit(Flags.AF));
        boolean zf = BitTest.test(value, bit(Flags.ZF));
        boolean sf = BitTest.test(value, bit(Flags.SF));
        boolean df = BitTest.test(value, bit(Flags.DF));
        boolean of = BitTest.test(value, bit(Flags.OF));
        writeCF.execute(frame, cf);
        writePF.execute(frame, pf);
        writeAF.execute(frame, af);
        writeZF.execute(frame, zf);
        writeSF.execute(frame, sf);
        writeDF.execute(frame, df);
        writeOF.execute(frame, of);
    }

    @Override
    public void executeI32(VirtualFrame frame, int value) {
        executeI16(frame, (short) value);
    }

    @Override
    public void executeI64(VirtualFrame frame, long value) {
        executeI16(frame, (short) value);
    }
}
