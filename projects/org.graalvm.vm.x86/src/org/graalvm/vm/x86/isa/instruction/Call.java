package org.graalvm.vm.x86.isa.instruction;

import static org.graalvm.vm.x86.Options.getBoolean;

import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.AMD64Language;
import org.graalvm.vm.x86.AMD64Register;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.Options;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.ImmediateOperand;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.isa.RegisterOperand;
import org.graalvm.vm.x86.isa.ReturnException;
import org.graalvm.vm.x86.node.MemoryWriteNode;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.RegisterReadNode;
import org.graalvm.vm.x86.node.RegisterWriteNode;
import org.graalvm.vm.x86.node.flow.CompiledTraceInterpreter;
import org.graalvm.vm.x86.node.flow.TraceRegistry;
import org.graalvm.vm.x86.node.init.CopyToCpuStateNode;
import org.graalvm.vm.x86.node.init.InitializeFromCpuStateNode;
import org.graalvm.vm.x86.util.Debug;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Call extends AMD64Instruction {
    protected final Operand operand;

    protected final long bta;
    @Child protected ReadNode readBTA;
    @Child protected RegisterReadNode readRSP;
    @Child protected RegisterWriteNode writeRSP;
    @Child protected MemoryWriteNode writeMemory;

    @Child private CopyToCpuStateNode readState = new CopyToCpuStateNode();
    @Child private InitializeFromCpuStateNode writeState = new InitializeFromCpuStateNode();

    @CompilationFinal private TraceRegistry traces;

    @CompilationFinal public static boolean TRUFFLE_CALLS = getBoolean(Options.TRUFFLE_CALLS);

    @Child private CompiledTraceInterpreter interpreter;

    @CompilationFinal public static boolean DEBUG = false;

    protected Call(long pc, byte[] instruction, Operand target, long bta) {
        super(pc, instruction);
        this.operand = target;
        this.bta = bta;

        setGPRReadOperands(operand, new RegisterOperand(Register.RSP));
        setGPRWriteOperands(new RegisterOperand(Register.RSP));
    }

    protected void createChildrenIfNecessary() {
        if (readRSP == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            AMD64Context ctx = getContextReference().get();
            ArchitecturalState state = ctx.getState();
            AMD64Register rsp = state.getRegisters().getRegister(Register.RSP);
            readRSP = rsp.createRead();
            writeRSP = rsp.createWrite();
            writeMemory = state.createMemoryWrite();
            traces = state.getTraceRegistry();

            TruffleLanguage<AMD64Context> language = getRootNode().getLanguage(AMD64Language.class);
            interpreter = insert(new CompiledTraceInterpreter(language, ctx.getFrameDescriptor()));
        }
    }

    protected abstract long getCallTarget(VirtualFrame frame);

    public static class CallRelative extends Call {
        private static long getBTA(long pc, Operand target) {
            if (target instanceof ImmediateOperand) {
                return pc + (int) ((ImmediateOperand) target).getValue();
            } else {
                return 0;
            }
        }

        public CallRelative(long pc, byte[] instruction, Operand target) {
            super(pc, instruction, target, getBTA(pc + instruction.length, target));
        }

        @Override
        protected long getCallTarget(VirtualFrame frame) {
            if (bta != 0) {
                return bta;
            } else {
                if (readBTA == null) {
                    CompilerDirectives.transferToInterpreterAndInvalidate();
                    ArchitecturalState state = getContextReference().get().getState();
                    readBTA = operand.createRead(state, next());
                }
                return next() + readBTA.executeI64(frame);
            }
        }
    }

    public static class CallAbsolute extends Call {
        public CallAbsolute(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R64), 0);
        }

        @Override
        protected long getCallTarget(VirtualFrame frame) {
            if (readBTA == null) {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                ArchitecturalState state = getContextReference().get().getState();
                readBTA = operand.createRead(state, next());
            }
            return readBTA.executeI64(frame);
        }
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        createChildrenIfNecessary();
        long target = getCallTarget(frame);
        long rsp = readRSP.executeI64(frame);
        rsp -= 8;
        writeMemory.executeI64(rsp, next());
        writeRSP.executeI64(frame, rsp);
        if (TRUFFLE_CALLS) {
            if (DEBUG) {
                Debug.printf("call to 0x%x\n", target);
            }
            long npc = interpreter.execute(frame, target);
            if (DEBUG) {
                Debug.printf("call to 0x%x returned (0x%x vs 0x%x)\n", target, next(), npc);
            }
            if (npc != next()) {
                if (DEBUG) {
                    Debug.printf("pc does not match, throwing return exception\n");
                }
                throw new ReturnException(npc);
            }
            return next();
        } else {
            return target;
        }
    }

    @Override
    public boolean isControlFlow() {
        return !TRUFFLE_CALLS;
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"call", bta != 0 ? String.format("0x%x", bta) : operand.toString()};
    }
}
