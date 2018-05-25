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

public class Shufpd extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;
    private final int imm;

    @CompilationFinal(dimensions = 1) private final boolean[] sel;

    @Child private ReadNode readSrc;
    @Child private ReadNode readDst;
    @Child private WriteNode writeDst;

    protected Shufpd(long pc, byte[] instruction, Operand operand1, Operand operand2, int imm) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.imm = imm;

        sel = new boolean[2];
        sel[0] = (imm & 0x2) != 0;
        sel[1] = (imm & 0x1) != 0;

        setGPRReadOperands(operand1, operand2);
        setGPRWriteOperands(operand1);
    }

    public Shufpd(long pc, byte[] instruction, OperandDecoder operands, byte imm) {
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
        result.setI64(0, src.getI64(sel[0] ? 0 : 1));
        result.setI64(1, dst.getI64(sel[1] ? 0 : 1));
        writeDst.executeI128(frame, result);
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"shufpd", operand1.toString(), operand2.toString(), String.format("0x%x", imm)};
    }
}
