package org.graalvm.vm.x86.node.flow;

import static org.graalvm.vm.x86.Options.getBoolean;
import static org.graalvm.vm.x86.util.Debug.printf;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.graalvm.vm.memory.util.HexFormatter;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.CpuRuntimeException;
import org.graalvm.vm.x86.Options;
import org.graalvm.vm.x86.SymbolResolver;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.IndirectException;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.isa.ReturnException;
import org.graalvm.vm.x86.isa.instruction.Call;
import org.graalvm.vm.x86.isa.instruction.Rdtsc;
import org.graalvm.vm.x86.isa.instruction.Rep;
import org.graalvm.vm.x86.node.AMD64Node;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.RegisterReadNode;
import org.graalvm.vm.x86.node.RegisterWriteNode;
import org.graalvm.vm.x86.node.WriteNode;
import org.graalvm.vm.x86.node.debug.PrintArgumentsNode;
import org.graalvm.vm.x86.node.debug.PrintStateNode;
import org.graalvm.vm.x86.posix.ProcessExitException;

import com.everyware.posix.elf.Symbol;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.ExplodeLoop.LoopExplosionKind;

public class AMD64BasicBlock extends AMD64Node {
    @CompilationFinal private static boolean DEBUG = getBoolean(Options.DEBUG_EXEC);
    @CompilationFinal private static boolean PRINT_SYMBOL = getBoolean(Options.DEBUG_PRINT_SYMBOLS);
    @CompilationFinal private static boolean PRINT_STATE = getBoolean(Options.DEBUG_PRINT_STATE);
    @CompilationFinal private static boolean PRINT_ONCE = getBoolean(Options.DEBUG_PRINT_ONCE);
    @CompilationFinal private static boolean PRINT_ARGS = getBoolean(Options.DEBUG_PRINT_ARGS);

    @CompilationFinal private static boolean DEBUG_COMPILER = false;

    @Child private PrintStateNode printState;
    @Child private PrintArgumentsNode printArgs;
    @CompilationFinal private SymbolResolver symbolResolver;

    @Child private ReadNode readInstructionCount;
    @Child private WriteNode writeInstructionCount;

    @Children private AMD64Instruction[] instructions;
    @CompilationFinal(dimensions = 1) private AMD64BasicBlock[] successors;

    @CompilationFinal private FrameSlot instructionCount;

    private boolean visited = false;

    @CompilationFinal public long index;

    public final boolean indirect;
    @CompilationFinal public long pc1;
    @CompilationFinal public long pc2;

    @CompilationFinal public int successor1;
    @CompilationFinal public int successor2;

    public AMD64BasicBlock(AMD64Instruction[] instructions) {
        assert instructions.length > 0;
        this.instructions = instructions;
        if (DEBUG_COMPILER) {
            printf("0x%016x: SIZE=%d\n", instructions[0].getPC(), instructions.length);
        }
        AMD64Instruction insn = getLastInstruction();
        indirect = insn.isControlFlow() && insn.getBTA() == null;
        if (insn.isControlFlow() && !indirect) {
            long[] bta = insn.getBTA();
            assert bta != null;
            assert bta.length > 0;
            assert bta.length <= 2;
            pc1 = bta[0];
            if (bta.length > 1) {
                pc2 = bta[1];
            } else {
                pc2 = pc1;
            }
        } else {
            pc1 = insn.next();
            pc2 = insn.next();
        }
    }

    public boolean isIndirect() {
        return indirect;
    }

    public boolean contains(long address) {
        for (AMD64Instruction insn : instructions) {
            if (insn.getPC() == address) {
                return true;
            }
        }
        return false;
    }

    public void setSuccessors(AMD64BasicBlock[] successors) {
        this.successors = successors;
    }

    public AMD64BasicBlock[] getSuccessors() {
        return successors;
    }

    @ExplodeLoop(kind = LoopExplosionKind.FULL_EXPLODE_UNTIL_RETURN)
    public AMD64BasicBlock getSuccessor(long pc) {
        if (successors == null) {
            return null;
        }
        for (AMD64BasicBlock block : successors) {
            if (block.getAddress() == pc) {
                CompilerAsserts.partialEvaluationConstant(block);
                return block;
            }
        }
        return null;
    }

