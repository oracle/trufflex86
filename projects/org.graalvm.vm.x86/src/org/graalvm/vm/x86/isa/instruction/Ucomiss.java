package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.RegisterAccessFactory;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteFlagNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public class Ucomiss extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child private ReadNode readA;
    @Child private ReadNode readB;

    @Child protected WriteFlagNode writeCF;
    @Child protected WriteFlagNode writeOF;
    @Child protected WriteFlagNode writeSF;
    @Child protected WriteFlagNode writeZF;
    @Child protected WriteFlagNode writePF;
    @Child protected WriteFlagNode writeAF;

    protected Ucomiss(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    public Ucomiss(long pc, byte[] instruction, OperandDecoder operands) {
        this(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128));
    }

    private void createChildrenIfNecessary() {
        if (readA == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            ArchitecturalState state = getContextReference().get().getState();
            RegisterAccessFactory regs = state.getRegisters();
            readA = operand1.createRead(state, next());
            readB = operand2.createRead(state, next());
            writeCF = regs.getCF().createWrite();
            writeOF = regs.getOF().createWrite();
            writeSF = regs.getSF().createWrite();
            writeZF = regs.getZF().createWrite();
            writePF = regs.getPF().createWrite();
            writeAF = regs.getAF().createWrite();
        }
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        createChildrenIfNecessary();
        float a = readA.executeF32(frame);
        float b = readB.executeF32(frame);
        if (Double.isNaN(a) || Double.isNaN(b)) { // unordered
            writeZF.execute(frame, true);
            writePF.execute(frame, true);
            writeCF.execute(frame, true);
        } else if (a > b) {
            writeZF.execute(frame, false);
            writePF.execute(frame, false);
            writeCF.execute(frame, false);
        } else if (a < b) {
            writeZF.execute(frame, false);
            writePF.execute(frame, false);
            writeCF.execute(frame, true);
        } else if (a == b) {
            writeZF.execute(frame, true);
            writePF.execute(frame, false);
            writeCF.execute(frame, false);
        } else {
            CompilerDirectives.transferToInterpreter();
            throw new AssertionError();
        }
        writeOF.execute(frame, false);
        writeSF.execute(frame, false);
        writeAF.execute(frame, false);
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"ucomiss", operand1.toString(), operand2.toString()};
    }
}
