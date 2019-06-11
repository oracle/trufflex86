package org.graalvm.vm.posix.vfs.proc;

import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.vfs.VFSDirectory;
import org.graalvm.vm.posix.vfs.VFSEntry;
import org.graalvm.vm.posix.vfs.VFSSymlink;

public class ProcfsSymlink extends VFSSymlink {
    private final String link;

    protected ProcfsSymlink(VFSDirectory parent, String path, long uid, long gid, long permissions, String link) {
        super(parent, path, uid, gid, permissions);
        this.link = link;
    }

    @Override
    public VFSEntry getTarget() throws PosixException {
        return getVFS().getat(this, link);
    }

    @Override
    public String readlink() throws PosixException {
        return link;
    }
}
