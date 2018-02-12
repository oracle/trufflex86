package org.graalvm.vm.x86.node.flow;

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

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;

public class DispatchNode extends AMD64Node {
    private static final boolean DEBUG = false;

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

    public AMD64BasicBlock get(long address) {
        if (DEBUG) {
            System.out.printf("parsing block at 0x%016x\n", address);
        }
        CompilerDirectives.transferToInterpreter();
        Map.Entry<Long, AMD64BasicBlock> entry = blockLookup.floorEntry(address);
        if (entry != null && entry.getValue().contains(address)) {
            // TODO: split!
            return entry.getValue();
        }
        reader.setPC(address);
        AMD64BasicBlock block = AMD64BasicBlockParser.parse(reader);
        addBlock(block);
        long[] bta = block.getBTA();
        if (DEBUG) {
            System.out.printf("block at 0x%016x has %d successor(s)\n", address, bta == null ? 0 : bta.length);
        }
        if (bta != null) {
            AMD64BasicBlock[] next = new AMD64BasicBlock[bta.length];
            for (int i = 0; i < bta.length; i++) {
                if (DEBUG) {
                    System.out.printf("block at 0x%016x: following successor 0x%016x\n", address, bta[i]);
                }
                next[i] = get(bta[i]);
            }
            block.setSuccessors(next);
        }
        return block;
    }

    private void addBlock(AMD64BasicBlock block) {
        if (DEBUG) {
            System.out.printf("registering block at 0x%016x\n", block.getAddress());
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
        long cnt = 10;
        long pc = readPC.executeI64(frame);
        if (usedBlocks == 0) {
            get(pc);
        }
        AMD64BasicBlock block = blocks[0];
        if (block.getAddress() != pc) {
            block = get(pc);
        }
        try {
            while (true) {
                if (cnt-- <= 0) {
                    break;
                }
                if (DEBUG) {
                    System.out.printf("pc=0x%016x\n", pc);
                }
                pc = block.execute(frame);
                AMD64BasicBlock successor = block.getSuccessor(pc);
                if (successor == null) {
                    // indirect branch?
                    block = get(pc);
                    assert block.getAddress() == pc;
                    if (DEBUG) {
                        System.out.printf("resolved successor (pc=0x%016x)\n", block.getAddress());
                    }
                } else {
                    block = successor;
                }
            }
        } catch (Throwable t) {
            CompilerDirectives.transferToInterpreter();
            t.printStackTrace();
        }
        writePC.executeI64(frame, pc);
        return pc;
    }
}
