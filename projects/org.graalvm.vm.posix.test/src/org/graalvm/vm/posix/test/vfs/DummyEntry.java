package org.graalvm.vm.posix.test.vfs;

import java.util.Date;

import org.graalvm.vm.posix.vfs.VFS;
import org.graalvm.vm.posix.vfs.VFSEntry;

public class DummyEntry extends VFSEntry {
	public DummyEntry(VFS vfs, String path) {
		super(vfs, path);
	}

	@Override
	public long size() {
		return 42;
	}

	@Override
	public Date atime() {
		return null;
	}

	@Override
	public Date mtime() {
		return null;
	}

	@Override
	public Date ctime() {
		return null;
	}

	@Override
	public void atime(Date time) {
	}

	@Override
	public void mtime(Date time) {
	}

	@Override
	public void ctime(Date time) {
	}
}
