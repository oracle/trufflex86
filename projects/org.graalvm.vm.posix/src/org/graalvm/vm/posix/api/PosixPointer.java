package org.graalvm.vm.posix.api;

public interface PosixPointer {
	PosixPointer add(int off);

	byte getI8() throws MemoryFaultException;

	short getI16() throws MemoryFaultException;

	int getI32() throws MemoryFaultException;

	long getI64() throws MemoryFaultException;

	void setI8(byte val) throws MemoryFaultException;

	void setI16(short val) throws MemoryFaultException;

	void setI32(int val) throws MemoryFaultException;

	void setI64(long val) throws MemoryFaultException;

	default long getAddress() {
		throw new AssertionError("not implemented");
	}

	default long size() {
		throw new AssertionError("not implemented");
	}

	default boolean hasMemory(@SuppressWarnings("unused") int size) {
		return false;
	}

	default byte[] getMemory() {
		throw new AssertionError("not implemented");
	}

	default int getOffset() {
		throw new AssertionError("not implemented");
	}

	default String getName() {
		return "[posix-pointer]";
	}
}
