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

public class Pshufd extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;
    private final byte order;

    @Child private ReadNode readSrc;
    @Child private WriteNode writeDst;

    protected Pshufd(long pc, byte[] instruction, Operand operand1, Operand operand2, byte order) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.order = order;
    }

    private int getOrder(int index) {
        int shift = index << 1;
        return 3 - ((order >> shift) & 0x3);
    }

    public Pshufd(long pc, byte[] instruction, OperandDecoder operands, byte order) {
        this(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128), order);
    }

    private void createChildrenIfNecessary() {
        if (readSrc == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            ArchitecturalState state = getContextReference().get().getState();
            readSrc = operand2.createRead(state, next());
            writeDst = operand1.createWrite(state, next());
        }
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        createChildrenIfNecessary();
        Vector128 src = readSrc.executeI128(frame);
        Vector128 dst = new Vector128();
        dst.setI32(0, src.getI32(getOrder(3)));
        dst.setI32(1, src.getI32(getOrder(2)));
        dst.setI32(2, src.getI32(getOrder(1)));
        dst.setI32(3, src.getI32(getOrder(0)));
        writeDst.executeI128(frame, dst);
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"pshufd", operand1.toString(), operand2.toString(), String.format("0x%x", Byte.toUnsignedInt(order))};
    }
}
