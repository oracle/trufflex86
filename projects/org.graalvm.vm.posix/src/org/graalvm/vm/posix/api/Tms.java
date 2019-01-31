package org.graalvm.vm.posix.api;

public class Tms implements Struct {
	public long tms_utime;
	public long tms_stime;
	public long tms_cutime;
	public long tms_cstime;

	@Override
	public PosixPointer write32(PosixPointer p) {
		PosixPointer ptr = p;
		ptr.setI32((int) tms_utime);
		ptr = ptr.add(4);
		ptr.setI32((int) tms_stime);
		ptr = ptr.add(4);
		ptr.setI32((int) tms_cutime);
		ptr = ptr.add(4);
		ptr.setI32((int) tms_cstime);
		return ptr.add(4);
	}

	@Override
	public PosixPointer write64(PosixPointer p) {
		PosixPointer ptr = p;
		ptr.setI64(tms_utime);
		ptr = ptr.add(8);
		ptr.setI64(tms_stime);
		ptr = ptr.add(8);
		ptr.setI64(tms_cutime);
		ptr = ptr.add(8);
		ptr.setI64(tms_cstime);
		return ptr.add(8);
	}
}
