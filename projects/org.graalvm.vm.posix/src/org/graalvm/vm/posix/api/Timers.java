package org.graalvm.vm.posix.api;

public class Timers {
    private static final int TIMERS = 1;

    private Timer[] timers = new Timer[TIMERS];

    private Object lock = new Object();

    public int create(Timer timer) throws PosixException {
        synchronized (lock) {
            for (int i = 0; i < timers.length; i++) {
                if (timers[i] == null) {
                    timers[i] = timer;
                    return i;
                }
            }
        }
        throw new PosixException(Errno.EAGAIN);
    }

    public Timer get(int id) throws PosixException {
        if (id < 0 || id > timers.length) {
            throw new PosixException(Errno.EINVAL);
        }
        Timer t;
        synchronized (lock) {
            t = timers[id];
        }
        if (t == null) {
            throw new PosixException(Errno.EINVAL);
        } else {
            return t;
        }
    }

    public void delete(int id) throws PosixException {
        if (id < 0 || id > timers.length) {
            throw new PosixException(Errno.EINVAL);
        }
        synchronized (lock) {
            if (timers[id] == null) {
                throw new PosixException(Errno.EINVAL);
            } else {
                timers[id].destroy();
                timers[id] = null;
            }
        }
    }
}
