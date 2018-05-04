package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;

public class Shufps extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;
    private final int imm;

    @CompilationFinal(dimensions = 1) private final int[] sel;

    @Child private ReadNode readSrc;
    @Child private ReadNode readDst;
    @Child private WriteNode writeDst;

    protected Shufps(long pc, byte[] instruction, Operand operand1, Operand operand2, int imm) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.imm = imm;

        sel = new int[4];
        sel[0] = (imm >> 6) & 0x03;
        sel[1] = (imm >> 4) & 0x03;
        sel[2] = (imm >> 2) & 0x03;
        sel[3] = imm & 0x03;

        setGPRReadOperands(operand1, operand2);
        setGPRWriteOperands(operand1);
    }

    public Shufps(long pc, byte[] instruction, OperandDecoder operands, byte imm) {
        this(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128), Byte.toUnsignedInt(imm));
    }

    private void createChildrenIfNecessary() {
        if (readSrc == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            ArchitecturalState state = getContextReference().get().getState();
            readSrc = operand2.createRead(state, next());
            readDst = operand1.createRead(state, next());
            writeDst = operand1.createWrite(state, next());
        }
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        createChildrenIfNecessary();
        Vector128 src = readSrc.executeI128(frame);
        Vector128 dst = readDst.executeI128(frame);
        Vector128 result = new Vector128();
        result.setI32(0, src.getI32(3 - sel[0]));
        result.setI32(1, src.getI32(3 - sel[1]));
        result.setI32(2, dst.getI32(3 - sel[2]));
        result.setI32(3, dst.getI32(3 - sel[3]));
        writeDst.executeI128(frame, result);
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"shufps", operand1.toString(), operand2.toString(), String.format("0x%x", imm)};
    }
}
