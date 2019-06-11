package org.graalvm.vm.posix.vfs.proc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.graalvm.vm.posix.api.Errno;
import org.graalvm.vm.posix.api.Posix;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.vfs.VFS;
import org.graalvm.vm.posix.vfs.VFSDirectory;
import org.graalvm.vm.posix.vfs.VFSEntry;
import org.graalvm.vm.posix.vfs.VFSFile;
import org.graalvm.vm.posix.vfs.VFSSymlink;

public class ProcfsRoot extends VFSDirectory {
    private final Date creationTime;
    private final Posix posix;

    public ProcfsRoot(VFS vfs, String path, long uid, long gid, long permissions, Posix posix) {
        super(vfs, path, uid, gid, permissions);
        this.posix = posix;
        creationTime = new Date();
    }

    @Override
    protected void create(VFSEntry file) throws PosixException {
        throw new PosixException(Errno.EPERM);
    }

    @Override
    protected VFSDirectory createDirectory(String name, long uid, long gid, long permissions) throws PosixException {
        throw new PosixException(Errno.EPERM);
    }

    @Override
    protected VFSFile createFile(String name, long uid, long gid, long permissions) throws PosixException {
        throw new PosixException(Errno.EPERM);
    }

    @Override
    protected VFSSymlink createSymlink(String name, long uid, long gid, long permissions, String target) throws PosixException {
        throw new PosixException(Errno.EPERM);
    }

    @Override
    protected void delete(String name) throws PosixException {
        throw new PosixException(Errno.EPERM);
    }

    private VFSEntry getProcessDirectory(int id) throws PosixException {
        if (id != 1) { // only PID 1 exists
            throw new PosixException(Errno.ENOENT);
        }
        return new ProcfsProcessDirectory(this, Integer.toString(id), 0, 0, 0755, posix);
    }

    @Override
    protected VFSEntry getEntry(String name) throws PosixException {
        switch (name) {
            case "self":
                // only PID 1 exists for now
                return new ProcfsSymlink(this, "self", 0, 0, 0755, Integer.toString(1));
            case "thread-self":
                return new ProcfsSymlink(this, "thread-self", 0, 0, 0755, "1/task/" + Integer.toString(Posix.getTid()));
            default:
                try {
                    int tid = Integer.parseInt(name);
                    return getProcessDirectory(tid);
                } catch (NumberFormatException e) {
                    // nothing
                }
                throw new PosixException(Errno.ENOENT);
        }
    }

    @Override
    protected List<VFSEntry> list() throws PosixException {
        List<VFSEntry> result = new ArrayList<>();
        result.add(getProcessDirectory(1));
        result.add(new ProcfsSymlink(this, "self", 0, 0, 0755, Integer.toString(1)));
        result.add(new ProcfsSymlink(this, "thread-self", 0, 0, 0755, "1/task/" + Integer.toString(Posix.getTid())));
        return result;
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
        return creationTime;
    }

    @Override
    public Date mtime() throws PosixException {
        return creationTime;
    }

    @Override
    public Date ctime() throws PosixException {
        return creationTime;
    }
}
