package org.graalvm.vm.x86.posix;

public class Ioctl {
    public static final int _IOC_NRBITS = 8;
    public static final int _IOC_TYPEBITS = 8;
    public static final int _IOC_SIZEBITS = 14;
    public static final int _IOC_DIRBITS = 2;

    public static final int _IOC_NRMASK = ((1 << _IOC_NRBITS) - 1);
    public static final int _IOC_TYPEMASK = ((1 << _IOC_TYPEBITS) - 1);
    public static final int _IOC_SIZEMASK = ((1 << _IOC_SIZEBITS) - 1);
    public static final int _IOC_DIRMASK = ((1 << _IOC_DIRBITS) - 1);

    public static final int _IOC_NRSHIFT = 0;
    public static final int _IOC_TYPESHIFT = (_IOC_NRSHIFT + _IOC_NRBITS);
    public static final int _IOC_SIZESHIFT = (_IOC_TYPESHIFT + _IOC_TYPEBITS);
    public static final int _IOC_DIRSHIFT = (_IOC_SIZESHIFT + _IOC_SIZEBITS);

    public static final int _IOC_NONE = 0;
    public static final int _IOC_WRITE = 1;
    public static final int _IOC_READ = 2;

    public static int _IOC(int dir, int type, int nr, int size) {
        // @formatter:off
        return
            (((dir)  << _IOC_DIRSHIFT) |
             ((type) << _IOC_TYPESHIFT) |
             ((nr)   << _IOC_NRSHIFT) |
             ((size) << _IOC_SIZESHIFT));
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

    public static int _IOWR(int type, int nr, int size) {
        return _IOC(_IOC_READ | _IOC_WRITE, type, nr, size);
    }

    /* used to decode ioctl numbers.. */
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

    /* ...and for the drivers/sound files... */
    public static int IOC_IN = (_IOC_WRITE << _IOC_DIRSHIFT);
    public static int IOC_OUT = (_IOC_READ << _IOC_DIRSHIFT);
    public static int IOC_INOUT = ((_IOC_WRITE | _IOC_READ) << _IOC_DIRSHIFT);
    public static int IOCSIZE_MASK = (_IOC_SIZEMASK << _IOC_SIZESHIFT);
    public static int IOCSIZE_SHIFT = (_IOC_SIZESHIFT);
}
