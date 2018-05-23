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

public class Rsqrtps extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child private ReadNode readSrc;
    @Child private WriteNode writeDst;

    protected Rsqrtps(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;

        setGPRReadOperands(operand2);
        setGPRWriteOperands(operand1);
    }

    public Rsqrtps(long pc, byte[] instruction, OperandDecoder operands) {
        this(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128));
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
        float[] data = {
                        (float) (1.0 / Math.sqrt(src.getF32(0))),
                        (float) (1.0 / Math.sqrt(src.getF32(1))),
                        (float) (1.0 / Math.sqrt(src.getF32(2))),
                        (float) (1.0 / Math.sqrt(src.getF32(3))),
        };
        Vector128 dst = new Vector128(data);
        writeDst.executeI128(frame, dst);
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"rsqrtps", operand1.toString(), operand2.toString()};
    }
}
