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

import static org.graalvm.vm.x86.util.Debug.printf;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.graalvm.vm.memory.JavaVirtualMemory;
import org.graalvm.vm.memory.MemoryPage;
import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.memory.exception.SegmentationViolation;
import org.graalvm.vm.posix.api.MemoryFaultException;
import org.graalvm.vm.posix.elf.Symbol;
import org.graalvm.vm.util.HexFormatter;
import org.graalvm.vm.util.log.Levels;
import org.graalvm.vm.util.log.Trace;
import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.CpuRuntimeException;
import org.graalvm.vm.x86.SymbolResolver;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.CodeMemoryReader;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.isa.IndirectException;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.isa.ReturnException;
import org.graalvm.vm.x86.node.AMD64Node;
import org.graalvm.vm.x86.node.RegisterReadNode;
import org.graalvm.vm.x86.node.RegisterWriteNode;
import org.graalvm.vm.x86.posix.InteropException;
import org.graalvm.vm.x86.posix.PosixEnvironment;
import org.graalvm.vm.x86.posix.ProcessExitException;
import org.graalvm.vm.x86.substitution.SubstitutionRegistry;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.ExplodeLoop.LoopExplosionKind;
import com.oracle.truffle.api.profiles.BranchProfile;

public class TraceDispatchNode extends AMD64Node {
    private static final Logger log = Trace.create(TraceDispatchNode.class);

    @CompilationFinal private boolean DEBUG = false;
    @CompilationFinal private boolean DEBUG_REGS = false;
    @CompilationFinal private static boolean NO_INDIRECT = true;

    @CompilationFinal private int maxBlockCount = 1;

    @Children private AMD64BasicBlock[] blocks;
    @CompilationFinal private int usedBlocks;

    @CompilationFinal private long startPC = -1;

    private final VirtualMemory memory;
    private final NavigableMap<Long, AMD64BasicBlock> blockLookup = new TreeMap<>();
    private final CodeReader reader;
    private final SubstitutionRegistry substitutions;

    @Child private RegisterReadNode readPC;
    @Child private RegisterReadNode readRSP;
    @Child private RegisterWriteNode writePC;

    private final BranchProfile exceptionProfile = BranchProfile.create();
    private final BranchProfile exitProfile = BranchProfile.create();
    private final BranchProfile returnProfile = BranchProfile.create();

    private final Object lock = new Object();

