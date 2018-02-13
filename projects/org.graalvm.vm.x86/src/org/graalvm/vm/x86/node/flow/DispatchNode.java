package org.graalvm.vm.x86.node.flow;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.CodeMemoryReader;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.node.AMD64Node;
import org.graalvm.vm.x86.node.RegisterReadNode;
import org.graalvm.vm.x86.node.RegisterWriteNode;
import org.graalvm.vm.x86.posix.ProcessExitException;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;

public class DispatchNode extends AMD64Node {
    @CompilationFinal private static boolean DEBUG = false;

    @Children private AMD64BasicBlock[] blocks;
    @CompilationFinal private int usedBlocks;

    private final VirtualMemory memory;
    private final NavigableMap<Long, AMD64BasicBlock> blockLookup = new TreeMap<>();
    private final CodeReader reader;

    @Child private RegisterReadNode readPC;
    @Child private RegisterWriteNode writePC;

    public DispatchNode(ArchitecturalState state) {
        memory = state.getMemory();
        reader = new CodeMemoryReader(memory, 0);
        readPC = state.getRegisters().getPC().createRead();
        writePC = state.getRegisters().getPC().createWrite();
        blocks = new AMD64BasicBlock[64];
        usedBlocks = 0;
    }

    public AMD64BasicBlock __get(long address) {
        if (DEBUG) {
            System.out.printf("parsing block at 0x%016x\n", address);
        }
        CompilerDirectives.transferToInterpreter();
        Map.Entry<Long, AMD64BasicBlock> entry = blockLookup.floorEntry(address);
        if (entry != null && entry.getValue().contains(address)) {
            AMD64BasicBlock block = entry.getValue();
            if (block.getAddress() == address) {
                if (DEBUG) {
                    System.out.printf("block at 0x%016x: already parsed\n", entry.getKey());
                }
                return block;
            }
            if (DEBUG) {
                System.out.printf("block at 0x%016x: splitting at 0x%016x\n", entry.getKey(), address);
            }
            AMD64BasicBlock split = block.split(address);
            addBlock(split);
            computeSuccessors(split);
            return split;
        }
        reader.setPC(address);
        AMD64BasicBlock block = AMD64BasicBlockParser.parse(reader);
        addBlock(block);
        computeSuccessors(block);
        return block;
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
            AMD64BasicBlock[] next = new AMD64BasicBlock[bta.length + 1];
            for (int i = 0; i < bta.length; i++) {
                if (DEBUG) {
                    System.out.printf("block at 0x%016x: following successor 0x%016x\n", block.getAddress(), bta[i]);
                }
                next[i] = get(bta[i]);
            }
            if (DEBUG) {
                System.out.printf("block at 0x%016x: following successor 0x%016x\n", block.getAddress(), block.getLastInstruction().next());
            }
            next[next.length - 1] = get(block.getLastInstruction().next());
            block.setSuccessors(next);
        }
        if (DEBUG) {
            System.out.printf("block at 0x%016x has %d successor(s)\n", block.getAddress(), block.getSuccessors() == null ? 0 : block.getSuccessors().length);
        }
    }

    private void addBlock(AMD64BasicBlock block) {
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

    public long execute(VirtualFrame frame) {
        long cnt = 20; // max execution steps (help debug infinite loops)
        long pc = readPC.executeI64(frame);
        try {
            if (usedBlocks == 0) {
                get(pc);
            }
            AMD64BasicBlock block = blocks[0];
            if (block.getAddress() != pc) {
                block = get(pc);
            }
            while (true) {
                if (DEBUG && cnt-- <= 0) {
                    break;
                }
                if (DEBUG) {
                    System.out.printf("==> EXECUTING pc=0x%016x\n", pc);
                }
                pc = block.execute(frame);
                AMD64BasicBlock successor = block.getSuccessor(pc);
                if (successor == null) {
                    // indirect branch?
                    if (DEBUG) {
                        System.out.printf("indirect branch?\n");
                    }
                    block = get(pc);
                    assert block.getAddress() == pc : String.format("block.address=0x%x, pc=0x%x", block.getAddress(), pc);
                    if (DEBUG) {
                        System.out.printf("resolved successor (pc=0x%016x)\n", block.getAddress());
                    }
                } else {
                    if (DEBUG) {
                        System.out.printf("successor: pc=0x%016x\n", successor.getAddress());
                    }
                    block = successor;
                }
            }
        } catch (ProcessExitException e) {
            if (DEBUG) {
                System.out.printf("Terminating execution at 0x%016x\n", pc);
            }
            writePC.executeI64(frame, pc);
            return e.getCode();
        } catch (Throwable t) {
            CompilerDirectives.transferToInterpreter();
            System.err.printf("Exception at address 0x%016x!\n", pc);
            t.printStackTrace();
        }
        writePC.executeI64(frame, pc);
        return pc;
    }
}
