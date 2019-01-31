package org.graalvm.vm.posix.api;

public class Sigaction implements Struct {
	public long sa_handler;
	public long sa_flags;
	public long sa_restorer;
	public final Sigset sa_mask = new Sigset();

	@Override
	public PosixPointer write32(PosixPointer ptr) {
		PosixPointer p = ptr;
		p.setI32((int) sa_handler);
		p = p.add(4);
		p.setI32((int) sa_flags);
		p = p.add(4);
		p.setI32((int) sa_restorer);
		p = p.add(4);
		return sa_mask.write32(p);
	}

	@Override
	public PosixPointer write64(PosixPointer ptr) {
		PosixPointer p = ptr;
		p.setI64(sa_handler);
		p = p.add(8);
		p.setI64(sa_flags);
		p = p.add(8);
		p.setI64(sa_restorer);
		p = p.add(8);
		return sa_mask.write64(p);
	}

	@Override
	public PosixPointer read32(PosixPointer ptr) {
		PosixPointer p = ptr;
		sa_handler = Integer.toUnsignedLong(p.getI32());
		p = p.add(4);
		sa_flags = Integer.toUnsignedLong(p.getI32());
		p = p.add(4);
		sa_restorer = Integer.toUnsignedLong(p.getI32());
		p = p.add(4);
		return sa_mask.read32(p);
	}

	@Override
	public PosixPointer read64(PosixPointer ptr) {
		PosixPointer p = ptr;
		sa_handler = p.getI64();
		p = p.add(8);
		sa_flags = p.getI64();
		p = p.add(8);
		sa_restorer = p.getI64();
		p = p.add(8);
		return sa_mask.read64(p);
	}

	public void copyFrom(Sigaction other) {
		sa_handler = other.sa_handler;
		sa_flags = other.sa_flags;
		sa_restorer = other.sa_restorer;
		sa_mask.setmask(other.sa_mask);
	}
}
