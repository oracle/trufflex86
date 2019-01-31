package org.graalvm.vm.posix.vfs;

import java.util.Iterator;

import org.graalvm.vm.posix.api.Dirent;
import org.graalvm.vm.posix.api.PosixException;

public class GenericDirectoryStream extends VFSDirectoryStream {
	public GenericDirectoryStream(VFSDirectory dir, Iterator<Dirent> iterator) {
		super(dir, iterator);
	}

	@Override
	public int close() throws PosixException {
		return 0;
	}
}
