package org.graalvm.vm.x86.posix;

import java.util.HashMap;
import java.util.Map;

public class Ioctls {
    private static final Map<Integer, Integer> ioctls = new HashMap<>();

    public static final int TCGETS = 0x5401;
    public static final int TCSETS = 0x5402;
    public static final int TCSETSW = 0x5403;
    public static final int TCSETSF = 0x5404;
    public static final int TIOCGWINSZ = 0x5413;
    public static final int TIOCOUTQ = 0x5411;
    public static final int TIOCSWINSZ = 0x5414;

    static {
        ioctls.put(TCGETS, com.everyware.posix.api.io.Ioctls.TCGETS);
        ioctls.put(TCSETS, com.everyware.posix.api.io.Ioctls.TCSETS);
        ioctls.put(TCSETSW, com.everyware.posix.api.io.Ioctls.TCSETSF);
        ioctls.put(TCSETSW, com.everyware.posix.api.io.Ioctls.TCSETSW);
        ioctls.put(TIOCGWINSZ, com.everyware.posix.api.io.Ioctls.TIOCGWINSZ);
        ioctls.put(TIOCOUTQ, com.everyware.posix.api.io.Ioctls.TIOCOUTQ);
        ioctls.put(TIOCSWINSZ, com.everyware.posix.api.io.Ioctls.TIOCSWINSZ);
    }

    public static int translate(int request) {
        Integer ioctl = ioctls.get(request);
        if (ioctl != null) {
            return ioctl;
        }

        int dir = Ioctl._IOC_DIR(request);
        int type = Ioctl._IOC_TYPE(request);
        int nr = Ioctl._IOC_NR(request);
        int size = Ioctl._IOC_SIZE(request);
        return com.everyware.posix.api.io.Ioctl._IOC(dir, type, nr, size);
    }
}
