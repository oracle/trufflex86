package org.graalvm.vm.posix.api;

public class Rlimit implements Struct {
	public long rlim_cur;
	public long rlim_max;

	@Override
	public PosixPointer write32(PosixPointer ptr) {
		PosixPointer p = ptr;
		p.setI32((int) rlim_cur);
		p = p.add(4);
		p.setI32((int) rlim_max);
		return p.add(4);
	}

	@Override
	public PosixPointer write64(PosixPointer ptr) {
		PosixPointer p = ptr;
		p.setI64(rlim_cur);
		p = p.add(8);
		p.setI64(rlim_max);
		return p.add(8);
	}
}
