package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.profiles.ConditionProfile;

public class Subss extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child private ReadNode readA;
    @Child private ReadNode readB;
    @Child private WriteNode writeDst;

    private final ConditionProfile profile = ConditionProfile.createCountingProfile();

    protected Subss(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    public Subss(long pc, byte[] instruction, OperandDecoder operands) {
        this(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128));
    }

    private void createChildrenIfNecessary() {
        if (readA == null) {
            CompilerDirectives.transferToInterpreter();
            ArchitecturalState state = getContextReference().get().getState();
            readA = operand1.createRead(state, next());
            readB = operand2.createRead(state, next());
            writeDst = operand1.createWrite(state, next());
        }
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        createChildrenIfNecessary();
        float a = readA.executeF32(frame);
        float b = readB.executeF32(frame);
        // TODO: rounding mode
        float x = a - b;
        if (profile.profile(!Float.isFinite(x))) {
            if (Float.isNaN(a) && Float.isNaN(b)) {
                x = a;
            } else if (Float.isInfinite(a) && Float.isInfinite(b)) {
                float signA = Math.copySign(1.0f, a);
                float signB = Math.copySign(1.0f, b);
                if (signA == signB) {
                    x = Float.NaN;
                }
            } else if (Float.isNaN(a) && Float.isNaN(b)) {
                x = Math.min(a, b);
            }
        }
        writeDst.executeF32(frame, x);
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"subss", operand1.toString(), operand2.toString()};
    }
}
