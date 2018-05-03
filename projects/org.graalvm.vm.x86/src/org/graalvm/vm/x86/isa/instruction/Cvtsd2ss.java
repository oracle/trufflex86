package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public class Cvtsd2ss extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child protected ReadNode readSrc;
    @Child protected WriteNode writeDst;

    protected Cvtsd2ss(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;

        setGPRReadOperands(operand2);
        setGPRWriteOperands(operand1);
    }

    protected void createChildrenIfNecessary() {
        if (readSrc == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            ArchitecturalState state = getContextReference().get().getState();
            readSrc = operand2.createRead(state, next());
            writeDst = operand1.createWrite(state, next());
        }
    }

    public Cvtsd2ss(long pc, byte[] instruction, OperandDecoder operands) {
        this(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128));
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        createChildrenIfNecessary();
        double value = readSrc.executeF64(frame);
        // TODO: rounding mode
        writeDst.executeF32(frame, (float) value);
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"cvtsd2ss", operand1.toString(), operand2.toString()};
    }
}