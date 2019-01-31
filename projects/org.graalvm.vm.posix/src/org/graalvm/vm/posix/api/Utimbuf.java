package org.graalvm.vm.posix.api;

public class Utimbuf implements Struct {
	public long actime;
	public long modtime;

	public PosixPointer write32(PosixPointer p) {
		PosixPointer ptr = p;
		ptr.setI32((int) actime);
		ptr = ptr.add(4);
		ptr.setI32((int) modtime);
		return ptr.add(4);
	}

	public PosixPointer write64(PosixPointer p) {
		PosixPointer ptr = p;
		ptr.setI64(actime);
		ptr = ptr.add(8);
		ptr.setI64(modtime);
		return ptr.add(8);
	}

	public PosixPointer read32(PosixPointer p) {
		PosixPointer ptr = p;
		actime = ptr.getI32();
		ptr = ptr.add(4);
		modtime = ptr.getI32();
		return ptr.add(4);
	}

	public PosixPointer read64(PosixPointer p) {
		PosixPointer ptr = p;
		actime = ptr.getI64();
		ptr = ptr.add(8);
		modtime = ptr.getI64();
		return ptr.add(8);
	}
}
