package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public class Unpckhpd extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child private ReadNode readSrc1;
    @Child private ReadNode readSrc2;
    @Child private WriteNode writeDst;

    protected Unpckhpd(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    public Unpckhpd(long pc, byte[] instruction, OperandDecoder operands) {
        this(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128));
    }

    private void createChildrenIfNecessary() {
        if (readSrc1 == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            ArchitecturalState state = getContextReference().get().getState();
            readSrc1 = operand1.createRead(state, next());
            readSrc2 = operand1.createRead(state, next());
            writeDst = operand1.createWrite(state, next());
        }
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        createChildrenIfNecessary();
        Vector128 a = readSrc1.executeI128(frame);
        Vector128 b = readSrc2.executeI128(frame);
        double low = a.getF64(0);
        double high = b.getF64(0);
        Vector128 result = new Vector128(high, low);
        writeDst.executeI128(frame, result);
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"unpckhpd", operand1.toString(), operand2.toString()};
    }
}
