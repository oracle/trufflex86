package org.graalvm.vm.posix.api.io;

import org.graalvm.vm.posix.api.PosixPointer;
import org.graalvm.vm.posix.api.Struct;

public class Pollfd implements Struct {
	public int fd;
	public short events;
	public short revents;

	@Override
	public PosixPointer write(PosixPointer p) {
		PosixPointer ptr = p;
		ptr.setI32(fd);
		ptr = ptr.add(4);
		ptr.setI16(events);
		ptr = ptr.add(2);
		ptr.setI16(revents);
		return ptr.add(2);
	}

	@Override
	public PosixPointer read(PosixPointer p) {
		PosixPointer ptr = p;
		fd = ptr.getI32();
		ptr = ptr.add(4);
		events = ptr.getI16();
		ptr = ptr.add(2);
		revents = ptr.getI16();
		return ptr.add(2);
	}
}
