package org.graalvm.vm.posix.vfs;

public class Tmpfs extends FileSystem {
	private TmpfsDirectory root;

	public Tmpfs(VFS vfs) {
		super("tmpfs");
		root = new TmpfsDirectory(vfs, "/", 0, 0, 755);
	}

	@Override
	public TmpfsDirectory getRoot() {
		return root;
	}
}
