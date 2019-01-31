package org.graalvm.vm.posix.vfs;

import org.graalvm.vm.posix.api.PosixPointer;
import org.graalvm.vm.util.io.Endianess;

public class TmpfsFileMemory implements PosixPointer {
	private TmpfsFile file;
	private long offset;

	public TmpfsFileMemory(TmpfsFile file, long offset) {
		this.file = file;
		this.offset = offset;
	}

	public PosixPointer add(int off) {
		return new TmpfsFileMemory(file, offset + off);
	}

	public byte getI8() {
		return file.getContent()[(int) offset];
	}

	public short getI16() {
		return Endianess.get16bitBE(file.getContent(), (int) offset);
	}

	public int getI32() {
		return Endianess.get32bitBE(file.getContent(), (int) offset);
	}

	public long getI64() {
		return Endianess.get64bitBE(file.getContent(), (int) offset);
	}

	public void setI8(byte val) {
		file.getContent()[(int) offset] = val;
	}

	public void setI16(short val) {
		Endianess.set16bitBE(file.getContent(), (int) offset, val);
	}

	public void setI32(int val) {
		Endianess.set32bitBE(file.getContent(), (int) offset, val);
	}

	public void setI64(long val) {
		Endianess.set64bitBE(file.getContent(), (int) offset, val);
	}
}
