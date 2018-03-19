package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.math.LongDivision;
import org.graalvm.vm.x86.math.LongDivision.Result;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Div extends AMD64Instruction {
    protected final Operand operand;

    @Child protected ReadNode readOp;

    protected Div(long pc, byte[] instruction, Operand operand) {
        super(pc, instruction);
        this.operand = operand;
    }

    public static class Divb extends Div {
        @Child private ReadNode readAX;
        @Child private WriteNode writeAX;

        public Divb(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R8));
        }

        private void createChildrenIfNecessary() {
            if (readAX == null) {
                CompilerDirectives.transferToInterpreter();
                ArchitecturalState state = getContextReference().get().getState();
                readAX = state.getRegisters().getRegister(Register.AX).createRead();
                readOp = operand.createRead(state, next());
                writeAX = state.getRegisters().getRegister(Register.AX).createWrite();
            }
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            int ax = Short.toUnsignedInt(readAX.executeI16(frame));
            int op = Byte.toUnsignedInt(readOp.executeI8(frame));
            if (op == 0) {
                CompilerDirectives.transferToInterpreter();
                throw new ArithmeticException("Division by zero"); // TODO: #DE
            }
            int q = ax / op;
            int r = ax % op;
            if (q > 0xFF) {
                CompilerDirectives.transferToInterpreter();
                throw new RuntimeException("Integer overflow"); // TODO: #DE
            }
            short result = (short) ((q & 0xFF) | ((r & 0xFF) << 8));
            writeAX.executeI16(frame, result);
            return next();
        }
    }

    public static class Divw extends Div {
        @Child private ReadNode readAX;
        @Child private ReadNode readDX;
        @Child private WriteNode writeAX;
        @Child private WriteNode writeDX;

        public Divw(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R16));
        }

        private void createChildrenIfNecessary() {
            if (readAX == null) {
                CompilerDirectives.transferToInterpreter();
                ArchitecturalState state = getContextReference().get().getState();
                readAX = state.getRegisters().getRegister(Register.AX).createRead();
                readDX = state.getRegisters().getRegister(Register.DX).createRead();
                readOp = operand.createRead(state, next());
                writeAX = state.getRegisters().getRegister(Register.AX).createWrite();
                writeDX = state.getRegisters().getRegister(Register.DX).createWrite();
            }
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            short ax = readAX.executeI16(frame);
            short dx = readDX.executeI16(frame);
            int input = Short.toUnsignedInt(ax) | (Short.toUnsignedInt(dx) << 16);
            int op = Short.toUnsignedInt(readOp.executeI16(frame));
            if (op == 0) {
                CompilerDirectives.transferToInterpreter();
                throw new ArithmeticException("Division by zero"); // TODO: #DE
            }
            int q = Integer.divideUnsigned(input, op);
            int r = Integer.remainderUnsigned(input, op);
            if (q > 0xFFFF) {
                CompilerDirectives.transferToInterpreter();
                throw new RuntimeException("Integer overflow"); // TODO: #DE
            }
            writeAX.executeI16(frame, (short) q);
            writeDX.executeI16(frame, (short) r);
            return next();
        }
    }

    public static class Divl extends Div {
        @Child private ReadNode readEAX;
        @Child private ReadNode readEDX;
        @Child private WriteNode writeEAX;
        @Child private WriteNode writeEDX;

        public Divl(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R32));
        }

        private void createChildrenIfNecessary() {
            if (readEAX == null) {
                CompilerDirectives.transferToInterpreter();
                ArchitecturalState state = getContextReference().get().getState();
                readEAX = state.getRegisters().getRegister(Register.EAX).createRead();
                readEDX = state.getRegisters().getRegister(Register.EDX).createRead();
                readOp = operand.createRead(state, next());
                writeEAX = state.getRegisters().getRegister(Register.EAX).createWrite();
                writeEDX = state.getRegisters().getRegister(Register.EDX).createWrite();
            }
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            int eax = readEAX.executeI32(frame);
            int edx = readEDX.executeI32(frame);
            long input = Integer.toUnsignedLong(eax) | (Integer.toUnsignedLong(edx) << 32);
            long op = Integer.toUnsignedLong(readOp.executeI32(frame));
            if (op == 0) {
                CompilerDirectives.transferToInterpreter();
                throw new ArithmeticException("Division by zero"); // TODO: #DE
            }
            long q = Long.divideUnsigned(input, op);
            long r = Long.remainderUnsigned(input, op);
            if (q > 0xFFFFFFFFL) {
                CompilerDirectives.transferToInterpreter();
                throw new RuntimeException("Integer overflow"); // TODO: #DE
            }
            writeEAX.executeI32(frame, (int) q);
            writeEDX.executeI32(frame, (int) r);
            return next();
        }
    }

    public static class Divq extends Div {
        @Child private ReadNode readRAX;
        @Child private ReadNode readRDX;
        @Child private WriteNode writeRAX;
        @Child private WriteNode writeRDX;

        public Divq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R64));
        }

        private void createChildrenIfNecessary() {
            if (readRAX == null) {
                CompilerDirectives.transferToInterpreter();
                ArchitecturalState state = getContextReference().get().getState();
                readRAX = state.getRegisters().getRegister(Register.RAX).createRead();
                readRDX = state.getRegisters().getRegister(Register.RDX).createRead();
                readOp = operand.createRead(state, next());
                writeRAX = state.getRegisters().getRegister(Register.RAX).createWrite();
                writeRDX = state.getRegisters().getRegister(Register.RDX).createWrite();
            }
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            long rax = readRAX.executeI64(frame);
            long rdx = readRDX.executeI64(frame);
            long op = readOp.executeI64(frame);
            if (op == 0) {
                CompilerDirectives.transferToInterpreter();
                throw new ArithmeticException("Division by zero"); // TODO: #DE
            }
            long q;
            long r;
            if (rdx != 0) {
                Result result = LongDivision.divs128by64(rdx, rax, op);
                q = result.quotient;
                r = result.remainder;
            } else {
                q = Long.divideUnsigned(rax, op);
                r = Long.remainderUnsigned(rax, op);
            }
            writeRAX.executeI64(frame, q);
            writeRDX.executeI64(frame, r);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"div", operand.toString()};
    }
}
