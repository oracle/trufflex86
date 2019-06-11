package org.graalvm.vm.posix.vfs.proc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.graalvm.vm.posix.api.Errno;
import org.graalvm.vm.posix.api.Posix;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.vfs.VFSDirectory;
import org.graalvm.vm.posix.vfs.VFSEntry;
import org.graalvm.vm.posix.vfs.VFSFile;
import org.graalvm.vm.posix.vfs.VFSSymlink;

public class ProcfsTasksDirectory extends VFSDirectory {
    private final Date creationTime;
    private final Posix posix;

    public ProcfsTasksDirectory(VFSDirectory parent, String path, long uid, long gid, long permissions, Posix posix) {
        super(parent, path, uid, gid, permissions);
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

    private VFSEntry getThreadDirectory(int id) throws PosixException {
        if (!posix.hasThread(id)) {
            throw new PosixException(Errno.ENOENT);
        }
        return new ProcfsThreadDirectory(this, Integer.toString(id), 0, 0, 0755, posix);
    }

    @Override
    protected VFSEntry getEntry(String name) throws PosixException {
        try {
            int tid = Integer.parseInt(name);
            return getThreadDirectory(tid);
        } catch (NumberFormatException e) {
            // nothing
        }
        throw new PosixException(Errno.ENOENT);
    }

    @Override
    protected List<VFSEntry> list() throws PosixException {
        List<VFSEntry> result = new ArrayList<>();
        List<Integer> tids = posix.getTids().stream().sorted().collect(Collectors.toList());
        for (int tid : tids) {
            result.add(getThreadDirectory(tid));
        }
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
