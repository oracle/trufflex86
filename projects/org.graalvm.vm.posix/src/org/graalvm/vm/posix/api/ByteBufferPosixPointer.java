package org.graalvm.vm.posix.api;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteBufferPosixPointer implements PosixPointer {
	private final ByteBuffer buffer;
	private final long offset;
	private final long size;
	private final long realSize;
	private String name;

	public ByteBufferPosixPointer(ByteBuffer buffer, long offset, long size) {
		this(buffer, offset, size, size, "[posix-pointer]");
	}

	public ByteBufferPosixPointer(ByteBuffer buffer, long offset, long size, long realSize, String name) {
		assert size > 0;
		assert realSize > 0;
		this.buffer = buffer;
		this.offset = offset;
		this.size = size;
		this.realSize = realSize;
		buffer.order(ByteOrder.BIG_ENDIAN);
		assert offset == (int) offset;
		this.name = name;
	}

	@Override
	public PosixPointer add(int off) {
		// realSize always holds the total buffer size regardless of the offset
		return new ByteBufferPosixPointer(buffer, offset + off, size - off, realSize, name);
	}

	@Override
	public byte getI8() {
		if(offset >= realSize) {
			return 0;
		} else {
			return buffer.get((int) offset);
		}
	}

	@Override
	public short getI16() {
		if(offset >= realSize) {
			return 0;
		} else {
			return buffer.getShort((int) offset);
		}
	}

	@Override
	public int getI32() {
		if(offset >= realSize) {
			return 0;
		} else {
			return buffer.getInt((int) offset);
		}
	}

	@Override
	public long getI64() {
		if(offset >= realSize) {
			return 0;
		} else {
			return buffer.getLong((int) offset);
		}
	}

	@Override
	public void setI8(byte val) {
		if(offset < realSize) {
			buffer.put((int) offset, val);
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	@Override
	public void setI16(short val) {
		if(offset < realSize) {
			buffer.putShort((int) offset, val);
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	@Override
	public void setI32(int val) {
		if(offset < realSize) {
			buffer.putInt((int) offset, val);
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	@Override
	public void setI64(long val) {
		if(offset < realSize) {
			buffer.putLong((int) offset, val);
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	@Override
	public long size() {
		return size;
	}

	@Override
	public String getName() {
		return name;
	}
}
