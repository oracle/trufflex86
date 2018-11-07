package org.graalvm.vm.x86.node.init;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.ElfLoader;
import org.graalvm.vm.x86.Options;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.node.AMD64Node;
import org.graalvm.vm.x86.node.RegisterReadNode;
import org.graalvm.vm.x86.node.RegisterWriteNode;
import org.graalvm.vm.x86.posix.PosixEnvironment;

import com.everyware.posix.api.PosixException;
import com.everyware.posix.vfs.FileSystem;
import com.everyware.posix.vfs.NativeFileSystem;
import com.everyware.posix.vfs.VFS;
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
        ElfLoader loader = new ElfLoader();
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
        ElfLoader loader = new ElfLoader();
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
