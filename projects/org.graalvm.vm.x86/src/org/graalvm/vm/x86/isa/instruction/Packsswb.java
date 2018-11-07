package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public class Packsswb extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child private ReadNode readOp1;
    @Child private ReadNode readOp2;
    @Child private WriteNode writeDst;

    protected Packsswb(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;

        setGPRReadOperands(operand1, operand2);
        setGPRWriteOperands(operand1);
    }

    public Packsswb(long pc, byte[] instruction, OperandDecoder operands) {
        this(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128));
    }

    @Override
    protected void createChildNodes() {
        ArchitecturalState state = getState();
        readOp1 = operand1.createRead(state, next());
        readOp2 = operand2.createRead(state, next());
        writeDst = operand1.createWrite(state, next());
    }

    private static byte sat(short val) {
        if (val < Byte.MIN_VALUE) {
            return Byte.MIN_VALUE;
        } else if (val > Byte.MAX_VALUE) {
            return Byte.MAX_VALUE;
        } else {
            return (byte) val;
        }
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        Vector128 dst = readOp1.executeI128(frame);
        Vector128 src = readOp2.executeI128(frame);
        short[] d = dst.getShorts();
        short[] s = src.getShorts();
        byte[] bytes = {sat(s[0]), sat(s[1]), sat(s[2]), sat(s[3]), sat(s[4]), sat(s[5]), sat(s[6]), sat(s[7]), sat(d[0]), sat(d[1]), sat(d[2]), sat(d[3]), sat(d[4]), sat(d[5]), sat(d[6]), sat(d[7])};
        Vector128 result = new Vector128(bytes);
        writeDst.executeI128(frame, result);
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"packsswb", operand1.toString(), operand2.toString()};
    }
}
