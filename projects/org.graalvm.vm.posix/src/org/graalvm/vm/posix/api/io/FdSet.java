package org.graalvm.vm.posix.api.io;

import org.graalvm.vm.posix.api.PosixPointer;
import org.graalvm.vm.posix.api.Struct;

public class FdSet implements Struct {
	public static final int FD_SETSIZE = 1024;

	public final long[] fds_bits = new long[FD_SETSIZE / (8 * 8)];

	public PosixPointer read(PosixPointer ptr) {
		return null;
	}
}
