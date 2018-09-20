package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public class Pshufhw extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;
    private final byte order;

    @Child private ReadNode readSrc;
    @Child private WriteNode writeDst;

    protected Pshufhw(long pc, byte[] instruction, Operand operand1, Operand operand2, byte order) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.order = order;

        setGPRReadOperands(operand2);
        setGPRWriteOperands(operand1);
    }

    private int getOrder(int index) {
        int shift = index << 1;
        return 3 - ((order >> shift) & 0x3);
    }

    public Pshufhw(long pc, byte[] instruction, OperandDecoder operands, byte order) {
        this(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128), order);
    }

    @Override
    protected void createChildNodes() {
        ArchitecturalState state = getState();
        readSrc = operand2.createRead(state, next());
        writeDst = operand1.createWrite(state, next());
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        Vector128 src = readSrc.executeI128(frame);
        short i0 = src.getI16(getOrder(3));
        short i1 = src.getI16(getOrder(2));
        short i2 = src.getI16(getOrder(1));
        short i3 = src.getI16(getOrder(0));
        long hi = (Short.toUnsignedLong(i0) << 48) | (Short.toUnsignedLong(i1) << 32) | (Short.toUnsignedLong(i2) << 16) | Short.toUnsignedLong(i3);
        Vector128 dst = new Vector128(hi, src.getI64(1));
        writeDst.executeI128(frame, dst);
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"pshufhw", operand1.toString(), operand2.toString(), String.format("0x%x", Byte.toUnsignedInt(order))};
    }
}
