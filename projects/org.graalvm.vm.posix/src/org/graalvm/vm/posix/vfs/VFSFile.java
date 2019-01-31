package org.graalvm.vm.posix.vfs;

import static org.graalvm.vm.posix.api.io.Fcntl.O_RDONLY;
import static org.graalvm.vm.posix.api.io.Fcntl.O_RDWR;
import static org.graalvm.vm.posix.api.io.Fcntl.O_TMPFILE;
import static org.graalvm.vm.posix.api.io.Fcntl.O_WRONLY;

import org.graalvm.vm.posix.api.Errno;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.io.Stat;
import org.graalvm.vm.posix.api.io.Stream;
import org.graalvm.vm.util.BitTest;

public abstract class VFSFile extends VFSEntry {
	public VFSFile(VFSDirectory parent, String path, long uid, long gid, long permissions) {
		super(parent, path, uid, gid, permissions);
	}

	@SuppressWarnings("unused")
	protected Stream open(boolean read, boolean write) throws PosixException {
		throw new AssertionError("not implemented");
	}

	public Stream open(int flags) throws PosixException {
		return open(flags, 0);
	}

	public Stream open(int flags, @SuppressWarnings("unused") int mode) throws PosixException {
		int rdwr = flags & 0x3;
		switch(rdwr) {
		case O_RDONLY:
			if(BitTest.test(flags, O_TMPFILE)) {
				throw new PosixException(Errno.EINVAL);
			}
			return open(true, false);
		case O_WRONLY:
			if(BitTest.test(flags, O_TMPFILE)) {
				throw new PosixException(Errno.EINVAL);
			}
			return open(false, true);
		case O_RDWR:
			if(BitTest.test(flags, O_TMPFILE)) {
				throw new PosixException(Errno.EINVAL);
			}
			return open(true, true);
		default:
			throw new PosixException(Errno.EINVAL);
		}
	}

	@Override
	public void stat(Stat buf) throws PosixException {
		super.stat(buf);
		buf.st_nlink = 1;
		buf.st_mode |= Stat.S_IFREG;
	}

	@Override
	public String toString() {
		return "VFSFile[" + getPath() + "]";
	}
}
