package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.util.io.Endianess;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Punpckh extends AMD64Instruction {
    private final String name;
    private final Operand operand1;
    private final Operand operand2;

    @Child protected ReadNode readOp1;
    @Child protected ReadNode readOp2;
    @Child protected WriteNode writeDst;

    protected Punpckh(long pc, byte[] instruction, String name, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.name = name;
        this.operand1 = operand1;
        this.operand2 = operand2;

        setGPRReadOperands(operand1, operand2);
        setGPRWriteOperands(operand1);
    }

    @Override
    protected void createChildNodes() {
        ArchitecturalState state = getState();
        readOp1 = operand1.createRead(state, next());
        readOp2 = operand2.createRead(state, next());
        writeDst = operand1.createWrite(state, next());
    }

    public static class Punpckhbw extends Punpckh {
        public Punpckhbw(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, "punpckhbw", operands.getAVXOperand2(128), operands.getAVXOperand1(128));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            Vector128 a = readOp1.executeI128(frame);
            Vector128 b = readOp2.executeI128(frame);
            long la = a.getI64(0);
            long lb = b.getI64(0);
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

    public static class Punpckhwd extends Punpckh {
        public Punpckhwd(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, "punpckhwd", operands.getAVXOperand2(128), operands.getAVXOperand1(128));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            Vector128 a = readOp1.executeI128(frame);
            Vector128 b = readOp2.executeI128(frame);
            short[] sa = a.getShorts();
            short[] sb = b.getShorts();
            short[] merged = {sb[0], sa[0], sb[1], sa[1], sb[2], sa[2], sb[3], sa[3]};
            Vector128 out = new Vector128(merged);
            writeDst.executeI128(frame, out);
            return next();
        }
    }

    public static class Punpckhdq extends Punpckh {
        public Punpckhdq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, "punpckhdq", operands.getAVXOperand2(128), operands.getAVXOperand1(128));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            Vector128 dst = readOp1.executeI128(frame);
            Vector128 src = readOp2.executeI128(frame);
            int h0 = src.getI32(0);
            int h1 = dst.getI32(0);
            int h2 = src.getI32(1);
            int h3 = dst.getI32(1);
            Vector128 out = new Vector128(h0, h1, h2, h3);
            writeDst.executeI128(frame, out);
            return next();
        }
    }

    public static class Punpckhqdq extends Punpckh {
        public Punpckhqdq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, "punpckhqdq", operands.getAVXOperand2(128), operands.getAVXOperand1(128));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            Vector128 dst = readOp1.executeI128(frame);
            Vector128 src = readOp2.executeI128(frame);
            long lo = dst.getI64(0);
            long hi = src.getI64(0);
            Vector128 out = new Vector128(hi, lo);
            writeDst.executeI128(frame, out);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{name, operand1.toString(), operand2.toString()};
    }
}
