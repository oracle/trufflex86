package org.graalvm.vm.posix.api.io;

public class Ioctl {
	public static final int _IOC_NONE = 0;
	public static final int _IOC_READ = 2;
	public static final int _IOC_WRITE = 4;

	public static final int _IOC_NRBITS = 8;
	public static final int _IOC_TYPEBITS = 8;
	public static final int _IOC_SIZEBITS = 13;
	public static final int _IOC_DIRBITS = 3;

	public static final int _IOC_NRMASK = (1 << _IOC_NRBITS) - 1;
	public static final int _IOC_TYPEMASK = (1 << _IOC_TYPEBITS) - 1;
	public static final int _IOC_SIZEMASK = (1 << _IOC_SIZEBITS) - 1;
	public static final int _IOC_DIRMASK = (1 << _IOC_DIRBITS) - 1;

	public static final int _IOC_NRSHIFT = 0;
	public static final int _IOC_TYPESHIFT = _IOC_NRSHIFT + _IOC_NRBITS;
	public static final int _IOC_SIZESHIFT = _IOC_TYPESHIFT + _IOC_TYPEBITS;
	public static final int _IOC_DIRSHIFT = _IOC_SIZESHIFT + _IOC_SIZEBITS;

	public static int _IOC(int dir, int type, int nr, int size) {
		// @formatter:off
		return (dir << _IOC_DIRSHIFT)
				| (type << _IOC_TYPESHIFT)
				| (nr << _IOC_NRSHIFT)
				| (size << _IOC_SIZESHIFT);
		// @formatter:on
	}

	public static int _IO(int type, int nr) {
		return _IOC(_IOC_NONE, type, nr, 0);
	}

	public static int _IOR(int type, int nr, int size) {
		return _IOC(_IOC_READ, type, nr, size);
	}

	public static int _IOW(int type, int nr, int size) {
		return _IOC(_IOC_WRITE, type, nr, size);
	}

	public static int _IORW(int type, int nr, int size) {
		return _IOC(_IOC_READ | _IOC_WRITE, type, nr, size);
	}

	public static int _IOC_DIR(int nr) {
		return (nr >> _IOC_DIRSHIFT) & _IOC_DIRMASK;
	}

	public static int _IOC_TYPE(int nr) {
		return (nr >> _IOC_TYPESHIFT) & _IOC_TYPEMASK;
	}

	public static int _IOC_NR(int nr) {
		return (nr >> _IOC_NRSHIFT) & _IOC_NRMASK;
	}

	public static int _IOC_SIZE(int nr) {
		return (nr >> _IOC_SIZESHIFT) & _IOC_SIZEMASK;
	}

	public static String toString(int n) {
		int dir = _IOC_DIR(n);
		int type = _IOC_TYPE(n);
		int nr = _IOC_NR(n);
		int size = _IOC_SIZE(n);
		String rw;
		switch(dir) {
		case _IOC_NONE:
			rw = "_IOC_NONE";
			break;
		case _IOC_READ:
			rw = "_IOC_READ";
			break;
		case _IOC_WRITE:
			rw = "_IOC_WRITE";
			break;
		case _IOC_READ | _IOC_WRITE:
			rw = "_IOC_READ|_IOC_WRITE";
			break;
		default:
			// unreachable
			rw = Integer.toString(dir);
		}
		return String.format("_IOC(%s, %d, %d, %d)", rw, type, nr, size);
	}
}
