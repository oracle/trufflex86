package org.graalvm.vm.posix.api;

public interface Struct {
	default PosixPointer write32(PosixPointer ptr) {
		return write(ptr);
	}

	default PosixPointer write64(PosixPointer ptr) {
		return write(ptr);
	}

	default PosixPointer read32(PosixPointer ptr) {
		return read(ptr);
	}

	default PosixPointer read64(PosixPointer ptr) {
		return read(ptr);
	}

	default PosixPointer read(@SuppressWarnings("unused") PosixPointer ptr) {
		throw new AssertionError("not implemented");
	}

	default PosixPointer write(@SuppressWarnings("unused") PosixPointer ptr) {
		throw new AssertionError("not implemented");
	}
}
