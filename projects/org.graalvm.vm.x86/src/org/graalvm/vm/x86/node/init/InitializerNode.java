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
package org.graalvm.vm.x86.node.init;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graalvm.vm.memory.ByteMemory;
import org.graalvm.vm.memory.Memory;
import org.graalvm.vm.memory.MemoryPage;
import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.posix.api.mem.Mman;
import org.graalvm.vm.util.log.Trace;
import org.graalvm.vm.x86.AMD64;
import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.AMD64Register;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.Options;
import org.graalvm.vm.x86.isa.AVXRegister;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.node.AMD64Node;
import org.graalvm.vm.x86.node.AVXRegisterWriteNode;
import org.graalvm.vm.x86.node.RegisterWriteNode;
import org.graalvm.vm.x86.node.WriteFlagNode;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;

public class InitializerNode extends AMD64Node {
    private static final Logger log = Trace.create(LoaderNode.class);
    @CompilationFinal(dimensions = 1) public static final byte[] BINARY = loadBinary();

    private final String programName;
    private final String[] arguments;

    @Child private LoaderNode setup;
    @Children private RegisterWriteNode[] gpr;
    @Children private AVXRegisterWriteNode[] zmm;
    @Child private RegisterWriteNode fs;
    @Child private RegisterWriteNode gs;

    @Child private WriteFlagNode writeCF;
    @Child private WriteFlagNode writePF;
    @Child private WriteFlagNode writeAF;
    @Child private WriteFlagNode writeZF;
    @Child private WriteFlagNode writeSF;
    @Child private WriteFlagNode writeDF;
    @Child private WriteFlagNode writeOF;
    @Child private WriteFlagNode writeAC;
    @Child private WriteFlagNode writeID;

    @CompilationFinal private FrameSlot instructionCount;

    public InitializerNode(ArchitecturalState state, String programName) {
        this(state, programName, null);
    }

    public InitializerNode(ArchitecturalState state, String programName, String[] arguments) {
        this.arguments = arguments;
        this.setup = new LoaderNode(state);
        this.programName = programName;
        gpr = new RegisterWriteNode[16];
        for (int i = 0; i < gpr.length; i++) {
            AMD64Register reg = state.getRegisters().getRegister(Register.get(i));
            gpr[i] = reg.createWrite();
        }
        zmm = new AVXRegisterWriteNode[32];
        for (int i = 0; i < zmm.length; i++) {
            AVXRegister reg = state.getRegisters().getAVXRegister(i);
            zmm[i] = reg.createWrite();
        }
        fs = state.getRegisters().getFS().createWrite();
        gs = state.getRegisters().getGS().createWrite();
        writeCF = state.getRegisters().getCF().createWrite();
        writePF = state.getRegisters().getPF().createWrite();
        writeAF = state.getRegisters().getAF().createWrite();
        writeZF = state.getRegisters().getZF().createWrite();
        writeSF = state.getRegisters().getSF().createWrite();
        writeDF = state.getRegisters().getDF().createWrite();
        writeOF = state.getRegisters().getOF().createWrite();
        writeAC = state.getRegisters().getAC().createWrite();
        writeID = state.getRegisters().getID().createWrite();

        instructionCount = state.getInstructionCount();
    }

    private static byte[] loadBinary() {
        String filename = Options.getString(Options.STATIC_BINARY);
        if (filename == null) {
            return null;
        }
        try {
            return Files.readAllBytes(Paths.get(filename));
        } catch (IOException e) {
            log.log(Level.WARNING, "Cannot load binary \"" + filename + "\"");
            return null;
        }
    }

    @ExplodeLoop
    public void execute(VirtualFrame frame) {
        for (RegisterWriteNode register : gpr) {
            register.executeI64(frame, 0);
        }

        for (AVXRegisterWriteNode register : zmm) {
            register.executeClear(frame);
        }

        AMD64Context ctx = getContextReference().get();
        VirtualMemory memory = ctx.getMemory();
        long stackbase = memory.pageStart(AMD64.STACK_BASE);
        long stacksize = memory.roundToPageSize(AMD64.STACK_SIZE);
        Memory stackMemory = new ByteMemory(stacksize, false);
        MemoryPage stack = new MemoryPage(stackMemory, stackbase, stacksize, "[stack]");
        memory.add(stack);
        if (ctx.getTraceWriter() != null) {
            ctx.getTraceWriter().mmap(stack.base, stack.size, Mman.PROT_READ | Mman.PROT_WRITE, Mman.MAP_FIXED | Mman.MAP_ANONYMOUS, -1, 0, stack.base, null);
        }
        long sp = AMD64.STACK_ADDRESS - 16;
        assert (sp & 0xf) == 0;
        gpr[Register.RSP.getID()].executeI64(frame, sp);

        // initialize return code page
        long retbase = memory.pageStart(AMD64.RETURN_BASE);
        long retsize = memory.roundToPageSize(AMD64.RETURN_CODE.length);
        Memory retMemory = new ByteMemory(retsize, false);
        for (int i = 0; i < AMD64.RETURN_CODE.length; i++) {
            retMemory.setI8(i, AMD64.RETURN_CODE[i]);
        }
        MemoryPage ret = new MemoryPage(retMemory, retbase, retsize, "[return code]");
        memory.add(ret);
        ctx.setReturnAddress(retbase);

        fs.executeI64(frame, 0);
        gs.executeI64(frame, 0);

        writeCF.execute(frame, false);
        writePF.execute(frame, false);
        writeAF.execute(frame, false);
        writeZF.execute(frame, false);
        writeSF.execute(frame, false);
        writeDF.execute(frame, false);
        writeOF.execute(frame, false);
        writeAC.execute(frame, false);
        writeID.execute(frame, false);

        frame.setLong(instructionCount, 0);

        String[] args = ctx.getArguments();
        if (arguments != null) {
            args = arguments;
        }

        if (BINARY != null) {
            setup.executeELF(frame, programName, args, BINARY);
        } else {
            setup.execute(frame, programName, args);
        }
    }
}
