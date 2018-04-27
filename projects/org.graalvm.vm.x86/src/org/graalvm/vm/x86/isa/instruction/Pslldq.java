package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.ImmediateOperand;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public class Pslldq extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child private ReadNode readSrc;
    @Child private WriteNode writeDst;

    private final int shift;

    protected Pslldq(long pc, byte[] instruction, Operand operand1, int shift) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = new ImmediateOperand(shift);
        this.shift = shift;

        setGPRReadOperands(operand1);
        setGPRWriteOperands(operand1);
    }

    public Pslldq(long pc, byte[] instruction, OperandDecoder operands, int shift) {
        this(pc, instruction, operands.getAVXOperand1(128), shift);
    }

    private void createChildrenIfNecessary() {
        if (readSrc == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            ArchitecturalState state = getContextReference().get().getState();
            readSrc = operand1.createRead(state, next());
            writeDst = operand1.createWrite(state, next());
        }
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        createChildrenIfNecessary();
        Vector128 value = readSrc.executeI128(frame);
        Vector128 result;
        if (shift > 15) {
            result = Vector128.ZERO;
        } else {
            result = value.shrBytes(shift);
        }
        writeDst.executeI128(frame, result);
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"pslldq", operand1.toString(), operand2.toString()};
    }
}
