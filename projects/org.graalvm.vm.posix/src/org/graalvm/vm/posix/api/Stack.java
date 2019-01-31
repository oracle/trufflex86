package org.graalvm.vm.posix.api;

public class Stack implements Struct {
	public static final int SS_ONSTACK = 1;
	public static final int SS_DISABLE = 2;

	public static final int MINSIGSTKSZ = 4096;
	public static final int SIGSTKSZ = 8192;

	public long ss_sp;
	public int ss_flags;
	public long ss_size;

	@Override
	public PosixPointer read32(PosixPointer ptr) {
		PosixPointer p = ptr;
		ss_sp = p.getI32();
		p = p.add(4);
		ss_flags = p.getI32();
		p = p.add(4);
		ss_size = p.getI32();
		return p.add(4);
	}

	@Override
	public PosixPointer read64(PosixPointer ptr) {
		PosixPointer p = ptr;
		ss_sp = p.getI64();
		p = p.add(8);
		ss_flags = p.getI32();
		p = p.add(8);
		ss_size = p.getI64();
		return p.add(8);
	}

	@Override
	public PosixPointer write32(PosixPointer ptr) {
		PosixPointer p = ptr;
		p.setI32((int) ss_sp);
		p = p.add(4);
		p.setI32(ss_flags);
		p = p.add(4);
		p.setI32((int) ss_size);
		return p.add(4);
	}

	@Override
	public PosixPointer write64(PosixPointer ptr) {
		PosixPointer p = ptr;
		p.setI64(ss_sp);
		p = p.add(8);
		p.setI32(ss_flags);
		p = p.add(8);
		p.setI64(ss_size);
		return p.add(8);
	}

	@Override
	public String toString() {
		return String.format("{ss_sp=0x%x, ss_flags=%d, ss_size=%d}", ss_sp, ss_flags, ss_size);
	}
}
