package org.graalvm.vm.posix.vfs;

import java.nio.file.Paths;

public class NativeFileSystem extends FileSystem {
	private final String path;
	private final VFS vfs;

	public NativeFileSystem(VFS vfs, String path) {
		super("native");
		this.vfs = vfs;
		this.path = path;
	}

	@Override
	public VFSDirectory getRoot() {
		return new NativeDirectory(vfs, Paths.get(path));
	}
}
