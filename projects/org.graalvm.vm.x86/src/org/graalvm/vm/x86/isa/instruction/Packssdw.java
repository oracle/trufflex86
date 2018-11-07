package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public class Packssdw extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child private ReadNode readOp1;
    @Child private ReadNode readOp2;
    @Child private WriteNode writeDst;

    protected Packssdw(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;

        setGPRReadOperands(operand1, operand2);
        setGPRWriteOperands(operand1);
    }

    public Packssdw(long pc, byte[] instruction, OperandDecoder operands) {
        this(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128));
    }

    @Override
    protected void createChildNodes() {
        ArchitecturalState state = getState();
        readOp1 = operand1.createRead(state, next());
        readOp2 = operand2.createRead(state, next());
        writeDst = operand1.createWrite(state, next());
    }

    private static short sat(int val) {
        if (val < Short.MIN_VALUE) {
            return Short.MIN_VALUE;
        } else if (val > Short.MAX_VALUE) {
            return Short.MAX_VALUE;
        } else {
            return (short) val;
        }
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        Vector128 dst = readOp1.executeI128(frame);
        Vector128 src = readOp2.executeI128(frame);
        int[] d = dst.getInts();
        int[] s = src.getInts();
        short[] shorts = {sat(s[0]), sat(s[1]), sat(s[2]), sat(s[3]), sat(d[0]), sat(d[1]), sat(d[2]), sat(d[3])};
        Vector128 result = new Vector128(shorts);
        writeDst.executeI128(frame, result);
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"packssdw", operand1.toString(), operand2.toString()};
    }
}
