package org.graalvm.vm.x86.node.flow;

import static org.graalvm.vm.x86.util.Debug.printf;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.graalvm.vm.memory.MemoryPage;
import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.memory.exception.SegmentationViolation;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.CpuRuntimeException;
import org.graalvm.vm.x86.SymbolResolver;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.CodeMemoryReader;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.node.AMD64Node;
import org.graalvm.vm.x86.node.RegisterReadNode;
import org.graalvm.vm.x86.node.RegisterWriteNode;
import org.graalvm.vm.x86.posix.ProcessExitException;

import com.everyware.posix.elf.Symbol;
import com.everyware.util.log.Levels;
import com.everyware.util.log.Trace;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;

public class TraceDispatchNode extends AMD64Node {
    private static final Logger log = Trace.create(TraceDispatchNode.class);

    @CompilationFinal private boolean DEBUG = false;
    @CompilationFinal private static boolean NO_INDIRECT = true;

    @CompilationFinal private int maxBlockCount = 1;

    @Children private AMD64BasicBlock[] blocks;
    @CompilationFinal private int usedBlocks;

    @CompilationFinal private long startPC = -1;

    private final VirtualMemory memory;
    private final NavigableMap<Long, AMD64BasicBlock> blockLookup = new TreeMap<>();
    private final CodeReader reader;

    @Child private RegisterReadNode readPC;
    @Child private RegisterWriteNode writePC;

    public TraceDispatchNode(ArchitecturalState state) {
        memory = state.getMemory();
        reader = new CodeMemoryReader(memory, 0);
        readPC = state.getRegisters().getPC().createRead();
        writePC = state.getRegisters().getPC().createWrite();
        blocks = new AMD64BasicBlock[maxBlockCount + 1];
        usedBlocks = 0;
    }

    public long getStartAddress() {
        return startPC;
    }

    public AMD64BasicBlock get(long address) {
        CompilerDirectives.transferToInterpreter();
        if (DEBUG) {
            System.out.printf("resolving block at 0x%016x\n", address);
        }
        AMD64BasicBlock block = blockLookup.get(address);
        if (block == null) {
            parse(address);
            block = blockLookup.get(address);
        }
        assert block != null;
        assert block.getAddress() == address;
        return block;
    }

    private AMD64BasicBlock find(long address) {
        CompilerDirectives.transferToInterpreter();
        AMD64BasicBlock block = blockLookup.get(address);
        if (block == null) {
            if (usedBlocks >= maxBlockCount) {
                if (DEBUG) {
                    System.out.printf("cannot parse block at 0x%016x: size limit reached\n", address);
                }
                throw new TraceTooLargeException(address);
            } else {
                parse(address);
                block = blockLookup.get(address);
            }
        }
        assert block != null;
        assert block.getAddress() == address;
        return block;
    }

    private void parse(long start) {
        CompilerDirectives.transferToInterpreter();
        if (DEBUG) {
            System.out.printf("starting parsing process at 0x%016x\n", start);
        }
        Deque<Long> parseQueue = new LinkedList<>();
        Deque<AMD64BasicBlock> newBlocks = new LinkedList<>();
        parseQueue.addLast(start);
        while (!parseQueue.isEmpty()) {
            long address = parseQueue.removeLast();
            reader.setPC(address);
            Map.Entry<Long, AMD64BasicBlock> entry = blockLookup.floorEntry(address);
            if (entry != null && entry.getValue().contains(address)) {
                AMD64BasicBlock block = entry.getValue();
                if (block.getAddress() != address) {
                    // split
                    if (DEBUG) {
                        System.out.printf("splitting block at 0x%016x\n", address);
                    }
                    AMD64BasicBlock split = block.split(address);
                    addBlock(split);
                    newBlocks.add(split);
                }
                continue;
            }
            if (DEBUG) {
                System.out.printf("parsing block at 0x%016x\n", address);
            }
            AMD64BasicBlock block = AMD64BasicBlockParser.parse(reader);
            addBlock(block);
            newBlocks.add(block);
            long[] btas = block.getBTA();
            if (btas != null) {
                for (long bta : btas) {
                    parseQueue.add(bta);
                }
            }
        }
        while (!newBlocks.isEmpty()) {
            AMD64BasicBlock block = newBlocks.removeLast();
            if (DEBUG) {
                System.out.printf("computing successors of 0x%016x\n", block.getAddress());
            }
            computeSuccessors(block);
        }
    }

    private void computeSuccessors(AMD64BasicBlock block) {
        long[] bta = block.getBTA();
        if (bta != null) {
            AMD64BasicBlock[] next = new AMD64BasicBlock[bta.length];
            for (int i = 0; i < bta.length; i++) {
                if (DEBUG) {
                    System.out.printf("block at 0x%016x: following successor 0x%016x\n", block.getAddress(), bta[i]);
                }
                next[i] = get(bta[i]);
            }
        }
        if (DEBUG) {
            System.out.printf("block at 0x%016x has %d successor(s)\n", block.getAddress(), block.getSuccessors() == null ? 0 : block.getSuccessors().length);
        }
    }

