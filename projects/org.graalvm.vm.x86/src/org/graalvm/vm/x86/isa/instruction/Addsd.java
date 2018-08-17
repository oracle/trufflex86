package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.profiles.ConditionProfile;

public class Addsd extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child private ReadNode readA;
    @Child private ReadNode readB;
    @Child private WriteNode writeDst;

    private final ConditionProfile profile = ConditionProfile.createCountingProfile();

    private final static double NEG_NAN = Double.longBitsToDouble(0xfff8000000000000L);

    protected Addsd(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;

        setGPRReadOperands(operand2);
        setGPRWriteOperands(operand1);
    }

    public Addsd(long pc, byte[] instruction, OperandDecoder operands) {
        this(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128));
    }

    @Override
    protected void createChildNodes() {
        ArchitecturalState state = getState();
        readA = operand1.createRead(state, next());
        readB = operand2.createRead(state, next());
        writeDst = operand1.createWrite(state, next());
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        double a = readA.executeF64(frame);
        double b = readB.executeF64(frame);
        // TODO: rounding mode
        double x = a + b;
        if (profile.profile(!Double.isFinite(x))) {
            if (Double.isNaN(a) && Double.isNaN(b)) {
                x = a;
            } else if (Double.isInfinite(a) && Double.isInfinite(b)) {
                double signA = Math.copySign(1.0, a);
                double signB = Math.copySign(1.0, b);
                if (signA != signB) {
                    x = NEG_NAN;
                }
            } else if (Double.isNaN(a) && Double.isNaN(b)) {
                x = Math.min(a, b);
            }
        }
        writeDst.executeF64(frame, x);
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"addsd", operand1.toString(), operand2.toString()};
    }
}
