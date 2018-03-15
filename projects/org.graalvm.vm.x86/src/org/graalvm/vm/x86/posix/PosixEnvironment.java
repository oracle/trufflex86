package org.graalvm.vm.x86.posix;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graalvm.vm.memory.ByteMemory;
import org.graalvm.vm.memory.Memory;
import org.graalvm.vm.memory.MemoryPage;
import org.graalvm.vm.memory.PosixMemory;
import org.graalvm.vm.memory.PosixVirtualMemoryPointer;
import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.memory.exception.SegmentationViolation;

import com.everyware.posix.api.BytePosixPointer;
import com.everyware.posix.api.Errno;
import com.everyware.posix.api.Posix;
import com.everyware.posix.api.PosixException;
import com.everyware.posix.api.PosixPointer;
import com.everyware.posix.api.Utsname;
import com.everyware.posix.api.io.FileDescriptorManager;
import com.everyware.posix.api.io.Iovec;
import com.everyware.posix.api.io.Stat;
import com.everyware.posix.api.mem.Mman;
import com.everyware.posix.vfs.FileSystem;
import com.everyware.posix.vfs.VFS;
import com.everyware.util.BitTest;
import com.everyware.util.log.Levels;
import com.everyware.util.log.Trace;

public class PosixEnvironment {
    private static final Logger log = Trace.create(PosixEnvironment.class);

    private VirtualMemory mem;
    private Posix posix;
    private boolean strace;
    private String arch;

    public PosixEnvironment(VirtualMemory mem, String arch) {
        this.mem = mem;
        this.arch = arch;
        posix = new Posix();
        strace = System.getProperty("posix.strace") != null;
    }

    public void setStrace(boolean value) {
        strace = value;
        posix.setStrace(value);
    }

    public boolean isStrace() {
        return strace;
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
        return mem.getPosixPointer(addr);
    }

    private long getPointer(PosixPointer ptr, boolean r, boolean w, boolean x) {
        PosixMemory pmem = new PosixMemory(ptr, false);
        MemoryPage page = mem.allocate(pmem, mem.roundToPageSize(ptr.size()), ptr.getName());
        page.r = r;
        page.w = w;
        page.x = x;
        return page.getBase();
    }

    private long getPointer(PosixPointer ptr, long address, long size, boolean r, boolean w, boolean x) {
        long addr = mem.addr(address);
        Memory memory = new PosixMemory(ptr, false);
        MemoryPage page = new MemoryPage(memory, addr, size, ptr.getName());
        page.r = r;
        page.w = w;
        page.x = x;
        mem.add(page);
        return address;
    }

    public void setStandardIn(InputStream in) {
        posix.setStream(FileDescriptorManager.STDIN, in);
    }

    public void setStandardOut(OutputStream out) {
        posix.setStream(FileDescriptorManager.STDOUT, out);
    }

    public void setStandardErr(OutputStream out) {
        posix.setStream(FileDescriptorManager.STDERR, out);
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

    public long readlink(long path, long buf, long bufsize) throws SyscallException {
        String p = cstr(path);
        PosixPointer ptr = posixPointer(buf);
        try {
            return posix.readlink(p, ptr, bufsize);
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "readlink failed: " + Errno.toString(e.getErrno()));
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

    public long getcwd(long buf, long size) throws SyscallException {
        try {
            posix.getcwd(posixPointer(buf), size);
            return buf;
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

    public long getpid() {
        return posix.getpid();
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
                if (BitTest.test(flags, Mman.MAP_FIXED)) {
                    Memory bytes = new ByteMemory(length, false);
                    page = new MemoryPage(bytes, mem.addr(addr), mem.roundToPageSize(length));
                    mem.add(page);
                } else {
                    page = mem.allocate(mem.roundToPageSize(length));
                }
                page.x = BitTest.test(prot, Mman.PROT_EXEC);
                return page.base;
            }
            PosixPointer p = new PosixVirtualMemoryPointer(mem, addr);
            PosixPointer ptr = posix.mmap(p, length, prot, flags, fildes, offset);
            boolean r = BitTest.test(prot, Mman.PROT_READ);
            boolean w = BitTest.test(prot, Mman.PROT_WRITE);
            boolean x = BitTest.test(prot, Mman.PROT_EXEC);
            if (BitTest.test(flags, Mman.MAP_FIXED)) {
                return getPointer(ptr, addr, mem.roundToPageSize(length), r, w, x);
            } else {
                assert mem.roundToPageSize(ptr.size()) == mem.roundToPageSize(length);
                return getPointer(ptr, r, w, x);
            }
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "mmap failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public int munmap(long addr, long length) throws SyscallException {
        if (strace) {
            log.log(Level.INFO, () -> String.format("munmap(0x%016x, %s)", addr, length));
        }
        try {
            // return posix.munmap(posixPointer(addr), length);
            mem.remove(addr, length);
            return 0;
        } catch (PosixException e) {
            if (strace) {
                log.log(Level.INFO, "munmap failed: " + Errno.toString(e.getErrno()));
            }
            throw new SyscallException(e.getErrno());
        }
    }

    public int mprotect(long addr, long size, int prot) throws SyscallException {
        if (strace) {
            log.log(Level.INFO, () -> String.format("mprotect(0x%016x, %d, %s)", addr, size, Mman.prot(prot)));
        }
        try {
            MemoryPage page = mem.get(addr);
            // TODO: handle length properly
            if (page.base == mem.addr(addr)) {
                // TODO: this is a hack because the length is not handled properly
                page.r |= BitTest.test(prot, Mman.PROT_READ);
                page.w |= BitTest.test(prot, Mman.PROT_WRITE);
                page.x |= BitTest.test(prot, Mman.PROT_EXEC);
            }
        } catch (SegmentationViolation e) {
            throw new SyscallException(Errno.ENOMEM);
        }
        return 0;
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
}
