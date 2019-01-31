package org.graalvm.vm.posix.vfs;

import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.io.Stat;

public abstract class VFSSpecialFile extends VFSFile {
	public static final int CHAR_DEVICE = 0;
	public static final int BLOCK_DEVICE = 1;
	public static final int FIFO = 2;
	public static final int SOCKET = 3;

	private int type;

	public VFSSpecialFile(VFSDirectory parent, String path, long uid, long gid, long permissions, int type) {
		super(parent, path, uid, gid, permissions);
		this.type = type;
	}

	@Override
	public void stat(Stat buf) throws PosixException {
		super.stat(buf);
		int mode = 0;
		switch(type) {
		case CHAR_DEVICE:
			mode = Stat.S_IFCHR;
			break;
		case BLOCK_DEVICE:
			mode = Stat.S_IFBLK;
			break;
		case FIFO:
			mode = Stat.S_IFIFO;
			break;
		case SOCKET:
			mode = Stat.S_IFSOCK;
			break;
		}
		buf.st_mode = (buf.st_mode & ~Stat.S_IFREG) | mode;
	}
}
