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
    public static final int FIONCLEX = 0x5450;
    public static final int FIOCLEX = 0x5451;

    static {
        ioctls.put(TCGETS, org.graalvm.vm.posix.api.io.Ioctls.TCGETS);
        ioctls.put(TCSETS, org.graalvm.vm.posix.api.io.Ioctls.TCSETS);
        ioctls.put(TCSETSW, org.graalvm.vm.posix.api.io.Ioctls.TCSETSF);
        ioctls.put(TCSETSW, org.graalvm.vm.posix.api.io.Ioctls.TCSETSW);
        ioctls.put(TIOCGWINSZ, org.graalvm.vm.posix.api.io.Ioctls.TIOCGWINSZ);
        ioctls.put(TIOCOUTQ, org.graalvm.vm.posix.api.io.Ioctls.TIOCOUTQ);
        ioctls.put(TIOCSWINSZ, org.graalvm.vm.posix.api.io.Ioctls.TIOCSWINSZ);
        ioctls.put(FIONCLEX, org.graalvm.vm.posix.api.io.Ioctls.FIONCLEX);
        ioctls.put(FIOCLEX, org.graalvm.vm.posix.api.io.Ioctls.FIOCLEX);
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
        return org.graalvm.vm.posix.api.io.Ioctl._IOC(dir, type, nr, size);
    }
}
