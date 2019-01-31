package org.graalvm.vm.posix.vfs;

import org.graalvm.vm.posix.api.PosixException;

public class TmpfsSymlink extends VFSSymlink {
	private String link;

	protected TmpfsSymlink(VFSDirectory parent, String path, long uid, long gid, long permissions, String link) {
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
