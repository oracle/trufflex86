package org.graalvm.vm.posix.vfs.proc;

import java.util.Date;
import java.util.function.Supplier;

import org.graalvm.vm.posix.api.Errno;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.io.Stream;
import org.graalvm.vm.posix.vfs.VFSDirectory;
import org.graalvm.vm.posix.vfs.VFSFile;

public class ProcfsDynamicReadOnlyFile extends VFSFile {
    private final Supplier<String> src;
    private final Date ctime;

    public ProcfsDynamicReadOnlyFile(VFSDirectory parent, String path, long uid, long gid, long permissions, Supplier<String> src) {
        super(parent, path, uid, gid, permissions);
        this.src = src;
        ctime = new Date();
    }

    @Override
    protected Stream open(boolean read, boolean write) throws PosixException {
        if (write) {
            throw new PosixException(Errno.EPERM);
        }
        String content = src.get();
        if (content == null) {
            throw new PosixException(Errno.EIO);
        }
        byte[] data = content.getBytes();
        return new ProcfsReadOnlyStream(this, data);
    }

    @Override
    public long size() throws PosixException {
        return 0;
    }

    @Override
    public void atime(Date time) throws PosixException {
        throw new PosixException(Errno.EPERM);
    }

    @Override
    public void mtime(Date time) throws PosixException {
        throw new PosixException(Errno.EPERM);
    }

    @Override
    public void ctime(Date time) throws PosixException {
        throw new PosixException(Errno.EPERM);
    }

    @Override
    public Date atime() throws PosixException {
        return ctime;
    }

    @Override
    public Date mtime() throws PosixException {
        return ctime;
    }

    @Override
    public Date ctime() throws PosixException {
        return ctime;
    }
}
