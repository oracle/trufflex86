package org.graalvm.vm.x86.isa.instruction;

import java.util.logging.Logger;

import org.graalvm.vm.util.log.Levels;
import org.graalvm.vm.util.log.Trace;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public class Fldcw extends AMD64Instruction {
    private static final Logger log = Trace.create(Stmxcsr.class);

    private final Operand operand;

    @Child private ReadNode readSrc;

    protected Fldcw(long pc, byte[] instruction, Operand operand) {
        super(pc, instruction);
        this.operand = operand;

        setGPRReadOperands(operand);
    }

    public Fldcw(long pc, byte[] instruction, OperandDecoder operands) {
        this(pc, instruction, operands.getOperand1(OperandDecoder.R16));
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        if (readSrc == null) { // TODO: use createChildren/createChildNodes
            CompilerDirectives.transferToInterpreterAndInvalidate();
            log.log(Levels.WARNING, String.format("Stub instruction FLDCW executed at 0x%016x", getPC()));
            ArchitecturalState state = getContextReference().get().getState();
            readSrc = operand.createRead(state, next());
        }
        // TODO: read FPU CW
        readSrc.executeI16(frame);
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"fldcw", operand.toString()};
    }
}
