package org.graalvm.vm.posix.api;

import java.util.Date;

public class Timeval implements Struct {
	public long tv_sec;
	public long tv_usec;

	public Timeval() {
	}

	public Timeval(long tv_sec, long tv_usec) {
		this.tv_sec = tv_sec;
		this.tv_usec = tv_usec;
	}

	public Timeval(Date date) {
		long msec = date.getTime();
		tv_sec = msec / 1000;
		tv_usec = (msec % 1000) * 1000;
	}

	@Override
	public PosixPointer write32(PosixPointer ptr) {
		PosixPointer p = ptr;
		p.setI32((int) tv_sec);
		p = p.add(4);
		p.setI32((int) tv_usec);
		return p.add(4);
	}

	@Override
	public PosixPointer write64(PosixPointer ptr) {
		PosixPointer p = ptr;
		p.setI64(tv_sec);
		p = p.add(8);
		p.setI64(tv_usec);
		return p.add(8);
	}

	@Override
	public String toString() {
		return String.format("timeval[tv_sec=%d,tv_usec=%d]", tv_sec, tv_usec);
	}
}
