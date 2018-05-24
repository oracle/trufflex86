package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.everyware.util.io.Endianess;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Punpckl extends AMD64Instruction {
    private final String name;
    private final Operand operand1;
    private final Operand operand2;

    @Child protected ReadNode readOp1;
    @Child protected ReadNode readOp2;
    @Child protected WriteNode writeDst;

    protected Punpckl(long pc, byte[] instruction, String name, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.name = name;
        this.operand1 = operand1;
        this.operand2 = operand2;

        setGPRReadOperands(operand1, operand2);
        setGPRWriteOperands(operand1);
    }

    protected void createChildrenIfNecessary() {
        if (readOp1 == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            ArchitecturalState state = getContextReference().get().getState();
            readOp1 = operand1.createRead(state, next());
            readOp2 = operand2.createRead(state, next());
            writeDst = operand1.createWrite(state, next());
        }
    }

    public static class Punpcklbw extends Punpckl {
        public Punpcklbw(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, "punpcklbw", operands.getAVXOperand2(128), operands.getAVXOperand1(128));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            Vector128 a = readOp1.executeI128(frame);
            Vector128 b = readOp2.executeI128(frame);
            long la = a.getI64(1);
            long lb = b.getI64(1);
            byte[] ba = new byte[8];
            byte[] bb = new byte[8];
            Endianess.set64bitBE(ba, 0, la);
            Endianess.set64bitBE(bb, 0, lb);
            byte[] merged = {bb[0], ba[0], bb[1], ba[1], bb[2], ba[2], bb[3], ba[3], bb[4], ba[4], bb[5], ba[5], bb[6], ba[6], bb[7], ba[7]};
            long resultH = Endianess.get64bitBE(merged);
            long resultL = Endianess.get64bitBE(merged, 8);
            Vector128 out = new Vector128(resultH, resultL);
            writeDst.executeI128(frame, out);
            return next();
        }
    }

    public static class Punpcklwd extends Punpckl {
        public Punpcklwd(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, "punpcklwd", operands.getAVXOperand2(128), operands.getAVXOperand1(128));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            Vector128 a = readOp1.executeI128(frame);
            Vector128 b = readOp2.executeI128(frame);
            short[] sa = a.getShorts();
            short[] sb = b.getShorts();
            short[] merged = {sb[4], sa[4], sb[5], sa[5], sb[6], sa[6], sb[7], sa[7]};
            Vector128 out = new Vector128(merged);
            writeDst.executeI128(frame, out);
            return next();
        }
    }

    public static class Punpckldq extends Punpckl {
        public Punpckldq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, "punpckldq", operands.getAVXOperand2(128), operands.getAVXOperand1(128));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            Vector128 a = readOp1.executeI128(frame);
            Vector128 b = readOp2.executeI128(frame);
            int ha = a.getI32(2);
            int hb = b.getI32(2);
            int la = a.getI32(3);
            int lb = b.getI32(3);
            Vector128 out = new Vector128(hb, ha, lb, la);
            writeDst.executeI128(frame, out);
            return next();
        }
    }

    public static class Punpcklqdq extends Punpckl {
        public Punpcklqdq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, "punpcklqdq", operands.getAVXOperand2(128), operands.getAVXOperand1(128));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            Vector128 dst = readOp1.executeI128(frame);
            Vector128 src = readOp2.executeI128(frame);
            long low = dst.getI64(1);
            long high = src.getI64(1);
            Vector128 out = new Vector128(high, low);
            writeDst.executeI128(frame, out);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{name, operand1.toString(), operand2.toString()};
    }
}
