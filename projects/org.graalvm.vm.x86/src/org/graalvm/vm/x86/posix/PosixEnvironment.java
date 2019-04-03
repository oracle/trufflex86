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
package org.graalvm.vm.x86.posix;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.graalvm.vm.memory.ByteMemory;
import org.graalvm.vm.memory.Memory;
import org.graalvm.vm.memory.MemoryPage;
import org.graalvm.vm.memory.PosixMemory;
import org.graalvm.vm.memory.PosixVirtualMemoryPointer;
import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.memory.exception.SegmentationViolation;
import org.graalvm.vm.posix.api.BytePosixPointer;
import org.graalvm.vm.posix.api.CString;
import org.graalvm.vm.posix.api.Dirent;
import org.graalvm.vm.posix.api.Errno;
import org.graalvm.vm.posix.api.Posix;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.PosixPointer;
import org.graalvm.vm.posix.api.Rlimit;
import org.graalvm.vm.posix.api.Sigaction;
import org.graalvm.vm.posix.api.Sigset;
import org.graalvm.vm.posix.api.Stack;
import org.graalvm.vm.posix.api.Timespec;
import org.graalvm.vm.posix.api.Timeval;
import org.graalvm.vm.posix.api.Tms;
import org.graalvm.vm.posix.api.Utsname;
import org.graalvm.vm.posix.api.io.Fcntl;
import org.graalvm.vm.posix.api.io.FileDescriptorManager;
import org.graalvm.vm.posix.api.io.Iovec;
import org.graalvm.vm.posix.api.io.Pollfd;
import org.graalvm.vm.posix.api.io.Stat;
import org.graalvm.vm.posix.api.io.Stream;
import org.graalvm.vm.posix.api.linux.Sysinfo;
import org.graalvm.vm.posix.api.mem.Mman;
import org.graalvm.vm.posix.api.net.Msghdr;
import org.graalvm.vm.posix.api.net.RecvResult;
import org.graalvm.vm.posix.api.net.Sockaddr;
import org.graalvm.vm.posix.elf.Elf;
import org.graalvm.vm.posix.elf.ProgramHeader;
import org.graalvm.vm.posix.elf.Section;
import org.graalvm.vm.posix.elf.Symbol;
import org.graalvm.vm.posix.elf.SymbolTable;
import org.graalvm.vm.posix.vfs.FileSystem;
import org.graalvm.vm.posix.vfs.VFS;
import org.graalvm.vm.util.BitTest;
import org.graalvm.vm.util.HexFormatter;
import org.graalvm.vm.util.io.Endianess;
import org.graalvm.vm.util.log.Levels;
import org.graalvm.vm.util.log.Trace;
import org.graalvm.vm.x86.Options;
import org.graalvm.vm.x86.SymbolResolver;
import org.graalvm.vm.x86.node.debug.trace.ExecutionTraceWriter;

public class PosixEnvironment {
    private static final Logger log = Trace.create(PosixEnvironment.class);

    private static final boolean DEBUG = Options.getBoolean(Options.DEBUG_EXEC) || Options.getBoolean(Options.DEBUG_SYMBOLS);
    private static final boolean STATIC_TIME = Options.getBoolean(Options.USE_STATIC_TIME);

    private final VirtualMemory mem;
    private final Posix posix;
    private boolean strace;
    private final String arch;

    private String execfn;

    private NavigableMap<Long, Symbol> symbols;
    private NavigableMap<Long, String> libraries;
    private SymbolResolver symbolResolver;

    private final ExecutionTraceWriter traceWriter;

    public PosixEnvironment(VirtualMemory mem, String arch, ExecutionTraceWriter traceWriter) {
        this.mem = mem;
        this.arch = arch;
        this.traceWriter = traceWriter;
        posix = new Posix();
        strace = System.getProperty("posix.strace") != null;
        if (DEBUG) {
            symbols = new TreeMap<>();
            symbolResolver = new SymbolResolver(symbols);
            libraries = new TreeMap<>();
        }
    }

    public void setStrace(boolean value) {
        strace = value;
        posix.setStrace(value);
    }

    public boolean isStrace() {
        return strace;
    }

    public void setExecfn(String execfn) {
        this.execfn = execfn;
    }

    public ExecutionTraceWriter getTraceWriter() {
        return traceWriter;
    }

    public Symbol getSymbol(long pc) {
        if (symbolResolver != null) {
            return symbolResolver.getSymbol(pc);
        } else {
            return null;
        }
    }