    public long[] getBTA() {
        return instructions[instructions.length - 1].getBTA();
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public long getIndex() {
        return index;
    }

    public long getAddress() {
        CompilerAsserts.partialEvaluationConstant(this);
        CompilerAsserts.partialEvaluationConstant(instructions);
        CompilerAsserts.partialEvaluationConstant(instructions[0]);
        long addr = instructions[0].getPC();
        CompilerAsserts.partialEvaluationConstant(addr);
        return addr;
    }

    public int getInstructionCount() {
        return instructions.length;
    }

    public AMD64Instruction getInstruction(long pc) {
        if (!contains(pc)) {
            return null;
        }
        for (AMD64Instruction insn : instructions) {
            if (insn.getPC() == pc) {
                return insn;
            }
        }
        return null;
    }

    @TruffleBoundary
    private void trace(long pc, AMD64Instruction insn) {
        if (PRINT_SYMBOL) {
            Symbol sym = symbolResolver.getSymbol(pc);
            String func = sym == null ? "" : sym.getName();
            if (PRINT_STATE) {
                System.out.println("----------------\nIN: " + func);
            } else if (sym != null) {
                System.out.println(sym.getName() + ":");
            }
            System.out.println("0x" + HexFormatter.tohex(pc, 8) + ":\t" + insn + "\n");
        } else {
            System.out.println("0x" + HexFormatter.tohex(pc, 8) + ":\t" + insn);
        }
    }

    private void debug(VirtualFrame frame, long pc, AMD64Instruction insn) {
        if (DEBUG && (!PRINT_ONCE || !visited)) {
            if (symbolResolver == null) {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                symbolResolver = getContextReference().get().getSymbolResolver();
            }
            trace(pc, insn);
        }
        if (DEBUG && (!PRINT_ONCE || !visited) && PRINT_STATE) {
            if (printState == null) {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                printState = insert(new PrintStateNode());
            }
            if (!PRINT_ONCE || !visited) {
                printState.execute(frame, pc);
            }
        }
    }

    private void updateInstructionCount(VirtualFrame frame, long n) {
        if (instructionCount == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            ArchitecturalState state = getContextReference().get().getState();
            instructionCount = state.getInstructionCount();
            readInstructionCount = new RegisterReadNode(instructionCount);
            writeInstructionCount = new RegisterWriteNode(instructionCount);
        }
        long cnt = readInstructionCount.executeI64(frame);
        cnt += n;
        writeInstructionCount.executeI64(frame, cnt);
    }

    @TruffleBoundary
    private void dump() {
        System.out.println(this);
    }

    public boolean executeBlock(VirtualFrame frame) {
        long pc = execute(frame);
        if (isIndirect()) {
            throw new IndirectException(pc);
        } else if (pc == pc1) {
            return true;
        } else if (pc == pc2) {
            return false;
        } else {
            CompilerDirectives.transferToInterpreter();
            throw new AssertionError();
        }
    }

    @ExplodeLoop
    public long execute(VirtualFrame frame) {
        if (DEBUG_COMPILER) {
            if (CompilerDirectives.inInterpreter()) {
                printf("0x%016x: interpreter (%d insns)\n", instructions[0].getPC(), instructions.length);
            } else {
                printf("0x%016x: compiled code (%d insns)\n", instructions[0].getPC(), instructions.length);
            }
        }
        long pc = getAddress();
        long n = 0;
        CompilerAsserts.partialEvaluationConstant(pc);
        try {
            for (AMD64Instruction insn : instructions) {
                if (DEBUG) {
                    debug(frame, pc, insn);
                }
                // rdtsc/call needs current instruction count
                if (insn instanceof Rdtsc || insn instanceof Call) {
                    updateInstructionCount(frame, n);
                    n = 0;
                }
                pc = insn.executeInstruction(frame);
                if (!(insn instanceof Rep)) {
                    n++;
                }
                if (DEBUG && PRINT_ARGS && insn instanceof Call) {
                    if (printArgs == null) {
                        CompilerDirectives.transferToInterpreterAndInvalidate();
                        printArgs = insert(new PrintArgumentsNode());
                    }
                    printArgs.execute(frame, pc);
                }
            }
        } catch (ProcessExitException | ReturnException e) {
            updateInstructionCount(frame, n);
            throw e;
        } catch (Throwable t) {
            updateInstructionCount(frame, n);
            CompilerDirectives.transferToInterpreter();
            throw new CpuRuntimeException(pc, t);
        }
        if (DEBUG && PRINT_ONCE) {
            visited = true;
        }
        updateInstructionCount(frame, n);
        return pc;
    }

    public AMD64Instruction getLastInstruction() {
        return instructions[instructions.length - 1];
    }

    public AMD64BasicBlock split(long address) {
        CompilerDirectives.transferToInterpreterAndInvalidate();
        assert instructions.length > 1;
        assert address != getAddress();
        for (int i = 0; i < instructions.length; i++) {
            if (instructions[i].getPC() == address) {
                AMD64Instruction[] head = Arrays.copyOf(instructions, i);
                AMD64Instruction[] tail = new AMD64Instruction[instructions.length - i];
                System.arraycopy(instructions, i, tail, 0, tail.length);
                assert head.length + tail.length == instructions.length;
                assert head.length > 0;
                assert tail.length > 0;
                instructions = head;
                AMD64BasicBlock result = new AMD64BasicBlock(tail);
                result.setSuccessors(successors);
                result.successor1 = successor1;
                result.successor2 = successor2;
                pc1 = result.getAddress();
                pc2 = result.getAddress();
                successor1 = -1;
                successor2 = -1;
                successors = new AMD64BasicBlock[]{result};
                return result;
            }
        }
        return null;
    }

    public Set<Register> getGPRReads() {
        Set<Register> written = new HashSet<>();
        return getGPRReads(written);
    }

    public Set<Register> getGPRReads(Set<Register> written) {
        CompilerAsserts.neverPartOfCompilation();
        Set<Register> regs = new HashSet<>();
        for (AMD64Instruction insn : instructions) {
            Register[] read = insn.getUsedGPRRead();
            Register[] write = insn.getUsedGPRWrite();
            for (Register r : read) {
                if (!written.contains(r)) {
                    regs.add(r);
                }
            }
            for (Register r : write) {
                written.add(r);
            }
        }
        return regs;
    }

    public Set<Register> getGPRWrites() {
        CompilerAsserts.neverPartOfCompilation();
        Set<Register> regs = new HashSet<>();
        for (AMD64Instruction insn : instructions) {
            Register[] write = insn.getUsedGPRWrite();
            for (Register r : write) {
                regs.add(r);
            }
        }
        return regs;
    }

    public Set<Integer> getAVXReads() {
        Set<Integer> written = new HashSet<>();
        return getAVXReads(written);
    }

    public Set<Integer> getAVXReads(Set<Integer> written) {
        CompilerAsserts.neverPartOfCompilation();
        Set<Integer> regs = new HashSet<>();
        for (AMD64Instruction insn : instructions) {
            int[] read = insn.getUsedAVXRead();
            int[] write = insn.getUsedAVXWrite();
            for (Integer r : read) {
                if (!written.contains(r)) {
                    regs.add(r);
                }
            }
            for (int r : write) {
                written.add(r);
            }
        }
        return regs;
    }

    public Set<Integer> getAVXWrites() {
        CompilerAsserts.neverPartOfCompilation();
        Set<Integer> regs = new HashSet<>();
        for (AMD64Instruction insn : instructions) {
            int[] write = insn.getUsedAVXWrite();
            for (Integer r : write) {
                regs.add(r);
            }
        }
        return regs;
    }

    @Override
    public String toString() {
        CompilerAsserts.neverPartOfCompilation();
        StringBuilder buf = new StringBuilder(String.format("%016x:\n", instructions[0].getPC()));
        for (AMD64Instruction insn : instructions) {
            buf.append(insn).append('\n');
        }
        return buf.toString();
    }
}
