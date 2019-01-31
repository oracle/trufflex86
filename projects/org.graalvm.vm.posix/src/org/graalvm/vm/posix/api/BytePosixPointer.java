package org.graalvm.vm.posix.api;

import org.graalvm.vm.util.io.Endianess;

public class BytePosixPointer implements PosixPointer {
	public final byte[] memory;
	public final int offset;
	private final long address;

	public BytePosixPointer(byte[] memory) {
		this(memory, 0, -1);
	}

	public BytePosixPointer(byte[] memory, int offset) {
		this(memory, offset, -1);
	}

	public BytePosixPointer(byte[] memory, int offset, long address) {
		this.memory = memory;
		this.offset = offset;
		this.address = address;
	}

	@Override
	public PosixPointer add(int off) {
		return new BytePosixPointer(memory, offset + off);
	}

	@Override
	public byte getI8() {
		try {
			return memory[offset];
		} catch(IndexOutOfBoundsException e) {
			throw new MemoryFaultException(e);
		}
	}

	@Override
	public void setI8(byte val) {
		try {
			memory[offset] = val;
		} catch(IndexOutOfBoundsException e) {
			throw new MemoryFaultException(e);
		}
	}

	@Override
	public short getI16() {
		try {
			return Endianess.get16bitBE(memory, offset);
		} catch(IndexOutOfBoundsException e) {
			throw new MemoryFaultException(e);
		}
	}

	@Override
	public void setI16(short val) {
		try {
			Endianess.set16bitBE(memory, offset, val);
		} catch(IndexOutOfBoundsException e) {
			throw new MemoryFaultException(e);
		}
	}

	@Override
	public int getI32() {
		try {
			return Endianess.get32bitBE(memory, offset);
		} catch(IndexOutOfBoundsException e) {
			throw new MemoryFaultException(e);
		}
	}

	@Override
	public void setI32(int val) {
		try {
			Endianess.set32bitBE(memory, offset, val);
		} catch(IndexOutOfBoundsException e) {
			throw new MemoryFaultException(e);
		}
	}

	@Override
	public long getI64() {
		try {
			return Endianess.get64bitBE(memory, offset);
		} catch(IndexOutOfBoundsException e) {
			throw new MemoryFaultException(e);
		}
	}

	@Override
	public void setI64(long val) {
		try {
			Endianess.set64bitBE(memory, offset, val);
		} catch(IndexOutOfBoundsException e) {
			throw new MemoryFaultException(e);
		}
	}

	@Override
	public boolean hasMemory(int size) {
		return size() >= size;
	}

	@Override
	public byte[] getMemory() {
		return memory;
	}

	@Override
	public int getOffset() {
		return offset;
	}

	@Override
	public long size() {
		return memory.length - offset;
	}

	@Override
	public int hashCode() {
		return (int) (memory.length + offset + address);
	}

	@Override
	public boolean equals(Object o) {
		if(!(o instanceof BytePosixPointer)) {
			return false;
		}
		BytePosixPointer p = (BytePosixPointer) o;
		assert (memory != p.memory || offset != p.offset || address == p.address);
		return memory == p.memory && offset == p.offset;
	}

	@Override
	public String toString() {
		if(address != -1) {
			return String.format("PTR[0x%x]", address);
		} else {
			return String.format("PTR[%s@%d]", System.identityHashCode(memory), offset);
		}
	}
}
