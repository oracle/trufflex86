package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AVXRegisterOperand;
import org.graalvm.vm.x86.isa.MemoryOperand;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.AddressComputationNode;
import org.graalvm.vm.x86.node.MemoryReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;

public class Fxrstor extends AMD64Instruction {
    private final MemoryOperand operand;

    @Child private AddressComputationNode address;
    @Child private MemoryReadNode memory;
    @Children private WriteNode[] writeXMM;

    private Fxrstor(long pc, byte[] instruction, Operand operand) {
        super(pc, instruction);
        this.operand = (MemoryOperand) operand;

        Operand[] writeOperands = new Operand[17];
        for (int i = 0; i < 16; i++) {
            writeOperands[i] = new AVXRegisterOperand(i, 128);
        }
        writeOperands[16] = operand;
        setGPRWriteOperands(writeOperands);
    }

    public Fxrstor(long pc, byte[] instruction, OperandDecoder operands) {
        this(pc, instruction, operands.getOperand1(OperandDecoder.R64));
    }

    @Override
    protected void createChildNodes() {
        ArchitecturalState state = getState();
        address = new AddressComputationNode(state, operand, next());
        memory = state.createMemoryRead();
        writeXMM = new WriteNode[16];
        for (int i = 0; i < writeXMM.length; i++) {
            writeXMM[i] = state.getRegisters().getAVXRegister(i).createWrite();
        }
    }

    @Override
    @ExplodeLoop
    public long executeInstruction(VirtualFrame frame) {
        long addr = address.execute(frame);
        long ptr = addr + 160;
        for (int i = 0; i < writeXMM.length; i++) {
            Vector128 xmm = memory.executeI128(ptr);
            writeXMM[i].executeI128(frame, xmm);
            ptr += 16;
        }
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"fxrstor", operand.toString()};
    }
}
