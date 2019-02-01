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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.vfs.FileSystem;
import org.graalvm.vm.posix.vfs.NativeFileSystem;
import org.graalvm.vm.posix.vfs.VFS;
import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.ElfLoader;
import org.graalvm.vm.x86.Options;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.node.AMD64Node;
import org.graalvm.vm.x86.node.RegisterReadNode;
import org.graalvm.vm.x86.node.RegisterWriteNode;
import org.graalvm.vm.x86.posix.PosixEnvironment;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;

public class LoaderNode extends AMD64Node {
    @Child private RegisterReadNode readSP;
    @Child private RegisterWriteNode writeSP;
    @Child private RegisterWriteNode writePC;

    public LoaderNode(ArchitecturalState state) {
        readSP = state.getRegisters().getRegister(Register.RSP).createRead();
        writeSP = state.getRegisters().getRegister(Register.RSP).createWrite();
        writePC = state.getRegisters().getPC().createWrite();
    }

    @TruffleBoundary
    private static Map<String, String> getenv() {
        if (Options.getBoolean(Options.DEBUG_STATIC_ENV)) {
            Map<String, String> env = new HashMap<>();
            env.put("PATH", System.getenv("PATH"));
            env.put("LANG", System.getenv("LANG"));
            env.put("HOME", System.getenv("HOME"));
            if (System.getenv("DISPLAY") != null) {
                env.put("DISPLAY", System.getenv("DISPLAY"));
            }
            return env;
        } else {
            return System.getenv();
        }
    }

    @TruffleBoundary
    private static void setup(String execfn, PosixEnvironment posix) throws PosixException {
        posix.setExecfn(execfn);
        VFS vfs = posix.getVFS();
        Path cwd = Paths.get(".").toAbsolutePath().normalize();
        FileSystem fs;

        String fsroot = Options.getString(Options.FSROOT);
        if (fsroot != null) {
            fs = new NativeFileSystem(vfs, fsroot);
            String cwdprop = Options.getString(Options.CWD);
            if (cwdprop != null) {
                cwd = Paths.get(cwdprop);
            } else {
                cwd = Paths.get("/");
            }
        } else {
            fs = new NativeFileSystem(vfs, cwd.getRoot().toString());
        }

        posix.mount("/", fs);
        StringBuilder posixPath = new StringBuilder();
        if (cwd.getNameCount() == 0) {
            posixPath.append('/');
        }
        for (int i = 0; i < cwd.getNameCount(); i++) {
            posixPath.append('/').append(cwd.getName(i));
        }
        posix.getPosix().chdir(posixPath.toString());
    }

    public Object execute(VirtualFrame frame, String execfn, String[] args) {
        AMD64Context ctx = getContextReference().get();
        ElfLoader loader = new ElfLoader(ctx.getTraceWriter());
        loader.setPosixEnvironment(ctx.getPosixEnvironment());
        loader.setVirtualMemory(ctx.getMemory());
        loader.setSP(readSP.executeI64(frame));
        loader.setProgramName(execfn);
        loader.setArguments(args);
        loader.setEnvironment(getenv());

        PosixEnvironment posix = getContextReference().get().getPosixEnvironment();

        try {
            setup(execfn, posix);
            VFS vfs = posix.getVFS();
            String path = vfs.resolve(execfn);
            loader.load(path);
        } catch (Throwable t) {
            CompilerDirectives.transferToInterpreter();
            throw new RuntimeException(t);
        }

        if (!loader.isAMD64()) {
            CompilerDirectives.transferToInterpreter();
            throw new RuntimeException("Not an x86_64 executable!");
        }

        writePC.executeI64(frame, loader.getPC());
        writeSP.executeI64(frame, loader.getSP());
        ctx.setSymbols(loader.getSymbols());

        return null;
    }

    public Object executeELF(VirtualFrame frame, String execfn, String[] args, byte[] elf) {
        AMD64Context ctx = getContextReference().get();
        ElfLoader loader = new ElfLoader(ctx.getTraceWriter());
        loader.setPosixEnvironment(ctx.getPosixEnvironment());
        loader.setVirtualMemory(ctx.getMemory());
        loader.setSP(readSP.executeI64(frame));
        loader.setProgramName(execfn);
        loader.setArguments(args);
        loader.setEnvironment(getenv());

        PosixEnvironment posix = getContextReference().get().getPosixEnvironment();

        try {
            setup(execfn, posix);
            loader.load(elf, execfn);
        } catch (Throwable t) {
            CompilerDirectives.transferToInterpreter();
            throw new RuntimeException(t);
        }

        if (!loader.isAMD64()) {
            CompilerDirectives.transferToInterpreter();
            throw new RuntimeException("Not an x86_64 executable!");
        }

        writePC.executeI64(frame, loader.getPC());
        writeSP.executeI64(frame, loader.getSP());
        ctx.setSymbols(loader.getSymbols());

        return null;
    }
}