    public TraceDispatchNode(ArchitecturalState state) {
        memory = state.getMemory();
        reader = new CodeMemoryReader(memory, 0);
        substitutions = state.getSubstitutions();
        readPC = state.getRegisters().getPC().createRead();
        readRSP = state.getRegisters().getRegister(Register.RSP).createRead();
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
            printf("resolving block at 0x%016x\n", address);
        }
        synchronized (lock) {
            AMD64BasicBlock block = blockLookup.get(address);
            if (block == null) {
                parse(address);
                block = blockLookup.get(address);
            }
            assert block != null;
            assert block.getAddress() == address;
            return block;
        }
    }

    protected AMD64BasicBlock find(long address) {
        CompilerDirectives.transferToInterpreter();
        AMD64BasicBlock block = blockLookup.get(address);
        if (block == null) {
            if (usedBlocks >= maxBlockCount) {
                if (DEBUG) {
                    printf("cannot parse block at 0x%016x: size limit reached\n", address);
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
        CompilerDirectives.transferToInterpreterAndInvalidate();
        if (DEBUG) {
            printf("starting parsing process at 0x%016x\n", start);
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
                        printf("splitting block at 0x%016x\n", address);
                    }
                    AMD64BasicBlock split = block.split(address);
                    addBlock(split);
                    newBlocks.add(split);
                }
                continue;
            }
            if (DEBUG) {
                printf("parsing block at 0x%016x\n", address);
            }
            AMD64BasicBlock block = AMD64BasicBlockParser.parse(reader, substitutions);
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
                printf("computing successors of 0x%016x\n", block.getAddress());
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
                    printf("block at 0x%016x: following successor 0x%016x\n", block.getAddress(), bta[i]);
                }
                next[i] = get(bta[i]);
            }
            block.setSuccessors(next);
        }
        if (!block.isIndirect()) {
            AMD64BasicBlock successor1 = get(block.pc1);
            AMD64BasicBlock successor2 = get(block.pc2);
            block.successor1 = (int) successor1.getIndex();
            block.successor2 = (int) successor2.getIndex();
        }
        if (DEBUG) {
            printf("block at 0x%016x has %d successor(s)\n", block.getAddress(), block.getSuccessors() == null ? 0 : block.getSuccessors().length);
        }
    }

    private void addBlock(AMD64BasicBlock block) {
        CompilerDirectives.transferToInterpreterAndInvalidate();
        if (DEBUG) {
            printf("registering block at 0x%016x (%d successors)\n", block.getAddress(), block.getSuccessors() == null ? 0 : block.getSuccessors().length);
            printf("Block content:\n%s\n", block.toString());
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
                printf("%s:\n", sym.getName());
            }
            System.out.print(entry.getValue());
            if (first) {
                first = false;
            }
        }
    }

    @ExplodeLoop(kind = LoopExplosionKind.MERGE_EXPLODE)
    public long execute(VirtualFrame frame) {
        long pc = readPC.executeI64(frame);
        if (startPC == -1) { // cache entry point
            CompilerDirectives.transferToInterpreterAndInvalidate();
            startPC = pc;
        }
        if (pc != startPC) { // check cached entry point
            CompilerDirectives.transferToInterpreter();
            throw new RuntimeException("non-constant entry point: " + HexFormatter.tohex(pc, 16) + " vs " + HexFormatter.tohex(startPC, 16));
        } else {
            pc = startPC;
        }
        CompilerAsserts.partialEvaluationConstant(startPC);
        CompilerAsserts.partialEvaluationConstant(blocks);
        CompilerAsserts.partialEvaluationConstant(pc);
        try {
            if (usedBlocks == 0) {
                get(pc);
            }
            assert blocks[0].getAddress() == pc : "trace execution must start with first instruction";
            CompilerAsserts.partialEvaluationConstant(blocks[0]);

            int index = 0;
            while (true) {
                if (DEBUG) {
                    printf("==> EXECUTING pc=0x%016x\n", pc);
                }
                try {
                    CompilerAsserts.partialEvaluationConstant(index);
                    boolean result = blocks[index].executeBlock(frame);
                    if (result) {
                        index = blocks[index].successor1;
                    } else {
                        index = blocks[index].successor2;
                    }
                } catch (IndirectException e) {
                    return e.getBTA();
                }
            }
        } catch (TraceTooLargeException e) {
            exceptionProfile.enter();
            writePC.executeI64(frame, pc);
            return pc;
        } catch (ProcessExitException e) {
            exitProfile.enter();
            if (DEBUG) {
                printf("Terminating execution at 0x%016x with exit code %d\n", pc, e.getCode());
            }
            writePC.executeI64(frame, pc);
            throw e;
        } catch (ReturnException | InteropException e) {
            returnProfile.enter();
            writePC.executeI64(frame, pc);
            throw e;
        } catch (CpuRuntimeException e) {
            CompilerDirectives.transferToInterpreter();
            AMD64Context ctx = getContextReference().get();
            SymbolResolver symbols = ctx.getSymbolResolver();
            Symbol sym = symbols.getSymbol(e.getPC());
            long offset = 0;

            if (sym == null) {
                PosixEnvironment posix = ctx.getPosixEnvironment();
                sym = posix.getSymbol(e.getPC());
                long b = posix.getBase(e.getPC());
                if (b != -1) {
                    offset = e.getPC() - b;
                }
            }

            String filename = null;
            VirtualMemory mem = ctx.getMemory();
            MemoryPage page = null;
            try {
                page = mem.get(e.getPC());
            } catch (SegmentationViolation ex) {
                // ignore
            }
            if (page != null && page.name != null) {
                filename = page.name;
            }
            if (filename == null) {
                PosixEnvironment posix = ctx.getPosixEnvironment();
                filename = posix.getFilename(e.getPC());
            }

            if (sym == null) {
                Trace.log.printf("Exception at address 0x%016x!\n", e.getPC());
            } else {
                Trace.log.printf("Exception at address 0x%016x <%s>!\n", e.getPC(), sym.getName());
            }
            if (filename != null) {
                if (offset != 0) {
                    Trace.log.printf("File: %s [offset=0x%x]\n", filename, offset);
                } else {
                    Trace.log.printf("File: %s\n", filename);
                }
            }

            if (!(e.getCause() instanceof SegmentationViolation)) {
                try {
                    memory.printAddressInfo(e.getPC(), Trace.log);
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
            writePC.executeI64(frame, pc);
            if (e.getCause() instanceof SegmentationViolation) {
                e.getCause().printStackTrace(Trace.log);
                memory.printLayout(Trace.log);
            } else if (e.getCause() instanceof MemoryFaultException && e.getCause().getCause() instanceof SegmentationViolation) {
                Throwable cause = e.getCause().getCause();
                // unwrap SegmentationViolation if it is nested in a MemoryFaultException
                cause.printStackTrace(Trace.log);
                memory.printLayout(Trace.log);
                throw new CpuRuntimeException(e.getPC(), cause);
            } else {
                e.getCause().printStackTrace(Trace.log);
            }
            try {
                long rsp = readRSP.executeI64(frame);
                Trace.log.printf("\nRSP=0x%016x\n", rsp);
                rsp &= ~0xF;
                memory.dump(rsp, 512);
            } catch (SegmentationViolation t) {
                Trace.log.println("Error while retrieving stack memory content.");
            }
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
    }

    public Register[] getGPRReads() {
        CompilerAsserts.neverPartOfCompilation();
        Set<Register> written = new HashSet<>();
        Set<Register> read = new HashSet<>(blocks[0].getGPRReads(written));
        Set<Register> allReads = new HashSet<>(read);
        if (DEBUG_REGS) {
            System.out.println("getGPRReads()");
            System.out.printf("block @ 0x%016x:\n", blocks[0].getAddress());
            System.out.print(blocks[0].toString());
            System.out.printf("written=%s\n", written.stream().map(Register::toString).collect(Collectors.joining(",")));
            System.out.printf("read=%s\n", read.stream().map(Register::toString).collect(Collectors.joining(",")));
        }
        for (int i = 1; i < usedBlocks; i++) {
            AMD64BasicBlock block = blocks[i];
            Set<Register> wr = new HashSet<>(written);
            allReads.addAll(block.getGPRReads(wr));
            if (DEBUG_REGS) {
                Set<Register> regs = block.getGPRReads(wr);
                System.out.printf("block @ 0x%016x:\n", block.getAddress());
                System.out.print(block.toString());
                System.out.printf("written[%s,0x%016x]=%s\n", i, block.getAddress(), wr.stream().map(Register::toString).collect(Collectors.joining(",")));
                System.out.printf("read[%s,0x%016x]=%s\n", i, block.getAddress(), regs.stream().map(Register::toString).collect(Collectors.joining(",")));
            }
        }
        Set<Register> result = new HashSet<>();
        for (Register r : allReads) {
            if (written.contains(r) && !read.contains(r)) {
                // overwritten in first block
                if (DEBUG_REGS) {
                    System.out.printf("register %s was overwritten in first block\n", r);
                }
            } else {
                result.add(r);
            }
        }
        if (DEBUG_REGS) {
            System.out.printf("result=%s\n", result.stream().map(Register::toString).collect(Collectors.joining(",")));
        }
        return result.toArray(new Register[result.size()]);
    }

    public Register[] getGPRWrites() {
        Set<Register> writes = new HashSet<>();
        for (int i = 0; i < usedBlocks; i++) {
            AMD64BasicBlock block = blocks[i];
            writes.addAll(block.getGPRWrites());
        }
        return writes.toArray(new Register[writes.size()]);
    }

    public int[] getAVXReads() {
        CompilerAsserts.neverPartOfCompilation();
        Set<Integer> written = new HashSet<>();
        Set<Integer> read = new HashSet<>(blocks[0].getAVXReads(written));
        Set<Integer> allReads = new HashSet<>(read);
        for (int i = 1; i < usedBlocks; i++) {
            AMD64BasicBlock block = blocks[i];
            Set<Integer> wr = new HashSet<>(written);
            allReads.addAll(block.getAVXReads(wr));
        }
        Set<Integer> result = new HashSet<>();
        for (int r : allReads) {
            if (written.contains(r) && !read.contains(r)) {
                // overwritten in first block
            } else {
                result.add(r);
            }
        }
        int[] regs = new int[result.size()];
        int i = 0;
        for (int reg : result) {
            regs[i++] = reg;
        }
        return regs;
    }

    public int[] getAVXWrites() {
        Set<Integer> writes = new HashSet<>();
        for (int i = 0; i < usedBlocks; i++) {
            AMD64BasicBlock block = blocks[i];
            writes.addAll(block.getAVXWrites());
        }
        int[] regs = new int[writes.size()];
        int i = 0;
        for (int reg : writes) {
            regs[i++] = reg;
        }
        return regs;
    }
}