    private void addBlock(AMD64BasicBlock block) {
        CompilerDirectives.transferToInterpreterAndInvalidate();
        if (DEBUG) {
            System.out.printf("registering block at 0x%016x (%d successors)\n", block.getAddress(), block.getSuccessors() == null ? 0 : block.getSuccessors().length);
            System.out.printf("Block content:\n%s\n", block.toString());
        }
        blockLookup.put(block.getAddress(), block);
        if (usedBlocks == blocks.length) {
            // resize
            int newSize = blocks.length + blocks.length / 2;
            assert newSize > 0; // check for integer overflows
            AMD64BasicBlock[] newBlocks = new AMD64BasicBlock[newSize];
            System.arraycopy(blocks, 0, newBlocks, 0, usedBlocks);
            blocks = newBlocks;
        }
        blocks[usedBlocks] = insert(block);
        block.setIndex(usedBlocks);
        usedBlocks++;
    }

    @TruffleBoundary
    public void dump() {
        SymbolResolver resolver = getContextReference().get().getSymbolResolver();
        boolean first = true;
        for (Map.Entry<Long, AMD64BasicBlock> entry : blockLookup.entrySet()) {
            long pc = entry.getKey();
            Symbol sym = resolver.getSymbolExact(pc);
            if (sym != null) {
                if (!first) {
                    System.out.println();
                }
                System.out.printf("%s:\n", sym.getName());
            }
            System.out.print(entry.getValue());
            if (first) {
                first = false;
            }
        }
    }

    @ExplodeLoop
    public long execute(VirtualFrame frame) {
        long pc = readPC.executeI64(frame);
        if (startPC == -1) { // cache entry point
            CompilerDirectives.transferToInterpreterAndInvalidate();
            startPC = pc;
        }
        if (pc != startPC) { // check cached entry point
            CompilerDirectives.transferToInterpreter();
            throw new RuntimeException("non-constant entry point");
        } else {
            pc = startPC;
        }
        CompilerAsserts.partialEvaluationConstant(pc);
        try {
            if (usedBlocks == 0) {
                get(pc);
            }
            AMD64BasicBlock block = blocks[0];
            assert block.getAddress() == pc;
            if (block.getAddress() != pc) {
                block = find(pc);
            }
            while (true) {
                if (DEBUG) {
                    printf("==> EXECUTING pc=0x%016x\n", pc);
                }
                pc = block.execute(frame);
                AMD64BasicBlock successor = block.getSuccessor(pc);
                if (successor == null) {
                    if (NO_INDIRECT) {
                        writePC.executeI64(frame, pc);
                        return pc;
                    }
                    // indirect branch?
                    if (DEBUG) {
                        printf("indirect branch?\n");
                    }
                    block = find(pc);
                    // assert block.getAddress() == pc : String.format("block.address=0x%x,
                    // pc=0x%x", block.getAddress(), pc);
                    if (DEBUG) {
                        printf("resolved successor (pc=0x%016x)\n", block.getAddress());
                    }
                } else {
                    if (DEBUG) {
                        printf("successor: pc=0x%016x\n", successor.getAddress());
                    }
                    block = successor;
                }
            }
        } catch (TraceTooLargeException e) {
            writePC.executeI64(frame, pc);
            return pc;
        } catch (ProcessExitException e) {
            if (DEBUG) {
                printf("Terminating execution at 0x%016x with exit code %d\n", pc, e.getCode());
            }
            writePC.executeI64(frame, pc);
            throw e;
        } catch (CpuRuntimeException e) {
            CompilerDirectives.transferToInterpreter();
            SymbolResolver symbols = getContextReference().get().getSymbolResolver();
            Symbol sym = symbols.getSymbol(e.getPC());
            if (sym == null) {
                Trace.log.printf("Exception at address 0x%016x!\n", e.getPC());
            } else {
                Trace.log.printf("Exception at address 0x%016x <%s>!\n", e.getPC(), sym.getName());
            }
            if (!(e.getCause() instanceof SegmentationViolation)) {
                try {
                    MemoryPage page = memory.get(e.getPC());
                    if (page != null && page.name != null) {
                        Trace.log.printf("Memory region name: '%s', base = 0x%016x (offset = 0x%016x)\n", page.name, page.base, e.getPC() - page.base);
                    }
                } catch (Throwable t) {
                    Trace.log.printf("Error while retrieving memory region metadata of 0x%016x\n", e.getPC());
                }
            }
            try {
                Long blockPC = blockLookup.floorKey(e.getPC());
                if (blockPC != null) {
                    AMD64BasicBlock block = blockLookup.get(blockPC);
                    if (block.contains(e.getPC())) {
                        AMD64Instruction insn = block.getInstruction(e.getPC());
                        Trace.log.printf("Instruction: %s\n", insn.getDisassembly());
                    }
                }
            } catch (Throwable t) {
                Trace.log.printf("Error while retrieving instruction at 0x%016x\n", e.getPC());
            }
            e.getCause().printStackTrace(Trace.log);
            if (e.getCause() instanceof SegmentationViolation) {
                memory.printLayout(Trace.log);
            }
            writePC.executeI64(frame, pc);
            throw e;
            // dump();
        } catch (Throwable t) {
            CompilerDirectives.transferToInterpreter();
            log.log(Levels.ERROR, String.format("Exception at address 0x%016x: %s", pc, t.getMessage()), t);
            try {
                MemoryPage page = memory.get(pc);
                if (page != null && page.name != null) {
                    Trace.log.printf("Memory region name: '%s', base = 0x%016x\n", page.name, page.base);
                }
            } catch (Throwable th) {
                Trace.log.printf("Error while retrieving associated page of 0x%016x\n", pc);
            }
            memory.printLayout(Trace.log);
            // dump();
            throw t;
        }
    }
}
