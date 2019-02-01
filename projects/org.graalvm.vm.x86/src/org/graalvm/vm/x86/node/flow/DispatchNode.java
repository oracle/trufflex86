/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.graalvm.vm.x86.node.flow;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.graalvm.vm.memory.JavaVirtualMemory;
import org.graalvm.vm.memory.MemoryPage;
import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.memory.exception.SegmentationViolation;
import org.graalvm.vm.posix.elf.Symbol;
import org.graalvm.vm.util.log.Levels;
import org.graalvm.vm.util.log.Trace;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.CpuRuntimeException;
import org.graalvm.vm.x86.SymbolResolver;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.CodeMemoryReader;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.node.RegisterReadNode;
import org.graalvm.vm.x86.node.RegisterWriteNode;
import org.graalvm.vm.x86.posix.ProcessExitException;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;

public class DispatchNode extends AbstractDispatchNode {
    private static final Logger log = Trace.create(DispatchNode.class);

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
            AMD64BasicBlock[] next = new AMD64BasicBlock[bta.length];
            for (int i = 0; i < bta.length; i++) {
                if (DEBUG) {
                    System.out.printf("block at 0x%016x: following successor 0x%016x\n", block.getAddress(), bta[i]);
                }
                next[i] = get(bta[i]);
            }
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

    @Override
    public long execute(VirtualFrame frame) {
        long cnt = -1; // max execution steps (help debug infinite loops)
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
                if (DEBUG && (cnt != -1) && (cnt-- == 0)) {
                    System.out.printf("Terminating interpreter loop at pc=0x%016x\n", pc);
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
            CompilerDirectives.transferToInterpreter();
            System.err.printf("Execution aborted at 0x%016x: limit reached\n", pc);
        } catch (ProcessExitException e) {
            if (DEBUG) {
                System.out.printf("Terminating execution at 0x%016x with exit code %d\n", pc, e.getCode());
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
                    if (memory instanceof JavaVirtualMemory) {
                        JavaVirtualMemory jmem = (JavaVirtualMemory) memory;
                        MemoryPage page = jmem.get(e.getPC());
                        if (page != null && page.name != null) {
                            Trace.log.printf("Memory region name: '%s', base = 0x%016x (offset = 0x%016x)\n", page.name, page.base, e.getPC() - page.base);
                        }
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
                if (memory instanceof JavaVirtualMemory) {
                    JavaVirtualMemory jmem = (JavaVirtualMemory) memory;
                    MemoryPage page = jmem.get(pc);
                    if (page != null && page.name != null) {
                        Trace.log.printf("Memory region name: '%s', base = 0x%016x\n", page.name, page.base);
                    }
                }
            } catch (Throwable th) {
                Trace.log.printf("Error while retrieving associated page of 0x%016x\n", pc);
            }
            memory.printLayout(Trace.log);
            // dump();
            throw t;
        }
        writePC.executeI64(frame, pc);
        return pc;
    }
}
