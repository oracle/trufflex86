package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AVXRegisterOperand;
import org.graalvm.vm.x86.isa.MemoryOperand;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.AddressComputationNode;
import org.graalvm.vm.x86.node.MemoryWriteNode;
import org.graalvm.vm.x86.node.ReadNode;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;

public class Fxsave extends AMD64Instruction {
    private final MemoryOperand operand;

    @Child private AddressComputationNode address;
    @Child private MemoryWriteNode memory;
    @Children private ReadNode[] readXMM;

    private Fxsave(long pc, byte[] instruction, Operand operand) {
        super(pc, instruction);
        this.operand = (MemoryOperand) operand;

        Operand[] readOperands = new Operand[16];
        for (int i = 0; i < readOperands.length; i++) {
            readOperands[i] = new AVXRegisterOperand(i, 128);
        }
        setGPRReadOperands(readOperands);
        setGPRWriteOperands(operand);
    }

    public Fxsave(long pc, byte[] instruction, OperandDecoder operands) {
        this(pc, instruction, operands.getOperand1(OperandDecoder.R64));
    }

    @Override
    protected void createChildNodes() {
        ArchitecturalState state = getState();
        address = new AddressComputationNode(state, operand, next());
        memory = state.createMemoryWrite();
        readXMM = new ReadNode[16];
        for (int i = 0; i < readXMM.length; i++) {
            readXMM[i] = state.getRegisters().getAVXRegister(i).createRead();
        }
    }

    @Override
    @ExplodeLoop
    public long executeInstruction(VirtualFrame frame) {
        long addr = address.execute(frame);
        long ptr = addr + 160;
        for (int i = 0; i < readXMM.length; i++) {
            Vector128 xmm = readXMM[i].executeI128(frame);
            memory.executeI128(ptr, xmm);
            ptr += 16;
        }
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"fxsave", operand.toString()};
    }
}
