package org.graalvm.vm.posix.api;

public class Dirent implements Struct {
	public static final byte DT_UNKNOWN = 0;
	public static final byte DT_FIFO = 1;
	public static final byte DT_CHR = 2;
	public static final byte DT_DIR = 4;
	public static final byte DT_BLK = 6;
	public static final byte DT_REG = 8;
	public static final byte DT_LNK = 10;
	public static final byte DT_SOCK = 12;
	public static final byte DT_WHT = 14;

	public static final int DIRENT_32 = 0;
	public static final int DIRENT_64 = 1;
	public static final int DIRENT64 = 2;

	public long d_ino;
	public long d_off;
	public short d_reclen;
	public byte d_type;
	public String d_name;

	public static byte IFTODT(int mode) {
		return (byte) ((mode & 0170000) >>> 12);
	}

	public static int DTTOIF(int dirtype) {
		return dirtype << 12;
	}

	public int size32() {
		d_reclen = (short) (12 + d_name.length());
		return Short.toUnsignedInt(d_reclen);
	}

	public int size64() {
		d_reclen = (short) (20 + d_name.length());
		return Short.toUnsignedInt(d_reclen);
	}

	@Override
	public PosixPointer write32(PosixPointer p) {
		int len = size32();
		PosixPointer ptr = p;
		ptr.setI32((int) d_ino);
		ptr = ptr.add(4);
		ptr.setI32((int) d_off);
		ptr = ptr.add(4);
		ptr.setI16((short) len);
		ptr = ptr.add(2);
		ptr = CString.strcpy(ptr, d_name);
		ptr.setI8(d_type);
		return ptr.add(1);
	}

	@Override
	public PosixPointer write64(PosixPointer p) {
		int len = size64();
		PosixPointer ptr = p;
		ptr.setI64(d_ino);
		ptr = ptr.add(8);
		ptr.setI64(d_off);
		ptr = ptr.add(8);
		ptr.setI16((short) len);
		ptr = ptr.add(2);
		ptr = CString.strcpy(ptr, d_name);
		ptr.setI8(d_type);
		return ptr.add(1);
	}

	public PosixPointer writeDirent64(PosixPointer p) {
		int len = size64();
		PosixPointer ptr = p;
		ptr.setI64(d_ino);
		ptr = ptr.add(8);
		ptr.setI64(d_off);
		ptr = ptr.add(8);
		ptr.setI16((short) len);
		ptr = ptr.add(2);
		ptr.setI8(d_type);
		ptr = ptr.add(1);
		ptr = CString.strcpy(ptr, d_name);
		return ptr;
	}
}
