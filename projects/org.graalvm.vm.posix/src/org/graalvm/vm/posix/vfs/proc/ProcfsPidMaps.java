package org.graalvm.vm.posix.vfs.proc;

import java.util.Date;

import org.graalvm.vm.posix.api.Errno;
import org.graalvm.vm.posix.api.MemoryMapProvider;
import org.graalvm.vm.posix.api.Posix;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.io.Stream;
import org.graalvm.vm.posix.vfs.VFSDirectory;
import org.graalvm.vm.posix.vfs.VFSFile;

public class ProcfsPidMaps extends VFSFile {
    private final Posix posix;
    private final Date ctime;

    public ProcfsPidMaps(VFSDirectory parent, String path, long uid, long gid, long permissions, Posix posix) {
        super(parent, path, uid, gid, permissions);
        this.posix = posix;
        ctime = new Date();
    }

    @Override
    protected Stream open(boolean read, boolean write) throws PosixException {
        if (write) {
            throw new PosixException(Errno.EPERM);
        }
        MemoryMapProvider provider = posix.getMemoryMapProvider();
        if (provider == null) {
            throw new PosixException(Errno.EIO);
        }
        byte[] data = provider.getMemoryMap();
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