    public long getBase(long pc) {
        if (libraries == null) {
            return 0;
        }
        Long result = libraries.floorKey(pc);
        if (result == null) {
            return -1;
        } else {
            return result;
        }
    }

    public String getFilename(long pc) {
        if (libraries != null) {
            Entry<Long, String> entry = libraries.floorEntry(pc);
            if (entry != null) {
                return entry.getValue();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private void loadSymbols(int fildes, long offset, long ptr, long length) {
        log.log(Levels.INFO, () -> {
            String filename = null;
            try {
                filename = posix.getFileDescriptor(fildes).name;
            } catch (Throwable t) {
                // ignore
            }

            if (filename == null) {
                filename = "/proc/self/fd/" + fildes;
            }

            return "Loading symbols for file " + filename + " (fd: " + fildes + ", pointer: " + HexFormatter.tohex(ptr, 16) + ", file offset: " + HexFormatter.tohex(offset, 16) + "-" +
                            HexFormatter.tohex(offset + length, 16) + ")";
        });
        try {
            // check if this is an ELF file
            byte[] magic = new byte[4];
            Stat stat = new Stat();
            Stream stream = posix.getStream(fildes);
            stream.stat(stat);
            if ((stat.st_mode & Stat.S_IFMT) == Stat.S_IFREG && stat.st_size > 4) {
                log.log(Levels.DEBUG, "File " + fildes + " is a regular file of size " + stat.st_size);
                int read = stream.pread(magic, 0, 4, 0);
                if (read == 4 && Endianess.get32bitBE(magic) == Elf.MAGIC && (int) stat.st_size > 0) {
                    log.log(Levels.DEBUG, "File " + fildes + " is an ELF file");
                    // load elf file
                    byte[] buf = new byte[(int) stat.st_size];
                    stream.pread(buf, 0, buf.length, 0);
                    Elf elf = new Elf(buf);
                    log.log(Levels.DEBUG, "Segments: " +
                                    elf.getProgramHeaders().stream().map(phdr -> String.format("0x%08x-0x%08x", phdr.p_vaddr, phdr.p_vaddr + phdr.p_memsz)).collect(Collectors.joining(", ")));
                    log.log(Levels.DEBUG, "Sections: " + elf.sections.stream().map(Section::getName).collect(Collectors.joining(", ")));

                    // find program header of this segment
                    long loadBias = ptr - offset; // strange assumption
                    for (ProgramHeader phdr : elf.getProgramHeaders()) {
                        if (phdr.p_offset == offset) { // this is it, probably
                            loadBias = ptr - phdr.p_vaddr;
                            log.log(Levels.DEBUG, "Program header found: " + String.format("0x%08x-0x%08x", phdr.p_vaddr, phdr.p_vaddr + phdr.p_memsz));
                            log.log(Levels.DEBUG, "Computed load bias is " + HexFormatter.tohex(loadBias, 16));
                            String filename = posix.getFileDescriptor(fildes).name;
                            if (filename == null) {
                                filename = "/proc/self/fd/" + fildes;
                            }
                            libraries.put(loadBias, filename);
                            break;
                        }
                    }

                    SymbolTable symtab = elf.getSymbolTable();
                    if (symtab == null) {
                        symtab = elf.getDynamicSymbolTable();
                    }
                    if (symtab != null) {
                        log.log(Levels.DEBUG, "Loading symbols in range " + HexFormatter.tohex(ptr, 16) + "-" + HexFormatter.tohex(ptr + length, 16) + "...");
                        for (Symbol sym : symtab.getSymbols()) {
                            if (sym.getSectionIndex() != Symbol.SHN_UNDEF && sym.getValue() >= offset && sym.getValue() < offset + length) {
                                symbols.put(sym.getValue() + loadBias, sym.offset(loadBias));
                                log.log(Levels.DEBUG, "Adding symbol " + sym + " for address 0x" + HexFormatter.tohex(sym.getValue() + loadBias, 16));
                            }
                        }
                    }
                }
            }
            symbolResolver = new SymbolResolver(symbols);
        } catch (PosixException | IOException e) {
            log.log(Level.WARNING, "Error while reading symbols: " + e.getMessage(), e);
        }
    }

    private String cstr(long buf) {
        long addr = mem.addr(buf);
        if (addr == 0) {
            return null;
        }
        StringBuilder str = new StringBuilder();
        long ptr = buf;
        while (true) {
            byte b = mem.getI8(ptr);
            if (b == 0) {
                break;
            } else {
                str.append((char) (b & 0xff));
                ptr++;
            }
        }
        return str.toString();
    }

    private PosixPointer posixPointer(long addr) {
        if (addr == 0) {
            return null;
        } else {
            return mem.getPosixPointer(addr);
        }
    }

    private PosixPointer sockaddrPointer(long addr) {
        if (addr == 0) {
            return null;
        } else {
            return new SockaddrPointer(mem.getPosixPointer(addr));
        }
    }

    private long getPointer(PosixPointer ptr, boolean r, boolean w, boolean x, long offset, boolean priv) {
        PosixMemory pmem = new PosixMemory(ptr, false, priv);
        MemoryPage page = mem.allocate(pmem, mem.roundToPageSize(ptr.size()), ptr.getName(), offset);
        page.r = r;
        page.w = w;
        page.x = x;
        return page.getBase();
    }

    private long getPointer(PosixPointer ptr, long address, long size, boolean r, boolean w, boolean x, long offset, boolean priv) {
        long addr = mem.addr(address);
        Memory memory = new PosixMemory(ptr, false, priv);
        MemoryPage page = new MemoryPage(memory, addr, size, ptr.getName(), offset);
        page.r = r;
        page.w = w;
        page.x = x;
        mem.add(page);
        return address;
    }

    // stdin/stdout/stderr are a terminal for now
    public void setStandardIn(InputStream in) {
        posix.setTTY(FileDescriptorManager.STDIN, in);
    }

    public void setStandardOut(OutputStream out) {
        posix.setTTY(FileDescriptorManager.STDOUT, out);
    }

    public void setStandardErr(OutputStream out) {
        posix.setTTY(FileDescriptorManager.STDERR, out);
    }

    public void setStandardIO(InputStream in, OutputStream out, OutputStream err) {
        posix.setTTY(FileDescriptorManager.STDIN, in, out);
        posix.setTTY(FileDescriptorManager.STDOUT, in, out);
        setStandardErr(err);
    }

    public int open(long pathname, int flags, int mode) throws SyscallException {
        try {
            return posix.open(cstr(pathname), flags, mode);
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "open failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public int openat(int fd, long pathname, int flags, int mode) throws SyscallException {
        try {
            return posix.openat(fd, cstr(pathname), flags, mode);
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "openat failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public int close(int fd) throws SyscallException {
        try {
            return posix.close(fd);
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "close failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public int creat(long path, int mode) throws SyscallException {
        try {
            return posix.creat(cstr(path), mode);
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "creat failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public long read(int fd, long buf, long nbyte) throws SyscallException {
        try {
            return posix.read(fd, posixPointer(buf), nbyte);
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "read failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public long write(int fd, long buf, long nbyte) throws SyscallException {
        try {
            return posix.write(fd, posixPointer(buf), nbyte);
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "write failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public long pread64(int fd, long buf, long nbyte, long offset) throws SyscallException {
        try {
            return posix.pread64(fd, posixPointer(buf), nbyte, offset);
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "pread64 failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public long pwrite64(int fd, long buf, long nbyte, long offset) throws SyscallException {
        try {
            return posix.pwrite64(fd, posixPointer(buf), nbyte, offset);
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "pwrite64 failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    private Iovec[] getIov64(long iov, int iovcnt) {
        Iovec[] iovs = new Iovec[iovcnt];
        long ptr = iov;
        for (int i = 0; i < iovcnt; i++) {
            long base = mem.getI64(ptr);
            ptr += 8;
            long size = mem.getI64(ptr);
            ptr += 8;
            PosixPointer baseptr = posixPointer(base);
            assert Long.compareUnsigned(size, Integer.MAX_VALUE) < 0;
            iovs[i] = new Iovec(baseptr, (int) size);
        }
        return iovs;
    }

    public long readv(int fd, long iov, int iovcnt) throws SyscallException {
        if (iovcnt < 0) {
            throw new SyscallException(Errno.EINVAL);
        }
        Iovec[] vec = getIov64(iov, iovcnt);
        try {
            return posix.readv(fd, vec);
        } catch (PosixException e) {
            throw new SyscallException(e.getErrno());
        }
    }

    public long writev(int fd, long iov, int iovcnt) throws SyscallException {
        if (iovcnt < 0) {
            throw new SyscallException(Errno.EINVAL);
        }
        Iovec[] vec = getIov64(iov, iovcnt);
        try {
            return posix.writev(fd, vec);
        } catch (PosixException e) {
            throw new SyscallException(e.getErrno());
        }
    }

    public long dup(int fildes) throws SyscallException {
        try {
            return posix.dup(fildes);
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "dup failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public long dup2(int fildes, int fildes2) throws SyscallException {
        try {
            return posix.dup2(fildes, fildes2);
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "dup2 failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public long dup3(int oldfd, int newfd, int flags) throws SyscallException {
        try {
            return posix.dup3(oldfd, newfd, flags);
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "dup3 failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public long readlink(long path, long buf, long bufsize) throws SyscallException {
        String p = cstr(path);
        PosixPointer ptr = posixPointer(buf);
        if (p.equals("/proc/self/exe")) {
            if (strace) {
                log.log(Level.INFO, () -> String.format("readlink(\"%s\", 0x%x, %d)", p, buf, bufsize));
            }
            CString.strcpy(ptr, execfn);
            return execfn.length();
        }
        try {
            return posix.readlink(p, ptr, bufsize);
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "readlink failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public int unlink(long pathname) throws SyscallException {
        String path = cstr(pathname);
        try {
            return posix.unlink(path);
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "unlink failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public long stat(long pathname, long statbuf) throws SyscallException {
        PosixPointer ptr = posixPointer(statbuf);
        Stat stat = new Stat();
        try {
            int result = posix.stat(cstr(pathname), stat);
            stat.write64(ptr);
            return result;
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "stat failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public long lstat(long pathname, long statbuf) throws SyscallException {
        PosixPointer ptr = posixPointer(statbuf);
        Stat stat = new Stat();
        try {
            int result = posix.lstat(cstr(pathname), stat);
            stat.write64(ptr);
            return result;
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "lstat failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public long fstat(int fd, long statbuf) throws SyscallException {
        PosixPointer ptr = posixPointer(statbuf);
        Stat stat = new Stat();
        try {
            int result = posix.fstat(fd, stat);
            stat.write64(ptr);
            return result;
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "fstat failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public long lseek(int fd, long offset, int whence) throws SyscallException {
        try {
            return posix.lseek(fd, offset, whence);
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "lseek failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public int access(long pathname, int mode) throws SyscallException {
        String path = cstr(pathname);
        try {
            return posix.access(path, mode);
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "access failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public long fcntl(int fd, int cmd, long arg) throws SyscallException {
        try {
            switch (cmd) {
                case Fcntl.F_GETFD:
                case Fcntl.F_GETFL:
                case Fcntl.F_SETFD:
                case Fcntl.F_SETFL:
                case Fcntl.F_DUPFD:
                case Fcntl.F_DUPFD_CLOEXEC:
                    return posix.fcntl(fd, cmd, (int) arg);
                default:
                    log.log(Level.INFO, "fcntl command not implemented: " + cmd);
                    throw new PosixException(Errno.EINVAL);
            }
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "fcntl failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public long getdents(int fd, long dirp, int count) throws SyscallException {
        long cnt = Integer.toUnsignedLong(count);
        try {
            return posix.getdents(fd, posixPointer(dirp), cnt, Dirent.DIRENT_64);
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "getdents failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public long getdents64(int fd, long dirp, int count) throws SyscallException {
        long cnt = Integer.toUnsignedLong(count);
        try {
            return posix.getdents(fd, posixPointer(dirp), cnt, Dirent.DIRENT64);
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "getdents64 failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public int futex(long uaddr, int futex_op, int val, long timeout, long uaddr2, int val3) throws SyscallException {
        try {
            return posix.futex(posixPointer(uaddr), futex_op, val, posixPointer(timeout), posixPointer(uaddr2), val3);
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "futex failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public long getcwd(long buf, long size) throws SyscallException {
        try {
            posix.getcwd(posixPointer(buf), size);
            String cwd = cstr(buf);
            return cwd.length() + 1;
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "getcwd failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public long ioctl(int fd, long request, long arg) throws SyscallException {
        assert request == (int) request;
        try {
            int ioctl = Ioctls.translate((int) request);
            switch ((int) request) {
                case Ioctls.TCGETS: {
                    // different layout of struct termios: missing last two entries (speed)
                    byte[] buf = new byte[44];
                    BytePosixPointer tmp = new BytePosixPointer(buf);
                    int result = posix.ioctl(fd, ioctl, tmp);
                    PosixPointer src = tmp;
                    PosixPointer dst = posixPointer(arg);
                    for (int i = 0; i < 36 / 4; i++) {
                        int val = src.getI32();
                        dst.setI32(val);
                        src = src.add(4);
                        dst = dst.add(4);
                    }
                    return result;
                }
                default:
                    return posix.ioctl(fd, ioctl, posixPointer(arg));
            }
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "ioctl failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    @SuppressWarnings("unused")
    public int fsync(int fildes) throws SyscallException {
        // TODO: implement!
        return 0;
    }

    public int uname(long buf) throws SyscallException {
        PosixPointer ptr = posixPointer(buf);
        Utsname uname = new Utsname();
        try {
            int result = posix.uname(uname);
            uname.machine = arch;
            uname.write(ptr);
            return result;
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "uname failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public long setuid(long uid) throws SyscallException {
        try {
            return posix.setuid(uid);
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "setuid failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public long getuid() {
        return posix.getuid();
    }

    public long geteuid() {
        return posix.getuid(); // TODO: implement euid
    }

    public long setgid(long uid) throws SyscallException {
        try {
            return posix.setgid(uid);
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "setgid failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public long getgid() {
        return posix.getgid();
    }

    public long getegid() {
        return posix.getgid(); // TODO: implement egid
    }

    public long getpid() {
        return posix.getpid();
    }

    public long gettid() {
        return posix.getpid(); // TODO: implement tid
    }

    private void logMmap(long addr, long length, int prot, int flags, int fildes, long offset, long result) {
        if (traceWriter != null) {
            traceWriter.mmap(addr, length, prot, flags, fildes, offset, result, null);
        }
    }

    private void logMmap(long addr, long length, int prot, int flags, int fildes, long offset, long result, PosixPointer ptr) {
        if (traceWriter != null) {
            byte[] data = new byte[(int) length];
            try {
                for (int i = 0; i < data.length; i++) {
                    data[i] = ptr.add(i).getI8();
                }
            } catch (Throwable t) {
                // swallow
            }
            traceWriter.mmap(addr, length, prot, flags, fildes, offset, result, data);
        }
    }

    public long mmap(long addr, long length, int pr, int fl, int fildes, long offset) throws SyscallException {
        int flags = fl | Mman.MAP_PRIVATE;
        int prot = pr | Mman.PROT_WRITE;
        try {
            if (mem.pageStart(addr) != mem.addr(addr)) {
                throw new PosixException(Errno.EINVAL);
            }
            if (length == 0) {
                throw new PosixException(Errno.EINVAL);
            }
            if (BitTest.test(flags, Mman.MAP_ANONYMOUS) && BitTest.test(flags, Mman.MAP_PRIVATE)) {
                if (strace) {
                    log.log(Levels.INFO, () -> String.format("mmap(0x%016x, %d, %s, %s, %d, %d)", addr,
                                    length, Mman.prot(prot), Mman.flags(flags), fildes, offset));
                }
                MemoryPage page;
                try {
                    if (BitTest.test(flags, Mman.MAP_FIXED) || addr != 0) {
                        long aligned = addr;
                        if (!BitTest.test(flags, Mman.MAP_FIXED)) {
                            aligned = mem.pageStart(addr);
                        }
                        Memory bytes = new ByteMemory(length, false);
                        page = new MemoryPage(bytes, mem.addr(aligned), mem.roundToPageSize(length));
                        mem.add(page);
                    } else {
                        page = mem.allocate(mem.roundToPageSize(length));
                    }
                } catch (OutOfMemoryError e) {
                    throw new SyscallException(Errno.ENOMEM);
                }
                page.x = BitTest.test(prot, Mman.PROT_EXEC);
                logMmap(addr, length, pr, fl, fildes, offset, page.base);
                return page.base;
            }
            PosixPointer p = new PosixVirtualMemoryPointer(mem, addr);
            PosixPointer ptr = posix.mmap(p, length, prot, flags, fildes, offset);
            boolean r = BitTest.test(prot, Mman.PROT_READ);
            boolean w = BitTest.test(prot, Mman.PROT_WRITE);
            boolean x = BitTest.test(prot, Mman.PROT_EXEC);
            boolean priv = BitTest.test(flags, Mman.MAP_PRIVATE);
            long result;
            if (BitTest.test(flags, Mman.MAP_FIXED)) {
                result = getPointer(ptr, addr, mem.roundToPageSize(length), r, w, x, offset, priv);
            } else {
                assert mem.roundToPageSize(ptr.size()) == mem.roundToPageSize(length);
                result = getPointer(ptr, r, w, x, offset, priv);
            }
            if (DEBUG) {
                loadSymbols(fildes, offset, result, length);
            }
            logMmap(addr, length, pr, fl, fildes, offset, result, ptr);
            return result;
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "mmap failed: " + Errno.toString(e.getErrno()));
            }
            logMmap(addr, length, pr, fl, fildes, offset, -e.getErrno());
            throw new SyscallException(e.getErrno());
        }
    }

    public int munmap(long addr, long length) throws SyscallException {
        if (strace) {
            log.log(Level.INFO, () -> String.format("munmap(0x%016x, %s)", addr, length));
        }
        try {
            // return posix.munmap(posixPointer(addr), length);
            mem.remove(addr, mem.roundToPageSize(length));
            if (traceWriter != null) {
                traceWriter.munmap(addr, length, 0);
            }
            return 0;
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "munmap failed: " + Errno.toString(e.getErrno()));
            }
            if (traceWriter != null) {
                traceWriter.munmap(addr, length, -e.getErrno());
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public int mprotect(long addr, long size, int prot) throws SyscallException {
        if (strace) {
            log.log(Level.INFO, () -> String.format("mprotect(0x%016x, %d, %s)", addr, size, Mman.prot(prot)));
        }
        try {
            boolean r = BitTest.test(prot, Mman.PROT_READ);
            boolean w = BitTest.test(prot, Mman.PROT_WRITE);
            boolean x = BitTest.test(prot, Mman.PROT_EXEC);
            mem.mprotect(addr, size, r, w, x);
            if (traceWriter != null) {
                traceWriter.mprotect(addr, size, prot, 0);
            }
        } catch (PosixException e) {
            if (traceWriter != null) {
                traceWriter.mprotect(addr, size, prot, -e.getErrno());
            }
            throw new SyscallException(e.getErrno());
        } catch (SegmentationViolation e) {
            if (traceWriter != null) {
                traceWriter.mprotect(addr, size, prot, -Errno.ENOMEM);
            }
            throw new SyscallException(Errno.ENOMEM);
        }
        return 0;
    }

    public int clock_getres(int clk_id, long tp) throws SyscallException {
        try {
            Timespec t = new Timespec();
            int val = posix.clock_getres(clk_id, t);
            if (tp != 0) {
                PosixPointer ptr = posixPointer(tp);
                t.write64(ptr);
            }
            return val;
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "clock_getres failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public int clock_gettime(int clk_id, long tp) throws SyscallException {
        try {
            Timespec t = new Timespec();
            int val = posix.clock_gettime(clk_id, t);
            if (STATIC_TIME) {
                t = new Timespec();
            }
            PosixPointer ptr = posixPointer(tp);
            t.write64(ptr);
            return val;
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "clock_gettime failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public int gettimeofday(long tp, @SuppressWarnings("unused") long tzp) {
        Timeval t = new Timeval();
        int val = posix.gettimeofday(t, null);
        if (STATIC_TIME) {
            t = new Timeval();
        }
        PosixPointer ptr = posixPointer(tp);
        t.write64(ptr);
        return val;
    }

    public long times(long buffer) throws SyscallException {
        try {
            Tms buf = new Tms();
            long val = posix.times(buf);
            if (STATIC_TIME) {
                buf = new Tms();
            }
            PosixPointer ptr = posixPointer(buffer);
            buf.write64(ptr);
            return val;
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "times failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public int prlimit64(int pid, int resource, long newLimit, long oldLimit) throws SyscallException {
        if (pid != 0) {
            if (strace) {
                log.log(Level.INFO, "prlimit64 failed: " + Errno.toString(Errno.ESRCH));
            }
            throw new SyscallException(Errno.ESRCH);
        }
        try {
            Rlimit rlim = new Rlimit();
            int result = 0;
            if (oldLimit != 0) {
                result = posix.getrlimit(resource, rlim);
                rlim.write64(posixPointer(oldLimit));
            }
            if (newLimit != 0) {
                // rlim.read64(posixPointer(newLimit));
                // result = posix.setrlimit(resource, rlim);
            }
            return result;
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "prlimit64 failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public int rt_sigaction(int sig, long act, long oact) throws SyscallException {
        try {
            PosixPointer pact = posixPointer(act);
            PosixPointer poact = posixPointer(oact);
            Sigaction newact = null;
            if (pact != null) {
                newact = new Sigaction();
                newact.read64(pact);
            }
            Sigaction oldact = poact != null ? new Sigaction() : null;
            int result = posix.sigaction(sig, newact, oldact);
            if (poact != null) {
                oldact.write64(poact);
            }
            return result;
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "rt_sigaction failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public int rt_sigprocmask(int how, long set, long oldset, int sigsetsize) throws SyscallException {
        try {
            if (sigsetsize != 8) {
                throw new PosixException(Errno.EINVAL);
            }
            PosixPointer pset = posixPointer(set);
            PosixPointer poldset = posixPointer(oldset);
            Sigset sset = null;
            Sigset soldset = null;
            if (pset != null) {
                sset = new Sigset();
                sset.read64(pset);
            }
            if (poldset != null) {
                soldset = new Sigset();
            }
            int result = posix.sigprocmask(how, sset, soldset);
            if (poldset != null) {
                soldset.write64(poldset);
            }
            return result;
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "rt_sigprocmask failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public int sigaltstack(long ss, long oldss) throws SyscallException {
        try {
            PosixPointer pss = posixPointer(ss);
            PosixPointer poldss = posixPointer(oldss);
            Stack sigstk = null;
            if (pss != null) {
                sigstk = new Stack();
                sigstk.read64(pss);
            }
            Stack oldsigstk = poldss == null ? null : new Stack();
            int result = posix.sigaltstack(sigstk, oldsigstk);
            if (poldss != null) {
                oldsigstk.write64(poldss);
            }
            return result;
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "sigaltstack failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public int poll(long fds, int nfds, int timeout) throws SyscallException {
        try {
            PosixPointer pfds = posixPointer(fds);
            Pollfd[] parsed = new Pollfd[nfds];
            PosixPointer ptr = pfds;
            for (int i = 0; i < nfds; i++) {
                parsed[i] = new Pollfd();
                ptr = parsed[i].read(ptr);
            }

            int result = posix.poll(parsed, nfds, timeout);

            if (result > 0) {
                ptr = pfds;
                for (int i = 0; i < nfds; i++) {
                    ptr = parsed[i].write(ptr);
                }
            }

            return result;
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "poll failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public int socket(int domain, int type, int protocol) throws SyscallException {
        try {
            return posix.socket(domain, type, protocol);
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "socket failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public int connect(int socket, long address, int addressLen) throws SyscallException {
        try {
            return posix.connect(socket, sockaddrPointer(address), addressLen);
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "connect failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public int bind(int socket, long address, int addressLen) throws SyscallException {
        try {
            return posix.bind(socket, sockaddrPointer(address), addressLen);
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "bind failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public int getsockname(int socket, long address, long address_len) throws SyscallException {
        try {
            Sockaddr sa = posix.getsockname(socket);
            sa.write(sockaddrPointer(address));
            posixPointer(address_len).setI32(sa.getSize());
            return 0;
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "getsockname failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public int getpeername(int socket, long address, long address_len) throws SyscallException {
        try {
            Sockaddr sa = posix.getpeername(socket);
            sa.write(sockaddrPointer(address));
            posixPointer(address_len).setI32(sa.getSize());
            return 0;
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "getpeername failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public int setsockopt(int sock, int level, int option_name, long option_value, int option_len) throws SyscallException {
        if (option_len != 4) {
            log.log(Level.WARNING, "Invalid option_len " + option_len + " in setsockopt");
            throw new SyscallException(Errno.EINVAL);
        }
        try {
            int val = posixPointer(option_value).getI32();
            return posix.setsockopt(sock, level, option_name, val);
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "getsockname failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public int shutdown(int socket, int how) throws SyscallException {
        try {
            return posix.shutdown(socket, how);
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "shutdown failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public long sendto(int socket, long buf, long len, int flags, long dest_addr, int addrlen) throws SyscallException {
        try {
            return posix.sendto(socket, posixPointer(buf), len, flags, sockaddrPointer(dest_addr), addrlen);
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "sendto failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public long recvfrom(int sock, long buffer, long length, int flags, long address, long address_len) throws SyscallException {
        try {
            RecvResult result = posix.recvfrom(sock, posixPointer(buffer), length, flags);
            if (address != 0) {
                result.sa.write(sockaddrPointer(address));
            }
            if (address_len != 0) {
                posixPointer(address_len).setI32(16);
            }
            return result.length;
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "recvfrom failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    private Msghdr parseMsghdr64(PosixPointer p) {
        PosixPointer ptr = p;
        Msghdr msghdr = new Msghdr();

        long msg_name = ptr.getI64();
        int msg_namelen = (int) ptr.add(8).getI64();
        ptr = ptr.add(16);
        if (msg_name != 0) {
            msghdr.msg_name = Sockaddr.get(sockaddrPointer(msg_name), msg_namelen);
        }
        long iov = ptr.getI64();
        msghdr.msg_iovlen = (int) ptr.add(8).getI64();
        msghdr.msg_iov = getIov64(iov, msghdr.msg_iovlen);
        ptr = ptr.add(16);
        // TODO: parse msg_control
        msghdr.msg_control = null;
        ptr = ptr.add(16);
        msghdr.msg_flags = ptr.getI32();
        return msghdr;
    }

    public long recvmsg(int socket, long message, int flags) throws SyscallException {
        try {
            PosixPointer ptr = posixPointer(message);
            Msghdr msg = parseMsghdr64(ptr);
            long result = posix.recvmsg(socket, msg, flags);
            PosixPointer msg_name = sockaddrPointer(ptr.getI64());
            if (msg_name != null && msg.msg_name != null) {
                msg.msg_name.write64(msg_name);
                ptr.add(8).setI32(msg.msg_name.getSize());
            }
            PosixPointer msg_control = posixPointer(ptr.add(32).getI64());
            int msg_controllen = ptr.add(40).getI32();
            if (msg_control != null && msg_controllen > 0) {
                if (msg.msg_control == null) {
                    ptr.add(40).setI64(0);
                } else {
                    ptr.add(40).setI64(16 + msg.msg_control.cmsg_data.length);
                }
            } else {
                ptr.add(40).setI64(0);
            }
            ptr.add(48).setI32(msg.msg_flags);
            return result;
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "recvmsg failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public int sysinfo(long info) throws SyscallException {
        try {
            Sysinfo sysinfo = new Sysinfo();
            int result = posix.sysinfo(sysinfo);
            sysinfo.write64(posixPointer(info));
            return result;
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "sysinfo failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public long time(long tloc) {
        if (strace) {
            log.log(Level.INFO, () -> String.format("time(0x%016x)", tloc));
        }
        long time = System.currentTimeMillis() / 1000;
        if (STATIC_TIME) {
            time = 0;
        }
        if (tloc != 0) {
            PosixPointer ptr = posixPointer(tloc);
            ptr.setI64(time);
        }
        return time;
    }

    public Posix getPosix() {
        return posix;
    }

    public VFS getVFS() {
        return posix.getVFS();
    }

    public void mount(String path, FileSystem fs) throws PosixException {
        posix.getVFS().mount(path, fs);
    }

    public Stack getSigaltstack() {
        return posix.getSigaltstack();
    }

    // DEBUGGING FEATURES (NONSTANDARD!)
    public void printk(long a1, long a2, long a3, long a4, long a5, long a6) throws SyscallException {
        String fmt = cstr(a1);
        StringBuilder buf = new StringBuilder(fmt.length());
        int state = 0;
        long[] args = {a2, a3, a4, a5, a6};
        int argidx = 0;
        for (char c : fmt.toCharArray()) {
            switch (state) {
                case 0: // normal text
                    switch (c) {
                        case '%':
                            state = 1;
                            break;
                        default:
                            buf.append(c);
                    }
                    break;
                case 1: // '%'
                    switch (c) {
                        case 's':
                            buf.append(cstr(args[argidx++]));
                            state = 0;
                            break;
                        case 'd':
                            buf.append(args[argidx++]);
                            state = 0;
                            break;
                        case 'x':
                            buf.append(Long.toHexString(args[argidx++]));
                            state = 0;
                            break;
                        case '%':
                            buf.append('%');
                            state = 0;
                            break;
                        default:
                            buf.append('%');
                            buf.append(c);
                            state = 0;
                            break;
                    }
                    break;
            }
        }
        byte[] bytes = buf.toString().getBytes();
        PosixPointer ptr = new BytePosixPointer(bytes);
        try {
            posix.write(1, ptr, bytes.length);
        } catch (PosixException e) {
            throw new SyscallException(e.getErrno());
        }
    }
}
